package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhachHangPanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JTable khachHangTable;
    private DefaultTableModel tableModel;

    private JTextField idKHField;
    private JTextField hoTenField;
    private JTextField sdtField;
    private JTextField emailField;
    private JComboBox<String> gioiTinhComboBox;
    private JDateChooser ngayThamGiaChooser;
    private JTextField hangMucField;
    private JTextField tongChiTieuField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton otpButton;

    public KhachHangPanel() {
        initComponents();
        setupLayout();
        setupListeners();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        searchField = new JTextField(20);
        searchButton = new JButton("Tìm Kiếm");
        refreshButton = new JButton("Làm Mới");

        // Tạo model cho bảng
        String[] columnNames = {"ID", "Họ Tên", "SĐT", "Email", "Giới Tính", "Ngày Tham Gia", "Hạng Mục", "Tổng Chi Tiêu"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        khachHangTable = new JTable(tableModel);

        idKHField = new JTextField(20);
        idKHField.setEditable(false);
        hoTenField = new JTextField(20);
        sdtField = new JTextField(20);
        emailField = new JTextField(20);

        gioiTinhComboBox = new JComboBox<>(new String[]{"Nam", "Nữ"});

        ngayThamGiaChooser = new JDateChooser();
        hangMucField = new JTextField(20);
        hangMucField.setEditable(false);
        tongChiTieuField = new JTextField(20);
        tongChiTieuField.setEditable(false);

        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập Nhật");
        deleteButton = new JButton("Xóa");
        clearButton = new JButton("Xóa Form");
        otpButton = new JButton("Gửi OTP");
    }

    private void setupLayout() {
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Panel bảng
        JScrollPane tableScrollPane = new JScrollPane(khachHangTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));

        // Panel form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Khách Hàng"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Cột 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Họ Tên:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("SĐT:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);

        // Cột 2
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(idKHField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(hoTenField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(sdtField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        // Cột 3
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Giới Tính:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Ngày Tham Gia:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Hạng Mục:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Tổng Chi Tiêu:"), gbc);

        // Cột 4
        gbc.gridx = 3;
        gbc.gridy = 0;
        formPanel.add(gioiTinhComboBox, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(ngayThamGiaChooser, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        formPanel.add(hangMucField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 3;
        formPanel.add(tongChiTieuField, gbc);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(otpButton);

        // Panel chính
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        searchButton.addActionListener(e -> searchKhachHang());
        refreshButton.addActionListener(e -> loadData());

        khachHangTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && khachHangTable.getSelectedRow() != -1) {
                displaySelectedKhachHang();
            }
        });

        addButton.addActionListener(e -> addKhachHang());
        updateButton.addActionListener(e -> updateKhachHang());
        deleteButton.addActionListener(e -> deleteKhachHang());
        clearButton.addActionListener(e -> clearForm());
        otpButton.addActionListener(e -> sendOTP());
    }

    private void loadData() {
        try {
            ResponseDTO response = ClientService.getInstance().getAllKhachHang();

            if (response.isSuccess()) {
                // Xóa dữ liệu cũ
                tableModel.setRowCount(0);

                // Lấy danh sách khách hàng từ response
                List<Map<String, Object>> khachHangList = (List<Map<String, Object>>) response.getData().get("khachHangList");

                // Thêm dữ liệu vào bảng
                for (Map<String, Object> khachHang : khachHangList) {
                    Object[] rowData = {
                            khachHang.get("idKH"),
                            khachHang.get("hoTen"),
                            khachHang.get("sdt"),
                            khachHang.get("email"),
                            khachHang.get("gioiTinh"),
                            khachHang.get("ngayThamGia"),
                            khachHang.get("hangMuc"),
                            khachHang.get("tongChiTieu")
                    };
                    tableModel.addRow(rowData);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchKhachHang() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            ResponseDTO response = ClientService.getInstance().searchKhachHang(keyword);

            if (response.isSuccess()) {
                // Xóa dữ liệu cũ
                tableModel.setRowCount(0);

                // Lấy danh sách khách hàng từ response
                List<Map<String, Object>> khachHangList = (List<Map<String, Object>>) response.getData().get("khachHangList");

                // Thêm dữ liệu vào bảng
                for (Map<String, Object> khachHang : khachHangList) {
                    Object[] rowData = {
                            khachHang.get("idKH"),
                            khachHang.get("hoTen"),
                            khachHang.get("sdt"),
                            khachHang.get("email"),
                            khachHang.get("gioiTinh"),
                            khachHang.get("ngayThamGia"),
                            khachHang.get("hangMuc"),
                            khachHang.get("tongChiTieu")
                    };
                    tableModel.addRow(rowData);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySelectedKhachHang() {
        int selectedRow = khachHangTable.getSelectedRow();

        if (selectedRow != -1) {
            String idKH = (String) tableModel.getValueAt(selectedRow, 0);

            try {
                ResponseDTO response = ClientService.getInstance().getKhachHangById(idKH);

                if (response.isSuccess()) {
                    Map<String, Object> khachHang = (Map<String, Object>) response.getData().get("khachHang");

                    // Hiển thị thông tin khách hàng lên form
                    idKHField.setText((String) khachHang.get("idKH"));
                    hoTenField.setText((String) khachHang.get("hoTen"));
                    sdtField.setText((String) khachHang.get("sdt"));
                    emailField.setText((String) khachHang.get("email"));

                    // Chọn giới tính
                    gioiTinhComboBox.setSelectedItem(khachHang.get("gioiTinh"));

                    // Hiển thị ngày tham gia
                    try {
                        java.util.Date ngayThamGia = new java.text.SimpleDateFormat("yyyy-MM-dd").parse((String) khachHang.get("ngayThamGia"));
                        ngayThamGiaChooser.setDate(ngayThamGia);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    hangMucField.setText((String) khachHang.get("hangMuc"));
                    tongChiTieuField.setText(String.valueOf(khachHang.get("tongChiTieu")));
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin khách hàng: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addKhachHang() {
        // Kiểm tra dữ liệu nhập vào
        if (!validateInput()) {
            return;
        }

        try {
            // Tạo đối tượng khách hàng từ form
            Map<String, Object> khachHang = createKhachHangFromForm();

            // Gửi yêu cầu thêm khách hàng
            ResponseDTO response = ClientService.getInstance().saveKhachHang(khachHang);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateKhachHang() {
        // Kiểm tra xem đã chọn khách hàng chưa
        if (idKHField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra dữ liệu nhập vào
        if (!validateInput()) {
            return;
        }

        try {
            // Tạo đối tượng khách hàng từ form
            Map<String, Object> khachHang = createKhachHangFromForm();

            // Gửi yêu cầu cập nhật khách hàng
            ResponseDTO response = ClientService.getInstance().updateKhachHang(khachHang);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khách hàng: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKhachHang() {
        // Kiểm tra xem đã chọn khách hàng chưa
        if (idKHField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                // Gửi yêu cầu xóa khách hàng
                ResponseDTO response = ClientService.getInstance().deleteKhachHang(idKHField.getText());

                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        idKHField.setText("");
        hoTenField.setText("");
        sdtField.setText("");
        emailField.setText("");
        gioiTinhComboBox.setSelectedIndex(0);
        ngayThamGiaChooser.setDate(null);
        hangMucField.setText("");
        tongChiTieuField.setText("");

        khachHangTable.clearSelection();
    }

    private void sendOTP() {
        // Kiểm tra xem đã chọn khách hàng chưa
        if (idKHField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để gửi OTP!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Gửi yêu cầu tạo và gửi OTP
            ResponseDTO response = ClientService.getInstance().generateOTP(idKHField.getText());

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Đã gửi OTP thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Hiển thị dialog nhập OTP
                String otpInput = JOptionPane.showInputDialog(this, "Nhập mã OTP đã gửi:", "Xác thực OTP", JOptionPane.QUESTION_MESSAGE);

                if (otpInput != null && !otpInput.isEmpty()) {
                    // Gửi yêu cầu xác thực OTP
                    ResponseDTO verifyResponse = ClientService.getInstance().verifyOTP(idKHField.getText(), otpInput);

                    if (verifyResponse.isSuccess()) {
                        JOptionPane.showMessageDialog(this, "Xác thực OTP thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Xác thực OTP thất bại: " + verifyResponse.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi OTP: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        // Kiểm tra họ tên
        if (hoTenField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            hoTenField.requestFocus();
            return false;
        }

        // Kiểm tra số điện thoại
        if (sdtField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            sdtField.requestFocus();
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        if (!sdtField.getText().trim().matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải có 10-11 chữ số)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            sdtField.requestFocus();
            return false;
        }

        // Kiểm tra email (nếu có)
        if (!emailField.getText().trim().isEmpty() && !emailField.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        return true;
    }

    private Map<String, Object> createKhachHangFromForm() {
        Map<String, Object> khachHang = new HashMap<>();

        // Nếu là thêm mới, tạo ID mới
        if (idKHField.getText().isEmpty()) {
            try {
                ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("GENERATE_KHACH_HANG_ID", null));
                if (response.isSuccess()) {
                    khachHang.put("idKH", response.getData().get("idKH"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            khachHang.put("idKH", idKHField.getText());
        }

        khachHang.put("hoTen", hoTenField.getText());
        khachHang.put("sdt", sdtField.getText());
        khachHang.put("email", emailField.getText());
        khachHang.put("gioiTinh", gioiTinhComboBox.getSelectedItem());

        // Format ngày tham gia
        if (ngayThamGiaChooser.getDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            khachHang.put("ngayThamGia", sdf.format(ngayThamGiaChooser.getDate()));
        } else {
            // Nếu không chọn ngày, sử dụng ngày hiện tại
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            khachHang.put("ngayThamGia", sdf.format(new java.util.Date()));
        }

        // Nếu là thêm mới, đặt hạng mục là "Thường" và tổng chi tiêu là 0
        if (idKHField.getText().isEmpty()) {
            khachHang.put("hangMuc", "Thường");
            khachHang.put("tongChiTieu", 0.0);
        } else {
            // Nếu là cập nhật, giữ nguyên giá trị hiện tại
            khachHang.put("hangMuc", hangMucField.getText());
            khachHang.put("tongChiTieu", Double.parseDouble(tongChiTieuField.getText()));
        }

        return khachHang;
    }
}