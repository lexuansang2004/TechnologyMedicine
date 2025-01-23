package iuh.fit;

import dao.*;
import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final Random random = new Random();

    public static String generateMaThuoc(ThuocDAO thuocDAO) {
        String maThuoc;
        do {
            maThuoc = String.format("SP%06d", random.nextInt(1000000));
        } while (thuocDAO.getById(maThuoc) != null);
        return maThuoc;
    }
    public static String generateMaNV(NhanVienDAO nhanVienDAO) {
        String maNV;
        do {
            maNV = String.format("NV%06d", random.nextInt(1000000));
        } while (nhanVienDAO.getById(maNV) != null);
        return maNV;
    }
    public static String generateMaKH(KhachHangDAO khachHangDAO) {
        String maKH;
        do {
            maKH = String.format("KH%06d", random.nextInt(1000000));
        } while (khachHangDAO.getById(maKH) != null);
        return maKH;
    }
    public static String generateMaHD(HoaDonDAO hoaDonDAO) {
        String maHD;
        do {
            maHD = String.format("HD%06d", random.nextInt(1000000));
        } while (hoaDonDAO.getById(maHD) != null);
        return maHD;
    }
    public static String generateMaDoiTra(DoiTraDAO doiTraDAO) {
        String maDT;
        do {
            maDT = String.format("DT%06d", random.nextInt(1000000));
        } while (doiTraDAO.getById(maDT) != null);
        return maDT;
    }
    public static String generateMaPhieuNhap(PhieuNhapDAO phieuNhapDAO) {
        String maPN;
        do {
            maPN = String.format("PN%06d", random.nextInt(1000000));
        } while (phieuNhapDAO.getById(maPN) != null);
        return maPN;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        System.setOut(out);
        Faker faker = new Faker();
        EntityManager em = Persistence.createEntityManagerFactory("mariadb").createEntityManager();
        ThuocDAO thuocDAO = new ThuocDAO(em);
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO(em);
        KhachHangDAO khachHangDAO = new KhachHangDAO(em);
        NhanVienDAO nhanVienDAO = new NhanVienDAO(em);
        HoaDonDAO hoaDonDAO = new HoaDonDAO(em);
        DoiTraDAO doiTraDAO = new DoiTraDAO(em);
        PhieuNhapDAO phieuNhapDAO = new PhieuNhapDAO(em);
        NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO(em);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1. Quản lý tài khoản");
            System.out.println("2. Quản lý khách hàng");
            System.out.println("3. Quản lý nhân viên");
            System.out.println("4. Quản lý thuốc");
            System.out.println("5. Quản lý hóa đơn");
            System.out.println("6. Quản lý đơn đổi trả");
            System.out.println("7. Quản lý phiếu nhập");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            int mainChoice = sc.nextInt();
            sc.nextLine();

            switch (mainChoice) {
                case 1:
                    quanLyTaiKhoan(taiKhoanDAO, sc);
                    break;
                case 2:
                    quanLyKhachHang(khachHangDAO, sc);
                    break;
                case 3:
                    quanLyNhanVien(nhanVienDAO, sc);
                    break;
                case 4:
                    quanLyThuoc(thuocDAO, sc);
                    break;
                case 5:
                    quanLyHoaDon(hoaDonDAO, thuocDAO, khachHangDAO, nhanVienDAO, sc);
                    break;
                case 6:
                    quanLyDoiTra(doiTraDAO, thuocDAO, khachHangDAO, nhanVienDAO, sc);
                    break;
                case 7:
                    quanLyPhieuNhap(phieuNhapDAO, thuocDAO, nhaCungCapDAO, nhanVienDAO, sc);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    em.close();
                    sc.close();
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }

    private static void quanLyTaiKhoan(TaiKhoanDAO taiKhoanDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý tài khoản =====");
            System.out.println("1. Thêm tài khoản");
            System.out.println("2. Cập nhật tài khoản");
            System.out.println("3. Xóa tài khoản");
            System.out.println("4. Hiển thị tất cả tài khoản");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    // Thêm tài khoản
                    break;
                case 2:
                    // Cập nhật tài khoản
                    break;
                case 3:
                    // Xóa tài khoản
                    break;
                case 4:
                    // Hiển thị tất cả tài khoản
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }

    private static void quanLyKhachHang(KhachHangDAO khachHangDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý khách hàng =====");
            System.out.println("1. Thêm khách hàng");
            System.out.println("2. Cập nhật khách hàng");
            System.out.println("3. Xóa khách hàng");
            System.out.println("4. Hiển thị tất cả khách hàng");
            System.out.println("5. Tìm kiếm khách hàng");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    KhachHang newKhachHang = new KhachHang();
                    newKhachHang.setId(generateMaKH(khachHangDAO));
                    System.out.print("Nhập họ tên: ");
                    newKhachHang.setHoTen(sc.nextLine());
                    System.out.print("Nhập số điện thoại: ");
                    newKhachHang.setSdt(sc.nextLine());
                    if (khachHangDAO.save(newKhachHang)) {
                        System.out.println("Thêm khách hàng thành công!");
                    } else {
                        System.out.println("Thêm khách hàng thất bại!");
                    }
                    break;
                case 2:
                    System.out.print("Nhập ID khách hàng cần cập nhật: ");
                    String idUpdate = sc.nextLine();
                    KhachHang khachHangUpdate = khachHangDAO.getById(idUpdate);
                    if (khachHangUpdate != null) {
                        System.out.print("Nhập họ tên mới: ");
                        khachHangUpdate.setHoTen(sc.nextLine());
                        System.out.print("Nhập số điện thoại mới: ");
                        khachHangUpdate.setSdt(sc.nextLine());
                        if (khachHangDAO.update(khachHangUpdate)) {
                            System.out.println("Cập nhật khách hàng thành công!");
                        } else {
                            System.out.println("Cập nhật khách hàng thất bại!");
                        }
                    } else {
                        System.out.println("Không tìm thấy khách hàng với ID: " + idUpdate);
                    }
                    break;
                case 3:
                    System.out.print("Nhập ID khách hàng cần xóa: ");
                    String idDelete = sc.nextLine();
                    if (khachHangDAO.delete(idDelete)) {
                        System.out.println("Xóa khách hàng thành công!");
                    } else {
                        System.out.println("Xóa khách hàng thất bại!");
                    }
                    break;
                case 4:
                    List<KhachHang> allKhachHang = khachHangDAO.getAll();
                    System.out.println("Danh sách khách hàng:");
                    for (KhachHang kh : allKhachHang) {
                        System.out.printf("ID: %s, Họ tên: %s, Số điện thoại: %s\n",
                                kh.getId(), kh.getHoTen(), kh.getSdt());
                    }
                    break;
                case 5:
                    System.out.print("Nhập từ khóa tìm kiếm (tên hoặc số điện thoại): ");
                    String keyword = sc.nextLine();
                    List<KhachHang> ketQuaTimKiem = khachHangDAO.searchByNameOrPhone(keyword);
                    if (!ketQuaTimKiem.isEmpty()) {
                        for (KhachHang kh : ketQuaTimKiem) {
                            System.out.printf("ID: %s, Họ tên: %s, Số điện thoại: %s\n",
                                    kh.getId(), kh.getHoTen(), kh.getSdt());
                        }
                    } else {
                        System.out.println("Không tìm thấy khách hàng.");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    break;
            }
        }
    }

    private static void quanLyNhanVien(NhanVienDAO nhanVienDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý nhân viên =====");
            System.out.println("1. Thêm nhân viên");
            System.out.println("2. Cập nhật nhân viên");
            System.out.println("3. Xóa nhân viên");
            System.out.println("4. Hiển thị tất cả nhân viên");
            System.out.println("5. Tìm nhân viên");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    try {
                        NhanVien newNhanVien = new NhanVien();
                        newNhanVien.setId(generateMaNV(nhanVienDAO));
                        System.out.print("Nhập họ tên: ");
                        newNhanVien.setHoTen(sc.nextLine());
                        System.out.print("Nhập số điện thoại: ");
                        newNhanVien.setSdt(sc.nextLine());
                        System.out.print("Nhập email: ");
                        newNhanVien.setEmail(sc.nextLine());
                        System.out.print("Nhập giới tính (NAM/NU): ");
                        newNhanVien.setGioiTinh(GioiTinh.valueOf(sc.nextLine()));
                        System.out.print("Nhập ngày sinh (YYYY-MM-DD): ");
                        newNhanVien.setNgaySinh(LocalDate.parse(sc.nextLine()));
                        System.out.print("Nhập ngày vào làm (YYYY-MM-DD): ");
                        newNhanVien.setNgayVaoLam(Date.from(LocalDate.parse(sc.nextLine()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        System.out.print("Nhập chức vụ (NHANVIEN/QLCH): ");
                        newNhanVien.setChucVu(ChucVu.valueOf(sc.nextLine()));
                        nhanVienDAO.save(newNhanVien);
                        System.out.println("Nhân viên đã được thêm thành công!");
                    } catch (Exception e) {
                        System.out.println("Có lỗi xảy ra khi thêm nhân viên. Vui lòng thử lại.");
                    }
                    break;
                case 2:
                    System.out.print("Nhập mã nhân viên cần cập nhật: ");
                    String maNhanVienUpdate = sc.nextLine();
                    NhanVien nhanVienUpdate = (NhanVien) nhanVienDAO.getById(maNhanVienUpdate);
                    if (nhanVienUpdate != null) {
                        System.out.print("Cập nhật họ tên: ");
                        nhanVienUpdate.setHoTen(sc.nextLine());
                        System.out.print("Cập nhật số điện thoại: ");
                        nhanVienUpdate.setSdt(sc.nextLine());
                        System.out.print("Cập nhật email: ");
                        nhanVienUpdate.setEmail(sc.nextLine());
                        System.out.print("Cập nhật giới tính (NAM/NU): ");
                        nhanVienUpdate.setGioiTinh(GioiTinh.valueOf(sc.nextLine()));
                        System.out.print("Cập nhật ngày sinh (YYYY-MM-DD): ");
                        nhanVienUpdate.setNgaySinh(LocalDate.parse(sc.nextLine()));
                        System.out.print("Cập nhật ngày vào làm (YYYY-MM-DD): ");
                        nhanVienUpdate.setNgayVaoLam(Date.from(LocalDate.parse(sc.nextLine()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        System.out.print("Cập nhật chức vụ (NHANVIEN/QLCH): ");
                        nhanVienUpdate.setChucVu(ChucVu.valueOf(sc.nextLine()));
                        nhanVienDAO.update(nhanVienUpdate);
                        System.out.println("Cập nhật nhân viên thành công!");
                    } else {
                        System.out.println("Không tìm thấy nhân viên với mã đã nhập.");
                    }
                    break;
                case 3:
                    System.out.print("Nhập mã nhân viên cần xóa: ");
                    String maNhanVienDelete = sc.nextLine();
                    nhanVienDAO.delete(maNhanVienDelete);
                    System.out.println("Nhân viên đã được xóa thành công!");
                    break;
                case 4:
                    List<NhanVien> allNhanVien = nhanVienDAO.getAll();
                    System.out.println("Danh sách tất cả nhân viên:");
                    for (NhanVien nhanVien : allNhanVien) {
                        System.out.printf("ID: %s, Họ tên: %s, SĐT: %s, Email: %s, Giới tính: %s, Ngày sinh: %s, Ngày vào làm: %s, Chức vụ: %s\n",
                                nhanVien.getId(), nhanVien.getHoTen(), nhanVien.getSdt(), nhanVien.getEmail(),
                                nhanVien.getGioiTinh(), nhanVien.getNgaySinh(), nhanVien.getNgayVaoLam(), nhanVien.getChucVu());
                    }
                    break;
                case 5:
                    System.out.println("Tìm nhân viên theo:");
                    System.out.println("1. Mã nhân viên");
                    System.out.println("2. Tên nhân viên");
                    System.out.print("Chọn chức năng: ");
                    int subChoice = sc.nextInt();
                    sc.nextLine();  // Đọc newline còn lại

                    switch (subChoice) {
                        case 1:
                            System.out.print("Nhập mã nhân viên: ");
                            String maNhanVienTim = sc.nextLine();
                            List<NhanVien> nhanVienListById = nhanVienDAO.getById(maNhanVienTim);
                            if (!nhanVienListById.isEmpty()) {
                                for (NhanVien nhanVien : nhanVienListById) {
                                    System.out.printf("ID: %s, Họ tên: %s, SĐT: %s, Email: %s, Giới tính: %s, Ngày sinh: %s, Ngày vào làm: %s, Chức vụ: %s\n",
                                            nhanVien.getId(), nhanVien.getHoTen(), nhanVien.getSdt(), nhanVien.getEmail(),
                                            nhanVien.getGioiTinh(), nhanVien.getNgaySinh(), nhanVien.getNgayVaoLam(), nhanVien.getChucVu());
                                }
                            } else {
                                System.out.println("Không tìm thấy nhân viên với mã: " + maNhanVienTim);
                            }
                            break;
                        case 2:
                            System.out.print("Nhập tên nhân viên: ");
                            String tenNhanVienTim = sc.nextLine();
                            List<NhanVien> nhanVienByName;
                            try {
                                nhanVienByName = (List<NhanVien>) nhanVienDAO.getByName(tenNhanVienTim);
                                if (!nhanVienByName.isEmpty()) {
                                    for (NhanVien nhanVien : nhanVienByName) {
                                        System.out.printf("ID: %s, Họ tên: %s, SĐT: %s, Email: %s, Giới tính: %s, Ngày sinh: %s, Ngày vào làm: %s, Chức vụ: %s\n",
                                                nhanVien.getId(), nhanVien.getHoTen(), nhanVien.getSdt(), nhanVien.getEmail(),
                                                nhanVien.getGioiTinh(), nhanVien.getNgaySinh(), nhanVien.getNgayVaoLam(), nhanVien.getChucVu());
                                    }
                                } else {
                                    System.out.println("Không tìm thấy nhân viên với tên đã nhập.");
                                }
                            } catch (Exception e) {
                                System.out.println("Không tìm thấy nhân viên với tên đã nhập.");
                            }
                            break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ!!!");
                            break;
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }



    private static void quanLyThuoc(ThuocDAO thuocDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý thuốc =====");
            System.out.println("1. Thêm thuốc");
            System.out.println("2. Cập nhật thuốc");
            System.out.println("3. Xóa thuốc");
            System.out.println("4. Hiển thị tất cả thuốc");
            System.out.println("5. Tìm thuốc");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    Thuoc newThuoc = new Thuoc();
                    newThuoc.setId(generateMaThuoc(thuocDAO));
                    System.out.print("Nhập tên thuốc: ");
                    newThuoc.setTenThuoc(sc.nextLine());
                    System.out.print("Nhập giá thuốc: ");
                    newThuoc.setGiaThuoc(sc.nextDouble());
                    sc.nextLine();
                    System.out.print("Nhập ngày sản xuất (YYYY-MM-DD): ");
                    String ngaySanXuatStr = sc.nextLine();
                    while (ngaySanXuatStr.isEmpty()) {
                        System.out.print("Định dạng không đúng. Vui lòng nhập lại (YYYY-MM-DD): ");
                        ngaySanXuatStr = sc.nextLine();
                    }
                    newThuoc.setNgaySanXuat(LocalDate.parse(ngaySanXuatStr));

                    System.out.print("Nhập hạn sử dụng (YYYY-MM-DD): ");
                    String hanSuDungStr = sc.nextLine();
                    while (hanSuDungStr.isEmpty()) {
                        System.out.print("Định dạng không đúng. Vui lòng nhập lại (YYYY-MM-DD): ");
                        hanSuDungStr = sc.nextLine();
                    }
                    newThuoc.setHanSuDung(LocalDate.parse(hanSuDungStr));

                    System.out.print("Đường dẫn hình ảnh thuốc: ");
                    newThuoc.setHinhAnh(sc.nextLine());
                    System.out.println("Chọn đơn vị tính: (1) Viên, (2) Hộp, (3) Chai ");
                    int donViTinhChoice = sc.nextInt();
                    sc.nextLine();
                    switch (donViTinhChoice) {
                        case 1:
                            newThuoc.setDonViTinh(DonViTinh.VIEN);
                            break;
                        case 2:
                            newThuoc.setDonViTinh(DonViTinh.HOP);
                            break;
                        case 3:
                            newThuoc.setDonViTinh(DonViTinh.CHAI);
                            break;
                    }
                    thuocDAO.save(newThuoc);
                    System.out.println("Thuốc đã được thêm thành công!");
                    break;
                case 2:
                    System.out.print("Nhập mã thuốc cần cập nhật: ");
                    String maThuocChange = sc.nextLine();
                    Thuoc thuocChange = (Thuoc) thuocDAO.getById(maThuocChange);
                    if (thuocChange != null) {
                        System.out.print("Nhập giá bán mới: ");
                        thuocChange.setGiaThuoc(sc.nextDouble());
                        sc.nextLine();
                        thuocDAO.update(thuocChange);
                        System.out.println("Cập nhật thuốc thành công!");
                    } else {
                        System.out.println("Không tìm thấy thuốc với mã " + maThuocChange);
                    }
                    break;
                case 3:
                    System.out.print("Nhập mã thuốc cần xóa: ");
                    String maThuocDelete = sc.nextLine();
                    thuocDAO.delete(maThuocDelete);
                    System.out.println("Thuốc đã được xóa thành công!");
                    break;
                case 4:
                    List<Thuoc> allThuoc = thuocDAO.getAll();
                    System.out.println("Danh sách tất cả thuốc:");
                    for (Thuoc thuoc : allThuoc) {
                        System.out.printf("ID: %s, Tên thuốc: %s, Giá: %.2f, Ngày SX: %s, Hạn SD: %s\n",
                                thuoc.getId(), thuoc.getTenThuoc(), thuoc.getGiaThuoc(),
                                thuoc.getNgaySanXuat(), thuoc.getHanSuDung());
                    }
                    break;
                case 5:
                    System.out.println("Tìm thuốc theo:");
                    System.out.println("1. Mã thuốc");
                    System.out.println("2. Tên thuốc");
                    System.out.println("3. Khoảng giá");
                    System.out.print("Chọn chức năng: ");
                    int subChoice = sc.nextInt();
                    sc.nextLine();

                    switch (subChoice) {
                        case 1:
                            System.out.print("Nhập mã thuốc: ");
                            String maThuocTim = sc.nextLine();
                            Thuoc thuocTim = thuocDAO.getById(maThuocTim);
                            if (thuocTim != null) {
                                System.out.printf("ID: %s, Tên thuốc: %s, Giá: %.2f, Ngày SX: %s, Hạn SD: %s\n",
                                        thuocTim.getId(), thuocTim.getTenThuoc(), thuocTim.getGiaThuoc(),
                                        thuocTim.getNgaySanXuat(), thuocTim.getHanSuDung());
                            } else {
                                System.out.println("Không tìm thấy thuốc với mã: " + maThuocTim);
                            }
                            break;
                        case 2:
                            System.out.print("Nhập tên thuốc: ");
                            String tenThuocTim = sc.nextLine();
                            List<Thuoc> thuocByName = thuocDAO.getByName(tenThuocTim);
                            if (!thuocByName.isEmpty()) {
                                for (Thuoc thuoc : thuocByName) {
                                    System.out.printf("ID: %s, Tên thuốc: %s, Giá: %.2f, Ngày SX: %s, Hạn SD: %s\n",
                                            thuoc.getId(), thuoc.getTenThuoc(), thuoc.getGiaThuoc(),
                                            thuoc.getNgaySanXuat(), thuoc.getHanSuDung());
                                }
                            } else {
                                System.out.println("Không tìm thấy thuốc với tên đã nhập.");
                            }
                            break;
                        case 3:
                            System.out.print("Nhập khoảng giá bắt đầu: ");
                            double giaBatDau = sc.nextDouble();
                            System.out.print("Nhập khoảng giá kết thúc: ");
                            double giaKetThuc = sc.nextDouble();
                            sc.nextLine();
                            List<Thuoc> thuocByGia = thuocDAO.getByPriceRange(giaBatDau, giaKetThuc);
                            if (!thuocByGia.isEmpty()) {
                                for (Thuoc thuoc : thuocByGia) {
                                    System.out.printf("ID: %s, Tên thuốc: %s, Giá: %.2f, Ngày SX: %s, Hạn SD: %s\n",
                                            thuoc.getId(), thuoc.getTenThuoc(), thuoc.getGiaThuoc(),
                                            thuoc.getNgaySanXuat(), thuoc.getHanSuDung());
                                }
                            } else {
                                System.out.println("Không tìm thấy thuốc trong khoảng giá đã nhập.");
                            }
                            break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ!!!");
                            break;
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }

    private static void quanLyHoaDon(HoaDonDAO hdDao, ThuocDAO thuocDAO, KhachHangDAO khachHangDAO, NhanVienDAO nhanVienDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý hóa đơn =====");
            System.out.println("1. Thêm hóa đơn");
            System.out.println("2. Cập nhật hóa đơn");
            System.out.println("3. Xóa hóa đơn");
            System.out.println("4. Hiển thị tất cả hóa đơn");
            System.out.println("5. Tìm hóa đơn");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    themHoaDon(hdDao, thuocDAO, khachHangDAO, nhanVienDAO, sc);
                    break;
                case 2:
                    capNhatHoaDon(hdDao, thuocDAO, khachHangDAO, sc);
                    break;
                case 3:
                    xoaHoaDon(hdDao, sc);
                    break;
                case 4:
                    hienThiHoaDon(hdDao);
                    break;
                case 5:
                    timHoaDon(hdDao, sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }

    private static void themHoaDon(HoaDonDAO hoaDonDAO, ThuocDAO thuocDAO, KhachHangDAO khachHangDAO, NhanVienDAO nhanVienDAO, Scanner sc) {
        try {
            HoaDon newHoaDon = new HoaDon();
            String maHoaDon = generateMaHD(hoaDonDAO);
            newHoaDon.setId(maHoaDon);

            System.out.println("Mã hóa đơn tự tạo: " + maHoaDon);

            // Đặt ngày tạo hóa đơn là ngày và giờ hiện tại
            LocalDateTime now = LocalDateTime.now();
            newHoaDon.setNgayTaoHD(now);

            System.out.println("Ngày tạo hóa đơn tự động là: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            System.out.print("Nhập mã khách hàng: ");
            String maKH = sc.nextLine();
            newHoaDon.setKhachHang(khachHangDAO.getById(maKH));

            System.out.print("Nhập mã nhân viên: ");
            String maNV = sc.nextLine();
            newHoaDon.setNhanVien(nhanVienDAO.getByIdNV(maNV)); // Đảm bảo phương thức getById trong NhanVienDAO trả về NhanVien hoặc null

            List<ChiTietHoaDon> chiTietHoaDonList = new ArrayList<>();
            double tongTien = 0;
            while (true) {
                System.out.print("Nhập mã thuốc (hoặc 'done' để hoàn tất): ");
                String maThuoc = sc.nextLine();
                if (maThuoc.equalsIgnoreCase("done")) {
                    break;
                }
                Thuoc thuoc = thuocDAO.getById(maThuoc);

                if (thuoc == null) {
                    System.out.println("Không tìm thấy thuốc với mã: " + maThuoc);
                    continue;
                }

                System.out.print("Nhập số lượng: ");
                int soLuong = sc.nextInt();
                sc.nextLine();

                double donGia = thuoc.getGiaThuoc(); // Lấy đơn giá từ đối tượng Thuoc

                ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                chiTiet.setHoaDon(newHoaDon);
                chiTiet.setThuoc(thuoc); // Đảm bảo đối tượng Thuoc không bị null
                chiTiet.setSoLuong(soLuong);
                chiTiet.setDonGia(donGia);

                chiTietHoaDonList.add(chiTiet);
                tongTien += donGia * soLuong; // Tính tổng tiền
            }

            boolean success = hoaDonDAO.createHoaDon(newHoaDon, chiTietHoaDonList);
            if (success) {
                System.out.printf("Hóa đơn đã được thêm thành công! Tổng tiền: %.2f%n", tongTien);
            } else {
                System.out.println("Thêm hóa đơn thất bại.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi thêm hóa đơn. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void capNhatHoaDon(HoaDonDAO hoaDonDAO, ThuocDAO thuocDAO, KhachHangDAO khachHangDAO, Scanner sc) {
        try {
            System.out.print("Nhập mã hóa đơn cần cập nhật: ");
            String maHDUpdate = sc.nextLine();
            HoaDon hoaDonUpdate = hoaDonDAO.getById(maHDUpdate);

            if (hoaDonUpdate != null) {
                // Giữ nguyên ngày tạo hóa đơn
                System.out.println("Ngày tạo hóa đơn: " + hoaDonUpdate.getNgayTaoHD().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                System.out.print("Cập nhật mã khách hàng: ");
                String maKH = sc.nextLine();
                hoaDonUpdate.setKhachHang(khachHangDAO.getById(maKH));

                List<ChiTietHoaDon> chiTietHoaDonList = hoaDonUpdate.getChiTietHoaDonList() != null ? new ArrayList<>(hoaDonUpdate.getChiTietHoaDonList()) : new ArrayList<>();
                System.out.println("Cập nhật các chi tiết hóa đơn:");
                chiTietHoaDonList.clear(); // Xóa danh sách cũ để cập nhật lại các chi tiết hóa đơn

                double tongTien = 0;
                while (true) {
                    System.out.print("Nhập mã thuốc (hoặc 'done' để hoàn tất): ");
                    String maThuoc = sc.nextLine();
                    if (maThuoc.equalsIgnoreCase("done")) {
                        break;
                    }
                    Thuoc thuoc = thuocDAO.getById(maThuoc);

                    if (thuoc == null) {
                        System.out.println("Không tìm thấy thuốc với mã: " + maThuoc);
                        continue;
                    }

                    System.out.print("Nhập số lượng: ");
                    int soLuong = sc.nextInt();
                    sc.nextLine();

                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setHoaDon(hoaDonUpdate);
                    chiTiet.setThuoc(thuoc);
                    chiTiet.setSoLuong(soLuong);
                    chiTiet.setDonGia(thuoc.getGiaThuoc()); // Lấy đơn giá từ đối tượng Thuoc

                    chiTietHoaDonList.add(chiTiet);
                    tongTien += chiTiet.getDonGia() * chiTiet.getSoLuong(); // Tính tổng tiền
                }

                boolean success = hoaDonDAO.updateHoaDon(hoaDonUpdate, chiTietHoaDonList);
                if (success) {
                    System.out.printf("Cập nhật hóa đơn thành công! Tổng tiền: %.2f%n", tongTien);
                } else {
                    System.out.println("Cập nhật hóa đơn thất bại.");
                }
            } else {
                System.out.println("Không tìm thấy hóa đơn với mã đã nhập.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi cập nhật hóa đơn. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void xoaHoaDon(HoaDonDAO hoaDonDAO, Scanner sc) {
        try {
            System.out.print("Nhập mã hóa đơn cần xóa: ");
            String maHDDelete = sc.nextLine();

            HoaDon hoaDon = hoaDonDAO.getById(maHDDelete);
            if (hoaDon != null) {
                // Xóa tất cả các chi tiết hóa đơn liên quan trước
                List<ChiTietHoaDon> chiTietHoaDonList = hoaDon.getChiTietHoaDonList();
                if (chiTietHoaDonList != null) {
                    chiTietHoaDonList.clear();
                }

                boolean success = hoaDonDAO.deleteHoaDon(maHDDelete);
                if (success) {
                    System.out.println("Hóa đơn đã được xóa thành công!");
                } else {
                    System.out.println("Xóa hóa đơn thất bại.");
                }
            } else {
                System.out.println("Không tìm thấy hóa đơn với mã đã nhập.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi xóa hóa đơn. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void hienThiHoaDon(HoaDonDAO hoaDonDAO) {
        List<HoaDon> hoaDonList = hoaDonDAO.getAll();
        for (HoaDon hoaDon : hoaDonList) {
            System.out.printf("ID Hóa Đơn: %s, Ngày Tạo: %s, Khách Hàng: %s, Nhân Viên: %s%n",
                    hoaDon.getId(),
                    hoaDon.getNgayTaoHD().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getHoTen() : "N/A",
                    hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getHoTen() : "N/A");

            List<ChiTietHoaDon> chiTietList = hoaDon.getChiTietHoaDonList();
            if (chiTietList != null) {
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    System.out.printf("\tTên Thuốc: %s, Số Lượng: %d, Đơn Giá: %.2f%n",
                            chiTiet.getThuoc() != null ? chiTiet.getThuoc().getId() : "N/A",
                            chiTiet.getSoLuong(),
                            chiTiet.getDonGia());
                }
            }
        }
    }

    private static void timHoaDon(HoaDonDAO hoaDonDAO, Scanner sc) {
        System.out.print("Nhập mã hóa đơn cần tìm: ");
        String maHDSearch = sc.nextLine();
        HoaDon hoaDon = hoaDonDAO.getById(maHDSearch);

        if (hoaDon != null) {
            String khachHangInfo = (hoaDon.getKhachHang() != null) ? hoaDon.getKhachHang().getHoTen() : "N/A";
            String nhanVienInfo = (hoaDon.getNhanVien() != null) ? hoaDon.getNhanVien().getHoTen() : "N/A";

            System.out.printf("ID: %s, Ngày tạo: %s, Khách hàng: %s, Nhân viên: %s\n",
                    hoaDon.getId(),
                    hoaDon.getNgayTaoHD().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    khachHangInfo,
                    nhanVienInfo);

            List<ChiTietHoaDon> chiTietList = hoaDon.getChiTietHoaDonList();
            if (chiTietList != null) {
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    String thuocInfo = (chiTiet.getThuoc() != null) ? chiTiet.getThuoc().getTenThuoc() : "N/A";
                    System.out.printf("\tTên Thuốc: %s, Số Lượng: %d, Đơn Giá: %.2f\n",
                            thuocInfo,
                            chiTiet.getSoLuong(),
                            chiTiet.getDonGia());
                }
            } else {
                System.out.println("\tKhông có chi tiết hóa đơn nào.");
            }
        } else {
            System.out.println("Không tìm thấy hóa đơn với mã đã nhập.");
        }
    }

    private static void quanLyDoiTra(DoiTraDAO doiTraDAO, ThuocDAO thuocDAO, KhachHangDAO khachHangDAO, NhanVienDAO nhanVienDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý đổi trả =====");
            System.out.println("1. Thêm đổi trả");
            System.out.println("2. Cập nhật đổi trả");
            System.out.println("3. Xóa đổi trả");
            System.out.println("4. Hiển thị tất cả đổi trả");
            System.out.println("5. Tìm đổi trả");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    themDoiTra(doiTraDAO, thuocDAO, khachHangDAO, nhanVienDAO, sc);
                    break;
                case 2:
                    capNhatDoiTra(doiTraDAO, thuocDAO, khachHangDAO, sc);
                    break;
                case 3:
                    xoaDoiTra(doiTraDAO, sc);
                    break;
                case 4:
                    hienThiDoiTra(doiTraDAO);
                    break;
                case 5:
                    timDoiTra(doiTraDAO, sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }

    private static void themDoiTra(DoiTraDAO doiTraDAO, ThuocDAO thuocDAO, KhachHangDAO khachHangDAO, NhanVienDAO nhanVienDAO, Scanner sc) {
        try {
            DoiTra newDoiTra = new DoiTra();
            String maDoiTra = generateMaDoiTra(doiTraDAO);
            newDoiTra.setId(maDoiTra);

            System.out.println("Mã đổi trả tự tạo: " + maDoiTra);

            // Đặt ngày đổi trả là ngày và giờ hiện tại
            LocalDateTime now = LocalDateTime.now();
            newDoiTra.setNgayDoiTra(now);

            System.out.println("Ngày đổi trả tự động là: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            System.out.print("Nhập mã khách hàng: ");
            String maKH = sc.nextLine();
            newDoiTra.setKhachHang(khachHangDAO.getById(maKH));

            System.out.print("Nhập mã nhân viên: ");
            String maNV = sc.nextLine();
            newDoiTra.setNhanVien(nhanVienDAO.getByIdNV(maNV));

            newDoiTra.setTrangThai(TrangThai.CHUAHOANTHANH);

            List<ChiTietDoiTra> chiTietDoiTraList = new ArrayList<>();
            while (true) {
                System.out.print("Nhập mã thuốc (hoặc 'done' để hoàn tất): ");
                String maThuoc = sc.nextLine();
                if (maThuoc.equalsIgnoreCase("done")) {
                    break;
                }
                Thuoc thuoc = thuocDAO.getById(maThuoc);

                if (thuoc == null) {
                    System.out.println("Không tìm thấy thuốc với mã: " + maThuoc);
                    continue;
                }

                System.out.print("Nhập số lượng: ");
                int soLuong = sc.nextInt();
                sc.nextLine();

                double donGia = thuoc.getGiaThuoc();

                ChiTietDoiTra chiTiet = new ChiTietDoiTra();
                chiTiet.setDoiTra(newDoiTra);
                chiTiet.setThuoc(thuoc);
                chiTiet.setSoLuong(soLuong);
                chiTiet.setDonGia(donGia);

                chiTietDoiTraList.add(chiTiet);
            }

            boolean success = doiTraDAO.create(newDoiTra, chiTietDoiTraList);
            if (success) {
                System.out.println("Đổi trả đã được thêm thành công!");
            } else {
                System.out.println("Thêm đổi trả thất bại.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi thêm đổi trả. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void capNhatDoiTra(DoiTraDAO doiTraDAO, ThuocDAO thuocDAO, KhachHangDAO khachHangDAO, Scanner sc) {
        try {
            System.out.print("Nhập mã đổi trả cần cập nhật: ");
            String maDoiTraUpdate = sc.nextLine();
            DoiTra doiTraUpdate = doiTraDAO.getById(maDoiTraUpdate);

            if (doiTraUpdate != null) {
                System.out.println("Ngày đổi trả: " + doiTraUpdate.getNgayDoiTra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                System.out.print("Cập nhật mã khách hàng: ");
                String maKH = sc.nextLine();
                doiTraUpdate.setKhachHang(khachHangDAO.getById(maKH));

                List<ChiTietDoiTra> chiTietDoiTraList = doiTraUpdate.getChiTietDoiTraList() != null ? new ArrayList<>(doiTraUpdate.getChiTietDoiTraList()) : new ArrayList<>();
                System.out.println("Cập nhật các chi tiết đổi trả:");
                chiTietDoiTraList.clear();

                while (true) {
                    System.out.print("Nhập mã thuốc (hoặc 'done' để hoàn tất): ");
                    String maThuoc = sc.nextLine();
                    if (maThuoc.equalsIgnoreCase("done")) {
                        break;
                    }
                    Thuoc thuoc = thuocDAO.getById(maThuoc);

                    if (thuoc == null) {
                        System.out.println("Không tìm thấy thuốc với mã: " + maThuoc);
                        continue;
                    }

                    System.out.print("Nhập số lượng: ");
                    int soLuong = sc.nextInt();
                    sc.nextLine();

                    ChiTietDoiTra chiTiet = new ChiTietDoiTra();
                    chiTiet.setDoiTra(doiTraUpdate);
                    chiTiet.setThuoc(thuoc);
                    chiTiet.setSoLuong(soLuong);
                    chiTiet.setDonGia(thuoc.getGiaThuoc());

                    chiTietDoiTraList.add(chiTiet);
                }

                doiTraUpdate.setTrangThai(TrangThai.CHUAHOANTHANH);

                boolean success = doiTraDAO.update(doiTraUpdate, chiTietDoiTraList);
                if (success) {
                    System.out.println("Cập nhật đổi trả thành công!");
                } else {
                    System.out.println("Cập nhật đổi trả thất bại.");
                }
            } else {
                System.out.println("Không tìm thấy đổi trả với mã đã nhập.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi cập nhật đổi trả. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void xoaDoiTra(DoiTraDAO doiTraDAO, Scanner sc) {
        try {
            System.out.print("Nhập mã đổi trả cần xóa: ");
            String maDoiTraDelete = sc.nextLine();

            DoiTra doiTra = doiTraDAO.getById(maDoiTraDelete);
            if (doiTra != null) {
                boolean success = doiTraDAO.delete(maDoiTraDelete);
                if (success) {
                    System.out.println("Đổi trả đã được xóa thành công!");
                } else {
                    System.out.println("Xóa đổi trả thất bại.");
                }
            } else {
                System.out.println("Không tìm thấy đổi trả với mã đã nhập.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi xóa đổi trả. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }
//    private static void hienThiDoiTra(DoiTraDAO doiTraDAO) {
//        List<DoiTra> allDoiTra = doiTraDAO.getAll();
//        System.out.println("Danh sách tất cả đổi trả:");
//        for (DoiTra doiTra : allDoiTra) {
//            System.out.printf("Mã: %s, Ngày đổi trả: %s, Trạng thái: %s\n",
//                    doiTra.getId(), doiTra.getNgayDoiTra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), doiTra.getTrangThai());
//        }
//    }
private static void hienThiDoiTra(DoiTraDAO doiTraDAO) {
    List<DoiTra> allDoiTra = doiTraDAO.getAll();
    System.out.println("Danh sách tất cả đổi trả:");
    for (DoiTra doiTra : allDoiTra) {
        System.out.printf("Mã: %s, Ngày đổi trả: %s, Trạng thái: %s, Khách hàng: %s, Nhân viên: %s\n",
                doiTra.getId(), doiTra.getNgayDoiTra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                doiTra.getTrangThai() != null ? doiTra.getTrangThai().name() : "Không xác định",
                doiTra.getKhachHang() != null ? doiTra.getKhachHang().getHoTen() : "Không xác định",
                doiTra.getNhanVien() != null ? doiTra.getNhanVien().getHoTen() : "Không xác định");
    }
}

    private static void timDoiTra(DoiTraDAO doiTraDAO, Scanner sc) {
        System.out.print("Nhập mã đổi trả cần tìm: ");
        String maDoiTraTim = sc.nextLine();
        DoiTra doiTra = doiTraDAO.getByMaDoiTra(maDoiTraTim);

        if (doiTra != null) {
            System.out.printf("Mã: %s, Ngày đổi trả: %s, Trạng thái: %s\n",
                    doiTra.getId(), doiTra.getNgayDoiTra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), doiTra.getTrangThai());

            System.out.println("Chi tiết đổi trả:");
            for (ChiTietDoiTra chiTiet : doiTra.getChiTietDoiTraList()) {
                System.out.printf("Thuốc: %s, Số lượng: %d, Đơn giá: %.2f\n",
                        chiTiet.getThuoc().getTenThuoc(), chiTiet.getSoLuong(), chiTiet.getDonGia());
            }
        } else {
            System.out.println("Không tìm thấy đổi trả với mã: " + maDoiTraTim);
        }
    }

    private static void quanLyPhieuNhap(PhieuNhapDAO phieuNhapDAO, ThuocDAO thuocDAO, NhaCungCapDAO nhaCungCapDAO, NhanVienDAO nhanVienDAO, Scanner sc) {
        while (true) {
            System.out.println("\n===== Quản lý phiếu nhập =====");
            System.out.println("1. Thêm phiếu nhập");
            System.out.println("2. Cập nhật phiếu nhập");
            System.out.println("3. Xóa phiếu nhập");
            System.out.println("4. Hiển thị tất cả phiếu nhập");
            System.out.println("5. Tìm phiếu nhập");
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    themPhieuNhap(phieuNhapDAO, thuocDAO, nhaCungCapDAO, nhanVienDAO, sc);
                    break;
                case 2:
                    capNhatPhieuNhap(phieuNhapDAO, thuocDAO, nhaCungCapDAO, sc);
                    break;
                case 3:
                    xoaPhieuNhap(phieuNhapDAO, sc);
                    break;
                case 4:
                    hienThiPhieuNhap(phieuNhapDAO);
                    break;
                case 5:
                    timPhieuNhap(phieuNhapDAO, sc);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }


    private static void themPhieuNhap(PhieuNhapDAO phieuNhapDAO, ThuocDAO thuocDAO, NhaCungCapDAO nhaCungCapDAO, NhanVienDAO nhanVienDAO, Scanner sc) {
        try {
            PhieuNhap newPhieuNhap = new PhieuNhap();
            String maPhieuNhap = generateMaPhieuNhap(phieuNhapDAO);
            newPhieuNhap.setId(maPhieuNhap);

            System.out.println("Mã phiếu nhập tự tạo: " + maPhieuNhap);

            // Đặt thời gian nhập là ngày và giờ hiện tại
            LocalDateTime now = LocalDateTime.now();
            newPhieuNhap.setNgayTaoPN(now);

            System.out.println("Thời gian nhập tự động là: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            System.out.print("Nhập mã nhà cung cấp: ");
            String maNCC = sc.nextLine();
            newPhieuNhap.setNhaCungCap(nhaCungCapDAO.getByMaNhaCungCap(maNCC));

            System.out.print("Nhập mã nhân viên: ");
            String maNV = sc.nextLine();
            newPhieuNhap.setNhanVien(nhanVienDAO.getByIdNV(maNV));

            List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
            while (true) {
                System.out.print("Nhập mã thuốc (hoặc 'done' để hoàn tất): ");
                String maThuoc = sc.nextLine();
                if (maThuoc.equalsIgnoreCase("done")) {
                    break;
                }
                Thuoc thuoc = thuocDAO.getById(maThuoc);

                if (thuoc == null) {
                    System.out.println("Không tìm thấy thuốc với mã: " + maThuoc);
                    continue;
                }

                System.out.print("Nhập số lượng: ");
                int soLuong = sc.nextInt();
                sc.nextLine();

                System.out.print("Nhập đơn giá: ");
                double donGia = sc.nextDouble();
                sc.nextLine();

                ChiTietPhieuNhap chiTiet = new ChiTietPhieuNhap();
                chiTiet.setPhieuNhap(newPhieuNhap);
                chiTiet.setThuoc(thuoc);
                chiTiet.setSoLuong(soLuong);
                chiTiet.setDonGia(donGia);

                chiTietPhieuNhapList.add(chiTiet);
            }

            boolean success = phieuNhapDAO.createPhieuNhap(newPhieuNhap, chiTietPhieuNhapList);
            if (success) {
                System.out.println("Phiếu nhập đã được thêm thành công!");
            } else {
                System.out.println("Thêm phiếu nhập thất bại.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi thêm phiếu nhập. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void capNhatPhieuNhap(PhieuNhapDAO phieuNhapDAO, ThuocDAO thuocDAO, NhaCungCapDAO nhaCungCapDAO, Scanner sc) {
        try {
            System.out.print("Nhập mã phiếu nhập cần cập nhật: ");
            String maPhieuNhapUpdate = sc.nextLine();
            PhieuNhap phieuNhapUpdate = phieuNhapDAO.getById(maPhieuNhapUpdate);

            if (phieuNhapUpdate != null) {
                System.out.println("Thời gian nhập: " + phieuNhapUpdate.getNgayTaoPN().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                System.out.print("Cập nhật mã nhà cung cấp: ");
                String maNCC = sc.nextLine();
                phieuNhapUpdate.setNhaCungCap(nhaCungCapDAO.getByMaNhaCungCap(maNCC));

                List<ChiTietPhieuNhap> chiTietPhieuNhapList = phieuNhapUpdate.getChiTietPhieuNhapList() != null ? new ArrayList<>(phieuNhapUpdate.getChiTietPhieuNhapList()) : new ArrayList<>();
                chiTietPhieuNhapList.clear();

                while (true) {
                    System.out.print("Nhập mã thuốc (hoặc 'done' để hoàn tất): ");
                    String maThuoc = sc.nextLine();
                    if (maThuoc.equalsIgnoreCase("done")) {
                        break;
                    }
                    Thuoc thuoc = thuocDAO.getById(maThuoc);

                    if (thuoc == null) {
                        System.out.println("Không tìm thấy thuốc với mã: " + maThuoc);
                        continue;
                    }

                    System.out.print("Nhập số lượng: ");
                    int soLuong = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Nhập đơn giá: ");
                    double donGia = sc.nextDouble();
                    sc.nextLine();

                    ChiTietPhieuNhap chiTiet = new ChiTietPhieuNhap();
                    chiTiet.setPhieuNhap(phieuNhapUpdate);
                    chiTiet.setThuoc(thuoc);
                    chiTiet.setSoLuong(soLuong);
                    chiTiet.setDonGia(donGia);

                    chiTietPhieuNhapList.add(chiTiet);
                }

                boolean success = phieuNhapDAO.updatePhieuNhap(phieuNhapUpdate, chiTietPhieuNhapList);
                if (success) {
                    System.out.println("Cập nhật phiếu nhập thành công!");
                } else {
                    System.out.println("Cập nhật phiếu nhập thất bại.");
                }
            } else {
                System.out.println("Không tìm thấy phiếu nhập với mã đã nhập.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi cập nhật phiếu nhập. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void xoaPhieuNhap(PhieuNhapDAO phieuNhapDAO, Scanner sc) {
        try {
            System.out.print("Nhập mã phiếu nhập cần xóa: ");
            String maPhieuNhapDelete = sc.nextLine();

            PhieuNhap phieuNhap = phieuNhapDAO.getById(maPhieuNhapDelete);
            if (phieuNhap != null) {
                boolean success = phieuNhapDAO.deletePhieuNhap(maPhieuNhapDelete);
                if (success) {
                    System.out.println("Phiếu nhập đã được xóa thành công!");
                } else {
                    System.out.println("Xóa phiếu nhập thất bại.");
                }
            } else {
                System.out.println("Không tìm thấy phiếu nhập với mã đã nhập.");
            }
        } catch (Exception e) {
            System.out.println("Có lỗi xảy ra khi xóa phiếu nhập. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private static void hienThiPhieuNhap(PhieuNhapDAO phieuNhapDAO) {
        List<PhieuNhap> allPhieuNhap = phieuNhapDAO.getAll();
        System.out.println("Danh sách tất cả phiếu nhập:");
        for (PhieuNhap phieuNhap : allPhieuNhap) {
            System.out.printf("Mã: %s, Thời gian nhập: %s, Nhà cung cấp: %s, Nhân viên: %s\n",
                    phieuNhap.getId(), phieuNhap.getNgayTaoPN().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    phieuNhap.getNhaCungCap().getTenNCC(), phieuNhap.getNhanVien().getHoTen());

            System.out.println("Danh sách thuốc:");
            for (ChiTietPhieuNhap chiTiet : phieuNhap.getChiTietPhieuNhapList()) {
                System.out.printf("Tên thuốc: %s, Số lượng: %d, Giá tiền nhập: %f\n",
                        chiTiet.getThuoc().getTenThuoc(), chiTiet.getSoLuong(), chiTiet.getDonGia());
            }
        }
    }


    private static void timPhieuNhap(PhieuNhapDAO phieuNhapDAO, Scanner sc) {
        System.out.print("Nhập mã phiếu nhập cần tìm: ");
        String maPhieuNhapTim = sc.nextLine();
        PhieuNhap phieuNhap = phieuNhapDAO.getById(maPhieuNhapTim);

        if (phieuNhap != null) {
            System.out.printf("Mã: %s, Thời gian nhập: %s, Nhà cung cấp: %s, Nhân viên: %s\n",
                    phieuNhap.getId(), phieuNhap.getNgayTaoPN().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    phieuNhap.getNhaCungCap().getTenNCC(), phieuNhap.getNhanVien().getHoTen());

            System.out.println("Chi tiết phiếu nhập:");
            for (ChiTietPhieuNhap chiTiet : phieuNhap.getChiTietPhieuNhapList()) {
                System.out.printf("Thuốc: %s, Số lượng: %d, Đơn giá: %.2f\n",
                        chiTiet.getThuoc().getTenThuoc(), chiTiet.getSoLuong(), chiTiet.getDonGia());
            }
        } else {
            System.out.println("Không tìm thấy phiếu nhập với mã: " + maPhieuNhapTim);
        }
    }

}
