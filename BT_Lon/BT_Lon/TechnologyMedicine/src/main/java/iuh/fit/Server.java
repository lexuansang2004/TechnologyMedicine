package iuh.fit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iuh.fit.config.Neo4jConfig;
import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.NhaCungCap;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.Thuoc;
import iuh.fit.service.*;
import iuh.fit.util.LocalDateAdapter;
import iuh.fit.util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 9876;
    private static final int THREAD_POOL_SIZE = 10;

    private final ExecutorService executorService;
    private final Gson gson;

    // Services
    private final ThuocService thuocService;
    private final KhachHangService khachHangService;
    private final NhaCungCapService nhaCungCapService;
    private final KhuyenMaiService khuyenMaiService;
    private final NhanVienService nhanVienService;
    private final TaiKhoanService taiKhoanService;

    public Server() {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        // Khởi tạo các service
        this.thuocService = new ThuocService();
        this.khachHangService = new KhachHangService();
        this.nhaCungCapService = new NhaCungCapService();
        this.khuyenMaiService = new KhuyenMaiService();
        this.nhanVienService = new NhanVienService();
        this.taiKhoanService = new TaiKhoanService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting server", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Chuyển đổi JSON thành đối tượng RequestDTO
                RequestDTO request = gson.fromJson(inputLine, RequestDTO.class);

                // Xử lý yêu cầu
                ResponseDTO response = processRequest(request);

                // Chuyển đổi đối tượng ResponseDTO thành JSON và gửi về client
                String outputLine = gson.toJson(response);
                out.println(outputLine);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error handling client", e);
        } finally {
            try {
                clientSocket.close();
                LOGGER.info("Client disconnected");
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing client socket", e);
            }
        }
    }

    private ResponseDTO processRequest(RequestDTO request) {
        String action = request.getAction();

        try {
            switch (action) {
                case "LOGIN":
                    return handleLogin(request);

                // Thuốc
                case "GET_ALL_THUOC":
                    return handleGetAllThuoc();
                case "GET_THUOC_BY_ID":
                    return handleGetThuocById(request);
                case "SEARCH_THUOC":
                    return handleSearchThuoc(request);
                case "SAVE_THUOC":
                    return handleSaveThuoc(request);
                case "UPDATE_THUOC":
                    return handleUpdateThuoc(request);
                case "DELETE_THUOC":
                    return handleDeleteThuoc(request);

                // Khách hàng
                case "GET_ALL_KHACH_HANG":
                    return handleGetAllKhachHang();
                case "GET_KHACH_HANG_BY_ID":
                    return handleGetKhachHangById(request);
//                case "SEARCH_KHACH_HANG":
//                    return handleSearchKhachHang(request);
                case "SAVE_KHACH_HANG":
                    return handleSaveKhachHang(request);
                case "UPDATE_KHACH_HANG":
                    return handleUpdateKhachHang(request);
                case "DELETE_KHACH_HANG":
                    return handleDeleteKhachHang(request);
//                case "GENERATE_OTP":
//                    return handleGenerateOTP(request);
//                case "VERIFY_OTP":
//                    return handleVerifyOTP(request);
//
//                // Nhà cung cấp
//                case "GET_ALL_NHA_CUNG_CAP":
//                    return handleGetAllNhaCungCap();
//                case "GET_NHA_CUNG_CAP_BY_ID":
//                    return handleGetNhaCungCapById(request);
//                case "SEARCH_NHA_CUNG_CAP":
//                    return handleSearchNhaCungCap(request);
//                case "SAVE_NHA_CUNG_CAP":
//                    return handleSaveNhaCungCap(request);
//                case "UPDATE_NHA_CUNG_CAP":
//                    return handleUpdateNhaCungCap(request);
//                case "DELETE_NHA_CUNG_CAP":
//                    return handleDeleteNhaCungCap(request);
//
//                // Khuyến mãi
//                case "GET_ALL_KHUYEN_MAI":
//                    return handleGetAllKhuyenMai();
//                case "GET_KHUYEN_MAI_BY_ID":
//                    return handleGetKhuyenMaiById(request);
//                case "GET_KHUYEN_MAI_BY_HANG_MUC":
//                    return handleGetKhuyenMaiByHangMuc(request);
//                case "GET_KHUYEN_MAI_BY_THUOC":
//                    return handleGetKhuyenMaiByThuoc(request);
//                case "SAVE_KHUYEN_MAI_HANG_MUC":
//                    return handleSaveKhuyenMaiHangMuc(request);
//                case "SAVE_KHUYEN_MAI_THUOC":
//                    return handleSaveKhuyenMaiThuoc(request);
//                case "UPDATE_KHUYEN_MAI":
//                    return handleUpdateKhuyenMai(request);
//                case "DELETE_KHUYEN_MAI":
//                    return handleDeleteKhuyenMai(request);
//
//                // Nhân viên
//                case "GET_ALL_NHAN_VIEN":
//                    return handleGetAllNhanVien();
//                case "GET_NHAN_VIEN_BY_ID":
//                    return handleGetNhanVienById(request);
//                case "SEARCH_NHAN_VIEN":
//                    return handleSearchNhanVien(request);
//                case "SAVE_NHAN_VIEN":
//                    return handleSaveNhanVien(request);
//                case "UPDATE_NHAN_VIEN":
//                    return handleUpdateNhanVien(request);
//                case "DELETE_NHAN_VIEN":
//                    return handleDeleteNhanVien(request);
//
//                // Tài khoản
//                case "GET_ALL_TAI_KHOAN":
//                    return handleGetAllTaiKhoan();
//                case "GET_TAI_KHOAN_BY_ID":
//                    return handleGetTaiKhoanById(request);
//                case "GET_TAI_KHOAN_BY_NHAN_VIEN":
//                    return handleGetTaiKhoanByNhanVien(request);
//                case "SAVE_TAI_KHOAN":
//                    return handleSaveTaiKhoan(request);
//                case "UPDATE_TAI_KHOAN":
//                    return handleUpdateTaiKhoan(request);
//                case "DELETE_TAI_KHOAN":
//                    return handleDeleteTaiKhoan(request);

                default:
                    return new ResponseDTO(false, "Không hỗ trợ hành động: " + action);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing request: " + action, e);
            return new ResponseDTO(false, "Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // Các phương thức xử lý yêu cầu cụ thể
    // Thuốc
    private ResponseDTO handleGetAllThuoc() {
        try {
            List<Thuoc> thuocs = thuocService.findAll();
            return new ResponseDTO(true, "Lấy tất cả thuốc thành công", (Map<String, Object>) thuocs);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy tất cả thuốc", e);
            return new ResponseDTO(false, "Lỗi lấy tất cả thuốc: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetThuocById(RequestDTO request) {
        String id = (String) request.getData().get("id");

        Optional<Thuoc> thuoc = thuocService.findById(id);
        if (thuoc.isPresent()) {
            return new ResponseDTO(true, "Lấy thuốc theo ID thành công", (Map<String, Object>) thuoc.get());
        } else {
            return new ResponseDTO(false, "Không tìm thấy thuốc với ID: " + id);
        }
    }


    private ResponseDTO handleSearchThuoc(RequestDTO request) {
        String name = (String) request.getData().get("name");

        List<Thuoc> thuocs = thuocService.findByName(name);
        if (!thuocs.isEmpty()) {
            return new ResponseDTO(true, "Tìm kiếm thuốc thành công", (Map<String, Object>) thuocs);
        } else {
            return new ResponseDTO(false, "Không tìm thấy thuốc với tên: " + name);
        }
    }

    private ResponseDTO handleSaveThuoc(RequestDTO request) {
        Thuoc thuoc = gson.fromJson(gson.toJson(request.getData()), Thuoc.class);

        if (thuocService.save(thuoc)) {
            return new ResponseDTO(true, "Lưu thuốc thành công");
        } else {
            return new ResponseDTO(false, "Lưu thuốc không thành công");
        }
    }


    private ResponseDTO handleUpdateThuoc(RequestDTO request) {
        Thuoc thuoc = gson.fromJson(gson.toJson(request.getData()), Thuoc.class);

        if (thuocService.update(thuoc)) {
            return new ResponseDTO(true, "Cập nhật thuốc thành công");
        } else {
            return new ResponseDTO(false, "Cập nhật thuốc không thành công");
        }
    }


    private ResponseDTO handleDeleteThuoc(RequestDTO request) {
        String id = (String) request.getData().get("id");

        if (thuocService.delete(id)) {
            return new ResponseDTO(true, "Xóa thuốc thành công");
        } else {
            return new ResponseDTO(false, "Xóa thuốc không thành công");
        }
    }


    // Khách hàng
    private ResponseDTO handleGetAllKhachHang() {
        try {
            // Sửa từ khachHangService.getAll() thành khachHangService.findAll()
            return new ResponseDTO(true, "Lấy tất cả khách hàng thành công", (Map<String, Object>) khachHangService.findAll());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching all customers", e);
            return new ResponseDTO(false, "Lỗi khi lấy tất cả khách hàng");
        }
    }


    private ResponseDTO handleGetKhachHangById(RequestDTO request) {
        try {
            String khachHangId = (String) request.getData().get("id");
            // Sửa từ khachHangService.getById() thành khachHangService.findById()
            Optional<KhachHang> khachHangOpt = khachHangService.findById(khachHangId);

            if (khachHangOpt.isPresent()) {
                return new ResponseDTO(true, "Lấy khách hàng thành công", (Map<String, Object>) khachHangOpt.get());
            } else {
                return new ResponseDTO(false, "Khách hàng không tồn tại");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching customer by id", e);
            return new ResponseDTO(false, "Lỗi khi lấy khách hàng theo ID");
        }
    }

//
//    private ResponseDTO handleSearchKhachHang(RequestDTO request) {
//        try {
//            String keyword = (String) request.getData().get("keyword");
//            // Giả sử khachHangService.search() trả về danh sách khách hàng theo từ khóa tìm kiếm
//            return new ResponseDTO(true, "Tìm kiếm khách hàng thành công", khachHangService.search(keyword));
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Error searching customers", e);
//            return new ResponseDTO(false, "Lỗi khi tìm kiếm khách hàng");
//        }
//    }

    private ResponseDTO handleSaveKhachHang(RequestDTO request) {
        try {
            // Chuyển Map<String, Object> thành đối tượng KhachHang
            Map<String, Object> data = request.getData();

            // Giả sử bạn có một constructor hoặc setter để gán dữ liệu cho đối tượng KhachHang
            KhachHang khachHang = new KhachHang();
            khachHang.setIdKH((String) data.get("idKH"));
            khachHang.setHoTen((String) data.get("hoTen"));
            khachHang.setSdt((String) data.get("sdt"));
            khachHang.setEmail((String) data.get("email"));
            khachHang.setNgayThamGia((LocalDate) data.get("ngayThamGia")); // Nếu có
            khachHang.setHangMuc((String) data.get("hangMuc"));
            khachHang.setTongChiTieu((Double) data.get("tongChiTieu"));

            // Lưu khách hàng
            boolean saved = khachHangService.save(khachHang);

            if (saved) {
                return new ResponseDTO(true, "Lưu khách hàng thành công");
            } else {
                return new ResponseDTO(false, "Không thể lưu khách hàng");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving customer", e);
            return new ResponseDTO(false, "Lỗi khi lưu khách hàng");
        }
    }


    private ResponseDTO handleUpdateKhachHang(RequestDTO request) {
        try {
            // Chuyển Map<String, Object> thành đối tượng KhachHang
            Map<String, Object> data = request.getData();

            // Giả sử bạn có một constructor hoặc setter để gán dữ liệu cho đối tượng KhachHang
            KhachHang khachHang = new KhachHang();
            khachHang.setIdKH((String) data.get("idKH"));
            khachHang.setHoTen((String) data.get("hoTen"));
            khachHang.setSdt((String) data.get("sdt"));
            khachHang.setEmail((String) data.get("email"));
            khachHang.setGioiTinh((String) data.get("gioiTinh"));
            khachHang.setNgayThamGia((LocalDate) data.get("ngayThamGia")); // Nếu có
            khachHang.setHangMuc((String) data.get("hangMuc"));
            khachHang.setTongChiTieu((Double) data.get("tongChiTieu"));

            // Cập nhật khách hàng
            boolean updated = khachHangService.update(khachHang);

            if (updated) {
                return new ResponseDTO(true, "Cập nhật khách hàng thành công");
            } else {
                return new ResponseDTO(false, "Không thể cập nhật khách hàng");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating customer", e);
            return new ResponseDTO(false, "Lỗi khi cập nhật khách hàng");
        }
    }


    private ResponseDTO handleDeleteKhachHang(RequestDTO request) {
        try {
            String khachHangId = (String) request.getData().get("id");
            khachHangService.delete(khachHangId);
            return new ResponseDTO(true, "Xóa khách hàng thành công");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting customer", e);
            return new ResponseDTO(false, "Lỗi khi xóa khách hàng");
        }
    }

    private ResponseDTO handleGetNhaCungCap(RequestDTO request) {
        String id = (String) request.getData().get("id");
        Optional<NhaCungCap> nhaCungCapOpt = nhaCungCapService.findById(id);

        if (nhaCungCapOpt.isPresent()) {
            return new ResponseDTO(true, "Lấy thông tin nhà cung cấp thành công", (Map<String, Object>) nhaCungCapOpt.get());
        } else {
            return new ResponseDTO(false, "Không tìm thấy nhà cung cấp với ID đã cho");
        }
    }

    private ResponseDTO handleSaveNhanVien(RequestDTO request) {
        NhanVien nhanVien = (NhanVien) request.getData().get("nhanVien");

        if (nhanVienService.save(nhanVien)) {
            return new ResponseDTO(true, "Lưu nhân viên thành công");
        } else {
            return new ResponseDTO(false, "Lưu nhân viên không thành công");
        }
    }



    private ResponseDTO handleDeleteNhaCungCap(RequestDTO request) {
        String id = (String) request.getData().get("id");

        if (nhaCungCapService.delete(id)) {
            return new ResponseDTO(true, "Xóa nhà cung cấp thành công");
        } else {
            return new ResponseDTO(false, "Xóa nhà cung cấp không thành công");
        }
    }


// Các phương thức cho các đối tượng khác như Nhà cung cấp, Khuyến mãi, Nhân viên, Tài khoản sẽ có cấu trúc tương tự.
// Chỉ cần thay đổi tên service và các thao tác tương ứng.


    private ResponseDTO handleLogin(RequestDTO request) {
        String username = (String) request.getData().get("username");
        String password = (String) request.getData().get("password");

        if (taiKhoanService.authenticate(username, password)) {
            ResponseDTO response = new ResponseDTO(true, "Đăng nhập thành công");
            response.addData("user", taiKhoanService.findByUsername(username).get());
            return response;
        } else {
            return new ResponseDTO(false, "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    // Các phương thức xử lý yêu cầu khác...

    public static void main(String[] args) {
        // Khởi tạo kết nối Neo4j
        try {
            Neo4jConfig.getInstance();
            LOGGER.info("Neo4j connection initialized");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing Neo4j connection", e);
            return;
        }

        // Khởi động server
        Server server = new Server();
        server.start();
    }
}