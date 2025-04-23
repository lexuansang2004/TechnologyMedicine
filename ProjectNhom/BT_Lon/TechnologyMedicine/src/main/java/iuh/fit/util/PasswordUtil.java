package iuh.fit.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Số vòng lặp mã hóa, càng cao càng an toàn nhưng tốn thời gian
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Mã hóa mật khẩu sử dụng BCrypt
     *
     * @param plainPassword Mật khẩu gốc
     * @return Mật khẩu đã mã hóa
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Kiểm tra mật khẩu có khớp với mật khẩu đã mã hóa không
     *
     * @param plainPassword Mật khẩu gốc
     * @param hashedPassword Mật khẩu đã mã hóa
     * @return true nếu mật khẩu khớp, false nếu không
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}