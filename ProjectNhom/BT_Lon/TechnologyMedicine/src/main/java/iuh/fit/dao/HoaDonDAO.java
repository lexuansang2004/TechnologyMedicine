package iuh.fit.dao;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.HoaDon;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Values;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoaDonDAO {
    private static final Logger LOGGER = Logger.getLogger(HoaDonDAO.class.getName());

//    public List<Map<String, Object>> findAll() {
//        List<Map<String, Object>> hoaDonList = new ArrayList<>();
//
//        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
//            String query = "MATCH (hd:HoaDon) "
//                    + "OPTIONAL MATCH (hd)-[:CUA_KHACH_HANG]->(kh:KhachHang) "
//                    + "OPTIONAL MATCH (hd)-[:DO_NHAN_VIEN]->(nv:NhanVien) "
//                    + "RETURN hd, kh, nv ORDER BY hd.ngayLap DESC";
//
//            Result result = session.run(query);
//            while (result.hasNext()) {
//                Record record = result.next();
//
//                // Lấy node HoaDon
//                Node hdNode = record.get("hd").asNode();
//                Map<String, Object> hoaDonMap = new HashMap<>();
//
//                // Lấy các thuộc tính cơ bản
//                hoaDonMap.put("idHD", hdNode.get("idHD").asString());
//                hoaDonMap.put("tongTien", hdNode.get("tongTien").asDouble());
//
//                // Xử lý ngayLap - sử dụng try-catch để xử lý các kiểu dữ liệu khác nhau
//                try {
//                    // Thử chuyển đổi thành LocalDateTime
//                    LocalDateTime ngayLap = hdNode.get("ngayLap").asLocalDateTime();
//                    hoaDonMap.put("ngayLap", ngayLap);
//                } catch (Exception e1) {
//                    try {
//                        // Nếu không phải LocalDateTime, thử chuyển đổi thành String
//                        String ngayLapStr = hdNode.get("ngayLap").asString();
//                        hoaDonMap.put("ngayLap", ngayLapStr);
//                    } catch (Exception e2) {
//                        // Nếu vẫn không được, sử dụng toString()
//                        try {
//                            hoaDonMap.put("ngayLap", hdNode.get("ngayLap").toString());
//                        } catch (Exception e3) {
//                            LOGGER.log(Level.WARNING, "Không thể xử lý ngayLap: " + e3.getMessage());
//                            // Đặt giá trị mặc định
//                            hoaDonMap.put("ngayLap", LocalDateTime.now());
//                        }
//                    }
//                }
//
//                // Thêm thông tin khách hàng nếu có
//                if (record.get("kh") != null && !record.get("kh").isNull()) {
//                    Node khNode = record.get("kh").asNode();
//                    Map<String, Object> khachHangMap = new HashMap<>();
//                    khachHangMap.put("idKH", khNode.get("idKH").asString());
//                    khachHangMap.put("hoTen", khNode.get("hoTen").asString());
//                    hoaDonMap.put("khachHang", khachHangMap);
//                }
//
//                // Thêm thông tin nhân viên nếu có
//                if (record.get("nv") != null && !record.get("nv").isNull()) {
//                    Node nvNode = record.get("nv").asNode();
//                    Map<String, Object> nhanVienMap = new HashMap<>();
//                    nhanVienMap.put("idNV", nvNode.get("idNV").asString());
//                    nhanVienMap.put("hoTen", nvNode.get("hoTen").asString());
//                    hoaDonMap.put("nhanVien", nhanVienMap);
//                }
//
//                hoaDonList.add(hoaDonMap);
//            }
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error finding all HoaDon", e);
//            e.printStackTrace(); // Thêm dòng này để xem lỗi chi tiết
//        }
//
//        return hoaDonList;
//    }
public List<Map<String, Object>> findAll() {
    List<Map<String, Object>> hoaDonList = new ArrayList<>();

    // Use getSession() instead of getDriver().session()
    try (Session session = Neo4jConfig.getInstance().getSession()) {
        // First, let's check what labels exist in the database
        String countQuery = "MATCH (hd:HoaDon) RETURN count(hd) as count";
        Result countResult = session.run(countQuery);
        Record countRecord = countResult.single();
        long count = countRecord.get("count").asLong();
        System.out.println("Found " + count + " HoaDon nodes in the database");

        String query = "MATCH (hd:HoaDon) RETURN hd";
        System.out.println("Executing Cypher query: " + query);

        Result result = session.run(query);

        while (result.hasNext()) {
            Record record = result.next();
            Node hdNode = record.get("hd").asNode();

            // Print node details for debugging
            System.out.println("Found HoaDon node: " + hdNode.id() + " with labels: " + hdNode.labels());
            System.out.println("Node properties: " + hdNode.asMap());

            // Create map from node properties
            Map<String, Object> hoaDonMap = new HashMap<>();

            // Get basic properties
            try {
                hoaDonMap.put("idHD", hdNode.get("idHD").asString());
            } catch (Exception e) {
                System.out.println("Error getting idHD: " + e.getMessage());
                continue; // Skip this node if it doesn't have idHD
            }

            // Handle tongTien
            try {
                hoaDonMap.put("tongTien", hdNode.get("tongTien").asDouble());
            } catch (Exception e) {
                hoaDonMap.put("tongTien", 0.0);
                System.out.println("Error getting tongTien: " + e.getMessage());
            }

            // Handle ngayLap
            try {
                // Try different formats based on your data
                try {
                    // If stored as a datetime object
                    hoaDonMap.put("ngayLap", hdNode.get("ngayLap").asLocalDateTime().toString());
                } catch (Exception e1) {
                    // If stored as a string
                    hoaDonMap.put("ngayLap", hdNode.get("ngayLap").asString());
                }
            } catch (Exception e) {
                hoaDonMap.put("ngayLap", LocalDateTime.now().toString());
                System.out.println("Error getting ngayLap: " + e.getMessage());
            }

            // Get customer and employee info from IDs
            try {
                String idKH = hdNode.get("idKH").asString();
                Map<String, Object> khachHangMap = new HashMap<>();
                khachHangMap.put("idKH", idKH);
                khachHangMap.put("hoTen", "KH: " + idKH);
                hoaDonMap.put("khachHang", khachHangMap);
            } catch (Exception e) {
                System.out.println("Error getting idKH: " + e.getMessage());
            }

            try {
                String idNV = hdNode.get("idNV").asString();
                Map<String, Object> nhanVienMap = new HashMap<>();
                nhanVienMap.put("idNV", idNV);
                nhanVienMap.put("hoTen", "NV: " + idNV);
                hoaDonMap.put("nhanVien", nhanVienMap);
            } catch (Exception e) {
                System.out.println("Error getting idNV: " + e.getMessage());
            }

            hoaDonList.add(hoaDonMap);
        }

        System.out.println("HoaDonDAO.findAll() found " + hoaDonList.size() + " records");
        if (!hoaDonList.isEmpty()) {
            System.out.println("First record: " + hoaDonList.get(0));
        }
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error finding all HoaDon", e);
        e.printStackTrace();
    }

    return hoaDonList;
}

    public Optional<Map<String, Object>> findById(String idHD) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (hd:HoaDon {idHD: $idHD}) "
                    + "OPTIONAL MATCH (hd)-[:CUA_KHACH_HANG]->(kh:KhachHang) "
                    + "OPTIONAL MATCH (hd)-[:DO_NHAN_VIEN]->(nv:NhanVien) "
                    + "RETURN hd, kh, nv";

            Result result = session.run(query, Values.parameters("idHD", idHD));
            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> hoaDonMap = new HashMap<>();
                hoaDonMap.put("idHD", record.get("hd").asNode().get("idHD").asString());
                hoaDonMap.put("tongTien", record.get("hd").asNode().get("tongTien").asDouble());
                hoaDonMap.put("ngayLap", record.get("hd").asNode().get("ngayLap").asString());

                // Thêm thông tin khách hàng nếu có
                if (record.get("kh") != null && !record.get("kh").isNull()) {
                    Map<String, Object> khachHangMap = new HashMap<>();
                    khachHangMap.put("idKH", record.get("kh").asNode().get("idKH").asString());
                    khachHangMap.put("hoTen", record.get("kh").asNode().get("hoTen").asString());
                    hoaDonMap.put("khachHang", khachHangMap);
                }

                // Thêm thông tin nhân viên nếu có
                if (record.get("nv") != null && !record.get("nv").isNull()) {
                    Map<String, Object> nhanVienMap = new HashMap<>();
                    nhanVienMap.put("idNV", record.get("nv").asNode().get("idNV").asString());
                    nhanVienMap.put("hoTen", record.get("nv").asNode().get("hoTen").asString());
                    hoaDonMap.put("nhanVien", nhanVienMap);
                }

                return Optional.of(hoaDonMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding HoaDon by ID: " + idHD, e);
        }

        return Optional.empty();
    }

    public List<Map<String, Object>> findByKhachHang(String idKH) {
        List<Map<String, Object>> hoaDonList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (hd:HoaDon)-[:CUA_KHACH_HANG]->(kh:KhachHang {idKH: $idKH}) "
                    + "OPTIONAL MATCH (hd)-[:DO_NHAN_VIEN]->(nv:NhanVien) "
                    + "RETURN hd, kh, nv ORDER BY hd.ngayLap DESC";

            Result result = session.run(query, Values.parameters("idKH", idKH));
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> hoaDonMap = new HashMap<>();
                hoaDonMap.put("idHD", record.get("hd").asNode().get("idHD").asString());
                hoaDonMap.put("tongTien", record.get("hd").asNode().get("tongTien").asDouble());
                hoaDonMap.put("ngayLap", record.get("hd").asNode().get("ngayLap").asString());

                // Thêm thông tin khách hàng
                Map<String, Object> khachHangMap = new HashMap<>();
                khachHangMap.put("idKH", record.get("kh").asNode().get("idKH").asString());
                khachHangMap.put("hoTen", record.get("kh").asNode().get("hoTen").asString());
                hoaDonMap.put("khachHang", khachHangMap);

                // Thêm thông tin nhân viên nếu có
                if (record.get("nv") != null && !record.get("nv").isNull()) {
                    Map<String, Object> nhanVienMap = new HashMap<>();
                    nhanVienMap.put("idNV", record.get("nv").asNode().get("idNV").asString());
                    nhanVienMap.put("hoTen", record.get("nv").asNode().get("hoTen").asString());
                    hoaDonMap.put("nhanVien", nhanVienMap);
                }

                hoaDonList.add(hoaDonMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding HoaDon by KhachHang ID: " + idKH, e);
        }

        return hoaDonList;
    }

    public List<Map<String, Object>> findByNhanVien(String idNV) {
        List<Map<String, Object>> hoaDonList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            String query = "MATCH (hd:HoaDon)-[:DO_NHAN_VIEN]->(nv:NhanVien {idNV: $idNV}) "
                    + "OPTIONAL MATCH (hd)-[:CUA_KHACH_HANG]->(kh:KhachHang) "
                    + "RETURN hd, kh, nv ORDER BY hd.ngayLap DESC";

            Result result = session.run(query, Values.parameters("idNV", idNV));
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> hoaDonMap = new HashMap<>();
                hoaDonMap.put("idHD", record.get("hd").asNode().get("idHD").asString());
                hoaDonMap.put("tongTien", record.get("hd").asNode().get("tongTien").asDouble());
                hoaDonMap.put("ngayLap", record.get("hd").asNode().get("ngayLap").asString());

                // Thêm thông tin khách hàng nếu có
                if (record.get("kh") != null && !record.get("kh").isNull()) {
                    Map<String, Object> khachHangMap = new HashMap<>();
                    khachHangMap.put("idKH", record.get("kh").asNode().get("idKH").asString());
                    khachHangMap.put("hoTen", record.get("kh").asNode().get("hoTen").asString());
                    hoaDonMap.put("khachHang", khachHangMap);
                }

                // Thêm thông tin nhân viên
                Map<String, Object> nhanVienMap = new HashMap<>();
                nhanVienMap.put("idNV", record.get("nv").asNode().get("idNV").asString());
                nhanVienMap.put("hoTen", record.get("nv").asNode().get("hoTen").asString());
                hoaDonMap.put("nhanVien", nhanVienMap);

                hoaDonList.add(hoaDonMap);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding HoaDon by NhanVien ID: " + idNV, e);
        }

        return hoaDonList;
    }

    public boolean save(Map<String, Object> hoaDonData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idHD = (String) hoaDonData.get("idHD");
                String idKH = (String) hoaDonData.get("idKH");
                String idNV = (String) hoaDonData.get("idNV");
                double tongTien = Double.parseDouble(hoaDonData.get("tongTien").toString());
                LocalDateTime ngayLap = (LocalDateTime) hoaDonData.get("ngayLap");

                String query = "CREATE (hd:HoaDon {idHD: $idHD, idKH: $idKH, idNV: $idNV, tongTien: $tongTien, ngayLap: $ngayLap}) "
                        + "WITH hd "
                        + "MATCH (kh:KhachHang {idKH: $idKH}) "
                        + "MATCH (nv:NhanVien {idNV: $idNV}) "
                        + "CREATE (hd)-[:CUA_KHACH_HANG]->(kh) "
                        + "CREATE (hd)-[:DO_NHAN_VIEN]->(nv) "
                        + "RETURN hd";

                Result result = tx.run(query, Values.parameters(
                        "idHD", idHD,
                        "idKH", idKH,
                        "idNV", idNV,
                        "tongTien", tongTien,
                        "ngayLap", ngayLap
                ));

                return result.hasNext();
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving HoaDon", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> hoaDonData) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                String idHD = (String) hoaDonData.get("idHD");
                String idKH = (String) hoaDonData.get("idKH");
                String idNV = (String) hoaDonData.get("idNV");
                double tongTien = Double.parseDouble(hoaDonData.get("tongTien").toString());

                // Cập nhật thông tin hóa đơn
                String updateQuery = "MATCH (hd:HoaDon {idHD: $idHD}) "
                        + "SET hd.idKH = $idKH, hd.idNV = $idNV, hd.tongTien = $tongTien "
                        + "RETURN hd";

                Result updateResult = tx.run(updateQuery, Values.parameters(
                        "idHD", idHD,
                        "idKH", idKH,
                        "idNV", idNV,
                        "tongTien", tongTien
                ));

                if (!updateResult.hasNext()) {
                    return false;
                }

                // Cập nhật mối quan hệ với khách hàng
                String updateKHQuery = "MATCH (hd:HoaDon {idHD: $idHD}) "
                        + "OPTIONAL MATCH (hd)-[r1:CUA_KHACH_HANG]->(:KhachHang) "
                        + "DELETE r1 "
                        + "WITH hd "
                        + "MATCH (kh:KhachHang {idKH: $idKH}) "
                        + "CREATE (hd)-[:CUA_KHACH_HANG]->(kh) "
                        + "RETURN hd";

                tx.run(updateKHQuery, Values.parameters("idHD", idHD, "idKH", idKH));

                // Cập nhật mối quan hệ với nhân viên
                String updateNVQuery = "MATCH (hd:HoaDon {idHD: $idHD}) "
                        + "OPTIONAL MATCH (hd)-[r2:DO_NHAN_VIEN]->(:NhanVien) "
                        + "DELETE r2 "
                        + "WITH hd "
                        + "MATCH (nv:NhanVien {idNV: $idNV}) "
                        + "CREATE (hd)-[:DO_NHAN_VIEN]->(nv) "
                        + "RETURN hd";

                tx.run(updateNVQuery, Values.parameters("idHD", idHD, "idNV", idNV));

                return true;
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating HoaDon", e);
            return false;
        }
    }

    public boolean delete(String idHD) {
        try (Session session = Neo4jConfig.getInstance().getDriver().session()) {
            return session.writeTransaction(tx -> {
                // Xóa các chi tiết hóa đơn trước
                String deleteChiTietQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r:CO_CHI_TIET]->() DELETE r";
                tx.run(deleteChiTietQuery, Values.parameters("idHD", idHD));

                // Xóa các mối quan hệ của hóa đơn
                String deleteRelationsQuery = "MATCH (hd:HoaDon {idHD: $idHD})-[r]-() DELETE r";
                tx.run(deleteRelationsQuery, Values.parameters("idHD", idHD));

                // Xóa hóa đơn
                String deleteHoaDonQuery = "MATCH (hd:HoaDon {idHD: $idHD}) DELETE hd";
                Result result = tx.run(deleteHoaDonQuery, Values.parameters("idHD", idHD));

                return true;
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting HoaDon", e);
            return false;
        }
    }

    public Map<String, Object> toMap(HoaDon hoaDon) {
        Map<String, Object> map = new HashMap<>();
        map.put("idHD", hoaDon.getIdHD());
        map.put("idKH", hoaDon.getIdKH());
        map.put("idNV", hoaDon.getIdNV());
        map.put("tongTien", hoaDon.getTongTien());
        map.put("ngayLap", hoaDon.getNgayLap());
        return map;
    }

    public HoaDon fromMap(Map<String, Object> map) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setIdHD((String) map.get("idHD"));
        hoaDon.setIdKH((String) map.get("idKH"));
        hoaDon.setIdNV((String) map.get("idNV"));
        hoaDon.setTongTien(Double.parseDouble(map.get("tongTien").toString()));
        hoaDon.setNgayLap((LocalDateTime) map.get("ngayLap"));
        return hoaDon;
    }
}

