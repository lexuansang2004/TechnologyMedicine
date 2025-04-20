package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    private String idKH;
    private String hoTen;
    private String sdt;
    private String email;
    private String gioiTinh;
    private LocalDate ngayThamGia;
    private String hangMuc;
    private Double tongChiTieu;
}