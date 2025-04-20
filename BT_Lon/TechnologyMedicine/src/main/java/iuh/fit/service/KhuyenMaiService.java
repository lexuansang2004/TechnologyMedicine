package iuh.fit.service;

import iuh.fit.dao.KhuyenMaiDAO;
import iuh.fit.entity.KhuyenMai;

import java.util.List;
import java.util.Optional;

public class KhuyenMaiService {

    private final KhuyenMaiDAO khuyenMaiDAO;

    public KhuyenMaiService() {
        this.khuyenMaiDAO = new KhuyenMaiDAO();
    }

    public List<KhuyenMai> findAll() {
        return khuyenMaiDAO.findAll();
    }

    public Optional<KhuyenMai> findById(String id) {
        return khuyenMaiDAO.findById(id);
    }

    public List<KhuyenMai> findByHangMuc(String hangMuc) {
        return khuyenMaiDAO.findByHangMuc(hangMuc);
    }

    public List<KhuyenMai> findByThuoc(String idThuoc) {
        return khuyenMaiDAO.findByThuoc(idThuoc);
    }

    public boolean saveForHangMuc(KhuyenMai khuyenMai) {
        // Nếu không có trạng thái, đặt là "Đang áp dụng"
        if (khuyenMai.getTrangThai() == null || khuyenMai.getTrangThai().isEmpty()) {
            khuyenMai.setTrangThai("Đang áp dụng");
        }

        return khuyenMaiDAO.saveForHangMuc(khuyenMai);
    }

    public boolean saveForThuoc(KhuyenMai khuyenMai) {
        // Nếu không có trạng thái, đặt là "Đang áp dụng"
        if (khuyenMai.getTrangThai() == null || khuyenMai.getTrangThai().isEmpty()) {
            khuyenMai.setTrangThai("Đang áp dụng");
        }

        return khuyenMaiDAO.saveForThuoc(khuyenMai);
    }

    public boolean update(KhuyenMai khuyenMai) {
        return khuyenMaiDAO.update(khuyenMai);
    }

    public boolean delete(String id) {
        return khuyenMaiDAO.delete(id);
    }

    public String generateNewId() {
        List<KhuyenMai> khuyenMaiList = findAll();
        int maxId = 0;

        for (KhuyenMai khuyenMai : khuyenMaiList) {
            String idStr = khuyenMai.getIdKM().substring(2);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("KM%03d", maxId + 1);
    }
}