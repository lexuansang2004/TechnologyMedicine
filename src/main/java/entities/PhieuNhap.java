package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PhieuNhap {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private LocalDateTime ngayTaoPN;

    @ManyToOne
    @JoinColumn(name = "maThuoc")
    private Thuoc thuoc;

    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "maNCC")
    private NhaCungCap nhaCungCap;

    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietPhieuNhap> chiTietPhieuNhapList;
}

