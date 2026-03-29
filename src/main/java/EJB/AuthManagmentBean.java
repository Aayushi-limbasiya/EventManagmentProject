/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.AuthTokens;
import Entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.Stateless;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import javax.crypto.SecretKey;

/**
 *
 * @author OS
 */
@Stateless
public class AuthManagmentBean implements AuthManagmentBeanLocal {
    
      @PersistenceContext(unitName = "jpu")
    EntityManager em;
      
    private static final String JWT_SECRET = "EventManagementSystemSecretKey2024SuperSecure";
    private static final long JWT_EXPIRY_HOURS = 24; // Token valid for 24 hours

    // ── EMAIL CONFIG 
    
    private static final String EMAIL_FROM     = "your_email@gmail.com";
    private static final String EMAIL_PASSWORD = "your_app_password";   // Gmail App Password
    private static final String SMTP_HOST      = "smtp.gmail.com";
    private static final String SMTP_PORT      = "587";

    // ── APP CONFIG
    private static final String APP_BASE_URL   = "http://localhost:8080/EventManagement";

    @Override
    public String login(String email, String password) {
        
        // Step 1: Find user
        TypedQuery<Users> q = em.createNamedQuery("Users.login", Users.class);
        q.setParameter("email", email);
        q.setParameter("password", password);
        Collection<Users> result = q.getResultList();

        if (result.isEmpty()) {
            throw new RuntimeException("Invalid email or password.");
        }

        Users user = result.iterator().next();

        // Step 2: Check account status
        if ("Blocked".equals(user.getVerifiedStatus())) {
            throw new RuntimeException("Your account has been blocked. Contact admin.");
        }
        if ("Pending".equals(user.getVerifiedStatus())) {
            throw new RuntimeException("Your account is pending verification.");
        }

        // Step 3: Generate JWT token
        String jwtToken = generateJwtToken(user);

        // Step 4: Save token to DB
        saveTokenToDB(user, jwtToken);

        // Step 5: Send login success email (async-safe via System.out fallback)
        try {
            sendLoginSuccessEmail(user);
        } catch (Exception e) {
            // Do not fail login if email fails
            System.out.println("Login email failed for: " + user.getEmail() + " | " + e.getMessage());
        }

        // Step 6: Return token
        return jwtToken;
    }

    @Override
    public boolean validateToken(String token) {
         try {
            // Step 1: Parse and validate JWT signature + expiry
            Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            // Step 2: Check DB — not revoked
            TypedQuery<AuthTokens> q =
                em.createNamedQuery("AuthTokens.findByToken", AuthTokens.class);
            q.setParameter("token", token);
            Collection<AuthTokens> result = q.getResultList();

            return !result.isEmpty();

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Users getUserFromToken(String token) {
         try {
            Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

            int userId = Integer.parseInt(claims.getSubject());
            return em.find(Users.class, userId);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getRoleFromToken(String token) {
          try {
            Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

            return claims.get("role", String.class);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void logout(String token) {
          TypedQuery<AuthTokens> q =
            em.createNamedQuery("AuthTokens.findByToken", AuthTokens.class);
        q.setParameter("token", token);
        Collection<AuthTokens> result = q.getResultList();

        if (!result.isEmpty()) {
            AuthTokens authToken = result.iterator().next();
            authToken.setIsRevoked(true);
            em.merge(authToken);
        }
    }

    @Override
    public void logoutAllDevices(int userId) {
          em.createNamedQuery("AuthTokens.revokeAllByUser")
            .setParameter("userId", userId)
            .executeUpdate();
        System.out.println("All tokens revoked for user ID: " + userId);
    }

    @Override
    public void forgotPassword(String email) {
        // Step 1: Find user by email
        TypedQuery<Users> q = em.createNamedQuery("Users.findByEmail", Users.class);
        q.setParameter("email", email);
        Collection<Users> result = q.getResultList();

        if (result.isEmpty()) {
            // Do not reveal if email exists for security
            System.out.println("Forgot password requested for non-existent email: " + email);
            return;
        }

        Users user = result.iterator().next();

        // Step 2: Generate secure reset token
        String resetToken = UUID.randomUUID().toString();

        // Step 3: Set expiry = 1 hour from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        Date expiry = cal.getTime();

        // Step 4: Save reset token to users table
        // Uses native query since reset_token is a new column added via SQL
        em.createNativeQuery(
            "UPDATE users SET reset_token = ?, reset_token_expiry = ? WHERE user_id = ?")
            .setParameter(1, resetToken)
            .setParameter(2, expiry)
            .setParameter(3, user.getUserId())
            .executeUpdate();

        // Step 5: Send reset email
        String resetLink = APP_BASE_URL + "/reset-password?token=" + resetToken;
        String subject = "Password Reset Request - Event Management System";
        String body = "Dear " + user.getName() + ",\n\n"
            + "We received a request to reset your password.\n\n"
            + "Click the link below to reset your password:\n"
            + resetLink + "\n\n"
            + "This link will expire in 1 hour.\n\n"
            + "If you did not request this, please ignore this email.\n\n"
            + "Regards,\nEvent Management Team";

        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void resetPassword(String resetToken, String newPassword) {
        // Step 1: Find user by reset token
        Collection<Users> result = em.createNativeQuery(
            "SELECT * FROM users WHERE reset_token = ?", Users.class)
            .setParameter(1, resetToken)
            .getResultList();

        if (result.isEmpty()) {
            throw new RuntimeException("Invalid or expired password reset token.");
        }

        Users user = result.iterator().next();

        // Step 2: Check token expiry
        Collection<Object[]> expiryResult = em.createNativeQuery(
            "SELECT reset_token_expiry FROM users WHERE user_id = ?")
            .setParameter(1, user.getUserId())
            .getResultList();

        if (!expiryResult.isEmpty()) {
            Object expiryObj = expiryResult.iterator().next();
            if (expiryObj instanceof Date) {
                Date expiry = (Date) expiryObj;
                if (new Date().after(expiry)) {
                    throw new RuntimeException("Password reset token has expired. Please request a new one.");
                }
            }
        }

        // Step 3: Set new password
        user.setPassword(newPassword);
        em.merge(user);

        // Step 4: Clear reset token from DB
        em.createNativeQuery(
            "UPDATE users SET reset_token = NULL, reset_token_expiry = NULL WHERE user_id = ?")
            .setParameter(1, user.getUserId())
            .executeUpdate();

        // Send confirmation email
        String subject = "Password Reset Successful - Event Management System";
        String body = "Dear " + user.getName() + ",\n\n"
            + "Your password has been reset successfully.\n\n"
            + "If you did not make this change, contact us immediately.\n\n"
            + "Regards,\nEvent Management Team";
        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
         Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            System.out.println("Email sending FAILED to: " + toEmail + " | Error: " + e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }

    @Override
    public void sendLoginSuccessEmail(Users user) {
         String roleName = (user.getRoleId() != null) ? user.getRoleId().getRoleName() : "User";
        String subject = "Login Successful - Event Management System";
        String body = "Dear " + user.getName() + ",\n\n"
            + "You have successfully logged in to the Event Management System.\n\n"
            + "Account Details:\n"
            + "  Name  : " + user.getName() + "\n"
            + "  Email : " + user.getEmail() + "\n"
            + "  Role  : " + roleName + "\n"
            + "  Time  : " + new Date().toString() + "\n\n"
            + "If this was not you, please reset your password immediately.\n\n"
            + "Regards,\nEvent Management Team";

        sendEmail(user.getEmail(), subject, body);
    }
    
    private String generateJwtToken(Users user) {
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

        String roleName = (user.getRoleId() != null)
            ? user.getRoleId().getRoleName() : "Participant";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, (int) JWT_EXPIRY_HOURS);
        Date expiry = cal.getTime();

        return Jwts.builder()
            .setSubject(String.valueOf(user.getUserId()))
            .claim("email", user.getEmail())
            .claim("role", roleName)
            .claim("name", user.getName())
            .setIssuedAt(new Date())
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
    

    /**
     * Save JWT token to auth_tokens table in DB
     */
    private void saveTokenToDB(Users user, String jwtToken) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, (int) JWT_EXPIRY_HOURS);
        Date expiry = cal.getTime();

        AuthTokens authToken = new AuthTokens();
        authToken.setUserId(user);
        authToken.setToken(jwtToken);
        authToken.setCreatedAt(new Date());
        authToken.setExpiresAt(expiry);
        authToken.setIsRevoked(false);
        em.persist(authToken);
    }
}

