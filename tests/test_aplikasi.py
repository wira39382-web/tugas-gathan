"""Pengujian alur konsol sebenarnya, menggunakan pustaka standar Python."""
from pathlib import Path
import subprocess
import unittest

ROOT = Path(__file__).resolve().parents[1]


def jalankan(*baris):
    hasil = subprocess.run(
        ["java", "-cp", "out", "Main"], cwd=ROOT,
        input="\n".join(map(str, baris)) + "\n", text=True,
        capture_output=True, timeout=10,
    )
    if hasil.returncode != 0 or hasil.stderr:
        raise AssertionError(f"Aplikasi gagal: {hasil.returncode}\n{hasil.stderr}")
    return hasil.stdout


class AplikasiTest(unittest.TestCase):
    def test_menu_awal_dikelompokkan(self):
        out = jalankan(2, 1, 0, 0)
        self.assertLess(out.index("-- MAKANAN --"), out.index("-- MINUMAN --"))
        for nama in ["Nasi Goreng", "Mie Goreng", "Ayam Bakar", "Soto Ayam",
                     "Es Teh", "Es Jeruk", "Jus Alpukat", "Air Mineral"]:
            self.assertIn(nama, out)

    def test_tanpa_promo(self):
        out = jalankan(1, 1, 1, "selesai", 0)
        self.assertIn("Nasi Goreng | 1 x Rp30.000 = Rp30.000", out)
        self.assertIn("TOTAL BAYAR           : Rp53.000", out)
        self.assertIn("Diskon 10%            : tidak berlaku", out)

    def test_batas_promo(self):
        # Ubah harga menu pertama agar subtotal tepat di sekitar tiap batas.
        for subtotal, total, diskon, gratis in [
            (49_999, "Rp74.999", False, False),
            (50_000, "Rp75.000", False, False),
            (50_001, "Rp65.001", False, True),
            (99_999, "Rp119.999", False, True),
            (100_000, "Rp120.000", False, True),
            (100_001, "Rp110.001", True, True),
        ]:
            with self.subTest(subtotal=subtotal):
                out = jalankan(2, 3, 1, subtotal - 20_000, "Ya", 0,
                               1, 1, 1, 5, 2, "selesai", 0)
                self.assertIn("TOTAL BAYAR           : " + total, out)
                self.assertEqual("Diskon 10% (>100.000)" in out, diskon)
                self.assertEqual("Promo Es Teh: 1 gelas gratis" in out, gratis)

    def test_kedua_promo_sekaligus(self):
        out = jalankan(1, 1, 3, 5, 2, "selesai", 0)
        self.assertIn("Subtotal pesanan      : Rp110.000", out)
        self.assertIn("Diskon 10% (>100.000)  : -Rp11.000", out)
        self.assertIn("Promo Es Teh: 1 gelas gratis, potongan Rp10.000", out)
        self.assertIn("Setelah diskon/promo  : Rp89.000", out)
        self.assertIn("Pajak 10% x Rp110.000 : Rp11.000", out)
        self.assertIn("TOTAL BAYAR           : Rp120.000", out)

    def test_diskon_tanpa_minuman(self):
        out = jalankan(1, 1, 4, "selesai", 0)
        self.assertIn("TOTAL BAYAR           : Rp140.000", out)
        self.assertIn("tidak ada pasangan Es Teh", out)

    def test_minuman_lain_tidak_mendapat_bogo(self):
        out = jalankan(1, 6, 4, "selesai", 0)
        self.assertIn("TOTAL BAYAR           : Rp86.000", out)
        self.assertNotIn("gelas gratis", out)

    def test_jumlah_promo_ganjil(self):
        out = jalankan(1, 3, 1, 5, 3, "selesai", 0)
        self.assertIn("Promo Es Teh: 1 gelas gratis", out)
        self.assertIn("TOTAL BAYAR           : Rp87.000", out)

    def test_satu_minuman_tidak_gratis(self):
        out = jalankan(1, 3, 2, 5, 1, "selesai", 0)
        self.assertIn("TOTAL BAYAR           : Rp119.000", out)
        self.assertNotIn("gelas gratis", out)

    def test_pembulatan_setengah_ke_atas(self):
        out = jalankan(2, 3, 1, 10_005, "ya", 0, 1, 1, 1, "selesai", 0)
        self.assertIn("Pajak 10% x Rp10.005 : Rp1.001", out)
        self.assertIn("TOTAL BAYAR           : Rp31.006", out)

    def test_pesanan_kosong(self):
        out = jalankan(1, "selesai", 0)
        self.assertIn("Tidak ada biaya yang dikenakan", out)
        self.assertNotIn("=== STRUK", out)

    def test_validasi_pilihan_menu_dan_jumlah(self):
        out = jalankan("abc", 8, 1, "pizza", 99, "nAsI gOrEnG", "abc", 0,
                       -1, "1.5", "9999999999999999999999", 2, "selesai", 0)
        self.assertEqual(out.count("Menu tidak ditemukan"), 2)
        self.assertGreaterEqual(out.count("Input tidak valid"), 7)
        self.assertIn("Nasi Goreng | 2 x Rp30.000 = Rp60.000", out)
        self.assertIn("TOTAL BAYAR           : Rp86.000", out)

    def test_menu_berulang_digabung(self):
        out = jalankan(1, 3, 1, 5, 1, "es teh", 1, "selesai", 0)
        self.assertIn("Es Teh | 2 x Rp10.000 = Rp20.000", out)
        self.assertIn("TOTAL BAYAR           : Rp76.000", out)

    def test_array_pesanan_bertambah(self):
        baris = [1]
        for nomor in range(1, 9):
            baris += [nomor, 1]
        out = jalankan(*baris, "selesai", 0)
        self.assertEqual(out.count(" | 1 x "), 8)
        self.assertIn("Subtotal pesanan      : Rp165.000", out)
        self.assertIn("TOTAL BAYAR           : Rp185.000", out)

    def test_kembali_membatalkan_pesanan(self):
        out = jalankan(1, 1, 1, "kembali", 0)
        self.assertIn("Pemesanan dibatalkan", out)
        self.assertNotIn("=== STRUK", out)

    def test_hapus_pesanan_konfirmasi_dan_nomor(self):
        out = jalankan(1, "hapus", 1, 1, 5, 2,
                       "hapus", 9, 1, "Tidak", "hapus", 1, "Ya", "selesai", 0)
        self.assertIn("Belum ada pesanan untuk dihapus", out)
        self.assertIn("Input tidak valid", out)
        struk = out.split("=== STRUK RESTORAN GATHAN ===")[1]
        self.assertNotIn("Nasi Goreng |", struk)
        self.assertIn("Es Teh | 2 x Rp10.000", struk)
        self.assertIn("TOTAL BAYAR           : Rp42.000", struk)

    def test_hapus_seluruh_pesanan(self):
        out = jalankan(1, 1, 1, "hapus", 1, "Ya", "selesai", 0)
        self.assertIn("Pesanan kosong", out)
        self.assertNotIn("=== STRUK", out)

    def test_tambah_beberapa_menu_dan_validasi(self):
        out = jalankan(2, 2, "", "selesai", "123", "Nasi Goreng", "Bakso",
                       9, 1, 0, "abc", 18_000, "mungkin", "Ya",
                       "Kopi", 2, 12_000, "Tidak", 0,
                       1, "bakso", 2, "kopi", 1, "selesai", 0)
        self.assertEqual(out.count("Menu berhasil ditambahkan"), 2)
        self.assertEqual(out.count("Nama harus unik"), 4)
        self.assertIn("Bakso | 2 x Rp18.000 = Rp36.000", out)
        self.assertIn("Kopi | 1 x Rp12.000 = Rp12.000", out)
        self.assertIn("TOTAL BAYAR           : Rp72.800", out)

    def test_ubah_harga_ditolak(self):
        out = jalankan(2, 3, 9, 1, 99_000, "Tidak", 0, 1, 1, 1, "selesai", 0)
        self.assertIn("Perubahan harga dibatalkan", out)
        self.assertIn("Nasi Goreng | 1 x Rp30.000", out)

    def test_ubah_harga_diterima(self):
        out = jalankan(2, 3, 1, -1, 35_000, "mungkin", "yA", 0,
                       1, 1, 1, "selesai", 0)
        self.assertIn("Harga berhasil diubah", out)
        self.assertIn("Nasi Goreng | 1 x Rp35.000", out)
        self.assertIn("TOTAL BAYAR           : Rp58.500", out)

    def test_hapus_menu_ditolak(self):
        out = jalankan(2, 4, 1, "Tidak", 0, 1, 1, 1, "selesai", 0)
        self.assertIn("Penghapusan menu dibatalkan", out)
        self.assertIn("Nasi Goreng |", out)

    def test_hapus_menu_diterima(self):
        out = jalankan(2, 4, "abc", 99, 1, "Ya", 0,
                       1, "Nasi Goreng", 1, 1, "selesai", 0)
        self.assertIn("Menu berhasil dihapus", out)
        self.assertIn("Menu tidak ditemukan", out)
        self.assertIn("Mie Goreng | 1 x Rp25.000", out)

    def test_semua_menu_dihapus_lalu_ditambah(self):
        baris = [2]
        for _ in range(8):
            baris += [4, 1, "Ya"]
        out = jalankan(*baris, 3, 4, 0, 1, 2, 2, "Bakso", 1, 18_000,
                       "Tidak", 0, 1, 1, 1, "selesai", 0)
        self.assertIn("Tidak ada menu untuk diubah", out)
        self.assertIn("Tidak ada menu untuk dihapus", out)
        self.assertIn("Menu masih kosong", out)
        self.assertIn("Bakso | 1 x Rp18.000", out)

    def test_navigasi_kembali(self):
        out = jalankan(2, 3, 0, 4, 0, 2, 0, 0, 0)
        self.assertEqual(out.count("=== PENGELOLAAN MENU ==="), 4)
        self.assertIn("Terima kasih, sampai jumpa", out)

    def test_jumlah_melampaui_integer_tidak_merusak_pesanan(self):
        out = jalankan(1, 8, 2_147_483_647, 8, 1, "selesai", 0)
        self.assertIn("Penambahan dibatalkan; kurangi jumlah", out)
        self.assertIn("Air Mineral | 2147483647 x Rp5.000 = Rp10.737.418.235.000", out)

    def test_eof_saat_pemesanan(self):
        out = jalankan(1, 1)
        self.assertIn("Input berakhir", out)
        self.assertNotIn("=== STRUK", out)


if __name__ == "__main__":
    unittest.main(verbosity=2)
