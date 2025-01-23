package dao;

import entities.ChiTietHoaDon;
import entities.DoiTra;
import entities.ChiTietDoiTra;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DoiTraDAO {
    private EntityManager em;

    public boolean create(DoiTra doiTra, List<ChiTietDoiTra> chiTietDoiTraList) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(doiTra);
            for (ChiTietDoiTra chiTiet : chiTietDoiTraList) {
                chiTiet.setDoiTra(doiTra); // Set DoiTra cho từng ChiTietDoiTra
                em.persist(chiTiet);
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

    public boolean update(DoiTra doiTra, List<ChiTietDoiTra> chiTietDoiTraList) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(doiTra);
            for (ChiTietDoiTra chiTiet : chiTietDoiTraList) {
                em.merge(chiTiet);
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

    public boolean delete(String maDoiTra) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            DoiTra doiTra = em.find(DoiTra.class, maDoiTra);
            if (doiTra != null) {
                for (ChiTietDoiTra chiTiet : doiTra.getChiTietDoiTraList()) {
                    em.remove(chiTiet);
                }
                em.remove(doiTra);
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

    public List<DoiTra> getAll() {
        String query = "SELECT d FROM DoiTra d";
        return em.createQuery(query, DoiTra.class).getResultList();
    }

    public DoiTra getById(String maDoiTra) {
        return em.find(DoiTra.class, maDoiTra);
    }

    public DoiTra getByMaDoiTra(String maDoiTra) {
        String query = "SELECT d FROM DoiTra d WHERE d.id = :maDoiTra";
        List<DoiTra> result = em.createQuery(query, DoiTra.class)
                .setParameter("maDoiTra", maDoiTra)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
