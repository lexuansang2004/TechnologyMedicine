package iuh.fit.service;

import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.OTPDAO;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.OTP;
import iuh.fit.util.CypherQueryUtil;
import iuh.fit.util.OTPUtil;
import iuh.fit.config.Neo4jConfig;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KhachHangService {
    private static final Logger LOGGER = Logger.getLogger(KhachHangService.class.getName());
    private final KhachHangDAO khachHangDAO;
    private final OTPDAO otpDAO;

    public KhachHangService() {
        this.khachHangDAO = new KhachHangDAO();
        this.otpDAO = new OTPDAO();
    }

    /**
     * Chuyển đổi KhachHang thành Map<String, Object>
     */
    private Map<String, Object> convertToMap(KhachHang khachHang) {
        Map<String, Object> map = new HashMap<>();
        map.put("idKH", khachHang.getIdKH());
        map.put("hoTen", khachHang.getHoTen());
        map.put("sdt", khachHang.getSdt());
        map.put("email", khachHang.getEmail());
        map.put("gioiTinh", khachHang.getGioiTinh());
        map.put("ngayThamGia", khachHang.getNgayThamGia());
        map.put("hangMuc", khachHang.getHangMuc());
        map.put("tongChiTieu", khachHang.getTongChiTieu());
        return map;
    }

    /**
     * Chuyển đổi Map<String, Object> thành KhachHang
     */
    private KhachHang convertToEntity(Map<String, Object> map) {
        KhachHang khachHang = new KhachHang();

        if (map.containsKey("idKH")) {
            khachHang.setIdKH((String) map.get("idKH"));
        } else {
            khachHang.setIdKH(generateId());
        }

        if (map.containsKey("hoTen")) {
            khachHang.setHoTen((String) map.get("hoTen"));
        }

        if (map.containsKey("sdt")) {
            khachHang.setSdt((String) map.get("sdt"));
        }

        if (map.containsKey("email")) {
            khachHang.setEmail((String) map.get("email"));
        }

        if (map.containsKey("gioiTinh")) {
            khachHang.setGioiTinh((String) map.get("gioiTinh"));
        }

        if (map.containsKey("ngayThamGia")) {
            if (map.get("ngayThamGia") instanceof LocalDate) {
                khachHang.setNgayThamGia((LocalDate) map.get("ngayThamGia"));
            } else if (map.get("ngayThamGia") instanceof String) {
                khachHang.setNgayThamGia(LocalDate.parse((String) map.get("ngayThamGia")));
            }
        } else {
            khachHang.setNgayThamGia(LocalDate.now());
        }

        if (map.containsKey("hangMuc")) {
            khachHang.setHangMuc((String) map.get("hangMuc"));
        } else {
            khachHang.setHangMuc("Thường");
        }

        if (map.containsKey("tongChiTieu")) {
            if (map.get("tongChiTieu") instanceof Number) {
                khachHang.setTongChiTieu(((Number) map.get("tongChiTieu")).doubleValue());
            } else if (map.get("tongChiTieu") instanceof String) {
                khachHang.setTongChiTieu(Double.parseDouble((String) map.get("tongChiTieu")));
            }
        } else {
            khachHang.setTongChiTieu(0.0);
        }

        return khachHang;
    }

    public List<Map<String, Object>> findAll() {
        // Chuyển đổi List<KhachHang> thành List<Map<String, Object>>
        List<KhachHang> khachHangList = khachHangDAO.findAll();
        return khachHangList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    public Optional<Map<String, Object>> findById(String id) {
        // Chuyển đổi Optional<KhachHang> thành Optional<Map<String, Object>>
        Optional<KhachHang> khachHangOpt = khachHangDAO.findById(id);
        return khachHangOpt.map(this::convertToMap);
    }

    public List<Map<String, Object>> findByName(String hoTen) {
        List<Map<String, Object>> khachHangList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (kh:KhachHang) WHERE kh.hoTen CONTAINS $hoTen RETURN kh";
            Result result = session.run(query, Values.parameters("hoTen", hoTen));

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> khachHang = record.get("kh").asMap();
                khachHangList.add(khachHang);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding customers by name", e);
        }

        return khachHangList;
    }

//    public List<Map<String, Object>> search(String keyword) {
//        List<Map<String, Object>> khachHangList = new ArrayList<>();
//
//        try (Session session = Neo4jConfig.getInstance().getSession()) {
//            String query = "MATCH (kh:KhachHang) " +
//                    "WHERE kh.idKH CONTAINS $keyword " +
//                    "OR kh.hoTen CONTAINS $keyword " +
//                    "OR kh.sdt CONTAINS $keyword " +
//                    "OR kh.email CONTAINS $keyword " +
//                    "RETURN kh";
//
//            Result result = session.run(query, Values.parameters("keyword", keyword));
//
//            while (result.hasNext()) {
//                Record record = result.next();
//                Map<String, Object> khachHang = record.get("kh").asMap();
//                khachHangList.add(khachHang);
//            }
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error searching customers", e);
//        }
//
//        return khachHangList;
//    }

    // Thêm phương thức tìm kiếm theo tiêu chí
    public List<Map<String, Object>> search(String keyword, String criteria) {
        List<Map<String, Object>> khachHangList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query;

            // Xây dựng câu truy vấn dựa trên tiêu chí tìm kiếm
            if ("ID".equals(criteria)) {
                query = "MATCH (kh:KhachHang) WHERE kh.idKH CONTAINS $keyword RETURN kh";
            } else if ("Họ tên".equals(criteria)) {
                query = "MATCH (kh:KhachHang) WHERE kh.hoTen CONTAINS $keyword RETURN kh";
            } else if ("Hạng mục".equals(criteria)) {
                query = "MATCH (kh:KhachHang) WHERE kh.hangMuc CONTAINS $keyword RETURN kh";
            } else {
                // Tìm kiếm tất cả
                query = "MATCH (kh:KhachHang) " +
                        "WHERE kh.idKH CONTAINS $keyword " +
                        "OR kh.hoTen CONTAINS $keyword " +
                        "OR kh.sdt CONTAINS $keyword " +
                        "OR kh.email CONTAINS $keyword " +
                        "OR kh.hangMuc CONTAINS $keyword " +
                        "RETURN kh";
            }

            Result result = session.run(query, Values.parameters("keyword", keyword));

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> khachHang = record.get("kh").asMap();
                khachHangList.add(khachHang);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching customers", e);
        }

        return khachHangList;
    }

//    public boolean save(Map<String, Object> khachHangMap) {
//        // Chuyển đổi Map<String, Object> thành KhachHang
//        KhachHang khachHang = convertToEntity(khachHangMap);
//        return khachHangDAO.save(khachHang);
//    }

    public boolean save(Map<String, Object> khachHangMap) {
        // Chuyển đổi Map<String, Object> thành KhachHang
        KhachHang khachHang = convertToEntity(khachHangMap);

        // Kiểm tra ngày tham gia - sửa lại logic để đảm bảo ngày tham gia không sau ngày hiện tại
        if (khachHang.getNgayThamGia().isAfter(LocalDate.now())) {
            LOGGER.warning("Ngày tham gia không thể sau ngày hiện tại");
            return false;
        }

        return khachHangDAO.save(khachHang);
    }
//    public boolean update(Map<String, Object> khachHangMap) {
//        // Chuyển đổi Map<String, Object> thành KhachHang
//        KhachHang khachHang = convertToEntity(khachHangMap);
//        return khachHangDAO.update(khachHang);
//    }

    public boolean update(Map<String, Object> khachHangMap) {
        // Chuyển đổi Map<String, Object> thành KhachHang
        KhachHang khachHang = convertToEntity(khachHangMap);

        // Kiểm tra ngày tham gia - sửa lại logic để đảm bảo ngày tham gia không sau ngày hiện tại
        if (khachHang.getNgayThamGia().isAfter(LocalDate.now())) {
            LOGGER.warning("Ngày tham gia không thể sau ngày hiện tại");
            return false;
        }

        return khachHangDAO.update(khachHang);
    }

    public boolean delete(String id) {
        return khachHangDAO.delete(id);
    }

//    public int generateOTP(String idKH) {
//        try {
//            // Tạo mã OTP
//            String otpString = OTPUtil.generateOTP();
//            int otpValue = Integer.parseInt(otpString);
//
//            // Tạo đối tượng OTP
//            String idOTP = "OTP" + System.currentTimeMillis();
//            OTP otp = OTPUtil.createOTP(idOTP, idKH);
//
//            // Lưu OTP vào database
//            otpDAO.save(otp);
//
//            // Lấy thông tin khách hàng để gửi OTP
//            Optional<KhachHang> khachHangOpt = khachHangDAO.findById(idKH);
//            if (khachHangOpt.isPresent()) {
//                KhachHang khachHang = khachHangOpt.get();
//                // Gửi OTP qua email hoặc SMS
//                OTPUtil.sendOTPByEmail(khachHang, otp);
//            }
//
//            return otpValue;
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Lỗi khi tạo OTP", e);
//            return -1;
//        }
//    }
//
//    public boolean verifyOTP(String idKH, String otp) {
//        try {
//            // Tìm OTP hợp lệ
//            Optional<OTP> otpOpt = otpDAO.findValidOTP(idKH, otp);
//
//            if (otpOpt.isPresent()) {
//                // Đánh dấu OTP đã sử dụng
//                otpDAO.markAsUsed(otpOpt.get().getIdOTP());
//                return true;
//            }
//
//            return false;
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Lỗi khi xác thực OTP", e);
//            return false;
//        }
//    }

    // Chỉ hiển thị phương thức generateOTP() và verifyOTP() đã sửa đổi
    public int generateOTP(String idKH, String method) {
        try {
            // Tạo mã OTP
            String otpString = OTPUtil.generateOTP();
            int otpValue = Integer.parseInt(otpString);

            LOGGER.info("Đã tạo OTP: " + otpValue + " cho khách hàng ID: " + idKH + " bằng phương thức: " + method);

            // Tạo đối tượng OTP
            String idOTP = "OTP" + System.currentTimeMillis();
            OTP otp = OTPUtil.createOTP(idOTP, idKH);

            // Đảm bảo OTP trong đối tượng khớp với OTP đã tạo
            otp.setMaOTP(otpString);

            // Lưu OTP vào database (phương thức save sẽ đánh dấu tất cả OTP cũ là đã sử dụng)
            boolean saveResult = otpDAO.save(otp);
            if (!saveResult) {
                LOGGER.warning("Không thể lưu OTP vào database");
                return -1;
            }

            // Lấy thông tin khách hàng để gửi OTP
            Optional<KhachHang> khachHangOpt = khachHangDAO.findById(idKH);
            if (khachHangOpt.isPresent()) {
                KhachHang khachHang = khachHangOpt.get();
                LOGGER.info("Tìm thấy khách hàng: " + khachHang.getHoTen() + ", Email: " + khachHang.getEmail() + ", SĐT: " + khachHang.getSdt());

                // Gửi OTP qua email hoặc SMS tùy theo phương thức được chọn
                boolean sendResult = false;
                if ("email".equals(method)) {
                    if (khachHang.getEmail() != null && !khachHang.getEmail().isEmpty()) {
                        sendResult = OTPUtil.sendOTPByEmail(khachHang, otp);
                        LOGGER.info("Kết quả gửi OTP qua email: " + (sendResult ? "Thành công" : "Thất bại"));
                    } else {
                        LOGGER.warning("Khách hàng không có email");
                    }
                } else if ("sms".equals(method)) {
                    // Chức năng SMS đang được cải tiến, không thực hiện gửi
                    LOGGER.info("Chức năng gửi OTP qua SMS đang được cải tiến");
                    sendResult = true; // Giả lập thành công để tiếp tục quy trình
                } else {
                    // Mặc định gửi qua email
                    sendResult = OTPUtil.sendOTPByEmail(khachHang, otp);
                    LOGGER.info("Kết quả gửi OTP mặc định qua email: " + (sendResult ? "Thành công" : "Thất bại"));
                }

                // Kiểm tra lại OTP đã lưu trong cơ sở dữ liệu
                LOGGER.info("Kiểm tra OTP đã lưu trong cơ sở dữ liệu cho khách hàng " + idKH);
                Optional<OTP> savedOTP = otpDAO.findLatestOTP(idKH);
                if (savedOTP.isPresent()) {
                    LOGGER.info("OTP đã lưu: " + savedOTP.get().getMaOTP() + ", Trạng thái: " + savedOTP.get().getTrangThai());

                    // Đảm bảo OTP trả về khớp với OTP đã lưu
                    if (!savedOTP.get().getMaOTP().equals(otpString)) {
                        LOGGER.severe("OTP trả về (" + otpString + ") không khớp với OTP đã lưu (" + savedOTP.get().getMaOTP() + ")");
                        return Integer.parseInt(savedOTP.get().getMaOTP());
                    }
                } else {
                    LOGGER.warning("Không tìm thấy OTP đã lưu cho khách hàng " + idKH);
                }

                return otpValue;
            } else {
                LOGGER.warning("Không tìm thấy khách hàng với ID: " + idKH);
                return -1;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tạo OTP", e);
            e.printStackTrace();
            return -1;
        }
    }

    // Chỉ hiển thị phương thức verifyOTP() đã sửa đổi
    public boolean verifyOTP(String idKH, String otp) {
        try {
            LOGGER.info("Xác thực OTP: " + otp + " cho khách hàng ID: " + idKH);

            // In ra thông tin OTP để debug
            LOGGER.info("OTP cần xác thực: '" + otp + "', độ dài: " + otp.length());

            // Kiểm tra trực tiếp trong cơ sở dữ liệu
            try (Session session = Neo4jConfig.getInstance().getSession()) {
                String query = "MATCH (otp:OTP {idKH: $idKH, maOTP: $maOTP}) " +
                        "WHERE otp.trangThai = 'Chưa sử dụng' " +
                        "RETURN otp";

                Result result = session.run(query, Map.of("idKH", idKH, "maOTP", otp));

                if (result.hasNext()) {
                    Record record = result.next();
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
                    String checkQuery = "MATCH (otp:OTP {idKH: $idKH, maOTP: $maOTP}) " +
                            "RETURN otp";

                    Result checkResult = session.run(checkQuery, Map.of("idKH", idKH, "maOTP", otp));

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


    /**
     * Cập nhật hạng mục khách hàng dựa trên tổng tiền chi tiêu
     *
     * @param idKH ID của khách hàng
     * @param tongTien Tổng tiền chi tiêu
     * @return Map chứa ID khách hàng và hạng mục mới
     */
    public Map<String, Object> updateCustomerRank(String idKH, double tongTien) {
        try {
            return CypherQueryUtil.updateCustomerRank(idKH, tongTien);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật hạng mục khách hàng", e);
            return null;
        }
    }

    private String generateId() {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (kh:KhachHang) RETURN COUNT(kh) as count";
            Result result = session.run(query);

            long count = 0;
            if (result.hasNext()) {
                count = result.next().get("count").asLong();
            }

            return String.format("KH%03d", count + 1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating ID", e);
            return "KH" + System.currentTimeMillis();
        }
    }
}