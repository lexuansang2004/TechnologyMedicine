package iuh.fit.service;

import iuh.fit.dao.KhachHangDAO;
import iuh.fit.dao.OTPDAO;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.OTP;
import iuh.fit.util.CypherQueryUtil;
import iuh.fit.util.OTPUtil;
import iuh.fit.config.Neo4jConfig;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

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

    public List<Map<String, Object>> search(String keyword) {
        List<Map<String, Object>> khachHangList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (kh:KhachHang) " +
                    "WHERE kh.idKH CONTAINS $keyword " +
                    "OR kh.hoTen CONTAINS $keyword " +
                    "OR kh.sdt CONTAINS $keyword " +
                    "OR kh.email CONTAINS $keyword " +
                    "RETURN kh";

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

    public boolean save(Map<String, Object> khachHangMap) {
        // Chuyển đổi Map<String, Object> thành KhachHang
        KhachHang khachHang = convertToEntity(khachHangMap);
        return khachHangDAO.save(khachHang);
    }

    public boolean update(Map<String, Object> khachHangMap) {
        // Chuyển đổi Map<String, Object> thành KhachHang
        KhachHang khachHang = convertToEntity(khachHangMap);
        return khachHangDAO.update(khachHang);
    }

    public boolean delete(String id) {
        return khachHangDAO.delete(id);
    }

    public int generateOTP(String idKH) {
        try {
            // Tạo mã OTP
            String otpString = OTPUtil.generateOTP();
            int otpValue = Integer.parseInt(otpString);

            // Tạo đối tượng OTP
            String idOTP = "OTP" + System.currentTimeMillis();
            OTP otp = OTPUtil.createOTP(idOTP, idKH);

            // Lưu OTP vào database
            otpDAO.save(otp);

            // Lấy thông tin khách hàng để gửi OTP
            Optional<KhachHang> khachHangOpt = khachHangDAO.findById(idKH);
            if (khachHangOpt.isPresent()) {
                KhachHang khachHang = khachHangOpt.get();
                // Gửi OTP qua email hoặc SMS
                OTPUtil.sendOTPByEmail(khachHang, otp);
            }

            return otpValue;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tạo OTP", e);
            return -1;
        }
    }

    public boolean verifyOTP(String idKH, String otp) {
        try {
            // Tìm OTP hợp lệ
            Optional<OTP> otpOpt = otpDAO.findValidOTP(idKH, otp);

            if (otpOpt.isPresent()) {
                // Đánh dấu OTP đã sử dụng
                otpDAO.markAsUsed(otpOpt.get().getIdOTP());
                return true;
            }

            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xác thực OTP", e);
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