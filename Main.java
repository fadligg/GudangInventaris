import gudangapp.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String barangFilePath = "Barang.txt";
        String kategoriFilePath = "Kategori.txt";

        Scanner scanner = new Scanner(System.in);
        ArrayListManagement arrayListManager = new ArrayListManagement();
        SortingSearching sorterSearcher = new SortingSearching();
        HashManagement hashManager = new HashManagement();
        TransaksiLinkedList transaksiList = new TransaksiLinkedList();
        TreeManagement treeManager = new TreeManagement();
        GraphManagement graphManager = new GraphManagement();
        FileDatabaseManager dbManager = new FileDatabaseManager();

        ArrayList<Barang> daftarBarang = dbManager.bacaBarangDariFile(barangFilePath);
        ArrayList<String> kategoriList = dbManager.bacaKategoriDariFile(kategoriFilePath);
        for (Barang barang : daftarBarang) {
            hashManager.tambahBarangKeHash(barang.getId(), barang);
        }
        for (String kategori : kategoriList) {
            treeManager.tambahKategori(null, kategori); // null untuk kategori tanpa parent
        }

        int pilihan;
        do {
            System.out.println("\n=== Sistem Manajemen Inventaris ===");
            System.out.println("1. Tambah Barang");
            System.out.println("2. Tampilkan Barang");
            System.out.println("3. Urutkan Barang");
            System.out.println("4. Cari Barang");
            System.out.println("5. Tambah Transaksi");
            System.out.println("6. Tampilkan Transaksi");
            System.out.println("7. Tambah Kategori");
            System.out.println("8. Tampilkan Kategori");
            System.out.println("9. Tambah Relasi Barang");
            System.out.println("10. Tampilkan Relasi Barang");
            System.out.println("0. Keluar");
            System.out.print("Masukkan pilihan: ");
            pilihan = Integer.parseInt(scanner.nextLine());

            switch (pilihan) {
                case 1 -> {
                    System.out.print("Masukkan ID Barang: ");
                    String id = scanner.nextLine();
                    System.out.print("Masukkan Nama Barang: ");
                    String nama = scanner.nextLine();
                    System.out.print("Masukkan Kategori Barang: ");
                    String kategori = scanner.nextLine();
                    System.out.print("Masukkan Stok Barang: ");
                    int stok = Integer.parseInt(scanner.nextLine());

                    if (treeManager.cariKategori(treeManager.getRoot(), kategori) != null) {
                        Barang barang = new Barang(id, nama, kategori, stok);
                        arrayListManager.tambahBarang(daftarBarang, barang);
                        hashManager.tambahBarangKeHash(id, barang);
                        dbManager.simpanBarangKeFile(barangFilePath, daftarBarang);
                    } else {
                        System.out.println("Kategori belum terdaftar. Tambahkan kategori terlebih dahulu.");
                    }
                }
                case 2 -> arrayListManager.tampilkanBarang(daftarBarang);
                case 3 -> {
                    System.out.print("Urutkan berdasarkan (id/nama/stok): ");
                    String tipe = scanner.nextLine();
                    sorterSearcher.urutkanBarang(daftarBarang, tipe);
                }
                case 4 -> {
                    System.out.print("Cari berdasarkan (id/nama/kategori): ");
                    String tipe = scanner.nextLine();
                    System.out.print("Masukkan nilai pencarian: ");
                    String key = scanner.nextLine();
                    if (tipe.equalsIgnoreCase("id")) {
                        Barang barang = hashManager.cariBarangDiHash(key);
                        if (barang != null) {
                            System.out.println("Barang ditemukan: " + barang);
                        } else {
                            System.out.println("Barang tidak ditemukan.");
                        }
                    } else {
                        sorterSearcher.cariBarang(daftarBarang, tipe, key);
                    }
                }
                case 5 -> {
                    hashManager.tampilkanSemuaBarangDiHash();
                    System.out.print("Masukkan ID Barang: ");
                    String idBarang = scanner.nextLine();
                    System.out.print("Masukkan Jumlah: ");
                    int jumlah = Integer.parseInt(scanner.nextLine());
                    System.out.print("Masukkan Tipe (Masuk/Keluar): ");
                    String tipe = scanner.nextLine();

                    Transaksi transaksi = new Transaksi(idBarang, jumlah, tipe);
                    transaksiList.tambahTransaksi(transaksi);

                    Barang barang = hashManager.cariBarangDiHash(idBarang);
                    if (barang != null) {
                        if (tipe.equalsIgnoreCase("Masuk")) {
                            barang.stok += jumlah;
                            System.out.println("Stok berhasil diperbarui. Stok saat ini: " + barang.stok);
                        } else if (tipe.equalsIgnoreCase("Keluar")) {
                            if (barang.stok >= jumlah) {
                                barang.stok -= jumlah;
                                System.out.println("Stok berhasil diperbarui. Stok saat ini: " + barang.stok);
                            } else {
                                System.out.println("Stok tidak mencukupi untuk transaksi ini.");
                            }
                        }
                        dbManager.simpanBarangKeFile(barangFilePath, daftarBarang);
                    } else {
                        System.out.println("ID Barang tidak ditemukan.");
                    }
                }
                case 6 -> transaksiList.tampilkanTransaksi();
                case 7 -> {
                    System.out.print("Masukkan Kategori Induk: ");
                    String parent = scanner.nextLine();
                    System.out.print("Masukkan Kategori Baru: ");
                    String kategori = scanner.nextLine();
                    treeManager.tambahKategori(parent, kategori);
                    treeManager.simpanTreeKeFile(kategoriFilePath);
                }
                case 8 -> treeManager.tampilkanTree(treeManager.getRoot(), 0);
                case 9 -> {
                    System.out.print("Masukkan Barang 1: ");
                    String barang1 = scanner.nextLine();
                    System.out.print("Masukkan Barang 2: ");
                    String barang2 = scanner.nextLine();
                    graphManager.tambahRelasiBarang(barang1, barang2);
                }
                case 10 -> graphManager.tampilkanRelasiBarang();
                case 0 -> System.out.println("Terima kasih telah menggunakan aplikasi.");
                default -> System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);

        scanner.close();
    }
}
