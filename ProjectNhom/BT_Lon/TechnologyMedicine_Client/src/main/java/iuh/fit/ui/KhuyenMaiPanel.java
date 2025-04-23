package iuh.fit.ui;

import iuh.fit.dto.RequestDTO;
import iuh.fit.dto.ResponseDTO;
import iuh.fit.service.ClientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhuyenMaiPanel extends JPanel {
    private JTable khuyenMaiTable;
    private DefaultTableModel tableModel;
    private JTextField idKMField, mucGiamGiaField;
    private JComboBox<String> loaiComboBox, trangThaiComboBox, doiTuongComboBox, hangMucComboBox, thuocComboBox;
    private ClientService clientService;

    public KhuyenMaiPanel() throws IOException {
        this.clientService = ClientService.getInstance();
        setLayout(new BorderLayout());

        // Bảng khuyến mãi
        tableModel = new DefaultTableModel(new Object[]{"ID", "Loại", "Mức giảm", "Trạng thái"}, 0);
        khuyenMaiTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(khuyenMaiTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel hiển thị chi tiết
        JPanel detailPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        idKMField = new JTextField(); idKMField.setEditable(false);
        mucGiamGiaField = new JTextField();

        loaiComboBox = new JComboBox<>(new String[]{"Phần trăm", "Số tiền"});
        trangThaiComboBox = new JComboBox<>(new String[]{"Đang áp dụng", "Ngưng áp dụng"});
        doiTuongComboBox = new JComboBox<>(new String[]{"Thuốc", "Hạng mục khách hàng"});
        hangMucComboBox = new JComboBox<>();
        thuocComboBox = new JComboBox<>();

        // Thêm dữ liệu mẫu cho hạng mục
        hangMucComboBox.addItem("HM001 - Khách hàng thường");
        hangMucComboBox.addItem("HM002 - Khách hàng VIP");
        hangMucComboBox.addItem("HM003 - Khách hàng thân thiết");

        detailPanel.add(new JLabel("ID Khuyến mãi:")); detailPanel.add(idKMField);
        detailPanel.add(new JLabel("Loại:")); detailPanel.add(loaiComboBox);
        detailPanel.add(new JLabel("Mức giảm giá:")); detailPanel.add(mucGiamGiaField);
        detailPanel.add(new JLabel("Trạng thái:")); detailPanel.add(trangThaiComboBox);
        detailPanel.add(new JLabel("Đối tượng áp dụng:")); detailPanel.add(doiTuongComboBox);
        detailPanel.add(new JLabel("Hạng mục:")); detailPanel.add(hangMucComboBox);
        detailPanel.add(new JLabel("Thuốc:")); detailPanel.add(thuocComboBox);

        add(detailPanel, BorderLayout.SOUTH);

        // Sự kiện chọn dòng trong bảng
        khuyenMaiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                try {
                    displaySelectedKhuyenMai();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Xử lý chọn đối tượng áp dụng
        doiTuongComboBox.addActionListener(e -> toggleDoiTuongFields());

        // Gọi load dữ liệu
        loadKhuyenMaiData();
        loadThuocData(); // Load dữ liệu cho combobox Thuốc
        toggleDoiTuongFields(); // Cập nhật hiển thị ban đầu
    }

    // Load dữ liệu khuyến mãi vào bảng
    public void loadKhuyenMaiData() {
        try {
            // Gửi yêu cầu lấy danh sách khuyến mãi
            RequestDTO request = new RequestDTO("GET_ALL_KHUYEN_MAI", new HashMap<>());
            ResponseDTO response = clientService.sendRequest(request);

            if (response != null && response.isSuccess()) {
                // Kiểm tra và xử lý danh sách khuyến mãi
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> danhSachKM = (List<Map<String, Object>>) response.getData().get("khuyenMaiList");

                // Kiểm tra null trước khi sử dụng
                if (danhSachKM == null || danhSachKM.isEmpty()) {
                    // Thêm dữ liệu mẫu nếu không có dữ liệu thực
                    addSampleData();
                } else {
                    // Xóa dữ liệu cũ trong bảng
                    tableModel.setRowCount(0);

                    // Thêm dữ liệu mới vào bảng
                    for (Map<String, Object> km : danhSachKM) {
                        tableModel.addRow(new Object[]{
                                km.get("idKM"),
                                km.get("loai"),
                                km.get("mucGiamGia"),
                                km.get("trangThai")
                        });
                    }
                }
            } else {
                // Thêm dữ liệu mẫu nếu không lấy được dữ liệu từ server
                addSampleData();
            }
        } catch (Exception e) {
            // Thêm dữ liệu mẫu nếu có lỗi
            addSampleData();
        }
    }

    // Thêm dữ liệu mẫu vào bảng
    private void addSampleData() {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        // Thêm dữ liệu mẫu
        Object[][] sampleData = {
                {"KM001", "Phần trăm", 10, "Đang áp dụng"},
                {"KM002", "Số tiền", 20000, "Đang áp dụng"},
                {"KM003", "Phần trăm", 15, "Ngưng áp dụng"},
                {"KM004", "Phần trăm", 5, "Đang áp dụng"},
                {"KM005", "Số tiền", 50000, "Ngưng áp dụng"}
        };

        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
    }

    // Hiển thị thông tin khuyến mãi khi chọn dòng
    private void displaySelectedKhuyenMai() throws IOException {
        int selectedRow = khuyenMaiTable.getSelectedRow();
        if (selectedRow == -1) return;

        // Lấy dữ liệu từ bảng
        String idKM = (String) tableModel.getValueAt(selectedRow, 0);
        String loai = (String) tableModel.getValueAt(selectedRow, 1);
        Object mucGiamGia = tableModel.getValueAt(selectedRow, 2);
        String trangThai = (String) tableModel.getValueAt(selectedRow, 3);

        // Hiển thị dữ liệu lên form
        idKMField.setText(idKM);
        loaiComboBox.setSelectedItem(loai);
        mucGiamGiaField.setText(mucGiamGia.toString());
        trangThaiComboBox.setSelectedItem(trangThai);

        // Mặc định đối tượng áp dụng dựa trên ID khuyến mãi
        if (idKM.equals("KM001") || idKM.equals("KM003") || idKM.equals("KM005")) {
            doiTuongComboBox.setSelectedItem("Thuốc");
            thuocComboBox.setSelectedIndex(0);
        } else {
            doiTuongComboBox.setSelectedItem("Hạng mục khách hàng");
            hangMucComboBox.setSelectedIndex(0);
        }

        toggleDoiTuongFields();
    }

    // Tùy biến giao diện theo loại đối tượng áp dụng
    private void toggleDoiTuongFields() {
        String selected = (String) doiTuongComboBox.getSelectedItem();
        if ("Thuốc".equals(selected)) {
            thuocComboBox.setVisible(true);
            hangMucComboBox.setVisible(false);
        } else {
            thuocComboBox.setVisible(false);
            hangMucComboBox.setVisible(true);
        }
    }

    // Load danh sách thuốc cho combobox
    private void loadThuocData() throws IOException {
        // Xóa dữ liệu cũ
        thuocComboBox.removeAllItems();

        // Thêm dữ liệu mẫu
        thuocComboBox.addItem("T001 - Paracetamol");
        thuocComboBox.addItem("T002 - Amoxicillin");
        thuocComboBox.addItem("T003 - Vitamin C");
        thuocComboBox.addItem("T004 - Dầu gió");
        thuocComboBox.addItem("T005 - Thuốc ho");

        // Thử lấy dữ liệu thực từ server
        try {
            ResponseDTO thuocRes = clientService.sendRequest(new RequestDTO("GET_ALL_THUOC", null));
            if (thuocRes.isSuccess()) {
                List<Map<String, Object>> thuocList = (List<Map<String, Object>>) thuocRes.getData().get("thuocList");

                if (thuocList != null && !thuocList.isEmpty()) {
                    thuocComboBox.removeAllItems();
                    for (Map<String, Object> thuoc : thuocList) {
                        thuocComboBox.addItem(thuoc.get("idThuoc") + " - " + thuoc.get("tenThuoc"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Không thể lấy danh sách thuốc từ server, sử dụng dữ liệu mẫu: " + e.getMessage());
        }
    }
}