package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Thuoc {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String tenThuoc;
    private String thanhPhan;

    @Enumerated(EnumType.STRING)
    private DonViTinh donViTinh;
    private double giaThuoc;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;
    private String hinhAnh;

    @OneToMany(mappedBy = "thuoc")
    private List<ChiTietHoaDon> dsChiTietHoaDon;

    @Override
    public String toString() {
        return "Thuoc{" + "id='" + id + '\'' + ", tenThuoc='" + tenThuoc + '\'' + ", thanhPhan='" + thanhPhan + '\'' + ", donViTinh=" + donViTinh + ", giaThuoc=" + giaThuoc + ", ngaySanXuat=" + ngaySanXuat + ", hanSuDung=" + hanSuDung + ", hinhAnh='" + hinhAnh + '\'' + '}';
    }
}
