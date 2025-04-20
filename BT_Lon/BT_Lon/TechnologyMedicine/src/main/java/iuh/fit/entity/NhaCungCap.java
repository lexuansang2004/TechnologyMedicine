package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCap {
    private String idNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;
}