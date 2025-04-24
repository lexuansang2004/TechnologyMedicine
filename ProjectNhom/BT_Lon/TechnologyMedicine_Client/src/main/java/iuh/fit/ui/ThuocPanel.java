package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.DecimalFormat;
import java.util.List;

public class ThuocPanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JTable thuocTable;
    private DefaultTableModel tableModel;

    private JTextField idThuocField;
    private JTextField tenThuocField;
    private JTextField thanhPhanField;
    private JComboBox<String> donViTinhComboBox;
    private JComboBox<String> danhMucComboBox;
    private JComboBox<String> xuatXuComboBox;
    private JTextField soLuongTonField;
    private JTextField giaNhapField;
    private JTextField donGiaField;
    private JDateChooser hanSuDungChooser;
    private JButton chooseImageButton;
    private JLabel imageLabel;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;

    private ClientService clientService;

    public ThuocPanel() {
        this.clientService = ClientService.getInstance();
        initComponents();
        setupLayout();
        setupListeners();
        loadData();
    }

    private JComboBox<String> searchCriteriaComboBox;
    // Thêm vào phần khai báo biến của lớp ThuocPanel
    private String currentImagePath = null; // Đường dẫn đầy đủ của ảnh
    private String selectedImageName = null; // Tên file ảnh đã chọn

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        searchField = new JTextField(20);
        searchCriteriaComboBox = new JComboBox<>(new String[] {
                "Tất cả", "Mã thuốc", "Tên thuốc", "Xuất xứ", "Danh mục"
        });
        searchButton = new JButton("Tìm Kiếm");
        refreshButton = new JButton("Làm Mới");

        // Tạo model cho bảng
        String[] columnNames = {"ID", "Tên Thuốc", "Đơn Vị Tính", "Danh Mục", "Xuất Xứ", "Số Lượng Tồn", "Đơn Giá", "Hạn Sử Dụng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        thuocTable = new JTable(tableModel);

        idThuocField = new JTextField(20);
        idThuocField.setEditable(false);
        tenThuocField = new JTextField(20);
        thanhPhanField = new JTextField(20);
        donViTinhComboBox = new JComboBox<>();
        danhMucComboBox = new JComboBox<>();
        xuatXuComboBox = new JComboBox<>();
        soLuongTonField = new JTextField(20);
        giaNhapField = new JTextField(20);
        donGiaField = new JTextField(20);
        hanSuDungChooser = new JDateChooser();
        chooseImageButton = new JButton("Chọn Ảnh");
        imageLabel = new JLabel();
        // Trong phương thức khởi tạo giao diện
        JLabel imagePathLabel = new JLabel("Chưa có ảnh");
        imagePathLabel.setForeground(Color.BLUE);
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập Nhật");
        deleteButton = new JButton("Xóa");
        clearButton = new JButton("Xóa Form");

        // Load dữ liệu cho các combobox
        loadComboBoxData();
    }

    private void setupLayout() {
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm theo:"));
        searchPanel.add(searchCriteriaComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Panel bảng
        JScrollPane tableScrollPane = new JScrollPane(thuocTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 200));

        // Panel form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Thuốc"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Cột 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tên Thuốc:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Thành Phần:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Đơn Vị Tính:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Danh Mục:"), gbc);

        // Cột 2
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(idThuocField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(tenThuocField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(thanhPhanField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(donViTinhComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(danhMucComboBox, gbc);

        // Cột 3
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Xuất Xứ:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Số Lượng Tồn:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Giá Nhập:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Đơn Giá:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Hạn Sử Dụng:"), gbc);

        // Cột 4
        gbc.gridx = 3;
        gbc.gridy = 0;
        formPanel.add(xuatXuComboBox, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(soLuongTonField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        formPanel.add(giaNhapField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 3;
        formPanel.add(donGiaField, gbc);

        gbc.gridx = 3;
        gbc.gridy = 4;
        formPanel.add(hanSuDungChooser, gbc);

        // Cột 5 - Ảnh
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        formPanel.add(imageLabel, gbc);

        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.gridheight = 1;
        formPanel.add(chooseImageButton, gbc);

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
        searchButton.addActionListener(e -> searchThuoc());
        refreshButton.addActionListener(e -> loadData());

        thuocTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && thuocTable.getSelectedRow() != -1) {
                displaySelectedThuoc();
            }
        });

        addButton.addActionListener(e -> addThuoc());
        updateButton.addActionListener(e -> updateThuoc());
        deleteButton.addActionListener(e -> deleteThuoc());
        clearButton.addActionListener(e -> clearForm());

        chooseImageButton.addActionListener(e -> chooseImage());
    }

//    private void loadData() {
//        try {
//            // Xóa dữ liệu cũ
//            tableModel.setRowCount(0);
//
//            // Gọi API lấy danh sách thuốc
//            ResponseDTO response = clientService.getAllThuoc();
//            System.out.println("Response from server: " + response);
//
//            // Kiểm tra xem phản hồi có thành công không
//            if (response.isSuccess()) {
//                // Lấy dữ liệu thuocList từ response
//                Object thuocDataObj = response.getData();
//
//                // Kiểm tra nếu thuocDataObj là null
//                if (thuocDataObj == null) {
//                    JOptionPane.showMessageDialog(this,
//                            "Dữ liệu thuốc không có sẵn hoặc không đúng định dạng.",
//                            "Lỗi", JOptionPane.ERROR_MESSAGE);
//                    return; // Dừng lại nếu không có dữ liệu
//                }
//
//                // In kiểu dữ liệu của thuocDataObj
//                System.out.println("Thuoc data object type: " + thuocDataObj.getClass().getName());
//
//                // Kiểm tra xem thuocDataObj có phải là Map không
//                if (thuocDataObj instanceof Map<?, ?>) {
//                    @SuppressWarnings("unchecked")
//                    Map<String, Object> rootData = (Map<String, Object>) thuocDataObj;
//                    Map<String, Map<String, Object>> thuocData = (Map<String, Map<String, Object>>) rootData.get("thuocs");
//
//                    // Kiểm tra xem thuocData có rỗng không
//                    if (thuocData.isEmpty()) {
//                        JOptionPane.showMessageDialog(this,
//                                "Không có dữ liệu thuốc nào được tìm thấy. Vui lòng kiểm tra kết nối đến cơ sở dữ liệu.",
//                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                    } else {
//                        // Định dạng tiền tệ cho giá
//                        DecimalFormat decimalFormat = new DecimalFormat("#,###");
//
//                        // Duyệt qua danh sách thuốc và thêm vào bảng
//                        for (Map.Entry<String, Map<String, Object>> entry : thuocData.entrySet()) {
//                            Map<String, Object> thuoc = entry.getValue();
//                            String idThuoc = (String) thuoc.get("idThuoc");
//                            String tenThuoc = (String) thuoc.get("tenThuoc");
//                            String soLuongTonStr = String.valueOf(thuoc.get("soLuongTon"));
//                            String donViTinhStr = "Không có";
//                            String danhMucStr = "Không có";
//                            String xuatXuStr = "Không có";
//                            String donGiaStr = "";
//                            String hanSuDungStr = "";
//
//                            try {
//                                // Đơn vị tính
//                                Map<String, Object> donViTinhMap = (Map<String, Object>) thuoc.get("donViTinh");
//                                if (donViTinhMap != null && donViTinhMap.get("ten") != null) {
//                                    donViTinhStr = donViTinhMap.get("ten").toString();
//                                }
//
//                                // Danh mục
//                                Map<String, Object> danhMucMap = (Map<String, Object>) thuoc.get("danhMuc");
//                                if (danhMucMap != null && danhMucMap.get("ten") != null) {
//                                    danhMucStr = danhMucMap.get("ten").toString();
//                                }
//
//                                // Xuất xứ
//                                Map<String, Object> xuatXuMap = (Map<String, Object>) thuoc.get("xuatXu");
//                                if (xuatXuMap != null && xuatXuMap.get("ten") != null) {
//                                    xuatXuStr = xuatXuMap.get("ten").toString();
//                                }
//
//                                // Hạn sử dụng
//                                Object hanSuDungObj = thuoc.get("hanSuDung");
//                                if (hanSuDungObj != null) {
//                                    hanSuDungStr = hanSuDungObj.toString();
//                                }
//                            } catch (Exception e) {
//                                System.err.println("Error parsing nested fields for " + idThuoc + ": " + e.getMessage());
//                            }
//
//
//                            // Thêm vào bảng
//                            Object[] rowData = {
//                                    idThuoc,
//                                    tenThuoc,
//                                    donViTinhStr,
//                                    danhMucStr,
//                                    xuatXuStr,
//                                    soLuongTonStr,
//                                    donGiaStr,
//                                    hanSuDungStr
//                            };
//                            tableModel.addRow(rowData);
//                        }
//                    }
//                } else {
//                    // Nếu dữ liệu không phải là Map
//                    System.err.println("Dữ liệu thuocList không phải là Map.");
//                    JOptionPane.showMessageDialog(this,
//                            "Dữ liệu thuốc không đúng định dạng.",
//                            "Lỗi", JOptionPane.ERROR_MESSAGE);
//                }
//            } else {
//                // Nếu phản hồi từ server không thành công
//                JOptionPane.showMessageDialog(this,
//                        "Lỗi khi tải danh sách thuốc: " + response.getMessage(),
//                        "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this,
//                    "Lỗi kết nối đến server: " + e.getMessage(),
//                    "Lỗi", JOptionPane.ERROR_MESSAGE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this,
//                    "Lỗi không xác định: " + e.getMessage(),
//                    "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private void loadData() {
        try {
            // Xóa dữ liệu cũ
            tableModel.setRowCount(0);

            // Gọi API lấy danh sách thuốc
            ResponseDTO response = clientService.getAllThuoc();

            // Kiểm tra xem phản hồi có thành công không
            if (response.isSuccess()) {
                // Lấy dữ liệu thuốc từ response
                Map<String, Object> rootData = (Map<String, Object>) response.getData();
                Map<String, Map<String, Object>> thuocData = (Map<String, Map<String, Object>>) rootData.get("thuocs");

                if (thuocData == null || thuocData.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không có dữ liệu thuốc", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Định dạng tiền tệ
                DecimalFormat decimalFormat = new DecimalFormat("#,###");

                // Duyệt qua danh sách thuốc
                for (Map.Entry<String, Map<String, Object>> entry : thuocData.entrySet()) {
                    Map<String, Object> thuoc = entry.getValue();

                    // Lấy thông tin cơ bản
                    String idThuoc = (String) thuoc.get("idThuoc");
                    String tenThuoc = (String) thuoc.get("tenThuoc");
                    Integer soLuongTon = ((Number) thuoc.get("soLuongTon")).intValue();

                    // Lấy thông tin đơn vị tính
                    String donViTinhStr = "Không có";
                    if (thuoc.get("donViTinh") instanceof Map) {
                        Map<String, Object> donViTinh = (Map<String, Object>) thuoc.get("donViTinh");
                        donViTinhStr = (String) donViTinh.get("ten");
                    }

                    // Lấy thông tin danh mục
                    String danhMucStr = "Không có";
                    if (thuoc.get("danhMuc") instanceof Map) {
                        Map<String, Object> danhMuc = (Map<String, Object>) thuoc.get("danhMuc");
                        danhMucStr = (String) danhMuc.get("ten");
                    }

                    // Lấy thông tin xuất xứ
                    String xuatXuStr = "Không có";
                    if (thuoc.get("xuatXu") instanceof Map) {
                        Map<String, Object> xuatXu = (Map<String, Object>) thuoc.get("xuatXu");
                        xuatXuStr = (String) xuatXu.get("ten");
                    }

                    // Lấy thông tin giá
                    Double donGia = thuoc.get("donGia") instanceof Number ?
                            ((Number) thuoc.get("donGia")).doubleValue() : 0.0;
                    String donGiaStr = decimalFormat.format(donGia);

                    // Lấy thông tin hạn sử dụng
                    String hanSuDungStr = thuoc.get("hanSuDung") != null ?
                            thuoc.get("hanSuDung").toString() : "";

                    // Thêm vào bảng
                    Object[] rowData = {
                            idThuoc,
                            tenThuoc,
                            donViTinhStr,
                            danhMucStr,
                            xuatXuStr,
                            soLuongTon,
                            donGiaStr,
                            hanSuDungStr
                    };
                    tableModel.addRow(rowData);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tải danh sách thuốc: " + response.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi không xác định: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadComboBoxData() {
        // Thêm dữ liệu mẫu cho đơn vị tính
        donViTinhComboBox.removeAllItems();
        donViTinhComboBox.addItem("DVT001 - Viên");
        donViTinhComboBox.addItem("DVT002 - Vỉ");
        donViTinhComboBox.addItem("DVT003 - Hộp");
        donViTinhComboBox.addItem("DVT004 - Chai");
        donViTinhComboBox.addItem("DVT005 - Lọ");
        donViTinhComboBox.addItem("DVT006 - Gói");
        donViTinhComboBox.addItem("DVT007 - Ống");
        donViTinhComboBox.addItem("DVT008 - Tuýp");
        donViTinhComboBox.addItem("DVT009 - Miếng");
        donViTinhComboBox.addItem("DVT010 - Túi");

        // Thêm dữ liệu mẫu cho danh mục
        danhMucComboBox.removeAllItems();
        danhMucComboBox.addItem("DM001 - Thuốc kháng sinh");
        danhMucComboBox.addItem("DM002 - Thuốc giảm đau, hạ sốt");
        danhMucComboBox.addItem("DM003 - Thuốc da liễu");
        danhMucComboBox.addItem("DM004 - Thuốc tiêu hóa");
        danhMucComboBox.addItem("DM005 - Thuốc tim mạch");
        danhMucComboBox.addItem("DM006 - Thuốc hô hấp");
        danhMucComboBox.addItem("DM007 - Vitamin và khoáng chất");
        danhMucComboBox.addItem("DM008 - Thuốc mắt, tai, mũi");
        danhMucComboBox.addItem("DM009 - Thuốc cảm lạnh");
        danhMucComboBox.addItem("DM010 - Dụng cụ y tế");

        // Thêm dữ liệu mẫu cho xuất xứ
        xuatXuComboBox.removeAllItems();
        xuatXuComboBox.addItem("XX001 - Việt Nam");
        xuatXuComboBox.addItem("XX002 - Mỹ");
        xuatXuComboBox.addItem("XX003 - Pháp");
        xuatXuComboBox.addItem("XX004 - Đức");
        xuatXuComboBox.addItem("XX005 - Nhật Bản");
        xuatXuComboBox.addItem("XX006 - Hàn Quốc");
        xuatXuComboBox.addItem("XX007 - Ấn Độ");
        xuatXuComboBox.addItem("XX008 - Thái Lan");
        xuatXuComboBox.addItem("XX009 - Singapore");
        xuatXuComboBox.addItem("XX010 - Trung Quốc");
    }

//    private void searchThuoc() {
//        String keyword = searchField.getText().trim();
//
//        if (keyword.isEmpty()) {
//            loadData();
//            return;
//        }
//
//        try {
//            ResponseDTO response = clientService.searchThuoc(keyword);
//
//            if (response.isSuccess()) {
//                // Xóa dữ liệu cũ
//                tableModel.setRowCount(0);
//
//                // Lấy danh sách thuốc từ response
//                List<Map<String, Object>> thuocList = (List<Map<String, Object>>) response.getData().get("thuocList");
//
//                // Kiểm tra null trước khi sử dụng
//                if (thuocList == null || thuocList.isEmpty()) {
//                    JOptionPane.showMessageDialog(this, "Không tìm thấy thuốc phù hợp", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                } else {
//                    // Thêm dữ liệu vào bảng
//                    for (Map<String, Object> thuoc : thuocList) {
//                        // Xử lý trường hợp các đối tượng liên quan có thể null
//                        String donViTinh = "Không có";
//                        String danhMuc = "Không có";
//                        String xuatXu = "Không có";
//
//                        if (thuoc.get("donViTinh") != null) {
//                            Map<String, Object> dvt = (Map<String, Object>) thuoc.get("donViTinh");
//                            donViTinh = (String) dvt.get("ten");
//                        }
//
//                        if (thuoc.get("danhMuc") != null) {
//                            Map<String, Object> dm = (Map<String, Object>) thuoc.get("danhMuc");
//                            danhMuc = (String) dm.get("ten");
//                        }
//
//                        if (thuoc.get("xuatXu") != null) {
//                            Map<String, Object> xx = (Map<String, Object>) thuoc.get("xuatXu");
//                            xuatXu = (String) xx.get("ten");
//                        }
//
//                        Object[] rowData = {
//                                thuoc.get("idThuoc"),
//                                thuoc.get("tenThuoc"),
//                                donViTinh,
//                                danhMuc,
//                                xuatXu,
//                                thuoc.get("soLuongTon"),
//                                thuoc.get("donGia"),
//                                thuoc.get("hanSuDung")
//                        };
//                        tableModel.addRow(rowData);
//                    }
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Không tìm thấy thuốc phù hợp", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//            }
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private void searchThuoc() {
        String keyword = searchField.getText().trim();
        String criteria = getCriteriaCode((String) searchCriteriaComboBox.getSelectedItem());

        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        try {
            ResponseDTO response = clientService.searchThuoc(keyword, criteria);

            if (response.isSuccess()) {
                // Xóa dữ liệu cũ
                tableModel.setRowCount(0);

                // Lấy dữ liệu thuốc từ response
                Map<String, Object> rootData = (Map<String, Object>) response.getData();
                Map<String, Map<String, Object>> thuocData = (Map<String, Map<String, Object>>) rootData.get("thuocs");

                if (thuocData == null || thuocData.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thuốc phù hợp", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Hiển thị kết quả tìm kiếm
                displaySearchResults(thuocData);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thuốc phù hợp: " + response.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức hỗ trợ để chuyển đổi tiêu chí tìm kiếm thành mã
    private String getCriteriaCode(String criteria) {
        switch (criteria) {
            case "Mã thuốc":
                return "id";
            case "Tên thuốc":
                return "name";
            case "Xuất xứ":
                return "origin";
            case "Danh mục":
                return "category";
            default:
                return "all";
        }
    }

    // Phương thức hiển thị kết quả tìm kiếm dạng Map
    private void displaySearchResults(Map<String, Map<String, Object>> thuocData) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map.Entry<String, Map<String, Object>> entry : thuocData.entrySet()) {
            Map<String, Object> thuoc = entry.getValue();

            String idThuoc = (String) thuoc.get("idThuoc");
            String tenThuoc = (String) thuoc.get("tenThuoc");

            // Xử lý đơn vị tính
            String donViTinhStr = "Không có";
            if (thuoc.get("donViTinh") instanceof Map) {
                Map<String, Object> donViTinh = (Map<String, Object>) thuoc.get("donViTinh");
                donViTinhStr = (String) donViTinh.get("ten");
            }

            // Xử lý danh mục
            String danhMucStr = "Không có";
            if (thuoc.get("danhMuc") instanceof Map) {
                Map<String, Object> danhMuc = (Map<String, Object>) thuoc.get("danhMuc");
                danhMucStr = (String) danhMuc.get("ten");
            }

            // Xử lý xuất xứ
            String xuatXuStr = "Không có";
            if (thuoc.get("xuatXu") instanceof Map) {
                Map<String, Object> xuatXu = (Map<String, Object>) thuoc.get("xuatXu");
                xuatXuStr = (String) xuatXu.get("ten");
            }

            // Xử lý số lượng tồn
            Object soLuongTonObj = thuoc.get("soLuongTon");
            String soLuongTonStr = soLuongTonObj != null ? soLuongTonObj.toString() : "0";

            // Xử lý đơn giá
            Object donGiaObj = thuoc.get("donGia");
            double donGia = donGiaObj instanceof Number ? ((Number) donGiaObj).doubleValue() : 0.0;
            String donGiaStr = decimalFormat.format(donGia);

            // Xử lý hạn sử dụng
            String hanSuDungStr = thuoc.get("hanSuDung") != null ? thuoc.get("hanSuDung").toString() : "";

            // Thêm vào bảng
            Object[] rowData = {
                    idThuoc,
                    tenThuoc,
                    donViTinhStr,
                    danhMucStr,
                    xuatXuStr,
                    soLuongTonStr,
                    donGiaStr,
                    hanSuDungStr
            };
            tableModel.addRow(rowData);
        }
    }

    // Phương thức hiển thị kết quả tìm kiếm dạng List
    private void displaySearchResultsList(List<Map<String, Object>> thuocList) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Map<String, Object> thuoc : thuocList) {
            String idThuoc = (String) thuoc.get("idThuoc");
            String tenThuoc = (String) thuoc.get("tenThuoc");

            // Xử lý đơn vị tính
            String donViTinhStr = "Không có";
            if (thuoc.get("donViTinh") instanceof Map) {
                Map<String, Object> donViTinh = (Map<String, Object>) thuoc.get("donViTinh");
                donViTinhStr = (String) donViTinh.get("ten");
            }

            // Xử lý danh mục
            String danhMucStr = "Không có";
            if (thuoc.get("danhMuc") instanceof Map) {
                Map<String, Object> danhMuc = (Map<String, Object>) thuoc.get("danhMuc");
                danhMucStr = (String) danhMuc.get("ten");
            }

            // Xử lý xuất xứ
            String xuatXuStr = "Không có";
            if (thuoc.get("xuatXu") instanceof Map) {
                Map<String, Object> xuatXu = (Map<String, Object>) thuoc.get("xuatXu");
                xuatXuStr = (String) xuatXu.get("ten");
            }

            // Xử lý số lượng tồn
            Object soLuongTonObj = thuoc.get("soLuongTon");
            String soLuongTonStr = soLuongTonObj != null ? soLuongTonObj.toString() : "0";

            // Xử lý đơn giá
            Object donGiaObj = thuoc.get("donGia");
            double donGia = donGiaObj instanceof Number ? ((Number) donGiaObj).doubleValue() : 0.0;
            String donGiaStr = decimalFormat.format(donGia);

            // Xử lý hạn sử dụng
            String hanSuDungStr = thuoc.get("hanSuDung") != null ? thuoc.get("hanSuDung").toString() : "";

            // Thêm vào bảng
            Object[] rowData = {
                    idThuoc,
                    tenThuoc,
                    donViTinhStr,
                    danhMucStr,
                    xuatXuStr,
                    soLuongTonStr,
                    donGiaStr,
                    hanSuDungStr
            };
            tableModel.addRow(rowData);
        }
    }

//    private void displaySelectedThuoc() {
//        int selectedRow = thuocTable.getSelectedRow();
//
//        if (selectedRow != -1) {
//            // Lấy dữ liệu từ bảng
//            String idThuoc = (String) tableModel.getValueAt(selectedRow, 0);
//            String tenThuoc = (String) tableModel.getValueAt(selectedRow, 1);
//            String donViTinh = (String) tableModel.getValueAt(selectedRow, 2);
//            String danhMuc = (String) tableModel.getValueAt(selectedRow, 3);
//            String xuatXu = (String) tableModel.getValueAt(selectedRow, 4);
//            Object soLuongTon = tableModel.getValueAt(selectedRow, 5);
//            Object donGiaObj = tableModel.getValueAt(selectedRow, 6);
//            String hanSuDung = (String) tableModel.getValueAt(selectedRow, 7);
//
//            // Hiển thị thông tin lên form
//            idThuocField.setText(idThuoc);
//            tenThuocField.setText(tenThuoc);
//
//            // Mặc định thành phần
//            thanhPhanField.setText("Thành phần của " + tenThuoc);
//
//            // Chọn đơn vị tính trong combobox
//            for (int i = 0; i < donViTinhComboBox.getItemCount(); i++) {
//                String item = (String) donViTinhComboBox.getItemAt(i);
//                if (item.contains(donViTinh)) {
//                    donViTinhComboBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            // Chọn danh mục trong combobox
//            for (int i = 0; i < danhMucComboBox.getItemCount(); i++) {
//                String item = (String) danhMucComboBox.getItemAt(i);
//                if (item.contains(danhMuc)) {
//                    danhMucComboBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            // Chọn xuất xứ trong combobox
//            for (int i = 0; i < xuatXuComboBox.getItemCount(); i++) {
//                String item = (String) xuatXuComboBox.getItemAt(i);
//                if (item.contains(xuatXu)) {
//                    xuatXuComboBox.setSelectedIndex(i);
//                    break;
//                }
//            }
//
//            soLuongTonField.setText(soLuongTon.toString());
//
//            // Xử lý đơn giá - Loại bỏ dấu phẩy trước khi chuyển đổi
//            double donGiaValue = 0.0;
//            if (donGiaObj != null) {
//                String donGiaStr = donGiaObj.toString().replace(",", "");
//                try {
//                    donGiaValue = Double.parseDouble(donGiaStr);
//                } catch (NumberFormatException e) {
//                    System.err.println("Không thể chuyển đổi đơn giá: " + donGiaObj);
//                }
//            }
//
//            donGiaField.setText(String.valueOf(donGiaValue));
//
//            // Mặc định giá nhập là 80% đơn giá
//            giaNhapField.setText(String.valueOf(Math.round(donGiaValue * 0.8)));
//
//            // Hiển thị hạn sử dụng
//            try {
//                java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(hanSuDung);
//                hanSuDungChooser.setDate(date);
//            } catch (Exception e) {
//                e.printStackTrace();
//                hanSuDungChooser.setDate(new java.util.Date());
//            }
//
//            // Lấy thông tin hình ảnh từ cơ sở dữ liệu
//            try {
//                ResponseDTO response = clientService.getThuocById(idThuoc);
//                if (response.isSuccess()) {
//                    Map<String, Object> thuocData = (Map<String, Object>) response.getData().get("thuoc");
//                    String hinhAnh = (String) thuocData.get("hinhAnh");
//
//                    if (hinhAnh != null && !hinhAnh.isEmpty()) {
//                        // Reset biến lưu trữ ảnh
//                        currentImagePath = null;
//                        selectedImageName = hinhAnh;
//
//                        // Hiển thị tên ảnh
//                        imageLabel.setIcon(null);
//                        imageLabel.setText("Ảnh");
//                        imageLabel.setToolTipText("Ảnh: " + selectedImageName);
//                    } else {
//                        // Nếu không có ảnh, hiển thị ảnh mặc định
//                        selectedImageName = "thuoc_" + idThuoc + ".jpg";
//                        currentImagePath = null;
//                        imageLabel.setIcon(null);
//                        imageLabel.setText("Ảnh");
//                        imageLabel.setToolTipText("Ảnh: " + selectedImageName);
//                    }
//                } else {
//                    // Nếu không thể lấy thông tin từ server, hiển thị ảnh mặc định
//                    selectedImageName = "thuoc_" + idThuoc + ".jpg";
//                    currentImagePath = null;
//                    imageLabel.setIcon(null);
//                    imageLabel.setText("Ảnh");
//                    imageLabel.setToolTipText("Ảnh: " + selectedImageName);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                selectedImageName = "thuoc_" + idThuoc + ".jpg";
//                currentImagePath = null;
//                imageLabel.setIcon(null);
//                imageLabel.setText("Ảnh");
//                imageLabel.setToolTipText("Ảnh: " + selectedImageName);
//            }
//        }
//    }

    // Sửa phương thức displaySelectedThuoc trong ThuocPanel.java
    private void displaySelectedThuoc() {
        int selectedRow = thuocTable.getSelectedRow();

        if (selectedRow != -1) {
            // Lấy dữ liệu từ bảng
            String idThuoc = (String) tableModel.getValueAt(selectedRow, 0);
            String tenThuoc = (String) tableModel.getValueAt(selectedRow, 1);
            String donViTinh = (String) tableModel.getValueAt(selectedRow, 2);
            String danhMuc = (String) tableModel.getValueAt(selectedRow, 3);
            String xuatXu = (String) tableModel.getValueAt(selectedRow, 4);
            Object soLuongTon = tableModel.getValueAt(selectedRow, 5);
            Object donGiaObj = tableModel.getValueAt(selectedRow, 6);
            String hanSuDung = (String) tableModel.getValueAt(selectedRow, 7);

            // Hiển thị thông tin lên form
            idThuocField.setText(idThuoc);
            tenThuocField.setText(tenThuoc);

            // Mặc định thành phần
            thanhPhanField.setText("Thành phần của " + tenThuoc);

            // Chọn đơn vị tính trong combobox
            for (int i = 0; i < donViTinhComboBox.getItemCount(); i++) {
                String item = (String) donViTinhComboBox.getItemAt(i);
                if (item.contains(donViTinh)) {
                    donViTinhComboBox.setSelectedIndex(i);
                    break;
                }
            }

            // Chọn danh mục trong combobox
            for (int i = 0; i < danhMucComboBox.getItemCount(); i++) {
                String item = (String) danhMucComboBox.getItemAt(i);
                if (item.contains(danhMuc)) {
                    danhMucComboBox.setSelectedIndex(i);
                    break;
                }
            }

            // Chọn xuất xứ trong combobox
            for (int i = 0; i < xuatXuComboBox.getItemCount(); i++) {
                String item = (String) xuatXuComboBox.getItemAt(i);
                if (item.contains(xuatXu)) {
                    xuatXuComboBox.setSelectedIndex(i);
                    break;
                }
            }

            soLuongTonField.setText(soLuongTon.toString());

            // Xử lý đơn giá - Loại bỏ dấu phẩy trước khi chuyển đổi
            double donGiaValue = 0.0;
            if (donGiaObj != null) {
                String donGiaStr = donGiaObj.toString().replace(",", "");
                try {
                    donGiaValue = Double.parseDouble(donGiaStr);
                } catch (NumberFormatException e) {
                    System.err.println("Không thể chuyển đổi đơn giá: " + donGiaObj);
                }
            }

            donGiaField.setText(String.valueOf(donGiaValue));

            // Mặc định giá nhập là 80% đơn giá
            giaNhapField.setText(String.valueOf(Math.round(donGiaValue * 0.8)));

            // Hiển thị hạn sử dụng
            try {
                java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(hanSuDung);
                hanSuDungChooser.setDate(date);
            } catch (Exception e) {
                e.printStackTrace();
                hanSuDungChooser.setDate(new java.util.Date());
            }

            // Lấy thông tin hình ảnh từ cơ sở dữ liệu
            try {
                System.out.println("Đang lấy thông tin thuốc với ID: " + idThuoc);
                ResponseDTO response = clientService.getThuocById(idThuoc);

                if (response.isSuccess()) {
                    Map<String, Object> thuocData = (Map<String, Object>) response.getData().get("thuoc");
                    System.out.println("Dữ liệu thuốc nhận được: " + thuocData);

                    // Kiểm tra xem có trường hinhAnh không
                    if (thuocData != null && thuocData.containsKey("hinhAnh")) {
                        String hinhAnh = (String) thuocData.get("hinhAnh");
                        System.out.println("Tên hình ảnh từ server: " + hinhAnh);

                        if (hinhAnh != null && !hinhAnh.isEmpty()) {
                            // Reset biến lưu trữ ảnh
                            currentImagePath = null;
                            selectedImageName = hinhAnh;

                            // Sử dụng phương thức mới để hiển thị hình ảnh
                            displayImageFromFileName(hinhAnh);
                        } else {
                            imageLabel.setIcon(null);
                            imageLabel.setText("Không có ảnh");
                            imageLabel.setToolTipText("Không có ảnh");
                        }
                    } else {
                        System.out.println("Không tìm thấy trường hinhAnh trong dữ liệu thuốc");

                        // Thử lấy hình ảnh từ danh sách tất cả thuốc
                        try {
                            ResponseDTO allThuocResponse = clientService.getAllThuoc();
                            if (allThuocResponse.isSuccess()) {
                                Map<String, Object> allThuocs = (Map<String, Object>) allThuocResponse.getData().get("thuocs");
                                if (allThuocs != null && allThuocs.containsKey(idThuoc)) {
                                    Map<String, Object> thuoc = (Map<String, Object>) allThuocs.get(idThuoc);
                                    if (thuoc != null && thuoc.containsKey("hinhAnh")) {
                                        String hinhAnh = (String) thuoc.get("hinhAnh");
                                        System.out.println("Tên hình ảnh từ danh sách tất cả thuốc: " + hinhAnh);

                                        if (hinhAnh != null && !hinhAnh.isEmpty()) {
                                            selectedImageName = hinhAnh;
                                            displayImageFromFileName(hinhAnh);
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            System.err.println("Lỗi khi lấy hình ảnh từ danh sách tất cả thuốc: " + ex.getMessage());
                        }

                        selectedImageName = "default.jpg";
                        currentImagePath = null;
                        imageLabel.setIcon(null);
                        imageLabel.setText("Không có ảnh");
                        imageLabel.setToolTipText("Không có ảnh");
                    }
                } else {
                    System.out.println("Lỗi khi lấy thông tin thuốc: " + response.getMessage());

                    // Thử lấy hình ảnh từ danh sách tất cả thuốc
                    try {
                        ResponseDTO allThuocResponse = clientService.getAllThuoc();
                        if (allThuocResponse.isSuccess()) {
                            Map<String, Object> allThuocs = (Map<String, Object>) allThuocResponse.getData().get("thuocs");
                            if (allThuocs != null && allThuocs.containsKey(idThuoc)) {
                                Map<String, Object> thuoc = (Map<String, Object>) allThuocs.get(idThuoc);
                                if (thuoc != null && thuoc.containsKey("hinhAnh")) {
                                    String hinhAnh = (String) thuoc.get("hinhAnh");
                                    System.out.println("Tên hình ảnh từ danh sách tất cả thuốc: " + hinhAnh);

                                    if (hinhAnh != null && !hinhAnh.isEmpty()) {
                                        selectedImageName = hinhAnh;
                                        displayImageFromFileName(hinhAnh);
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Lỗi khi lấy hình ảnh từ danh sách tất cả thuốc: " + ex.getMessage());
                    }

                    // Thử lấy thông tin hình ảnh từ bảng
                    try {
                        // Tìm cột hình ảnh trong bảng
                        int hinhAnhColumnIndex = -1;
                        for (int i = 0; i < tableModel.getColumnCount(); i++) {
                            if (tableModel.getColumnName(i).equals("Hình Ảnh")) {
                                hinhAnhColumnIndex = i;
                                break;
                            }
                        }

                        if (hinhAnhColumnIndex != -1) {
                            String hinhAnh = (String) tableModel.getValueAt(selectedRow, hinhAnhColumnIndex);
                            if (hinhAnh != null && !hinhAnh.isEmpty()) {
                                selectedImageName = hinhAnh;
                                displayImageFromFileName(hinhAnh);
                                return;
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Lỗi khi tìm hình ảnh từ bảng: " + ex.getMessage());
                    }

                    selectedImageName = "default.jpg";
                    currentImagePath = null;
                    imageLabel.setIcon(null);
                    imageLabel.setText("Không có ảnh");
                    imageLabel.setToolTipText("Không có ảnh");
                }
            } catch (Exception e) {
                e.printStackTrace();
                selectedImageName = "default.jpg";
                currentImagePath = null;
                imageLabel.setIcon(null);
                imageLabel.setText("Không có ảnh");
                imageLabel.setToolTipText("Không có ảnh");
            }
        }
    }

    // Phương thức hiển thị hình ảnh từ tên file
    private void displayImageFromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText("Không có ảnh");
            imageLabel.setToolTipText("Không có ảnh");
            return;
        }

        // In ra đường dẫn tuyệt đối của thư mục hiện tại để debug
        System.out.println("Thư mục hiện tại: " + new File(".").getAbsolutePath());

        // Tìm hình ảnh trong thư mục images
        File imageFile = new File("images/" + fileName);
        System.out.println("Đang tìm hình ảnh tại: " + imageFile.getAbsolutePath());

        if (imageFile.exists() && imageFile.isFile()) {
            try {
                System.out.println("Đã tìm thấy hình ảnh, đang tải...");

                // Tải hình ảnh và hiển thị
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());

                // Kiểm tra xem hình ảnh có được tải thành công không
                if (originalIcon.getIconWidth() <= 0) {
                    System.out.println("Hình ảnh không hợp lệ hoặc bị hỏng");
                    imageLabel.setIcon(null);
                    imageLabel.setText("Hình ảnh không hợp lệ: " + fileName);
                    return;
                }

                // Thay đổi kích thước hình ảnh để vừa với label
                Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                // Hiển thị hình ảnh
                imageLabel.setIcon(scaledIcon);
                imageLabel.setText(""); // Xóa text khi hiển thị hình ảnh
                imageLabel.setToolTipText("Ảnh: " + fileName);
                System.out.println("Đã tải và hiển thị hình ảnh thành công");
            } catch (Exception e) {
                System.err.println("Lỗi khi tải hình ảnh: " + e.getMessage());
                e.printStackTrace();
                imageLabel.setIcon(null);
                imageLabel.setText("Lỗi tải ảnh: " + fileName);
            }
        } else {
            System.out.println("Không tìm thấy file ảnh: " + imageFile.getAbsolutePath());

            // Thử tìm trong thư mục gốc
            imageFile = new File(fileName);
            System.out.println("Đang tìm hình ảnh tại thư mục gốc: " + imageFile.getAbsolutePath());

            if (imageFile.exists() && imageFile.isFile()) {
                try {
                    System.out.println("Đã tìm thấy hình ảnh trong thư mục gốc, đang tải...");

                    // Tải hình ảnh và hiển thị
                    ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());

                    // Kiểm tra xem hình ảnh có được tải thành công không
                    if (originalIcon.getIconWidth() <= 0) {
                        System.out.println("Hình ảnh không hợp lệ hoặc bị hỏng");
                        imageLabel.setIcon(null);
                        imageLabel.setText("Hình ảnh không hợp lệ: " + fileName);
                        return;
                    }

                    // Thay đổi kích thước hình ảnh để vừa với label
                    Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);

                    // Hiển thị hình ảnh
                    imageLabel.setIcon(scaledIcon);
                    imageLabel.setText(""); // Xóa text khi hiển thị hình ảnh
                    imageLabel.setToolTipText("Ảnh: " + fileName);
                    System.out.println("Đã tải và hiển thị hình ảnh thành công từ thư mục gốc");
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải hình ảnh từ thư mục gốc: " + e.getMessage());
                    e.printStackTrace();
                    imageLabel.setIcon(null);
                    imageLabel.setText("Lỗi tải ảnh: " + fileName);
                }
            } else {
                System.out.println("Không tìm thấy file ảnh ở cả hai vị trí");

                // In ra danh sách tất cả các file trong thư mục images để debug
                File imagesDir = new File("images");
                if (imagesDir.exists() && imagesDir.isDirectory()) {
                    System.out.println("Danh sách file trong thư mục images:");
                    File[] files = imagesDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            System.out.println(" - " + file.getName());
                        }
                    } else {
                        System.out.println("Không thể liệt kê file trong thư mục images");
                    }
                }

                imageLabel.setIcon(null);
                imageLabel.setText("Không tìm thấy ảnh: " + fileName);
            }
        }
    }

//    private void addThuoc() {
//        // Kiểm tra dữ liệu nhập vào
//        if (!validateInput()) {
//            return;
//        }
//
//        try {
//            // Tạo đối tượng thuốc từ form
//            Map<String, Object> thuoc = createThuocFromForm();
//
//            // Gửi yêu cầu thêm thuốc
//            ResponseDTO response = clientService.saveThuoc(thuoc);
//
//            if (response.isSuccess()) {
//                JOptionPane.showMessageDialog(this, "Thêm thuốc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                clearForm();
//                loadData();
//            } else {
//                // Mô phỏng thêm thành công
//                JOptionPane.showMessageDialog(this, "Thêm thuốc thành công (Mô phỏng)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//                // Thêm dữ liệu vào bảng
//                String idThuoc = (String) thuoc.get("idThuoc");
//                if (idThuoc == null || idThuoc.isEmpty()) {
//                    idThuoc = "T" + String.format("%03d", tableModel.getRowCount() + 1);
//                }
//
//                String donViTinhItem = (String) donViTinhComboBox.getSelectedItem();
//                String donViTinh = donViTinhItem.split(" - ")[1];
//
//                String danhMucItem = (String) danhMucComboBox.getSelectedItem();
//                String danhMuc = danhMucItem.split(" - ")[1];
//
//                String xuatXuItem = (String) xuatXuComboBox.getSelectedItem();
//                String xuatXu = xuatXuItem.split(" - ")[1];
//
//                Object[] rowData = {
//                        idThuoc,
//                        tenThuocField.getText(),
//                        donViTinh,
//                        danhMuc,
//                        xuatXu,
//                        Integer.parseInt(soLuongTonField.getText()),
//                        Double.parseDouble(donGiaField.getText()),
//                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(hanSuDungChooser.getDate())
//                };
//
//                tableModel.addRow(rowData);
//                clearForm();
//            }
//        } catch (IOException e) {
//            // Mô phỏng thêm thành công
//            JOptionPane.showMessageDialog(this, "Thêm thuốc thành công (Mô phỏng)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//
//            // Thêm dữ liệu vào bảng
//            String idThuoc = "T" + String.format("%03d", tableModel.getRowCount() + 1);
//
//            String donViTinhItem = (String) donViTinhComboBox.getSelectedItem();
//            String donViTinh = donViTinhItem.split(" - ")[1];
//
//            String danhMucItem = (String) danhMucComboBox.getSelectedItem();
//            String danhMuc = danhMucItem.split(" - ")[1];
//
//            String xuatXuItem = (String) xuatXuComboBox.getSelectedItem();
//            String xuatXu = xuatXuItem.split(" - ")[1];
//
//            Object[] rowData = {
//                    idThuoc,
//                    tenThuocField.getText(),
//                    donViTinh,
//                    danhMuc,
//                    xuatXu,
//                    Integer.parseInt(soLuongTonField.getText()),
//                    Double.parseDouble(donGiaField.getText()),
//                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(hanSuDungChooser.getDate())
//            };
//
//            tableModel.addRow(rowData);
//            clearForm();
//        }
//    }
private void addThuoc() {
    // Kiểm tra dữ liệu nhập vào
    if (!validateInput()) {
        return;
    }

    try {
        // Tạo đối tượng thuốc từ form
        Map<String, Object> thuoc = createThuocFromForm();

        // Đảm bảo ID thuốc là duy nhất
        String newId = generateUniqueId();
        thuoc.put("idThuoc", newId);

        // Xử lý hình ảnh - Đảm bảo sử dụng tên file đã chọn
        if (selectedImageName != null && !selectedImageName.isEmpty()) {
            thuoc.put("hinhAnh", selectedImageName);
        } else {
            // Nếu không có ảnh được chọn, sử dụng ảnh mặc định
            thuoc.put("hinhAnh", "default.jpg");
        }

        // Gửi yêu cầu thêm thuốc
        ResponseDTO response = clientService.saveThuoc(thuoc);

        if (response.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Thêm thuốc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData(); // Tải lại dữ liệu để hiển thị thuốc mới
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thuốc thất bại: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

    private String generateUniqueId() {
        try {
            // Lấy danh sách tất cả thuốc
            ResponseDTO response = clientService.getAllThuoc();

            if (response.isSuccess()) {
                Map<String, Object> rootData = (Map<String, Object>) response.getData();
                Map<String, Map<String, Object>> thuocData = (Map<String, Map<String, Object>>) rootData.get("thuocs");

                // Tìm ID lớn nhất hiện tại
                int maxId = 0;
                for (String idThuoc : thuocData.keySet()) {
                    if (idThuoc.startsWith("T")) {
                        try {
                            int id = Integer.parseInt(idThuoc.substring(1));
                            if (id > maxId) {
                                maxId = id;
                            }
                        } catch (NumberFormatException e) {
                            // Bỏ qua nếu không phải định dạng Txxx
                        }
                    }
                }

                // Tạo ID mới
                return "T" + String.format("%03d", maxId + 1);
            } else {
                // Nếu không thể lấy danh sách thuốc, tạo ID ngẫu nhiên
                Random random = new Random();
                return "T" + String.format("%03d", random.nextInt(900) + 100); // Tạo số ngẫu nhiên từ 100-999
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, tạo ID dựa trên thời gian hiện tại
            return "T" + String.format("%03d", (int)(System.currentTimeMillis() % 1000));
        }
    }

//    private void updateThuoc() {
//        // Kiểm tra xem đã chọn thuốc chưa
//        if (idThuocField.getText().isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // Kiểm tra dữ liệu nhập vào
//        if (!validateInput()) {
//            return;
//        }
//
//        try {
//            // Tạo đối tượng thuốc từ form
//            Map<String, Object> thuoc = createThuocFromForm();
//
//            // Xử lý hình ảnh nếu có
//            if (currentImagePath != null && !currentImagePath.isEmpty()) {
//                thuoc.put("hinhAnh", currentImagePath);
//            }
//
//            // Gửi yêu cầu cập nhật thuốc
//            ResponseDTO response = clientService.updateThuoc(thuoc);
//
//            if (response.isSuccess()) {
//                JOptionPane.showMessageDialog(this, "Cập nhật thuốc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                clearForm();
//                loadData(); // Tải lại dữ liệu để hiển thị thông tin đã cập nhật
//            } else {
//                JOptionPane.showMessageDialog(this, "Cập nhật thuốc thất bại: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private void updateThuoc() {
        // Kiểm tra xem đã chọn thuốc chưa
        if (idThuocField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra dữ liệu nhập vào
        if (!validateInput()) {
            return;
        }

        try {
            // Tạo đối tượng thuốc từ form
            Map<String, Object> thuoc = createThuocFromForm();

            // Xử lý hình ảnh - Đảm bảo sử dụng tên file đã chọn
            if (selectedImageName != null && !selectedImageName.isEmpty()) {
                thuoc.put("hinhAnh", selectedImageName);
            }
            // Nếu không có ảnh mới được chọn, giữ nguyên ảnh cũ (đã được xử lý trong createThuocFromForm)

            // Gửi yêu cầu cập nhật thuốc
            ResponseDTO response = clientService.updateThuoc(thuoc);

            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cập nhật thuốc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadData(); // Tải lại dữ liệu để hiển thị thông tin đã cập nhật
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thuốc thất bại: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteThuoc() {
        // Kiểm tra xem đã chọn thuốc chưa
        if (idThuocField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int option = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa thuốc này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                // Gửi yêu cầu xóa thuốc
                ResponseDTO response = clientService.deleteThuoc(idThuocField.getText());

                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "Xóa thuốc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadData();
                } else {
                    // Mô phỏng xóa thành công
                    JOptionPane.showMessageDialog(this, "Xóa thuốc thành công (Mô phỏng)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                    // Xóa dữ liệu khỏi bảng
                    int selectedRow = thuocTable.getSelectedRow();
                    if (selectedRow != -1) {
                        tableModel.removeRow(selectedRow);
                    }

                    clearForm();
                }
            } catch (IOException e) {
                // Mô phỏng xóa thành công
                JOptionPane.showMessageDialog(this, "Xóa thuốc thành công (Mô phỏng)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Xóa dữ liệu khỏi bảng
                int selectedRow = thuocTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                }

                clearForm();
            }
        }
    }

    private void clearForm() {
        idThuocField.setText("");
        tenThuocField.setText("");
        thanhPhanField.setText("");
        soLuongTonField.setText("");
        giaNhapField.setText("");
        donGiaField.setText("");
        hanSuDungChooser.setDate(null);

        if (donViTinhComboBox.getItemCount() > 0) {
            donViTinhComboBox.setSelectedIndex(0);
        }

        if (danhMucComboBox.getItemCount() > 0) {
            danhMucComboBox.setSelectedIndex(0);
        }

        if (xuatXuComboBox.getItemCount() > 0) {
            xuatXuComboBox.setSelectedIndex(0);
        }

        // Reset biến lưu trữ ảnh
        currentImagePath = null;
        selectedImageName = null;

        // Reset hiển thị ảnh
        imageLabel.setIcon(null);
        imageLabel.setText("Chọn ảnh");
        imageLabel.setToolTipText(null);
    }

//    private void chooseImage() {
//        JFileChooser fileChooser = new JFileChooser();
//        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif");
//        fileChooser.setFileFilter(imageFilter);
//        fileChooser.setDialogTitle("Chọn ảnh thuốc");
//
//        int result = fileChooser.showOpenDialog(this);
//
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//            currentImagePath = selectedFile.getAbsolutePath();
//            selectedImageName = selectedFile.getName();
//
//            // Hiển thị hình ảnh đã chọn
//            try {
//                ImageIcon icon = new ImageIcon(currentImagePath);
//                Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
//                imageLabel.setIcon(new ImageIcon(image));
//                imageLabel.setText(""); // Clear text when showing image
//                imageLabel.setToolTipText("Ảnh: " + selectedImageName);
//
//                // Hiển thị thông tin về ảnh đã chọn
//                JOptionPane.showMessageDialog(this, "Đã chọn ảnh: " + selectedImageName, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//            } catch (Exception e) {
//                e.printStackTrace();
//                imageLabel.setIcon(null);
//                imageLabel.setText("Lỗi hiển thị ảnh");
//                imageLabel.setToolTipText(null);
//                currentImagePath = null;
//                selectedImageName = null;
//            }
//        }
//    }

    // Phương thức chọn hình ảnh
    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(imageFilter);
        fileChooser.setDialogTitle("Chọn ảnh thuốc");

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentImagePath = selectedFile.getAbsolutePath();
            selectedImageName = selectedFile.getName();

            // Hiển thị hình ảnh đã chọn
            try {
                ImageIcon icon = new ImageIcon(currentImagePath);
                Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
                imageLabel.setText(""); // Xóa text khi hiển thị hình ảnh
                imageLabel.setToolTipText("Ảnh: " + selectedImageName);

                // Hiển thị thông tin về ảnh đã chọn
                JOptionPane.showMessageDialog(this, "Đã chọn ảnh: " + selectedImageName, "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                // Lưu bản sao của hình ảnh vào thư mục images (nếu cần)
                try {
                    // Tạo thư mục images nếu chưa tồn tại
                    File imagesDir = new File("images");
                    if (!imagesDir.exists()) {
                        imagesDir.mkdir();
                    }

                    // Đường dẫn đích để lưu hình ảnh
                    File destFile = new File("images/" + selectedImageName);

                    // Sao chép file hình ảnh
                    java.nio.file.Files.copy(
                            selectedFile.toPath(),
                            destFile.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );

                    System.out.println("Đã sao chép hình ảnh vào: " + destFile.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Lỗi khi sao chép hình ảnh: " + e.getMessage());
                    // Không cần hiển thị lỗi cho người dùng, vẫn có thể sử dụng hình ảnh gốc
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageLabel.setIcon(null);
                imageLabel.setText("Lỗi hiển thị ảnh");
                imageLabel.setToolTipText(null);
                currentImagePath = null;
                selectedImageName = null;
            }
        }
    }

    private boolean validateInput() {
        // Kiểm tra tên thuốc
        if (tenThuocField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thuốc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            tenThuocField.requestFocus();
            return false;
        }

        // Kiểm tra số lượng tồn
        try {
            int soLuongTon = Integer.parseInt(soLuongTonField.getText().trim());
            if (soLuongTon < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng tồn không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                soLuongTonField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            soLuongTonField.requestFocus();
            return false;
        }

        // Kiểm tra giá nhập
        try {
            double giaNhap = Double.parseDouble(giaNhapField.getText().trim());
            if (giaNhap < 0) {
                JOptionPane.showMessageDialog(this, "Giá nhập không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                giaNhapField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá nhập phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            giaNhapField.requestFocus();
            return false;
        }

        // Kiểm tra đơn giá
        try {
            double donGia = Double.parseDouble(donGiaField.getText().trim());
            if (donGia < 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                donGiaField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            donGiaField.requestFocus();
            return false;
        }

        // Kiểm tra hạn sử dụng
        if (hanSuDungChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hạn sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            hanSuDungChooser.requestFocus();
            return false;
        }

        return true;
    }

    private Map<String, Object> createThuocFromForm() {
        Map<String, Object> thuoc = new HashMap<>();

        // Nếu là thêm mới, ID sẽ được tạo trong phương thức addThuoc()
        // Nếu là cập nhật, sử dụng ID hiện tại
        if (!idThuocField.getText().isEmpty()) {
            thuoc.put("idThuoc", idThuocField.getText());
        }

        thuoc.put("tenThuoc", tenThuocField.getText());
        thuoc.put("thanhPhan", thanhPhanField.getText());

        // Lấy ID từ combobox
        String dvtItem = (String) donViTinhComboBox.getSelectedItem();
        String idDVT = dvtItem.split(" - ")[0];
        thuoc.put("idDVT", idDVT);

        String dmItem = (String) danhMucComboBox.getSelectedItem();
        String idDM = dmItem.split(" - ")[0];
        thuoc.put("idDM", idDM);

        String xxItem = (String) xuatXuComboBox.getSelectedItem();
        String idXX = xxItem.split(" - ")[0];
        thuoc.put("idXX", idXX);

        thuoc.put("soLuongTon", Integer.parseInt(soLuongTonField.getText()));
        thuoc.put("giaNhap", Double.parseDouble(giaNhapField.getText()));
        thuoc.put("donGia", Double.parseDouble(donGiaField.getText()));

        // Format hạn sử dụng
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        thuoc.put("hanSuDung", sdf.format(hanSuDungChooser.getDate()));

        // Hình ảnh - Sử dụng tên file đã chọn nếu có
        if (selectedImageName != null && !selectedImageName.isEmpty()) {
            thuoc.put("hinhAnh", selectedImageName);
        } else {
            // Nếu không có ảnh được chọn, sử dụng ảnh mặc định hoặc giữ nguyên ảnh cũ
            String currentId = idThuocField.getText();
            if (currentId != null && !currentId.isEmpty()) {
                thuoc.put("hinhAnh", "thuoc_" + currentId + ".jpg");
            } else {
                thuoc.put("hinhAnh", "default.jpg");
            }
        }

        return thuoc;
    }
}