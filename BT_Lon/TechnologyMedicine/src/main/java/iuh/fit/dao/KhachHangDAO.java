package iuh.fit.dao;

import iuh.fit.entity.KhachHang;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KhachHangDAO extends GenericDAO<KhachHang> {

    public List<KhachHang> findAll() {
        String query = "MATCH (kh:KhachHang) RETURN kh";
        return executeQuery(query, Map.of());
    }

    public Optional<KhachHang> findById(String id) {
        String query = "MATCH (kh:KhachHang {idKH: $idKH}) RETURN kh";

        List<KhachHang> results = executeQuery(query, Map.of("idKH", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<KhachHang> findByPhone(String phone) {
        String query = "MATCH (kh:KhachHang {sdt: $sdt}) RETURN kh";

        List<KhachHang> results = executeQuery(query, Map.of("sdt", phone));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<KhachHang> findByEmail(String email) {
        String query = "MATCH (kh:KhachHang {email: $email}) RETURN kh";

        List<KhachHang> results = executeQuery(query, Map.of("email", email));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<KhachHang> findByKeyword(String keyword) {
        String query = "MATCH (kh:KhachHang) " +
                "WHERE kh.hoTen CONTAINS $keyword OR kh.sdt CONTAINS $keyword " +
                "RETURN kh";

        return executeQuery(query, Map.of("keyword", keyword));
    }

    public boolean save(KhachHang khachHang) {
        String query = "CREATE (kh:KhachHang {idKH: $idKH, hoTen: $hoTen, sdt: $sdt, email: $email, " +
                "gioiTinh: $gioiTinh, ngayThamGia: date($ngayThamGia), " +
                "hangMuc: $hangMuc, tongChiTieu: $tongChiTieu})";

        Map<String, Object> params = new HashMap<>();
        params.put("idKH", khachHang.getIdKH());
        params.put("hoTen", khachHang.getHoTen());
        params.put("sdt", khachHang.getSdt());
        params.put("email", khachHang.getEmail());
        params.put("gioiTinh", khachHang.getGioiTinh());
        params.put("ngayThamGia", khachHang.getNgayThamGia().toString());
        params.put("hangMuc", khachHang.getHangMuc());
        params.put("tongChiTieu", khachHang.getTongChiTieu());

        return executeUpdate(query, params);
    }

    public boolean update(KhachHang khachHang) {
        String query = "MATCH (kh:KhachHang {idKH: $idKH}) " +
                "SET kh.hoTen = $hoTen, kh.sdt = $sdt, kh.email = $email, " +
                "kh.gioiTinh = $gioiTinh, kh.hangMuc = $hangMuc, kh.tongChiTieu = $tongChiTieu";

        Map<String, Object> params = new HashMap<>();
        params.put("idKH", khachHang.getIdKH());
        params.put("hoTen", khachHang.getHoTen());
        params.put("sdt", khachHang.getSdt());
        params.put("email", khachHang.getEmail());
        params.put("gioiTinh", khachHang.getGioiTinh());
        params.put("hangMuc", khachHang.getHangMuc());
        params.put("tongChiTieu", khachHang.getTongChiTieu());

        return executeUpdate(query, params);
    }

    public boolean updateSpending(String id, double amount) {
        String query = "MATCH (kh:KhachHang {idKH: $idKH}) " +
                "SET kh.tongChiTieu = kh.tongChiTieu + $amount, " +
                "kh.hangMuc = CASE " +
                "  WHEN kh.tongChiTieu + $amount >= 5000000 THEN 'Vàng' " +
                "  WHEN kh.tongChiTieu + $amount >= 2000000 THEN 'Bạc' " +
                "  ELSE 'Thường' " +
                "END";

        return executeUpdate(query, Map.of("idKH", id, "amount", amount));
    }

    public boolean delete(String id) {
        String query = "MATCH (kh:KhachHang {idKH: $idKH}) " +
                "DETACH DELETE kh";

        return executeUpdate(query, Map.of("idKH", id));
    }

    @Override
    protected KhachHang mapRecordToEntity(Record record) {
        try {
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

            return khachHang;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to KhachHang: " + e.getMessage());
            return null;
        }
    }
}