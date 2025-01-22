package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
public class TaiKhoan {
    @Id
    @OneToOne
    @JoinColumn(name = "taiKhoan")
    private NhanVien nhanVien;
    @Column(nullable = false)
    private String password;
}
