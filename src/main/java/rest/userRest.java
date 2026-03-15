/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

import EJB.UserManagementLocal;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
//import UserManagementLocal;

@Path("users")
public class userRest {

    @EJB
    UserManagementLocal userBean;

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerUser(Users user){
        userBean.registerUser(user);
    }

    @GET
    @Path("login/{email}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Users loginUser(@PathParam("email") String email,
                           @PathParam("password") String password) {

        return userBean.loginUser(email, password);
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Users> getAllUsers() {
        return userBean.getAllUsers();
    }
    
    @GET
    @Path("search/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Users> searchUsers(@PathParam("keyword") String keyword) {
        return userBean.searchUsers(keyword);
    }
    
    @PUT
    @Path("block/{id}")
    public void blockUser(@PathParam("id") int id) {
        userBean.blockUser(id);
    }
    
    @PUT
    @Path("unblock/{id}")
    public void unblockUser(@PathParam("id") int id) {
        userBean.unblockUser(id);
    }
}