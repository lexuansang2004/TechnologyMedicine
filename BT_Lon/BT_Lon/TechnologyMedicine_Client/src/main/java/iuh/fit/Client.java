package iuh.fit;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import iuh.fit.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Client {

    public static void main(String[] args) {
        // Đặt look and feel
        try {
            FlatLightLaf.setup();
            FlatArcIJTheme.setup();
            UIManager.setLookAndFeel(new FlatArcIJTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đặt font mặc định
        setUIFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 13));

        // Hiển thị màn hình đăng nhập
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }

    /**
     * Đặt font mặc định cho tất cả các thành phần UI
     *
     * @param f Font mới
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}