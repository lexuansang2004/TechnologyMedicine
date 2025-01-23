package entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(ChiTietPhieuNhapId.class)
public class ChiTietPhieuNhap {
    @Id
    @ManyToOne
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    private int soLuong;
    private double donGia;

    @Id
    @ManyToOne
    @JoinColumn(name = "maPhieuNhap")
    private PhieuNhap phieuNhap;

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thuoc=" + thuoc +
                ", phieuNhap=" + phieuNhap +
                '}';
    }
}
