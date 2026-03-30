/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;
import EJB.EventManagmentLocal;
import Entity.Events;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
 * EventManagmentREST
 * REST API for Event Management Module
 * Base URL: http://localhost:8080/EventManagmentSystem/api/events
 * Matches EventManagmentLocal interface exactly
 *
 * IMPORTANT: Static paths declared BEFORE /{id} to avoid Payara 400 error
 */
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventManagmentREST {

    @EJB
    private EventManagmentLocal eventBean;

    // ══════════════════════════════════════════════════════════
    // STATIC GET PATHS — MUST BE BEFORE /{id}
    // ══════════════════════════════════════════════════════════

    // GET /api/events/all
    @GET
    @Path("/all")
    public Response getAllEvents() {
        try {
            Collection<Events> events = eventBean.getAllEvents();
            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/events/search?keyword=tech
    @GET
    @Path("/search")
    public Response searchEvents(@QueryParam("keyword") String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"keyword parameter is required\"}")
                        .build();
            }
            Collection<Events> events = eventBean.searchEvents(keyword);
            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/events/upcoming
    @GET
    @Path("/upcoming")
    public Response getUpcomingEvents() {
        try {
            Collection<Events> events = eventBean.getUpcomingEvents();
            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/events/past
    @GET
    @Path("/past")
    public Response getPastEvents() {
        try {
            Collection<Events> events = eventBean.getPastEvents();
            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/events/status/{status}
    // status values: Draft / Pending / Approved / Completed
    @GET
    @Path("/status/{status}")
    public Response getEventsByStatus(@PathParam("status") String status) {
        try {
            Collection<Events> events = eventBean.getEventsByStatus(status);
            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/events/organizer/{userId}
    @GET
    @Path("/organizer/{userId}")
    public Response getEventsByOrganizer(@PathParam("userId") int userId) {
        try {
            Collection<Events> events = eventBean.getEventsByOrganizer(userId);
            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // DYNAMIC GET PATH — ALWAYS LAST
    // FIX: String instead of int to avoid Payara 400 on /all etc.
    // ══════════════════════════════════════════════════════════

    // GET /api/events/1
    @GET
    @Path("/{id}")
    public Response getEventById(@PathParam("id") String eventIdStr) {
        try {
            int eventId;
            try {
                eventId = Integer.parseInt(eventIdStr);
            } catch (NumberFormatException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Invalid event ID: " + eventIdStr + "\"}")
                        .build();
            }
            Events event = eventBean.getEventById(eventId);
            if (event != null) {
                return Response.ok(event).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Event not found with ID: " + eventId + "\"}")
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

    // POST /api/events/create
    // Body: Events JSON — status auto set to Draft in EJB
    @POST
    @Path("/create")
    public Response createEvent(Events event) {
        try {
            eventBean.createEvent(event);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Event created successfully with status Draft\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // PUT METHODS
    // ══════════════════════════════════════════════════════════

    // PUT /api/events/update
    // Body: Events JSON with eventId included
    @PUT
    @Path("/update")
    public Response updateEvent(Events event) {
        try {
            eventBean.updateEvent(event);
            return Response.ok("{\"message\":\"Event updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /api/events/1/status?status=Pending
    // status values: Draft / Pending / Approved / Completed
    @PUT
    @Path("/{id}/status")
    public Response updateEventStatus(@PathParam("id") int eventId,
                                      @QueryParam("status") String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"status parameter is required\"}")
                        .build();
            }
            eventBean.updateEventStatus(eventId, status);
            return Response.ok("{\"message\":\"Event status updated to: " + status + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /api/events/1/banner?path=uploads/event1.jpg
    @PUT
    @Path("/{id}/banner")
    public Response uploadBanner(@PathParam("id") int eventId,
                                 @QueryParam("path") String imagePath) {
        try {
            if (imagePath == null || imagePath.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"path parameter is required\"}")
                        .build();
            }
            eventBean.uploadEventBanner(eventId, imagePath);
            return Response.ok("{\"message\":\"Banner path saved: " + imagePath + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // DELETE METHOD
    // ══════════════════════════════════════════════════════════

    // DELETE /api/events/1
    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") int eventId) {
        try {
            eventBean.deleteEvent(eventId);
            return Response.ok("{\"message\":\"Event deleted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // STATS
    // ══════════════════════════════════════════════════════════

    // GET /api/events/1/registrations/count
    @GET
    @Path("/{id}/registrations/count")
    public Response getRegistrationCount(@PathParam("id") int eventId) {
        try {
            Long count = eventBean.getEventRegistrationCount(eventId);
            return Response.ok(
                "{\"eventId\":" + eventId + ",\"registrationCount\":" + count + "}"
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
