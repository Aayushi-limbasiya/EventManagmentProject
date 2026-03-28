/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;

//import EJB.EventManagementLocal;
import EJB.EventManagmentLocal;
import Entity.Events;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;

@Path("events")
public class EventRest {

    @EJB
    EventManagmentLocal eventBean;

    // ✅ CREATE EVENT
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createEvent(Events event) {
        eventBean.createEvent(event);
    }

    // ✅ GET ALL EVENTS
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Events> getAllEvents() {
        return eventBean.getAllEvents();
    }

    // ✅ GET EVENT BY ID
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Events getEvent(@PathParam("id") int id) {
        return eventBean.getEventById(id);
    }

    // ✅ UPDATE EVENT
    @PUT
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateEvent(Events event) {
        eventBean.updateEvent(event);
    }

    // ✅ DELETE EVENT
    @DELETE
    @Path("delete/{id}")
    public void deleteEvent(@PathParam("id") int id) {
        eventBean.deleteEvent(id);
    }

    // ✅ SEARCH EVENTS
    @GET
    @Path("search/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Events> searchEvents(@PathParam("keyword") String keyword) {
        return eventBean.searchEvents(keyword);
    }

    // ✅ FILTER BY STATUS
    @GET
    @Path("status/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Events> getByStatus(@PathParam("status") String status) {
        return eventBean.getEventsByStatus(status);
    }

    // ✅ EVENTS BY ORGANIZER
    @GET
    @Path("organizer/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Events> getByOrganizer(@PathParam("userId") int userId) {
        return eventBean.getEventsByOrganizer(userId);
    }

    // ✅ UPDATE STATUS
    @PUT
    @Path("status/{id}/{status}")
    public void updateStatus(@PathParam("id") int id,
                             @PathParam("status") String status) {
        eventBean.updateEventStatus(id, status);
    }

    // ✅ UPCOMING EVENTS
    @GET
    @Path("upcoming")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Events> upcomingEvents() {
        return eventBean.getUpcomingEvents();
    }

    // ✅ PAST EVENTS
    @GET
    @Path("past")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Events> pastEvents() {
        return eventBean.getPastEvents();
    }

    // ✅ REGISTRATION COUNT
    @GET
    @Path("count/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Long getRegistrationCount(@PathParam("id") int id) {
        return eventBean.getEventRegistrationCount(id);
    }
}
