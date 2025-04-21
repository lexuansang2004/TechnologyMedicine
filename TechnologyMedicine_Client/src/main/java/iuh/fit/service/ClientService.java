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

    // Các phương thức khác cho nhà cung cấp, khuyến mãi, nhân viên, tài khoản...
}