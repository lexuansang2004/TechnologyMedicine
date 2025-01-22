package entities;

public enum GioiTinh {
    NAM("Nam"),
    NU("Nữ"),
    KHAC("Khác");

    private String tenGioiTinh;

    GioiTinh(String tenGioiTinh) {
        this.tenGioiTinh = tenGioiTinh;
    }

    @Override
    public String toString() {
        return tenGioiTinh;
    }
}
