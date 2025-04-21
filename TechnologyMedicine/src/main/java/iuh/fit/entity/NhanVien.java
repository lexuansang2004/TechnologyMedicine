package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    private String idNV;
    private String hoTen;
    private String sdt;
    private String gioiTinh;
    private Integer namSinh;
    private LocalDate ngayVaoLam;
}