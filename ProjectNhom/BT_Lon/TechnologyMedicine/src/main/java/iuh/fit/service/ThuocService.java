package iuh.fit.service;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.dao.ThuocDAO;
import iuh.fit.entity.Thuoc;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThuocService {
    private static final Logger LOGGER = Logger.getLogger(ThuocService.class.getName());

    private final ThuocDAO thuocDAO;

    public ThuocService() {
        this.thuocDAO = new ThuocDAO();
    }

    // Ví dụ một phương thức trong ThuocService
    public List<Thuoc> findAll() {
        return thuocDAO.findAll();
    }



// Tương tự cho các phương thức khác...

    public Optional<Map<String, Object>> findById(String idThuoc) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (t:Thuoc {idThuoc: $idThuoc}) " +
                    "OPTIONAL MATCH (t)-[:CO_DON_VI_TINH]->(dvt:DonViTinh) " +
                    "OPTIONAL MATCH (t)-[:THUOC_DANH_MUC]->(dm:DanhMuc) " +
                    "OPTIONAL MATCH (t)-[:CO_XUAT_XU]->(xx:XuatXu) " +
                    "RETURN t, dvt, dm, xx";

            Result result = session.run(query, Values.parameters("idThuoc", idThuoc));

            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> thuoc = record.get("t").asMap();

                if (record.get("dvt") != null) {
                    thuoc.put("donViTinh", record.get("dvt").asMap());
                }

                if (record.get("dm") != null) {
                    thuoc.put("danhMuc", record.get("dm").asMap());
                }

                if (record.get("xx") != null) {
                    thuoc.put("xuatXu", record.get("xx").asMap());
                }

                return Optional.of(thuoc);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding medicine by ID", e);
        }

        return Optional.empty();
    }

    public List<Map<String, Object>> findByName(String tenThuoc) {
        List<Map<String, Object>> thuocList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (t:Thuoc) " +
                    "WHERE t.tenThuoc CONTAINS $tenThuoc " +
                    "OPTIONAL MATCH (t)-[:CO_DON_VI_TINH]->(dvt:DonViTinh) " +
                    "OPTIONAL MATCH (t)-[:THUOC_DANH_MUC]->(dm:DanhMuc) " +
                    "OPTIONAL MATCH (t)-[:CO_XUAT_XU]->(xx:XuatXu) " +
                    "RETURN t, dvt, dm, xx";

            Result result = session.run(query, Values.parameters("tenThuoc", tenThuoc));

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> thuoc = record.get("t").asMap();

                if (record.get("dvt") != null) {
                    thuoc.put("donViTinh", record.get("dvt").asMap());
                }

                if (record.get("dm") != null) {
                    thuoc.put("danhMuc", record.get("dm").asMap());
                }

                if (record.get("xx") != null) {
                    thuoc.put("xuatXu", record.get("xx").asMap());
                }

                thuocList.add(thuoc);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding medicines by name", e);
        }

        return thuocList;
    }

    public List<Map<String, Object>> search(String keyword) {
        List<Map<String, Object>> thuocList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (t:Thuoc) " +
                    "WHERE t.idThuoc CONTAINS $keyword " +
                    "OR t.tenThuoc CONTAINS $keyword " +
                    "OR t.thanhPhan CONTAINS $keyword " +
                    "OPTIONAL MATCH (t)-[:CO_DON_VI_TINH]->(dvt:DonViTinh) " +
                    "OPTIONAL MATCH (t)-[:THUOC_DANH_MUC]->(dm:DanhMuc) " +
                    "OPTIONAL MATCH (t)-[:CO_XUAT_XU]->(xx:XuatXu) " +
                    "RETURN t, dvt, dm, xx";

            Result result = session.run(query, Values.parameters("keyword", keyword));

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> thuoc = record.get("t").asMap();

                if (record.get("dvt") != null) {
                    thuoc.put("donViTinh", record.get("dvt").asMap());
                }

                if (record.get("dm") != null) {
                    thuoc.put("danhMuc", record.get("dm").asMap());
                }

                if (record.get("xx") != null) {
                    thuoc.put("xuatXu", record.get("xx").asMap());
                }

                thuocList.add(thuoc);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching medicines", e);
        }

        return thuocList;
    }

    public boolean save(Map<String, Object> thuocData) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            // Tạo ID mới nếu chưa có
            if (!thuocData.containsKey("idThuoc") || thuocData.get("idThuoc") == null) {
                String newId = generateNewId();
                thuocData.put("idThuoc", newId);
            }

            String idDVT = (String) thuocData.get("idDVT");
            String idDM = (String) thuocData.get("idDM");
            String idXX = (String) thuocData.get("idXX");

            // Tạo thuốc và mối quan hệ
            String query = "CREATE (t:Thuoc {idThuoc: $idThuoc, tenThuoc: $tenThuoc, hinhAnh: $hinhAnh, " +
                    "thanhPhan: $thanhPhan, idDVT: $idDVT, idDM: $idDM, idXX: $idXX, " +
                    "soLuongTon: $soLuongTon, giaNhap: $giaNhap, donGia: $donGia, hanSuDung: date($hanSuDung)}) " +
                    "WITH t " +
                    "MATCH (dvt:DonViTinh {idDVT: $idDVT}) " +
                    "MATCH (dm:DanhMuc {idDM: $idDM}) " +
                    "MATCH (xx:XuatXu {idXX: $idXX}) " +
                    "CREATE (t)-[:CO_DON_VI_TINH]->(dvt) " +
                    "CREATE (t)-[:THUOC_DANH_MUC]->(dm) " +
                    "CREATE (t)-[:CO_XUAT_XU]->(xx) " +
                    "RETURN t";

            Result result = session.run(query, Values.parameters(
                    "idThuoc", thuocData.get("idThuoc"),
                    "tenThuoc", thuocData.get("tenThuoc"),
                    "hinhAnh", thuocData.getOrDefault("hinhAnh", "default.jpg"),
                    "thanhPhan", thuocData.get("thanhPhan"),
                    "idDVT", idDVT,
                    "idDM", idDM,
                    "idXX", idXX,
                    "soLuongTon", thuocData.get("soLuongTon"),
                    "giaNhap", thuocData.get("giaNhap"),
                    "donGia", thuocData.get("donGia"),
                    "hanSuDung", thuocData.get("hanSuDung")
            ));

            return result.hasNext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving medicine", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> thuocData) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String idThuoc = (String) thuocData.get("idThuoc");
            String idDVT = (String) thuocData.get("idDVT");
            String idDM = (String) thuocData.get("idDM");
            String idXX = (String) thuocData.get("idXX");

            // Cập nhật thuốc và mối quan hệ
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("MATCH (t:Thuoc {idThuoc: $idThuoc}) ");

            // Cập nhật thuộc tính
            queryBuilder.append("SET t.tenThuoc = $tenThuoc, ");
            queryBuilder.append("t.hinhAnh = $hinhAnh, ");
            queryBuilder.append("t.thanhPhan = $thanhPhan, ");
            queryBuilder.append("t.idDVT = $idDVT, ");
            queryBuilder.append("t.idDM = $idDM, ");
            queryBuilder.append("t.idXX = $idXX, ");
            queryBuilder.append("t.soLuongTon = $soLuongTon, ");
            queryBuilder.append("t.giaNhap = $giaNhap, ");
            queryBuilder.append("t.donGia = $donGia, ");
            queryBuilder.append("t.hanSuDung = date($hanSuDung) ");

            // Xóa mối quan hệ cũ
            queryBuilder.append("WITH t ");
            queryBuilder.append("OPTIONAL MATCH (t)-[r:CO_DON_VI_TINH]->() DELETE r ");
            queryBuilder.append("WITH t ");
            queryBuilder.append("OPTIONAL MATCH (t)-[r:THUOC_DANH_MUC]->() DELETE r ");
            queryBuilder.append("WITH t ");
            queryBuilder.append("OPTIONAL MATCH (t)-[r:CO_XUAT_XU]->() DELETE r ");

            // Tạo mối quan hệ mới
            queryBuilder.append("WITH t ");
            queryBuilder.append("MATCH (dvt:DonViTinh {idDVT: $idDVT}) ");
            queryBuilder.append("MATCH (dm:DanhMuc {idDM: $idDM}) ");
            queryBuilder.append("MATCH (xx:XuatXu {idXX: $idXX}) ");
            queryBuilder.append("CREATE (t)-[:CO_DON_VI_TINH]->(dvt) ");
            queryBuilder.append("CREATE (t)-[:THUOC_DANH_MUC]->(dm) ");
            queryBuilder.append("CREATE (t)-[:CO_XUAT_XU]->(xx) ");
            queryBuilder.append("RETURN t");

            Result result = session.run(queryBuilder.toString(), Values.parameters(
                    "idThuoc", idThuoc,
                    "tenThuoc", thuocData.get("tenThuoc"),
                    "hinhAnh", thuocData.getOrDefault("hinhAnh", "default.jpg"),
                    "thanhPhan", thuocData.get("thanhPhan"),
                    "idDVT", idDVT,
                    "idDM", idDM,
                    "idXX", idXX,
                    "soLuongTon", thuocData.get("soLuongTon"),
                    "giaNhap", thuocData.get("giaNhap"),
                    "donGia", thuocData.get("donGia"),
                    "hanSuDung", thuocData.get("hanSuDung")
            ));

            return result.hasNext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating medicine", e);
            return false;
        }
    }

    public boolean delete(String idThuoc) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (t:Thuoc {idThuoc: $idThuoc}) " +
                    "OPTIONAL MATCH (t)-[r]-() " +
                    "DELETE r, t";

            session.run(query, Values.parameters("idThuoc", idThuoc));

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting medicine", e);
            return false;
        }
    }

    public String generateNewId() {
        List<Thuoc> thuocList = findAll();
        int maxId = 1;

        for (Thuoc thuoc : thuocList) {
            String idStr = thuoc.getIdThuoc().substring(1);
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return String.format("T%03d", maxId + 1);
    }
}