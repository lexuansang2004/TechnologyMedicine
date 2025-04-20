package iuh.fit.dao;

import iuh.fit.entity.KhuyenMai;
import iuh.fit.entity.Thuoc;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KhuyenMaiDAO extends GenericDAO<KhuyenMai> {

    public List<KhuyenMai> findAll() {
        String query = "MATCH (km:KhuyenMai) RETURN km";
        return executeQuery(query, Map.of());
    }

    public Optional<KhuyenMai> findById(String id) {
        String query = "MATCH (km:KhuyenMai {idKM: $idKM}) RETURN km";

        List<KhuyenMai> results = executeQuery(query, Map.of("idKM", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<KhuyenMai> findByHangMuc(String hangMuc) {
        String query = "MATCH (km:KhuyenMai) " +
                "WHERE km.hangMuc = $hangMuc AND km.trangThai = 'Đang áp dụng' " +
                "RETURN km";

        return executeQuery(query, Map.of("hangMuc", hangMuc));
    }

    public List<KhuyenMai> findByThuoc(String idThuoc) {
        String query = "MATCH (km:KhuyenMai)-[:AP_DUNG_CHO_THUOC]->(t:Thuoc {idThuoc: $idThuoc}) " +
                "WHERE km.trangThai = 'Đang áp dụng' " +
                "RETURN km, t";

        return executeQuery(query, Map.of("idThuoc", idThuoc));
    }

    public boolean saveForHangMuc(KhuyenMai khuyenMai) {
        String query = "CREATE (km:KhuyenMai {idKM: $idKM, loai: $loai, mucGiamGia: $mucGiamGia, " +
                "hangMuc: $hangMuc, trangThai: $trangThai}) " +
                "WITH km " +
                "MATCH (kh:KhachHang) " +
                "WHERE kh.hangMuc = $hangMuc " +
                "CREATE (km)-[:AP_DUNG_CHO]->(kh)";

        Map<String, Object> params = new HashMap<>();
        params.put("idKM", khuyenMai.getIdKM());
        params.put("loai", khuyenMai.getLoai());
        params.put("mucGiamGia", khuyenMai.getMucGiamGia());
        params.put("hangMuc", khuyenMai.getHangMuc());
        params.put("trangThai", khuyenMai.getTrangThai());

        return executeUpdate(query, params);
    }

    public boolean saveForThuoc(KhuyenMai khuyenMai) {
        String query = "CREATE (km:KhuyenMai {idKM: $idKM, loai: $loai, mucGiamGia: $mucGiamGia, " +
                "idThuoc: $idThuoc, trangThai: $trangThai}) " +
                "WITH km " +
                "MATCH (t:Thuoc {idThuoc: $idThuoc}) " +
                "CREATE (km)-[:AP_DUNG_CHO_THUOC]->(t)";

        Map<String, Object> params = new HashMap<>();
        params.put("idKM", khuyenMai.getIdKM());
        params.put("loai", khuyenMai.getLoai());
        params.put("mucGiamGia", khuyenMai.getMucGiamGia());
        params.put("idThuoc", khuyenMai.getIdThuoc());
        params.put("trangThai", khuyenMai.getTrangThai());

        return executeUpdate(query, params);
    }

    public boolean update(KhuyenMai khuyenMai) {
        String query = "MATCH (km:KhuyenMai {idKM: $idKM}) " +
                "SET km.loai = $loai, km.mucGiamGia = $mucGiamGia, km.trangThai = $trangThai";

        Map<String, Object> params = new HashMap<>();
        params.put("idKM", khuyenMai.getIdKM());
        params.put("loai", khuyenMai.getLoai());
        params.put("mucGiamGia", khuyenMai.getMucGiamGia());
        params.put("trangThai", khuyenMai.getTrangThai());

        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (km:KhuyenMai {idKM: $idKM}) " +
                "DETACH DELETE km";

        return executeUpdate(query, Map.of("idKM", id));
    }

    @Override
    protected KhuyenMai mapRecordToEntity(Record record) {
        try {
            Value kmValue = record.get("km");

            KhuyenMai khuyenMai = new KhuyenMai();
            khuyenMai.setIdKM(kmValue.get("idKM").asString());
            khuyenMai.setLoai(kmValue.get("loai").asString());
            khuyenMai.setMucGiamGia(kmValue.get("mucGiamGia").asDouble());
            khuyenMai.setTrangThai(kmValue.get("trangThai").asString());

            // Kiểm tra xem có thuộc tính hangMuc không
            // Kiểm tra xem có thuộc tính hangMuc không
            if (kmValue.containsKey("hangMuc") && !kmValue.get("hangMuc").isNull()) {
                khuyenMai.setHangMuc(kmValue.get("hangMuc").asString());
            }

// Kiểm tra xem có thuộc tính idThuoc không
            if (kmValue.containsKey("idThuoc") && !kmValue.get("idThuoc").isNull()) {
                khuyenMai.setIdThuoc(kmValue.get("idThuoc").asString());
            }


            // Kiểm tra xem có thông tin thuốc không
            if (record.containsKey("t")) {
                Value tValue = record.get("t");

                Thuoc thuoc = new Thuoc();
                thuoc.setIdThuoc(tValue.get("idThuoc").asString());
                thuoc.setTenThuoc(tValue.get("tenThuoc").asString());

                khuyenMai.setThuoc(thuoc);
            }

            return khuyenMai;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to KhuyenMai: " + e.getMessage());
            return null;
        }
    }
}