package iuh.fit.service;

import iuh.fit.dao.ThuocDAO;
import iuh.fit.entity.Thuoc;

import java.util.List;
import java.util.Optional;

public class ThuocService {

    private final ThuocDAO thuocDAO;

    public ThuocService() {
        this.thuocDAO = new ThuocDAO();
    }

    // Lấy tất cả thuốc
    public List<Thuoc> findAll() {
        return thuocDAO.findAll();
    }

    // Lấy thuốc theo ID
    public Optional<Thuoc> findById(String id) {
        return thuocDAO.findById(id);
    }

    // Tìm kiếm thuốc theo tên
    public List<Thuoc> findByName(String name) {
        return thuocDAO.findByName(name);
    }

    // Lưu thuốc mới
    public boolean save(Thuoc thuoc) {
        return thuocDAO.save(thuoc);
    }

    // Cập nhật thuốc
    public boolean update(Thuoc thuoc) {
        return thuocDAO.update(thuoc);
    }

    // Xóa thuốc
    public boolean delete(String id) {
        return thuocDAO.delete(id);
    }

    // Cập nhật số lượng thuốc
    public boolean updateStock(String id, int quantity) {
        return thuocDAO.updateStock(id, quantity);
    }

    public String generateNewId() {
        List<Thuoc> thuocList = findAll();
        int maxId = 0;

        for (Thuoc thuoc : thuocList) {
            String idStr = thuoc.getIdThuoc().substring(1);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("T%03d", maxId + 1);
    }
}