//package dao;
//
//import entities.NhanVien;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityTransaction;
//import lombok.AllArgsConstructor;
//
//import java.util.List;
//
//@AllArgsConstructor
//public class NhanVienDAO {
//    private EntityManager em;
//    public boolean save(NhanVien nhanVien) {
//        EntityTransaction tr = em.getTransaction();
//        try {
//            tr.begin();
//            em.persist(nhanVien);
//            tr.commit();
//            return true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            if (tr.isActive()) {
//                tr.rollback();
//            }
//        }
//        return false;
//    }
//
//    public boolean update(NhanVien nhanVien) {
//        EntityTransaction tr = em.getTransaction();
//        try {
//            tr.begin();
//            em.merge(nhanVien);
//            tr.commit();
//            return true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            if (tr.isActive()) {
//                tr.rollback();
//            }
//        }
//        return false;
//    }
//    public boolean delete(String nhanVien) {
//        EntityTransaction tr = em.getTransaction();
//        try {
//            tr.begin();
//            em.remove(em.merge(nhanVien));
//            tr.commit();
//            return true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            if (tr.isActive()) {
//                tr.rollback();
//            }
//        }
//        return false;
//    }
//
//    public List<NhanVien> getAll() {
//        String query = "SELECT t FROM NhanVien t";
//        return em.createQuery(query, NhanVien.class).getResultList();
//    }
//    public List<NhanVien> getById(String maNV) {
//        String query = "SELECT n FROM NhanVien n WHERE n.id = :maNV";
//        return em.createQuery(query, NhanVien.class)
//                .setParameter("maNV", maNV)
//                .getResultList();
//    }
//
//
//    public List<NhanVien> getByName(String tenNV) {
//        String query = "SELECT t FROM NhanVien t WHERE t.hoTen LIKE :tenNV";
//        return em.createQuery(query, NhanVien.class)
//                .setParameter("tenNV", "%" + tenNV + "%")
//                .getResultList();
//    }
//
//    public NhanVien getByIdNV(String maNV) {
//        String query = "SELECT n FROM NhanVien n WHERE n.id = :maNV";
//        return em.createQuery(query, NhanVien.class)
//                .setParameter("maNV", maNV)
//                .getResultStream()
//                .findFirst()
//                .orElse(null); // Trả về null nếu không tìm thấy
//    }
//
//}


package dao;

import entities.NhanVien;
import entities.Thuoc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class NhanVienDAO {
    private EntityManager em;
    public boolean save(NhanVien nhanVien) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(nhanVien);
            tr.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tr.isActive()) {
                tr.rollback();
            }
        }
        return false;
    }

    public boolean update(NhanVien nhanVien) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(nhanVien);
            tr.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tr.isActive()) {
                tr.rollback();
            }
        }
        return false;
    }
    public boolean delete(String maNhanVien) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            // Tìm nhân viên theo mã
            NhanVien nhanVien = em.find(NhanVien.class, maNhanVien);
            if (nhanVien != null) {
                em.remove(nhanVien); // Xóa nhân viên nếu tồn tại
            } else {
                System.out.println("Nhân viên không tồn tại với mã: " + maNhanVien);
                tr.rollback();
                return false;
            }
            tr.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tr.isActive()) {
                tr.rollback();
            }
        }
        return false;
    }


    public List<NhanVien> getAll() {
        String query = "SELECT t FROM NhanVien t";
        return em.createQuery(query, NhanVien.class).getResultList();
    }
    public List<NhanVien> getById(String maNV) {
        String query = "SELECT n FROM NhanVien n WHERE n.id = :maNV";
        return em.createQuery(query, NhanVien.class)
                .setParameter("maNV", maNV)
                .getResultList();
    }


    public List<NhanVien> getByName(String tenNV) {
        String query = "SELECT t FROM NhanVien t WHERE t.hoTen LIKE :tenNV";
        return em.createQuery(query, NhanVien.class)
                .setParameter("tenNV", "%" + tenNV + "%")
                .getResultList();
    }

    public NhanVien getNhanVienById(String maNV) {
        return em.find(NhanVien.class, maNV);
    }

    public NhanVien getNhanVienByTen(String tenNV) {
        String query = "SELECT n FROM NhanVien n WHERE n.hoTen = :tenNV";
        return em.createQuery(query, NhanVien.class)
                .setParameter("tenNV", tenNV)
                .getSingleResult();
    }

    public NhanVien getByIdNV(String maNV) {
        String query = "SELECT n FROM NhanVien n WHERE n.id = :maNV";
        return em.createQuery(query, NhanVien.class)
                .setParameter("maNV", maNV)
                .getResultStream()
                .findFirst()
                .orElse(null); // Trả về null nếu không tìm thấy
    }

    public String generateMaNV() {
        String prefix = "NV";
        String query = "SELECT n.id FROM NhanVien n";
        List<String> ids = em.createQuery(query, String.class).getResultList();

        int maxNumber = ids.stream()
                .map(id -> Integer.parseInt(id.substring(2))) // Lấy phần số từ mã
                .max(Integer::compareTo) // Tìm giá trị lớn nhất
                .orElse(0); // Nếu danh sách rỗng, trả về 0

        // Tăng số lớn nhất thêm 1
        int nextNumber = maxNumber + 1;

        // Tạo mã mới theo định dạng NV%06d
        return String.format(prefix + "%06d", nextNumber);
    }

    public static void main(String[] args) {
        // hiển thị danh sách nhân viên
        EntityManager em = Persistence.createEntityManagerFactory("mariadb").createEntityManager();
        NhanVienDAO nhanVienDAO = new NhanVienDAO(em);
        List<NhanVien> nhanViens = nhanVienDAO.getAll();
        for (NhanVien nhanVien : nhanViens) {
            System.out.printf("ID: %s, Họ tên: %s, SĐT: %s, Email: %s, Giới tính: %s, Ngày sinh: %s, Ngày vào làm: %s, Chức vụ: %s\n",
                    nhanVien.getId(), nhanVien.getHoTen(), nhanVien.getSdt(), nhanVien.getEmail(),
                    nhanVien.getGioiTinh(), nhanVien.getNgaySinh(), nhanVien.getNgayVaoLam(), nhanVien.getChucVu());
        }
        // Xóa nhân viên
//        if (nhanViens.size() > 0) {
//            boolean result = nhanVienDAO.delete("NV001");
//            if (result) {
//                System.out.println("Xóa nhân viên thành công");
//            } else {
//                System.out.println("Xóa nhân viên thất bại");
//            }
//        }
        // Tìm nhân viên theo mã
        NhanVien nhanVien = nhanVienDAO.getNhanVienById("NV000015");
        System.out.printf(String.valueOf(nhanVien));

        // Cập nhật nhân viên
        if ( nhanVien != null) {
            nhanVien.setHoTen("Trần Văn Hazz");
            nhanVien.setSdt("0123456789");
            nhanVien.setEmail("hazz@gmail.com");
            boolean result = nhanVienDAO.update(nhanVien);
            if (result) {
                System.out.println("Cập nhật nhân viên thành công");
            } else {
                System.out.println("Cập nhật nhân viên thất bại");
            }
        }else {
            System.out.println("Nhân viên không tồn tại");
        }
    }

}
