package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhanVienPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable tblNhanVien;
    private JTextField txtIdNV, txtHoTen, txtSDT, txtNamSinh, txtEmail;
    private JComboBox<String> cboGioiTinh;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTaoTaiKhoan, btnQuenMatKhau;
    private ClientService clientService;

    public NhanVienPanel(ClientService clientService) {
        this.clientService = clientService;
        setLayout(new BorderLayout());

        // Panel chứa form nhập liệu
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Thêm các thành phần vào form
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã nhân viên:"), gbc);

        gbc.gridx = 1;
        txtIdNV = new JTextField(20);
        txtIdNV.setEditable(false);
        pnlForm.add(txtIdNV, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlForm.add(new JLabel("Họ tên:"), gbc);

        gbc.gridx = 1;
        txtHoTen = new JTextField(20);
        pnlForm.add(txtHoTen, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Số điện thoại:"), gbc);

        gbc.gridx = 1;
        txtSDT = new JTextField(20);
        pnlForm.add(txtSDT, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        pnlForm.add(new JLabel("Email:"), gbc);

        gbc.gridx = 3;
        txtEmail = new JTextField(20);
        pnlForm.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        pnlForm.add(new JLabel("Giới tính:"), gbc);

        gbc.gridx = 1;
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        pnlForm.add(cboGioiTinh, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        pnlForm.add(new JLabel("Năm sinh:"), gbc);

        gbc.gridx = 1;
        txtNamSinh = new JTextField(20);
        pnlForm.add(txtNamSinh, gbc);

        // Panel chứa các nút chức năng
        JPanel pnlButtons = new JPanel(new FlowLayout());

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");
        btnTaoTaiKhoan = new JButton("Tạo tài khoản");
        btnQuenMatKhau = new JButton("Quên mật khẩu");

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnTaoTaiKhoan);
        pnlButtons.add(btnQuenMatKhau);

        // Panel chứa bảng dữ liệu
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));

        String[] columnNames = {"Mã NV", "Họ tên", "SĐT", "Email", "Giới tính", "Năm sinh", "Ngày vào làm", "Trạng thái TK"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNhanVien = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblNhanVien);
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        // Thêm các panel vào panel chính
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlTable, BorderLayout.CENTER);

        // Đăng ký sự kiện
        registerEvents();

        // Load dữ liệu ban đầu
        loadNhanVienData();
    }

    private void registerEvents() {
        // Sự kiện khi click vào một dòng trong bảng
        tblNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblNhanVien.getSelectedRow();
                if (selectedRow >= 0) {
                    String idNV = tblNhanVien.getValueAt(selectedRow, 0).toString();
                    String hoTen = tblNhanVien.getValueAt(selectedRow, 1).toString();
                    String sdt = tblNhanVien.getValueAt(selectedRow, 2).toString();
                    String email = tblNhanVien.getValueAt(selectedRow, 3).toString();
                    String gioiTinh = tblNhanVien.getValueAt(selectedRow, 4).toString();
                    String namSinh = tblNhanVien.getValueAt(selectedRow, 5).toString();

                    txtIdNV.setText(idNV);
                    txtHoTen.setText(hoTen);
                    txtSDT.setText(sdt);
                    txtEmail.setText(email);
                    cboGioiTinh.setSelectedItem(gioiTinh);
                    txtNamSinh.setText(namSinh);
                }
            }
        });

        // Sự kiện khi nhấn nút Thêm
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themNhanVien();
            }
        });

        // Sự kiện khi nhấn nút Sửa
        btnSua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaNhanVien();
            }
        });

        // Sự kiện khi nhấn nút Xóa
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaNhanVien();
            }
        });

        // Sự kiện khi nhấn nút Làm mới
        btnLamMoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoi();
            }
        });

        // Sự kiện khi nhấn nút Tạo tài khoản
        btnTaoTaiKhoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taoTaiKhoan();
            }
        });

        // Sự kiện khi nhấn nút Quên mật khẩu
        btnQuenMatKhau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quenMatKhau();
            }
        });
    }

//    private void loadNhanVienData() {
//        try {
//            // Gửi yêu cầu lấy danh sách nhân viên
//            ResponseDTO response = ClientService.getInstance().getAllNhanVien();
//
//            if (response != null && response.isSuccess()) {
//                // Kiểm tra và xử lý danh sách nhân viên
//                @SuppressWarnings("unchecked")
//                List<Map<String, Object>> danhSachNV = (List<Map<String, Object>>) response.getData().get("nhanVienList");
//
//                // Kiểm tra null trước khi sử dụng
//                if (danhSachNV == null) {
//                    danhSachNV = new ArrayList<>();
//                }
//
//                // Xóa dữ liệu cũ trong bảng
//                DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
//                model.setRowCount(0);
//
//                // Thêm dữ liệu mới vào bảng
//                for (Map<String, Object> nv : danhSachNV) {
//                    // Lấy trạng thái tài khoản từ dữ liệu trả về từ server
//                    String trangThaiTK = (String) nv.get("trangThaiTK");
//                    if (trangThaiTK == null) {
//                        trangThaiTK = "Chưa tạo"; // Giá trị mặc định nếu không có
//                    }
//
//                    model.addRow(new Object[]{
//                            nv.get("idNV"),
//                            nv.get("hoTen"),
//                            nv.get("sdt"),
//                            nv.get("gioiTinh"),
//                            nv.get("namSinh"),
//                            nv.get("ngayVaoLam"),
//                            trangThaiTK
//                    });
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Không thể lấy danh sách nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }

    private void loadNhanVienData() {
        try {
            // Gửi yêu cầu lấy danh sách nhân viên
            ResponseDTO response = ClientService.getInstance().getAllNhanVien();

            if (response != null && response.isSuccess()) {
                // Kiểm tra và xử lý danh sách nhân viên
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> danhSachNV = (List<Map<String, Object>>) response.getData().get("nhanVienList");

                // Kiểm tra null trước khi sử dụng
                if (danhSachNV == null) {
                    danhSachNV = new ArrayList<>();
                }

                // Xóa dữ liệu cũ trong bảng
                DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
                model.setRowCount(0);

                // Thêm dữ liệu mới vào bảng
                for (Map<String, Object> nv : danhSachNV) {
                    // Lấy trạng thái tài khoản từ dữ liệu trả về từ server
                    String trangThaiTK = (String) nv.get("trangThaiTK");
                    if (trangThaiTK == null) {
                        trangThaiTK = "Chưa tạo"; // Giá trị mặc định nếu không có
                    }

                    // Lấy email từ dữ liệu trả về
                    String email = (String) nv.get("email");
                    if (email == null) {
                        email = ""; // Giá trị mặc định nếu không có
                    }

                    model.addRow(new Object[]{
                            nv.get("idNV"),
                            nv.get("hoTen"),
                            nv.get("sdt"),
                            email,
                            nv.get("gioiTinh"),
                            nv.get("namSinh"),
                            nv.get("ngayVaoLam"),
                            trangThaiTK
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lấy danh sách nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//    private void themNhanVien() {
//        try {
//            // Kiểm tra dữ liệu nhập
//            if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtNamSinh.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Tạo dữ liệu nhân viên
//            Map<String, Object> nhanVienData = new HashMap<>();
//            nhanVienData.put("hoTen", txtHoTen.getText().trim());
//            nhanVienData.put("sdt", txtSDT.getText().trim());
//            nhanVienData.put("gioiTinh", cboGioiTinh.getSelectedItem().toString());
//            nhanVienData.put("namSinh", Integer.parseInt(txtNamSinh.getText().trim()));
//
//            // Gửi yêu cầu thêm nhân viên
//            Map<String, Object> data = new HashMap<>();
//            data.put("nhanVien", nhanVienData);
//            RequestDTO request = new RequestDTO("SAVE_NHAN_VIEN", data);
//            ResponseDTO response = clientService.sendRequest(request);
//
//            if (response != null && response.isSuccess()) {
//                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                lamMoi();
//                loadNhanVienData();
//            } else {
//                // Mô phỏng thêm thành công
//                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//                // Tạo ID mới
//                String idNV = "NV" + String.format("%03d", tblNhanVien.getRowCount() + 1);
//
//                // Thêm vào bảng
//                DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
//                model.addRow(new Object[]{
//                        idNV,
//                        txtHoTen.getText().trim(),
//                        txtSDT.getText().trim(),
//                        cboGioiTinh.getSelectedItem().toString(),
//                        txtNamSinh.getText().trim(),
//                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()),
//                        "Chưa tạo"
//                });
//
//                lamMoi();
//            }
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Năm sinh phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }

    private void themNhanVien() {
        try {
            // Kiểm tra dữ liệu nhập
            if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtNamSinh.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra định dạng email
            String email = txtEmail.getText().trim();
            if (!email.isEmpty() && !isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Email không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo dữ liệu nhân viên
            Map<String, Object> nhanVienData = new HashMap<>();
            nhanVienData.put("hoTen", txtHoTen.getText().trim());
            nhanVienData.put("sdt", txtSDT.getText().trim());
            nhanVienData.put("email", email);
            nhanVienData.put("gioiTinh", cboGioiTinh.getSelectedItem().toString());
            nhanVienData.put("namSinh", Integer.parseInt(txtNamSinh.getText().trim()));

            // Gửi yêu cầu thêm nhân viên
            Map<String, Object> data = new HashMap<>();
            data.put("nhanVien", nhanVienData);
            RequestDTO request = new RequestDTO("SAVE_NHAN_VIEN", data);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoi();
                loadNhanVienData();
            } else {
                // Mô phỏng thêm thành công
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Tạo ID mới
                String idNV = "NV" + String.format("%03d", tblNhanVien.getRowCount() + 1);

                // Thêm vào bảng
                DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
                model.addRow(new Object[]{
                        idNV,
                        txtHoTen.getText().trim(),
                        txtSDT.getText().trim(),
                        email,
                        cboGioiTinh.getSelectedItem().toString(),
                        txtNamSinh.getText().trim(),
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()),
                        "Chưa tạo"
                });

                lamMoi();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm sinh phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//    private void suaNhanVien() {
//        try {
//            // Kiểm tra đã chọn nhân viên chưa
//            if (txtIdNV.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Kiểm tra dữ liệu nhập
//            if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtNamSinh.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Tạo dữ liệu nhân viên
//            Map<String, Object> nhanVienData = new HashMap<>();
//            nhanVienData.put("idNV", txtIdNV.getText().trim());
//            nhanVienData.put("hoTen", txtHoTen.getText().trim());
//            nhanVienData.put("sdt", txtSDT.getText().trim());
//            nhanVienData.put("gioiTinh", cboGioiTinh.getSelectedItem().toString());
//            nhanVienData.put("namSinh", Integer.parseInt(txtNamSinh.getText().trim()));
//
//            // Lấy ngày vào làm từ bảng
//            int selectedRow = tblNhanVien.getSelectedRow();
//            if (selectedRow >= 0) {
//                Object ngayVaoLam = tblNhanVien.getValueAt(selectedRow, 5);
//                if (ngayVaoLam != null) {
//                    nhanVienData.put("ngayVaoLam", ngayVaoLam.toString());
//                }
//            }
//
//            // Gửi yêu cầu cập nhật nhân viên
//            Map<String, Object> data = new HashMap<>();
//            data.put("nhanVien", nhanVienData);
//            RequestDTO request = new RequestDTO("UPDATE_NHAN_VIEN", data);
//            ResponseDTO response = clientService.sendRequest(request);
//
//            if (response != null && response.isSuccess()) {
//                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                lamMoi();
//                loadNhanVienData();
//            } else {
//                // Mô phỏng cập nhật thành công
//                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//                // Cập nhật dữ liệu trong bảng
//                if (selectedRow >= 0) {
//                    DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
//                    model.setValueAt(txtHoTen.getText().trim(), selectedRow, 1);
//                    model.setValueAt(txtSDT.getText().trim(), selectedRow, 2);
//                    model.setValueAt(cboGioiTinh.getSelectedItem().toString(), selectedRow, 3);
//                    model.setValueAt(txtNamSinh.getText().trim(), selectedRow, 4);
//                }
//
//                lamMoi();
//            }
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Năm sinh phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }

    private void suaNhanVien() {
        try {
            // Kiểm tra đã chọn nhân viên chưa
            if (txtIdNV.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra dữ liệu nhập
            if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtNamSinh.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra định dạng email
            String email = txtEmail.getText().trim();
            if (!email.isEmpty() && !isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Email không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo dữ liệu nhân viên
            Map<String, Object> nhanVienData = new HashMap<>();
            nhanVienData.put("idNV", txtIdNV.getText().trim());
            nhanVienData.put("hoTen", txtHoTen.getText().trim());
            nhanVienData.put("sdt", txtSDT.getText().trim());
            nhanVienData.put("email", email);
            nhanVienData.put("gioiTinh", cboGioiTinh.getSelectedItem().toString());
            nhanVienData.put("namSinh", Integer.parseInt(txtNamSinh.getText().trim()));

            // Lấy ngày vào làm từ bảng
            int selectedRow = tblNhanVien.getSelectedRow();
            if (selectedRow >= 0) {
                Object ngayVaoLam = tblNhanVien.getValueAt(selectedRow, 6);
                if (ngayVaoLam != null) {
                    nhanVienData.put("ngayVaoLam", ngayVaoLam.toString());
                }
            }

            // Gửi yêu cầu cập nhật nhân viên
            Map<String, Object> data = new HashMap<>();
            data.put("nhanVien", nhanVienData);
            RequestDTO request = new RequestDTO("UPDATE_NHAN_VIEN", data);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoi();
                loadNhanVienData();
            } else {
                // Mô phỏng cập nhật thành công
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Cập nhật dữ liệu trong bảng
                if (selectedRow >= 0) {
                    DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
                    model.setValueAt(txtHoTen.getText().trim(), selectedRow, 1);
                    model.setValueAt(txtSDT.getText().trim(), selectedRow, 2);
                    model.setValueAt(email, selectedRow, 3);
                    model.setValueAt(cboGioiTinh.getSelectedItem().toString(), selectedRow, 4);
                    model.setValueAt(txtNamSinh.getText().trim(), selectedRow, 5);
                }

                lamMoi();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm sinh phải là số", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//    private void xoaNhanVien() {
//        try {
//            // Kiểm tra đã chọn nhân viên chưa
//            if (txtIdNV.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Xác nhận xóa
//            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//            if (confirm != JOptionPane.YES_OPTION) {
//                return;
//            }
//
//            // Gửi yêu cầu xóa nhân viên
//            Map<String, Object> data = new HashMap<>();
//            data.put("idNV", txtIdNV.getText().trim());
//            RequestDTO request = new RequestDTO("DELETE_NHAN_VIEN", data);
//            ResponseDTO response = clientService.sendRequest(request);
//
//            if (response != null && response.isSuccess()) {
//                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                lamMoi();
//                loadNhanVienData();
//            } else {
//                // Mô phỏng xóa thành công
//                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//                // Xóa dữ liệu khỏi bảng
//                int selectedRow = tblNhanVien.getSelectedRow();
//                if (selectedRow >= 0) {
//                    DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
//                    model.removeRow(selectedRow);
//                }
//
//                lamMoi();
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }

    private void xoaNhanVien() {
        try {
            // Kiểm tra đã chọn nhân viên chưa
            if (txtIdNV.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Gửi yêu cầu xóa nhân viên
            Map<String, Object> data = new HashMap<>();
            data.put("idNV", txtIdNV.getText().trim());
            RequestDTO request = new RequestDTO("DELETE_NHAN_VIEN", data);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                lamMoi();
                loadNhanVienData();
            } else {
                // Mô phỏng xóa thành công
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Xóa dữ liệu khỏi bảng
                int selectedRow = tblNhanVien.getSelectedRow();
                if (selectedRow >= 0) {
                    DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
                    model.removeRow(selectedRow);
                }

                lamMoi();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//    private void lamMoi() {
//        txtIdNV.setText("");
//        txtHoTen.setText("");
//        txtSDT.setText("");
//        cboGioiTinh.setSelectedIndex(0);
//        txtNamSinh.setText("");
//        tblNhanVien.clearSelection();
//    }

    private void lamMoi() {
        txtIdNV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtNamSinh.setText("");
        tblNhanVien.clearSelection();
    }

//    private void taoTaiKhoan() {
//        try {
//            // Kiểm tra đã chọn nhân viên chưa
//            if (txtIdNV.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tạo tài khoản", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Kiểm tra trạng thái tài khoản
//            int selectedRow = tblNhanVien.getSelectedRow();
//            if (selectedRow >= 0) {
//                String trangThaiTK = tblNhanVien.getValueAt(selectedRow, 6).toString();
//                if ("Đã tạo".equals(trangThaiTK)) {
//                    JOptionPane.showMessageDialog(this, "Nhân viên này đã có tài khoản", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                    return;
//                }
//            }
//
//            // Hiển thị dialog tạo tài khoản
//            showTaoTaiKhoanDialog(txtIdNV.getText().trim(), txtHoTen.getText().trim());
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }

    private void taoTaiKhoan() {
        try {
            // Kiểm tra đã chọn nhân viên chưa
            if (txtIdNV.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tạo tài khoản", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trạng thái tài khoản
            int selectedRow = tblNhanVien.getSelectedRow();
            if (selectedRow >= 0) {
                String trangThaiTK = tblNhanVien.getValueAt(selectedRow, 7).toString();
                if ("Đã tạo".equals(trangThaiTK)) {
                    JOptionPane.showMessageDialog(this, "Nhân viên này đã có tài khoản", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            // Hiển thị dialog tạo tài khoản
            showTaoTaiKhoanDialog(txtIdNV.getText().trim(), txtHoTen.getText().trim());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void quenMatKhau() {
        try {
            // Kiểm tra đã chọn nhân viên chưa
            if (txtIdNV.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần đặt lại mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra email
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhân viên này chưa có email, không thể đặt lại mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Xác nhận đặt lại mật khẩu
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đặt lại mật khẩu cho nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Gửi yêu cầu đặt lại mật khẩu
            Map<String, Object> data = new HashMap<>();
            data.put("idNV", txtIdNV.getText().trim());
            RequestDTO request = new RequestDTO("RESET_PASSWORD", data);
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công. Mật khẩu mới đã được gửi đến email của nhân viên.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Mô phỏng đặt lại mật khẩu thành công
                JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công. Mật khẩu mới đã được gửi đến email của nhân viên. (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

//    private void showTaoTaiKhoanDialog(String idNV, String hoTen) {
//        // Tạo dialog
//        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo tài khoản", true);
//        dialog.setLayout(new BorderLayout());
//        dialog.setSize(400, 200);
//        dialog.setLocationRelativeTo(this);
//
//        // Panel chứa form
//        JPanel pnlForm = new JPanel(new GridBagLayout());
//        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Thêm các thành phần vào form
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        pnlForm.add(new JLabel("Nhân viên:"), gbc);
//
//        gbc.gridx = 1;
//        JTextField txtNhanVien = new JTextField(20);
//        txtNhanVien.setText(hoTen);
//        txtNhanVien.setEditable(false);
//        pnlForm.add(txtNhanVien, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        pnlForm.add(new JLabel("Tên đăng nhập:"), gbc);
//
//        gbc.gridx = 1;
//        JTextField txtUsername = new JTextField(20);
//        pnlForm.add(txtUsername, gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        pnlForm.add(new JLabel("Vai trò:"), gbc);
//
//        gbc.gridx = 1;
//        JComboBox<String> cboVaiTro = new JComboBox<>();
//
//        // Thêm dữ liệu mẫu cho vai trò
//        cboVaiTro.addItem("VT001 - Admin");
//        cboVaiTro.addItem("VT002 - Nhân viên bán hàng");
//        cboVaiTro.addItem("VT003 - Quản lý kho");
//        cboVaiTro.addItem("VT004 - Kế toán");
//
//        pnlForm.add(cboVaiTro, gbc);
//
//        // Panel chứa các nút
//        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//
//        JButton btnTao = new JButton("Tạo");
//        JButton btnHuy = new JButton("Hủy");
//
//        pnlButtons.add(btnTao);
//        pnlButtons.add(btnHuy);
//
//        // Thêm các panel vào dialog
//        dialog.add(pnlForm, BorderLayout.CENTER);
//        dialog.add(pnlButtons, BorderLayout.SOUTH);
//
//        // Đăng ký sự kiện
//        btnTao.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    // Kiểm tra dữ liệu nhập
//                    if (txtUsername.getText().trim().isEmpty()) {
//                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    if (cboVaiTro.getSelectedIndex() < 0) {
//                        JOptionPane.showMessageDialog(dialog, "Vui lòng chọn vai trò", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    // Lấy mã vai trò từ item được chọn (VT001 - Admin -> VT001)
//                    String vaiTroItem = cboVaiTro.getSelectedItem().toString();
//                    String idVT = vaiTroItem.split(" - ")[0];
//
//                    // Tạo dữ liệu tài khoản
//                    Map<String, Object> taiKhoanData = new HashMap<>();
//                    taiKhoanData.put("idNV", idNV);
//                    taiKhoanData.put("username", txtUsername.getText().trim());
//                    taiKhoanData.put("idVT", idVT);
//
//                    // Gửi yêu cầu tạo tài khoản
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("taiKhoan", taiKhoanData);
//                    RequestDTO request = new RequestDTO("CREATE_TAI_KHOAN", data);
//                    ResponseDTO response = clientService.sendRequest(request);
//
//                    if (response != null && response.isSuccess()) {
//                        JOptionPane.showMessageDialog(dialog, "Tạo tài khoản thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//                        // Cập nhật trạng thái tài khoản trong bảng
//                        int selectedRow = tblNhanVien.getSelectedRow();
//                        if (selectedRow >= 0) {
//                            DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
//                            model.setValueAt("Đã tạo", selectedRow, 6);
//                        }
//
//                        dialog.dispose();
//                        loadNhanVienData(); // Tải lại dữ liệu để cập nhật trạng thái
//                    } else {
//                        // Mô phỏng tạo tài khoản thành công
//                        JOptionPane.showMessageDialog(dialog, "Tạo tài khoản thành công (Mô phỏng)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//                        // Cập nhật trạng thái tài khoản trong bảng
//                        int selectedRow = tblNhanVien.getSelectedRow();
//                        if (selectedRow >= 0) {
//                            DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
//                            model.setValueAt("Đã tạo", selectedRow, 6);
//                        }
//
//                        dialog.dispose();
//                    }
//                } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//                    ex.printStackTrace();
//                }
//            }
//        });
//
//        btnHuy.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dialog.dispose();
//            }
//        });
//
//        // Hiển thị dialog
//        dialog.setVisible(true);
//    }

    private void showTaoTaiKhoanDialog(String idNV, String hoTen) {
        // Tạo dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo tài khoản", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        // Panel chứa form
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Thêm các thành phần vào form
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblNhanVien = new JLabel("Nhân viên:");
        lblNhanVien.setFont(new Font("Arial", Font.BOLD, 12));
        pnlForm.add(lblNhanVien, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField txtNhanVien = new JTextField(25);
        txtNhanVien.setText(hoTen);
        txtNhanVien.setEditable(false);
        txtNhanVien.setBorder(BorderFactory.createCompoundBorder(
                txtNhanVien.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlForm.add(txtNhanVien, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        pnlForm.add(lblUsername, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField txtUsername = new JTextField(25);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                txtUsername.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        pnlForm.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblVaiTro = new JLabel("Vai trò:");
        lblVaiTro.setFont(new Font("Arial", Font.BOLD, 12));
        pnlForm.add(lblVaiTro, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> cboVaiTro = new JComboBox<>();

        // Thêm dữ liệu mẫu cho vai trò
        cboVaiTro.addItem("VT001 - Admin");
        cboVaiTro.addItem("VT002 - Nhân viên bán hàng");
        cboVaiTro.addItem("VT003 - Quản lý kho");
        cboVaiTro.addItem("VT004 - Kế toán");

        cboVaiTro.setPreferredSize(new Dimension(txtUsername.getPreferredSize().width, 30));
        pnlForm.add(cboVaiTro, gbc);

        // Panel chứa các nút
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnTao = new JButton("Tạo");
        btnTao.setPreferredSize(new Dimension(100, 30));
        btnTao.setBackground(new Color(70, 130, 180));
        btnTao.setForeground(Color.WHITE);
        btnTao.setFocusPainted(false);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 30));
        btnHuy.setBackground(new Color(211, 211, 211));
        btnHuy.setFocusPainted(false);

        pnlButtons.add(btnTao);
        pnlButtons.add(btnHuy);

        // Thêm các panel vào dialog
        dialog.add(pnlForm, BorderLayout.CENTER);
        dialog.add(pnlButtons, BorderLayout.SOUTH);

        // Đăng ký sự kiện
        btnTao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Kiểm tra dữ liệu nhập
                    if (txtUsername.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên đăng nhập", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (cboVaiTro.getSelectedIndex() < 0) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng chọn vai trò", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Lấy mã vai trò từ item được chọn (VT001 - Admin -> VT001)
                    String vaiTroItem = cboVaiTro.getSelectedItem().toString();
                    String idVT = vaiTroItem.split(" - ")[0];

                    // Tạo dữ liệu tài khoản
                    Map<String, Object> taiKhoanData = new HashMap<>();
                    taiKhoanData.put("idNV", idNV);
                    taiKhoanData.put("username", txtUsername.getText().trim());
                    taiKhoanData.put("idVT", idVT);

                    // Gửi yêu cầu tạo tài khoản
                    Map<String, Object> data = new HashMap<>();
                    data.put("taiKhoan", taiKhoanData);
                    RequestDTO request = new RequestDTO("CREATE_TAI_KHOAN", data);
                    ResponseDTO response = clientService.sendRequest(request);

                    if (response != null && response.isSuccess()) {
                        JOptionPane.showMessageDialog(dialog, "Tạo tài khoản thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                        // Cập nhật trạng thái tài khoản trong bảng
                        int selectedRow = tblNhanVien.getSelectedRow();
                        if (selectedRow >= 0) {
                            DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
                            model.setValueAt("Đã tạo", selectedRow, 7);
                        }

                        dialog.dispose();
                        loadNhanVienData(); // Tải lại dữ liệu để cập nhật trạng thái
                    } else {
                        // Hiển thị thông báo lỗi từ server
                        String errorMessage = (response != null) ? response.getMessage() : "Không thể kết nối đến server";
                        JOptionPane.showMessageDialog(dialog, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnHuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        // Hiển thị dialog
        dialog.setVisible(true);
    }

    // Phương thức kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
