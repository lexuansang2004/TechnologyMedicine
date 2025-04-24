//package iuh.fit.config;
//
//import java.util.Properties;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//public class EmailConfig {
//
//    private static final Logger LOGGER = Logger.getLogger(EmailConfig.class.getName());
//
//    private static final String HOST = "smtp.gmail.com";
//    private static final String PORT = "587";
////    private static final String USERNAME = System.getenv("EMAIL_USERNAME"); // Lấy từ biến môi trường
////    private static final String PASSWORD = System.getenv("EMAIL_PASSWORD"); // Lấy từ biến môi trường
//// Thay thế bằng email và mật khẩu cụ thể của bạn
//private static final String USERNAME = "lexuansang14042004@gmail.com"; // Thay thế bằng email của bạn
//    private static final String PASSWORD = "truongthanhH@520"; // Thay thế bằng mật khẩu ứng dụng của bạn
//
//    private static EmailConfig instance;
//    private final Session session;
//
//    private EmailConfig() {
//        if (USERNAME == null || PASSWORD == null) {
//            LOGGER.severe("Email credentials are not set in environment variables.");
//            LOGGER.severe("Please set EMAIL_USERNAME and EMAIL_PASSWORD environment variables.");
//            throw new RuntimeException("Missing EMAIL_USERNAME or EMAIL_PASSWORD environment variables");
//        }
//
//        LOGGER.info("Initializing email configuration with username: " + USERNAME);
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", HOST);
//        props.put("mail.smtp.port", PORT);
//        props.put("mail.debug", "true"); // Enable debug mode
//        props.put("mail.smtp.ssl.trust", HOST); // Trust the host
//
//        session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(USERNAME, PASSWORD);
//            }
//        });
//
//        LOGGER.info("Email configuration initialized");
//    }
//
//    public static synchronized EmailConfig getInstance() {
//        if (instance == null) {
//            try {
//                instance = new EmailConfig();
//            } catch (Exception e) {
//                LOGGER.severe("Failed to initialize EmailConfig: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        return instance;
//    }
//
//    public boolean sendEmail(String to, String subject, String content) {
//        try {
//            LOGGER.info("Attempting to send email to: " + to);
//            LOGGER.info("Subject: " + subject);
//
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(USERNAME));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//            message.setSubject(subject);
//            message.setContent(content, "text/html; charset=utf-8");
//
//            LOGGER.info("Email configured, attempting to send...");
//            Transport.send(message);
//            LOGGER.info("Email sent successfully to: " + to);
//            return true;
//        } catch (MessagingException e) {
//            LOGGER.log(Level.SEVERE, "Failed to send email to: " + to, e);
//            e.printStackTrace();
//            return false;
//        }
//    }
//}

package iuh.fit.config;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;

public class EmailConfig {

    private static final Logger LOGGER = Logger.getLogger(EmailConfig.class.getName());

    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    // Thông tin đăng nhập email của bạn
    private static final String USERNAME = "lexuansang14042004@gmail.com";
    private static final String PASSWORD = "dhksiywburnerdxq";

    private static EmailConfig instance;
    private final Session session;

    private EmailConfig() {
        LOGGER.info("Initializing email configuration with username: " + USERNAME);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.debug", "true"); // Enable debug mode
        props.put("mail.smtp.ssl.trust", HOST); // Trust the host

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        LOGGER.info("Email configuration initialized");
    }

    public static synchronized EmailConfig getInstance() {
        if (instance == null) {
            try {
                instance = new EmailConfig();
            } catch (Exception e) {
                LOGGER.severe("Failed to initialize EmailConfig: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return instance;
    }

    public boolean sendEmail(String to, String subject, String content) {
        try {
            LOGGER.info("Attempting to send email to: " + to);
            LOGGER.info("Subject: " + subject);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            LOGGER.info("Email configured, attempting to send...");
            Transport.send(message);
            LOGGER.info("Email sent successfully to: " + to);
            return true;
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to: " + to, e);
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức kiểm tra kết nối
    public boolean testConnection() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(HOST, USERNAME, PASSWORD);
            transport.close();
            LOGGER.info("Email connection test successful");
            return true;
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Email connection test failed: " + e.getMessage(), e);
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức hiển thị kết quả kiểm tra kết nối
    public void displayConnectionTest() {
        try {
            boolean connected = testConnection();
            if (connected) {
                JOptionPane.showMessageDialog(null, "Kết nối email thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Kết nối email thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi kiểm tra kết nối email: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static boolean sendTestEmail(String to) {
        String subject = "Email thử nghiệm từ ứng dụng quản lý thuốc";
        String content = "<html><body>"
                + "<h2>Xin chào!</h2>"
                + "<p>Đây là email thử nghiệm từ ứng dụng quản lý thuốc.</p>"
                + "<p>Nếu bạn nhận được email này, cấu hình email đã hoạt động chính xác.</p>"
                + "<p>Thời gian gửi: " + java.time.LocalDateTime.now() + "</p>"
                + "</body></html>";

        return getInstance().sendEmail(to, subject, content);
    }

    // Phương thức hiển thị kết quả gửi email thử nghiệm
    public static void displayTestEmailResult(String to) {
        boolean sent = sendTestEmail(to);
        if (sent) {
            JOptionPane.showMessageDialog(null, "Đã gửi email thử nghiệm thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Gửi email thử nghiệm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức main để kiểm tra
    public static void main(String[] args) {
        // Kiểm tra kết nối
        EmailConfig.getInstance().displayConnectionTest();

        // Gửi email thử nghiệm
        displayTestEmailResult("evoltevolution4@gmail.com"); // Thay bằng email thực để kiểm tra
    }
}
