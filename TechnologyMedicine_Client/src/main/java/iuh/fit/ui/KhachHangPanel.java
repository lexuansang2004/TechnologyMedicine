// ========================
// 1. KhachHangPanel.java (JPanel)
// ========================
package iuh.fit.ui;

import iuh.fit.entity.KhachHang;
import iuh.fit.service.KhachHangService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class KhachHangPanel extends JPanel {
    private final KhachHangService service = new KhachHangService();

    private JTextField txtHoTen, txtSdt, txtHangMuc, txtTongChiTieu, txtSearch;
    private JRadioButton radNam, radNu;
    private ButtonGroup genderGroup;
    private JDateChooser dateChooser;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public KhachHangPanel() {
        setLayout(new BorderLayout());

        // ===== Form Input =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtHoTen = new JTextField(25);
        txtSdt = new JTextField(15);
        txtHangMuc = new JTextField(15);
        txtTongChiTieu = new JTextField(15);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        radNam = new JRadioButton("Nam");
        radNu = new JRadioButton("Nữ");
        genderGroup = new ButtonGroup();
        genderGroup.add(radNam);
        genderGroup.add(radNu);

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1; formPanel.add(txtHoTen, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 3; formPanel.add(txtSdt, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        genderPanel.add(radNam);
        genderPanel.add(radNu);
        formPanel.add(genderPanel, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Ngày tham gia:"), gbc);
        gbc.gridx = 3; formPanel.add(dateChooser, gbc);

        y++;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Hạng mục:"), gbc);
        gbc.gridx = 1; formPanel.add(txtHangMuc, gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Tổng chi tiêu:"), gbc);
        gbc.gridx = 3; formPanel.add(txtTongChiTieu, gbc);

        // ===== Search Panel =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        // ===== Table =====
        String[] columns = {"Mã KH", "Họ tên", "SĐT", "Giới tính", "Ngày tham gia", "Hạng mục", "Tổng chi tiêu"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

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

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<KhachHang> list = service.findAll();
        for (KhachHang kh : list) {
            tableModel.addRow(new Object[]{kh.getIdKH(), kh.getHoTen(), kh.getSdt(), kh.getGioiTinh(), kh.getNgayThamGia(), kh.getHangMuc(), kh.getTongChiTieu()});
        }
        clearForm();
    }

    private void clearForm() {
        txtHoTen.setText("");
        txtSdt.setText("");
        genderGroup.clearSelection();
        dateChooser.setDate(null);
        txtHangMuc.setText("");
        txtTongChiTieu.setText("");
        txtSearch.setText("");
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
            txtSdt.setText(tableModel.getValueAt(row, 2).toString());
            String gioiTinh = tableModel.getValueAt(row, 3).toString();
            if (gioiTinh.equalsIgnoreCase("Nam")) radNam.setSelected(true);
            else radNu.setSelected(true);
            dateChooser.setDate(java.sql.Date.valueOf(tableModel.getValueAt(row, 4).toString()));
            txtHangMuc.setText(tableModel.getValueAt(row, 5).toString());
            txtTongChiTieu.setText(tableModel.getValueAt(row, 6).toString());
        }
    }

    private void handleAdd(ActionEvent e) {
        String id = service.generateNewId();
        String gioiTinh = radNam.isSelected() ? "Nam" : radNu.isSelected() ? "Nữ" : "";
        try {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày tham gia");
                return;
            }
            LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            KhachHang kh = new KhachHang(
                    id,
                    txtHoTen.getText().trim(),
                    txtSdt.getText().trim(),
                    null,
                    gioiTinh,
                    localDate,
                    txtHangMuc.getText().trim(),
                    Double.parseDouble(txtTongChiTieu.getText().trim())
            );

            if (service.save(kh)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
        }
    }

    private void handleUpdate(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa");
            return;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        String gioiTinh = radNam.isSelected() ? "Nam" : radNu.isSelected() ? "Nữ" : "";
        try {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày tham gia");
                return;
            }
            LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            KhachHang kh = new KhachHang(
                    id,
                    txtHoTen.getText().trim(),
                    txtSdt.getText().trim(),
                    null,
                    gioiTinh,
                    localDate,
                    txtHangMuc.getText().trim(),
                    Double.parseDouble(txtTongChiTieu.getText().trim())
            );
            if (service.update(kh)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
        }
    }

    private void handleDelete(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa");
            return;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
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
        List<KhachHang> list = service.findByKeyword(keyword);
        for (KhachHang kh : list) {
            tableModel.addRow(new Object[]{kh.getIdKH(), kh.getHoTen(), kh.getSdt(), kh.getGioiTinh(), kh.getNgayThamGia(), kh.getHangMuc(), kh.getTongChiTieu()});
        }
    }
}
