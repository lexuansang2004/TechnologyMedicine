package dao;

import entities.PhieuNhap;
import entities.ChiTietPhieuNhap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PhieuNhapDAO {
    private EntityManager em;

    public boolean createPhieuNhap(PhieuNhap phieuNhap, List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(phieuNhap);
            for (ChiTietPhieuNhap chiTiet : chiTietPhieuNhapList) {
                chiTiet.setPhieuNhap(phieuNhap); // Set PhieuNhap cho từng ChiTietPhieuNhap
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

    public boolean updatePhieuNhap(PhieuNhap phieuNhap, List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(phieuNhap);
            for (ChiTietPhieuNhap chiTiet : chiTietPhieuNhapList) {
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

    public boolean deletePhieuNhap(String maPhieuNhap) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            PhieuNhap phieuNhap = em.find(PhieuNhap.class, maPhieuNhap);
            if (phieuNhap != null) {
                for (ChiTietPhieuNhap chiTiet : phieuNhap.getChiTietPhieuNhapList()) {
                    em.remove(chiTiet);
                }
                em.remove(phieuNhap);
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

    public List<PhieuNhap> getAll() {
        String query = "SELECT p FROM PhieuNhap p";
        return em.createQuery(query, PhieuNhap.class).getResultList();
    }

    public PhieuNhap getById(String maPhieuNhap) {
        return em.find(PhieuNhap.class, maPhieuNhap);
    }
}
