package iuh.fit;

import dao.ThuocDAO;
import entities.DonViTinh;
import entities.Thuoc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EntityManager em = Persistence.createEntityManagerFactory("mariadb").createEntityManager();
        ThuocDAO thuocDAO = new ThuocDAO(em);
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Options =====");
            System.out.println("1. Thêm thuốc");
            System.out.println("2. Cập nhật thuốc");
            System.out.println("3. Xóa thuốc");
            System.out.println("4. Hiển thị tất cả thuốc");
            System.out.println("5. Tìm thuốc");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice){
                case 1:
                    Thuoc newThuoc = new Thuoc();
                    System.out.print("Nhập tên thuốc: ");
                    newThuoc.setTenThuoc(sc.nextLine());
                    System.out.print("Nhập giá thuốc: ");
                    newThuoc.setGiaThuoc(sc.nextDouble());
                    System.out.print("Nhập ngày sản xuất (YYYY/MM/DD: ");
                    newThuoc.setNgaySanXuat(LocalDate.parse(sc.nextLine()));
                    System.out.print("Nhập hạn sử dụng (YYYY/MM/DD: ");
                    newThuoc.setHanSuDung(LocalDate.parse(sc.nextLine()));
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
                    break;
                case 2:
                    System.out.print("Nhập mã thuốc cần cập nhật: ");
                    String maThuocChange = sc.nextLine();
                    Thuoc thuocChange = (Thuoc) thuocDAO.getById(maThuocChange);
                    if (thuocChange != null){
                        System.out.print("Nhập giá bán mới: ");
                        thuocChange.setGiaThuoc(sc.nextDouble());
                        sc.nextLine();
                        thuocDAO.update(thuocChange);
                    } else {
                        System.out.println("Không tìm thấy thuốc với mã " + maThuocChange);
                    }
                    break;
                case 3:
                    System.out.print("Nhập mã thuốc cần xóa: ");
                    String maThuocDelete = sc.nextLine();
                    thuocDAO.delete(maThuocDelete);
                    break;
                case 0:
                    System.out.println("Good bye");
                    em.close();
                    sc.close();
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!!!");
                    break;
            }
        }
    }
}