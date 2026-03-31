/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;

/**
 *
 * @author parth
 */


import EJB.FeedbackCertificateBeanLocal;
import Entity.Feedback;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackREST {

    @EJB
    private FeedbackCertificateBeanLocal feedbackBean;

    // ── GET test ──────────────────────────────────────────────────
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        return Response.ok("Feedback API is working!").build();
    }

    // ── GET feedback by ID ────────────────────────────────────────
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        Feedback f = feedbackBean.getFeedbackById(id);
        if (f == null)
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Feedback not found\"}").build();
        return Response.ok(f).build();
    }

    // ── GET all feedback for an event ─────────────────────────────
    @GET
    @Path("/event/{eventId}")
    public Response getByEvent(@PathParam("eventId") int eventId) {
        Collection<Feedback> list = feedbackBean.getFeedbackByEvent(eventId);
        return Response.ok(list).build();
    }

    // ── GET all feedback by a user ────────────────────────────────
    @GET
    @Path("/user/{userId}")
    public Response getByUser(@PathParam("userId") int userId) {
        Collection<Feedback> list = feedbackBean.getFeedbackByUser(userId);
        return Response.ok(list).build();
    }

    // ── GET average rating for an event ──────────────────────────
    @GET
    @Path("/event/{eventId}/average-rating")
    public Response getAverageRating(@PathParam("eventId") int eventId) {
        double avg = feedbackBean.getAverageRating(eventId);
        return Response.ok("{\"eventId\":" + eventId
                + ",\"averageRating\":" + avg + "}").build();
    }

    // ── GET total feedback count for an event ─────────────────────
    @GET
    @Path("/event/{eventId}/count")
    public Response getCount(@PathParam("eventId") int eventId) {
        long count = feedbackBean.getFeedbackCount(eventId);
        return Response.ok("{\"eventId\":" + eventId
                + ",\"totalFeedback\":" + count + "}").build();
    }

    // ── GET rating distribution for an event ─────────────────────
    @GET
    @Path("/event/{eventId}/distribution")
    public Response getDistribution(@PathParam("eventId") int eventId) {
        Map<Integer, Long> dist = feedbackBean.getRatingDistribution(eventId);
        return Response.ok(dist).build();
    }

    // ── GET feedback by organizer ─────────────────────────────────
    @GET
    @Path("/organizer/{organizerId}")
    public Response getByOrganizer(@PathParam("organizerId") int organizerId) {
        Collection<Feedback> list = feedbackBean.getFeedbackByOrganizer(organizerId);
        return Response.ok(list).build();
    }

    // ── GET organizer analytics ───────────────────────────────────
    @GET
    @Path("/organizer/{organizerId}/analytics")
    public Response getOrganizerAnalytics(@PathParam("organizerId") int organizerId) {
        Map<Integer, Double> analytics = feedbackBean.getOrganizerFeedbackAnalytics(organizerId);
        return Response.ok(analytics).build();
    }

    // ── GET top rated events report ───────────────────────────────
    @GET
    @Path("/report/top-rated")
    public Response getTopRated() {
        List<Object[]> report = feedbackBean.getEventRatingReport();
        List<Map<String, Object>> result = report.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("eventId", row[0]);
            map.put("averageRating", row[1]);
            return map;
        }).collect(Collectors.toList());
        return Response.ok(result).build();
    }

    // ── GET check if user already submitted feedback ───────────────
    @GET
    @Path("/check")
    public Response checkSubmitted(@QueryParam("userId") int userId,
                                   @QueryParam("eventId") int eventId) {
        boolean submitted = feedbackBean.hasSubmittedFeedback(userId, eventId);
        return Response.ok("{\"alreadySubmitted\":" + submitted + "}").build();
    }

    // ── POST submit new feedback ──────────────────────────────────
    @POST
    @Path("/submit")
    public Response submit(@QueryParam("userId") int userId,
                           @QueryParam("eventId") int eventId,
                           @QueryParam("rating") int rating,
                           @QueryParam("comment") String comment) {
        try {
            feedbackBean.submitFeedback(userId, eventId, rating, comment);
            return Response.status(Response.Status.CREATED)
                           .entity("{\"message\":\"Feedback submitted successfully\"}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // ── PUT update feedback ───────────────────────────────────────
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int feedbackId,
                           @QueryParam("rating") int rating,
                           @QueryParam("comment") String comment) {
        try {
            feedbackBean.updateFeedback(feedbackId, rating, comment);
            return Response.ok("{\"message\":\"Feedback updated successfully\"}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // ── DELETE feedback ───────────────────────────────────────────
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int feedbackId) {
        try {
            feedbackBean.deleteFeedback(feedbackId);
            return Response.ok("{\"message\":\"Feedback deleted successfully\"}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
