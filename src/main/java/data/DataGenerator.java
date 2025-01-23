package data;

import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    private final Faker faker; // Khởi tạo faker như một biến final
    private final EntityManager em; // Khởi tạo EntityManager như một biến thành viên

    public static int maHDCount = 1;
    public static int maNVCount = 1;
    public static int maKHCount = 1;
    public static int maThuocCount = 1;
    public static int maDoiTraCount = 1;
    public static int maPhieuNhapCount = 1;
    public static int maNCCCount = 1;

    public DataGenerator() {
        this.faker = new Faker();
        this.em = Persistence.createEntityManagerFactory("mariadb").createEntityManager(); // Khởi tạo EntityManager
    }

    private String generateMaHD() {
        String maHD;
        do {
            maHD = String.format("HD%06d", maHDCount++);
        } while (em.find(HoaDon.class, maHD) != null); // Kiểm tra nếu ID đã tồn tại
        return maHD;
    }

    private String generateMaNCC() {
        String maNCC;
        do {
            maNCC = String.format("NCC%06d", maNCCCount++);
        } while (em.find(HoaDon.class, maNCC) != null); // Kiểm tra nếu ID đã tồn tại
        return maNCC;
    }

    private String generateMaNV() {
        String maNV;
        do {
            maNV = String.format("NV%06d", maNVCount++);
        } while (em.find(NhanVien.class, maNV) != null); // Kiểm tra nếu ID đã tồn tại
        return maNV;
    }

    private String generateMaKH() {
        String maKH;
        do {
            maKH = String.format("KH%06d", maKHCount++);
        } while (em.find(KhachHang.class, maKH) != null); // Kiểm tra nếu ID đã tồn tại
        return maKH;
    }

    private String generateMaThuoc() {
        String maThuoc;
        do {
            maThuoc = String.format("SP%06d", maThuocCount++);
        } while (em.find(Thuoc.class, maThuoc) != null); // Kiểm tra nếu ID đã tồn tại
        return maThuoc;
    }

    private String generateMaDoiTra() {
        String maDoiTra;
        do {
            maDoiTra = String.format("DT%06d", maDoiTraCount++);
        } while (em.find(DoiTra.class, maDoiTra) != null); // Kiểm tra nếu ID đã tồn tại
        return maDoiTra;
    }

    private String generateMaPhieuNhap() {
        String maPhieuNhap;
        do {
            maPhieuNhap = String.format("PN%06d", maPhieuNhapCount++);
        } while (em.find(DoiTra.class, maPhieuNhap) != null); // Kiểm tra nếu ID đã tồn tại
        return maPhieuNhap;
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

    public KhachHang generateKH() {
        KhachHang khachHang = new KhachHang();
        khachHang.setId(generateMaKH());
        khachHang.setHoTen(faker.name().fullName());
        khachHang.setSdt(faker.phoneNumber().phoneNumber());
        khachHang.setGioiTinh(faker.options().option(GioiTinh.class));
        khachHang.setNgayThamGia(generateRandomDateWithinLast10Years());
        khachHang.setHangMuc(faker.options().option(HangMuc.class));
        khachHang.setTongChiTieu(faker.number().randomDouble(2, 1000, 1000000));
        return khachHang;
    }

    public NhaCungCap generateNCC() {
        NhaCungCap nhaCungCap = new NhaCungCap();
        nhaCungCap.setId(generateMaNCC());
        nhaCungCap.setTenNCC(faker.name().fullName());
        nhaCungCap.setSdt(faker.phoneNumber().phoneNumber());
        nhaCungCap.setEmail(faker.internet().emailAddress());
        nhaCungCap.setDiaChi(faker.address().fullAddress());
        return nhaCungCap;
    }

    public NhanVien generateNV() {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setId(generateMaNV());
        nhanVien.setHoTen(faker.name().fullName());
        nhanVien.setSdt(faker.phoneNumber().phoneNumber());
        nhanVien.setEmail(faker.internet().emailAddress());
        nhanVien.setGioiTinh(faker.options().option(GioiTinh.class));
        nhanVien.setNgaySinh(generateRandomBirthday(18, 60));
        nhanVien.setNgayVaoLam(generateRandomDateWithinLast10Years());
        nhanVien.setChucVu(faker.options().option(ChucVu.class));
        return nhanVien;
    }

    public TaiKhoan generateTK(NhanVien nhanVien) {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setNhanVien(nhanVien);
        taiKhoan.setPassword(faker.internet().password(8, 16));
        return taiKhoan;
    }

    private Thuoc generateThuoc() {
        Thuoc thuoc = new Thuoc();
        thuoc.setId(generateMaThuoc());
        thuoc.setTenThuoc(faker.medication().drugName());
        thuoc.setThanhPhan(String.join(", ", faker.lorem().words(3)));
        thuoc.setDonViTinh(faker.options().option(DonViTinh.class));
        thuoc.setGiaThuoc(faker.number().randomDouble(2, 1000, 1000000));
        thuoc.setNgaySanXuat(LocalDate.now().minusDays(faker.number().numberBetween(1, 365)));
        thuoc.setHanSuDung(LocalDate.now().plusDays(faker.number().numberBetween(1, 365)));
        thuoc.setHinhAnh(faker.internet().image());
        return thuoc;
    }

    public HoaDon generateHD(KhachHang khachHang, NhanVien nhanVien) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setId(generateMaHD());
        hoaDon.setNgayTaoHD(LocalDateTime.now());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNhanVien(nhanVien);
        return hoaDon;
    }

    public ChiTietHoaDon generateChiTietHD(Thuoc thuoc, HoaDon hoaDon) {
        ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
        chiTietHoaDon.setHoaDon(hoaDon);
        chiTietHoaDon.setThuoc(thuoc);
        chiTietHoaDon.setSoLuong(faker.number().numberBetween(1, 100));
        chiTietHoaDon.setDonGia(thuoc.getGiaThuoc());
        return chiTietHoaDon;
    }

    public DoiTra generateDT(KhachHang khachHang, NhanVien nhanVien) {
        DoiTra doiTra = new DoiTra();
        doiTra.setId(generateMaDoiTra());
        doiTra.setNgayDoiTra(LocalDateTime.now());
        doiTra.setKhachHang(khachHang);
        doiTra.setNhanVien(nhanVien);
        doiTra.setTrangThai(faker.options().option(TrangThai.values()));  // Tạo trạng thái ngẫu nhiên cho trangThai
        return doiTra;
    }

    public ChiTietDoiTra generateChiTietDT(Thuoc thuoc, DoiTra doiTra) {
        ChiTietDoiTra chiTietDoiTra = new ChiTietDoiTra();
        chiTietDoiTra.setDoiTra(doiTra);
        chiTietDoiTra.setThuoc(thuoc);
        chiTietDoiTra.setSoLuong(faker.number().numberBetween(1, 100));
        chiTietDoiTra.setDonGia(thuoc.getGiaThuoc());
        return chiTietDoiTra;
    }

    public PhieuNhap generatePhieuNhap(NhaCungCap nhaCungCap, NhanVien nhanVien) {
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setId(generateMaPhieuNhap());
        phieuNhap.setNgayTaoPN(LocalDateTime.now());
        phieuNhap.setNhaCungCap(nhaCungCap);
        phieuNhap.setNhanVien(nhanVien);
        return phieuNhap;
    }

    public ChiTietPhieuNhap generateChiTietPhieuNhap(Thuoc thuoc, PhieuNhap phieuNhap) {
        ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
        chiTietPhieuNhap.setPhieuNhap(phieuNhap);
        chiTietPhieuNhap.setThuoc(thuoc);
        chiTietPhieuNhap.setSoLuong(faker.number().numberBetween(1, 100));
        chiTietPhieuNhap.setDonGia(faker.number().randomDouble(2, 1000, 100000));
        return chiTietPhieuNhap;
    }

    public void generateData() {
        EntityTransaction tr = em.getTransaction();
        try {
            for (int i = 0; i < 10; i++) {
                tr.begin();
                KhachHang khachHang = generateKH();
                em.persist(khachHang);
                em.flush();

                NhanVien nhanVien = generateNV();
                em.persist(nhanVien);
                em.flush();

                TaiKhoan taiKhoan = generateTK(nhanVien);
                em.persist(taiKhoan);
                em.flush();

                NhaCungCap nhaCungCap = generateNCC();
                em.persist(nhaCungCap);
                em.flush();

                List<Thuoc> thuocList = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    Thuoc thuoc = generateThuoc();
                    em.persist(thuoc);
                    em.flush();
                    thuocList.add(thuoc);
                }

                PhieuNhap phieuNhap = generatePhieuNhap(nhaCungCap, nhanVien);
                em.persist(phieuNhap);
                em.flush();

                for (int j = 0; j < 5; j++) {
                    ChiTietPhieuNhap chiTietPhieuNhap = generateChiTietPhieuNhap(thuocList.get(j), phieuNhap);
                    em.persist(chiTietPhieuNhap);
                    em.flush();
                }

                HoaDon hoaDon = generateHD(khachHang, nhanVien);
                em.persist(hoaDon);
                em.flush();

                for (int j = 0; j < 10; j++) {
                    ChiTietHoaDon chiTietHoaDon = generateChiTietHD(thuocList.get(j), hoaDon);
                    em.persist(chiTietHoaDon);
                    em.flush();
                }

                DoiTra doiTra = generateDT(khachHang, nhanVien);
                em.persist(doiTra);
                em.flush();

                for (int j = 0; j < 3; j++) {
                    ChiTietDoiTra chiTietDoiTra = generateChiTietDT(thuocList.get(j), doiTra);
                    em.persist(chiTietDoiTra);
                    em.flush();
                }
                tr.commit();
            }
        } catch (Exception e) {
            tr.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        DataGenerator dg = new DataGenerator();
        dg.generateData();
    }
}
