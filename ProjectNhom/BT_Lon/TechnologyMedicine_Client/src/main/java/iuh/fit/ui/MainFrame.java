package iuh.fit.ui;

import iuh.fit.service.ClientService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private JButton thuocButton;
    private JButton khachHangButton;
    private JButton nhaCungCapButton;
    private JButton khuyenMaiButton;
    private JButton nhanVienButton;
    private JButton hoaDonButton;
    private JButton phieuNhapButton;
    private JButton thongKeButton;
    private JButton logoutButton;

    private Object currentUser;
    private ClientService clientService;

    public MainFrame(Object currentUser) {
        this.currentUser = currentUser;
        this.clientService = ClientService.getInstance();
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Thuốc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        contentPanel = new JPanel(new BorderLayout());

        thuocButton = new JButton("Quản Lý Thuốc");
        khachHangButton = new JButton("Quản Lý Khách Hàng");
        nhaCungCapButton = new JButton("Quản Lý Nhà Cung Cấp");
        khuyenMaiButton = new JButton("Quản Lý Khuyến Mãi");
        nhanVienButton = new JButton("Quản Lý Nhân Viên");
        hoaDonButton = new JButton("Quản Lý Hóa Đơn");
        phieuNhapButton = new JButton("Quản Lý Phiếu Nhập");
        thongKeButton = new JButton("Thống Kê");
        logoutButton = new JButton("Đăng Xuất");
    }

    private void setupLayout() {
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel menu bên trái
        JPanel menuPanel = new JPanel(new GridLayout(9, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));

        menuPanel.add(thuocButton);
        menuPanel.add(khachHangButton);
        menuPanel.add(nhaCungCapButton);
        menuPanel.add(khuyenMaiButton);
        menuPanel.add(nhanVienButton);
        menuPanel.add(hoaDonButton);
        menuPanel.add(phieuNhapButton);
        menuPanel.add(thongKeButton);
        menuPanel.add(logoutButton);

        // Panel nội dung
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Hiển thị màn hình chào mừng mặc định
        showWelcomePanel();

        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void setupListeners() {
        thuocButton.addActionListener(this::showThuocPanel);
        khachHangButton.addActionListener(this::showKhachHangPanel);
        nhaCungCapButton.addActionListener(this::showNhaCungCapPanel);
        khuyenMaiButton.addActionListener(this::showKhuyenMaiPanel);
        nhanVienButton.addActionListener(this::showNhanVienPanel);
        hoaDonButton.addActionListener(this::showHoaDonPanel);
        phieuNhapButton.addActionListener(this::showPhieuNhapPanel);
        thongKeButton.addActionListener(this::showThongKePanel);
        logoutButton.addActionListener(this::logout);
    }

    private void showWelcomePanel() {
        contentPanel.removeAll();

        JPanel welcomePanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Chào mừng đến với Hệ Thống Quản Lý Thuốc", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel instructionLabel = new JLabel("Vui lòng chọn chức năng từ menu bên trái", JLabel.CENTER);
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(instructionLabel, BorderLayout.SOUTH);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showThuocPanel(ActionEvent e) {
        contentPanel.removeAll();

        ThuocPanel thuocPanel = new ThuocPanel();
        contentPanel.add(thuocPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showKhachHangPanel(ActionEvent e) {
        contentPanel.removeAll();

        KhachHangPanel khachHangPanel = new KhachHangPanel();
        contentPanel.add(khachHangPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNhaCungCapPanel(ActionEvent e) {
        contentPanel.removeAll();

        NhaCungCapPanel nhaCungCapPanel = new NhaCungCapPanel();
        contentPanel.add(nhaCungCapPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showKhuyenMaiPanel(ActionEvent e) {
        contentPanel.removeAll();

        try {
            KhuyenMaiPanel khuyenMaiPanel = new KhuyenMaiPanel();
            contentPanel.add(khuyenMaiPanel, BorderLayout.CENTER);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải giao diện Khuyến Mãi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNhanVienPanel(ActionEvent e) {
        contentPanel.removeAll();

        NhanVienPanel nhanVienPanel = new NhanVienPanel(clientService);
        contentPanel.add(nhanVienPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHoaDonPanel(ActionEvent e) {
        contentPanel.removeAll();
        contentPanel.add(new HoaDonPanel(clientService), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPhieuNhapPanel(ActionEvent e) {
        contentPanel.removeAll();
        contentPanel.add(new PhieuNhapPanel(clientService), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showThongKePanel(ActionEvent e) {
        contentPanel.removeAll();
        contentPanel.add(new ThongKePanel(clientService), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Đóng kết nối
            try {
                ClientService.getInstance().disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Mở lại màn hình đăng nhập
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

            // Đóng màn hình chính
            dispose();
        }
    }
}