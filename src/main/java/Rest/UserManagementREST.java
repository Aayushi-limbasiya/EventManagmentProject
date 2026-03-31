package Rest;
import EJB.UserManagementLocal;
import Entity.Roles;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManagementREST {

    @EJB
    private UserManagementLocal userBean;

    @PersistenceContext(unitName = "jpu")
    private EntityManager em;

    // ══════════════════════════════════════════════════════════
    // ✅ STATIC GET PATHS — MUST BE FIRST IN FILE
    // ══════════════════════════════════════════════════════════
    @GET
    @Path("all")
    public Response getAllUsers() {
        try {

            return Response.ok(userBean.getAllUsers()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("search")
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
    
    @GET
    @Path("role/{roleId}")
    public Response getUsersByRole(@PathParam("roleId") int roleId) {
        try {
        // Call the EJB method instead of using 'em' here
        Collection<Users> users = userBean.getUsersByRole(roleId);
        
        if (users == null || users.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build(); // Or empty array []
        }
        return Response.ok(users).build();
    } catch (Exception e) {
        return Response.serverError().entity(e.getMessage()).build();
    }
    }

    @GET
    @Path("id/{id}")
    public Response getUserById(@PathParam("id") int userId) {
        try {

            Users user = userBean.getUserById(userId);

            if (user != null) {
                return Response.ok(user).build();
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User not found\"}")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // POST + PUT METHODS
    // ══════════════════════════════════════════════════════════
    // POST /api/users/register
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

    // PUT /api/users/update
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

    // PUT /api/users/1/photo?path=default.jpg
    @PUT
    @Path("/{id}/photo")
    public Response uploadProfilePhoto(@PathParam("id") int userId,
            @QueryParam("path") String photoPath) {
        try {
            if (photoPath == null || photoPath.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"path parameter is required\"}")
                        .build();
            }
            userBean.uploadProfilePhoto(userId, photoPath);
            return Response.ok("{\"message\":\"Profile photo saved: " + photoPath + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /api/users/1/verify
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

    // PUT /api/users/1/block
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

    // PUT /api/users/1/unblock
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
}
