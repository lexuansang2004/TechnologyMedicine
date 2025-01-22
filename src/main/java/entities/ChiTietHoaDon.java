package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
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
}
