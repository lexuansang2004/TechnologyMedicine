package iuh.fit.dao;

import iuh.fit.entity.DanhMuc;
import iuh.fit.entity.DonViTinh;
import iuh.fit.entity.Thuoc;
import iuh.fit.entity.XuatXu;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThuocDAO extends GenericDAO<Thuoc> {

    public List<Thuoc> findAll() {
        String query = "MATCH (t:Thuoc)-[:CO_DON_VI_TINH]->(dvt:DonViTinh), " +
                "(t)-[:THUOC_DANH_MUC]->(dm:DanhMuc), " +
                "(t)-[:CO_XUAT_XU]->(xx:XuatXu) " +
                "RETURN t, dvt, dm, xx";
        return executeQuery(query, Map.of());
    }

    public Optional<Thuoc> findById(String id) {
        String query = "MATCH (t:Thuoc {idThuoc: $idThuoc})-[:CO_DON_VI_TINH]->(dvt:DonViTinh), " +
                "(t)-[:THUOC_DANH_MUC]->(dm:DanhMuc), " +
                "(t)-[:CO_XUAT_XU]->(xx:XuatXu) " +
                "RETURN t, dvt, dm, xx";

        List<Thuoc> results = executeQuery(query, Map.of("idThuoc", id));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Thuoc> findByName(String name) {
        String query = "MATCH (t:Thuoc)-[:CO_DON_VI_TINH]->(dvt:DonViTinh), " +
                "(t)-[:THUOC_DANH_MUC]->(dm:DanhMuc), " +
                "(t)-[:CO_XUAT_XU]->(xx:XuatXu) " +
                "WHERE t.tenThuoc CONTAINS $tenThuoc " +
                "RETURN t, dvt, dm, xx";

        return executeQuery(query, Map.of("tenThuoc", name));
    }

    public boolean save(Thuoc thuoc) {
        String query = "CREATE (t:Thuoc {idThuoc: $idThuoc, tenThuoc: $tenThuoc, hinhAnh: $hinhAnh, " +
                "thanhPhan: $thanhPhan, idDVT: $idDVT, idDM: $idDM, idXX: $idXX, " +
                "soLuongTon: $soLuongTon, giaNhap: $giaNhap, donGia: $donGia, hanSuDung: date($hanSuDung)}) " +
                "WITH t " +
                "MATCH (dvt:DonViTinh {idDVT: $idDVT}), " +
                "(dm:DanhMuc {idDM: $idDM}), " +
                "(xx:XuatXu {idXX: $idXX}) " +
                "CREATE (t)-[:CO_DON_VI_TINH]->(dvt), " +
                "(t)-[:THUOC_DANH_MUC]->(dm), " +
                "(t)-[:CO_XUAT_XU]->(xx)";

        Map<String, Object> params = new HashMap<>();
        params.put("idThuoc", thuoc.getIdThuoc());
        params.put("tenThuoc", thuoc.getTenThuoc());
        params.put("hinhAnh", thuoc.getHinhAnh());
        params.put("thanhPhan", thuoc.getThanhPhan());
        params.put("idDVT", thuoc.getIdDVT());
        params.put("idDM", thuoc.getIdDM());
        params.put("idXX", thuoc.getIdXX());
        params.put("soLuongTon", thuoc.getSoLuongTon());
        params.put("giaNhap", thuoc.getGiaNhap());
        params.put("donGia", thuoc.getDonGia());
        params.put("hanSuDung", thuoc.getHanSuDung().toString());

        return executeUpdate(query, params);
    }

    public boolean update(Thuoc thuoc) {
        String query = "MATCH (t:Thuoc {idThuoc: $idThuoc}) " +
                "SET t.tenThuoc = $tenThuoc, t.hinhAnh = $hinhAnh, t.thanhPhan = $thanhPhan, " +
                "t.idDVT = $idDVT, t.idDM = $idDM, t.idXX = $idXX, " +
                "t.soLuongTon = $soLuongTon, t.giaNhap = $giaNhap, t.donGia = $donGia, " +
                "t.hanSuDung = date($hanSuDung) " +
                "WITH t " +
                "MATCH (t)-[r1:CO_DON_VI_TINH]->(), (t)-[r2:THUOC_DANH_MUC]->(), (t)-[r3:CO_XUAT_XU]->() " +
                "DELETE r1, r2, r3 " +
                "WITH t " +
                "MATCH (dvt:DonViTinh {idDVT: $idDVT}), " +
                "(dm:DanhMuc {idDM: $idDM}), " +
                "(xx:XuatXu {idXX: $idXX}) " +
                "CREATE (t)-[:CO_DON_VI_TINH]->(dvt), " +
                "(t)-[:THUOC_DANH_MUC]->(dm), " +
                "(t)-[:CO_XUAT_XU]->(xx)";

        Map<String, Object> params = new HashMap<>();
        params.put("idThuoc", thuoc.getIdThuoc());
        params.put("tenThuoc", thuoc.getTenThuoc());
        params.put("hinhAnh", thuoc.getHinhAnh());
        params.put("thanhPhan", thuoc.getThanhPhan());
        params.put("idDVT", thuoc.getIdDVT());
        params.put("idDM", thuoc.getIdDM());
        params.put("idXX", thuoc.getIdXX());
        params.put("soLuongTon", thuoc.getSoLuongTon());
        params.put("giaNhap", thuoc.getGiaNhap());
        params.put("donGia", thuoc.getDonGia());
        params.put("hanSuDung", thuoc.getHanSuDung().toString());

        return executeUpdate(query, params);
    }

    public boolean delete(String id) {
        String query = "MATCH (t:Thuoc {idThuoc: $idThuoc}) " +
                "DETACH DELETE t";
        return executeUpdate(query, Map.of("idThuoc", id));
    }

    public boolean updateStock(String id, int quantity) {
        String query = "MATCH (t:Thuoc {idThuoc: $idThuoc}) SET t.soLuongTon = $quantity RETURN t";
        return executeUpdate(query, Map.of("idThuoc", id, "quantity", quantity));
    }

    @Override
    protected Thuoc mapRecordToEntity(Record record) {
        try {
            Value tValue = record.get("t");
            Value dvtValue = record.get("dvt");
            Value dmValue = record.get("dm");
            Value xxValue = record.get("xx");

            Thuoc thuoc = new Thuoc();
            thuoc.setIdThuoc(tValue.get("idThuoc").asString());
            thuoc.setTenThuoc(tValue.get("tenThuoc").asString());
            thuoc.setHinhAnh(tValue.get("hinhAnh").asString());
            thuoc.setThanhPhan(tValue.get("thanhPhan").asString());
            thuoc.setIdDVT(tValue.get("idDVT").asString());
            thuoc.setIdDM(tValue.get("idDM").asString());
            thuoc.setIdXX(tValue.get("idXX").asString());
            thuoc.setSoLuongTon(tValue.get("soLuongTon").asInt());
            thuoc.setGiaNhap(tValue.get("giaNhap").asDouble());
            thuoc.setDonGia(tValue.get("donGia").asDouble());
            thuoc.setHanSuDung(tValue.get("hanSuDung").asLocalDate());

            DonViTinh donViTinh = new DonViTinh();
            donViTinh.setIdDVT(dvtValue.get("idDVT").asString());
            donViTinh.setTen(dvtValue.get("ten").asString());

            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setIdDM(dmValue.get("idDM").asString());
            danhMuc.setTen(dmValue.get("ten").asString());

            XuatXu xuatXu = new XuatXu();
            xuatXu.setIdXX(xxValue.get("idXX").asString());
            xuatXu.setTen(xxValue.get("ten").asString());

            thuoc.setDonViTinh(donViTinh);
            thuoc.setDanhMuc(danhMuc);
            thuoc.setXuatXu(xuatXu);

            return thuoc;
        } catch (Exception e) {
            LOGGER.warning("Error mapping record to Thuoc: " + e.getMessage());
            return null;
        }
    }
}
