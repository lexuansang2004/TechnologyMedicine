package iuh.fit.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.util.LocalDateAdapter;
import iuh.fit.util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class ClientService {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9876;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson;

    private static ClientService instance;

    private ClientService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResponseDTO sendRequest(RequestDTO request) throws IOException {
        if (socket == null || socket.isClosed()) {
            System.out.println("Socket is null or closed. Connecting...");
            connect();
        }

        String requestJson = gson.toJson(request);
        System.out.println("Gửi yêu cầu: " + requestJson); // Log yêu cầu trước khi gửi

        out.println(requestJson);

        String responseJson = in.readLine();
        System.out.println("Nhận phản hồi: " + responseJson); // Log phản hồi nhận được

        return gson.fromJson(responseJson, ResponseDTO.class);
    }


    public ResponseDTO login(String username, String password) throws IOException {
        // Tạo yêu cầu login
        RequestDTO request = new RequestDTO();
        request.setAction("LOGIN");
        request.addData("username", username);
        request.addData("password", password);

        // In ra các thông tin yêu cầu để kiểm tra
        System.out.println("Gửi yêu cầu login với username: " + username + " và password: " + password);

        // Gửi yêu cầu và nhận phản hồi
        return sendRequest(request);
    }


    // Thuốc
    public ResponseDTO getAllThuoc() throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_ALL_THUOC");

        return sendRequest(request);
    }

    public ResponseDTO getThuocById(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_THUOC_BY_ID");
        request.addData("idThuoc", id);

        return sendRequest(request);
    }

    public ResponseDTO searchThuoc(String keyword) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SEARCH_THUOC");
        request.addData("keyword", keyword);

        return sendRequest(request);
    }

    public ResponseDTO saveThuoc(Object thuoc) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SAVE_THUOC");
        request.addData("thuoc", thuoc);

        return sendRequest(request);
    }

    public ResponseDTO updateThuoc(Object thuoc) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_THUOC");
        request.addData("thuoc", thuoc);

        return sendRequest(request);
    }

    public ResponseDTO deleteThuoc(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_THUOC");
        request.addData("idThuoc", id);

        return sendRequest(request);
    }

    // Khách hàng
    public ResponseDTO getAllKhachHang() throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_ALL_KHACH_HANG");

        return sendRequest(request);
    }

    public ResponseDTO getKhachHangById(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_KHACH_HANG_BY_ID");
        request.addData("idKH", id);

        return sendRequest(request);
    }

    public ResponseDTO searchKhachHang(String keyword) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SEARCH_KHACH_HANG");
        request.addData("keyword", keyword);

        return sendRequest(request);
    }

    public ResponseDTO saveKhachHang(Object khachHang) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SAVE_KHACH_HANG");
        request.addData("khachHang", khachHang);

        return sendRequest(request);
    }

    public ResponseDTO updateKhachHang(Object khachHang) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_KHACH_HANG");
        request.addData("khachHang", khachHang);

        return sendRequest(request);
    }

    public ResponseDTO deleteKhachHang(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_KHACH_HANG");
        request.addData("idKH", id);

        return sendRequest(request);
    }

    public ResponseDTO generateOTP(String idKH) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GENERATE_OTP");
        request.addData("idKH", idKH);

        return sendRequest(request);
    }

    public ResponseDTO verifyOTP(String idKH, String maOTP) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("VERIFY_OTP");
        request.addData("idKH", idKH);
        request.addData("maOTP", maOTP);

        return sendRequest(request);
    }

    // Nhà cung cấp
    public ResponseDTO getAllNhaCungCap() throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_ALL_NHA_CUNG_CAP");

        return sendRequest(request);
    }

    public ResponseDTO getNhaCungCapById(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_NHA_CUNG_CAP_BY_ID");
        request.addData("idNCC", id);

        return sendRequest(request);
    }

    public ResponseDTO saveNhaCungCap(Object nhaCungCap) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SAVE_NHA_CUNG_CAP");
        request.addData("nhaCungCap", nhaCungCap);

        return sendRequest(request);
    }

    public ResponseDTO updateNhaCungCap(Object nhaCungCap) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_NHA_CUNG_CAP");
        request.addData("nhaCungCap", nhaCungCap);

        return sendRequest(request);
    }

    public ResponseDTO deleteNhaCungCap(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_NHA_CUNG_CAP");
        request.addData("idNCC", id);

        return sendRequest(request);
    }

    // Nhân viên
    public ResponseDTO getAllNhanVien() throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_ALL_NHAN_VIEN");

        return sendRequest(request);
    }

    public ResponseDTO getNhanVienById(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_NHAN_VIEN_BY_ID");
        request.addData("idNV", id);

        return sendRequest(request);
    }

    public ResponseDTO saveNhanVien(Object nhanVien) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SAVE_NHAN_VIEN");
        request.addData("nhanVien", nhanVien);

        return sendRequest(request);
    }

    public ResponseDTO updateNhanVien(Object nhanVien) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_NHAN_VIEN");
        request.addData("nhanVien", nhanVien);

        return sendRequest(request);
    }

    public ResponseDTO deleteNhanVien(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_NHAN_VIEN");
        request.addData("idNV", id);

        return sendRequest(request);
    }

    // Tài khoản
    public ResponseDTO createTaiKhoan(Map<String, Object> taiKhoanData) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("CREATE_TAI_KHOAN");
        request.addData("taiKhoan", taiKhoanData);

        return sendRequest(request);
    }

    // Hóa đơn
    public ResponseDTO getAllHoaDon() throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_ALL_HOA_DON");

        return sendRequest(request);
    }

    public ResponseDTO getHoaDonById(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_HOA_DON_BY_ID");
        request.addData("idHD", id);

        return sendRequest(request);
    }

    public ResponseDTO getHoaDonByKhachHang(String idKH) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_HOA_DON_BY_KHACH_HANG");
        request.addData("idKH", idKH);

        return sendRequest(request);
    }

    public ResponseDTO getHoaDonByNhanVien(String idNV) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_HOA_DON_BY_NHAN_VIEN");
        request.addData("idNV", idNV);

        return sendRequest(request);
    }

    public ResponseDTO saveHoaDon(Map<String, Object> hoaDon) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SAVE_HOA_DON");
        request.addData("hoaDon", hoaDon);

        return sendRequest(request);
    }

    public ResponseDTO updateHoaDon(Map<String, Object> hoaDon) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_HOA_DON");
        request.addData("hoaDon", hoaDon);

        return sendRequest(request);
    }

    public ResponseDTO deleteHoaDon(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_HOA_DON");
        request.addData("idHD", id);

        return sendRequest(request);
    }

    public ResponseDTO getChiTietHoaDon(String idHD) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_CHI_TIET_HOA_DON");
        request.addData("idHD", idHD);

        return sendRequest(request);
    }

    public ResponseDTO addChiTietHoaDon(String idHD, Map<String, Object> chiTiet) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("ADD_CHI_TIET_HOA_DON");
        request.addData("idHD", idHD);
        request.addData("chiTiet", chiTiet);

        return sendRequest(request);
    }

    public ResponseDTO updateChiTietHoaDon(Map<String, Object> chiTiet) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_CHI_TIET_HOA_DON");
        request.addData("chiTiet", chiTiet);

        return sendRequest(request);
    }

    public ResponseDTO deleteChiTietHoaDon(String idHD, String idThuoc) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_CHI_TIET_HOA_DON");
        request.addData("idHD", idHD);
        request.addData("idThuoc", idThuoc);

        return sendRequest(request);
    }

    public ResponseDTO updateTongTienHoaDon(String idHD) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_TONG_TIEN_HOA_DON");
        request.addData("idHD", idHD);

        return sendRequest(request);
    }

    // Phiếu nhập
    public ResponseDTO getAllPhieuNhap() throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_ALL_PHIEU_NHAP");

        return sendRequest(request);
    }

    public ResponseDTO getPhieuNhapById(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_PHIEU_NHAP_BY_ID");
        request.addData("idPN", id);

        return sendRequest(request);
    }

    public ResponseDTO getPhieuNhapByNhaCungCap(String idNCC) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_PHIEU_NHAP_BY_NHA_CUNG_CAP");
        request.addData("idNCC", idNCC);

        return sendRequest(request);
    }

    public ResponseDTO getPhieuNhapByNhanVien(String idNV) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_PHIEU_NHAP_BY_NHAN_VIEN");
        request.addData("idNV", idNV);

        return sendRequest(request);
    }

    public ResponseDTO savePhieuNhap(Map<String, Object> phieuNhap) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("SAVE_PHIEU_NHAP");
        request.addData("phieuNhap", phieuNhap);

        return sendRequest(request);
    }

    public ResponseDTO updatePhieuNhap(Map<String, Object> phieuNhap) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_PHIEU_NHAP");
        request.addData("phieuNhap", phieuNhap);

        return sendRequest(request);
    }

    public ResponseDTO deletePhieuNhap(String id) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_PHIEU_NHAP");
        request.addData("idPN", id);

        return sendRequest(request);
    }

    public ResponseDTO getChiTietPhieuNhap(String idPN) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("GET_CHI_TIET_PHIEU_NHAP");
        request.addData("idPN", idPN);

        return sendRequest(request);
    }

    public ResponseDTO addChiTietPhieuNhap(String idPN, Map<String, Object> chiTiet) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("ADD_CHI_TIET_PHIEU_NHAP");
        request.addData("idPN", idPN);
        request.addData("chiTiet", chiTiet);

        return sendRequest(request);
    }

    public ResponseDTO updateChiTietPhieuNhap(Map<String, Object> chiTiet) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_CHI_TIET_PHIEU_NHAP");
        request.addData("chiTiet", chiTiet);

        return sendRequest(request);
    }

    public ResponseDTO deleteChiTietPhieuNhap(String idPN, String idThuoc) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("DELETE_CHI_TIET_PHIEU_NHAP");
        request.addData("idPN", idPN);
        request.addData("idThuoc", idThuoc);

        return sendRequest(request);
    }

    public ResponseDTO updateTongTienPhieuNhap(String idPN) throws IOException {
        RequestDTO request = new RequestDTO();
        request.setAction("UPDATE_TONG_TIEN_PHIEU_NHAP");
        request.addData("idPN", idPN);

        return sendRequest(request);
    }
}