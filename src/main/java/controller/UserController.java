/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import EJB.UserManagmentLocal;
import Entity.Users;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.Collection;

@RequestScoped
public class UserController {

    @Inject
    UserManagmentLocal userService;

    // Register User
    public void registerUser(Users user){
        userService.registerUser(user);
    }

    // Login
    public Users loginUser(String email, String password){
        return userService.loginUser(email, password);
    }

    // Get user by ID
    public Users getUserById(int userId){
        return userService.getUserById(userId);
    }

    // Update user
    public void updateUser(Users user){
        userService.updateUser(user);
    }

    // Delete user
    public void deleteUser(int userId){
        userService.deleteUser(userId);
    }

    // Get all users
    public Collection<Users> getAllUsers(){
        return userService.getAllUsers();
    }

    // Get users by role
    public Collection<Users> getUsersByRole(String role){
        return userService.getUsersByRole(role);
    }

    // Upload profile photo
    public void uploadProfilePhoto(int userId, String photoPath){
        userService.uploadProfilePhoto(userId, photoPath);
    }

}
