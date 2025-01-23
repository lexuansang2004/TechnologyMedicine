package dao;

import entities.NhaCungCap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class NhaCungCapDAO {
    private EntityManager em;

    public boolean createNhaCungCap(NhaCungCap nhaCungCap) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(nhaCungCap);
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

    public boolean updateNhaCungCap(NhaCungCap nhaCungCap) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(nhaCungCap);
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

    public boolean deleteNhaCungCap(Long id) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            NhaCungCap nhaCungCap = em.find(NhaCungCap.class, id);
            if (nhaCungCap != null) {
                em.remove(nhaCungCap);
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

    public List<NhaCungCap> getAll() {
        String query = "SELECT n FROM NhaCungCap n";
        return em.createQuery(query, NhaCungCap.class).getResultList();
    }

    public NhaCungCap getById(Long id) {
        return em.find(NhaCungCap.class, id);
    }

    public NhaCungCap getByMaNhaCungCap(String maNhaCungCap) {
        String query = "SELECT n FROM NhaCungCap n WHERE n.maNhaCungCap = :maNhaCungCap";
        List<NhaCungCap> result = em.createQuery(query, NhaCungCap.class)
                .setParameter("maNhaCungCap", maNhaCungCap)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
}
