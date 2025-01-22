package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HoaDon {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private LocalDate ngayTaoHD;
    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;
}
