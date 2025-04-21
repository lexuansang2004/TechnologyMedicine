// ========================
// 1. NhaCungCapPanel.java (JPanel)
// ========================
package iuh.fit.ui;

import iuh.fit.entity.NhaCungCap;
import iuh.fit.service.NhaCungCapService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class NhaCungCapPanel extends JPanel {
    private final NhaCungCapService service = new NhaCungCapService();

    private JTextField txtTen, txtSdt, txtDiaChi, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public NhaCungCapPanel() {
        setLayout(new BorderLayout());

        // ===== Form Input (Căn chỉnh lại khoảng cách) =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhà cung cấp"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTen = new JTextField(20);
        txtSdt = new JTextField(20);
        txtDiaChi = new JTextField(40);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Tên NCC:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(txtTen, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        formPanel.add(txtSdt, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1;
        formPanel.add(txtDiaChi, gbc);
        gbc.gridwidth = 1;

        // ===== Search Panel =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tên NCC:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // ===== Combine Top Panels =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // ===== Table =====
        String[] columns = {"Mã NCC", "Tên NCC", "SĐT", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        // Căn giữa cột SĐT
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== Button Panel =====
        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // ===== Add to Main Layout =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Events =====
        btnAdd.addActionListener(this::handleAdd);
        btnUpdate.addActionListener(this::handleUpdate);
        btnDelete.addActionListener(this::handleDelete);
        btnSearch.addActionListener(this::handleSearch);
        btnRefresh.addActionListener(e -> loadData());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<NhaCungCap> list = service.findAll();
        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[]{ncc.getIdNCC(), ncc.getTenNCC(), ncc.getSdt(), ncc.getDiaChi()});
        }
        clearForm();
    }

    private void clearForm() {
        txtTen.setText("");
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtSearch.setText("");
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtTen.setText(tableModel.getValueAt(row, 1).toString());
            txtSdt.setText(tableModel.getValueAt(row, 2).toString());
            txtDiaChi.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void handleAdd(ActionEvent e) {
        String id = service.generateNewId();
        String ten = txtTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        NhaCungCap ncc = new NhaCungCap(id, ten, sdt, diaChi);
        if (service.save(ncc)) {
            JOptionPane.showMessageDialog(this, "Thêm thành công");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại");
        }
    }

    private void handleUpdate(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để sửa");
            return;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        NhaCungCap ncc = new NhaCungCap(id, txtTen.getText().trim(), txtSdt.getText().trim(), txtDiaChi.getText().trim());
        if (service.update(ncc)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
        }
    }

    private void handleDelete(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xóa");
            return;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa nhà cung cấp này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.delete(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại");
            }
        }
    }

    private void handleSearch(ActionEvent e) {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        List<NhaCungCap> list = service.findByName(keyword);
        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[]{ncc.getIdNCC(), ncc.getTenNCC(), ncc.getSdt(), ncc.getDiaChi()});
        }
    }
}
