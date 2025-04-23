package iuh.fit.dao;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.ChiTietPhieuNhap;
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

public class ChiTietPhieuNhapDAO {
    private static final Logger LOGGER = Logger.getLogger(ChiTietPhieuNhapDAO.class.getName());

    public List<Map<String, Object>> findByPhieuNhap(String idPN) {
        List<Map<String, Object>> chiTietList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc) "
                    + "OPTIONAL MATCH (t)-[:CO_DON_VI_TINH]->(dvt:DonViTinh) "
                    + "RETURN pn, r, t, dvt";

            Result result = session.run(query, Values.parameters("idPN", idPN));
            while (result.hasNext()) {
                Record record = result.next();

                Map<String, Object> chiTietMap = new HashMap<>();
                chiTietMap.put("idPN", idPN);
                chiTietMap.put("idThuoc", record.get("t").asNode().get("idThuoc").asString());
                chiTietMap.put("soLuong", record.get("r").asRelationship().get("soLuong").asInt());
                chiTietMap.put("donGia", record.get("r").asRelationship().get("donGia").asDouble());

                // Thêm thông tin thuốc
                Map<String, Object> thuocMap = record.get("t").asNode().asMap();
                chiTietMap.put("thuoc", thuocMap);

                // Thêm thông tin đơn vị tính nếu có
                if (record.get("dvt") != null && !record.get("dvt").isNull()) {
                    Map<String, Object> dvtMap = record.get("dvt").asNode().asMap();
                    chiTietMap.put("donViTinh", dvtMap);
                }

                chiTietList.add(chiTietMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding ChiTietPhieuNhap by PhieuNhap ID: " + idPN, e);
        }

        return chiTietList;
    }

    public boolean add(String idPN, Map<String, Object> chiTietData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idThuoc = (String) chiTietData.get("idThuoc");
                int soLuong = Integer.parseInt(chiTietData.get("soLuong").toString());
                double donGia = Double.parseDouble(chiTietData.get("donGia").toString());

                // Kiểm tra xem chi tiết đã tồn tại chưa
                String checkQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                        + "RETURN r";
                Result checkResult = tx.run(checkQuery, Values.parameters("idPN", idPN, "idThuoc", idThuoc));

                if (checkResult.hasNext()) {
                    // Nếu đã tồn tại, cập nhật số lượng
                    String updateQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET r.soLuong = $soLuong, r.donGia = $donGia "
                            + "RETURN r";

                    Result updateResult = tx.run(updateQuery, Values.parameters(
                            "idPN", idPN,
                            "idThuoc", idThuoc,
                            "soLuong", soLuong,
                            "donGia", donGia
                    ));

                    return updateResult.hasNext();
                } else {
                    // Nếu chưa tồn tại, tạo mới
                    String createQuery = "MATCH (pn:PhieuNhap {idPN: $idPN}) "
                            + "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                            + "CREATE (pn)-[r:CO_CHI_TIET {soLuong: $soLuong, donGia: $donGia}]->(t) "
                            + "RETURN r";

                    Result createResult = tx.run(createQuery, Values.parameters(
                            "idPN", idPN,
                            "idThuoc", idThuoc,
                            "soLuong", soLuong,
                            "donGia", donGia
                    ));

                    // Cập nhật số lượng tồn của thuốc
                    String updateThuocQuery = "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET t.soLuongTon = t.soLuongTon + $soLuong "
                            + "RETURN t";

                    tx.run(updateThuocQuery, Values.parameters(
                            "idThuoc", idThuoc,
                            "soLuong", soLuong
                    ));

                    return createResult.hasNext();
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding ChiTietPhieuNhap", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> chiTietData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idPN = (String) chiTietData.get("idPN");
                String idThuoc = (String) chiTietData.get("idThuoc");
                int soLuong = Integer.parseInt(chiTietData.get("soLuong").toString());
                double donGia = Double.parseDouble(chiTietData.get("donGia").toString());

                // Lấy số lượng hiện tại để tính toán sự thay đổi
                String getCurrentQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                        + "RETURN r.soLuong AS currentSoLuong";

                Result getCurrentResult = tx.run(getCurrentQuery, Values.parameters("idPN", idPN, "idThuoc", idThuoc));

                if (getCurrentResult.hasNext()) {
                    int currentSoLuong = getCurrentResult.next().get("currentSoLuong").asInt();
                    int soLuongChange = soLuong - currentSoLuong;

                    // Cập nhật chi tiết phiếu nhập
                    String updateQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET r.soLuong = $soLuong, r.donGia = $donGia "
                            + "RETURN r";

                    Result updateResult = tx.run(updateQuery, Values.parameters(
                            "idPN", idPN,
                            "idThuoc", idThuoc,
                            "soLuong", soLuong,
                            "donGia", donGia
                    ));

                    // Cập nhật số lượng tồn của thuốc
                    if (soLuongChange != 0) {
                        String updateThuocQuery = "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                                + "SET t.soLuongTon = t.soLuongTon + $soLuongChange "
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
            LOGGER.log(Level.SEVERE, "Error updating ChiTietPhieuNhap", e);
            return false;
        }
    }

    public boolean delete(String idPN, String idThuoc) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                // Lấy số lượng hiện tại để cập nhật số lượng tồn
                String getCurrentQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                        + "RETURN r.soLuong AS soLuong";

                Result getCurrentResult = tx.run(getCurrentQuery, Values.parameters("idPN", idPN, "idThuoc", idThuoc));

                if (getCurrentResult.hasNext()) {
                    int soLuong = getCurrentResult.next().get("soLuong").asInt();

                    // Xóa chi tiết phiếu nhập
                    String deleteQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->(t:Thuoc {idThuoc: $idThuoc}) "
                            + "DELETE r";

                    tx.run(deleteQuery, Values.parameters("idPN", idPN, "idThuoc", idThuoc));

                    // Cập nhật số lượng tồn của thuốc
                    String updateThuocQuery = "MATCH (t:Thuoc {idThuoc: $idThuoc}) "
                            + "SET t.soLuongTon = t.soLuongTon - $soLuong "
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
            LOGGER.log(Level.SEVERE, "Error deleting ChiTietPhieuNhap", e);
            return false;
        }
    }

    public Map<String, Object> toMap(ChiTietPhieuNhap chiTiet) {
        Map<String, Object> map = new HashMap<>();
        map.put("idPN", chiTiet.getIdPN());
        map.put("idThuoc", chiTiet.getIdThuoc());
        map.put("soLuong", chiTiet.getSoLuong());
        map.put("donGia", chiTiet.getDonGia());
        return map;
    }

    public ChiTietPhieuNhap fromMap(Map<String, Object> map) {
        ChiTietPhieuNhap chiTiet = new ChiTietPhieuNhap();
        chiTiet.setIdPN((String) map.get("idPN"));
        chiTiet.setIdThuoc((String) map.get("idThuoc"));
        chiTiet.setSoLuong(Integer.parseInt(map.get("soLuong").toString()));
        chiTiet.setDonGia(Double.parseDouble(map.get("donGia").toString()));
        return chiTiet;
    }
}