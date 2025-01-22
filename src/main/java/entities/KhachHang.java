package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KhachHang {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String hoTen;
    private String sdt;
    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;
    private Date ngayThamGia;
    @Enumerated(EnumType.STRING)
    private HangMuc hangMuc;
    private double tongChiTieu;
    @OneToMany(mappedBy = "khachHang")
    private List<HoaDon> dsHoaDon;

}
