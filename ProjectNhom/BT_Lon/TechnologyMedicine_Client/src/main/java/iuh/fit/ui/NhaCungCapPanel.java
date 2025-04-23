package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhaCungCapPanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JTable nhaCungCapTable;
    private DefaultTableModel tableModel;

    private JTextField idNCCField;
    private JTextField tenNCCField;
    private JTextField sdtField;
    private JTextField diaChiField;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    public NhaCungCapPanel() {
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
        String[] columnNames = {"ID", "Tên Nhà Cung Cấp", "SĐT", "Địa Chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        nhaCungCapTable = new JTable(tableModel);

        idNCCField = new JTextField(20);
        idNCCField.setEditable(false);
        tenNCCField = new JTextField(20);
        sdtField = new JTextField(20);
        diaChiField = new JTextField(20);

        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập Nhật");
        deleteButton = new JButton("Xóa");
        clearButton = new JButton("Xóa Form");
    }

    private void setupLayout() {
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Panel bảng
        JScrollPane tableScrollPane = new JScrollPane(nhaCungCapTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));

        // Panel form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Nhà Cung Cấp"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Cột 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tên Nhà Cung Cấp:"), gbc);

        // Cột 2
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(idNCCField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(tenNCCField, gbc);

        // Cột 3
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("SĐT:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Địa Chỉ:"), gbc);

        // Cột 4
        gbc.gridx = 3;
        gbc.gridy = 0;
        formPanel.add(sdtField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(diaChiField, gbc);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Panel chính
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        searchButton.addActionListener(e -> searchNhaCungCap());
        refreshButton.addActionListener(e -> loadData());

        nhaCungCapTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && nhaCungCapTable.getSelectedRow() != -1) {
                displaySelectedNhaCungCap();
            }
        });

        addButton.addActionListener(e -> addNhaCungCap());
        updateButton.addActionListener(e -> updateNhaCungCap());
        deleteButton.addActionListener(e -> deleteNhaCungCap());
        clearButton.addActionListener(e -> clearForm());
    }

    private void loadData() {
        try {
            ResponseDTO response = ClientService.getInstance().getAllNhaCungCap();

            if (response.isSuccess()) {
                // Xóa dữ liệu cũ
                tableModel.setRowCount(0);

                // Lấy danh sách nhà cung cấp từ response
                List<Map<String, Object>> nhaCungCapList = (List<Map<String, Object>>) response.getData().get("nhaCungCapList");

                // Thêm dữ liệu vào bảng
                for (Map<String, Object> nhaCungCap : nhaCungCapList) {
                    Object[] rowData = {
                            nhaCungCap.get("idNCC"),
                            nhaCungCap.get("tenNCC"),
                            nhaCungCap.get("sdt"),
                            nhaCungCap.get("diaChi")
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

    private void searchNhaCungCap() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("SEARCH_NHA_CUNG_CAP", Map.of("keyword", keyword)));

            if (response.isSuccess()) {
                // Xóa dữ liệu cũ
                tableModel.setRowCount(0);

                // Lấy danh sách nhà cung cấp từ response
                List<Map<String, Object>> nhaCungCapList = (List<Map<String, Object>>) response.getData().get("nhaCungCapList");

                // Thêm dữ liệu vào bảng
                for (Map<String, Object> nhaCungCap : nhaCungCapList) {
                    Object[] rowData = {
                            nhaCungCap.get("idNCC"),
                            nhaCungCap.get("tenNCC"),
                            nhaCungCap.get("sdt"),
                            nhaCungCap.get("diaChi")
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

    private void displaySelectedNhaCungCap() {
        int selectedRow = nhaCungCapTable.getSelectedRow();

        if (selectedRow != -1) {
            String idNCC = (String) tableModel.getValueAt(selectedRow, 0);

            try {
                ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("GET_NHA_CUNG_CAP_BY_ID", Map.of("idNCC", idNCC)));

                if (response.isSuccess()) {
                    Map<String, Object> nhaCungCap = (Map<String, Object>) response.getData().get("nhaCungCap");

                    // Hiển thị thông tin nhà cung cấp lên form
                    idNCCField.setText((String) nhaCungCap.get("idNCC"));
                    tenNCCField.setText((String) nhaCungCap.get("tenNCC"));
                    sdtField.setText((String) nhaCungCap.get("sdt"));
                    diaChiField.setText((String) nhaCungCap.get("diaChi"));
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin nhà cung cấp: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addNhaCungCap() {
        // Kiểm tra dữ liệu nhập vào
        if (!validateInput()) {
            return;
        }

        try {
            // Tạo đối tượng nhà cung cấp từ form
            Map<String, Object> nhaCungCap = createNhaCungCapFromForm();

            // Gửi yêu cầu thêm nhà cung cấp
            ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("SAVE_NHA_CUNG_CAP", Map.of("nhaCungCap", nhaCungCap)));

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhà cung cấp: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNhaCungCap() {
        // Kiểm tra xem đã chọn nhà cung cấp chưa
        if (idNCCField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra dữ liệu nhập vào
        if (!validateInput()) {
            return;
        }

        try {
            // Tạo đối tượng nhà cung cấp từ form
            Map<String, Object> nhaCungCap = createNhaCungCapFromForm();

            // Gửi yêu cầu cập nhật nhà cung cấp
            ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("UPDATE_NHA_CUNG_CAP", Map.of("nhaCungCap", nhaCungCap)));

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nhà cung cấp: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNhaCungCap() {
        // Kiểm tra xem đã chọn nhà cung cấp chưa
        if (idNCCField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhà cung cấp này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                // Gửi yêu cầu xóa nhà cung cấp
                ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("DELETE_NHA_CUNG_CAP", Map.of("idNCC", idNCCField.getText())));

                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhà cung cấp: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        idNCCField.setText("");
        tenNCCField.setText("");
        sdtField.setText("");
        diaChiField.setText("");

        nhaCungCapTable.clearSelection();
    }

    private boolean validateInput() {
        // Kiểm tra tên nhà cung cấp
        if (tenNCCField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            tenNCCField.requestFocus();
            return false;
        }

        // Kiểm tra số điện thoại
        if (sdtField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            sdtField.requestFocus();
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        if (!sdtField.getText().trim().matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải có 10-11 chữ số)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            sdtField.requestFocus();
            return false;
        }

        // Kiểm tra địa chỉ
        if (diaChiField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            diaChiField.requestFocus();
            return false;
        }

        return true;
    }

    private Map<String, Object> createNhaCungCapFromForm() {
        Map<String, Object> nhaCungCap = new HashMap<>();

        // Nếu là thêm mới, tạo ID mới
        if (idNCCField.getText().isEmpty()) {
            try {
                ResponseDTO response = ClientService.getInstance().sendRequest(new RequestDTO("GENERATE_NHA_CUNG_CAP_ID", null));
                if (response.isSuccess()) {
                    nhaCungCap.put("idNCC", response.getData().get("idNCC"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            nhaCungCap.put("idNCC", idNCCField.getText());
        }

        nhaCungCap.put("tenNCC", tenNCCField.getText());
        nhaCungCap.put("sdt", sdtField.getText());
        nhaCungCap.put("diaChi", diaChiField.getText());

        return nhaCungCap;
    }
}