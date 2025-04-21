package iuh.fit.service;

import iuh.fit.dao.DonViTinhDAO;
import iuh.fit.entity.DonViTinh;

import java.util.List;
import java.util.Optional;

public class DonViTinhService {

    private final DonViTinhDAO donViTinhDAO;

    public DonViTinhService() {
        this.donViTinhDAO = new DonViTinhDAO();
    }

    public List<DonViTinh> findAll() {
        return donViTinhDAO.findAll();
    }

    public Optional<DonViTinh> findById(String id) {
        return donViTinhDAO.findById(id);
    }

    public Optional<DonViTinh> findByName(String name) {
        return donViTinhDAO.findByName(name);
    }

    public boolean save(DonViTinh dvt) {
        return donViTinhDAO.save(dvt);
    }

    public boolean update(DonViTinh dvt) {
        return donViTinhDAO.update(dvt);
    }

    public boolean delete(String id) {
        return donViTinhDAO.delete(id);
    }

    public String generateNewId() {
        List<DonViTinh> list = findAll();
        int maxId = 0;
        for (DonViTinh dvt : list) {
            String idStr = dvt.getIdDVT().replaceAll("\\D+", "");
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("DVT%03d", maxId + 1);
    }
}
