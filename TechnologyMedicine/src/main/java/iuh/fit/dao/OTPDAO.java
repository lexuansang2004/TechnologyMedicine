package iuh.fit.dao;

import iuh.fit.entity.KhachHang;
import iuh.fit.entity.OTP;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OTPDAO extends GenericDAO<OTP> {

    public boolean save(OTP otp) {
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

        return executeUpdate(query, params);
    }

    public Optional<OTP> findValidOTP(String idKH, String maOTP) {
        String query = "MATCH (otp:OTP {maOTP: $maOTP, idKH: $idKH})-[:XAC_THUC_CHO]->(kh:KhachHang) " +
                "WHERE otp.trangThai = 'Chưa sử dụng' AND datetime() < otp.thoiGianHetHan " +
                "RETURN otp, kh";

        List<OTP> results = executeQuery(query, Map.of("idKH", idKH, "maOTP", maOTP));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean markAsUsed(String idOTP) {
        String query = "MATCH (otp:OTP {idOTP: $idOTP}) " +
                "SET otp.trangThai = 'Đã sử dụng'";

        return executeUpdate(query, Map.of("idOTP", idOTP));
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
            LOGGER.warning("Error mapping record to OTP: " + e.getMessage());
            return null;
        }
    }
}