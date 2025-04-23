package iuh.fit.util;

import iuh.fit.config.Neo4jConfig;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp tiện ích cung cấp các phương thức thay thế cho các thủ tục APOC
 */
public class CypherQueryUtil {
    private static final Logger LOGGER = Logger.getLogger(CypherQueryUtil.class.getName());
    private static final Driver driver = Neo4jConfig.getInstance().getDriver();

    /**
     * Lấy mức giảm giá dựa trên loại khuyến mãi và hạng mục khách hàng
     * Thay thế cho hàm APOC: getMucGiamGia(loai, hangMuc)
     *
     * @param loai    Loại khuyến mãi (Phần Trăm hoặc Tiền Mặt)
     * @param hangMuc Hạng mục khách hàng (Kim Cương, Vàng, Bạc)
     * @return Mức giảm giá
     */
    public static double getMucGiamGia(String loai, String hangMuc) {
        if ("Phần Trăm".equals(loai)) {
            switch (hangMuc) {
                case "Kim Cương":
                    return 14;
                case "Vàng":
                    return 7;
                case "Bạc":
                    return 3;
                default:
                    return 0;
            }
        } else if ("Tiền Mặt".equals(loai)) {
            switch (hangMuc) {
                case "Kim Cương":
                    return 100000;
                case "Vàng":
                    return 50000;
                case "Bạc":
                    return 20000;
                default:
                    return 0;
            }
        }
        return 0;
    }

    /**
     * Cập nhật hạng mục khách hàng dựa trên tổng tiền chi tiêu
     * Thay thế cho thủ tục APOC: updateCustomerRank(idKH, tongTien)
     *
     * @param idKH     ID của khách hàng
     * @param tongTien Tổng tiền chi tiêu
     * @return Map chứa ID khách hàng và hạng mục mới
     */
    public static Map<String, Object> updateCustomerRank(String idKH, double tongTien) {
        Map<String, Object> result = new HashMap<>();

        try (Session session = driver.session()) {
            // Lấy thông tin khách hàng hiện tại
            String query = "MATCH (k:KhachHang {idKH: $idKH}) RETURN k.hangMuc as hangMuc";
            Record record = session.run(query, Values.parameters("idKH", idKH)).single();
            String currentRank = record.get("hangMuc").asString();

            // Xác định hạng mục mới dựa trên tổng tiền
            String newRank = currentRank;
            if ((tongTien >= 1000000 && tongTien < 3000000 && "Bạc".equals(currentRank)) ||
                    (tongTien >= 3000000 && "Vàng".equals(currentRank))) {

                if (tongTien >= 3000000) {
                    newRank = "Kim Cương";
                } else if (tongTien >= 1000000) {
                    newRank = "Vàng";
                }

                // Cập nhật hạng mục mới cho khách hàng
                String updateQuery = "MATCH (k:KhachHang {idKH: $idKH}) SET k.hangMuc = $hangMuc RETURN k.idKH, k.hangMuc";
                Record updateRecord = session.run(updateQuery,
                        Values.parameters("idKH", idKH, "hangMuc", newRank)).single();

                result.put("idKH", updateRecord.get("k.idKH").asString());
                result.put("hangMuc", updateRecord.get("k.hangMuc").asString());
            } else {
                result.put("idKH", idKH);
                result.put("hangMuc", currentRank);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật hạng mục khách hàng", e);
        }

        return result;
    }

    /**
     * Tính tổng tiền sau khi áp dụng khuyến mãi cho hóa đơn
     * Thay thế cho thủ tục APOC: calculateDiscountedTotal(idHD)
     *
     * @param idHD ID của hóa đơn
     * @return Map chứa thông tin về hóa đơn, khuyến mãi và thành tiền sau khuyến mãi
     */
    public static Map<String, Object> calculateDiscountedTotal(String idHD) {
        Map<String, Object> result = new HashMap<>();

        try (Session session = driver.session()) {
            String query = "MATCH (hd:HoaDon {idHD: $idHD})-[:AP_DUNG_KHUYEN_MAI]->(km:KhuyenMai) " +
                    "RETURN hd.idHD, hd.tongTien, km.loai, km.mucGiamGia";

            Record record = session.run(query, Values.parameters("idHD", idHD)).single();

            String invoiceId = record.get("hd.idHD").asString();
            double totalAmount = record.get("hd.tongTien").asDouble();
            String discountType = record.get("km.loai").asString();
            double discountValue = record.get("km.mucGiamGia").asDouble();

            double discountedTotal = totalAmount;

            if ("Phần Trăm".equals(discountType)) {
                discountedTotal = totalAmount * (1 - discountValue / 100);
            } else if ("Tiền Mặt".equals(discountType)) {
                if (totalAmount > discountValue) {
                    discountedTotal = totalAmount - discountValue;
                } else {
                    discountedTotal = 0;
                }
            }

            result.put("idHD", invoiceId);
            result.put("tongTienGoc", totalAmount);
            result.put("loai", discountType);
            result.put("mucGiamGia", discountValue);
            result.put("thanhTienSauKM", discountedTotal);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tính tổng tiền sau khuyến mãi", e);
        }

        return result;
    }

    /**
     * Áp dụng khuyến mãi cho hóa đơn
     *
     * @param idHD ID của hóa đơn
     * @param idKM ID của khuyến mãi
     * @return true nếu áp dụng thành công, false nếu thất bại
     */
    public static boolean applyPromotionToInvoice(String idHD, String idKM) {
        try (Session session = driver.session()) {
            String query = "MATCH (hd:HoaDon {idHD: $idHD}), (km:KhuyenMai {idKM: $idKM}) " +
                    "CREATE (hd)-[:AP_DUNG_KHUYEN_MAI]->(km) " +
                    "RETURN hd.idHD";

            Record record = session.run(query, Values.parameters("idHD", idHD, "idKM", idKM)).single();
            return record != null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi áp dụng khuyến mãi cho hóa đơn", e);
            return false;
        }
    }

    /**
     * Kiểm tra xem khách hàng có đủ điều kiện nhận khuyến mãi không
     *
     * @param idKH ID của khách hàng
     * @param idKM ID của khuyến mãi
     * @return true nếu đủ điều kiện, false nếu không
     */
    public static boolean isCustomerEligibleForPromotion(String idKH, String idKM) {
        try (Session session = driver.session()) {
            // Lấy thông tin khách hàng
            String customerQuery = "MATCH (k:KhachHang {idKH: $idKH}) RETURN k.hangMuc as hangMuc";
            Record customerRecord = session.run(customerQuery, Values.parameters("idKH", idKH)).single();
            String customerRank = customerRecord.get("hangMuc").asString();

            // Lấy thông tin khuyến mãi
            String promotionQuery = "MATCH (km:KhuyenMai {idKM: $idKM}) " +
                    "RETURN km.dieuKien, km.hangMucApDung";
            Record promotionRecord = session.run(promotionQuery, Values.parameters("idKM", idKM)).single();

            String condition = promotionRecord.get("km.dieuKien").asString();
            String applicableRank = promotionRecord.get("km.hangMucApDung").asString();

            // Kiểm tra điều kiện hạng mục
            if (applicableRank != null && !applicableRank.isEmpty()) {
                if (!applicableRank.contains(customerRank)) {
                    return false;
                }
            }

            // Kiểm tra các điều kiện khác (nếu có)
            // ...

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra điều kiện khuyến mãi", e);
            return false;
        }
    }

    /**
     * Lấy danh sách khuyến mãi áp dụng được cho khách hàng
     *
     * @param idKH ID của khách hàng
     * @return Danh sách các khuyến mãi
     */
    public static List<Map<String, Object>> getEligiblePromotions(String idKH) {
        List<Map<String, Object>> promotions = new ArrayList<>();

        try (Session session = driver.session()) {
            // Lấy thông tin khách hàng
            String customerQuery = "MATCH (k:KhachHang {idKH: $idKH}) RETURN k.hangMuc as hangMuc";
            Record customerRecord = session.run(customerQuery, Values.parameters("idKH", idKH)).single();
            String customerRank = customerRecord.get("hangMuc").asString();

            // Lấy danh sách khuyến mãi phù hợp với hạng mục khách hàng
            String promotionsQuery = "MATCH (km:KhuyenMai) " +
                    "WHERE km.hangMucApDung CONTAINS $hangMuc OR km.hangMucApDung IS NULL " +
                    "RETURN km.idKM, km.tenKM, km.loai, km.mucGiamGia, km.ngayBatDau, km.ngayKetThuc";

            Result result = session.run(promotionsQuery, Values.parameters("hangMuc", customerRank));

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> promotion = new HashMap<>();

                promotion.put("idKM", record.get("km.idKM").asString());
                promotion.put("tenKM", record.get("km.tenKM").asString());
                promotion.put("loai", record.get("km.loai").asString());
                promotion.put("mucGiamGia", record.get("km.mucGiamGia").asDouble());
                promotion.put("ngayBatDau", record.get("km.ngayBatDau").asString());
                promotion.put("ngayKetThuc", record.get("km.ngayKetThuc").asString());

                promotions.add(promotion);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách khuyến mãi", e);
        }

        return promotions;
    }
}
