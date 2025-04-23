package iuh.fit.util;

import iuh.fit.config.EmailConfig;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.OTP;

import java.time.LocalDateTime;
import java.util.Random;

public class OTPUtil {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    /**
     * Tạo mã OTP ngẫu nhiên
     *
     * @return Mã OTP
     */
    public static String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    /**
     * Tạo đối tượng OTP mới
     *
     * @param idOTP ID của OTP
     * @param idKH ID của khách hàng
     * @return Đối tượng OTP
     */
    public static OTP createOTP(String idOTP, String idKH) {
        String maOTP = generateOTP();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(OTP_EXPIRY_MINUTES);

        OTP otp = new OTP();
        otp.setIdOTP(idOTP);
        otp.setIdKH(idKH);
        otp.setMaOTP(maOTP);
        otp.setThoiGianTao(now);
        otp.setThoiGianHetHan(expiry);
        otp.setTrangThai("Chưa sử dụng");

        return otp;
    }

    /**
     * Gửi mã OTP qua email
     *
     * @param khachHang Khách hàng
     * @param otp Mã OTP
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendOTPByEmail(KhachHang khachHang, OTP otp) {
        String subject = "Mã xác thực OTP - Hệ thống Quản lý Thuốc";
        String content = "<html><body>" +
                "<h2>Xin chào " + khachHang.getHoTen() + ",</h2>" +
                "<p>Mã xác thực OTP của bạn là: <strong>" + otp.getMaOTP() + "</strong></p>" +
                "<p>Mã này có hiệu lực trong vòng " + OTP_EXPIRY_MINUTES + " phút.</p>" +
                "<p>Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                "<p>Trân trọng,<br>Hệ thống Quản lý Thuốc</p>" +
                "</body></html>";

        return EmailConfig.getInstance().sendEmail(khachHang.getEmail(), subject, content);
    }

    /**
     * Gửi mã OTP qua SMS (giả lập)
     *
     * @param khachHang Khách hàng
     * @param otp Mã OTP
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendOTPBySMS(KhachHang khachHang, OTP otp) {
        // Giả lập gửi SMS, trong thực tế sẽ sử dụng dịch vụ SMS Gateway
        System.out.println("Gửi SMS đến " + khachHang.getSdt() + ": Mã OTP của bạn là " + otp.getMaOTP());
        return true;
    }
}