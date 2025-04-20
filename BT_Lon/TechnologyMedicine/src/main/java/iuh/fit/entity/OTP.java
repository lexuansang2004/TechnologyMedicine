package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
    private String idOTP;
    private String idKH;
    private String maOTP;
    private LocalDateTime thoiGianTao;
    private LocalDateTime thoiGianHetHan;
    private String trangThai;

    // Thông tin liên kết
    private KhachHang khachHang;
}