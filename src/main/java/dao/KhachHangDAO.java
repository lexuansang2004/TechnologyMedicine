package dao;

import entities.KhachHang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class KhachHangDAO {
    private EntityManager em;

    public boolean save(KhachHang khachHang) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(khachHang);
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

    public boolean update(KhachHang khachHang) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(khachHang);
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

    public boolean delete(String id) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            KhachHang khachHang = em.find(KhachHang.class, id);
            if (khachHang != null) {
                em.remove(khachHang);
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

    public List<KhachHang> getAll() {
        String query = "SELECT kh FROM KhachHang kh";
        return em.createQuery(query, KhachHang.class).getResultList();
    }

    public KhachHang getById(String id) {
        return em.find(KhachHang.class, id);
    }

    public List<KhachHang> searchByNameOrPhone(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        String query = "SELECT kh FROM KhachHang kh WHERE kh.hoTen LIKE :keyword OR kh.sdt LIKE :keyword";
        return em.createQuery(query, KhachHang.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }
}