package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Thuoc {
    private String idThuoc;
    private String tenThuoc;
    private String hinhAnh;
    private String thanhPhan;
    private String idDVT;
    private String idDM;
    private String idXX;
    private Integer soLuongTon;
    private Double giaNhap;
    private Double donGia;
    private LocalDate hanSuDung;

    // Thông tin liên kết
    private DonViTinh donViTinh;
    private DanhMuc danhMuc;
    private XuatXu xuatXu;
}