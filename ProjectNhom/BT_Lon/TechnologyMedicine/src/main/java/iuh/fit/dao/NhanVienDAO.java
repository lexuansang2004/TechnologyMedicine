package iuh.fit.dao;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.NhanVien;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.util.*;

public class NhanVienDAO extends GenericDAO<NhanVien> {

    public List<NhanVien> findAll() {
        String query = "MATCH (nv:NhanVien) RETURN nv";
        return executeQuery(query, Map.of());
    }

    public Optional<NhanVien> findById(String id) {
        String query = "MATCH (nv:NhanVien {idNV: $idNV}) RETURN nv";
        List<NhanVien> results = executeQuery(query, Map.of("idNV", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<NhanVien> findByName(String name) {
        String query = "MATCH (nv:NhanVien) " +
                "WHERE nv.hoTen CONTAINS $hoTen " +
                "RETURN nv";
        return executeQuery(query, Map.of("hoTen", name));
    }

    public boolean save(NhanVien nhanVien) {
        String query = "CREATE (nv:NhanVien {idNV: $idNV, hoTen: $hoTen, sdt: $sdt, " +
                "gioiTinh: $gioiTinh, namSinh: $namSinh, ngayVaoLam: date($ngayVaoLam)})";

        Map<String, Object> params = new HashMap<>();
        params.put("idNV", nhanVien.getIdNV());
        params.put("hoTen", nhanVien.getHoTen());
        params.put("sdt", nhanVien.getSdt());
        params.put("gioiTinh", nhanVien.getGioiTinh());
        params.put("namSinh", nhanVien.getNamSinh());
        params.put("ngayVaoLam", nhanVien.getNgayVaoLam().toString());

        return executeUpdate(query, params);
    }

    public boolean update(NhanVien nhanVien) {
        String query = "MATCH (nv:NhanVien {idNV: $idNV}) " +
                "SET nv.hoTen = $hoTen, nv.sdt = $sdt, nv.gioiTinh = $gioiTinh, nv.namSinh = $namSinh";

        Map<String, Object> params = new HashMap<>();
        params.put("idNV", nhanVien.getIdNV());
        params.put("hoTen", nhanVien.getHoTen());
        params.put("sdt", nhanVien.getSdt());
        params.put("gioiTinh", nhanVien.getGioiTinh());
        params.put("namSinh", nhanVien.getNamSinh());

        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (nv:NhanVien {idNV: $idNV}) DETACH DELETE nv";
        return executeUpdate(query, Map.of("idNV", id));
    }

    public List<Map<String, Object>> getAllNhanVienWithAccountStatus() {
        String query = "MATCH (nv:NhanVien) " +
                "OPTIONAL MATCH (tk:TaiKhoan)-[:THUOC_VE]->(nv) " +
                "RETURN nv.idNV as idNV, nv.hoTen as hoTen, nv.sdt as sdt, " +
                "nv.gioiTinh as gioiTinh, nv.namSinh as namSinh, " +
                "nv.ngayVaoLam as ngayVaoLam, " +
                "CASE WHEN tk IS NOT NULL THEN 'Đã tạo' ELSE 'Chưa tạo' END as trangThaiTK";

        List<Map<String, Object>> result = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            org.neo4j.driver.Result queryResult = session.run(query);

            while (queryResult.hasNext()) {
                Record record = queryResult.next();
                Map<String, Object> nhanVien = new HashMap<>();

                nhanVien.put("idNV", record.get("idNV").asString());
                nhanVien.put("hoTen", record.get("hoTen").asString());
                nhanVien.put("sdt", record.get("sdt").asString());
                nhanVien.put("gioiTinh", record.get("gioiTinh").asString());
                nhanVien.put("namSinh", record.get("namSinh").asInt());

                // Xử lý ngày vào làm
                Value ngayVaoLamValue = record.get("ngayVaoLam");
                if (!ngayVaoLamValue.isNull()) {
                    nhanVien.put("ngayVaoLam", ngayVaoLamValue.asLocalDate().toString());
                } else {
                    nhanVien.put("ngayVaoLam", "");
                }

                nhanVien.put("trangThaiTK", record.get("trangThaiTK").asString());

                result.add(nhanVien);
            }
        } catch (Exception e) {
            LOGGER.warning("Error getting NhanVien with account status: " + e.getMessage());
        }

        return result;
    }

    @Override
    protected NhanVien mapRecordToEntity(Record record) {
        try {
            Value nvValue = record.get("nv");

            NhanVien nhanVien = new NhanVien();
            nhanVien.setIdNV(nvValue.get("idNV").asString());
            nhanVien.setHoTen(nvValue.get("hoTen").asString());
            nhanVien.setSdt(nvValue.get("sdt").asString());
            nhanVien.setGioiTinh(nvValue.get("gioiTinh").asString());
            nhanVien.setNamSinh(nvValue.get("namSinh").asInt());
            nhanVien.setNgayVaoLam(nvValue.get("ngayVaoLam").asLocalDate());

            return nhanVien;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to NhanVien: " + e.getMessage());
            return null;
        }
    }
}
