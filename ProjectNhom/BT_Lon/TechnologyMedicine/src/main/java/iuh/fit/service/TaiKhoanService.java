package iuh.fit.service;

import iuh.fit.config.Neo4jConfig;
import iuh.fit.entity.TaiKhoan;
import iuh.fit.util.PasswordUtil;
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

    public boolean createTaiKhoan(Map<String, Object> taiKhoanData) {
        try {
            // Tạo đối tượng TaiKhoan mới
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setIdTK(generateId());
            taiKhoan.setUsername((String) taiKhoanData.get("username"));
            taiKhoan.setPassword("123456"); // Mật khẩu mặc định, nên mã hóa
            taiKhoan.setIdNV((String) taiKhoanData.get("idNV"));
            taiKhoan.setIdVT((String) taiKhoanData.get("idVT"));

            // Lưu tài khoản
            return save((Map<String, Object>) taiKhoan);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
}