/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Rest;

import EJB.UserManagementLocal;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

/**
 * UserManagementREST
 * REST API for User Management Module
 * Base URL: http://localhost:8080/EventManagmentSystem/api/users
 * Methods match your exact UserManagementLocal interface
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManagementREST {

    @EJB
    private UserManagementLocal userBean;

    // ── BASIC FUNCTIONS ───────────────────────────────────────

    /**
     * POST /api/users/register
     * Register a new user
     */
    @POST
    @Path("/register")
    public Response registerUser(Users user) {
        try {
            userBean.registerUser(user);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"User registered successfully\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * GET /api/users/{id}
     * Get user by ID
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") int userId) {
        try {
            Users user = userBean.getUserById(userId);
            if (user != null) {
                return Response.ok(user).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User not found with ID: " + userId + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * PUT /api/users/update
     * Update user profile
     */
    @PUT
    @Path("/update")
    public Response updateUser(Users user) {
        try {
            userBean.updateUser(user);
            return Response.ok("{\"message\":\"User updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * PUT /api/users/{id}/photo?path=uploads/photo.jpg
     * Upload profile photo path
     */
    @PUT
    @Path("/{id}/photo")
    public Response uploadProfilePhoto(@PathParam("id") int userId,
                                       @QueryParam("path") String photoPath) {
        try {
            userBean.uploadProfilePhoto(userId, photoPath);
            return Response.ok("{\"message\":\"Profile photo updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ── EXTRA FUNCTIONS ───────────────────────────────────────

    /**
     * PUT /api/users/{id}/verify
     * Admin verifies a user account
     */
    @PUT
    @Path("/{id}/verify")
    public Response verifyAccount(@PathParam("id") int userId) {
        try {
            userBean.verifyAccount(userId);
            return Response.ok("{\"message\":\"Account verified successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * GET /api/users/search?keyword=john
     * Search users by name or email
     */
    @GET
    @Path("/search")
    public Response searchUsers(@QueryParam("keyword") String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"keyword parameter is required\"}")
                        .build();
            }
            Collection<Users> users = userBean.searchUsers(keyword);
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * PUT /api/users/{id}/block
     * Admin blocks a user
     */
    @PUT
    @Path("/{id}/block")
    public Response blockUser(@PathParam("id") int userId) {
        try {
            userBean.blockUser(userId);
            return Response.ok("{\"message\":\"User blocked successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * PUT /api/users/{id}/unblock
     * Admin unblocks a user
     */
    @PUT
    @Path("/{id}/unblock")
    public Response unblockUser(@PathParam("id") int userId) {
        try {
            userBean.unblockUser(userId);
            return Response.ok("{\"message\":\"User unblocked successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * GET /api/users/role/{roleId}
     * Get users by role ID (1=Admin, 2=Organizer, 3=Participant)
     */
    @GET
    @Path("/role/{roleId}")
    public Response getUsersByRole(@PathParam("roleId") int roleId) {
        try {
            Collection<Users> users = userBean.getUsersByRole(roleId);
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * GET /api/users/all
     * Get all users (Admin only)
     */
    @GET
    @Path("/all")
    public Response getAllUsers() {
        try {
            Collection<Users> users = userBean.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}