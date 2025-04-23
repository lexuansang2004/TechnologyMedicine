package iuh.fit.dao;

import iuh.fit.entity.NhanVien;
import iuh.fit.entity.TaiKhoan;
import iuh.fit.entity.VaiTro;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaiKhoanDAO extends GenericDAO<TaiKhoan> {

    public List<TaiKhoan> findAll() {
        String query = "MATCH (tk:TaiKhoan)-[:THUOC_VE]->(nv:NhanVien), (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                "RETURN tk, nv, vt";
        return executeQuery(query, Map.of());
    }

    public Optional<TaiKhoan> findById(String id) {
        String query = "MATCH (tk:TaiKhoan {idTK: $idTK})-[:THUOC_VE]->(nv:NhanVien), (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                "RETURN tk, nv, vt";

        List<TaiKhoan> results = executeQuery(query, Map.of("idTK", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<TaiKhoan> findByUsername(String username) {
        String query = "MATCH (tk:TaiKhoan {username: $username})-[:THUOC_VE]->(nv:NhanVien), (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                "RETURN tk, nv, vt";

        List<TaiKhoan> results = executeQuery(query, Map.of("username", username));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<TaiKhoan> findByNhanVien(String idNV) {
        String query = "MATCH (tk:TaiKhoan)-[:THUOC_VE]->(nv:NhanVien {idNV: $idNV}), (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                "RETURN tk, nv, vt";

        return executeQuery(query, Map.of("idNV", idNV));
    }

    public boolean save(TaiKhoan taiKhoan) {
        String query = "CREATE (tk:TaiKhoan {idTK: $idTK, username: $username, password: $password, " +
                "idNV: $idNV, idVT: $idVT}) " +
                "WITH tk " +
                "MATCH (nv:NhanVien {idNV: $idNV}), (vt:VaiTro {idVT: $idVT}) " +
                "CREATE (tk)-[:THUOC_VE]->(nv), (tk)-[:CO_VAI_TRO]->(vt)";

        Map<String, Object> params = new HashMap<>();
        params.put("idTK", taiKhoan.getIdTK());
        params.put("username", taiKhoan.getUsername());
        params.put("password", taiKhoan.getPassword());
        params.put("idNV", taiKhoan.getIdNV());
        params.put("idVT", taiKhoan.getIdVT());

        return executeUpdate(query, params);
    }

    public boolean update(TaiKhoan taiKhoan) {
        String query = "MATCH (tk:TaiKhoan {idTK: $idTK}) " +
                "SET tk.username = $username, tk.password = $password, tk.idVT = $idVT " +
                "WITH tk " +
                "MATCH (tk)-[r:CO_VAI_TRO]->() " +
                "DELETE r " +
                "WITH tk " +
                "MATCH (vt:VaiTro {idVT: $idVT}) " +
                "CREATE (tk)-[:CO_VAI_TRO]->(vt)";

        Map<String, Object> params = new HashMap<>();
        params.put("idTK", taiKhoan.getIdTK());
        params.put("username", taiKhoan.getUsername());
        params.put("password", taiKhoan.getPassword());
        params.put("idVT", taiKhoan.getIdVT());

        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (tk:TaiKhoan {idTK: $idTK}) " +
                "DETACH DELETE tk";

        return executeUpdate(query, Map.of("idTK", id));
    }

    @Override
    protected TaiKhoan mapRecordToEntity(Record record) {
        try {
            Value tkValue = record.get("tk");
            Value nvValue = record.get("nv");
            Value vtValue = record.get("vt");

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setIdTK(tkValue.get("idTK").asString());
            taiKhoan.setUsername(tkValue.get("username").asString());
            taiKhoan.setPassword(tkValue.get("password").asString());
            taiKhoan.setIdNV(tkValue.get("idNV").asString());
            taiKhoan.setIdVT(tkValue.get("idVT").asString());

            NhanVien nhanVien = new NhanVien();
            nhanVien.setIdNV(nvValue.get("idNV").asString());
            nhanVien.setHoTen(nvValue.get("hoTen").asString());
            nhanVien.setSdt(nvValue.get("sdt").asString());
            nhanVien.setGioiTinh(nvValue.get("gioiTinh").asString());
            nhanVien.setNamSinh(nvValue.get("namSinh").asInt());
            nhanVien.setNgayVaoLam(nvValue.get("ngayVaoLam").asLocalDate());

            VaiTro vaiTro = new VaiTro();
            vaiTro.setIdVT(vtValue.get("idVT").asString());
            vaiTro.setTen(vtValue.get("ten").asString());

            taiKhoan.setNhanVien(nhanVien);
            taiKhoan.setVaiTro(vaiTro);

            return taiKhoan;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to TaiKhoan: " + e.getMessage());
            return null;
        }
    }
}