package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NhanVien {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String hoTen;
    private String sdt;
    private String email;
    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;
    private LocalDate ngaySinh;
    private Date ngayVaoLam;
    @Enumerated(EnumType.STRING)
    private ChucVu chucVu;

    @OneToOne(mappedBy = "nhanVien", cascade = CascadeType.ALL)
    private TaiKhoan taiKhoan;

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDon> dsHoaDon;
}
