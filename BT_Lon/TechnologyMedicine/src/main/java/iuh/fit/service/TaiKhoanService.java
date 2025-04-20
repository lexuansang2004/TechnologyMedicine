package iuh.fit.service;

import iuh.fit.dao.TaiKhoanDAO;
import iuh.fit.entity.TaiKhoan;

import java.util.List;
import java.util.Optional;

public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public List<TaiKhoan> findAll() {
        return taiKhoanDAO.findAll();
    }

    public Optional<TaiKhoan> findById(String id) {
        return taiKhoanDAO.findById(id);
    }

    public Optional<TaiKhoan> findByUsername(String username) {
        return taiKhoanDAO.findByUsername(username);
    }

    public List<TaiKhoan> findByNhanVien(String idNV) {
        return taiKhoanDAO.findByNhanVien(idNV);
    }

    public boolean save(TaiKhoan taiKhoan) {
        return taiKhoanDAO.save(taiKhoan);
    }

    public boolean update(TaiKhoan taiKhoan) {
        return taiKhoanDAO.update(taiKhoan);
    }

    public boolean delete(String id) {
        return taiKhoanDAO.delete(id);
    }

    public String generateNewId() {
        List<TaiKhoan> taiKhoanList = findAll();
        int maxId = 0;

        for (TaiKhoan taiKhoan : taiKhoanList) {
            String idStr = taiKhoan.getIdTK().substring(2);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("TK%03d", maxId + 1);
    }

    public boolean authenticate(String username, String password) {
        Optional<TaiKhoan> taiKhoanOpt = findByUsername(username);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            // Kiểm tra mật khẩu đã băm với mật khẩu người dùng nhập vào
            return taiKhoan.checkPassword(password);
        }
        return false;
    }
}