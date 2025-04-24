package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoaDonPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;
    private JButton btnThem, btnXem, btnTimKiem, btnLamMoi;
    private JTextField txtTimKiem;
    private ClientService clientService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public HoaDonPanel(ClientService clientService) {
        this.clientService = clientService;
        initComponents();
        setupLayout();
        registerEvents();
        loadHoaDonData();
    }

    private void initComponents() {
        // Khởi tạo bảng
        String[] columnNames = {"Mã HD", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền"};
        modelHoaDon = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHoaDon = new JTable(modelHoaDon);

        // Khởi tạo các nút và trường tìm kiếm
        btnThem = new JButton("Tạo hóa đơn mới");
        btnXem = new JButton("Xem chi tiết");
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm kiếm");
        btnLamMoi = new JButton("Làm mới");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel chứa các nút chức năng
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(btnThem);
        pnlTop.add(btnXem);
        pnlTop.add(new JLabel("Tìm kiếm:"));
        pnlTop.add(txtTimKiem);
        pnlTop.add(btnTimKiem);
        pnlTop.add(btnLamMoi);

        // Panel chứa bảng dữ liệu
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        // Thêm các panel vào panel chính
        add(pnlTop, BorderLayout.NORTH);
        add(pnlTable, BorderLayout.CENTER);
    }

    private void registerEvents() {
        btnThem.addActionListener(e -> showTaoHoaDonDialog());
        btnXem.addActionListener(e -> showChiTietHoaDon());
        btnTimKiem.addActionListener(e -> searchHoaDon());
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadHoaDonData();
        });
    }

    private void loadHoaDonData() {
        try {
            // Xóa dữ liệu cũ
            modelHoaDon.setRowCount(0);

            // Lấy danh sách hóa đơn từ server
            ResponseDTO response = clientService.getAllHoaDon();

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> hoaDonList = (List<Map<String, Object>>) response.getData().get("hoaDonList");

                if (hoaDonList != null && !hoaDonList.isEmpty()) {
                    for (Map<String, Object> hoaDon : hoaDonList) {
                        String idHD = (String) hoaDon.get("idHD");

                        // Xử lý ngày lập
                        String ngayLapStr = formatNgayLap(hoaDon.get("ngayLap"));

                        // Lấy thông tin khách hàng
                        String tenKH = getTenKhachHang(hoaDon);

                        // Lấy thông tin nhân viên
                        String tenNV = getTenNhanVien(hoaDon);

                        // Xử lý tổng tiền
                        String tongTienStr = formatTongTien(hoaDon.get("tongTien"));

                        // Thêm dòng vào bảng
                        modelHoaDon.addRow(new Object[]{idHD, ngayLapStr, tenKH, tenNV, tongTienStr});
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không có dữ liệu hóa đơn nào được tìm thấy.",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tải danh sách hóa đơn: " + (response != null ? response.getMessage() : "Không có phản hồi từ server"),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi không xác định: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String formatNgayLap(Object ngayLapObj) {
        if (ngayLapObj == null) return "N/A";

        try {
            if (ngayLapObj instanceof LocalDateTime) {
                return ((LocalDateTime) ngayLapObj).format(dateFormatter);
            } else if (ngayLapObj instanceof String) {
                try {
                    LocalDateTime ngayLap = LocalDateTime.parse((String) ngayLapObj);
                    return ngayLap.format(dateFormatter);
                } catch (Exception e) {
                    return (String) ngayLapObj;
                }
            } else {
                return ngayLapObj.toString();
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String getTenKhachHang(Map<String, Object> hoaDon) {
        try {
            if (hoaDon.containsKey("khachHang")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> khachHang = (Map<String, Object>) hoaDon.get("khachHang");
                if (khachHang != null && khachHang.containsKey("hoTen")) {
                    String hoTen = (String) khachHang.get("hoTen");
                    // Nếu hoTen bắt đầu bằng "KH: ", lấy thông tin chi tiết từ server
                    if (hoTen.startsWith("KH: ")) {
                        String idKH = (String) khachHang.get("idKH");
                        return getKhachHangNameById(idKH);
                    }
                    return hoTen;
                } else if (khachHang != null && khachHang.containsKey("idKH")) {
                    // Nếu không có tên, lấy thông tin khách hàng từ server
                    String idKH = (String) khachHang.get("idKH");
                    return getKhachHangNameById(idKH);
                }
            } else if (hoaDon.containsKey("idKH")) {
                // Nếu chỉ có ID khách hàng, lấy thông tin từ server
                String idKH = (String) hoaDon.get("idKH");
                return getKhachHangNameById(idKH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private String getTenNhanVien(Map<String, Object> hoaDon) {
        try {
            if (hoaDon.containsKey("nhanVien")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nhanVien = (Map<String, Object>) hoaDon.get("nhanVien");
                if (nhanVien != null && nhanVien.containsKey("hoTen")) {
                    String hoTen = (String) nhanVien.get("hoTen");
                    // Nếu hoTen bắt đầu bằng "NV: ", lấy thông tin chi tiết từ server
                    if (hoTen.startsWith("NV: ")) {
                        String idNV = (String) nhanVien.get("idNV");
                        return getNhanVienNameById(idNV);
                    }
                    return hoTen;
                } else if (nhanVien != null && nhanVien.containsKey("idNV")) {
                    // Nếu không có tên, lấy thông tin nhân viên từ server
                    String idNV = (String) nhanVien.get("idNV");
                    return getNhanVienNameById(idNV);
                }
            } else if (hoaDon.containsKey("idNV")) {
                // Nếu chỉ có ID nhân viên, lấy thông tin từ server
                String idNV = (String) hoaDon.get("idNV");
                return getNhanVienNameById(idNV);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private String formatTongTien(Object tongTienObj) {
        if (tongTienObj == null) return "0";

        try {
            double tongTien;
            if (tongTienObj instanceof Number) {
                tongTien = ((Number) tongTienObj).doubleValue();
            } else {
                tongTien = Double.parseDouble(tongTienObj.toString());
            }
            return decimalFormat.format(tongTien);
        } catch (Exception e) {
            return "0";
        }
    }

    private String getKhachHangNameById(String idKH) {
        try {
            RequestDTO request = new RequestDTO("GET_KHACH_HANG_BY_ID", new HashMap<>());
            request.addData("idKH", idKH);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> khachHang = (Map<String, Object>) response.getData().get("khachHang");
                if (khachHang != null && khachHang.containsKey("hoTen")) {
                    return (String) khachHang.get("hoTen");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "KH: " + idKH;
    }

    private String getNhanVienNameById(String idNV) {
        try {
            RequestDTO request = new RequestDTO("GET_NHAN_VIEN_BY_ID", new HashMap<>());
            request.addData("idNV", idNV);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nhanVien = (Map<String, Object>) response.getData().get("nhanVien");
                if (nhanVien != null && nhanVien.containsKey("hoTen")) {
                    return (String) nhanVien.get("hoTen");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NV: " + idNV;
    }

    private void showTaoHoaDonDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo hóa đơn mới", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel thông tin hóa đơn
        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Mã hóa đơn (tự động sinh)
        gbc.gridx = 0; gbc.gridy = 0;
        pnlInfo.add(new JLabel("Mã hóa đơn:"), gbc);

        JTextField txtIdHD = new JTextField(20);
        txtIdHD.setEditable(false);
        txtIdHD.setText("HD" + System.currentTimeMillis() % 10000); // ID tạm thời
        gbc.gridx = 1;
        pnlInfo.add(txtIdHD, gbc);

        // Ngày lập (mặc định là ngày hiện tại)
        gbc.gridx = 0; gbc.gridy = 1;
        pnlInfo.add(new JLabel("Ngày lập:"), gbc);

        JTextField txtNgayLap = new JTextField(20);
        txtNgayLap.setEditable(false);
        txtNgayLap.setText(LocalDate.now().toString());
        gbc.gridx = 1;
        pnlInfo.add(txtNgayLap, gbc);

        // Khách hàng
        gbc.gridx = 0; gbc.gridy = 2;
        pnlInfo.add(new JLabel("Khách hàng:"), gbc);

        JComboBox<String> cboKhachHang = new JComboBox<>();
        loadKhachHangComboBox(cboKhachHang);
        gbc.gridx = 1;
        pnlInfo.add(cboKhachHang, gbc);

        // Nhân viên
        gbc.gridx = 0; gbc.gridy = 3;
        pnlInfo.add(new JLabel("Nhân viên:"), gbc);

        JComboBox<String> cboNhanVien = new JComboBox<>();
        loadNhanVienComboBox(cboNhanVien);
        gbc.gridx = 1;
        pnlInfo.add(cboNhanVien, gbc);

        // Panel chi tiết hóa đơn
        JPanel pnlChiTiet = new JPanel(new BorderLayout(5, 5));
        pnlChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

        // Bảng chi tiết
        String[] chiTietColumns = {"Mã thuốc", "Tên thuốc", "Đơn giá", "Số lượng", "Thành tiền"};
        DefaultTableModel modelChiTiet = new DefaultTableModel(chiTietColumns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cho phép sửa số lượng
            }
        };
        JTable tblChiTiet = new JTable(modelChiTiet);
        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        pnlChiTiet.add(scrollChiTiet, BorderLayout.CENTER);

        // Panel thêm thuốc
        JPanel pnlAddThuoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cboThuoc = new JComboBox<>();
        loadThuocComboBox(cboThuoc);
        JSpinner spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        JButton btnAddThuoc = new JButton("Thêm thuốc");

        pnlAddThuoc.add(new JLabel("Thuốc:"));
        pnlAddThuoc.add(cboThuoc);
        pnlAddThuoc.add(new JLabel("Số lượng:"));
        pnlAddThuoc.add(spnSoLuong);
        pnlAddThuoc.add(btnAddThuoc);

        pnlChiTiet.add(pnlAddThuoc, BorderLayout.NORTH);

        // Panel tổng tiền và nút lưu
        JPanel pnlBottom = new JPanel(new BorderLayout());

        JPanel pnlTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        pnlTongTien.add(lblTongTien);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLuu = new JButton("Lưu hóa đơn");
        JButton btnHuy = new JButton("Hủy");
        pnlButtons.add(btnLuu);
        pnlButtons.add(btnHuy);

        pnlBottom.add(pnlTongTien, BorderLayout.CENTER);
        pnlBottom.add(pnlButtons, BorderLayout.SOUTH);

        // Thêm các panel vào panel chính
        mainPanel.add(pnlInfo, BorderLayout.NORTH);
        mainPanel.add(pnlChiTiet, BorderLayout.CENTER);
        mainPanel.add(pnlBottom, BorderLayout.SOUTH);

        dialog.add(mainPanel);

        // Xử lý sự kiện

        // Thêm thuốc vào chi tiết
        btnAddThuoc.addActionListener(e -> {
            if (cboThuoc.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn thuốc", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String thuocItem = (String) cboThuoc.getSelectedItem();
            String[] parts = thuocItem.split(" - ");
            String idThuoc = parts[0];
            String tenThuoc = parts[1];
            int soLuong = (int) spnSoLuong.getValue();

            // Lấy đơn giá thuốc từ server
            double donGia = getThuocPrice(idThuoc);
            double thanhTien = donGia * soLuong;

            // Kiểm tra xem thuốc đã có trong bảng chưa
            boolean found = false;
            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                if (modelChiTiet.getValueAt(i, 0).equals(idThuoc)) {
                    // Cập nhật số lượng và thành tiền
                    int oldSoLuong = Integer.parseInt(modelChiTiet.getValueAt(i, 3).toString());
                    int newSoLuong = oldSoLuong + soLuong;
                    double newThanhTien = donGia * newSoLuong;

                    modelChiTiet.setValueAt(newSoLuong, i, 3);
                    modelChiTiet.setValueAt(decimalFormat.format(newThanhTien), i, 4);

                    found = true;
                    break;
                }
            }

            if (!found) {
                // Thêm dòng mới
                modelChiTiet.addRow(new Object[]{
                        idThuoc,
                        tenThuoc,
                        decimalFormat.format(donGia),
                        soLuong,
                        decimalFormat.format(thanhTien)
                });
            }

            // Cập nhật tổng tiền
            updateTongTien(modelChiTiet, lblTongTien);
        });

        // Cập nhật tổng tiền khi thay đổi số lượng
        tblChiTiet.getModel().addTableModelListener(e -> {
            updateTongTien(modelChiTiet, lblTongTien);
        });

        // Lưu hóa đơn
        btnLuu.addActionListener(e -> {
            try {
                // Kiểm tra dữ liệu
                if (cboKhachHang.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cboNhanVien.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (modelChiTiet.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng thêm ít nhất một thuốc vào hóa đơn", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Lấy ID khách hàng và nhân viên
                String khachHangItem = (String) cboKhachHang.getSelectedItem();
                String idKH = khachHangItem.split(" - ")[0];

                String nhanVienItem = (String) cboNhanVien.getSelectedItem();
                String idNV = nhanVienItem.split(" - ")[0];

                // Tính tổng tiền
                double tongTien = 0;
                for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                    String thanhTienStr = modelChiTiet.getValueAt(i, 4).toString().replace(",", "");
                    tongTien += Double.parseDouble(thanhTienStr);
                }

                // Tạo dữ liệu hóa đơn
                Map<String, Object> hoaDonData = new HashMap<>();
                hoaDonData.put("idHD", txtIdHD.getText());
                hoaDonData.put("idKH", idKH);
                hoaDonData.put("idNV", idNV);
                hoaDonData.put("ngayLap", LocalDateTime.now().toString());
                hoaDonData.put("tongTien", tongTien);

                // Gửi yêu cầu lưu hóa đơn
                RequestDTO requestHD = new RequestDTO("SAVE_HOA_DON", new HashMap<>());
                requestHD.addData("hoaDon", hoaDonData);
                ResponseDTO responseHD = clientService.sendRequest(requestHD);

                if (responseHD != null && responseHD.isSuccess()) {
                    // Lưu chi tiết hóa đơn
                    for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                        String idThuoc = (String) modelChiTiet.getValueAt(i, 0);
                        int soLuong = Integer.parseInt(modelChiTiet.getValueAt(i, 3).toString());
                        String donGiaStr = modelChiTiet.getValueAt(i, 2).toString().replace(",", "");
                        double donGia = Double.parseDouble(donGiaStr);

                        Map<String, Object> chiTietData = new HashMap<>();
                        chiTietData.put("idThuoc", idThuoc);
                        chiTietData.put("soLuong", soLuong);
                        chiTietData.put("donGia", donGia);

                        RequestDTO requestCT = new RequestDTO("ADD_CHI_TIET_HOA_DON", new HashMap<>());
                        requestCT.addData("idHD", txtIdHD.getText());
                        requestCT.addData("chiTiet", chiTietData);
                        clientService.sendRequest(requestCT);
                    }

                    JOptionPane.showMessageDialog(dialog, "Lưu hóa đơn thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadHoaDonData(); // Tải lại danh sách hóa đơn
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lưu hóa đơn thất bại: " + (responseHD != null ? responseHD.getMessage() : "Không có phản hồi từ server"), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Hủy
        btnHuy.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void loadKhachHangComboBox(JComboBox<String> comboBox) {
        try {
            RequestDTO request = new RequestDTO("GET_ALL_KHACH_HANG", new HashMap<>());
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> khachHangList = (List<Map<String, Object>>) response.getData().get("khachHangList");

                if (khachHangList != null && !khachHangList.isEmpty()) {
                    for (Map<String, Object> khachHang : khachHangList) {
                        String idKH = (String) khachHang.get("idKH");
                        String hoTen = (String) khachHang.get("hoTen");
                        comboBox.addItem(idKH + " - " + hoTen);
                    }
                } else {
                    // Thêm dữ liệu mẫu
                    comboBox.addItem("KH001 - Nguyễn Văn A");
                    comboBox.addItem("KH002 - Trần Thị B");
                }
            } else {
                // Thêm dữ liệu mẫu
                comboBox.addItem("KH001 - Nguyễn Văn A");
                comboBox.addItem("KH002 - Trần Thị B");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Thêm dữ liệu mẫu
            comboBox.addItem("KH001 - Nguyễn Văn A");
            comboBox.addItem("KH002 - Trần Thị B");
        }
    }

    private void loadNhanVienComboBox(JComboBox<String> comboBox) {
        try {
            RequestDTO request = new RequestDTO("GET_ALL_NHAN_VIEN", new HashMap<>());
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> nhanVienList = (List<Map<String, Object>>) response.getData().get("nhanVienList");

                if (nhanVienList != null && !nhanVienList.isEmpty()) {
                    for (Map<String, Object> nhanVien : nhanVienList) {
                        String idNV = (String) nhanVien.get("idNV");
                        String hoTen = (String) nhanVien.get("hoTen");
                        comboBox.addItem(idNV + " - " + hoTen);
                    }
                } else {
                    // Thêm dữ liệu mẫu
                    comboBox.addItem("NV001 - Nguyễn Văn Admin");
                    comboBox.addItem("NV002 - Trần Thị Nhân Viên");
                }
            } else {
                // Thêm dữ liệu mẫu
                comboBox.addItem("NV001 - Nguyễn Văn Admin");
                comboBox.addItem("NV002 - Trần Thị Nhân Viên");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Thêm dữ liệu mẫu
            comboBox.addItem("NV001 - Nguyễn Văn Admin");
            comboBox.addItem("NV002 - Trần Thị Nhân Viên");
        }
    }

    private void loadThuocComboBox(JComboBox<String> comboBox) {
        try {
            RequestDTO request = new RequestDTO("GET_ALL_THUOC", new HashMap<>());
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = response.getData();

                if (data != null && data.containsKey("thuocs")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> thuocList = (Map<String, Object>) data.get("thuocs");

                    if (thuocList != null && !thuocList.isEmpty()) {
                        for (Map.Entry<String, Object> entry : thuocList.entrySet()) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> thuoc = (Map<String, Object>) entry.getValue();
                            String idThuoc = (String) thuoc.get("idThuoc");
                            String tenThuoc = (String) thuoc.get("tenThuoc");
                            comboBox.addItem(idThuoc + " - " + tenThuoc);
                        }
                    } else {
                        // Thêm dữ liệu mẫu
                        comboBox.addItem("T001 - Paracetamol");
                        comboBox.addItem("T002 - Amoxicillin");
                        comboBox.addItem("T003 - Vitamin C");
                    }
                } else {
                    // Thêm dữ liệu mẫu
                    comboBox.addItem("T001 - Paracetamol");
                    comboBox.addItem("T002 - Amoxicillin");
                    comboBox.addItem("T003 - Vitamin C");
                }
            } else {
                // Thêm dữ liệu mẫu
                comboBox.addItem("T001 - Paracetamol");
                comboBox.addItem("T002 - Amoxicillin");
                comboBox.addItem("T003 - Vitamin C");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Thêm dữ liệu mẫu
            comboBox.addItem("T001 - Paracetamol");
            comboBox.addItem("T002 - Amoxicillin");
            comboBox.addItem("T003 - Vitamin C");
        }
    }

    private double getThuocPrice(String idThuoc) {
        try {
            RequestDTO request = new RequestDTO("GET_THUOC_BY_ID", new HashMap<>());
            request.addData("idThuoc", idThuoc);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> thuoc = (Map<String, Object>) response.getData().get("thuoc");
                if (thuoc != null && thuoc.containsKey("donGia")) {
                    return Double.parseDouble(thuoc.get("donGia").toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10000; // Giá mặc định nếu không lấy được
    }

    private void updateTongTien(DefaultTableModel model, JLabel lblTongTien) {
        double tongTien = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String thanhTienStr = model.getValueAt(i, 4).toString().replace(",", "");
            try {
                tongTien += Double.parseDouble(thanhTienStr);
            } catch (NumberFormatException e) {
                // Bỏ qua nếu không phải số
            }
        }
        lblTongTien.setText("Tổng tiền: " + decimalFormat.format(tongTien) + " VNĐ");
    }

    private void showChiTietHoaDon() {
        int selectedRow = tblHoaDon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idHD = (String) tblHoaDon.getValueAt(selectedRow, 0);

        try {
            // Lấy thông tin chi tiết hóa đơn
            RequestDTO request = new RequestDTO("GET_CHI_TIET_HOA_DON", new HashMap<>());
            request.addData("idHD", idHD);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                // Hiển thị dialog chi tiết hóa đơn
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn " + idHD, true);
                dialog.setSize(800, 500);
                dialog.setLocationRelativeTo(this);

                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Thông tin hóa đơn
                JPanel pnlInfo = new JPanel(new GridLayout(4, 2, 10, 5));
                pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

                pnlInfo.add(new JLabel("Mã hóa đơn:"));
                pnlInfo.add(new JLabel(idHD));

                pnlInfo.add(new JLabel("Ngày lập:"));
                pnlInfo.add(new JLabel((String) tblHoaDon.getValueAt(selectedRow, 1)));

                pnlInfo.add(new JLabel("Khách hàng:"));
                pnlInfo.add(new JLabel((String) tblHoaDon.getValueAt(selectedRow, 2)));

                pnlInfo.add(new JLabel("Nhân viên:"));
                pnlInfo.add(new JLabel((String) tblHoaDon.getValueAt(selectedRow, 3)));

                // Bảng chi tiết
                JPanel pnlTable = new JPanel(new BorderLayout());
                pnlTable.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

                String[] columnNames = {"Mã thuốc", "Tên thuốc", "Đơn giá", "Số lượng", "Thành tiền"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                JTable tblChiTiet = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(tblChiTiet);
                pnlTable.add(scrollPane, BorderLayout.CENTER);

                // Thêm dữ liệu vào bảng
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> chiTietList = (List<Map<String, Object>>) response.getData().get("chiTietList");
                double tongTien = 0;

                if (chiTietList != null && !chiTietList.isEmpty()) {
                    for (Map<String, Object> chiTiet : chiTietList) {
                        String idThuoc = (String) chiTiet.get("idThuoc");
                        int soLuong = Integer.parseInt(chiTiet.get("soLuong").toString());
                        double donGia = Double.parseDouble(chiTiet.get("donGia").toString());
                        double thanhTien = soLuong * donGia;
                        tongTien += thanhTien;

                        // Lấy tên thuốc
                        Map<String, Object> thuoc = (Map<String, Object>) chiTiet.get("thuoc");
                        String tenThuoc = thuoc != null ? (String) thuoc.get("tenThuoc") : "N/A";

                        model.addRow(new Object[]{
                                idThuoc,
                                tenThuoc,
                                decimalFormat.format(donGia),
                                soLuong,
                                decimalFormat.format(thanhTien)
                        });
                    }
                }

                // Panel tổng tiền
                JPanel pnlBottom = new JPanel(new BorderLayout());

                JPanel pnlTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JLabel lblTongTien = new JLabel("Tổng tiền: " + decimalFormat.format(tongTien) + " VNĐ");
                lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
                pnlTongTien.add(lblTongTien);

                JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnDong = new JButton("Đóng");
                btnDong.addActionListener(e -> dialog.dispose());
                pnlButtons.add(btnDong);

                pnlBottom.add(pnlTongTien, BorderLayout.CENTER);
                pnlBottom.add(pnlButtons, BorderLayout.SOUTH);

                // Thêm các panel vào panel chính
                mainPanel.add(pnlInfo, BorderLayout.NORTH);
                mainPanel.add(pnlTable, BorderLayout.CENTER);
                mainPanel.add(pnlBottom, BorderLayout.SOUTH);

                dialog.add(mainPanel);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết hóa đơn: " + (response != null ? response.getMessage() : "Không có phản hồi từ server"), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xem chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getThuocName(String idThuoc) {
        try {
            RequestDTO request = new RequestDTO("GET_THUOC_BY_ID", new HashMap<>());
            request.addData("idThuoc", idThuoc);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> thuoc = (Map<String, Object>) response.getData().get("thuoc");
                if (thuoc != null && thuoc.containsKey("tenThuoc")) {
                    return (String) thuoc.get("tenThuoc");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Thuốc " + idThuoc;
    }

    private void searchHoaDon() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadHoaDonData();
            return;
        }

        // Tìm kiếm trong bảng hiện tại
        for (int i = 0; i < tblHoaDon.getRowCount(); i++) {
            boolean found = false;
            for (int j = 0; j < tblHoaDon.getColumnCount(); j++) {
                String cellValue = tblHoaDon.getValueAt(i, j).toString().toLowerCase();
                if (cellValue.contains(keyword.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                tblHoaDon.setRowSelectionInterval(i, i);
                tblHoaDon.scrollRectToVisible(tblHoaDon.getCellRect(i, 0, true));
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn phù hợp với từ khóa: " + keyword, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}