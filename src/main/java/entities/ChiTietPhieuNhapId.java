package entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChiTietPhieuNhapId implements Serializable {
    private String phieuNhap;
    private String thuoc;
}
