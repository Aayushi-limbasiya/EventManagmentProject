/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Users;
import jakarta.ejb.Local;

/**
 *
 * @author OS
 */
@Local
public interface AuthManagmentBeanLocal {
       String login(String email, String password);

    /**
     * Validate a JWT token
     * Returns true if token is valid + not expired + not revoked in DB
     */
    boolean validateToken(String token);

    /**
     * Get Users object from JWT token (for authorization)
     * Returns null if token invalid
     */
    Users getUserFromToken(String token);

    /**
     * Get role name from JWT token
     * Returns: Admin / Organizer / Participant
     */
    String getRoleFromToken(String token);

    /**
     * Logout — revokes specific token in DB
     */
    void logout(String token);

    /**
     * Logout from ALL devices — revokes all tokens for a user
     */
    void logoutAllDevices(int userId);

    /**
     * Forgot Password Step 1
     * Generates reset token, saves to users table, sends reset link email
     */
    void forgotPassword(String email);

    /**
     * Forgot Password Step 2
     * Validates reset token + expiry, sets new password, clears reset token
     */
    void resetPassword(String resetToken, String newPassword);

    /**
     * Send real email using JavaMail via GlassFish Mail Session
     * Used internally by all other methods
     */
    void sendEmail(String toEmail, String subject, String body);

    /**
     * Send login success notification email
     * Called internally after successful login
     */
    void sendLoginSuccessEmail(Users user);
}
