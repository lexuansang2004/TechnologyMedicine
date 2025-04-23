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
public class HoaDon {
    private String idHD;
    private String idKH;
    private String idNV;
    private Double tongTien;
    private LocalDateTime ngayLap;

    // Thông tin liên kết
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private List<ChiTietHoaDon> chiTietList = new ArrayList<>();
    private List<KhuyenMai> khuyenMaiList = new ArrayList<>();
}