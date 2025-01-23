package entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NhaCungCap {
    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String tenNCC;
    private String sdt;
    private String diaChi;
    private String email;

    @OneToMany(mappedBy = "nhaCungCap")
    private List<PhieuNhap> dsPhieuNhap;
}
