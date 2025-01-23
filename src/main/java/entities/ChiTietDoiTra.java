package entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(ChiTietDoiTraId.class)
public class ChiTietDoiTra {
    @Id
    @ManyToOne
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    private int soLuong;
    private double donGia;

    @Id
    @ManyToOne
    @JoinColumn(name = "maDoiTra")
    private DoiTra doiTra;
}
