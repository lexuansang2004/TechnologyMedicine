package iuh.fit.service;

import iuh.fit.dao.ChiTietHoaDonDAO;
import iuh.fit.dao.HoaDonDAO;
import iuh.fit.entity.HoaDon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoaDonService {
    private static final Logger LOGGER = Logger.getLogger(HoaDonService.class.getName());

    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;

    public HoaDonService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    }

    public List<Map<String, Object>> findAll() {
        try {
            List<Map<String, Object>> result = hoaDonDAO.findAll();
            System.out.println("HoaDonDAO.findAll() returned: " + result);
            System.out.println("Number of records: " + (result != null ? result.size() : "null"));
            return result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in HoaDonService.findAll()", e);
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng thay vì null
        }
    }

    public Optional<Map<String, Object>> findById(String idHD) {
        return hoaDonDAO.findById(idHD);
    }

    public List<Map<String, Object>> findByKhachHang(String idKH) {
        return hoaDonDAO.findByKhachHang(idKH);
    }

    public List<Map<String, Object>> findByNhanVien(String idNV) {
        return hoaDonDAO.findByNhanVien(idNV);
    }

    public boolean save(Map<String, Object> hoaDonData) {
        try {
            // Đảm bảo ngày lập được thiết lập
            if (!hoaDonData.containsKey("ngayLap")) {
                hoaDonData.put("ngayLap", LocalDateTime.now());
            }

            return hoaDonDAO.save(hoaDonData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving HoaDon", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> hoaDonData) {
        try {
            return hoaDonDAO.update(hoaDonData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating HoaDon", e);
            return false;
        }
    }

    public boolean delete(String idHD) {
        try {
            return hoaDonDAO.delete(idHD);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting HoaDon", e);
            return false;
        }
    }

    public List<Map<String, Object>> getChiTietHoaDon(String idHD) {
        return chiTietHoaDonDAO.findByHoaDon(idHD);
    }

    public boolean addChiTietHoaDon(String idHD, Map<String, Object> chiTietData) {
        try {
            return chiTietHoaDonDAO.add(idHD, chiTietData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding ChiTietHoaDon", e);
            return false;
        }
    }

    public boolean updateChiTietHoaDon(Map<String, Object> chiTietData) {
        try {
            return chiTietHoaDonDAO.update(chiTietData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating ChiTietHoaDon", e);
            return false;
        }
    }

    public boolean deleteChiTietHoaDon(String idHD, String idThuoc) {
        try {
            return chiTietHoaDonDAO.delete(idHD, idThuoc);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting ChiTietHoaDon", e);
            return false;
        }
    }

    public boolean updateTongTien(String idHD) {
        try {
            // Lấy tất cả chi tiết hóa đơn
            List<Map<String, Object>> chiTietList = chiTietHoaDonDAO.findByHoaDon(idHD);

            // Tính tổng tiền
            double tongTien = 0;
            for (Map<String, Object> chiTiet : chiTietList) {
                int soLuong = Integer.parseInt(chiTiet.get("soLuong").toString());
                double donGia = Double.parseDouble(chiTiet.get("donGia").toString());
                tongTien += soLuong * donGia;
            }

            // Lấy thông tin hóa đơn hiện tại
            Optional<Map<String, Object>> hoaDonOpt = hoaDonDAO.findById(idHD);
            if (hoaDonOpt.isPresent()) {
                Map<String, Object> hoaDonData = hoaDonOpt.get();
                hoaDonData.put("tongTien", tongTien);

                // Cập nhật hóa đơn
                return hoaDonDAO.update(hoaDonData);
            }

            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating TongTien for HoaDon", e);
            return false;
        }
    }
}