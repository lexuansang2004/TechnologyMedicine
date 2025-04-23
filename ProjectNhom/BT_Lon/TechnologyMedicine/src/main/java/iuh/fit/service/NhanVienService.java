package iuh.fit.service;

import iuh.fit.dao.NhanVienDAO;
import iuh.fit.entity.NhanVien;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NhanVienService {

    private final NhanVienDAO nhanVienDAO;

    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    public List<NhanVien> findAll() {
        return nhanVienDAO.findAll();
    }

    public Optional<NhanVien> findById(String id) {
        return nhanVienDAO.findById(id);
    }

    public List<NhanVien> findByName(String name) {
        return nhanVienDAO.findByName(name);
    }

    public boolean save(NhanVien nhanVien) {
        // Nếu không có ngày vào làm, đặt là ngày hiện tại
        if (nhanVien.getNgayVaoLam() == null) {
            nhanVien.setNgayVaoLam(LocalDate.now());
        }

        return nhanVienDAO.save(nhanVien);
    }

    public boolean update(NhanVien nhanVien) {
        return nhanVienDAO.update(nhanVien);
    }

    public boolean delete(String id) {
        return nhanVienDAO.delete(id);
    }

    public List<Map<String, Object>> getAllNhanVienWithAccountStatus() {
        return nhanVienDAO.getAllNhanVienWithAccountStatus();
    }

    public String generateNewId() {
        List<NhanVien> nhanVienList = findAll();
        int maxId = 0;

        for (NhanVien nhanVien : nhanVienList) {
            String idStr = nhanVien.getIdNV().substring(2);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("NV%03d", maxId + 1);
    }
}