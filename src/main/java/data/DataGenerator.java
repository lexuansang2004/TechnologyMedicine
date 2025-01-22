package data;

import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private final Faker faker; // Khởi tạo faker như một biến final
    public static int maHDCount = 1;
    public static int maNVCount = 1;
    public static int maKHCount = 1;
    public static int maThuocCount = 1;

    public DataGenerator() {
        this.faker = new Faker(); // Khởi tạo Faker trong constructor
    }

    private String gererateMaHD() {
        return String.format("HD%06d", maHDCount++);
    }

    private String gererateMaNV() {
        return String.format("NV%06d", maNVCount++);
    }

    private String gererateMaKH() {
        return String.format("KH%06d", maKHCount++);
    }

    private String gererateMaThuoc() {
        return String.format("SP%06d", maThuocCount++);
    }

    private Date generateRandomDateWithinLast10Years() {
        LocalDate now = LocalDate.now();
        LocalDate tenYearsAgo = now.minusYears(10);
        long startEpochDay = tenYearsAgo.toEpochDay();
        long endEpochDay = now.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return Date.from(LocalDate.ofEpochDay(randomDay).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate generateRandomBirthday(int minAge, int maxAge) {
        LocalDate now = LocalDate.now();
        LocalDate maxDate = now.minusYears(minAge);
        LocalDate minDate = now.minusYears(maxAge);
        long startEpochDay = minDate.toEpochDay();
        long endEpochDay = maxDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }

    public KhachHang gererateKH() {
        KhachHang khachHang = new KhachHang();
        khachHang.setId(gererateMaKH());
        khachHang.setHoTen(faker.name().fullName());
        khachHang.setSdt(faker.phoneNumber().phoneNumber());
        khachHang.setGioiTinh(faker.options().option(GioiTinh.class));
        khachHang.setNgayThamGia(generateRandomDateWithinLast10Years());
        khachHang.setHangMuc(faker.options().option(HangMuc.class));
        khachHang.setTongChiTieu(faker.number().randomDouble(2, 1000, 1000000));
        return khachHang;
    }

    public NhanVien gererateNV() {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setId(gererateMaNV());
        nhanVien.setHoTen(faker.name().fullName());
        nhanVien.setSdt(faker.phoneNumber().phoneNumber());
        nhanVien.setEmail(faker.internet().emailAddress());
        nhanVien.setGioiTinh(faker.options().option(GioiTinh.class));
        nhanVien.setNgaySinh(generateRandomBirthday(18, 60));
        nhanVien.setNgayVaoLam(generateRandomDateWithinLast10Years());
        nhanVien.setChucVu(faker.options().option(ChucVu.class));
        return nhanVien;
    }

    public TaiKhoan gererateTK(NhanVien nhanVien) {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setNhanVien(nhanVien);
        taiKhoan.setPassword(faker.internet().password(8, 16));
        return taiKhoan;
    }

    private Thuoc gererateThuoc() {
        Thuoc thuoc = new Thuoc();
        thuoc.setId(gererateMaThuoc());
        thuoc.setTenThuoc(faker.medication().drugName());
        thuoc.setThanhPhan(String.join(", ", faker.lorem().words(3)));
        thuoc.setDonViTinh(faker.options().option(DonViTinh.class));
        thuoc.setGiaThuoc(faker.number().randomDouble(2, 1000, 1000000));
        thuoc.setNgaySanXuat(LocalDate.now().minusDays(faker.number().numberBetween(1, 365)));
        thuoc.setHanSuDung(LocalDate.now().plusDays(faker.number().numberBetween(1, 365)));
        thuoc.setHinhAnh(faker.internet().image());
        return thuoc;
    }

    public HoaDon gererateHD(KhachHang khachHang, NhanVien nhanVien) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setId(gererateMaHD());
        hoaDon.setNgayTaoHD(LocalDate.now());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNhanVien(nhanVien);
        return hoaDon;
    }

    public ChiTietHoaDon gererateChiTietHD(Thuoc thuoc, HoaDon hoaDon) {
        ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
        chiTietHoaDon.setHoaDon(hoaDon);
        chiTietHoaDon.setThuoc(thuoc);
        chiTietHoaDon.setSoLuong(faker.number().numberBetween(1, 100));
        chiTietHoaDon.setDonGia(thuoc.getGiaThuoc());
        return chiTietHoaDon;
    }

    public void generateData() {
        EntityManager em = Persistence.createEntityManagerFactory("mariadb").createEntityManager();
        EntityTransaction tr = em.getTransaction();
        for (int i = 0; i < 10; i++) {
            tr.begin();
            KhachHang khachHang = gererateKH();
            em.persist(khachHang);
            NhanVien nhanVien = gererateNV();
            em.persist(nhanVien);
            TaiKhoan taiKhoan = gererateTK(nhanVien);
            em.persist(taiKhoan);

            for (int j = 0; j < 10; j++) {
                Thuoc thuoc = gererateThuoc();
                em.persist(thuoc);
            }

            HoaDon hoaDon = gererateHD(khachHang, nhanVien);
            em.persist(hoaDon);

            for (int j = 0; j < 10; j++) {
                em.persist(gererateChiTietHD(gererateThuoc(), hoaDon));
            }
            tr.commit();
        }
    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator();
        dg.generateData();
    }
}
