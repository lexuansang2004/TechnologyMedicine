package dao;

import entities.TaiKhoan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TaiKhoanDAO {
    private EntityManager em;
    public boolean save(TaiKhoan taiKhoan) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(taiKhoan);
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
    public boolean update(TaiKhoan taiKhoan) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(taiKhoan);
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
    public boolean delete(TaiKhoan taiKhoan) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(em.merge(taiKhoan));
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
    public List<TaiKhoan> getAll() {
        String query = "SELECT t FROM TaiKhoan t";
        return em.createQuery(query, TaiKhoan.class).getResultList();
    }

    public List<TaiKhoan> getById(String maNV) {
        String query = "SELECT t FROM TaiKhoan t WHERE t.id = :maNV";
        return em.createQuery(query, TaiKhoan.class).setParameter("maNV", maNV).getResultList();
    }
}
