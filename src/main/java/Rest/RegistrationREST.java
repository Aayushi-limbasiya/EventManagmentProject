package Rest;

import EJB.RegistrationBeanLocal;
import Entity.Registrations;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.Map;

@Path("/registrations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistrationREST {

    @EJB
    private RegistrationBeanLocal registrationBean;

    // ─────────────────────────────────────────────────────────────
    // 1. REGISTER FOR AN EVENT
    // POST /registrations/register
    // Body: { "userId": 1, "eventId": 2 }
    // Returns: Confirmed / Waitlist
    // ─────────────────────────────────────────────────────────────
    @POST
    @Path("/register")
    public Response registerForEvent(Map<String, Object> body) {
        try {
            int userId  = Integer.parseInt(body.get("userId").toString());
            int eventId = Integer.parseInt(body.get("eventId").toString());
            String status = registrationBean.registerForEvent(userId, eventId);
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(
                        "message", "Registration successful.",
                        "status",  status
                    )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. GET REGISTRATION BY ID
    // GET /registrations/{registrationId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/{registrationId}")
    public Response getRegistrationById(@PathParam("registrationId") int registrationId) {
        Registrations reg = registrationBean.getRegistrationById(registrationId);
        if (reg == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Registration not found with ID: " + registrationId))
                    .build();
        }
        return Response.ok(reg).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 3. CANCEL A REGISTRATION
    // PUT /registrations/{registrationId}/cancel
    // Automatically promotes first waitlisted user to Confirmed
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{registrationId}/cancel")
    public Response cancelRegistration(@PathParam("registrationId") int registrationId) {
        try {
            registrationBean.cancelRegistration(registrationId);
            return Response.ok(Map.of(
                "message", "Registration ID " + registrationId + " cancelled. Waitlist promoted if applicable."
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. APPROVE A PENDING REGISTRATION (Organizer/Admin)
    // PUT /registrations/{registrationId}/approve
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{registrationId}/approve")
    public Response approveRegistration(@PathParam("registrationId") int registrationId) {
        try {
            registrationBean.approveRegistration(registrationId);
            return Response.ok(Map.of(
                "message", "Registration ID " + registrationId + " approved. Status set to Confirmed."
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. MARK ATTENDANCE (Check-in at event)
    // PUT /registrations/{registrationId}/checkin/{eventId}
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{registrationId}/checkin/{eventId}")
    public Response markAttendance(
            @PathParam("registrationId") int registrationId,
            @PathParam("eventId") int eventId) {
        try {
            registrationBean.markAttendance(registrationId, eventId);
            return Response.ok(Map.of(
                "message", "Attendance marked as Present for registration ID: " + registrationId
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 6. MARK ABSENT
    // PUT /registrations/{registrationId}/absent
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{registrationId}/absent")
    public Response markAbsent(@PathParam("registrationId") int registrationId) {
        try {
            registrationBean.markAbsent(registrationId);
            return Response.ok(Map.of(
                "message", "Attendance marked as Absent for registration ID: " + registrationId
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 7. GET ALL EVENTS A USER IS REGISTERED FOR
    // GET /registrations/user/{userId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/user/{userId}")
    public Response getRegisteredEventsByUser(@PathParam("userId") int userId) {
        Collection<Registrations> regs = registrationBean.getRegisteredEventsByUser(userId);
        return Response.ok(regs).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 8. GET ALL REGISTRATIONS FOR AN EVENT
    // GET /registrations/event/{eventId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}")
    public Response getAllRegistrationsByEvent(@PathParam("eventId") int eventId) {
        Collection<Registrations> regs = registrationBean.getAllRegistrationsByEvent(eventId);
        return Response.ok(regs).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 9. GET CONFIRMED PARTICIPANTS FOR AN EVENT
    // GET /registrations/event/{eventId}/participants
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}/participants")
    public Response getParticipantsByEvent(@PathParam("eventId") int eventId) {
        Collection<Registrations> regs = registrationBean.getParticipantsByEvent(eventId);
        return Response.ok(regs).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 10. GET WAITLIST FOR AN EVENT
    // GET /registrations/event/{eventId}/waitlist
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}/waitlist")
    public Response getWaitlistByEvent(@PathParam("eventId") int eventId) {
        Collection<Registrations> regs = registrationBean.getWaitlistByEvent(eventId);
        return Response.ok(regs).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 11. GET PENDING APPROVALS FOR AN EVENT (Organizer)
    // GET /registrations/event/{eventId}/pending
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}/pending")
    public Response getPendingApprovalsByEvent(@PathParam("eventId") int eventId) {
        Collection<Registrations> regs = registrationBean.getPendingApprovalsByEvent(eventId);
        return Response.ok(regs).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 12. GET CONFIRMED COUNT FOR AN EVENT
    // GET /registrations/event/{eventId}/count
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}/count")
    public Response getConfirmedCount(@PathParam("eventId") int eventId) {
        long count = registrationBean.getConfirmedCount(eventId);
        return Response.ok(Map.of("eventId", eventId, "confirmedCount", count)).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 13. GET REGISTRATIONS BY EVENT AND ATTENDANCE STATUS
    // GET /registrations/event/{eventId}/attendance/{attendanceStatus}
    // attendanceStatus: Present | Absent
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}/attendance/{attendanceStatus}")
    public Response getByEventAndAttendance(
            @PathParam("eventId") int eventId,
            @PathParam("attendanceStatus") String attendanceStatus) {
        Collection<Registrations> regs = registrationBean.getByEventAndAttendance(eventId, attendanceStatus);
        return Response.ok(regs).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 14. GET REGISTRATION CONFIRMATION DETAILS
    // GET /registrations/{registrationId}/confirmation
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/{registrationId}/confirmation")
    public Response getRegistrationConfirmation(@PathParam("registrationId") int registrationId) {
        try {
            Registrations reg = registrationBean.getRegistrationConfirmation(registrationId);
            return Response.ok(reg).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 15. GENERATE QR CODE VALUE
    // GET /registrations/{registrationId}/qr
    // Returns: "REG-1-EVENT-2-USER-3"
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/{registrationId}/qr")
    public Response generateQRCodeValue(@PathParam("registrationId") int registrationId) {
        try {
            String qrValue = registrationBean.generateQRCodeValue(registrationId);
            return Response.ok(Map.of(
                "registrationId", registrationId,
                "qrValue", qrValue
            )).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 16. CHECK IF USER IS ALREADY REGISTERED
    // GET /registrations/check?userId=1&eventId=2
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/check")
    public Response isAlreadyRegistered(
            @QueryParam("userId") int userId,
            @QueryParam("eventId") int eventId) {
        boolean registered = registrationBean.isAlreadyRegistered(userId, eventId);
        return Response.ok(Map.of(
            "userId",     userId,
            "eventId",    eventId,
            "registered", registered
        )).build();
    }
}
