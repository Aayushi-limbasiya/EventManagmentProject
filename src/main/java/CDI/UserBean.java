/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.UserManagementLocal;
import Entity.Users;
import jakarta.ejb.EJB;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    @EJB
    private UserManagementLocal userService;

    private List<Users> users = new ArrayList<>();
    private Users selectedUser = new Users();

    private String searchKeyword;

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
    // ===============================
    public void registerUser() {
        try {
            userService.registerUser(selectedUser);
            showMessage("User registered successfully");
            selectedUser = new Users();
            loadUsers();
        } catch (Exception e) {
            showMessage("Registration failed: " + e.getMessage());
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

    public List<Users> getUsers() {
        return users;
    }

    public Users getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(Users selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
