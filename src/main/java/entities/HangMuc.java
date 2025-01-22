package entities;

public enum HangMuc {
    BAC("Bạc"),
    VANG("Vàng");

    private String tenHangMuc;

    HangMuc(String tenHangMuc) {
        this.tenHangMuc = tenHangMuc;
    }

    @Override
    public String toString() {
        return tenHangMuc;
    }
}
