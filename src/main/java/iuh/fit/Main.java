package iuh.fit;

import dao.ThuocDAO;
import dao.TaiKhoanDAO;
import dao.KhachHangDAO;
import dao.NhanVienDAO;
import dao.HoaDonDAO;
import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

    public static void main(String[] args) throws UnsupportedEncodingException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        System.setOut(out);
        Faker faker = new Faker();
        EntityManager em = Persistence.createEntityManagerFactory("mariadb").createEntityManager();
        ThuocDAO thuocDAO = new ThuocDAO(em);
        TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO(em);
//        KhachHangDAO khachHangDAO = new KhachHangDAO(em);
        NhanVienDAO nhanVienDAO = new NhanVienDAO(em);
//        HoaDonDAO hoaDonDAO = new HoaDonDAO(em);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1. Quản lý tài khoản");
            System.out.println("2. Quản lý khách hàng");
            System.out.println("3. Quản lý nhân viên");
            System.out.println("4. Quản lý thuốc");
            System.out.println("5. Quản lý hóa đơn");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            int mainChoice = sc.nextInt();
            sc.nextLine();

            switch (mainChoice) {
                case 1:
                    quanLyTaiKhoan(taiKhoanDAO, sc);
                    break;
                case 2:
//                    quanLyKhachHang(khachHangDAO, sc);
                    break;
                case 3:
                    quanLyNhanVien(nhanVienDAO, sc);
                    break;
                case 4:
                    quanLyThuoc(thuocDAO, sc);
                    break;
                case 5:
//                    quanLyHoaDon(hoaDonDAO, sc);
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
            System.out.println("0. Trở lại");
            System.out.print("Chọn chức năng: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    // Thêm khách hàng
                    break;
                case 2:
                    // Cập nhật khách hàng
                    break;
                case 3:
                    // Xóa khách hàng
                    break;
                case 4:
                    // Hiển thị tất cả khách hàng
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
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
}
