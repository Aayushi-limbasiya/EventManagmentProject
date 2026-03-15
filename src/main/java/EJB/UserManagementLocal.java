/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Users;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author OS
 */
@Local
public interface UserManagementLocal {
    // BASIC FUNCTIONS
    
    void registerUser(Users user);
    
    Users loginUser(String email, String password);
    
    Users getUserById(int userId);
    
    void updateUser(Users user);
    
    void uploadProfilePhoto(int userId, String photoPath);
    
    
    // EXTRA FUNCTIONS
    
    void changePassword(int userId, String newPassword);
    
    void forgotPassword(String email);
    
    void verifyAccount(int userId);
    
    Collection<Users> searchUsers(String keyword);
    
    void blockUser(int userId);
    
    void unblockUser(int userId);
    
    Collection<Users> getUsersByRole(int roleId);
    
    Collection<Users> getAllUsers();
    
    void sendRegistrationEmail(String email);
}
