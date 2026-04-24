/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.UserManagementLocal;
import Entity.Roles;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    @EJB
    private UserManagementLocal userService;

    @PersistenceContext(unitName = "jpu")
    private EntityManager em;

    private List<Users> users = new ArrayList<>();
    private Users selectedUser = new Users();

    private String searchKeyword;

    // ===============================
    // 🔹 ROLE NAME (for register form)
    //    Values: "Participant"  or  "Organizer"
    //    Querying by NAME avoids auto-increment ID mismatches
    // ===============================
    private String selectedRoleName = "Participant"; // default

    public String getSelectedRoleName() {
        return selectedRoleName;
    }

    public void setSelectedRoleName(String selectedRoleName) {
        this.selectedRoleName = selectedRoleName;
    }

    // ===============================
    // 🔹 LOAD ALL USERS
    // ===============================
    public void loadUsers() {
        try {
            users = new ArrayList<>(userService.getAllUsers());
        } catch (Exception e) {
            showMessage("Error loading users: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 SEARCH USERS
    // ===============================
    public void searchUsers() {
        try {
            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                loadUsers();
                return;
            }
            users = new ArrayList<>(userService.searchUsers(searchKeyword));
        } catch (Exception e) {
            showMessage("Search error: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 GET USER BY ID
    // ===============================
    public void loadUserById(int id) {
        try {
            selectedUser = userService.getUserById(id);
        } catch (Exception e) {
            showMessage("Error fetching user");
        }
    }

    // ===============================
    // 🔹 REGISTER USER
    //    Looks up Roles entity by NAME (not by ID)
    //    so it works regardless of auto-increment values.
    // ===============================
    public String registerUser() {
        try {
            // Validate role name
            if (selectedRoleName == null
                    || (!"Participant".equalsIgnoreCase(selectedRoleName)
                        && !"Organizer".equalsIgnoreCase(selectedRoleName))) {
                showMessage("Please select a valid role (Participant or Organizer).");
                return null;
            }

            // Look up role by name from DB
            Roles role;
            try {
                role = em.createNamedQuery("Roles.findByRoleName", Roles.class)
                         .setParameter("roleName", selectedRoleName)
                         .getSingleResult();
            } catch (Exception ex) {
                showMessage("Role '" + selectedRoleName
                        + "' not found in database. Please run the INSERT roles SQL script.");
                return null;
            }

            selectedUser.setRoleId(role);

            // Delegate to EJB (which sets verified_status, sends email, etc.)
            userService.registerUser(selectedUser);

            // Success message
            String successMsg = "Participant".equalsIgnoreCase(selectedRoleName)
                ? "Account created successfully! You can now sign in."
                : "Organizer account created! Pending admin verification — check your email once approved.";

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, successMsg, null));

            // Navigate to login page with popup trigger param
            String redirectParam = "Participant".equalsIgnoreCase(selectedRoleName)
                ? "login?faces-redirect=true&registered=true"
                : "login?faces-redirect=true&registered=organizer";

            selectedUser    = new Users();
            selectedRoleName = "Participant";

            return redirectParam;

        } catch (Exception e) {
            showMessage("Registration failed: " + e.getMessage());
            return null;
        }
    }

    // ===============================
    // 🔹 UPDATE USER
    // ===============================
    public void updateUser() {
        try {
            userService.updateUser(selectedUser);
            showMessage("User updated successfully");
            loadUsers();
        } catch (Exception e) {
            showMessage("Update failed: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 BLOCK USER
    // ===============================
    public void blockUser(int userId) {
        try {
            userService.blockUser(userId);
            showMessage("User blocked");
            loadUsers();
        } catch (Exception e) {
            showMessage("Error blocking user");
        }
    }

    // ===============================
    // 🔹 UNBLOCK USER
    // ===============================
    public void unblockUser(int userId) {
        try {
            userService.unblockUser(userId);
            showMessage("User unblocked");
            loadUsers();
        } catch (Exception e) {
            showMessage("Error unblocking user");
        }
    }

    // ===============================
    // 🔹 VERIFY USER
    // ===============================
    public void verifyUser(int userId) {
        try {
            userService.verifyAccount(userId);
            showMessage("User verified");
            loadUsers();
        } catch (Exception e) {
            showMessage("Error verifying user");
        }
    }

    // ===============================
    // 🔹 MESSAGE HELPER
    // ===============================
    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ===============================
    // 🔹 GETTERS & SETTERS
    // ===============================

    public List<Users> getUsers() { return users; }

    public Users getSelectedUser() { return selectedUser; }
    public void setSelectedUser(Users selectedUser) { this.selectedUser = selectedUser; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
}
