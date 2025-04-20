package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhap {
    private String idPN;
    private LocalDateTime thoiGian;
    private String idNV;
    private String idNCC;
    private Double tongTien;

    // Thông tin liên kết
    private NhanVien nhanVien;
    private NhaCungCap nhaCungCap;
    private List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();
}