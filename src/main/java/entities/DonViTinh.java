package entities;

public enum DonViTinh {
    VIEN("Viên"),
    HOP("Hộp"),
    CHAI("Chai");

    private String tenDonViTinh;

    DonViTinh(String tenDonViTinh) {
        this.tenDonViTinh = tenDonViTinh;
    }

    @Override
    public String toString() {
        return tenDonViTinh;
    }
}