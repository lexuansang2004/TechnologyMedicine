package iuh.fit.service;

import iuh.fit.dao.KhuyenMaiDAO;
import iuh.fit.entity.KhuyenMai;
import iuh.fit.config.Neo4jConfig;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KhuyenMaiService {
    private static final Logger LOGGER = Logger.getLogger(KhuyenMaiService.class.getName());
    private final KhuyenMaiDAO khuyenMaiDAO;

    public KhuyenMaiService() {
        this.khuyenMaiDAO = new KhuyenMaiDAO();
    }

    /**
     * Chuyển đổi KhuyenMai thành Map<String, Object>
     */
    private Map<String, Object> convertToMap(KhuyenMai khuyenMai) {
        Map<String, Object> map = new HashMap<>();
        map.put("idKM", khuyenMai.getIdKM());
        map.put("loai", khuyenMai.getLoai());
        map.put("mucGiamGia", khuyenMai.getMucGiamGia());
        map.put("trangThai", khuyenMai.getTrangThai());

        if (khuyenMai.getHangMuc() != null) {
            map.put("hangMuc", khuyenMai.getHangMuc());
        }

        if (khuyenMai.getIdThuoc() != null) {
            map.put("idThuoc", khuyenMai.getIdThuoc());
        }

        if (khuyenMai.getThuoc() != null) {
            Map<String, Object> thuocMap = new HashMap<>();
            thuocMap.put("idThuoc", khuyenMai.getThuoc().getIdThuoc());
            thuocMap.put("tenThuoc", khuyenMai.getThuoc().getTenThuoc());
            map.put("thuoc", thuocMap);
        }

        return map;
    }

    /**
     * Chuyển đổi Map<String, Object> thành KhuyenMai
     */
    private KhuyenMai convertToEntity(Map<String, Object> map) {
        KhuyenMai khuyenMai = new KhuyenMai();

        if (map.containsKey("idKM")) {
            khuyenMai.setIdKM((String) map.get("idKM"));
        } else {
            khuyenMai.setIdKM(generateId());
        }

        if (map.containsKey("loai")) {
            khuyenMai.setLoai((String) map.get("loai"));
        }

        if (map.containsKey("mucGiamGia")) {
            if (map.get("mucGiamGia") instanceof Number) {
                khuyenMai.setMucGiamGia(((Number) map.get("mucGiamGia")).doubleValue());
            } else if (map.get("mucGiamGia") instanceof String) {
                khuyenMai.setMucGiamGia(Double.parseDouble((String) map.get("mucGiamGia")));
            }
        }

        if (map.containsKey("trangThai")) {
            khuyenMai.setTrangThai((String) map.get("trangThai"));
        } else {
            khuyenMai.setTrangThai("Đang áp dụng");
        }

        if (map.containsKey("hangMuc")) {
            khuyenMai.setHangMuc((String) map.get("hangMuc"));
        }

        if (map.containsKey("idThuoc")) {
            khuyenMai.setIdThuoc((String) map.get("idThuoc"));
        }

        return khuyenMai;
    }

    public List<Map<String, Object>> findAll() {
        // Chuyển đổi List<KhuyenMai> thành List<Map<String, Object>>
        List<KhuyenMai> khuyenMaiList = khuyenMaiDAO.findAll();
        return khuyenMaiList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    public Optional<Map<String, Object>> findById(String id) {
        // Chuyển đổi Optional<KhuyenMai> thành Optional<Map<String, Object>>
        Optional<KhuyenMai> khuyenMaiOpt = khuyenMaiDAO.findById(id);
        return khuyenMaiOpt.map(this::convertToMap);
    }

    public List<Map<String, Object>> findByHangMuc(String hangMuc) {
        // Chuyển đổi List<KhuyenMai> thành List<Map<String, Object>>
        List<KhuyenMai> khuyenMaiList = khuyenMaiDAO.findByHangMuc(hangMuc);
        return khuyenMaiList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> findByThuoc(String idThuoc) {
        // Chuyển đổi List<KhuyenMai> thành List<Map<String, Object>>
        List<KhuyenMai> khuyenMaiList = khuyenMaiDAO.findByThuoc(idThuoc);
        return khuyenMaiList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    public boolean save(Map<String, Object> khuyenMaiMap) {
        // Chuyển đổi Map<String, Object> thành KhuyenMai
        KhuyenMai khuyenMai = convertToEntity(khuyenMaiMap);

        // Kiểm tra xem là khuyến mãi cho hạng mục hay thuốc
        if (khuyenMai.getHangMuc() != null) {
            return khuyenMaiDAO.saveForHangMuc(khuyenMai);
        } else if (khuyenMai.getIdThuoc() != null) {
            return khuyenMaiDAO.saveForThuoc(khuyenMai);
        } else {
            LOGGER.warning("Không thể xác định loại khuyến mãi (hạng mục hoặc thuốc)");
            return false;
        }
    }

    public boolean update(Map<String, Object> khuyenMaiMap) {
        // Chuyển đổi Map<String, Object> thành KhuyenMai
        KhuyenMai khuyenMai = convertToEntity(khuyenMaiMap);
        return khuyenMaiDAO.update(khuyenMai);
    }

    public boolean delete(String id) {
        return khuyenMaiDAO.delete(id);
    }

    public List<Map<String, Object>> getActivePromotions() {
        List<Map<String, Object>> khuyenMaiList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (km:KhuyenMai) WHERE km.trangThai = 'Đang áp dụng' RETURN km";
            Result result = session.run(query);

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> khuyenMai = record.get("km").asMap();
                khuyenMaiList.add(khuyenMai);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting active promotions", e);
        }

        return khuyenMaiList;
    }

    private String generateId() {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (km:KhuyenMai) RETURN COUNT(km) as count";
            Result result = session.run(query);

            long count = 0;
            if (result.hasNext()) {
                count = result.next().get("count").asLong();
            }

            return String.format("KM%03d", count + 1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating ID", e);
            return "KM" + System.currentTimeMillis();
        }
    }
}