package iuh.fit.entity;

import iuh.fit.util.PasswordUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaiKhoan {
    private String idTK;
    private String username;
    private String password;
    private String idNV;
    private String idVT;

    // Thông tin liên kết
    private NhanVien nhanVien;
    private VaiTro vaiTro;

    public TaiKhoan(String idTK, String username, String password, String idNV, String idVT) {
        this.idTK = idTK;
        this.username = username;
        setPassword(password);
        this.idNV = idNV;
        this.idVT = idVT;
    }

    // Setter mã hóa mật khẩu trước khi lưu
    public void setPassword(String password) {
        // Nếu mật khẩu chưa được mã hóa, băm và lưu lại
        if (password != null && !password.startsWith("$2a$")) {
            this.password = PasswordUtil.hashPassword(password);
        } else {
            this.password = password;
        }
    }

    // Phương thức kiểm tra mật khẩu
    public boolean checkPassword(String plainPassword) {
        // So sánh mật khẩu nhập vào với mật khẩu đã băm trong cơ sở dữ liệu
        return PasswordUtil.checkPassword(plainPassword, this.password);
    }

    public String getPassword() {
        return password;
    }
}

