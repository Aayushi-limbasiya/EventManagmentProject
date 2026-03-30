/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;

import EJB.AuthManagmentBeanLocal;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthManagmentREST
 * REST API for Authentication Module
 * Base URL: http://localhost:8080/EventManagmentSystem/api/auth
 * Matches AuthManagmentBeanLocal interface exactly
 *
 * Real email is sent on:
 *   - login()           → sendLoginSuccessEmail() called inside EJB
 *   - forgotPassword()  → reset link email sent inside EJB
 *   - resetPassword()   → confirmation email sent inside EJB
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthManagmentREST {

    @EJB
    private AuthManagmentBeanLocal authBean;

    // ══════════════════════════════════════════════════════════
    // POST /api/auth/login
    // Body: { "email": "...", "password": "..." }
    // Returns: JWT token + user details
    // Also sends real email to user on success
    // ══════════════════════════════════════════════════════════
    @POST
    @Path("/login")
    public Response login(Users credentials) {
        try {
            // EJB login() → validates credentials → generates JWT
            // → saves to DB → sends login success email automatically
            String token = authBean.login(
                credentials.getEmail(),
                credentials.getPassword()
            );

            // Get user details from token to return in response
            Users user = authBean.getUserFromToken(token);
            String role = authBean.getRoleFromToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);
            response.put("userId", user.getUserId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("message", "Login successful. Email notification sent.");

            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // GET /api/auth/validate
    // Header: Authorization: Bearer 
    // Returns: { valid: true/false, role, name, userId }
    // ══════════════════════════════════════════════════════════
    @GET
    @Path("/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            boolean valid = authBean.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", valid);

            if (valid) {
                String role = authBean.getRoleFromToken(token);
                Users user = authBean.getUserFromToken(token);
                response.put("role", role);
                if (user != null) {
                    response.put("userId", user.getUserId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                }
            }
            return Response.ok(response).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // POST /api/auth/logout
    // Header: Authorization: Bearer 
    // Revokes specific token in DB
    // ══════════════════════════════════════════════════════════
    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            authBean.logout(token);
            return Response.ok("{\"message\":\"Logged out successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // POST /api/auth/logout-all?userId=1
    // Revokes ALL tokens for a user (logout all devices)
    // ══════════════════════════════════════════════════════════
    @POST
    @Path("/logout-all")
    public Response logoutAllDevices(@QueryParam("userId") int userId) {
        try {
            authBean.logoutAllDevices(userId);
            return Response.ok("{\"message\":\"Logged out from all devices\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // POST /api/auth/forgot-password?email=user@gmail.com
    // Generates reset token → saves to DB → sends reset link email
    // Real email is sent via Gmail SMTP in EJB
    // ══════════════════════════════════════════════════════════
    @POST
    @Path("/forgot-password")
    public Response forgotPassword(@QueryParam("email") String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"email parameter is required\"}")
                        .build();
            }
            // EJB generates reset token → saves to DB → sends email automatically
            authBean.forgotPassword(email);
            return Response.ok(
                "{\"message\":\"If this email exists, a reset link has been sent\"}"
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/reset-password")
    public Response resetPassword(@QueryParam("token") String resetToken,
                                  @QueryParam("newPassword") String newPassword) {
        try {
            if (resetToken == null || resetToken.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"token parameter is required\"}")
                        .build();
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"newPassword parameter is required\"}")
                        .build();
            }
            // EJB validates token → sets new password → sends confirmation email
            authBean.resetPassword(resetToken, newPassword);
            return Response.ok(
                "{\"message\":\"Password reset successfully. Please login with your new password.\"}"
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // PRIVATE HELPER
    // Extract Bearer token from Authorization header
    // ══════════════════════════════════════════════════════════
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException(
                "Missing or invalid Authorization header. Format: Bearer <token>"
            );
        }
        return authHeader.substring(7);
    }
}