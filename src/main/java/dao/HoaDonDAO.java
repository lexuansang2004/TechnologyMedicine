package dao;

import entities.HoaDon;
import entities.ChiTietHoaDon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class HoaDonDAO {
    private EntityManager em;

    public boolean createHoaDon(HoaDon hoaDon, List<ChiTietHoaDon> chiTietHoaDonList) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(hoaDon);
            for (ChiTietHoaDon chiTiet : chiTietHoaDonList) {
                chiTiet.setHoaDon(hoaDon); // Set HoaDon cho từng ChiTietHoaDon
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

    public boolean updateHoaDon(HoaDon hoaDon, List<ChiTietHoaDon> chiTietHoaDonList) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(hoaDon);
            for (ChiTietHoaDon chiTiet : chiTietHoaDonList) {
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

    public boolean deleteHoaDon(String maHoaDon) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            HoaDon hoaDon = em.find(HoaDon.class, maHoaDon);
            if (hoaDon != null) {
                for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDonList()) {
                    em.remove(chiTiet);
                }
                em.remove(hoaDon);
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

    public List<HoaDon> getAll() {
        String query = "SELECT t FROM HoaDon t";
        return em.createQuery(query, HoaDon.class).getResultList();
    }

    public HoaDon getById(String maHoaDon) {
        return em.find(HoaDon.class, maHoaDon);
    }
}
