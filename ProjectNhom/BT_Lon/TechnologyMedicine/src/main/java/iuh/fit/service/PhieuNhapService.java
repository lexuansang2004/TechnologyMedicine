package iuh.fit.service;

import iuh.fit.dao.ChiTietPhieuNhapDAO;
import iuh.fit.dao.PhieuNhapDAO;
import iuh.fit.entity.PhieuNhap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhieuNhapService {
    private static final Logger LOGGER = Logger.getLogger(PhieuNhapService.class.getName());

    private final PhieuNhapDAO phieuNhapDAO;
    private final ChiTietPhieuNhapDAO chiTietPhieuNhapDAO;

    public PhieuNhapService() {
        this.phieuNhapDAO = new PhieuNhapDAO();
        this.chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
    }

    public List<Map<String, Object>> findAll() {
        try {
            List<Map<String, Object>> result = phieuNhapDAO.findAll();
            System.out.println("PhieuNhapDAO.findAll() returned: " + result);
            System.out.println("Number of records: " + (result != null ? result.size() : "null"));
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in PhieuNhapService.findAll()", e);
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng thay vì null
        }
    }

    public Optional<Map<String, Object>> findById(String idPN) {
        return phieuNhapDAO.findById(idPN);
    }

    public List<Map<String, Object>> findByNhaCungCap(String idNCC) {
        return phieuNhapDAO.findByNhaCungCap(idNCC);
    }

    public List<Map<String, Object>> findByNhanVien(String idNV) {
        return phieuNhapDAO.findByNhanVien(idNV);
    }

    public boolean save(Map<String, Object> phieuNhapData) {
        try {
            // Đảm bảo thời gian được thiết lập
            if (!phieuNhapData.containsKey("thoiGian")) {
                phieuNhapData.put("thoiGian", LocalDateTime.now());
            }

            return phieuNhapDAO.save(phieuNhapData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving PhieuNhap", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> phieuNhapData) {
        try {
            return phieuNhapDAO.update(phieuNhapData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating PhieuNhap", e);
            return false;
        }
    }

    public boolean delete(String idPN) {
        try {
            return phieuNhapDAO.delete(idPN);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting PhieuNhap", e);
            return false;
        }
    }

    public List<Map<String, Object>> getChiTietPhieuNhap(String idPN) {
        return chiTietPhieuNhapDAO.findByPhieuNhap(idPN);
    }

    public boolean addChiTietPhieuNhap(String idPN, Map<String, Object> chiTietData) {
        try {
            return chiTietPhieuNhapDAO.add(idPN, chiTietData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding ChiTietPhieuNhap", e);
            return false;
        }
    }

    public boolean updateChiTietPhieuNhap(Map<String, Object> chiTietData) {
        try {
            return chiTietPhieuNhapDAO.update(chiTietData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating ChiTietPhieuNhap", e);
            return false;
        }
    }

    public boolean deleteChiTietPhieuNhap(String idPN, String idThuoc) {
        try {
            return chiTietPhieuNhapDAO.delete(idPN, idThuoc);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting ChiTietPhieuNhap", e);
            return false;
        }
    }

    public boolean updateTongTien(String idPN) {
        try {
            // Lấy tất cả chi tiết phiếu nhập
            List<Map<String, Object>> chiTietList = chiTietPhieuNhapDAO.findByPhieuNhap(idPN);

            // Tính tổng tiền
            double tongTien = 0;
            for (Map<String, Object> chiTiet : chiTietList) {
                int soLuong = Integer.parseInt(chiTiet.get("soLuong").toString());
                double donGia = Double.parseDouble(chiTiet.get("donGia").toString());
                tongTien += soLuong * donGia;
            }

            // Lấy thông tin phiếu nhập hiện tại
            Optional<Map<String, Object>> phieuNhapOpt = phieuNhapDAO.findById(idPN);
            if (phieuNhapOpt.isPresent()) {
                Map<String, Object> phieuNhapData = phieuNhapOpt.get();
                phieuNhapData.put("tongTien", tongTien);

                // Cập nhật phiếu nhập
                return phieuNhapDAO.update(phieuNhapData);
            }

            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating TongTien for PhieuNhap", e);
            return false;
        }
    }
}