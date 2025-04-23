package iuh.fit.dao;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.PhieuNhap;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhieuNhapDAO {
    private static final Logger LOGGER = Logger.getLogger(PhieuNhapDAO.class.getName());

//    public List<Map<String, Object>> findAll() {
//        List<Map<String, Object>> phieuNhapList = new ArrayList<>();
//
//        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
//            String query = "MATCH (pn:PhieuNhap) "
//                    + "OPTIONAL MATCH (pn)-[:TU_NHA_CUNG_CAP]->(ncc:NhaCungCap) "
//                    + "OPTIONAL MATCH (pn)-[:DO_NHAN_VIEN]->(nv:NhanVien) "
//                    + "RETURN pn, ncc, nv ORDER BY pn.thoiGian DESC";
//
//            Result result = session.run(query);
//            while (result.hasNext()) {
//                Record record = result.next();
//
//                // Lấy node PhieuNhap
//                Node pnNode = record.get("pn").asNode();
//                Map<String, Object> phieuNhapMap = new HashMap<>();
//
//                // Lấy các thuộc tính cơ bản
//                phieuNhapMap.put("idPN", pnNode.get("idPN").asString());
//                phieuNhapMap.put("tongTien", pnNode.get("tongTien").asDouble());
//
//                // Xử lý thoiGian - sử dụng try-catch để xử lý các kiểu dữ liệu khác nhau
//                try {
//                    // Thử chuyển đổi thành LocalDateTime
//                    LocalDateTime thoiGian = pnNode.get("thoiGian").asLocalDateTime();
//                    phieuNhapMap.put("thoiGian", thoiGian);
//                } catch (Exception e1) {
//                    try {
//                        // Nếu không phải LocalDateTime, thử chuyển đổi thành String
//                        String thoiGianStr = pnNode.get("thoiGian").asString();
//                        phieuNhapMap.put("thoiGian", thoiGianStr);
//                    } catch (Exception e2) {
//                        // Nếu vẫn không được, sử dụng toString()
//                        try {
//                            phieuNhapMap.put("thoiGian", pnNode.get("thoiGian").toString());
//                        } catch (Exception e3) {
//                            LOGGER.log(Level.WARNING, "Không thể xử lý thoiGian: " + e3.getMessage());
//                            // Đặt giá trị mặc định
//                            phieuNhapMap.put("thoiGian", LocalDateTime.now());
//                        }
//                    }
//                }
//
//                // Thêm thông tin nhà cung cấp nếu có
//                if (record.get("ncc") != null && !record.get("ncc").isNull()) {
//                    Node nccNode = record.get("ncc").asNode();
//                    Map<String, Object> nhaCungCapMap = new HashMap<>();
//                    nhaCungCapMap.put("idNCC", nccNode.get("idNCC").asString());
//                    nhaCungCapMap.put("tenNCC", nccNode.get("tenNCC").asString());
//                    phieuNhapMap.put("nhaCungCap", nhaCungCapMap);
//                }
//
//                // Thêm thông tin nhân viên nếu có
//                if (record.get("nv") != null && !record.get("nv").isNull()) {
//                    Node nvNode = record.get("nv").asNode();
//                    Map<String, Object> nhanVienMap = new HashMap<>();
//                    nhanVienMap.put("idNV", nvNode.get("idNV").asString());
//                    nhanVienMap.put("hoTen", nvNode.get("hoTen").asString());
//                    phieuNhapMap.put("nhanVien", nhanVienMap);
//                }
//
//                phieuNhapList.add(phieuNhapMap);
//            }
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error finding all PhieuNhap", e);
//            e.printStackTrace(); // Thêm dòng này để xem lỗi chi tiết
//        }
//
//        return phieuNhapList;
//    }
public List<Map<String, Object>> findAll() {
    List<Map<String, Object>> phieuNhapList = new ArrayList<>();

    // Use getSession() instead of getDriver().session()
    try (Session session = Neo4jConfig.getInstance().getSession()) {
        // Check if PhieuNhap nodes exist
        String countQuery = "MATCH (pn:PhieuNhap) RETURN count(pn) as count";
        Result countResult = session.run(countQuery);
        Record countRecord = countResult.single();
        long count = countRecord.get("count").asLong();
        System.out.println("Found " + count + " PhieuNhap nodes in the database");

        // Use the standard query to get PhieuNhap nodes
        String query = "MATCH (pn:PhieuNhap) RETURN pn";
        System.out.println("Executing Cypher query: " + query);

        Result result = session.run(query);
        while (result.hasNext()) {
            Record record = result.next();
            Node pnNode = record.get("pn").asNode();

            // Print node details for debugging
            System.out.println("Found PhieuNhap node: " + pnNode.id() + " with labels: " + pnNode.labels());
            System.out.println("Node properties: " + pnNode.asMap());

            // Create map from node properties
            Map<String, Object> phieuNhapMap = new HashMap<>();

            // Get basic properties
            try {
                phieuNhapMap.put("idPN", pnNode.get("idPN").asString());
            } catch (Exception e) {
                System.out.println("Error getting idPN: " + e.getMessage());
                continue; // Skip this node if it doesn't have idPN
            }

            // Handle tongTien
            try {
                phieuNhapMap.put("tongTien", pnNode.get("tongTien").asDouble());
            } catch (Exception e) {
                phieuNhapMap.put("tongTien", 0.0);
                System.out.println("Error getting tongTien: " + e.getMessage());
            }

            // Handle thoiGian
            try {
                // Try different formats based on your data
                try {
                    // If stored as a datetime object
                    phieuNhapMap.put("thoiGian", pnNode.get("thoiGian").asLocalDateTime().toString());
                } catch (Exception e1) {
                    // If stored as a string
                    phieuNhapMap.put("thoiGian", pnNode.get("thoiGian").asString());
                }
            } catch (Exception e) {
                phieuNhapMap.put("thoiGian", LocalDateTime.now().toString());
                System.out.println("Error getting thoiGian: " + e.getMessage());
            }

            // Get supplier and employee info from IDs
            try {
                String idNCC = pnNode.get("idNCC").asString();
                Map<String, Object> nhaCungCapMap = new HashMap<>();
                nhaCungCapMap.put("idNCC", idNCC);
                nhaCungCapMap.put("tenNCC", "NCC: " + idNCC);
                phieuNhapMap.put("nhaCungCap", nhaCungCapMap);
            } catch (Exception e) {
                System.out.println("Error getting idNCC: " + e.getMessage());
            }

            try {
                String idNV = pnNode.get("idNV").asString();
                Map<String, Object> nhanVienMap = new HashMap<>();
                nhanVienMap.put("idNV", idNV);
                nhanVienMap.put("hoTen", "NV: " + idNV);
                phieuNhapMap.put("nhanVien", nhanVienMap);
            } catch (Exception e) {
                System.out.println("Error getting idNV: " + e.getMessage());
            }

            phieuNhapList.add(phieuNhapMap);
        }

        System.out.println("PhieuNhapDAO.findAll() found " + phieuNhapList.size() + " records");
        if (!phieuNhapList.isEmpty()) {
            System.out.println("First record: " + phieuNhapList.get(0));
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error finding all PhieuNhap", e);
        e.printStackTrace();
    }

    return phieuNhapList;
}

    public Optional<Map<String, Object>> findById(String idPN) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (pn:PhieuNhap {idPN: $idPN}) "
                    + "OPTIONAL MATCH (pn)-[:TU_NHA_CUNG_CAP]->(ncc:NhaCungCap) "
                    + "OPTIONAL MATCH (pn)-[:DO_NHAN_VIEN]->(nv:NhanVien) "
                    + "RETURN pn, ncc, nv";

            Result result = session.run(query, Values.parameters("idPN", idPN));
            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> phieuNhapMap = record.get("pn").asNode().asMap();

                // Thêm thông tin nhà cung cấp nếu có
                if (record.get("ncc") != null && !record.get("ncc").isNull()) {
                    Map<String, Object> nhaCungCapMap = record.get("ncc").asNode().asMap();
                    phieuNhapMap.put("nhaCungCap", nhaCungCapMap);
                }

                // Thêm thông tin nhân viên nếu có
                if (record.get("nv") != null && !record.get("nv").isNull()) {
                    Map<String, Object> nhanVienMap = record.get("nv").asNode().asMap();
                    phieuNhapMap.put("nhanVien", nhanVienMap);
                }

                return Optional.of(phieuNhapMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding PhieuNhap by ID: " + idPN, e);
        }

        return Optional.empty();
    }

    public List<Map<String, Object>> findByNhaCungCap(String idNCC) {
        List<Map<String, Object>> phieuNhapList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (pn:PhieuNhap)-[:TU_NHA_CUNG_CAP]->(ncc:NhaCungCap {idNCC: $idNCC}) "
                    + "OPTIONAL MATCH (pn)-[:DO_NHAN_VIEN]->(nv:NhanVien) "
                    + "RETURN pn, ncc, nv ORDER BY pn.thoiGian DESC";

            Result result = session.run(query, Values.parameters("idNCC", idNCC));
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> phieuNhapMap = record.get("pn").asNode().asMap();

                // Thêm thông tin nhà cung cấp
                Map<String, Object> nhaCungCapMap = record.get("ncc").asNode().asMap();
                phieuNhapMap.put("nhaCungCap", nhaCungCapMap);

                // Thêm thông tin nhân viên nếu có
                if (record.get("nv") != null && !record.get("nv").isNull()) {
                    Map<String, Object> nhanVienMap = record.get("nv").asNode().asMap();
                    phieuNhapMap.put("nhanVien", nhanVienMap);
                }

                phieuNhapList.add(phieuNhapMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding PhieuNhap by NhaCungCap ID: " + idNCC, e);
        }

        return phieuNhapList;
    }

    public List<Map<String, Object>> findByNhanVien(String idNV) {
        List<Map<String, Object>> phieuNhapList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (pn:PhieuNhap)-[:DO_NHAN_VIEN]->(nv:NhanVien {idNV: $idNV}) "
                    + "OPTIONAL MATCH (pn)-[:TU_NHA_CUNG_CAP]->(ncc:NhaCungCap) "
                    + "RETURN pn, ncc, nv ORDER BY pn.thoiGian DESC";

            Result result = session.run(query, Values.parameters("idNV", idNV));
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> phieuNhapMap = record.get("pn").asNode().asMap();

                // Thêm thông tin nhà cung cấp nếu có
                if (record.get("ncc") != null && !record.get("ncc").isNull()) {
                    Map<String, Object> nhaCungCapMap = record.get("ncc").asNode().asMap();
                    phieuNhapMap.put("nhaCungCap", nhaCungCapMap);
                }

                // Thêm thông tin nhân viên
                Map<String, Object> nhanVienMap = record.get("nv").asNode().asMap();
                phieuNhapMap.put("nhanVien", nhanVienMap);

                phieuNhapList.add(phieuNhapMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding PhieuNhap by NhanVien ID: " + idNV, e);
        }

        return phieuNhapList;
    }

    public boolean save(Map<String, Object> phieuNhapData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idPN = (String) phieuNhapData.get("idPN");
                String idNCC = (String) phieuNhapData.get("idNCC");
                String idNV = (String) phieuNhapData.get("idNV");
                double tongTien = Double.parseDouble(phieuNhapData.get("tongTien").toString());
                LocalDateTime thoiGian = (LocalDateTime) phieuNhapData.get("thoiGian");

                String query = "CREATE (pn:PhieuNhap {idPN: $idPN, idNCC: $idNCC, idNV: $idNV, tongTien: $tongTien, thoiGian: $thoiGian}) "
                        + "WITH pn "
                        + "MATCH (ncc:NhaCungCap {idNCC: $idNCC}) "
                        + "MATCH (nv:NhanVien {idNV: $idNV}) "
                        + "CREATE (pn)-[:TU_NHA_CUNG_CAP]->(ncc) "
                        + "CREATE (pn)-[:DO_NHAN_VIEN]->(nv) "
                        + "RETURN pn";

                Result result = tx.run(query, Values.parameters(
                        "idPN", idPN,
                        "idNCC", idNCC,
                        "idNV", idNV,
                        "tongTien", tongTien,
                        "thoiGian", thoiGian
                ));

                return result.hasNext();
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving PhieuNhap", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> phieuNhapData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idPN = (String) phieuNhapData.get("idPN");
                String idNCC = (String) phieuNhapData.get("idNCC");
                String idNV = (String) phieuNhapData.get("idNV");
                double tongTien = Double.parseDouble(phieuNhapData.get("tongTien").toString());

                // Cập nhật thông tin phiếu nhập
                String updateQuery = "MATCH (pn:PhieuNhap {idPN: $idPN}) "
                        + "SET pn.idNCC = $idNCC, pn.idNV = $idNV, pn.tongTien = $tongTien "
                        + "RETURN pn";

                Result updateResult = tx.run(updateQuery, Values.parameters(
                        "idPN", idPN,
                        "idNCC", idNCC,
                        "idNV", idNV,
                        "tongTien", tongTien
                ));

                if (!updateResult.hasNext()) {
                    return false;
                }

                // Cập nhật mối quan hệ với nhà cung cấp
                String updateNCCQuery = "MATCH (pn:PhieuNhap {idPN: $idPN}) "
                        + "OPTIONAL MATCH (pn)-[r1:TU_NHA_CUNG_CAP]->(:NhaCungCap) "
                        + "DELETE r1 "
                        + "WITH pn "
                        + "MATCH (ncc:NhaCungCap {idNCC: $idNCC}) "
                        + "CREATE (pn)-[:TU_NHA_CUNG_CAP]->(ncc) "
                        + "RETURN pn";

                tx.run(updateNCCQuery, Values.parameters("idPN", idPN, "idNCC", idNCC));

                // Cập nhật mối quan hệ với nhân viên
                String updateNVQuery = "MATCH (pn:PhieuNhap {idPN: $idPN}) "
                        + "OPTIONAL MATCH (pn)-[r2:DO_NHAN_VIEN]->(:NhanVien) "
                        + "DELETE r2 "
                        + "WITH pn "
                        + "MATCH (nv:NhanVien {idNV: $idNV}) "
                        + "CREATE (pn)-[:DO_NHAN_VIEN]->(nv) "
                        + "RETURN pn";

                tx.run(updateNVQuery, Values.parameters("idPN", idPN, "idNV", idNV));

                return true;
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating PhieuNhap", e);
            return false;
        }
    }

    public boolean delete(String idPN) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                // Xóa các chi tiết phiếu nhập trước
                String deleteChiTietQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r:CO_CHI_TIET]->() DELETE r";
                tx.run(deleteChiTietQuery, Values.parameters("idPN", idPN));

                // Xóa các mối quan hệ của phiếu nhập
                String deleteRelationsQuery = "MATCH (pn:PhieuNhap {idPN: $idPN})-[r]-() DELETE r";
                tx.run(deleteRelationsQuery, Values.parameters("idPN", idPN));

                // Xóa phiếu nhập
                String deletePhieuNhapQuery = "MATCH (pn:PhieuNhap {idPN: $idPN}) DELETE pn";
                Result result = tx.run(deletePhieuNhapQuery, Values.parameters("idPN", idPN));

                return true;
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting PhieuNhap", e);
            return false;
        }
    }

    public Map<String, Object> toMap(PhieuNhap phieuNhap) {
        Map<String, Object> map = new HashMap<>();
        map.put("idPN", phieuNhap.getIdPN());
        map.put("idNCC", phieuNhap.getIdNCC());
        map.put("idNV", phieuNhap.getIdNV());
        map.put("tongTien", phieuNhap.getTongTien());
        map.put("thoiGian", phieuNhap.getThoiGian());
        return map;
    }

    public PhieuNhap fromMap(Map<String, Object> map) {
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setIdPN((String) map.get("idPN"));
        phieuNhap.setIdNCC((String) map.get("idNCC"));
        phieuNhap.setIdNV((String) map.get("idNV"));
        phieuNhap.setTongTien(Double.parseDouble(map.get("tongTien").toString()));
        phieuNhap.setThoiGian((LocalDateTime) map.get("thoiGian"));
        return phieuNhap;
    }
}