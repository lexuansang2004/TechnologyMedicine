package entities;

import lombok.Data;
import java.io.Serializable;

@Data
public class ChiTietHoaDonId implements Serializable {
    private String hoaDon;
    private String thuoc;
}
