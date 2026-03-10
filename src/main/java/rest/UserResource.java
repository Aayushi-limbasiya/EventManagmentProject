/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import Entity.Users;
import controller.UserController;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class UserResource {

    @Inject
    UserController controller;

    // Register User
    @POST
    @Path("/register")
    public void registerUser(Users user){
        controller.registerUser(user);
    }

    // Login User
    @POST
    @Path("/login")
    public Users loginUser(Users user){
        return controller.loginUser(user.getEmail(), user.getPassword());
    }

    // Get User by ID
    @GET
    @Path("/{id}")
    public Users getUserById(@PathParam("id") int id){
        return controller.getUserById(id);
    }

    // Update User
    @PUT
    @Path("/update")
    public void updateUser(Users user){
        controller.updateUser(user);
    }

    // Delete User
    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") int id){
        controller.deleteUser(id);
    }

    // Get All Users
    @GET
    @Path("/all")
    public Collection<Users> getAllUsers(){
        return controller.getAllUsers();
    }

    // Get Users by Role
    @GET
    @Path("/role/{role}")
    public Collection<Users> getUsersByRole(@PathParam("role") String role){
        return controller.getUsersByRole(role);
    }

    // Upload Profile Photo
    @PUT
    @Path("/uploadPhoto/{id}")
    public void uploadProfilePhoto(@PathParam("id") int id, String photoPath){
        controller.uploadProfilePhoto(id, photoPath);
    }
}
