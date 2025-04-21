package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap {
    private String idPN;
    private String idThuoc;
    private Integer soLuong;
    private Double donGia;

    // Thông tin liên kết
    private Thuoc thuoc;
}