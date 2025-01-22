package entities;

public enum ChucVu {
    QUANLY("Người quản lý"),
    NHANVIENBANHANG("Nhân viên bán hàng");

    private String tenChucVu;

    ChucVu(String tenChucVu) {
        this.tenChucVu = tenChucVu;
    }

    @Override
    public String toString() {
        return tenChucVu;
    }
}
