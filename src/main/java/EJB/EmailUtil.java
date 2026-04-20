/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EJB;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * EmailUtil
 * Shared email utility used by all EJB beans
 * Uses same Gmail credentials as AuthManagmentBean
 * Plain Java class — not a session bean
 * Call from any EJB: EmailUtil.sendEmail(to, subject, body)
 */
public class EmailUtil {

    // Same credentials as AuthManagmentBean — keeps everything consistent
    private static final String EMAIL_FROM     = "eventportal57@gmail.com";
    private static final String EMAIL_PASSWORD = "oltu rrxo jbhd tqmy";
    private static final String SMTP_HOST      = "smtp.gmail.com";
    private static final String SMTP_PORT      = "587";
    
    

    /**
     * Send real email via Gmail SMTP
     * Static method — call as EmailUtil.sendEmail(to, subject, body)
     * Does NOT throw exception — logs failure and continues
     * So email failure never breaks business logic
     */
    public static void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("[EMAIL SENT] To: " + toEmail + " | Subject: " + subject);
        } catch (MessagingException e) {
            // Log but do not throw — email failure should not break registration/approval etc.
            System.out.println("[EMAIL FAILED] To: " + toEmail + " | Error: " + e.getMessage());
        }
    }
}
