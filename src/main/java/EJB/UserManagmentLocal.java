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
public interface UserManagmentLocal {
     // Register User
    void registerUser(Users user);

    // Login Authentication
    Users loginUser(String email, String password);

    // Get User By ID
    Users getUserById(int userId);

    // Update User
    void updateUser(Users user);

    // Delete User
    void deleteUser(int userId);

    // Get All Users
    Collection<Users> getAllUsers();

    // Get Users By Role
    Collection<Users> getUsersByRole(String role);
    
    void uploadProfilePhoto(int userId, String photoPath);
}
