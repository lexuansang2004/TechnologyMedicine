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
import java.util.List;
import java.util.Map;

public class PhieuNhapPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable tblPhieuNhap;
    private DefaultTableModel modelPhieuNhap;
    private JButton btnThem, btnXem, btnTimKiem, btnLamMoi;
    private JTextField txtTimKiem;
    private ClientService clientService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public PhieuNhapPanel(ClientService clientService) {
        this.clientService = clientService;
        setLayout(new BorderLayout());

        // Panel chứa các nút chức năng
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnThem = new JButton("Tạo phiếu nhập mới");
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
        pnlTable.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu nhập"));

        String[] columnNames = {"Mã PN", "Thời gian", "Nhà cung cấp", "Nhân viên", "Tổng tiền"};
        modelPhieuNhap = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPhieuNhap = new JTable(modelPhieuNhap);
        JScrollPane scrollPane = new JScrollPane(tblPhieuNhap);
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        // Thêm các panel vào panel chính
        add(pnlTop, BorderLayout.NORTH);
        add(pnlTable, BorderLayout.CENTER);

        // Đăng ký sự kiện
        registerEvents();

        // Tải dữ liệu phiếu nhập
        loadPhieuNhapData();
    }

    private void registerEvents() {
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTaoPhieuNhapDialog();
            }
        });

        btnXem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChiTietPhieuNhap();
            }
        });

        btnTimKiem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPhieuNhap();
            }
        });

        btnLamMoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.setText("");
                loadPhieuNhapData();
            }
        });
    }

    private void loadPhieuNhapData() {
        try {
            // Xóa dữ liệu cũ
            modelPhieuNhap.setRowCount(0);

            // Lấy danh sách phiếu nhập từ server
            ResponseDTO response = clientService.getAllPhieuNhap();
            System.out.println("Response from server: " + response);

            if (response.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> phieuNhapList = (List<Map<String, Object>>) response.getData().get("phieuNhapList");
                System.out.println("Found " + phieuNhapList.size() + " phieu nhap records");

                for (Map<String, Object> phieuNhap : phieuNhapList) {
                    String idPN = (String) phieuNhap.get("idPN");

                    // Xử lý thời gian
                    String thoiGianStr = "";
                    try {
                        Object thoiGianObj = phieuNhap.get("thoiGian");
                        if (thoiGianObj instanceof LocalDateTime) {
                            thoiGianStr = ((LocalDateTime) thoiGianObj).format(dateFormatter);
                        } else if (thoiGianObj instanceof String) {
                            // Nếu là string, thử parse thành LocalDateTime
                            try {
                                LocalDateTime thoiGian = LocalDateTime.parse((String) thoiGianObj);
                                thoiGianStr = thoiGian.format(dateFormatter);
                            } catch (Exception e) {
                                // Nếu không parse được, sử dụng string trực tiếp
                                thoiGianStr = (String) thoiGianObj;
                            }
                        } else {
                            thoiGianStr = thoiGianObj != null ? thoiGianObj.toString() : "";
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing thoiGian for " + idPN + ": " + e.getMessage());
                        thoiGianStr = "N/A";
                    }

                    // Lấy thông tin nhà cung cấp
                    String tenNCC = "N/A";
                    try {
                        if (phieuNhap.containsKey("nhaCungCap")) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> nhaCungCap = (Map<String, Object>) phieuNhap.get("nhaCungCap");
                            tenNCC = (String) nhaCungCap.get("tenNCC");
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing nhaCungCap for " + idPN + ": " + e.getMessage());
                    }

                    // Lấy thông tin nhân viên
                    String tenNV = "N/A";
                    try {
                        if (phieuNhap.containsKey("nhanVien")) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> nhanVien = (Map<String, Object>) phieuNhap.get("nhanVien");
                            tenNV = (String) nhanVien.get("hoTen");
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing nhanVien for " + idPN + ": " + e.getMessage());
                    }

                    // Xử lý tổng tiền
                    String tongTienStr = "0";
                    try {
                        double tongTien = Double.parseDouble(phieuNhap.get("tongTien").toString());
                        tongTienStr = decimalFormat.format(tongTien);
                    } catch (Exception e) {
                        System.err.println("Error processing tongTien for " + idPN + ": " + e.getMessage());
                    }

                    // Thêm dòng vào bảng
                    modelPhieuNhap.addRow(new Object[]{idPN, thoiGianStr, tenNCC, tenNV, tongTienStr});
                }

                if (phieuNhapList.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Không có dữ liệu phiếu nhập nào được tìm thấy. Vui lòng kiểm tra kết nối đến cơ sở dữ liệu.",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tải danh sách phiếu nhập: " + response.getMessage(),
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

    private void showTaoPhieuNhapDialog() {
        JOptionPane.showMessageDialog(this, "Chức năng tạo phiếu nhập mới đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        // Phần code tạo dialog tạo phiếu nhập mới sẽ được phát triển sau
    }

    private void showChiTietPhieuNhap() {
        int selectedRow = tblPhieuNhap.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để xem chi tiết", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idPN = (String) tblPhieuNhap.getValueAt(selectedRow, 0);
        try {
            // Lấy thông tin chi tiết phiếu nhập
            ResponseDTO response = clientService.getChiTietPhieuNhap(idPN);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Chức năng xem chi tiết phiếu nhập đang được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // Phần code hiển thị chi tiết phiếu nhập sẽ được phát triển sau
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết phiếu nhập: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối đến server: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPhieuNhap() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadPhieuNhapData();
            return;
        }

        // Tìm kiếm trong bảng hiện tại
        for (int i = 0; i < tblPhieuNhap.getRowCount(); i++) {
            boolean found = false;
            for (int j = 0; j < tblPhieuNhap.getColumnCount(); j++) {
                String cellValue = tblPhieuNhap.getValueAt(i, j).toString().toLowerCase();
                if (cellValue.contains(keyword.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                tblPhieuNhap.setRowSelectionInterval(i, i);
                tblPhieuNhap.scrollRectToVisible(tblPhieuNhap.getCellRect(i, 0, true));
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập phù hợp với từ khóa: " + keyword, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}