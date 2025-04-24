package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel forgotPasswordLabel;

    public LoginFrame() {
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        setTitle("Đăng Nhập - Hệ Thống Quản Lý Thuốc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng Nhập");
        exitButton = new JButton("Thoát");
        forgotPasswordLabel = new JLabel("Quên mật khẩu?");
        forgotPasswordLabel.setForeground(Color.BLUE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ THUỐC");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titlePanel.add(titleLabel);

        // Panel đăng nhập
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(forgotPasswordLabel, gbc);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupListeners() {
        loginButton.addActionListener(this::handleLogin);
        exitButton.addActionListener(e -> System.exit(0));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showForgotPasswordDialog();
            }
        });

        // Cho phép nhấn Enter để đăng nhập
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ResponseDTO response = ClientService.getInstance().login(username, password);

            if (response.isSuccess()) {
                // Đăng nhập thành công, mở màn hình chính
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Lấy thông tin người dùng từ response
                Object user = response.getData().get("user");

                // Mở màn hình chính
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);

                // Đóng màn hình đăng nhập
                dispose();
            } else {
                // Đăng nhập thất bại
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến server: " + ex.getMessage(), "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showForgotPasswordDialog() {
        JDialog dialog = new JDialog(this, "Quên Mật Khẩu", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel infoLabel = new JLabel("Nhập mã nhân viên để đặt lại mật khẩu:");
        JTextField idNVField = new JTextField(20);
        JButton submitButton = new JButton("Gửi Yêu Cầu");
        JButton cancelButton = new JButton("Hủy");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(infoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Mã nhân viên:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(idNVField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);

        // Xử lý sự kiện nút Gửi Yêu Cầu
        submitButton.addActionListener(e -> {
            String idNV = idNVField.getText().trim();
            if (idNV.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã nhân viên", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Gửi yêu cầu đặt lại mật khẩu
                Map<String, Object> data = new HashMap<>();
                data.put("idNV", idNV);
                RequestDTO request = new RequestDTO("RESET_PASSWORD", data);
                ResponseDTO response = ClientService.getInstance().sendRequest(request);

                if (response != null && response.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Mật khẩu mới đã được gửi đến email của bạn.\nVui lòng kiểm tra hộp thư đến.",
                            "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    String errorMessage = (response != null) ? response.getMessage() : "Không thể kết nối đến server";
                    JOptionPane.showMessageDialog(dialog, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Xử lý sự kiện nút Hủy
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}