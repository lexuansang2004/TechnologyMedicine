package iuh.fit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iuh.fit.config.Neo4jConfig;
import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.entity.*;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private final HoaDonService hoaDonService;
    private final PhieuNhapService phieuNhapService;

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
        this.hoaDonService = new HoaDonService();
        this.phieuNhapService = new PhieuNhapService();
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
//                case "SEARCH_THUOC":
//                    return handleSearchThuoc(request);
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
                case "GENERATE_OTP":
                    return handleGenerateOTP(request);
                case "VERIFY_OTP":
                    return handleVerifyOTP(request);
                case "UPDATE_CUSTOMER_RANK":
                    return handleUpdateCustomerRank(request);

                // Nhà cung cấp
                case "GET_ALL_NHA_CUNG_CAP":
                    return handleGetAllNhaCungCap();
                case "GET_NHA_CUNG_CAP_BY_ID":
                    return handleGetNhaCungCapById(request);
//                case "SEARCH_NHA_CUNG_CAP":
//                    return handleSearchNhaCungCap(request);
                case "SAVE_NHA_CUNG_CAP":
                    return handleSaveNhaCungCap(request);
                case "UPDATE_NHA_CUNG_CAP":
                    return handleUpdateNhaCungCap(request);
                case "DELETE_NHA_CUNG_CAP":
                    return handleDeleteNhaCungCap(request);

                // Khuyến mãi
                case "GET_ALL_KHUYEN_MAI":
                    return handleGetAllKhuyenMai();
                case "GET_KHUYEN_MAI_BY_ID":
                    return handleGetKhuyenMaiById(request);
                case "GET_KHUYEN_MAI_BY_HANG_MUC":
                    return handleGetKhuyenMaiByHangMuc(request);
                case "GET_KHUYEN_MAI_BY_THUOC":
                    return handleGetKhuyenMaiByThuoc(request);
                case "SAVE_KHUYEN_MAI_HANG_MUC":
                    return handleSaveKhuyenMaiHangMuc(request);
                case "SAVE_KHUYEN_MAI_THUOC":
                    return handleSaveKhuyenMaiThuoc(request);
                case "UPDATE_KHUYEN_MAI":
                    return handleUpdateKhuyenMai(request);
                case "DELETE_KHUYEN_MAI":
                    return handleDeleteKhuyenMai(request);
                case "CALCULATE_DISCOUNTED_TOTAL":
                    return handleCalculateDiscountedTotal(request);
                case "APPLY_PROMOTION_TO_INVOICE":
                    return handleApplyPromotionToInvoice(request);
                case "GET_ELIGIBLE_PROMOTIONS":
                    return handleGetEligiblePromotions(request);
                case "IS_CUSTOMER_ELIGIBLE_FOR_PROMOTION":
                    return handleIsCustomerEligibleForPromotion(request);
                case "GET_MUC_GIAM_GIA":
                    return handleGetMucGiamGia(request);

                // Nhân viên
                case "GET_ALL_NHAN_VIEN":
                    return handleGetAllNhanVien();
                case "GET_NHAN_VIEN_BY_ID":
                    return handleGetNhanVienById(request);
//                case "SEARCH_NHAN_VIEN":
//                    return handleSearchNhanVien(request);
                case "SAVE_NHAN_VIEN":
                    return handleSaveNhanVien(request);
                case "UPDATE_NHAN_VIEN":
                    return handleUpdateNhanVien(request);
                case "DELETE_NHAN_VIEN":
                    return handleDeleteNhanVien(request);

                // Tài khoản
                case "GET_ALL_TAI_KHOAN":
                    return handleGetAllTaiKhoan();
                case "GET_TAI_KHOAN_BY_ID":
                    return handleGetTaiKhoanById(request);
//                case "GET_TAI_KHOAN_BY_NHAN_VIEN":
//                    return handleGetTaiKhoanByNhanVien(request);
                case "CREATE_TAI_KHOAN":
                    return handleCreateTaiKhoan(request);
                case "SAVE_TAI_KHOAN":
                    return handleSaveTaiKhoan(request);
                case "UPDATE_TAI_KHOAN":
                    return handleUpdateTaiKhoan(request);
                case "DELETE_TAI_KHOAN":
                    return handleDeleteTaiKhoan(request);

                // Hóa đơn
                case "GET_ALL_HOA_DON":
                    return handleGetAllHoaDon();
                case "GET_HOA_DON_BY_ID":
                    return handleGetHoaDonById(request);
                case "GET_HOA_DON_BY_KHACH_HANG":
                    return handleGetHoaDonByKhachHang(request);
                case "GET_HOA_DON_BY_NHAN_VIEN":
                    return handleGetHoaDonByNhanVien(request);
                case "SAVE_HOA_DON":
                    return handleSaveHoaDon(request);
                case "UPDATE_HOA_DON":
                    return handleUpdateHoaDon(request);
                case "DELETE_HOA_DON":
                    return handleDeleteHoaDon(request);
                case "GET_CHI_TIET_HOA_DON":
                    return handleGetChiTietHoaDon(request);
                case "ADD_CHI_TIET_HOA_DON":
                    return handleAddChiTietHoaDon(request);
                case "UPDATE_CHI_TIET_HOA_DON":
                    return handleUpdateChiTietHoaDon(request);
                case "DELETE_CHI_TIET_HOA_DON":
                    return handleDeleteChiTietHoaDon(request);
                case "UPDATE_TONG_TIEN_HOA_DON":
                    return handleUpdateTongTienHoaDon(request);

                // Phiếu nhập
                case "GET_ALL_PHIEU_NHAP":
                    return handleGetAllPhieuNhap();
                case "GET_PHIEU_NHAP_BY_ID":
                    return handleGetPhieuNhapById(request);
                case "GET_PHIEU_NHAP_BY_NHA_CUNG_CAP":
                    return handleGetPhieuNhapByNhaCungCap(request);
                case "GET_PHIEU_NHAP_BY_NHAN_VIEN":
                    return handleGetPhieuNhapByNhanVien(request);
                case "SAVE_PHIEU_NHAP":
                    return handleSavePhieuNhap(request);
                case "UPDATE_PHIEU_NHAP":
                    return handleUpdatePhieuNhap(request);
                case "DELETE_PHIEU_NHAP":
                    return handleDeletePhieuNhap(request);
                case "GET_CHI_TIET_PHIEU_NHAP":
                    return handleGetChiTietPhieuNhap(request);
                case "ADD_CHI_TIET_PHIEU_NHAP":
                    return handleAddChiTietPhieuNhap(request);
                case "UPDATE_CHI_TIET_PHIEU_NHAP":
                    return handleUpdateChiTietPhieuNhap(request);
                case "DELETE_CHI_TIET_PHIEU_NHAP":
                    return handleDeleteChiTietPhieuNhap(request);
                case "UPDATE_TONG_TIEN_PHIEU_NHAP":
                    return handleUpdateTongTienPhieuNhap(request);

                default:
                    return new ResponseDTO(false, "Không hỗ trợ hành động: " + action);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing request: " + action, e);
            return new ResponseDTO(false, "Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // Các phương thức xử lý yêu cầu cụ thể

    private ResponseDTO handleLogin(RequestDTO request) {
        String username = (String) request.getData().get("username");
        String password = (String) request.getData().get("password");

        if (taiKhoanService.authenticate(username, password)) {
            ResponseDTO response = new ResponseDTO(true, "Đăng nhập thành công");

            // Kiểm tra Optional trước khi gọi get()
            Optional<Map<String, Object>> userOpt = taiKhoanService.findByUsername(username);
            if (userOpt.isPresent()) {
                response.addData("user", userOpt.get());
            } else {
                // Nếu không tìm thấy thông tin chi tiết, chỉ trả về thông tin cơ bản
                Map<String, Object> basicUserInfo = new HashMap<>();
                basicUserInfo.put("username", username);
                response.addData("user", basicUserInfo);
            }

            return response;
        } else {
            return new ResponseDTO(false, "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    // Xử lý Thuốc
//    private ResponseDTO handleGetAllThuoc() {
//        try {
//            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách thuốc thành công");
//            response.addData("thuocList", thuocService.findAll());
//            return response;
//        } catch (Exception e) {
//            return new ResponseDTO(false, "Lỗi khi lấy danh sách thuốc: " + e.getMessage());
//        }
//    }
    private ResponseDTO handleGetAllThuoc() {
        try {
            List<Thuoc> thuocs = thuocService.findAll();

            // Danh sách thuốc
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> thuocList = new HashMap<>();

            for (Thuoc thuoc : thuocs) {
                Map<String, Object> thuocInfo = new HashMap<>();
                thuocInfo.put("idThuoc", thuoc.getIdThuoc());
                thuocInfo.put("tenThuoc", thuoc.getTenThuoc());
                thuocInfo.put("giaNhap", thuoc.getGiaNhap());
                thuocInfo.put("soLuongTon", thuoc.getSoLuongTon());
                thuocInfo.put("donViTinh", thuoc.getDonViTinh());
                thuocInfo.put("xuatXu", thuoc.getXuatXu());
                thuocInfo.put("danhMuc", thuoc.getDanhMuc());
                thuocInfo.put("hanSuDung", thuoc.getHanSuDung());

                thuocList.put(thuoc.getIdThuoc(), thuocInfo);
            }

            data.put("thuocs", thuocList); // Tách riêng danh sách thuốc vào key "thuocs"

            return new ResponseDTO(true, "Lấy tất cả thuốc thành công", data);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy tất cả thuốc", e);
            return new ResponseDTO(false, "Lỗi lấy tất cả thuốc: " + e.getMessage(), null);
        }
    }

    private ResponseDTO handleGetThuocById(RequestDTO request) {
        try {
            String idThuoc = (String) request.getData().get("idThuoc");
            var thuocOpt = thuocService.findById(idThuoc);

            if (thuocOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin thuốc thành công");
                response.addData("thuoc", thuocOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy thuốc với ID: " + idThuoc);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin thuốc: " + e.getMessage());
        }
    }

    private ResponseDTO handleSaveThuoc(RequestDTO request) {
        try {
            Map<String, Object> thuocData = (Map<String, Object>) request.getData().get("thuoc");
            boolean result = thuocService.save((Map<String, Object>) thuocData);

            if (result) {
                return new ResponseDTO(true, "Thêm thuốc thành công");
            } else {
                return new ResponseDTO(false, "Thêm thuốc thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm thuốc: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateThuoc(RequestDTO request) {
        try {
            Map<String, Object> thuocData = (Map<String, Object>) request.getData().get("thuoc");
            boolean result = thuocService.update((Map<String, Object>) thuocData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật thuốc thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật thuốc thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật thuốc: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteThuoc(RequestDTO request) {
        try {
            String idThuoc = (String) request.getData().get("idThuoc");
            boolean result = thuocService.delete(idThuoc);

            if (result) {
                return new ResponseDTO(true, "Xóa thuốc thành công");
            } else {
                return new ResponseDTO(false, "Xóa thuốc thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa thuốc: " + e.getMessage());
        }
    }

    // Xử lý Khách Hàng
    private ResponseDTO handleGetAllKhachHang() {
        try {
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách khách hàng thành công");
            response.addData("khachHangList", khachHangService.findAll());
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách khách hàng: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetKhachHangById(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            var khachHangOpt = khachHangService.findById(idKH);

            if (khachHangOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin khách hàng thành công");
                response.addData("khachHang", khachHangOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy khách hàng với ID: " + idKH);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin khách hàng: " + e.getMessage());
        }
    }

    private ResponseDTO handleSaveKhachHang(RequestDTO request) {
        try {
            Map<String, Object> khachHangData = (Map<String, Object>) request.getData().get("khachHang");
            boolean result = khachHangService.save((Map<String, Object>) khachHangData);

            if (result) {
                return new ResponseDTO(true, "Thêm khách hàng thành công");
            } else {
                return new ResponseDTO(false, "Thêm khách hàng thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm khách hàng: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateKhachHang(RequestDTO request) {
        try {
            Map<String, Object> khachHangData = (Map<String, Object>) request.getData().get("khachHang");
            boolean result = khachHangService.update((Map<String, Object>) khachHangData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật khách hàng thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật khách hàng thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật khách hàng: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteKhachHang(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            boolean result = khachHangService.delete(idKH);

            if (result) {
                return new ResponseDTO(true, "Xóa khách hàng thành công");
            } else {
                return new ResponseDTO(false, "Xóa khách hàng thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa khách hàng: " + e.getMessage());
        }
    }

    private ResponseDTO handleGenerateOTP(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            String otp = String.valueOf(khachHangService.generateOTP(idKH));

            if (otp != null) {
                ResponseDTO response = new ResponseDTO(true, "Tạo OTP thành công");
                response.addData("otp", otp);
                return response;
            } else {
                return new ResponseDTO(false, "Tạo OTP thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi tạo OTP: " + e.getMessage());
        }
    }

    private ResponseDTO handleVerifyOTP(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            String otp = (String) request.getData().get("otp");
            boolean result = khachHangService.verifyOTP(idKH, otp);

            if (result) {
                return new ResponseDTO(true, "Xác thực OTP thành công");
            } else {
                return new ResponseDTO(false, "Xác thực OTP thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xác thực OTP: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateCustomerRank(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            double tongTien = Double.parseDouble(request.getData().get("tongTien").toString());

            Map<String, Object> result = khachHangService.updateCustomerRank(idKH, tongTien);

            if (result != null) {
                ResponseDTO response = new ResponseDTO(true, "Cập nhật hạng mục khách hàng thành công");
                response.addData("result", result);
                return response;
            } else {
                return new ResponseDTO(false, "Cập nhật hạng mục khách hàng thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật hạng mục khách hàng: " + e.getMessage());
        }
    }

    // Xử lý Nhà Cung Cấp
    private ResponseDTO handleGetAllNhaCungCap() {
        try {
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách nhà cung cấp thành công");
            response.addData("nhaCungCapList", nhaCungCapService.findAll());
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách nhà cung cấp: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetNhaCungCapById(RequestDTO request) {
        try {
            String idNCC = (String) request.getData().get("idNCC");
            var nhaCungCapOpt = nhaCungCapService.findById(idNCC);

            if (nhaCungCapOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin nhà cung cấp thành công");
                response.addData("nhaCungCap", nhaCungCapOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy nhà cung cấp với ID: " + idNCC);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin nhà cung cấp: " + e.getMessage());
        }
    }

    private ResponseDTO handleSaveNhaCungCap(RequestDTO request) {
        try {
            Map<String, Object> nhaCungCapData = (Map<String, Object>) request.getData().get("nhaCungCap");
            boolean result = nhaCungCapService.save((NhaCungCap) nhaCungCapData);

            if (result) {
                return new ResponseDTO(true, "Thêm nhà cung cấp thành công");
            } else {
                return new ResponseDTO(false, "Thêm nhà cung cấp thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm nhà cung cấp: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateNhaCungCap(RequestDTO request) {
        try {
            Map<String, Object> nhaCungCapData = (Map<String, Object>) request.getData().get("nhaCungCap");
            boolean result = nhaCungCapService.update((NhaCungCap) nhaCungCapData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật nhà cung cấp thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật nhà cung cấp thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật nhà cung cấp: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteNhaCungCap(RequestDTO request) {
        try {
            String idNCC = (String) request.getData().get("idNCC");
            boolean result = nhaCungCapService.delete(idNCC);

            if (result) {
                return new ResponseDTO(true, "Xóa nhà cung cấp thành công");
            } else {
                return new ResponseDTO(false, "Xóa nhà cung cấp thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa nhà cung cấp: " + e.getMessage());
        }
    }

    // Xử lý Khuyến Mãi
    private ResponseDTO handleGetAllKhuyenMai() {
        try {
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách khuyến mãi thành công");
            response.addData("khuyenMaiList", khuyenMaiService.findAll());
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách khuyến mãi: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetKhuyenMaiById(RequestDTO request) {
        try {
            String idKM = (String) request.getData().get("idKM");
            var khuyenMaiOpt = khuyenMaiService.findById(idKM);

            if (khuyenMaiOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin khuyến mãi thành công");
                response.addData("khuyenMai", khuyenMaiOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy khuyến mãi với ID: " + idKM);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin khuyến mãi: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetKhuyenMaiByHangMuc(RequestDTO request) {
        try {
            String hangMuc = (String) request.getData().get("hangMuc");
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách khuyến mãi theo hạng mục thành công");
            response.addData("khuyenMaiList", khuyenMaiService.findByHangMuc(hangMuc));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách khuyến mãi theo hạng mục: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetKhuyenMaiByThuoc(RequestDTO request) {
        try {
            String idThuoc = (String) request.getData().get("idThuoc");
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách khuyến mãi theo thuốc thành công");
            response.addData("khuyenMaiList", khuyenMaiService.findByThuoc(idThuoc));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách khuyến mãi theo thuốc: " + e.getMessage());
        }
    }

    // Sửa phương thức handleSaveKhuyenMaiHangMuc
    private ResponseDTO handleSaveKhuyenMaiHangMuc(RequestDTO request) {
        try {
            Map<String, Object> khuyenMaiData = (Map<String, Object>) request.getData().get("khuyenMai");
            // Đảm bảo khuyenMaiData có trường hangMuc
            if (!khuyenMaiData.containsKey("hangMuc")) {
                return new ResponseDTO(false, "Thiếu thông tin hạng mục cho khuyến mãi");
            }

            boolean result = khuyenMaiService.save(khuyenMaiData);

            if (result) {
                return new ResponseDTO(true, "Thêm khuyến mãi hạng mục thành công");
            } else {
                return new ResponseDTO(false, "Thêm khuyến mãi hạng mục thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm khuyến mãi hạng mục: " + e.getMessage());
        }
    }

    // Sửa phương thức handleSaveKhuyenMaiThuoc
    private ResponseDTO handleSaveKhuyenMaiThuoc(RequestDTO request) {
        try {
            Map<String, Object> khuyenMaiData = (Map<String, Object>) request.getData().get("khuyenMai");
            // Đảm bảo khuyenMaiData có trường idThuoc
            if (!khuyenMaiData.containsKey("idThuoc")) {
                return new ResponseDTO(false, "Thiếu thông tin thuốc cho khuyến mãi");
            }

            boolean result = khuyenMaiService.save(khuyenMaiData);

            if (result) {
                return new ResponseDTO(true, "Thêm khuyến mãi thuốc thành công");
            } else {
                return new ResponseDTO(false, "Thêm khuyến mãi thuốc thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm khuyến mãi thuốc: " + e.getMessage());
        }
    }

    // Sửa phương thức handleUpdateKhuyenMai
    private ResponseDTO handleUpdateKhuyenMai(RequestDTO request) {
        try {
            Map<String, Object> khuyenMaiData = (Map<String, Object>) request.getData().get("khuyenMai");
            boolean result = khuyenMaiService.update(khuyenMaiData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật khuyến mãi thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật khuyến mãi thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật khuyến mãi: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteKhuyenMai(RequestDTO request) {
        try {
            String idKM = (String) request.getData().get("idKM");
            boolean result = khuyenMaiService.delete(idKM);

            if (result) {
                return new ResponseDTO(true, "Xóa khuyến mãi thành công");
            } else {
                return new ResponseDTO(false, "Xóa khuyến mãi thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa khuyến mãi: " + e.getMessage());
        }
    }

    // Sửa phương thức handleCalculateDiscountedTotal
    private ResponseDTO handleCalculateDiscountedTotal(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            // Sử dụng CypherQueryUtil thay vì khuyenMaiService
            Map<String, Object> result = iuh.fit.util.CypherQueryUtil.calculateDiscountedTotal(idHD);

            if (result != null) {
                ResponseDTO response = new ResponseDTO(true, "Tính tổng tiền sau khuyến mãi thành công");
                response.addData("result", result);
                return response;
            } else {
                return new ResponseDTO(false, "Tính tổng tiền sau khuyến mãi thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi tính tổng tiền sau khuyến mãi: " + e.getMessage());
        }
    }

    // Sửa phương thức handleApplyPromotionToInvoice
    private ResponseDTO handleApplyPromotionToInvoice(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            String idKM = (String) request.getData().get("idKM");

            // Sử dụng CypherQueryUtil thay vì khuyenMaiService
            boolean result = iuh.fit.util.CypherQueryUtil.applyPromotionToInvoice(idHD, idKM);

            if (result) {
                return new ResponseDTO(true, "Áp dụng khuyến mãi cho hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Áp dụng khuyến mãi cho hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi áp dụng khuyến mãi cho hóa đơn: " + e.getMessage());
        }
    }

    // Sửa phương thức handleGetEligiblePromotions
    private ResponseDTO handleGetEligiblePromotions(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            // Sử dụng CypherQueryUtil thay vì khuyenMaiService
            List<Map<String, Object>> promotions = iuh.fit.util.CypherQueryUtil.getEligiblePromotions(idKH);

            if (promotions != null) {
                ResponseDTO response = new ResponseDTO(true, "Lấy danh sách khuyến mãi áp dụng được thành công");
                response.addData("promotions", promotions);
                return response;
            } else {
                return new ResponseDTO(false, "Lấy danh sách khuyến mãi áp dụng được thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách khuyến mãi áp dụng được: " + e.getMessage());
        }
    }

    // Sửa phương thức handleIsCustomerEligibleForPromotion
    private ResponseDTO handleIsCustomerEligibleForPromotion(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            String idKM = (String) request.getData().get("idKM");

            // Sử dụng CypherQueryUtil thay vì khuyenMaiService
            boolean result = iuh.fit.util.CypherQueryUtil.isCustomerEligibleForPromotion(idKH, idKM);

            ResponseDTO response = new ResponseDTO(true, "Kiểm tra điều kiện khuyến mãi thành công");
            response.addData("eligible", result);
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi kiểm tra điều kiện khuyến mãi: " + e.getMessage());
        }
    }

    // Sửa phương thức handleGetMucGiamGia
    private ResponseDTO handleGetMucGiamGia(RequestDTO request) {
        try {
            String loai = (String) request.getData().get("loai");
            String hangMuc = (String) request.getData().get("hangMuc");

            // Sử dụng CypherQueryUtil thay vì khuyenMaiService
            double mucGiamGia = iuh.fit.util.CypherQueryUtil.getMucGiamGia(loai, hangMuc);

            ResponseDTO response = new ResponseDTO(true, "Lấy mức giảm giá thành công");
            response.addData("mucGiamGia", mucGiamGia);
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy mức giảm giá: " + e.getMessage());
        }
    }

    // Xử lý Nhân Viên
    // Thêm vào lớp Server
    private ResponseDTO handleGetAllNhanVien() {
        try {
            List<Map<String, Object>> nhanVienList = nhanVienService.getAllNhanVienWithAccountStatus();

            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách nhân viên thành công");
            response.addData("nhanVienList", nhanVienList);

            return response;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách nhân viên", e);
            return new ResponseDTO(false, "Lỗi lấy danh sách nhân viên: " + e.getMessage());
        }
    }

    private ResponseDTO handleCreateTaiKhoan(RequestDTO request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> taiKhoanData = (Map<String, Object>) request.getData().get("taiKhoan");

            boolean result = taiKhoanService.createTaiKhoan(taiKhoanData);

            if (result) {
                return new ResponseDTO(true, "Tạo tài khoản thành công");
            } else {
                return new ResponseDTO(false, "Không thể tạo tài khoản");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi tạo tài khoản", e);
            return new ResponseDTO(false, "Lỗi tạo tài khoản: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetNhanVienById(RequestDTO request) {
        try {
            String idNV = (String) request.getData().get("idNV");
            var nhanVienOpt = nhanVienService.findById(idNV);

            if (nhanVienOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin nhân viên thành công");
                response.addData("nhanVien", nhanVienOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy nhân viên với ID: " + idNV);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin nhân viên: " + e.getMessage());
        }
    }

    private ResponseDTO handleSaveNhanVien(RequestDTO request) {
        try {
            Map<String, Object> nhanVienData = (Map<String, Object>) request.getData().get("nhanVien");
            boolean result = nhanVienService.save((NhanVien) nhanVienData);

            if (result) {
                return new ResponseDTO(true, "Thêm nhân viên thành công");
            } else {
                return new ResponseDTO(false, "Thêm nhân viên thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateNhanVien(RequestDTO request) {
        try {
            Map<String, Object> nhanVienData = (Map<String, Object>) request.getData().get("nhanVien");
            boolean result = nhanVienService.update((NhanVien) nhanVienData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật nhân viên thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật nhân viên thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật nhân viên: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteNhanVien(RequestDTO request) {
        try {
            String idNV = (String) request.getData().get("idNV");
            boolean result = nhanVienService.delete(idNV);

            if (result) {
                return new ResponseDTO(true, "Xóa nhân viên thành công");
            } else {
                return new ResponseDTO(false, "Xóa nhân viên thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa nhân viên: " + e.getMessage());
        }
    }

    // Xử lý Tài Khoản
    private ResponseDTO handleGetAllTaiKhoan() {
        try {
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách tài khoản thành công");
            response.addData("taiKhoanList", taiKhoanService.findAll());
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetTaiKhoanById(RequestDTO request) {
        try {
            String idTK = (String) request.getData().get("idTK");
            var taiKhoanOpt = taiKhoanService.findById(idTK);

            if (taiKhoanOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin tài khoản thành công");
                response.addData("taiKhoan", taiKhoanOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy tài khoản với ID: " + idTK);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin tài khoản: " + e.getMessage());
        }
    }

    private ResponseDTO handleSaveTaiKhoan(RequestDTO request) {
        try {
            Map<String, Object> taiKhoanData = (Map<String, Object>) request.getData().get("taiKhoan");
            boolean result = taiKhoanService.save((Map<String, Object>) taiKhoanData);

            if (result) {
                return new ResponseDTO(true, "Thêm tài khoản thành công");
            } else {
                return new ResponseDTO(false, "Thêm tài khoản thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm tài khoản: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateTaiKhoan(RequestDTO request) {
        try {
            Map<String, Object> taiKhoanData = (Map<String, Object>) request.getData().get("taiKhoan");
            boolean result = taiKhoanService.update((Map<String, Object>) taiKhoanData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật tài khoản thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật tài khoản thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật tài khoản: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteTaiKhoan(RequestDTO request) {
        try {
            String idTK = (String) request.getData().get("idTK");
            boolean result = taiKhoanService.delete(idTK);

            if (result) {
                return new ResponseDTO(true, "Xóa tài khoản thành công");
            } else {
                return new ResponseDTO(false, "Xóa tài khoản thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

    // Xử lý Hóa Đơn
//    private ResponseDTO handleGetAllHoaDon() {
//        try {
//            List<Map<String, Object>> hoaDonList = hoaDonService.findAll();
//            System.out.println("HoaDonService.findAll() returned: " + hoaDonList);
//            System.out.println("Number of records: " + (hoaDonList != null ? hoaDonList.size() : "null"));
//
//            if (hoaDonList != null && !hoaDonList.isEmpty()) {
//                // In ra thông tin chi tiết của bản ghi đầu tiên để kiểm tra
//                System.out.println("First record details: " + hoaDonList.get(0));
//            }
//
//            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách hóa đơn thành công");
//            response.addData("hoaDonList", hoaDonList);
//            return response;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseDTO(false, "Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
//        }
//    }
    private ResponseDTO handleGetAllHoaDon() {
        try {
            List<Map<String, Object>> hoaDonList = hoaDonService.findAll();
            System.out.println("hoaDonService.findAll() returned: " + hoaDonList);
            System.out.println("Number of records: " + (hoaDonList != null ? hoaDonList.size() : "null"));

            if (hoaDonList != null && !hoaDonList.isEmpty()) {
                // In ra thông tin chi tiết của bản ghi đầu tiên để kiểm tra
                System.out.println("First record details: " + hoaDonList.get(0));
            }

            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách hóa đơn thành công");
            response.addData("hoaDonList", hoaDonList);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(false, "Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetHoaDonById(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            var hoaDonOpt = hoaDonService.findById(idHD);

            if (hoaDonOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin hóa đơn thành công");
                response.addData("hoaDon", hoaDonOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy hóa đơn với ID: " + idHD);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetHoaDonByKhachHang(RequestDTO request) {
        try {
            String idKH = (String) request.getData().get("idKH");
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách hóa đơn theo khách hàng thành công");
            response.addData("hoaDonList", hoaDonService.findByKhachHang(idKH));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách hóa đơn theo khách hàng: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetHoaDonByNhanVien(RequestDTO request) {
        try {
            String idNV = (String) request.getData().get("idNV");
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách hóa đơn theo nhân viên thành công");
            response.addData("hoaDonList", hoaDonService.findByNhanVien(idNV));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách hóa đơn theo nhân viên: " + e.getMessage());
        }
    }

    private ResponseDTO handleSaveHoaDon(RequestDTO request) {
        try {
            Map<String, Object> hoaDonData = (Map<String, Object>) request.getData().get("hoaDon");
            boolean result = hoaDonService.save(hoaDonData);

            if (result) {
                return new ResponseDTO(true, "Thêm hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Thêm hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateHoaDon(RequestDTO request) {
        try {
            Map<String, Object> hoaDonData = (Map<String, Object>) request.getData().get("hoaDon");
            boolean result = hoaDonService.update(hoaDonData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteHoaDon(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            boolean result = hoaDonService.delete(idHD);

            if (result) {
                return new ResponseDTO(true, "Xóa hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Xóa hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetChiTietHoaDon(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            ResponseDTO response = new ResponseDTO(true, "Lấy chi tiết hóa đơn thành công");
            response.addData("chiTietList", hoaDonService.getChiTietHoaDon(idHD));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleAddChiTietHoaDon(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            Map<String, Object> chiTietData = (Map<String, Object>) request.getData().get("chiTiet");
            boolean result = hoaDonService.addChiTietHoaDon(idHD, chiTietData);

            if (result) {
                // Cập nhật tổng tiền hóa đơn
                hoaDonService.updateTongTien(idHD);
                return new ResponseDTO(true, "Thêm chi tiết hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Thêm chi tiết hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateChiTietHoaDon(RequestDTO request) {
        try {
            Map<String, Object> chiTietData = (Map<String, Object>) request.getData().get("chiTiet");
            boolean result = hoaDonService.updateChiTietHoaDon(chiTietData);

            if (result) {
                // Cập nhật tổng tiền hóa đơn
                String idHD = (String) chiTietData.get("idHD");
                hoaDonService.updateTongTien(idHD);
                return new ResponseDTO(true, "Cập nhật chi tiết hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật chi tiết hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteChiTietHoaDon(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            String idThuoc = (String) request.getData().get("idThuoc");
            boolean result = hoaDonService.deleteChiTietHoaDon(idHD, idThuoc);

            if (result) {
                // Cập nhật tổng tiền hóa đơn
                hoaDonService.updateTongTien(idHD);
                return new ResponseDTO(true, "Xóa chi tiết hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Xóa chi tiết hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa chi tiết hóa đơn: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateTongTienHoaDon(RequestDTO request) {
        try {
            String idHD = (String) request.getData().get("idHD");
            boolean result = hoaDonService.updateTongTien(idHD);

            if (result) {
                return new ResponseDTO(true, "Cập nhật tổng tiền hóa đơn thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật tổng tiền hóa đơn thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật tổng tiền hóa đơn: " + e.getMessage());
        }
    }

    // Xử lý Phiếu Nhập
    private ResponseDTO handleGetAllPhieuNhap() {
        try {
            List<Map<String, Object>> phieuNhapList = phieuNhapService.findAll();
            System.out.println("PhieuNhapService.findAll() returned: " + phieuNhapList);
            System.out.println("Number of records: " + (phieuNhapList != null ? phieuNhapList.size() : "null"));

            if (phieuNhapList != null && !phieuNhapList.isEmpty()) {
                // In ra thông tin chi tiết của bản ghi đầu tiên để kiểm tra
                System.out.println("First record details: " + phieuNhapList.get(0));
            }

            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách phiếu nhập thành công");
            response.addData("phieuNhapList", phieuNhapList);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(false, "Lỗi khi lấy danh sách phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetPhieuNhapById(RequestDTO request) {
        try {
            String idPN = (String) request.getData().get("idPN");
            var phieuNhapOpt = phieuNhapService.findById(idPN);

            if (phieuNhapOpt.isPresent()) {
                ResponseDTO response = new ResponseDTO(true, "Lấy thông tin phiếu nhập thành công");
                response.addData("phieuNhap", phieuNhapOpt.get());
                return response;
            } else {
                return new ResponseDTO(false, "Không tìm thấy phiếu nhập với ID: " + idPN);
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy thông tin phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetPhieuNhapByNhaCungCap(RequestDTO request) {
        try {
            String idNCC = (String) request.getData().get("idNCC");
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách phiếu nhập theo nhà cung cấp thành công");
            response.addData("phieuNhapList", phieuNhapService.findByNhaCungCap(idNCC));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách phiếu nhập theo nhà cung cấp: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetPhieuNhapByNhanVien(RequestDTO request) {
        try {
            String idNV = (String) request.getData().get("idNV");
            ResponseDTO response = new ResponseDTO(true, "Lấy danh sách phiếu nhập theo nhân viên thành công");
            response.addData("phieuNhapList", phieuNhapService.findByNhanVien(idNV));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy danh sách phiếu nhập theo nhân viên: " + e.getMessage());
        }
    }

    private ResponseDTO handleSavePhieuNhap(RequestDTO request) {
        try {
            Map<String, Object> phieuNhapData = (Map<String, Object>) request.getData().get("phieuNhap");
            boolean result = phieuNhapService.save(phieuNhapData);

            if (result) {
                return new ResponseDTO(true, "Thêm phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Thêm phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdatePhieuNhap(RequestDTO request) {
        try {
            Map<String, Object> phieuNhapData = (Map<String, Object>) request.getData().get("phieuNhap");
            boolean result = phieuNhapService.update(phieuNhapData);

            if (result) {
                return new ResponseDTO(true, "Cập nhật phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeletePhieuNhap(RequestDTO request) {
        try {
            String idPN = (String) request.getData().get("idPN");
            boolean result = phieuNhapService.delete(idPN);

            if (result) {
                return new ResponseDTO(true, "Xóa phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Xóa phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleGetChiTietPhieuNhap(RequestDTO request) {
        try {
            String idPN = (String) request.getData().get("idPN");
            ResponseDTO response = new ResponseDTO(true, "Lấy chi tiết phiếu nhập thành công");
            response.addData("chiTietList", phieuNhapService.getChiTietPhieuNhap(idPN));
            return response;
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi lấy chi tiết phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleAddChiTietPhieuNhap(RequestDTO request) {
        try {
            String idPN = (String) request.getData().get("idPN");
            Map<String, Object> chiTietData = (Map<String, Object>) request.getData().get("chiTiet");
            boolean result = phieuNhapService.addChiTietPhieuNhap(idPN, chiTietData);

            if (result) {
                // Cập nhật tổng tiền phiếu nhập
                phieuNhapService.updateTongTien(idPN);
                return new ResponseDTO(true, "Thêm chi tiết phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Thêm chi tiết phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi thêm chi tiết phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateChiTietPhieuNhap(RequestDTO request) {
        try {
            Map<String, Object> chiTietData = (Map<String, Object>) request.getData().get("chiTiet");
            boolean result = phieuNhapService.updateChiTietPhieuNhap(chiTietData);

            if (result) {
                // Cập nhật tổng tiền phiếu nhập
                String idPN = (String) chiTietData.get("idPN");
                phieuNhapService.updateTongTien(idPN);
                return new ResponseDTO(true, "Cập nhật chi tiết phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật chi tiết phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật chi tiết phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleDeleteChiTietPhieuNhap(RequestDTO request) {
        try {
            String idPN = (String) request.getData().get("idPN");
            String idThuoc = (String) request.getData().get("idThuoc");
            boolean result = phieuNhapService.deleteChiTietPhieuNhap(idPN, idThuoc);

            if (result) {
                // Cập nhật tổng tiền phiếu nhập
                phieuNhapService.updateTongTien(idPN);
                return new ResponseDTO(true, "Xóa chi tiết phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Xóa chi tiết phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi xóa chi tiết phiếu nhập: " + e.getMessage());
        }
    }

    private ResponseDTO handleUpdateTongTienPhieuNhap(RequestDTO request) {
        try {
            String idPN = (String) request.getData().get("idPN");
            boolean result = phieuNhapService.updateTongTien(idPN);

            if (result) {
                return new ResponseDTO(true, "Cập nhật tổng tiền phiếu nhập thành công");
            } else {
                return new ResponseDTO(false, "Cập nhật tổng tiền phiếu nhập thất bại");
            }
        } catch (Exception e) {
            return new ResponseDTO(false, "Lỗi khi cập nhật tổng tiền phiếu nhập: " + e.getMessage());
        }
    }

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
