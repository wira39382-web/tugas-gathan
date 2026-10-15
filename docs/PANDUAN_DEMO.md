# Panduan penjelasan pengerjaan Tugas Praktik 2

Panduan ini dapat dipakai untuk menjelaskan program secara langsung atau menyiapkan rekaman.
Isi identitas sendiri pada laporan. Coba semua langkah terlebih dahulu sebelum presentasi.

## 1. Pembukaan dan rancangan (sekitar 1 menit)

"Saya membuat aplikasi restoran berbasis konsol Java. Aplikasi memiliki menu pelanggan
dan pengelolaan pemilik. Data disimpan dalam array, dengan empat makanan dan empat minuman awal."

Buka `src/Menu.java`: jelaskan atribut nama, harga dan kategori, constructor,
akses `private`, getter, serta setter harga. Tunjukkan object `new Menu(...)` pada `Main`.
Jelaskan `int` untuk harga/jumlah, `long` untuk akumulasi uang dan `String` untuk nama/input.

## 2. Pemesanan dan struk (sekitar 2 menit)

Jalankan `bash scripts/run.sh`, pilih `1`. Tunjukkan kategori makanan dan minuman.
Masukkan nama yang tidak ada, misalnya `Pizza`, dan tunjukkan input diminta ulang.

Pesan Nasi Goreng (nomor 1) sebanyak 3 dan Es Teh (nomor 5) sebanyak 2.
Masukkan jumlah tidak valid seperti `0` terlebih dahulu untuk menunjukkan validasi.
Ketik `selesai` dan jelaskan:

- Subtotal Rp110.000.
- Diskon 10% Rp11.000 karena subtotal melebihi Rp100.000.
- Satu dari dua Es Teh gratis, potongan Rp10.000 karena subtotal melebihi Rp50.000.
- Setelah potongan Rp89.000.
- Pajak tetap 10% subtotal, yaitu Rp11.000; pelayanan Rp20.000.
- Total bayar Rp120.000.

"Kelayakan promo ditentukan dari subtotal sebelum potongan. Kata melebihi diterapkan
dengan operator lebih besar, sehingga nilai tepat pada batas belum memenuhi promo terkait."

## 3. Cabang keputusan lain (sekitar 1–2 menit)

- Pesan satu Nasi Goreng: tanpa promo, total Rp53.000.
- Pesan satu Ayam Bakar dan dua Es Teh: hanya promo minuman, total Rp76.000.
- Pesan empat Nasi Goreng: diskon 10%, tanpa pasangan Es Teh, total Rp140.000.
- Masuk pemesanan lalu ketik `selesai`: pesanan kosong tanpa biaya.
- Pesan item, ketik `hapus`, pilih nomornya, konfirmasi `Ya`: baris tersebut dihapus.

Tabel batas Rp50.000/Rp100.000 serta hasil lengkap ada pada laporan.

## 4. Pengelolaan menu (sekitar 2 menit)

Pilih `2` dari menu utama:

1. Pilih tambah menu; masukkan `Bakso`, kategori makanan, harga `18000`.
2. Jawab `Ya` untuk menambah `Kopi`, kategori minuman, harga `12000`; jawab `Tidak` untuk berhenti.
3. Pilih lihat menu, tunjukkan kedua menu baru dan pengelompokan kategori.
4. Pilih ubah harga, pilih nomor Bakso, masukkan harga baru. Jawab `Tidak`,
   tunjukkan harga tetap. Ulangi dan jawab `Ya`, tunjukkan perubahan.
5. Pilih hapus, pilih nomor Kopi. Tunjukkan pembatalan dengan `Tidak`, lalu ulangi dengan `Ya`.
6. Masukkan nomor di luar daftar untuk menunjukkan validasi. Gunakan `0` untuk kembali.

"Perubahan hanya diterapkan setelah konfirmasi. Data disimpan selama program hidup;
menjalankan ulang program akan mengembalikan menu awal."

## 5. Tunjukkan struktur kode dan pengujian (sekitar 1 menit)

- `Menu[]` dan `Pesanan[]` menyimpan data; `Arrays.copyOf()` memperbesar kapasitas.
- `for` menampilkan item, menghitung subtotal dan menggeser elemen saat penghapusan.
- `for-each` mengunjungi kategori makanan dan minuman.
- `while` mengulang pemesanan dan input yang belum valid.
- `do-while` menjalankan navigasi dan penambahan beberapa menu.
- `switch` memilih aksi; `if`, `if-else`, `else if`, dan nested `if` memeriksa kondisi.
- `equalsIgnoreCase()` mengenali nama menu dan perintah tanpa membedakan kapital.

Jalankan `bash scripts/test.sh` dan tunjukkan hasil 25 metode uji lulus.
Jelaskan bahwa pengujian mengirim input ke aplikasi Java lalu memeriksa output dan nilai tagihan,
termasuk skenario promo, pembatalan, pengelolaan, validasi, dan kapasitas array.
