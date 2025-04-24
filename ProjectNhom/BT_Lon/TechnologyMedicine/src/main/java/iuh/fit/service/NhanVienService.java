package iuh.fit.service;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.dao.NhanVienDAO;
import iuh.fit.dao.OTPDAO;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.OTP;
import iuh.fit.util.OTPUtil;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NhanVienService {

    private static final Logger LOGGER = Logger.getLogger(NhanVienService.class.getName());
    private final NhanVienDAO nhanVienDAO;
    private final OTPDAO otpDAO;

    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
        this.otpDAO = new OTPDAO();
    }

    public List<NhanVien> findAll() {
        return nhanVienDAO.findAll();
    }

    public Optional<NhanVien> findById(String id) {
        return nhanVienDAO.findById(id);
    }

    public Optional<NhanVien> findByEmail(String email) {
        return nhanVienDAO.findByEmail(email);
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

    // Phương thức tạo OTP cho nhân viên
    public int generateOTP(String idNV, String method) {
        try {
            Optional<NhanVien> nhanVienOpt = findById(idNV);
            if (nhanVienOpt.isEmpty()) {
                LOGGER.warning("Không tìm thấy nhân viên với ID: " + idNV);
                return -1;
            }

            NhanVien nhanVien = nhanVienOpt.get();

            // Kiểm tra email
            if ("email".equals(method) && (nhanVien.getEmail() == null || nhanVien.getEmail().isEmpty())) {
                LOGGER.warning("Nhân viên không có email: " + idNV);
                return -1;
            }

            // Tạo ID OTP mới
            String idOTP = "OTP" + UUID.randomUUID().toString().substring(0, 8);

            // Tạo đối tượng OTP
            OTP otp = OTPUtil.createOTP(idOTP, idNV);

            // Lưu OTP vào cơ sở dữ liệu
            boolean saved = otpDAO.save(otp);
            if (!saved) {
                LOGGER.warning("Không thể lưu OTP cho nhân viên: " + idNV);
                return -1;
            }

            // Gửi OTP qua email hoặc SMS
            boolean sent = false;
            if ("email".equals(method)) {
                sent = OTPUtil.sendOTPByEmail(nhanVien, otp);
            } else if ("sms".equals(method)) {
                sent = OTPUtil.sendOTPBySMS(nhanVien, otp);
            }

            if (!sent) {
                LOGGER.warning("Không thể gửi OTP cho nhân viên: " + idNV);
                return -1;
            }

            return Integer.parseInt(otp.getMaOTP());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tạo OTP cho nhân viên", e);
            return -1;
        }
    }

    // Chỉ hiển thị phương thức verifyOTP() đã sửa đổi
    public boolean verifyOTP(String idNV, String otp) {
        try {
            LOGGER.info("Xác thực OTP: " + otp + " cho khách hàng ID: " + idNV);

            // In ra thông tin OTP để debug
            LOGGER.info("OTP cần xác thực: '" + otp + "', độ dài: " + otp.length());

            // Kiểm tra trực tiếp trong cơ sở dữ liệu
            try (Session session = Neo4jConfig.getInstance().getSession()) {
                String query = "MATCH (otp:OTP {idNV: $idNV, maOTP: $maOTP}) " +
                        "WHERE otp.trangThai = 'Chưa sử dụng' " +
                        "RETURN otp";

                Result result = session.run(query, Map.of("idNV", idNV, "maOTP", otp));

                if (result.hasNext()) {
                    org.neo4j.driver.Record record = result.next();
                    Value otpValue = record.get("otp");

                    String maOTP = otpValue.get("maOTP").asString();
                    String trangThai = otpValue.get("trangThai").asString();
                    String idOTP = otpValue.get("idOTP").asString();

                    LOGGER.info("Tìm thấy OTP trong cơ sở dữ liệu: " + maOTP +
                            ", Trạng thái: " + trangThai +
                            ", ID: " + idOTP);

                    // Đánh dấu OTP đã sử dụng
                    String updateQuery = "MATCH (otp:OTP {idOTP: $idOTP}) " +
                            "SET otp.trangThai = 'Đã sử dụng'";

                    session.run(updateQuery, Map.of("idOTP", idOTP));
                    LOGGER.info("Đã đánh dấu OTP " + idOTP + " là đã sử dụng");

                    return true;
                } else {
                    LOGGER.warning("Không tìm thấy OTP hợp lệ trong cơ sở dữ liệu");

                    // Kiểm tra xem OTP có tồn tại nhưng không hợp lệ
                    String checkQuery = "MATCH (otp:OTP {idNV: $idNV, maOTP: $maOTP}) " +
                            "RETURN otp";

                    Result checkResult = session.run(checkQuery, Map.of("idNV", idNV, "maOTP", otp));

                    if (checkResult.hasNext()) {
                        Record record = checkResult.next();
                        Value otpValue = record.get("otp");

                        String trangThai = otpValue.get("trangThai").asString();
                        String thoiGianHetHan = otpValue.get("thoiGianHetHan").asString();

                        LOGGER.warning("OTP tồn tại nhưng không hợp lệ: " +
                                "Trạng thái = " + trangThai +
                                ", Thời gian hết hạn = " + thoiGianHetHan);
                    } else {
                        LOGGER.warning("OTP " + otp + " không tồn tại trong cơ sở dữ liệu");
                    }

                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xác thực OTP", e);
            e.printStackTrace();
            return false;
        }
    }
}