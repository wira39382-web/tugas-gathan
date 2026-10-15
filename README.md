# Tugas Praktik 2 — Aplikasi Restoran Java

Aplikasi konsol berbahasa Indonesia untuk pemesanan dan pengelolaan menu restoran.
Menggunakan class `Menu`, class utama `Main`, array, String, percabangan, dan pengulangan.

## Menjalankan

Prasyarat: JDK 17 atau lebih baru. Java 21 tersedia di lingkungan cloud ini.
Tidak ada dependensi pihak ketiga, database, layanan jaringan, maupun kredensial.

```bash
bash scripts/run.sh
```

Kompilasi manual pada komputer dengan JDK:

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/Menu.java src/Main.java
java -cp out Main
```

Di cloud, perintah `javac` tidak ada pada PATH tetapi modul compiler tersedia.
`scripts/build.sh` otomatis memakai `java -m jdk.compiler/com.sun.tools.javac.Main`.

## Aturan transaksi

- Ada empat makanan dan empat minuman awal; daftar tampil menurut kategori.
- Pesan dengan nomor atau nama, lalu masukkan jumlah bulat positif. Nama tidak peka huruf besar/kecil.
- Input pesanan berulang tidak dibatasi sejumlah langkah tertentu. Array diperbesar sesuai kebutuhan;
  item yang sama digabung. Batas jumlah per item dan harga mengikuti kapasitas `int` Java.
- `selesai` mencetak struk, `hapus` menghapus item pesanan dengan konfirmasi,
  dan `kembali` membatalkan pesanan serta kembali ke menu utama.
- Subtotal adalah jumlah harga satuan dikali jumlah **sebelum** potongan.
- Subtotal **lebih dari** Rp100.000: diskon 10% dari subtotal.
- Subtotal **lebih dari** Rp50.000: setiap dua Es Teh dalam jumlah pesanan mendapat
  satu gratis. Contoh: pesan 3, bayar 2; pesan 4, bayar 2. Tidak menambahkan gelas di luar jumlah pesanan.
- Kedua promo dapat digabung. Pajak 10% dihitung dari subtotal sebelum potongan.
  Pajak dan diskon dibulatkan ke rupiah terdekat, nilai setengah dibulatkan ke atas.
- Total = subtotal − diskon − potongan Es Teh + pajak + pelayanan Rp20.000.
- Pesanan kosong/batal tidak dikenai pajak maupun pelayanan.
- Promo melekat pada minuman bernama `Es Teh`, termasuk setelah harganya diubah.
  Menghapus Es Teh menonaktifkan promo; menambahkannya kembali sebagai minuman mengaktifkannya kembali.
- Ubah harga dan hapus menu hanya berjalan setelah konfirmasi `Ya`.
  `Tidak` membatalkan; jawaban lain diminta ulang. Nomor `0` kembali pada submenu.

Data menu dan pesanan disimpan dalam memori. Perubahan menu berlaku selama aplikasi berjalan
dan kembali ke menu awal setelah aplikasi dimulai ulang. Soal tidak mensyaratkan penyimpanan permanen
atau login pemilik, sehingga kedua fitur tersebut tidak ditambahkan.

## Pemeriksaan otomatis

Memerlukan Python 3 hanya untuk pengujian, bukan untuk menjalankan aplikasi:

```bash
bash scripts/test.sh
```

Pengujian menjalankan proses Java dengan input konsol, memeriksa hasil transaksi,
batas promo, konfirmasi pengelolaan, pengembangan array, dan input tidak valid.

## Laporan

- [Laporan dengan source code lengkap](docs/LAPORAN.md)
- [Laporan Word yang dapat diedit](docs/LAPORAN.docx)

Format laporan terlampir tidak tersedia dalam percakapan. Laporan menggunakan struktur umum;
lengkapi identitas mahasiswa dan sesuaikan format kampus sebelum dikumpulkan.
