package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {
    private String idKM;
    private String loai;
    private Double mucGiamGia;
    private String hangMuc;
    private String idThuoc;
    private String trangThai;

    // Thông tin liên kết
    private Thuoc thuoc;
}