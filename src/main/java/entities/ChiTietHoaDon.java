package entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon {
    @Id
    @ManyToOne
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    private int soLuong;
    private double donGia;

    @Id
    @ManyToOne
    @JoinColumn(name = "maHoaDon")
    private HoaDon hoaDon;

    @Override
    public String toString() {
        return "ChiTietHoaDon{" + "soLuong=" + soLuong + ", donGia=" + donGia + '}';
    }
}
