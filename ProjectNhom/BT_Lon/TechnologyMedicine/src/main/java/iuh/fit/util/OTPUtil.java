package iuh.fit.util;

import iuh.fit.config.EmailConfig;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.OTP;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OTPUtil {

    private static final Logger LOGGER = Logger.getLogger(OTPUtil.class.getName());
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    /**
     * Tạo mã OTP ngẫu nhiên
     * @return Mã OTP dạng chuỗi
     */
    public static String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        String otpString = otp.toString();
        LOGGER.info("Đã tạo OTP mới: " + otpString);
        return otpString;
    }

    /**
     * Tạo đối tượng OTP
     * @param idOTP ID của OTP
     * @param idUser ID của người dùng (có thể là khách hàng hoặc nhân viên)
     * @return Đối tượng OTP
     */
    public static OTP createOTP(String idOTP, String idUser) {
        String maOTP = generateOTP();
        LOGGER.info("Tạo đối tượng OTP với mã: " + maOTP);

        OTP otp = new OTP();
        otp.setIdOTP(idOTP);
        otp.setIdKH(idUser); // Sử dụng idKH để lưu ID người dùng (có thể là khách hàng hoặc nhân viên)
        otp.setMaOTP(maOTP);
        otp.setThoiGianTao(LocalDateTime.now());
        otp.setThoiGianHetHan(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setTrangThai("Chưa sử dụng");

        return otp;
    }

    /**
     * Gửi OTP qua email cho khách hàng
     * @param khachHang Thông tin khách hàng
     * @param otp Đối tượng OTP
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendOTPByEmail(KhachHang khachHang, OTP otp) {
        try {
            String to = khachHang.getEmail();

            // Kiểm tra email
            if (to == null || to.trim().isEmpty()) {
                LOGGER.warning("Email của khách hàng trống hoặc null");
                return false;
            }

            LOGGER.info("Chuẩn bị gửi OTP " + otp.getMaOTP() + " đến email: " + to);

            String subject = "Mã xác thực OTP";
            String content = "<html><body>"
                    + "<h2>Xin chào " + khachHang.getHoTen() + ",</h2>"
                    + "<p>Mã xác thực OTP của bạn là: <strong>" + otp.getMaOTP() + "</strong></p>"
                    + "<p>Mã này sẽ hết hạn sau " + OTP_EXPIRY_MINUTES + " phút.</p>"
                    + "<p>Vui lòng không chia sẻ mã này với bất kỳ ai.</p>"
                    + "<p>Trân trọng,<br>Nhà thuốc TechnologyMedicine</p>"
                    + "</body></html>";

            try {
                boolean result = EmailConfig.getInstance().sendEmail(to, subject, content);
                if (result) {
                    LOGGER.info("Đã gửi OTP thành công đến email: " + to);
                } else {
                    LOGGER.warning("Không thể gửi OTP đến email: " + to);
                }
                return result;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi gửi email", e);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi OTP qua email", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gửi OTP qua email cho nhân viên
     * @param nhanVien Thông tin nhân viên
     * @param otp Đối tượng OTP
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendOTPByEmail(NhanVien nhanVien, OTP otp) {
        try {
            String to = nhanVien.getEmail();

            // Kiểm tra email
            if (to == null || to.trim().isEmpty()) {
                LOGGER.warning("Email của nhân viên trống hoặc null");
                return false;
            }

            LOGGER.info("Chuẩn bị gửi OTP " + otp.getMaOTP() + " đến email: " + to);

            String subject = "Mã xác thực OTP";
            String content = "<html><body>"
                    + "<h2>Xin chào " + nhanVien.getHoTen() + ",</h2>"
                    + "<p>Mã xác thực OTP của bạn là: <strong>" + otp.getMaOTP() + "</strong></p>"
                    + "<p>Mã này sẽ hết hạn sau " + OTP_EXPIRY_MINUTES + " phút.</p>"
                    + "<p>Vui lòng không chia sẻ mã này với bất kỳ ai.</p>"
                    + "<p>Trân trọng,<br>Nhà thuốc TechnologyMedicine</p>"
                    + "</body></html>";

            try {
                boolean result = EmailConfig.getInstance().sendEmail(to, subject, content);
                if (result) {
                    LOGGER.info("Đã gửi OTP thành công đến email: " + to);
                } else {
                    LOGGER.warning("Không thể gửi OTP đến email: " + to);
                }
                return result;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi gửi email", e);
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi OTP qua email", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gửi OTP qua SMS cho khách hàng
     * @param khachHang Thông tin khách hàng
     * @param otp Đối tượng OTP
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendOTPBySMS(KhachHang khachHang, OTP otp) {
        try {
            String phone = khachHang.getSdt();

            // Kiểm tra số điện thoại
            if (phone == null || phone.trim().isEmpty()) {
                LOGGER.warning("Số điện thoại của khách hàng trống hoặc null");
                return false;
            }

            // Giả lập việc gửi SMS
            LOGGER.info("GIẢ LẬP: Gửi OTP " + otp.getMaOTP() + " đến số điện thoại " + phone);

            // Trong môi trường thực tế, bạn sẽ tích hợp với dịch vụ gửi SMS
            // Ví dụ: Twilio, Nexmo, ...

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi OTP qua SMS", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gửi OTP qua SMS cho nhân viên
     * @param nhanVien Thông tin nhân viên
     * @param otp Đối tượng OTP
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendOTPBySMS(NhanVien nhanVien, OTP otp) {
        try {
            String phone = nhanVien.getSdt();

            // Kiểm tra số điện thoại
            if (phone == null || phone.trim().isEmpty()) {
                LOGGER.warning("Số điện thoại của nhân viên trống hoặc null");
                return false;
            }

            // Giả lập việc gửi SMS
            LOGGER.info("GIẢ LẬP: Gửi OTP " + otp.getMaOTP() + " đến số điện thoại " + phone);

            // Trong môi trường thực tế, bạn sẽ tích hợp với dịch vụ gửi SMS
            // Ví dụ: Twilio, Nexmo, ...

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi OTP qua SMS", e);
            e.printStackTrace();
            return false;
        }
    }
}