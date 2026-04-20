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

    // ================================
    // 🔐 LOGIN
    // ================================
    public void login() {
        try {
            token = authEJB.login(email, password);

            loggedInUser = authEJB.getUserFromToken(token);
            role = authEJB.getRoleFromToken(token);

            // Redirect based on role
            if (role.equalsIgnoreCase("Admin")) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("admin/dashboard.xhtml");
            } else if (role.equalsIgnoreCase("Organizer")) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("organizer/dashboard.xhtml");
            } else {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("user/home.xhtml");
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

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Reset link sent to email"));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                e.getMessage(), null));
        }
    }

    // ================================
    // 🔁 RESET PASSWORD
    // ================================
    public void resetPassword(String token, String newPassword) {
        try {
            authEJB.resetPassword(token, newPassword);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Password reset successful"));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                e.getMessage(), null));
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

    public String getRole() {
        return role;
    }
}