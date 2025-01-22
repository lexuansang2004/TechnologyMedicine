package dao;

import entities.Thuoc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ThuocDAO {
    private EntityManager em;

    public boolean save(Thuoc thuoc) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(thuoc);
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
    public boolean update(Thuoc thuoc) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(thuoc);
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
    public boolean delete(String maThuoc) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Thuoc thuoc = em.find(Thuoc.class, maThuoc);
            if (thuoc != null) {
                em.remove(thuoc);
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

    public List<Thuoc> getAll() {
        String query = "SELECT t FROM Thuoc t";
        return em.createQuery(query, Thuoc.class).getResultList();
    }

    public Thuoc getById(String id) {
        return em.find(Thuoc.class, id);
    }

    public List<Thuoc> getByName(String tenThuoc) {
        if (tenThuoc == null || tenThuoc.trim().isEmpty()) {
            return List.of();
        }
        String query = "SELECT t FROM Thuoc t WHERE t.tenThuoc LIKE :tenThuoc";
        return em.createQuery(query, Thuoc.class)
                .setParameter("tenThuoc", "%" + tenThuoc + "%")
                .getResultList();
    }
    public List<Thuoc> getByPriceRange(double min, double max) {
        if (min < 0 || max < 0 || min > max) {
            throw new IllegalArgumentException("Phạm vi giá không hợp lệ.");
        }
        String query = "SELECT t FROM Thuoc t WHERE t.giaThuoc BETWEEN :min AND :max";
        return em.createQuery(query, Thuoc.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }
}
