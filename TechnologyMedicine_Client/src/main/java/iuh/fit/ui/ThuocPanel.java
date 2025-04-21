package iuh.fit.ui;

import iuh.fit.entity.*;
import iuh.fit.service.*;

import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ThuocPanel extends JPanel {
    private final ThuocService service = new ThuocService();
    private final DonViTinhService donViTinhService = new DonViTinhService();
    private final DanhMucService danhMucService = new DanhMucService();
    private final XuatXuService xuatXuService = new XuatXuService();

    private JTextField txtId, txtTen, txtThanhPhan, txtSoLuong, txtGiaNhap, txtDonGia, txtSearch;
    private JComboBox<String> cbDVT, cbDM, cbXX, cbFilterDVT, cbFilterDM, cbFilterXX;
    private JDateChooser dateChooser;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public ThuocPanel() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thuốc"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtId = new JTextField(); txtId.setEditable(false);
        txtTen = new JTextField(20);
        txtThanhPhan = new JTextField(20);
        txtSoLuong = new JTextField(20);
        txtGiaNhap = new JTextField(20);
        txtDonGia = new JTextField(20);
        cbDVT = new JComboBox<>();
        cbDM = new JComboBox<>();
        cbXX = new JComboBox<>();
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setHorizontalAlignment(SwingConstants.CENTER);

        String[] labels = {"Tên thuốc:", "Thành phần:", "Số lượng tồn:", "Giá nhập:", "Đơn giá:", "Đơn vị tính:", "Danh mục:", "Xuất xứ:", "Hạn sử dụng:"};
        Component[] inputs = {txtTen, txtThanhPhan, txtSoLuong, txtGiaNhap, txtDonGia, cbDVT, cbDM, cbXX, dateChooser};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = (i % 2 == 0) ? 0 : 2;
            gbc.gridy = i / 2;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = (i % 2 == 0) ? 1 : 3;
            formPanel.add(inputs[i], gbc);
        }

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm kiếm");
        cbFilterDVT = new JComboBox<>();
        cbFilterDM = new JComboBox<>();
        cbFilterXX = new JComboBox<>();
        searchPanel.add(new JLabel("Tên thuốc:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(new JLabel("Đơn vị tính:"));
        searchPanel.add(cbFilterDVT);
        searchPanel.add(new JLabel("Danh mục:"));
        searchPanel.add(cbFilterDM);
        searchPanel.add(new JLabel("Xuất xứ:"));
        searchPanel.add(cbFilterXX);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        String[] columns = {"Mã thuốc", "Tên", "Thành phần", "SL tồn", "Giá nhập", "Đơn giá", "HSD", "Đơn vị tính", "Danh mục", "Xuất xứ"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{3, 4, 5, 6}) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(this::handleAdd);
        btnUpdate.addActionListener(this::handleUpdate);
        btnDelete.addActionListener(this::handleDelete);
        btnSearch.addActionListener(this::handleSearch);
        btnRefresh.addActionListener(e -> loadData());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        loadComboBoxData();
        loadData();
    }

    private void loadComboBoxData() {
        cbDVT.removeAllItems(); cbDM.removeAllItems(); cbXX.removeAllItems();
        cbFilterDVT.removeAllItems(); cbFilterDM.removeAllItems(); cbFilterXX.removeAllItems();
        cbFilterDVT.addItem("Tất cả"); cbFilterDM.addItem("Tất cả"); cbFilterXX.addItem("Tất cả");

        donViTinhService.findAll().forEach(dvt -> {
            cbDVT.addItem(dvt.getTen());
            cbFilterDVT.addItem(dvt.getTen());
        });
        danhMucService.findAll().forEach(dm -> {
            cbDM.addItem(dm.getTen());
            cbFilterDM.addItem(dm.getTen());
        });
        xuatXuService.findAll().forEach(xx -> {
            cbXX.addItem(xx.getTen());
            cbFilterXX.addItem(xx.getTen());
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Thuoc> list = service.findAll();
        for (Thuoc t : list) {
            tableModel.addRow(new Object[]{
                    t.getIdThuoc(), t.getTenThuoc(), t.getThanhPhan(),
                    t.getSoLuongTon(), t.getGiaNhap(), t.getDonGia(),
                    t.getHanSuDung(), t.getDonViTinh().getTen(),
                    t.getDanhMuc().getTen(), t.getXuatXu().getTen()
            });
        }
        clearForm();
    }

    private void clearForm() {
        txtId.setText(""); txtTen.setText(""); txtThanhPhan.setText("");
        txtSoLuong.setText(""); txtGiaNhap.setText(""); txtDonGia.setText("");
        txtSearch.setText(""); cbDVT.setSelectedIndex(-1); cbDM.setSelectedIndex(-1);
        cbXX.setSelectedIndex(-1); dateChooser.setDate(null);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String id = tableModel.getValueAt(row, 0).toString();
            Thuoc t = service.findById(id).orElse(null);
            if (t == null) return;
            txtId.setText(t.getIdThuoc()); txtTen.setText(t.getTenThuoc());
            txtThanhPhan.setText(t.getThanhPhan());
            txtSoLuong.setText(String.valueOf(t.getSoLuongTon()));
            txtGiaNhap.setText(String.valueOf(t.getGiaNhap()));
            txtDonGia.setText(String.valueOf(t.getDonGia()));
            dateChooser.setDate(java.sql.Date.valueOf(t.getHanSuDung()));
            cbDVT.setSelectedItem(t.getDonViTinh().getTen());
            cbDM.setSelectedItem(t.getDanhMuc().getTen());
            cbXX.setSelectedItem(t.getXuatXu().getTen());
        }
    }

    private void handleAdd(ActionEvent e) {
        String id = service.generateNewId();
        try {
            LocalDate hsd = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String tenDVT = (String) cbDVT.getSelectedItem();
            String tenDM = (String) cbDM.getSelectedItem();
            String tenXX = (String) cbXX.getSelectedItem();
            DonViTinh dvt = donViTinhService.findByName(tenDVT).orElseThrow();
            DanhMuc dm = danhMucService.findByName(tenDM).orElseThrow();
            XuatXu xx = xuatXuService.findByName(tenXX).orElseThrow();
            Thuoc thuoc = new Thuoc(id, txtTen.getText(), "default.png", txtThanhPhan.getText(),
                    dvt.getIdDVT(), dm.getIdDM(), xx.getIdXX(),
                    Integer.parseInt(txtSoLuong.getText()), Double.parseDouble(txtGiaNhap.getText()),
                    Double.parseDouble(txtDonGia.getText()), hsd, dvt, dm, xx);
            if (service.save(thuoc)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công");
                loadData();
            } else JOptionPane.showMessageDialog(this, "Thêm thất bại");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ hoặc thiếu thông tin liên kết");
        }
    }

    private void handleUpdate(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc để sửa"); return;
        }
        try {
            String id = txtId.getText();
            LocalDate hsd = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String tenDVT = (String) cbDVT.getSelectedItem();
            String tenDM = (String) cbDM.getSelectedItem();
            String tenXX = (String) cbXX.getSelectedItem();
            DonViTinh dvt = donViTinhService.findByName(tenDVT).orElseThrow();
            DanhMuc dm = danhMucService.findByName(tenDM).orElseThrow();
            XuatXu xx = xuatXuService.findByName(tenXX).orElseThrow();
            Thuoc thuoc = new Thuoc(id, txtTen.getText(), "default.png", txtThanhPhan.getText(),
                    dvt.getIdDVT(), dm.getIdDM(), xx.getIdXX(),
                    Integer.parseInt(txtSoLuong.getText()), Double.parseDouble(txtGiaNhap.getText()),
                    Double.parseDouble(txtDonGia.getText()), hsd, dvt, dm, xx);
            if (service.update(thuoc)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công");
                loadData();
            } else JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ hoặc thiếu thông tin liên kết");
        }
    }

    private void handleDelete(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc để xóa"); return;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa thuốc này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (service.delete(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công");
                loadData();
            } else JOptionPane.showMessageDialog(this, "Xóa thất bại");
        }
    }

    private void handleSearch(ActionEvent e) {
        String keyword = txtSearch.getText().trim();
        String filterDVT = (String) cbFilterDVT.getSelectedItem();
        String filterDM = (String) cbFilterDM.getSelectedItem();
        String filterXX = (String) cbFilterXX.getSelectedItem();

        tableModel.setRowCount(0);
        List<Thuoc> list = service.findByName(keyword);
        for (Thuoc t : list) {
            boolean match = (filterDVT.equals("Tất cả") || t.getDonViTinh().getTen().equals(filterDVT)) &&
                    (filterDM.equals("Tất cả") || t.getDanhMuc().getTen().equals(filterDM)) &&
                    (filterXX.equals("Tất cả") || t.getXuatXu().getTen().equals(filterXX));
            if (match) {
                tableModel.addRow(new Object[]{
                        t.getIdThuoc(), t.getTenThuoc(), t.getThanhPhan(), t.getSoLuongTon(),
                        t.getGiaNhap(), t.getDonGia(), t.getHanSuDung(),
                        t.getDonViTinh().getTen(), t.getDanhMuc().getTen(), t.getXuatXu().getTen()
                });
            }
        }
    }
}
