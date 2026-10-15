import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

/** Aplikasi konsol Tugas Praktik 2, tanpa dependensi eksternal. */
public class Main {
    static final long BIAYA_PELAYANAN = 20_000;
    static final String MENU_PROMO = "Es Teh";
    private static final Scanner INPUT = new Scanner(System.in);
    private static final NumberFormat RUPIAH = NumberFormat.getIntegerInstance(
            Locale.forLanguageTag("id-ID"));
    private static Menu[] daftarMenu = new Menu[8];
    private static int jumlahMenu;

    static class Pesanan {
        final Menu menu;
        int jumlah;

        Pesanan(Menu menu, int jumlah) {
            this.menu = menu;
            this.jumlah = jumlah;
        }

        long total() { return (long) menu.getHarga() * jumlah; }
    }

    static class RincianBiaya {
        long subtotal;
        long pajak;
        long diskon;
        long potonganPromo;
        int jumlahGratis;
        long pelayanan;
        long total;
    }

    private static class AkhirInput extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static void main(String[] args) {
        isiMenuAwal();
        try {
            int pilihan;
            do {
                System.out.println("\n=== RESTORAN GATHAN ===");
                System.out.println("1. Pelanggan: pesan makanan/minuman");
                System.out.println("2. Pemilik: kelola menu");
                System.out.println("0. Keluar");
                pilihan = bacaAngka("Pilihan: ", 0, 2);
                switch (pilihan) {
                    case 1: terimaPesanan(); break;
                    case 2: kelolaMenu(); break;
                    case 0: System.out.println("Terima kasih, sampai jumpa!"); break;
                    default: break;
                }
            } while (pilihan != 0);
        } catch (AkhirInput selesai) {
            System.out.println("\nInput berakhir. Aplikasi ditutup tanpa memproses transaksi yang belum selesai.");
        }
    }

    static void isiMenuAwal() {
        daftarMenu = new Menu[] {
            new Menu("Nasi Goreng", 30_000, Menu.MAKANAN),
            new Menu("Mie Goreng", 25_000, Menu.MAKANAN),
            new Menu("Ayam Bakar", 40_000, Menu.MAKANAN),
            new Menu("Soto Ayam", 20_000, Menu.MAKANAN),
            new Menu("Es Teh", 10_000, Menu.MINUMAN),
            new Menu("Es Jeruk", 15_000, Menu.MINUMAN),
            new Menu("Jus Alpukat", 20_000, Menu.MINUMAN),
            new Menu("Air Mineral", 5_000, Menu.MINUMAN)
        };
        jumlahMenu = daftarMenu.length;
    }

    static String rupiah(long nilai) { return "Rp" + RUPIAH.format(nilai); }

    static void tampilkanMenu() {
        System.out.println("\n=== DAFTAR MENU ===");
        String[] kategori = {Menu.MAKANAN, Menu.MINUMAN};
        for (String kelompok : kategori) {
            System.out.println("-- " + kelompok.toUpperCase(Locale.ROOT) + " --");
            boolean ada = false;
            for (int i = 0; i < jumlahMenu; i++) {
                if (daftarMenu[i].getKategori().equals(kelompok)) {
                    System.out.printf("%d. %-22s %s%n", i + 1,
                            daftarMenu[i].getNama(), rupiah(daftarMenu[i].getHarga()));
                    ada = true;
                }
            }
            if (!ada) System.out.println("(Belum ada menu)");
        }
    }

    private static String bacaTeks(String pesan) {
        System.out.print(pesan);
        if (!INPUT.hasNextLine()) throw new AkhirInput();
        return INPUT.nextLine().trim();
    }

    private static int bacaAngka(String pesan, int minimum, int maksimum) {
        while (true) {
            String teks = bacaTeks(pesan);
            try {
                int nilai = Integer.parseInt(teks);
                if (nilai >= minimum && nilai <= maksimum) return nilai;
            } catch (NumberFormatException invalid) {
                // Teks, pecahan, dan angka yang terlalu besar diminta ulang.
            }
            System.out.printf("Input tidak valid. Masukkan bilangan bulat %d sampai %d.%n",
                    minimum, maksimum);
        }
    }

    private static boolean konfirmasi(String pesan) {
        while (true) {
            String jawaban = bacaTeks(pesan + " (Ya/Tidak): ");
            if (jawaban.equalsIgnoreCase("ya")) return true;
            if (jawaban.equalsIgnoreCase("tidak")) return false;
            System.out.println("Input tidak valid. Ketik Ya atau Tidak.");
        }
    }

    private static int cariMenu(String pilihan) {
        for (int i = 0; i < jumlahMenu; i++) {
            if (daftarMenu[i].getNama().equalsIgnoreCase(pilihan)) return i;
        }
        try {
            int nomor = Integer.parseInt(pilihan);
            if (nomor >= 1 && nomor <= jumlahMenu) return nomor - 1;
        } catch (NumberFormatException invalid) {
            // Bukan nomor maupun nama menu yang dikenal.
        }
        return -1;
    }

    static void terimaPesanan() {
        if (jumlahMenu == 0) {
            System.out.println("Menu masih kosong. Silakan tambahkan menu melalui pengelolaan.");
            return;
        }
        Pesanan[] pesanan = new Pesanan[4];
        int banyak = 0;
        tampilkanMenu();
        System.out.println("Promo: subtotal > Rp100.000 mendapat diskon 10%.");
        System.out.println("Subtotal > Rp50.000: setiap 2 Es Teh yang dipesan, 1 gratis.");
        System.out.println("Kedua promo dapat digabung; pajak 10% dari subtotal + pelayanan Rp20.000.");
        System.out.println("Masukkan nomor/nama menu, 'hapus', 'selesai', atau 'kembali' untuk batal.");
        while (true) {
            String pilihan = bacaTeks("Menu: ");
            if (pilihan.equalsIgnoreCase("kembali")) {
                System.out.println("Pemesanan dibatalkan.");
                return;
            } else if (pilihan.equalsIgnoreCase("selesai")) {
                cetakStruk(pesanan, banyak);
                return;
            } else if (pilihan.equalsIgnoreCase("hapus")) {
                banyak = hapusPesanan(pesanan, banyak);
                continue;
            }
            int indeks = cariMenu(pilihan);
            if (indeks < 0) {
                System.out.println("Menu tidak ditemukan. Pilih nomor/nama yang ada pada daftar.");
                continue;
            }
            int jumlah = bacaAngka("Jumlah: ", 1, Integer.MAX_VALUE);
            int posisi = -1;
            for (int i = 0; i < banyak; i++) {
                if (pesanan[i].menu == daftarMenu[indeks]) posisi = i;
            }
            try {
                int gabungan = posisi < 0 ? jumlah : Math.addExact(pesanan[posisi].jumlah, jumlah);
                long subtotal = 0;
                for (int i = 0; i < banyak; i++) subtotal = Math.addExact(subtotal, pesanan[i].total());
                subtotal = Math.addExact(subtotal, (long) daftarMenu[indeks].getHarga() * jumlah);
                if (subtotal > Long.MAX_VALUE / 2) throw new ArithmeticException();
                if (posisi >= 0) {
                    pesanan[posisi].jumlah = gabungan;
                } else {
                    if (banyak == pesanan.length) pesanan = Arrays.copyOf(pesanan, banyak * 2);
                    pesanan[banyak++] = new Pesanan(daftarMenu[indeks], jumlah);
                }
                System.out.printf("Ditambahkan: %s x%d.%n", daftarMenu[indeks].getNama(), jumlah);
            } catch (ArithmeticException terlaluBesar) {
                System.out.println("Pesanan melampaui kapasitas angka. Penambahan dibatalkan; kurangi jumlah.");
            }
        }
    }

    static int hapusPesanan(Pesanan[] pesanan, int banyak) {
        if (banyak == 0) {
            System.out.println("Belum ada pesanan untuk dihapus.");
            return banyak;
        }
        for (int i = 0; i < banyak; i++) {
            System.out.printf("%d. %s x%d%n", i + 1, pesanan[i].menu.getNama(), pesanan[i].jumlah);
        }
        int nomor = bacaAngka("Nomor pesanan yang dihapus (0 kembali): ", 0, banyak);
        if (nomor != 0 && konfirmasi("Hapus seluruh jumlah item ini?")) {
            for (int i = nomor - 1; i < banyak - 1; i++) pesanan[i] = pesanan[i + 1];
            pesanan[--banyak] = null;
            System.out.println("Item pesanan dihapus.");
        }
        return banyak;
    }

    static RincianBiaya hitungBiaya(Pesanan[] pesanan, int banyak) {
        RincianBiaya biaya = new RincianBiaya();
        for (int i = 0; i < banyak; i++) biaya.subtotal += pesanan[i].total();
        if (banyak == 0) return biaya;
        // Pembulatan setengah ke atas ke rupiah terdekat, tanpa floating point.
        biaya.pajak = (biaya.subtotal + 5) / 10;
        biaya.pelayanan = BIAYA_PELAYANAN;
        if (biaya.subtotal > 100_000) biaya.diskon = (biaya.subtotal + 5) / 10;
        if (biaya.subtotal > 50_000) {
            for (int i = 0; i < banyak; i++) {
                if (pesanan[i].menu.getKategori().equals(Menu.MINUMAN)) {
                    if (pesanan[i].menu.getNama().equalsIgnoreCase(MENU_PROMO)) {
                        biaya.jumlahGratis += pesanan[i].jumlah / 2;
                        biaya.potonganPromo += (long) (pesanan[i].jumlah / 2) * pesanan[i].menu.getHarga();
                    }
                }
            }
        }
        biaya.total = biaya.subtotal - biaya.diskon - biaya.potonganPromo
                + biaya.pajak + biaya.pelayanan;
        return biaya;
    }

    static void cetakStruk(Pesanan[] pesanan, int banyak) {
        if (banyak == 0) {
            System.out.println("Pesanan kosong. Tidak ada biaya yang dikenakan.");
            return;
        }
        RincianBiaya biaya = hitungBiaya(pesanan, banyak);
        System.out.println("\n=== STRUK RESTORAN GATHAN ===");
        for (int i = 0; i < banyak; i++) {
            Pesanan item = pesanan[i];
            System.out.printf("%s | %d x %s = %s%n", item.menu.getNama(), item.jumlah,
                    rupiah(item.menu.getHarga()), rupiah(item.total()));
        }
        System.out.println("Subtotal pesanan      : " + rupiah(biaya.subtotal));
        if (biaya.diskon > 0) {
            System.out.println("Diskon 10% (>100.000)  : -" + rupiah(biaya.diskon));
        } else {
            System.out.println("Diskon 10%            : tidak berlaku");
        }
        if (biaya.jumlahGratis > 0) {
            System.out.printf("Promo Es Teh: %d gelas gratis, potongan %s%n",
                    biaya.jumlahGratis, rupiah(biaya.potonganPromo));
        } else if (biaya.subtotal > 50_000) {
            System.out.println("Promo Es Teh          : tidak ada pasangan Es Teh");
        } else {
            System.out.println("Promo Es Teh          : subtotal belum melebihi Rp50.000");
        }
        System.out.println("Setelah diskon/promo  : " + rupiah(biaya.subtotal - biaya.diskon - biaya.potonganPromo));
        System.out.println("Pajak 10% x " + rupiah(biaya.subtotal) + " : " + rupiah(biaya.pajak));
        System.out.println("Biaya pelayanan       : " + rupiah(biaya.pelayanan));
        System.out.println("TOTAL BAYAR           : " + rupiah(biaya.total));
        System.out.println("Terima kasih atas kunjungan Anda.");
    }

    static void kelolaMenu() {
        int pilihan;
        do {
            System.out.println("\n=== PENGELOLAAN MENU ===");
            System.out.println("1. Lihat menu\n2. Tambah menu\n3. Ubah harga\n4. Hapus menu\n0. Kembali");
            pilihan = bacaAngka("Pilihan: ", 0, 4);
            switch (pilihan) {
                case 1: tampilkanMenu(); break;
                case 2: tambahMenu(); break;
                case 3: ubahHarga(); break;
                case 4: hapusMenu(); break;
                case 0: break;
                default: break;
            }
        } while (pilihan != 0);
    }

    static void tambahMenu() {
        do {
            String nama;
            while (true) {
                nama = bacaTeks("Nama menu baru (0 kembali): ");
                if (nama.equals("0")) return;
                boolean namaAngka = nama.matches("[+-]?\\d+");
                boolean perintah = nama.equalsIgnoreCase("selesai") || nama.equalsIgnoreCase("kembali")
                        || nama.equalsIgnoreCase("hapus");
                if (!nama.isEmpty() && !namaAngka && !perintah && cariMenu(nama) < 0) break;
                System.out.println("Nama harus unik, tidak kosong, bukan angka atau perintah aplikasi.");
            }
            int kategori = bacaAngka("Kategori (1 makanan, 2 minuman): ", 1, 2);
            int harga = bacaAngka("Harga dalam rupiah (tanpa titik): ", 1, Integer.MAX_VALUE);
            if (jumlahMenu == daftarMenu.length) {
                daftarMenu = Arrays.copyOf(daftarMenu, Math.max(4, jumlahMenu * 2));
            }
            daftarMenu[jumlahMenu++] = new Menu(nama, harga, kategori == 1 ? Menu.MAKANAN : Menu.MINUMAN);
            System.out.println("Menu berhasil ditambahkan.");
        } while (konfirmasi("Tambah menu lagi?"));
    }

    static void ubahHarga() {
        if (jumlahMenu == 0) {
            System.out.println("Tidak ada menu untuk diubah.");
            return;
        }
        tampilkanMenu();
        int nomor = bacaAngka("Nomor menu yang diubah (0 kembali): ", 0, jumlahMenu);
        if (nomor == 0) return;
        Menu menu = daftarMenu[nomor - 1];
        int harga = bacaAngka("Harga baru (rupiah tanpa titik): ", 1, Integer.MAX_VALUE);
        if (konfirmasi("Ubah " + menu.getNama() + " dari " + rupiah(menu.getHarga()) + " menjadi " + rupiah(harga) + "?")) {
            menu.setHarga(harga);
            System.out.println("Harga berhasil diubah.");
        } else {
            System.out.println("Perubahan harga dibatalkan.");
        }
    }

    static void hapusMenu() {
        if (jumlahMenu == 0) {
            System.out.println("Tidak ada menu untuk dihapus.");
            return;
        }
        tampilkanMenu();
        int nomor = bacaAngka("Nomor menu yang dihapus (0 kembali): ", 0, jumlahMenu);
        if (nomor == 0) return;
        if (konfirmasi("Hapus menu " + daftarMenu[nomor - 1].getNama() + "?")) {
            for (int i = nomor - 1; i < jumlahMenu - 1; i++) daftarMenu[i] = daftarMenu[i + 1];
            daftarMenu[--jumlahMenu] = null;
            System.out.println("Menu berhasil dihapus.");
        } else {
            System.out.println("Penghapusan menu dibatalkan.");
        }
    }
}
