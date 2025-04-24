package iuh.fit.dao;

import iuh.fit.entity.KhachHang;
import iuh.fit.entity.OTP;
import iuh.fit.config.Neo4jConfig;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OTPDAO extends GenericDAO<OTP> {

    private static final Logger LOGGER = Logger.getLogger(OTPDAO.class.getName());

    public boolean save(OTP otp) {
        // Đánh dấu tất cả OTP cũ là đã sử dụng trước khi lưu OTP mới
        markAllPreviousOTPsAsUsed(otp.getIdKH());

        String query = "CREATE (otp:OTP {idOTP: $idOTP, idKH: $idKH, maOTP: $maOTP, " +
                "thoiGianTao: datetime($thoiGianTao), thoiGianHetHan: datetime($thoiGianHetHan), " +
                "trangThai: $trangThai}) " +
                "WITH otp " +
                "MATCH (kh:KhachHang {idKH: $idKH}) " +
                "CREATE (otp)-[:XAC_THUC_CHO]->(kh)";

        Map<String, Object> params = new HashMap<>();
        params.put("idOTP", otp.getIdOTP());
        params.put("idKH", otp.getIdKH());
        params.put("maOTP", otp.getMaOTP());
        params.put("thoiGianTao", otp.getThoiGianTao().toString());
        params.put("thoiGianHetHan", otp.getThoiGianHetHan().toString());
        params.put("trangThai", otp.getTrangThai());

        LOGGER.info("Lưu OTP mới: " + otp.getMaOTP() + " cho khách hàng: " + otp.getIdKH());

        boolean result = executeUpdate(query, params);
        LOGGER.info("Kết quả lưu OTP: " + (result ? "Thành công" : "Thất bại"));
        return result;
    }

    // Thêm phương thức mới để tìm OTP theo mã chính xác
    public Optional<OTP> findExactOTP(String idKH, String maOTP) {
        LOGGER.info("Tìm OTP chính xác với idKH: " + idKH + ", maOTP: '" + maOTP + "'");

        String query = "MATCH (otp:OTP {idKH: $idKH, maOTP: $maOTP})-[:XAC_THUC_CHO]->(kh:KhachHang) " +
                "RETURN otp, kh";

        List<OTP> results = executeQuery(query, Map.of("idKH", idKH, "maOTP", maOTP));

        if (results.isEmpty()) {
            LOGGER.warning("Không tìm thấy OTP chính xác với idKH: " + idKH + ", maOTP: '" + maOTP + "'");
        } else {
            LOGGER.info("Đã tìm thấy OTP chính xác: " + results.get(0).getMaOTP() +
                    ", Trạng thái: " + results.get(0).getTrangThai());
        }

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Sửa đổi phương thức findValidOTP để kiểm tra chính xác hơn
    public Optional<OTP> findValidOTP(String idKH, String maOTP) {
        LOGGER.info("Tìm OTP hợp lệ với idKH: " + idKH + ", maOTP: '" + maOTP + "', độ dài: " + maOTP.length());

        // In ra tất cả OTP của khách hàng để gỡ lỗi
        logAllOTPs(idKH);

        // Sử dụng truy vấn Cypher trực tiếp để tránh vấn đề với tham số
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (otp:OTP)-[:XAC_THUC_CHO]->(kh:KhachHang) " +
                    "WHERE otp.idKH = $idKH AND otp.maOTP = $maOTP " +
                    "AND otp.trangThai = 'Chưa sử dụng' " +
                    "AND datetime() < otp.thoiGianHetHan " +
                    "RETURN otp, kh";

            Result result = session.run(query, Map.of("idKH", idKH, "maOTP", maOTP));

            if (result.hasNext()) {
                Record record = result.next();
                OTP otp = mapRecordToEntity(record);
                LOGGER.info("Đã tìm thấy OTP hợp lệ: " + otp.getMaOTP());
                return Optional.of(otp);
            } else {
                LOGGER.warning("Không tìm thấy OTP hợp lệ với idKH: " + idKH + ", maOTP: '" + maOTP + "'");

                // Kiểm tra xem OTP có tồn tại nhưng đã hết hạn hoặc đã sử dụng
                checkOTPStatus(idKH, maOTP);
                return Optional.empty();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm OTP hợp lệ: " + e.getMessage(), e);
            return Optional.empty();
        }
    }



    public boolean markAsUsed(String idOTP) {
        LOGGER.info("Đánh dấu OTP đã sử dụng: " + idOTP);

        String query = "MATCH (otp:OTP {idOTP: $idOTP}) " +
                "SET otp.trangThai = 'Đã sử dụng'";

        boolean result = executeUpdate(query, Map.of("idOTP", idOTP));
        LOGGER.info("Kết quả đánh dấu OTP đã sử dụng: " + (result ? "Thành công" : "Thất bại"));
        return result;
    }

    /**
     * Đánh dấu tất cả OTP cũ của khách hàng là đã sử dụng
     */
    public boolean markAllPreviousOTPsAsUsed(String idKH) {
        LOGGER.info("Đánh dấu tất cả OTP cũ của khách hàng " + idKH + " là đã sử dụng");

        String query = "MATCH (otp:OTP {idKH: $idKH}) " +
                "WHERE otp.trangThai = 'Chưa sử dụng' " +
                "SET otp.trangThai = 'Đã sử dụng'";

        boolean result = executeUpdate(query, Map.of("idKH", idKH));
        LOGGER.info("Kết quả đánh dấu tất cả OTP cũ: " + (result ? "Thành công" : "Thất bại"));
        return result;
    }

    /**
     * Tìm OTP mới nhất của khách hàng
     */
    public Optional<OTP> findLatestOTP(String idKH) {
        LOGGER.info("Tìm OTP mới nhất của khách hàng " + idKH);

        String query = "MATCH (otp:OTP {idKH: $idKH})-[:XAC_THUC_CHO]->(kh:KhachHang) " +
                "RETURN otp, kh " +
                "ORDER BY otp.thoiGianTao DESC " +
                "LIMIT 1";

        List<OTP> results = executeQuery(query, Map.of("idKH", idKH));

        if (!results.isEmpty()) {
            LOGGER.info("Đã tìm thấy OTP mới nhất: " + results.get(0).getMaOTP() +
                    ", Trạng thái: " + results.get(0).getTrangThai());
        } else {
            LOGGER.warning("Không tìm thấy OTP nào cho khách hàng " + idKH);
        }

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Ghi log tất cả OTP của khách hàng để gỡ lỗi
     */
    // Cải thiện phương thức logAllOTPs để hiển thị thông tin chi tiết hơn
    private void logAllOTPs(String idKH) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (otp:OTP {idKH: $idKH}) " +
                    "RETURN otp " +
                    "ORDER BY otp.thoiGianTao DESC";

            Result result = session.run(query, Map.of("idKH", idKH));

            LOGGER.info("=== Danh sách OTP của khách hàng " + idKH + " ===");
            int count = 0;

            while (result.hasNext()) {
                count++;
                Record record = result.next();
                Value otpValue = record.get("otp");

                String maOTP = otpValue.get("maOTP").asString();

                LOGGER.info(count + ". OTP: '" + maOTP + "', độ dài: " + maOTP.length() +
                        ", ID: " + otpValue.get("idOTP").asString() +
                        ", Trạng thái: " + otpValue.get("trangThai").asString() +
                        ", Thời gian tạo: " + otpValue.get("thoiGianTao").asString() +
                        ", Thời gian hết hạn: " + otpValue.get("thoiGianHetHan").asString());
            }

            if (count == 0) {
                LOGGER.warning("Không tìm thấy OTP nào cho khách hàng " + idKH);
            }

            LOGGER.info("=== Kết thúc danh sách OTP ===");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi ghi log OTP: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra trạng thái của OTP cụ thể
     */
    private void checkOTPStatus(String idKH, String maOTP) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (otp:OTP {idKH: $idKH, maOTP: $maOTP}) RETURN otp";

            Result result = session.run(query, Map.of("idKH", idKH, "maOTP", maOTP));

            if (result.hasNext()) {
                Record record = result.next();
                Value otpValue = record.get("otp");

                String trangThai = otpValue.get("trangThai").asString();
                String thoiGianHetHan = otpValue.get("thoiGianHetHan").asString();

                LOGGER.warning("OTP " + maOTP + " tồn tại nhưng không hợp lệ: " +
                        "Trạng thái = " + trangThai +
                        ", Thời gian hết hạn = " + thoiGianHetHan);
            } else {
                LOGGER.warning("OTP " + maOTP + " không tồn tại trong cơ sở dữ liệu");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra trạng thái OTP: " + e.getMessage(), e);
        }
    }

    @Override
    protected OTP mapRecordToEntity(Record record) {
        try {
            Value otpValue = record.get("otp");

            OTP otp = new OTP();
            otp.setIdOTP(otpValue.get("idOTP").asString());
            otp.setIdKH(otpValue.get("idKH").asString());
            otp.setMaOTP(otpValue.get("maOTP").asString());
            otp.setThoiGianTao(otpValue.get("thoiGianTao").asLocalDateTime());
            otp.setThoiGianHetHan(otpValue.get("thoiGianHetHan").asLocalDateTime());
            otp.setTrangThai(otpValue.get("trangThai").asString());

            if (record.containsKey("kh")) {
                Value khValue = record.get("kh");

                KhachHang khachHang = new KhachHang();
                khachHang.setIdKH(khValue.get("idKH").asString());
                khachHang.setHoTen(khValue.get("hoTen").asString());
                khachHang.setSdt(khValue.get("sdt").asString());
                khachHang.setEmail(khValue.get("email").asString());
                khachHang.setGioiTinh(khValue.get("gioiTinh").asString());
                khachHang.setNgayThamGia(khValue.get("ngayThamGia").asLocalDate());
                khachHang.setHangMuc(khValue.get("hangMuc").asString());
                khachHang.setTongChiTieu(khValue.get("tongChiTieu").asDouble());

                otp.setKhachHang(khachHang);
            }

            return otp;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chuyển đổi Record thành OTP: " + e.getMessage(), e);
            return null;
        }
    }
}
