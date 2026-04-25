/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.AuthManagmentBeanLocal;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;
import java.io.IOException;
import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    @EJB
    private AuthManagmentBeanLocal authEJB;

    private String email;
    private String password;

    private String token;
    private Users loggedInUser;
    private String role;

    // For forgot/reset password flow
    private String resetToken;
    private String newPassword;

    // ================================
    // 🔐 LOGIN
    // ================================
    public void login() {
        try {
            token = authEJB.login(email, password);

            loggedInUser = authEJB.getUserFromToken(token);
            role = authEJB.getRoleFromToken(token);

            // Redirect based on role — welcome toast triggered by ?welcome=true&name=
            String firstName = (loggedInUser.getName() != null && loggedInUser.getName().contains(" "))
                ? loggedInUser.getName().split(" ")[0]
                : (loggedInUser.getName() != null ? loggedInUser.getName() : "User");

            if (role.equalsIgnoreCase("Admin")) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("Admin/admin_dashboard.xhtml?welcome=true&name=" + firstName);
            } else if (role.equalsIgnoreCase("Organizer")) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("Organizer/organizer_dashboard.xhtml?welcome=true&name=" + firstName);
            } else {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("User/user_dashboard.xhtml?welcome=true&name=" + firstName);
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Login Failed", e.getMessage()));
        }
    }

    // ================================
    // 🚪 LOGOUT
    // ================================
    public void logout() {
        try {
            if (token != null) {
                authEJB.logout(token);
            }

            FacesContext.getCurrentInstance().getExternalContext()
                    .invalidateSession();

            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("login.xhtml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================
    // 🔑 FORGOT PASSWORD
    // ================================
    public void forgotPassword() {
        try {
            authEJB.forgotPassword(email);
            // Redirect with ?step=2 in URL — survives tab switching unlike ViewScope
            FacesContext.getCurrentInstance().getExternalContext()
                .redirect("forgot-password.xhtml?step=2");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // ================================
    // 🔁 RESET PASSWORD (CDI-callable)
    // ================================
    public String doResetPassword() {
        try {
            authEJB.resetPassword(resetToken, newPassword);
            // Redirect to login with success toast trigger
            FacesContext.getCurrentInstance().getExternalContext()
                .redirect("login.xhtml?pwdreset=success");
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        }
    }

    // ================================
    // 🔁 RESET PASSWORD (legacy - keep for compatibility)
    // ================================
    public void resetPassword(String token, String newPassword) {
        try {
            authEJB.resetPassword(token, newPassword);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Password reset successful"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // ================================
    // ✅ CHECK LOGIN
    // ================================
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    // ================================
    // 🔁 GETTERS & SETTERS
    // ================================
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Users getLoggedInUser() {
        return loggedInUser;
    }

    public String getRole() { return role; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}