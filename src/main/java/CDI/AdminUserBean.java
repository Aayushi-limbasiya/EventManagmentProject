package CDI;

import EJB.UserManagementLocal;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI Bean for Admin User Management page.
 * Handles: load all users, verify, block, unblock.
 */
@Named("adminUserBean")
@ViewScoped
public class AdminUserBean implements Serializable {

    @EJB
    private UserManagementLocal userService;

    private List<Users> allUsers = new ArrayList<>();

    // ── LOAD ALL USERS ──────────────────────────────────────
    public void init() {
        loadAllUsers();
    }

    public void loadAllUsers() {
        try {
            allUsers = new ArrayList<>(userService.getAllUsers());
        } catch (Exception e) {
            addMsg("Error loading users: " + e.getMessage());
        }
    }

    // ── VERIFY USER ─────────────────────────────────────────
    public String verifyUser(int userId) {
        try {
            userService.verifyAccount(userId);
            loadAllUsers();
            addMsg("User verified successfully.");
            return "admin_user_management?faces-redirect=true&verified=true";
        } catch (Exception e) {
            addMsg("Error verifying user: " + e.getMessage());
            return null;
        }
    }

    // ── BLOCK USER ──────────────────────────────────────────
    public String blockUser(int userId) {
        try {
            userService.blockUser(userId);
            loadAllUsers();
            return "admin_user_management?faces-redirect=true&blocked=true";
        } catch (Exception e) {
            addMsg("Error blocking user: " + e.getMessage());
            return null;
        }
    }

    // ── UNBLOCK USER ─────────────────────────────────────────
    public String unblockUser(int userId) {
        try {
            userService.unblockUser(userId);
            loadAllUsers();
            addMsg("User unblocked successfully.");
            return "admin_user_management?faces-redirect=true";
        } catch (Exception e) {
            addMsg("Error unblocking user: " + e.getMessage());
            return null;
        }
    }

    // ── HELPER ──────────────────────────────────────────────
    private void addMsg(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ── GETTERS ─────────────────────────────────────────────
    public List<Users> getAllUsers() {
        if (allUsers.isEmpty()) loadAllUsers();
        return allUsers;
    }
}
