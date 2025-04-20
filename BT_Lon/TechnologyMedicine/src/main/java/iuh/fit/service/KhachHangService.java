package iuh.fit.service;

import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.OTPDAO;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.OTP;
import iuh.fit.util.OTPUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class KhachHangService {

    private final KhachHangDAO khachHangDAO;
    private final OTPDAO otpDAO;

    public KhachHangService() {
        this.khachHangDAO = new KhachHangDAO();
        this.otpDAO = new OTPDAO();
    }

    public List<KhachHang> findAll() {
        return khachHangDAO.findAll();
    }

    public Optional<KhachHang> findById(String id) {
        return khachHangDAO.findById(id);
    }

    public Optional<KhachHang> findByPhone(String phone) {
        return khachHangDAO.findByPhone(phone);
    }

    public Optional<KhachHang> findByEmail(String email) {
        return khachHangDAO.findByEmail(email);
    }

    public List<KhachHang> findByKeyword(String keyword) {
        return khachHangDAO.findByKeyword(keyword);
    }

    public boolean save(KhachHang khachHang) {
        // Nếu không có ngày tham gia, đặt là ngày hiện tại
        if (khachHang.getNgayThamGia() == null) {
            khachHang.setNgayThamGia(LocalDate.now());
        }

        // Nếu không có hạng mục, đặt là "Bạc"
        if (khachHang.getHangMuc() == null || khachHang.getHangMuc().isEmpty()) {
            khachHang.setHangMuc("Bạc");
        }

        // Nếu không có tổng chi tiêu, đặt là 0
        if (khachHang.getTongChiTieu() == null) {
            khachHang.setTongChiTieu(0.0);
        }

        return khachHangDAO.save(khachHang);
    }

    public boolean update(KhachHang khachHang) {
        return khachHangDAO.update(khachHang);
    }

    public boolean updateSpending(String id, double amount) {
        return khachHangDAO.updateSpending(id, amount);
    }

    public boolean delete(String id) {
        return khachHangDAO.delete(id);
    }

    public String generateNewId() {
        List<KhachHang> khachHangList = findAll();
        int maxId = 0;

        for (KhachHang khachHang : khachHangList) {
            String idStr = khachHang.getIdKH().substring(2);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("KH%03d", maxId + 1);
    }

    public OTP generateOTP(String idKH) {
        Optional<KhachHang> khachHangOpt = findById(idKH);
        if (khachHangOpt.isEmpty()) {
            return null;
        }

        KhachHang khachHang = khachHangOpt.get();

        // Tạo ID cho OTP
        String idOTP = "OTP" + System.currentTimeMillis();

        // Tạo OTP
        OTP otp = OTPUtil.createOTP(idOTP, idKH);

        // Lưu OTP vào database
        if (otpDAO.save(otp)) {
            // Gửi OTP qua email hoặc SMS
            if (khachHang.getEmail() != null && !khachHang.getEmail().isEmpty()) {
                OTPUtil.sendOTPByEmail(khachHang, otp);
            } else if (khachHang.getSdt() != null && !khachHang.getSdt().isEmpty()) {
                OTPUtil.sendOTPBySMS(khachHang, otp);
            }

            return otp;
        }

        return null;
    }

    public boolean verifyOTP(String idKH, String maOTP) {
        Optional<OTP> otpOpt = otpDAO.findValidOTP(idKH, maOTP);
        if (otpOpt.isPresent()) {
            // Đánh dấu OTP đã sử dụng
            otpDAO.markAsUsed(otpOpt.get().getIdOTP());
            return true;
        }

        return false;
    }
}