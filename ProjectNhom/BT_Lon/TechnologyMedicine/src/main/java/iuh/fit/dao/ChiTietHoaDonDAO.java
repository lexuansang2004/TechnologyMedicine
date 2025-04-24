package iuh.fit.dao;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.ChiTietHoaDon;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChiTietHoaDonDAO {
    private static final Logger LOGGER = Logger.getLogger(ChiTietHoaDonDAO.class.getName());

    public List<Map<String, Object>> findByHoaDon(String idHD) {
        List<Map<String, Object>> chiTietList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (hd:HoaDon)-[r:CO_CHI_TIET]->(t:Thuoc) " +
                    "WHERE hd.idHD = $idHD " +
                    "OPTIONAL MATCH (t)-[:CO_DON_VI_TINH]->(dvt:DonViTinh) " +
                    "RETURN hd, r, t, dvt";

            Result result = session.run(query, Values.parameters("idHD", idHD));
            while (result.hasNext()) {
                Record record = result.next();

                try {
                    System.out.println("Found record: " + record);

                    Map<String, Object> chiTietMap = new HashMap<>();
                    chiTietMap.put("idHD", idHD);

                    // Lấy thông tin thuốc an toàn
                    if (record.containsKey("t") && !record.get("t").isNull()) {
                        var thuocNode = record.get("t").asNode();

                        // Tránh lỗi nếu không có idThuoc
                        if (thuocNode.containsKey("idThuoc")) {
                            chiTietMap.put("idThuoc", thuocNode.get("idThuoc").asString());
                        } else {
                            System.out.println("Node thuốc không có idThuoc!");
                            continue;
                        }

                        Map<String, Object> thuocMap = new HashMap<>();

                        thuocNode.keys().forEach(key -> {
                            try {
                                var val = thuocNode.get(key);
                                // Nếu là datetime thì xử lý riêng
                                if (val.hasType(org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM.DATE_TIME())) {
                                    thuocMap.put(key, val.asZonedDateTime().toString()); // hoặc val.toString()
                                } else {
                                    thuocMap.put(key, val.asObject());
                                }
                            } catch (Exception ex) {
                                System.out.println("Lỗi khi xử lý field " + key + ": " + ex.getMessage());
                            }
                        });
                        chiTietMap.put("thuoc", thuocMap);
                    } else {
                        System.out.println("Không có node t (Thuoc) trong record!");
                        continue;
                    }

                    // Relationship
                    if (record.containsKey("r") && !record.get("r").isNull()) {
                        var r = record.get("r").asRelationship();

                        chiTietMap.put("soLuong", r.get("soLuong").asInt());
                        chiTietMap.put("donGia", r.get("donGia").asDouble());
                    } else {
                        System.out.println("Không có relationship r!");
                        continue;
                    }

                    // DVT nếu có
                    if (record.containsKey("dvt") && !record.get("dvt").isNull()) {
                        chiTietMap.put("donViTinh", record.get("dvt").asNode().asMap());
                    }

                    chiTietList.add(chiTietMap);
                } catch (Exception ex) {
                    System.err.println("Lỗi khi xử lý 1 record chi tiết: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

            return chiTietList;
    }

    public boolean add(String idHD, Map<String, Object> chiTietData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idThuoc = (String) chiTietData.get("idThuoc");
                int soLuong = Integer.parseInt(chiTietData.get("soLuong").toString());
                double donGia = Double.parseDouble(chiTietData.get("donGia").toString());

                // Kiểm tra xem chi tiết đã tồn tại chưa
                String checkQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                        + "RETURN r";
                Result checkResult = tx.run(checkQuery, Values.parameters("idHD", idHD, "idThuoc", idThuoc));

                if (checkResult.hasNext()) {
                    // Nếu đã tồn tại, cập nhật số lượng
                    String updateQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET r.soLuong = $soLuong, r.donGia = $donGia "
                            + "RETURN r";

                    Result updateResult = tx.run(updateQuery, Values.parameters(
                            "idHD", idHD,
                            "idThuoc", idThuoc,
                            "soLuong", soLuong,
                            "donGia", donGia
                    ));

                    return updateResult.hasNext();
                } else {
                    // Nếu chưa tồn tại, tạo mới
                    String createQuery = "MATCH (hd:HoaDon {idHD: $idHD}) "
                            + "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                            + "CREATE (hd)-[r:CO_CHI_TIET {soLuong: $soLuong, donGia: $donGia}]->(t) "
                            + "RETURN r";

                    Result createResult = tx.run(createQuery, Values.parameters(
                            "idHD", idHD,
                            "idThuoc", idThuoc,
                            "soLuong", soLuong,
                            "donGia", donGia
                    ));

                    // Cập nhật số lượng tồn của thuốc
                    String updateThuocQuery = "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET t.soLuongTon = t.soLuongTon - $soLuong "
                            + "RETURN t";

                    tx.run(updateThuocQuery, Values.parameters(
                            "idThuoc", idThuoc,
                            "soLuong", soLuong
                    ));

                    return createResult.hasNext();
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding ChiTietHoaDon", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> chiTietData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idHD = (String) chiTietData.get("idHD");
                String idThuoc = (String) chiTietData.get("idThuoc");
                int soLuong = Integer.parseInt(chiTietData.get("soLuong").toString());
                double donGia = Double.parseDouble(chiTietData.get("donGia").toString());

                // Lấy số lượng hiện tại để tính toán sự thay đổi
                String getCurrentQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                        + "RETURN r.soLuong AS currentSoLuong";

                Result getCurrentResult = tx.run(getCurrentQuery, Values.parameters("idHD", idHD, "idThuoc", idThuoc));

                if (getCurrentResult.hasNext()) {
                    int currentSoLuong = getCurrentResult.next().get("currentSoLuong").asInt();
                    int soLuongChange = soLuong - currentSoLuong;

                    // Cập nhật chi tiết hóa đơn
                    String updateQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET r.soLuong = $soLuong, r.donGia = $donGia "
                            + "RETURN r";

                    Result updateResult = tx.run(updateQuery, Values.parameters(
                            "idHD", idHD,
                            "idThuoc", idThuoc,
                            "soLuong", soLuong,
                            "donGia", donGia
                    ));

                    // Cập nhật số lượng tồn của thuốc
                    if (soLuongChange != 0) {
                        String updateThuocQuery = "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                                + "SET t.soLuongTon = t.soLuongTon - $soLuongChange "
                                + "RETURN t";

                        tx.run(updateThuocQuery, Values.parameters(
                                "idThuoc", idThuoc,
                                "soLuongChange", soLuongChange
                        ));
                    }

                    return updateResult.hasNext();
                }

                return false;
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating ChiTietHoaDon", e);
            return false;
        }
    }

    public boolean delete(String idHD, String idThuoc) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                // Lấy số lượng hiện tại để hoàn trả vào số lượng tồn
                String getCurrentQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                        + "RETURN r.soLuong AS soLuong";

                Result getCurrentResult = tx.run(getCurrentQuery, Values.parameters("idHD", idHD, "idThuoc", idThuoc));

                if (getCurrentResult.hasNext()) {
                    int soLuong = getCurrentResult.next().get("soLuong").asInt();

                    // Xóa chi tiết hóa đơn
                    String deleteQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                            + "DELETE r";

                    tx.run(deleteQuery, Values.parameters("idHD", idHD, "idThuoc", idThuoc));

                    // Cập nhật số lượng tồn của thuốc
                    String updateThuocQuery = "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET t.soLuongTon = t.soLuongTon + $soLuong "
                            + "RETURN t";

                    tx.run(updateThuocQuery, Values.parameters(
                            "idThuoc", idThuoc,
                            "soLuong", soLuong
                    ));

                    return true;
                }

                return false;
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting ChiTietHoaDon", e);
            return false;
        }
    }

    public Map<String, Object> toMap(ChiTietHoaDon chiTiet) {
        Map<String, Object> map = new HashMap<>();
        map.put("idHD", chiTiet.getIdHD());
        map.put("idThuoc", chiTiet.getIdThuoc());
        map.put("soLuong", chiTiet.getSoLuong());
        map.put("donGia", chiTiet.getDonGia());
        return map;
    }

    public ChiTietHoaDon fromMap(Map<String, Object> map) {
        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
        chiTiet.setIdHD((String) map.get("idHD"));
        chiTiet.setIdThuoc((String) map.get("idThuoc"));
        chiTiet.setSoLuong(Integer.parseInt(map.get("soLuong").toString()));
        chiTiet.setDonGia(Double.parseDouble(map.get("donGia").toString()));
        return chiTiet;
    }
}