/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;

import EJB.EventApprovalBeanLocal;
import Entity.Approvals;
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
import java.util.Map;

/**
 * EventApprovalREST
 * REST API for Event Approval Module
 * Base URL: http://localhost:8080/EventManagmentSystem/api/approvals
 * Matches EventApprovalBeanLocal interface exactly
 * Actor: Admin
 *
 * IMPORTANT: Static paths declared BEFORE /{id} to avoid Payara 400 error
 */
@Path("/approvals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventApprovalREST {

    @EJB
    private EventApprovalBeanLocal approvalBean;

    // ══════════════════════════════════════════════════════════
    // STATIC GET PATHS — MUST BE BEFORE /{id}
    // ══════════════════════════════════════════════════════════

    // GET /api/approvals/pending
    // Admin views all events waiting for approval decision
    @GET
    @Path("/pending")
    public Response getPendingApprovals() {
        try {
            Collection<Approvals> approvals = approvalBean.getPendingApprovals();
            return Response.ok(approvals).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/approvals/approved
    // Get all approved event approval records
    @GET
    @Path("/approved")
    public Response getAllApproved() {
        try {
            Collection<Approvals> approvals = approvalBean.getAllApproved();
            return Response.ok(approvals).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/approvals/rejected
    // Get all rejected event approval records
    @GET
    @Path("/rejected")
    public Response getAllRejected() {
        try {
            Collection<Approvals> approvals = approvalBean.getAllRejected();
            return Response.ok(approvals).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/approvals/dashboard
    // Admin dashboard stats — count of pending, approved, rejected
    @GET
    @Path("/dashboard")
    public Response getDashboardStats() {
        try {
            Map<String, Long> stats = approvalBean.getDashboardStats();
            return Response.ok(stats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/approvals/event/{eventId}/history
    // Get full approval history for a specific event
    @GET
    @Path("/event/{eventId}/history")
    public Response getApprovalHistoryByEvent(@PathParam("eventId") int eventId) {
        try {
            Collection<Approvals> history = approvalBean.getApprovalHistoryByEvent(eventId);
            return Response.ok(history).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/approvals/event/{eventId}/latest
    // Get latest approval record for an event
    @GET
    @Path("/event/{eventId}/latest")
    public Response getLatestApprovalByEvent(@PathParam("eventId") int eventId) {
        try {
            Approvals approval = approvalBean.getLatestApprovalByEvent(eventId);
            if (approval != null) {
                return Response.ok(approval).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No approval record found for event ID: " + eventId + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/approvals/admin/{adminUserId}
    // Get all approvals made by a specific admin
    @GET
    @Path("/admin/{adminUserId}")
    public Response getApprovalsByAdmin(@PathParam("adminUserId") int adminUserId) {
        try {
            Collection<Approvals> approvals = approvalBean.getApprovalsByAdmin(adminUserId);
            return Response.ok(approvals).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // DYNAMIC GET PATH — LAST
    // ══════════════════════════════════════════════════════════

    // GET /api/approvals/1
    // Get approval by ID
    @GET
    @Path("/{id}")
    public Response getApprovalById(@PathParam("id") String approvalIdStr) {
        try {
            int approvalId;
            try {
                approvalId = Integer.parseInt(approvalIdStr);
            } catch (NumberFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Invalid approval ID: " + approvalIdStr + "\"}")
                        .build();
            }
            Approvals approval = approvalBean.getApprovalById(approvalId);
            if (approval != null) {
                return Response.ok(approval).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Approval not found with ID: " + approvalId + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // POST METHODS
    // ══════════════════════════════════════════════════════════

    // POST /api/approvals/submit?eventId=1&adminUserId=1
    // Organizer submits event for approval
    // Creates approval record in DB with decision = Pending
    // Event must be in Pending status first
    @POST
    @Path("/submit")
    public Response submitForApproval(@QueryParam("eventId") int eventId,
                                      @QueryParam("adminUserId") int adminUserId) {
        try {
            approvalBean.submitForApproval(eventId, adminUserId);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Event submitted for approval. Awaiting admin review.\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // PUT METHODS — Admin Decision
    // ══════════════════════════════════════════════════════════

    // PUT /api/approvals/approve?eventId=1&adminUserId=1&remark=Looks good
    // Admin approves event
    // Auto sets event status = Approved
    // Sends email notification to organizer
    @PUT
    @Path("/approve")
    public Response approveEvent(@QueryParam("eventId") int eventId,
                                 @QueryParam("adminUserId") int adminUserId,
                                 @QueryParam("remark") String remark) {
        try {
            if (remark == null || remark.trim().isEmpty()) {
                remark = "Approved by admin";
            }
            // EJB: updates approval → sets event status = Approved → sends email
            approvalBean.approveEvent(eventId, adminUserId, remark);
            return Response.ok(
                "{\"message\":\"Event approved successfully. Organizer notified. Event status set to Approved.\"}"
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /api/approvals/reject?eventId=1&adminUserId=1&remark=Incomplete details
    // Admin rejects event
    // Auto sets event status = Rejected
    // Sends email notification to organizer
    @PUT
    @Path("/reject")
    public Response rejectEvent(@QueryParam("eventId") int eventId,
                                @QueryParam("adminUserId") int adminUserId,
                                @QueryParam("remark") String remark) {
        try {
            if (remark == null || remark.trim().isEmpty()) {
                remark = "Rejected by admin";
            }
            // EJB: updates approval → sets event status = Rejected → sends email
            approvalBean.rejectEvent(eventId, adminUserId, remark);
            return Response.ok(
                "{\"message\":\"Event rejected. Organizer notified. Event status set to Rejected.\"}"
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
