package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DoiTra {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private LocalDateTime ngayDoiTra;

    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;

    @OneToMany(mappedBy = "doiTra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDoiTra> chiTietDoiTraList;
}
