package iuh.fit.ui;

import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        setLayout(new BorderLayout());

        // Panel chứa các nút chức năng
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnThem = new JButton("Tạo hóa đơn mới");
        btnXem = new JButton("Xem chi tiết");
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm kiếm");
        btnLamMoi = new JButton("Làm mới");

        pnlTop.add(btnThem);
        pnlTop.add(btnXem);
        pnlTop.add(new JLabel("Tìm kiếm:"));
        pnlTop.add(txtTimKiem);
        pnlTop.add(btnTimKiem);
        pnlTop.add(btnLamMoi);

        // Panel chứa bảng dữ liệu
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));

        String[] columnNames = {"Mã HD", "Ngày lập", "Khách hàng", "Nhân viên", "Tổng tiền"};
        modelHoaDon = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHoaDon = new JTable(modelHoaDon);
        JScrollPane scrollPane = new JScrollPane(tblHoaDon);
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        // Thêm các panel vào panel chính
        add(pnlTop, BorderLayout.NORTH);
        add(pnlTable, BorderLayout.CENTER);

        // Đăng ký sự kiện
        registerEvents();

        // Tải dữ liệu hóa đơn
        loadHoaDonData();
    }

    private void registerEvents() {
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTaoHoaDonDialog();
            }
        });

        btnXem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChiTietHoaDon();
            }
        });

        btnTimKiem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchHoaDon();
            }
        });

        btnLamMoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.setText("");
                loadHoaDonData();
            }
        });
    }

    private void loadHoaDonData() {
        try {
            // Xóa dữ liệu cũ
            modelHoaDon.setRowCount(0);

            // Lấy danh sách hóa đơn từ server
            ResponseDTO response = clientService.getAllHoaDon();
            System.out.println("Response from server: " + response);

            if (response.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> hoaDonList = (List<Map<String, Object>>) response.getData().get("hoaDonList");
                System.out.println("Found " + hoaDonList.size() + " hoa don records");

                for (Map<String, Object> hoaDon : hoaDonList) {
                    String idHD = (String) hoaDon.get("idHD");

                    // Xử lý ngày lập
                    String ngayLapStr = "";
                    try {
                        Object ngayLapObj = hoaDon.get("ngayLap");
                        if (ngayLapObj instanceof LocalDateTime) {
                            ngayLapStr = ((LocalDateTime) ngayLapObj).format(dateFormatter);
                        } else if (ngayLapObj instanceof String) {
                            // Nếu là string, thử parse thành LocalDateTime
                            try {
                                LocalDateTime ngayLap = LocalDateTime.parse((String) ngayLapObj);
                                ngayLapStr = ngayLap.format(dateFormatter);
                            } catch (Exception e) {
                                // Nếu không parse được, sử dụng string trực tiếp
                                ngayLapStr = (String) ngayLapObj;
                            }
                        } else {
                            ngayLapStr = ngayLapObj != null ? ngayLapObj.toString() : "";
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing ngayLap for " + idHD + ": " + e.getMessage());
                        ngayLapStr = "N/A";
                    }

                    // Lấy thông tin khách hàng
                    String tenKH = "N/A";
                    try {
                        if (hoaDon.containsKey("khachHang")) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> khachHang = (Map<String, Object>) hoaDon.get("khachHang");
                            tenKH = (String) khachHang.get("hoTen");
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing khachHang for " + idHD + ": " + e.getMessage());
                    }

                    // Lấy thông tin nhân viên
                    String tenNV = "N/A";
                    try {
                        if (hoaDon.containsKey("nhanVien")) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> nhanVien = (Map<String, Object>) hoaDon.get("nhanVien");
                            tenNV = (String) nhanVien.get("hoTen");
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing nhanVien for " + idHD + ": " + e.getMessage());
                    }

                    // Xử lý tổng tiền
                    String tongTienStr = "0";
                    try {
                        double tongTien = Double.parseDouble(hoaDon.get("tongTien").toString());
                        tongTienStr = decimalFormat.format(tongTien);
                    } catch (Exception e) {
                        System.err.println("Error processing tongTien for " + idHD + ": " + e.getMessage());
                    }

                    // Thêm dòng vào bảng
                    modelHoaDon.addRow(new Object[]{idHD, ngayLapStr, tenKH, tenNV, tongTienStr});
                }

                if (hoaDonList.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Không có dữ liệu hóa đơn nào được tìm thấy. Vui lòng kiểm tra kết nối đến cơ sở dữ liệu.",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tải danh sách hóa đơn: " + response.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối đến server: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi không xác định: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTaoHoaDonDialog() {
        JOptionPane.showMessageDialog(this, "Chức năng tạo hóa đơn mới đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        // Phần code tạo dialog tạo hóa đơn mới sẽ được phát triển sau
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
            ResponseDTO response = clientService.getChiTietHoaDon(idHD);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Chức năng xem chi tiết hóa đơn đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Phần code hiển thị chi tiết hóa đơn sẽ được phát triển sau
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết hóa đơn: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối đến server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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