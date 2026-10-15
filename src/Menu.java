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
