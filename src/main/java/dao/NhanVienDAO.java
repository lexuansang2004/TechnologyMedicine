package dao;

import entities.NhanVien;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
    public boolean delete(String nhanVien) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.merge(nhanVien));
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
}
