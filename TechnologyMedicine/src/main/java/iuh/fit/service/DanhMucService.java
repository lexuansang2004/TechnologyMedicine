package iuh.fit.service;

import iuh.fit.dao.DanhMucDAO;
import iuh.fit.entity.DanhMuc;

import java.util.List;
import java.util.Optional;

public class DanhMucService {

    private final DanhMucDAO danhMucDAO;

    public DanhMucService() {
        this.danhMucDAO = new DanhMucDAO();
    }

    public List<DanhMuc> findAll() {
        return danhMucDAO.findAll();
    }

    public Optional<DanhMuc> findById(String id) {
        return danhMucDAO.findById(id);
    }

    public Optional<DanhMuc> findByName(String name) {
        return danhMucDAO.findByName(name);
    }

    public boolean save(DanhMuc danhMuc) {
        return danhMucDAO.save(danhMuc);
    }

    public boolean update(DanhMuc danhMuc) {
        return danhMucDAO.update(danhMuc);
    }

    public boolean delete(String id) {
        return danhMucDAO.delete(id);
    }

    public String generateNewId() {
        List<DanhMuc> danhMucList = findAll();
        int maxId = 0;
        for (DanhMuc dm : danhMucList) {
            String idStr = dm.getIdDM().replaceAll("\\D+", ""); // Lấy số
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("DM%03d", maxId + 1);
    }
}
