/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;

import EJB.EventSchedulingLocal;
import Entity.EventSchedule;
import Entity.Venues;
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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * EventSchedulingREST
 * REST API for Event Scheduling Module
 * Base URL: http://localhost:8080/EventManagmentSystem/api/schedule
 * Matches EventSchedulingLocal interface exactly
 *
 * IMPORTANT: Static paths declared BEFORE /{id} to avoid Payara 400 error
 */
@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventSchedulingREST {

    @EJB
    private EventSchedulingLocal scheduleBean;

    // ══════════════════════════════════════════════════════════
    // STATIC GET PATHS — MUST BE BEFORE /{id}
    // ══════════════════════════════════════════════════════════

    // GET /api/schedule/calendar
    // Get all scheduled events sorted by start time (calendar view)
    @GET
    @Path("/calendar")
    public Response getCalendarEvents() {
        try {
            Collection<EventSchedule> schedules = scheduleBean.getCalendarEvents();
            return Response.ok(schedules).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/schedule/venues/all
    // Get all venues (Admin / Organizer)
    @GET
    @Path("/venues/all")
    public Response getAllVenues() {
        try {
            Collection<Venues> venues = scheduleBean.getAllVenues();
            return Response.ok(venues).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/schedule/event/{eventId}
    // Get schedule for a specific event
    @GET
    @Path("/event/{eventId}")
    public Response getScheduleByEvent(@PathParam("eventId") int eventId) {
        try {
            EventSchedule schedule = scheduleBean.getScheduleByEvent(eventId);
            if (schedule != null) {
                return Response.ok(schedule).build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No schedule found for event ID: " + eventId + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/schedule/venue/{venueId}/history
    // Get usage history for a venue
    @GET
    @Path("/venue/{venueId}/history")
    public Response getVenueUsageHistory(@PathParam("venueId") int venueId) {
        try {
            Collection<EventSchedule> history = scheduleBean.getVenueUsageHistory(venueId);
            return Response.ok(history).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // GET /api/schedule/venue/{venueId}/available
    // Check if venue is available for given time slot
    // Query params: startTime=2025-06-01 10:00:00 &endTime=2025-06-01 18:00:00
    @GET
    @Path("/venue/{venueId}/available")
    public Response checkVenueAvailability(@PathParam("venueId") int venueId,
                                           @QueryParam("startTime") String startTimeStr,
                                           @QueryParam("endTime") String endTimeStr) {
        try {
            if (startTimeStr == null || endTimeStr == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"startTime and endTime query params are required\"}")
                        .build();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = sdf.parse(startTimeStr);
            Date endTime   = sdf.parse(endTimeStr);

            boolean available = scheduleBean.checkVenueAvailability(venueId, startTime, endTime);

            return Response.ok(
                "{\"venueId\":" + venueId +
                ",\"available\":" + available +
                ",\"message\":\"" + (available ? "Venue is available" : "Venue is NOT available — conflict exists") + "\"}"
            ).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + ". Use format: yyyy-MM-dd HH:mm:ss\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // SCHEDULE METHODS — POST + PUT
    // ══════════════════════════════════════════════════════════

    // POST /api/schedule/assign
    // Assign venue to event — creates schedule
    // Query params: eventId, venueId, startTime, endTime
    @POST
    @Path("/assign")
    public Response assignVenue(@QueryParam("eventId") int eventId,
                                @QueryParam("venueId") int venueId,
                                @QueryParam("startTime") String startTimeStr,
                                @QueryParam("endTime") String endTimeStr) {
        try {
            if (startTimeStr == null || endTimeStr == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"startTime and endTime are required\"}")
                        .build();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = sdf.parse(startTimeStr);
            Date endTime   = sdf.parse(endTimeStr);

            // EJB checks venue availability automatically before assigning
            scheduleBean.assignVenue(eventId, venueId, startTime, endTime);

            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Venue assigned successfully. Schedule created.\"}")
                    .build();
        } catch (RuntimeException e) {
            // Catches "Venue not available!" from EJB
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + ". Use format: yyyy-MM-dd HH:mm:ss\"}")
                    .build();
        }
    }

    // PUT /api/schedule/update
    // Update existing schedule
    // Body: EventSchedule JSON with scheduleId
    @PUT
    @Path("/update")
    public Response updateSchedule(EventSchedule schedule) {
        try {
            scheduleBean.updateSchedule(schedule);
            return Response.ok("{\"message\":\"Schedule updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /api/schedule/{scheduleId}/capacity?capacity=200
    // Update capacity for a schedule
    @PUT
    @Path("/{scheduleId}/capacity")
    public Response updateCapacity(@PathParam("scheduleId") int scheduleId,
                                   @QueryParam("capacity") int capacity) {
        try {
            if (capacity <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"capacity must be greater than 0\"}")
                        .build();
            }
            scheduleBean.updateCapacity(scheduleId, capacity);
            return Response.ok("{\"message\":\"Capacity updated to: " + capacity + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // ══════════════════════════════════════════════════════════
    // VENUE MANAGEMENT — Admin only
    // ══════════════════════════════════════════════════════════

    // POST /api/schedule/venues/add
    // Admin adds a new venue
    // Body: Venues JSON
    @POST
    @Path("/venues/add")
    public Response addVenue(Venues venue) {
        try {
            scheduleBean.addVenue(venue);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"Venue added successfully\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // PUT /api/schedule/venues/update
    // Admin updates venue details
    // Body: Venues JSON with venueId
    @PUT
    @Path("/venues/update")
    public Response updateVenue(Venues venue) {
        try {
            scheduleBean.updateVenue(venue);
            return Response.ok("{\"message\":\"Venue updated successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // DELETE /api/schedule/venues/{venueId}
    // Admin deletes a venue
    @DELETE
    @Path("/venues/{venueId}")
    public Response deleteVenue(@PathParam("venueId") int venueId) {
        try {
            scheduleBean.deleteVenue(venueId);
            return Response.ok("{\"message\":\"Venue deleted successfully\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
