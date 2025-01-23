package entities;

public enum TrangThai {
    CHUAHOANTHANH("Chưa hoàn thành"),
    HOANTHANH("Hoàn thành");

    private String trangThai;

    TrangThai(String tenChucVu) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return trangThai;
    }
}
