package iuh.fit.service;

import iuh.fit.config.EmailConfig;
import iuh.fit.config.Neo4jConfig;
import iuh.fit.dao.NhanVienDAO;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.TaiKhoan;
import iuh.fit.util.PasswordUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaiKhoanService {
    private static final Logger LOGGER = Logger.getLogger(TaiKhoanService.class.getName());

    public boolean authenticate(String username, String password) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            // Thêm log để debug
            LOGGER.info("Authenticating user: " + username);

            // Lấy thông tin tài khoản từ database
            String query = "MATCH (tk:TaiKhoan {username: $username}) RETURN tk";
            Result result = session.run(query, Values.parameters("username", username));

            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> taiKhoan = record.get("tk").asMap();
                String storedPassword = (String) taiKhoan.get("password");

                LOGGER.info("Found account for username: " + username);

                // Kiểm tra trực tiếp mật khẩu "123456" cho mục đích debug
                if (password.equals("123456") && storedPassword.equals("$2a$12$8vxYfAWyUCRJvGkXsUKqAOH0RFCfFxLm1P9o5Y1WUVxnqr/h0bKHK")) {
                    LOGGER.info("Password match for hardcoded value");
                    return true;
                }

                // Kiểm tra mật khẩu thông thường
                boolean match = PasswordUtil.checkPassword(password, storedPassword);
                LOGGER.info("Password match result: " + match);
                return match;
            }

            LOGGER.info("No account found with username: " + username);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user: " + e.getMessage(), e);
            return false;
        }
    }

    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> taiKhoanList = new ArrayList<>();

        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan) " +
                    "OPTIONAL MATCH (tk)-[:THUOC_VE]->(nv:NhanVien) " +
                    "OPTIONAL MATCH (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                    "RETURN tk, nv, vt";

            Result result = session.run(query);

            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> taiKhoan = record.get("tk").asMap();

                if (record.get("nv") != null) {
                    taiKhoan.put("nhanVien", record.get("nv").asMap());
                }

                if (record.get("vt") != null) {
                    taiKhoan.put("vaiTro", record.get("vt").asMap());
                }

                // Không trả về mật khẩu
                taiKhoan.remove("password");

                taiKhoanList.add(taiKhoan);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding all accounts", e);
        }

        return taiKhoanList;
    }

    public Optional<Map<String, Object>> findById(String idTK) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan {idTK: $idTK}) " +
                    "OPTIONAL MATCH (tk)-[:THUOC_VE]->(nv:NhanVien) " +
                    "OPTIONAL MATCH (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                    "RETURN tk, nv, vt";

            Result result = session.run(query, Values.parameters("idTK", idTK));

            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> taiKhoan = record.get("tk").asMap();

                if (record.get("nv") != null) {
                    taiKhoan.put("nhanVien", record.get("nv").asMap());
                }

                if (record.get("vt") != null) {
                    taiKhoan.put("vaiTro", record.get("vt").asMap());
                }

                // Không trả về mật khẩu
                taiKhoan.remove("password");

                return Optional.of(taiKhoan);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding account by ID", e);
        }

        return Optional.empty();
    }

    public Optional<Map<String, Object>> findByUsername(String username) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            // Sửa lại query để đảm bảo lấy đúng thông tin
            String query = "MATCH (tk:TaiKhoan {username: $username}) " +
                    "OPTIONAL MATCH (tk)-[:THUOC_VE]->(nv:NhanVien) " +
                    "OPTIONAL MATCH (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                    "RETURN tk, nv, vt";

            Result result = session.run(query, Values.parameters("username", username));

            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> taiKhoan = record.get("tk").asMap();

                if (record.get("nv") != null && !record.get("nv").isNull()) {
                    taiKhoan.put("nhanVien", record.get("nv").asMap());
                }

                if (record.get("vt") != null && !record.get("vt").isNull()) {
                    taiKhoan.put("vaiTro", record.get("vt").asMap());
                }

                // Không trả về mật khẩu
                taiKhoan.remove("password");

                return Optional.of(taiKhoan);
            }

            // Thêm log để debug
            LOGGER.info("No account found with username: " + username);

            return Optional.empty();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding account by username: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Optional<Map<String, Object>> findByNhanVien(String idNV) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan {idNV: $idNV}) " +
                    "OPTIONAL MATCH (tk)-[:THUOC_VE]->(nv:NhanVien) " +
                    "OPTIONAL MATCH (tk)-[:CO_VAI_TRO]->(vt:VaiTro) " +
                    "RETURN tk, nv, vt";

            Result result = session.run(query, Values.parameters("idNV", idNV));

            if (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> taiKhoan = record.get("tk").asMap();

                if (record.get("nv") != null) {
                    taiKhoan.put("nhanVien", record.get("nv").asMap());
                }

                if (record.get("vt") != null) {
                    taiKhoan.put("vaiTro", record.get("vt").asMap());
                }

                // Không trả về mật khẩu
                taiKhoan.remove("password");

                return Optional.of(taiKhoan);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding account by employee ID", e);
        }

        return Optional.empty();
    }

//    public boolean createTaiKhoan(Map<String, Object> taiKhoanData) {
//        try {
//            // Tạo đối tượng TaiKhoan mới
//            TaiKhoan taiKhoan = new TaiKhoan();
//            taiKhoan.setIdTK(generateId());
//            taiKhoan.setUsername((String) taiKhoanData.get("username"));
//            taiKhoan.setPassword("123456"); // Mật khẩu mặc định, nên mã hóa
//            taiKhoan.setIdNV((String) taiKhoanData.get("idNV"));
//            taiKhoan.setIdVT((String) taiKhoanData.get("idVT"));
//
//            // Lưu tài khoản
//            return save((Map<String, Object>) taiKhoan);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    /**
     * Tạo tài khoản mới cho nhân viên
     * @param taiKhoanData Dữ liệu tài khoản
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createTaiKhoan(Map<String, Object> taiKhoanData) {
        try {
            String idNV = (String) taiKhoanData.get("idNV");
            String username = (String) taiKhoanData.get("username");
            String idVT = (String) taiKhoanData.get("idVT");

            // Kiểm tra dữ liệu đầu vào
            if (idNV == null || idNV.isEmpty() || username == null || username.isEmpty() || idVT == null || idVT.isEmpty()) {
                LOGGER.warning("Dữ liệu tài khoản không đầy đủ");
                return false;
            }

            // Kiểm tra nhân viên tồn tại
            Optional<NhanVien> nhanVienOpt = new NhanVienDAO().findById(idNV);
            if (nhanVienOpt.isEmpty()) {
                LOGGER.warning("Không tìm thấy nhân viên với ID: " + idNV);
                return false;
            }

            // Kiểm tra username đã tồn tại chưa
            if (isUsernameExists(username)) {
                LOGGER.warning("Username đã tồn tại: " + username);
                return false;
            }

            // Kiểm tra vai trò tồn tại
            boolean vaiTroExists = checkVaiTroExists(idVT);
            if (!vaiTroExists) {
                LOGGER.warning("Không tìm thấy vai trò với ID: " + idVT);
                return false;
            }

            // Tạo ID tài khoản mới
            String idTK = generateNewId();

            // Tạo mật khẩu ngẫu nhiên
            String password = generateRandomPassword();
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Lưu tài khoản vào cơ sở dữ liệu
            try (Session session = Neo4jConfig.getInstance().getSession()) {
                String query = "CREATE (tk:TaiKhoan {idTK: $idTK, username: $username, password: $password}) " +
                        "WITH tk " +
                        "MATCH (nv:NhanVien {idNV: $idNV}) " +
                        "MATCH (vt:VaiTro {idVT: $idVT}) " +
                        "CREATE (tk)-[:THUOC_VE]->(nv) " +
                        "CREATE (tk)-[:CO_VAI_TRO]->(vt) " +
                        "RETURN tk.idTK";

                Result result = session.run(query, Map.of(
                        "idTK", idTK,
                        "username", username,
                        "password", hashedPassword,
                        "idNV", idNV,
                        "idVT", idVT
                ));

                if (result.hasNext()) {
                    // Gửi email thông báo tài khoản đã được tạo
                    NhanVien nhanVien = nhanVienOpt.get();
                    if (nhanVien.getEmail() != null && !nhanVien.getEmail().isEmpty()) {
                        boolean sent = EmailConfig.getInstance().sendEmail(
                                nhanVien.getEmail(),
                                "Thông tin tài khoản mới",
                                "<html><body>" +
                                        "<h2>Xin chào " + nhanVien.getHoTen() + ",</h2>" +
                                        "<p>Tài khoản của bạn đã được tạo với thông tin sau:</p>" +
                                        "<p>Tên đăng nhập: <strong>" + username + "</strong></p>" +
                                        "<p>Mật khẩu: <strong>" + password + "</strong></p>" +
                                        "<p>Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu.</p>" +
                                        "<p>Trân trọng,<br>Nhà thuốc TechnologyMedicine</p>" +
                                        "</body></html>"
                        );

                        if (!sent) {
                            LOGGER.warning("Không thể gửi email thông báo tài khoản mới");
                        }
                    }

                    LOGGER.info("Đã tạo tài khoản cho nhân viên: " + idNV);
                    return true;
                } else {
                    LOGGER.warning("Không thể tạo tài khoản cho nhân viên: " + idNV);
                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tạo tài khoản", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean isUsernameExists(String username) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan {username: $username}) RETURN count(tk) as count";
            Result result = session.run(query, Map.of("username", username));

            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                return count > 0;
            }

            return false;
        }
    }

    /**
     * Kiểm tra vai trò tồn tại
     */
    private boolean checkVaiTroExists(String idVT) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (vt:VaiTro {idVT: $idVT}) RETURN count(vt) as count";
            Result result = session.run(query, Map.of("idVT", idVT));

            if (result.hasNext()) {
                Record record = result.next();
                int count = record.get("count").asInt();
                return count > 0;
            }

            return false;
        }
    }


    /**
     * Tạo ID tài khoản mới
     */
    private String generateNewId() {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan) RETURN tk.idTK as idTK ORDER BY tk.idTK DESC LIMIT 1";
            Result result = session.run(query);

            if (result.hasNext()) {
                Record record = result.next();
                String lastId = record.get("idTK").asString();

                // Trích xuất số từ ID cuối cùng (TK001 -> 1)
                int number = Integer.parseInt(lastId.substring(2));

                // Tạo ID mới (TK001 -> TK002)
                return String.format("TK%03d", number + 1);
            } else {
                // Nếu không có tài khoản nào, bắt đầu từ TK001
                return "TK001";
            }
        }
    }

    /**
     * Tạo mật khẩu ngẫu nhiên
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public boolean save(Map<String, Object> taiKhoanData) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            // Tạo ID mới nếu chưa có
            if (!taiKhoanData.containsKey("idTK") || taiKhoanData.get("idTK") == null) {
                String newId = generateId();
                taiKhoanData.put("idTK", newId);
            }

            // Mã hóa mật khẩu
            String password = (String) taiKhoanData.get("password");
            if (password != null && !password.startsWith("$2a$")) {
                String hashedPassword = PasswordUtil.hashPassword(password);
                taiKhoanData.put("password", hashedPassword);
            }

            String idNV = (String) taiKhoanData.get("idNV");
            String idVT = (String) taiKhoanData.get("idVT");

            // Tạo tài khoản và mối quan hệ
            String query = "CREATE (tk:TaiKhoan {idTK: $idTK, username: $username, password: $password, idNV: $idNV, idVT: $idVT}) " +
                    "WITH tk " +
                    "MATCH (nv:NhanVien {idNV: $idNV}) " +
                    "MATCH (vt:VaiTro {idVT: $idVT}) " +
                    "CREATE (tk)-[:THUOC_VE]->(nv) " +
                    "CREATE (tk)-[:CO_VAI_TRO]->(vt) " +
                    "RETURN tk";

            Result result = session.run(query, Values.parameters(
                    "idTK", taiKhoanData.get("idTK"),
                    "username", taiKhoanData.get("username"),
                    "password", taiKhoanData.get("password"),
                    "idNV", idNV,
                    "idVT", idVT
            ));

            return result.hasNext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving account", e);
            return false;
        }
    }

    public boolean update(Map<String, Object> taiKhoanData) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String idTK = (String) taiKhoanData.get("idTK");
            String idNV = (String) taiKhoanData.get("idNV");
            String idVT = (String) taiKhoanData.get("idVT");

            // Mã hóa mật khẩu nếu có thay đổi
            String password = (String) taiKhoanData.get("password");
            if (password != null && !password.startsWith("$2a$")) {
                String hashedPassword = PasswordUtil.hashPassword(password);
                taiKhoanData.put("password", hashedPassword);
            }

            // Cập nhật tài khoản và mối quan hệ
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("MATCH (tk:TaiKhoan {idTK: $idTK}) ");

            // Cập nhật thuộc tính
            queryBuilder.append("SET tk.username = $username ");

            if (taiKhoanData.containsKey("password") && taiKhoanData.get("password") != null) {
                queryBuilder.append(", tk.password = $password ");
            }

            queryBuilder.append(", tk.idNV = $idNV, tk.idVT = $idVT ");

            // Xóa mối quan hệ cũ
            queryBuilder.append("WITH tk ");
            queryBuilder.append("OPTIONAL MATCH (tk)-[r:THUOC_VE]->() DELETE r ");
            queryBuilder.append("WITH tk ");
            queryBuilder.append("OPTIONAL MATCH (tk)-[r:CO_VAI_TRO]->() DELETE r ");

            // Tạo mối quan hệ mới
            queryBuilder.append("WITH tk ");
            queryBuilder.append("MATCH (nv:NhanVien {idNV: $idNV}) ");
            queryBuilder.append("MATCH (vt:VaiTro {idVT: $idVT}) ");
            queryBuilder.append("CREATE (tk)-[:THUOC_VE]->(nv) ");
            queryBuilder.append("CREATE (tk)-[:CO_VAI_TRO]->(vt) ");
            queryBuilder.append("RETURN tk");

            // Thay thế Values.ofMap(parameters) bằng Values.parameters()
            Result result;
            if (taiKhoanData.containsKey("password") && taiKhoanData.get("password") != null) {
                result = session.run(queryBuilder.toString(), Values.parameters(
                        "idTK", idTK,
                        "username", taiKhoanData.get("username"),
                        "password", taiKhoanData.get("password"),
                        "idNV", idNV,
                        "idVT", idVT
                ));
            } else {
                result = session.run(queryBuilder.toString(), Values.parameters(
                        "idTK", idTK,
                        "username", taiKhoanData.get("username"),
                        "idNV", idNV,
                        "idVT", idVT
                ));
            }

            return result.hasNext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating account", e);
            return false;
        }
    }

    public boolean delete(String idTK) {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan {idTK: $idTK}) " +
                    "OPTIONAL MATCH (tk)-[r]-() " +
                    "DELETE r, tk";

            session.run(query, Values.parameters("idTK", idTK));

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting account", e);
            return false;
        }
    }

    private String generateId() {
        try (Session session = Neo4jConfig.getInstance().getSession()) {
            String query = "MATCH (tk:TaiKhoan) RETURN COUNT(tk) as count";
            Result result = session.run(query);

            long count = 0;
            if (result.hasNext()) {
                count = result.next().get("count").asLong();
            }

            return String.format("TK%03d", count + 1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating ID", e);
            return "TK" + System.currentTimeMillis();
        }
    }

    /**
     * Đặt lại mật khẩu cho nhân viên
     * @param idNV ID của nhân viên
     * @param newPassword Mật khẩu mới
     * @return true nếu đặt lại mật khẩu thành công, false nếu thất bại
     */
    public boolean resetPassword(String idNV, String newPassword) {
        try {
            // Tìm tài khoản của nhân viên
            String query = "MATCH (tk:TaiKhoan)-[:THUOC_VE]->(nv:NhanVien {idNV: $idNV}) " +
                    "RETURN tk.idTK as idTK, tk.username as username";

            try (Session session = Neo4jConfig.getInstance().getSession()) {
                Result result = session.run(query, Map.of("idNV", idNV));

                if (result.hasNext()) {
                    Record record = result.next();
                    String idTK = record.get("idTK").asString();

                    // Mã hóa mật khẩu mới
                    String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                    // Cập nhật mật khẩu
                    String updateQuery = "MATCH (tk:TaiKhoan {idTK: $idTK}) " +
                            "SET tk.password = $password";

                    session.run(updateQuery, Map.of(
                            "idTK", idTK,
                            "password", hashedPassword
                    ));

                    LOGGER.info("Đã đặt lại mật khẩu cho nhân viên: " + idNV);
                    return true;
                } else {
                    LOGGER.warning("Không tìm thấy tài khoản cho nhân viên: " + idNV);
                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đặt lại mật khẩu", e);
            return false;
        }
    }
}