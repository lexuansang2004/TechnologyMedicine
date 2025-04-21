package iuh.fit.service;

import iuh.fit.dao.XuatXuDAO;
import iuh.fit.entity.XuatXu;

import java.util.List;
import java.util.Optional;

public class XuatXuService {

    private final XuatXuDAO xuatXuDAO;

    public XuatXuService() {
        this.xuatXuDAO = new XuatXuDAO();
    }

    public List<XuatXu> findAll() {
        return xuatXuDAO.findAll();
    }

    public Optional<XuatXu> findById(String id) {
        return xuatXuDAO.findById(id);
    }

    public Optional<XuatXu> findByName(String name) {
        return xuatXuDAO.findByName(name);
    }

    public boolean save(XuatXu xx) {
        return xuatXuDAO.save(xx);
    }

    public boolean update(XuatXu xx) {
        return xuatXuDAO.update(xx);
    }

    public boolean delete(String id) {
        return xuatXuDAO.delete(id);
    }

    public String generateNewId() {
        List<XuatXu> list = findAll();
        int maxId = 0;
        for (XuatXu xx : list) {
            String idStr = xx.getIdXX().replaceAll("\\D+", "");
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) maxId = id;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("XX%03d", maxId + 1);
    }
}
