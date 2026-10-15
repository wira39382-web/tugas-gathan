# LAPORAN TUGAS PRAKTIK 2

## Aplikasi Restoran Sederhana Menggunakan Java

Nama: **[Isi nama mahasiswa]**  
NIM: **[Isi NIM]**  
Program studi: **[Isi program studi]**  
Mata kuliah: **[Isi nama mata kuliah]**  
Tutor: **[Isi nama tutor]**  
Tanggal pengumpulan: **[Isi tanggal]**

Catatan format: format laporan terlampir belum tersedia. Dokumen ini memakai
struktur umum dan perlu disesuaikan dengan format kampus sebelum dikumpulkan.

## 1. Tujuan

Membuat aplikasi restoran berbasis konsol untuk mengelola menu, menerima pesanan,
menghitung pajak dan promosi, serta mencetak struk. Program menerapkan tipe data,
variabel, identifier, keyword, class, object, method, array, String, percabangan,
dan perulangan.

## 2. Lingkungan dan cara menjalankan

Source code menggunakan Java tanpa pustaka eksternal. Gunakan JDK 17 atau lebih baru.
Validasi dilakukan dengan Java 21.0.12.1 di lingkungan cloud Linux.
Python 3 digunakan untuk menjalankan pengujian otomatis; aplikasi Java tidak membutuhkannya.

Jalankan dari direktori proyek:

```bash
bash scripts/run.sh
```

Pada komputer dengan JDK, dapat pula dikompilasi langsung:

```bash
mkdir -p out
javac -encoding UTF-8 -d out src/Menu.java src/Main.java
java -cp out Main
```

Lingkungan cloud menyediakan modul `jdk.compiler`, walaupun executable `javac`
tidak ada pada PATH. Skrip build otomatis menggunakan compiler melalui modul tersebut.
Build mengikuti versi Java aktif; hasil kompilasi Java 21 dijalankan dengan Java 21 atau lebih baru.
Tidak dibutuhkan koneksi jaringan, database, maupun API key.

## 3. Rancangan data dan class

### 3.1 Class Menu

`Menu` merepresentasikan satu item restoran. Atribut `nama` dan `kategori` bertipe
`String`, sedangkan `harga` bertipe `int` dalam rupiah. Atribut dibuat `private`
dan diakses melalui getter. `setHarga()` memvalidasi harga positif sebelum perubahan.
Konstanta `MAKANAN` dan `MINUMAN` menjaga konsistensi kategori.

### 3.2 Class Main dan object pesanan

`Main` mengatur navigasi, input, array menu, pengelolaan menu, perhitungan dan struk.
Inner class `Pesanan` menyimpan referensi object `Menu` beserta jumlah yang dipesan.
Inner class `RincianBiaya` menyimpan hasil perhitungan supaya struk memakai hasil yang sama.
Object dibuat dengan keyword `new`.

Menu restoran disimpan dalam `Menu[]`, dan pesanan disimpan dalam `Pesanan[]`.
Variabel `jumlahMenu` dan `banyak` menunjukkan jumlah elemen aktif, berbeda dari kapasitas array.
Ketika penuh, `Arrays.copyOf()` membuat array baru dengan kapasitas lebih besar.
Penghapusan menggeser elemen berikutnya ke kiri dan mengosongkan slot terakhir.
Tidak digunakan `ArrayList` untuk mengelola menu atau pesanan.

### 3.3 Menu awal

| Kategori | Nama | Harga |
|---|---|---:|
| Makanan | Nasi Goreng | Rp30.000 |
| Makanan | Mie Goreng | Rp25.000 |
| Makanan | Ayam Bakar | Rp40.000 |
| Makanan | Soto Ayam | Rp20.000 |
| Minuman | Es Teh | Rp10.000 |
| Minuman | Es Jeruk | Rp15.000 |
| Minuman | Jus Alpukat | Rp20.000 |
| Minuman | Air Mineral | Rp5.000 |

Tampilan dipisahkan menjadi kategori makanan dan minuman. Nomor mengacu pada
indeks array + 1, sehingga tetap cocok untuk memilih, mengubah, dan menghapus menu.

## 4. Alur program dan method

1. `main()` mengisi menu awal dan menampilkan pilihan pelanggan, pemilik, atau keluar.
2. `tampilkanMenu()` menampilkan daftar berdasarkan kategori dengan harga rupiah.
3. `terimaPesanan()` menerima nomor/nama menu dan jumlah; input diulang sampai
   `selesai`. Item yang sama digabung. `hapus` membuka pembatalan item,
   sedangkan `kembali` membatalkan transaksi dan kembali ke menu utama.
4. `hitungBiaya()` menghitung subtotal, diskon, promo, pajak, pelayanan, dan total.
5. `cetakStruk()` menampilkan nama item, jumlah, harga satuan, total per item,
   subtotal, potongan, nilai setelah potongan, dasar pajak, pajak, pelayanan dan total bayar.
6. `kelolaMenu()` membuka submenu lihat, tambah, ubah harga, hapus, dan kembali.
7. `tambahMenu()` menerima nama unik, kategori dan harga, lalu menawarkan penambahan berikutnya.
8. `ubahHarga()` dan `hapusMenu()` menampilkan daftar serta meminta nomor dan konfirmasi.
   `Ya` mengeksekusi tindakan, `Tidak` membatalkan, lalu kembali ke pengelolaan.
9. `hapusPesanan()` menghapus seluruh jumlah satu baris pesanan setelah konfirmasi.
10. `bacaAngka()`, `bacaTeks()`, `konfirmasi()`, dan `cariMenu()` membantu validasi input.

## 5. Aturan perhitungan

Aturan promo dan dasar pajak berikut telah disepakati untuk memperjelas bagian soal
yang tidak merinci mekanisme beli satu gratis satu.

- Subtotal = jumlah seluruh harga satuan × jumlah yang dipesan, sebelum potongan.
- Pajak = 10% subtotal. Pelayanan tetap Rp20.000 untuk transaksi tidak kosong.
- Subtotal **> Rp100.000** mendapat diskon 10% subtotal.
- Subtotal **> Rp50.000** mendapat promo untuk satu jenis minuman, yaitu Es Teh.
  Jumlah gratis = pembagian bulat jumlah Es Teh dengan 2.
  Potongan promo = jumlah gratis × harga satuan Es Teh saat itu.
- Pesan 2 Es Teh berarti menerima 2 dan membayar 1; pesan 3 menerima 3 dan membayar 2.
  Promo tidak menambahkan gelas di luar jumlah yang dipesan.
- Kedua promo dapat digabung, dengan kelayakan diperiksa dari subtotal sebelum potongan.
- Pajak dan diskon dibulatkan ke rupiah terdekat, nilai setengah ke atas.
  Rumus bilangan bulat untuk 10% adalah `(subtotal + 5) / 10`.
- Total bayar = subtotal − diskon − potongan promo + pajak + pelayanan.
- Pesanan kosong atau dibatalkan tidak menghasilkan tagihan.

Nilai tepat Rp50.000 belum memenuhi promo minuman; nilai tepat Rp100.000 belum
memenuhi diskon 10%. Operator `>` digunakan sesuai kata "melebihi" pada soal.

### Contoh perhitungan dan struk

Pesanan: 3 Nasi Goreng dan 2 Es Teh.

```text
Nasi Goreng | 3 x Rp30.000 = Rp90.000
Es Teh | 2 x Rp10.000 = Rp20.000
Subtotal pesanan      : Rp110.000
Diskon 10% (>100.000)  : -Rp11.000
Promo Es Teh: 1 gelas gratis, potongan Rp10.000
Setelah diskon/promo  : Rp89.000
Pajak 10% x Rp110.000 : Rp11.000
Biaya pelayanan       : Rp20.000
TOTAL BAYAR           : Rp120.000
```

Input untuk menjalankan contoh tersebut dari menu utama:

```text
1
1
3
5
2
selesai
0
```

## 6. Penerapan konsep pemrograman

| Konsep | Implementasi dan tujuan |
|---|---|
| `int` | Harga satuan, jumlah item, indeks dan pilihan menu |
| `long` | Perkalian harga dan jumlah, subtotal, pajak dan total; mencegah overflow `int` |
| `boolean` | Hasil konfirmasi dan penanda keberadaan kategori |
| `String` | Nama, kategori, input dan perintah; memakai `trim()` dan `equalsIgnoreCase()` |
| Identifier | `daftarMenu`, `jumlahMenu`, `BIAYA_PELAYANAN`, `hitungBiaya` |
| Keyword | `class`, `public`, `private`, `static`, `final`, `new`, `return`, `break`, `continue` |
| Class/object/method | `Menu`, `new Menu(...)`, `getHarga()` dan `setHarga()` |
| Array | Penyimpanan `Menu[]`, `Pesanan[]` dan kategori `String[]` |
| `if` | Memeriksa ambang diskon, array penuh, harga valid dan menu kosong |
| `if-else` | Eksekusi atau pembatalan perubahan harga berdasarkan konfirmasi |
| `if-else if` | Memilih perintah pelanggan dan informasi kelayakan promo pada struk |
| `switch case` | Menentukan menu utama dan menu pengelolaan |
| Nested `if` | Memeriksa kategori minuman lalu nama Es Teh untuk promo |
| `for` | Menampilkan elemen, mencari menu, menghitung subtotal dan menggeser data |
| `for-each` | Mengunjungi setiap kategori makanan/minuman |
| `while` | Menerima pesanan berulang serta mengulang input yang tidak valid |
| `do-while` | Menu utama, menu pengelolaan dan penambahan beberapa menu sekaligus |
| `try-catch` | Menangani angka tidak valid, kapasitas angka terlampaui, dan akhir input |

## 7. Validasi dan pengujian

Pengujian menggunakan proses Java sebenarnya dengan input konsol terprogram.
Jalankan `bash scripts/test.sh`. Hasil validasi: **25 metode uji lulus**, termasuk
enam subkasus batas promo. Tidak ada uji gagal atau dilewati pada validasi tersebut.
Kompilasi dengan `-Xlint:all` selesai tanpa peringatan.

### 7.1 Skenario keputusan perhitungan

Untuk tabel batas, pesanan berisi 2 Es Teh (Rp20.000) dan satu makanan yang harganya
disesuaikan melalui menu pemilik agar menghasilkan subtotal yang ditentukan.

| Subtotal | Diskon 10% | Promo Es Teh | Pajak | Total bayar |
|---:|---:|---:|---:|---:|
| Rp49.999 | Rp0 | Rp0 | Rp5.000 | Rp74.999 |
| Rp50.000 | Rp0 | Rp0 | Rp5.000 | Rp75.000 |
| Rp50.001 | Rp0 | Rp10.000 | Rp5.000 | Rp65.001 |
| Rp99.999 | Rp0 | Rp10.000 | Rp10.000 | Rp119.999 |
| Rp100.000 | Rp0 | Rp10.000 | Rp10.000 | Rp120.000 |
| Rp100.001 | Rp10.000 | Rp10.000 | Rp10.000 | Rp110.001 |

### 7.2 Skenario fungsional lainnya

| Skenario | Hasil yang diverifikasi |
|---|---|
| Menu awal | Empat makanan dan empat minuman tampil menurut kategori |
| 1 Nasi Goreng | Total Rp53.000, tanpa promo |
| 3 Nasi Goreng + 2 Es Teh | Kedua promo berlaku, total Rp120.000 |
| 4 Nasi Goreng | Diskon berlaku tanpa promo minuman, total Rp140.000 |
| 4 Es Jeruk | Promo Es Teh tidak diterapkan, total Rp86.000 |
| 1 Ayam Bakar + 3 Es Teh | Satu Es Teh gratis, total Rp87.000 |
| 2 Ayam Bakar + 1 Es Teh | Belum ada pasangan Es Teh, total Rp119.000 |
| Harga makanan Rp10.005 | Pajak dibulatkan menjadi Rp1.001 |
| Pesanan kosong | Tanpa struk dan tanpa biaya |
| Nama menu salah/nomor di luar daftar | Input menu diminta ulang |
| Jumlah nol, negatif, pecahan atau teks | Input jumlah diminta ulang |
| Input nama dengan variasi kapital | Menu dikenali dengan benar |
| Es Teh dipesan pada dua langkah | Jumlah digabung sebelum promo dihitung |
| Delapan item berbeda | Array pesanan bertambah tanpa kehilangan item |
| Perintah kembali | Transaksi dibatalkan tanpa tagihan |
| Hapus item: Tidak lalu Ya | Penolakan mempertahankan item, persetujuan menghapusnya |
| Hapus seluruh item | Pesanan menjadi kosong dan tidak ditagih |
| Tambah dua menu sekaligus | Array menu bertambah dan kedua item dapat dipesan |
| Nama kosong, duplikat, angka atau perintah | Nama baru diminta ulang |
| Ubah harga: Tidak / Ya | Harga lama tetap / harga baru dipakai dalam transaksi |
| Hapus menu: Tidak / Ya | Menu tetap / menu hilang dan indeks bergeser |
| Semua menu dihapus | Pesan kondisi kosong; menu baru tetap dapat ditambahkan |
| Nomor 0 pada submenu | Kembali tanpa perubahan |
| Akumulasi jumlah melampaui `int` | Penambahan ditolak dan pesanan sebelumnya tetap utuh |
| Input berakhir saat pemesanan | Program keluar normal tanpa mencetak tagihan belum selesai |

Validasi dilakukan pada instance cloud saat ini, bukan bukti pemulihan snapshot di tugas baru.

## 8. Batasan

Data bersifat sementara selama aplikasi berjalan. Tidak ada database, penyimpanan file menu,
antarmuka grafis, atau autentikasi pemilik karena tidak diminta dalam soal.
Harga dan jumlah harus berupa bilangan bulat positif yang dapat ditampung `int` Java
(maksimum 2.147.483.647). Total transaksi memakai `long`, dan penambahan ditolak
jika akumulasi terlalu besar agar perhitungan tidak rusak.

Promo mengikuti minuman bernama Es Teh. Jika menu tersebut dihapus, tidak ada promo
Es Teh; jika ditambahkan kembali sebagai minuman, promo kembali tersedia.
Jumlah masukan pemesanan tidak dibatasi sejumlah iterasi tetap, dengan kapasitas
praktis mengikuti memori dan batas numerik komputer.

## 9. Penutup

Program memenuhi alur pemesanan dan pengelolaan menu, menghitung biaya dan kedua promo,
serta menampilkan struk terperinci. Penggunaan array, object, method, percabangan
dan perulangan dapat ditunjukkan langsung melalui skenario demonstrasi.
Panduan penjelasan lisan tersedia pada `docs/PANDUAN_DEMO.md`.

## Lampiran — Source code lengkap

### Menu.java

```java
/** Satu menu restoran; harga disimpan sebagai rupiah bulat. */
public class Menu {
    public static final String MAKANAN = "makanan";
    public static final String MINUMAN = "minuman";

    private final String nama;
    private int harga;
    private final String kategori;

    public Menu(String nama, int harga, String kategori) {
        if (nama == null || nama.trim().isEmpty() || harga <= 0
                || (!MAKANAN.equals(kategori) && !MINUMAN.equals(kategori))) {
            throw new IllegalArgumentException("Data menu tidak valid.");
        }
        this.nama = nama.trim();
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() { return nama; }
    public int getHarga() { return harga; }
    public String getKategori() { return kategori; }

    public void setHarga(int harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus positif.");
        }
        this.harga = harga;
    }
}
```

### Main.java

```java
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
```

