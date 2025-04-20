package iuh.fit.config;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailConfig {

    private static final Logger LOGGER = Logger.getLogger(EmailConfig.class.getName());

    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USERNAME = System.getenv("EMAIL_USERNAME"); // Lấy từ biến môi trường
    private static final String PASSWORD = System.getenv("EMAIL_PASSWORD"); // Lấy từ biến môi trường

    private static EmailConfig instance;
    private final Session session;

    private EmailConfig() {
        if (USERNAME == null || PASSWORD == null) {
            LOGGER.severe("Email credentials are not set in environment variables.");
            throw new RuntimeException("Missing EMAIL_USERNAME or EMAIL_PASSWORD environment variables");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

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
            instance = new EmailConfig();
        }
        return instance;
    }

    public boolean sendEmail(String to, String subject, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            LOGGER.info("Email sent successfully to: " + to);
            return true;
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to: " + to, e);
            return false;
        }
    }
}
