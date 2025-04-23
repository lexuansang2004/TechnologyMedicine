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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cboLoaiThongKe;
    private JPanel pnlContent;
    private JButton btnThongKe;
    private JComboBox<String> cboThoiGian;
    private ClientService clientService;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public ThongKePanel(ClientService clientService) {
        this.clientService = clientService;
        setLayout(new BorderLayout());

        // Panel chứa các điều khiển
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));

        pnlTop.add(new JLabel("Loại thống kê:"));
        cboLoaiThongKe = new JComboBox<>(new String[]{
                "Doanh thu theo thời gian",
                "Sản phẩm bán chạy",
                "Khách hàng tiềm năng",
                "Hiệu quả khuyến mãi"
        });
        pnlTop.add(cboLoaiThongKe);

        pnlTop.add(new JLabel("Thời gian:"));
        cboThoiGian = new JComboBox<>(new String[]{
                "Hôm nay",
                "7 ngày qua",
                "30 ngày qua",
                "Quý này",
                "Năm nay"
        });
        pnlTop.add(cboThoiGian);

        btnThongKe = new JButton("Thống kê");
        pnlTop.add(btnThongKe);

        // Panel chứa nội dung thống kê
        pnlContent = new JPanel();
        pnlContent.setLayout(new BorderLayout());
        pnlContent.setBorder(BorderFactory.createTitledBorder("Kết quả thống kê"));

        // Thêm các panel vào panel chính
        add(pnlTop, BorderLayout.NORTH);
        add(pnlContent, BorderLayout.CENTER);

        // Đăng ký sự kiện
        registerEvents();

        // Hiển thị nội dung mặc định
        showDefaultContent();
    }

    private void registerEvents() {
        btnThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateThongKe();
            }
        });

        cboLoaiThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Thay đổi nội dung hiển thị khi chọn loại thống kê khác
                showDefaultContent();
            }
        });
    }

    private void showDefaultContent() {
        // Xóa nội dung cũ
        pnlContent.removeAll();

        // Tạo nội dung mặc định
        JLabel lblMessage = new JLabel("Chọn loại thống kê và nhấn nút 'Thống kê' để xem kết quả", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Arial", Font.BOLD, 14));

        pnlContent.add(lblMessage, BorderLayout.CENTER);

        // Cập nhật giao diện
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void generateThongKe() {
        String loaiThongKe = (String) cboLoaiThongKe.getSelectedItem();
        String thoiGian = (String) cboThoiGian.getSelectedItem();

        // Xóa nội dung cũ
        pnlContent.removeAll();

        try {
            switch (loaiThongKe) {
                case "Doanh thu theo thời gian":
                    showDoanhThuTheoThoiGian(thoiGian);
                    break;
                case "Sản phẩm bán chạy":
                    showSanPhamBanChay(thoiGian);
                    break;
                case "Khách hàng tiềm năng":
                    showKhachHangTiemNang(thoiGian);
                    break;
                case "Hiệu quả khuyến mãi":
                    showHieuQuaKhuyenMai(thoiGian);
                    break;
                default:
                    showDefaultContent();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo thống kê: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            showDefaultContent();
        }

        // Cập nhật giao diện
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private void showDoanhThuTheoThoiGian(String thoiGian) {
        // Tạo panel hiển thị doanh thu theo thời gian
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng doanh thu
        String[] columnNames = {"Thời gian", "Số hóa đơn", "Tổng doanh thu", "Trung bình/hóa đơn"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Thêm dữ liệu mẫu (sẽ được thay thế bằng dữ liệu thực khi phát triển)
        model.addRow(new Object[]{"01/01/2024", "10", "5,000,000", "500,000"});
        model.addRow(new Object[]{"02/01/2024", "15", "7,500,000", "500,000"});
        model.addRow(new Object[]{"03/01/2024", "8", "4,000,000", "500,000"});
        model.addRow(new Object[]{"04/01/2024", "12", "6,000,000", "500,000"});
        model.addRow(new Object[]{"05/01/2024", "20", "10,000,000", "500,000"});

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Thêm thông tin tổng hợp
        JPanel pnlSummary = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlSummary.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel pnlTotalInvoices = createSummaryPanel("Tổng số hóa đơn", "65");
        JPanel pnlTotalRevenue = createSummaryPanel("Tổng doanh thu", "32,500,000 VNĐ");
        JPanel pnlAvgRevenue = createSummaryPanel("Trung bình/hóa đơn", "500,000 VNĐ");

        pnlSummary.add(pnlTotalInvoices);
        pnlSummary.add(pnlTotalRevenue);
        pnlSummary.add(pnlAvgRevenue);

        panel.add(pnlSummary, BorderLayout.SOUTH);

        // Thêm vào panel nội dung
        pnlContent.add(panel, BorderLayout.CENTER);
    }

    private void showSanPhamBanChay(String thoiGian) {
        // Tạo panel hiển thị sản phẩm bán chạy
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng sản phẩm bán chạy
        String[] columnNames = {"Mã thuốc", "Tên thuốc", "Số lượng bán", "Doanh thu", "Tỷ lệ"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Thêm dữ liệu mẫu (sẽ được thay thế bằng dữ liệu thực khi phát triển)
        model.addRow(new Object[]{"T001", "Paracetamol", "100", "1,500,000", "15%"});
        model.addRow(new Object[]{"T002", "Vitamin C", "80", "1,200,000", "12%"});
        model.addRow(new Object[]{"T003", "Aspirin", "70", "1,050,000", "10.5%"});
        model.addRow(new Object[]{"T004", "Amoxicillin", "60", "900,000", "9%"});
        model.addRow(new Object[]{"T005", "Omeprazole", "50", "750,000", "7.5%"});

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Thêm vào panel nội dung
        pnlContent.add(panel, BorderLayout.CENTER);
    }

    private void showKhachHangTiemNang(String thoiGian) {
        // Tạo panel hiển thị khách hàng tiềm năng
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng khách hàng tiềm năng
        String[] columnNames = {"Mã KH", "Tên khách hàng", "Số hóa đơn", "Tổng chi tiêu", "Hạng mục"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Thêm dữ liệu mẫu (sẽ được thay thế bằng dữ liệu thực khi phát triển)
        model.addRow(new Object[]{"KH001", "Nguyễn Văn A", "10", "5,000,000", "VIP"});
        model.addRow(new Object[]{"KH002", "Trần Thị B", "8", "4,000,000", "Thân thiết"});
        model.addRow(new Object[]{"KH003", "Lê Văn C", "6", "3,000,000", "Thân thiết"});
        model.addRow(new Object[]{"KH004", "Phạm Thị D", "5", "2,500,000", "Thường"});
        model.addRow(new Object[]{"KH005", "Hoàng Văn E", "4", "2,000,000", "Thường"});

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Thêm vào panel nội dung
        pnlContent.add(panel, BorderLayout.CENTER);
    }

    private void showHieuQuaKhuyenMai(String thoiGian) {
        // Tạo panel hiển thị hiệu quả khuyến mãi
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng hiệu quả khuyến mãi
        String[] columnNames = {"Mã KM", "Tên khuyến mãi", "Số lượt áp dụng", "Tổng giảm giá", "Hiệu quả"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Thêm dữ liệu mẫu (sẽ được thay thế bằng dữ liệu thực khi phát triển)
        model.addRow(new Object[]{"KM001", "Giảm giá 10% cho VIP", "20", "1,000,000", "Cao"});
        model.addRow(new Object[]{"KM002", "Giảm giá 5% cho Thân thiết", "30", "750,000", "Trung bình"});
        model.addRow(new Object[]{"KM003", "Mua 2 tặng 1", "15", "500,000", "Thấp"});
        model.addRow(new Object[]{"KM004", "Giảm giá thuốc hết hạn", "10", "300,000", "Thấp"});
        model.addRow(new Object[]{"KM005", "Khuyến mãi ngày lễ", "25", "1,250,000", "Cao"});

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Thêm vào panel nội dung
        pnlContent.add(panel, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 16));
        lblValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);

        return panel;
    }
}