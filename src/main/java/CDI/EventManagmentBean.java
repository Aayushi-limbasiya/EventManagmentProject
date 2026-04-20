/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.EventManagmentLocal;
import Entity.Events;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collection;

@Named("eventBean")
@ViewScoped
public class EventManagmentBean implements Serializable {

    @EJB
    private EventManagmentLocal eventEJB;

    private Collection<Events> eventList;
    private Events selectedEvent = new Events();
    private String keyword;
    private String status;

    // ================================
    // 🔹 LOAD DATA
    // ================================
    public void loadAllEvents() {
        eventList = eventEJB.getAllEvents();
    }

    public void loadUpcomingEvents() {
        eventList = eventEJB.getUpcomingEvents();
    }

    public void loadPastEvents() {
        eventList = eventEJB.getPastEvents();
    }

    public void loadByStatus() {
        if (status != null && !status.isEmpty()) {
            eventList = eventEJB.getEventsByStatus(status);
        }
    }

    public void loadByOrganizer(int userId) {
        eventList = eventEJB.getEventsByOrganizer(userId);
    }

    // ================================
    // 🔍 SEARCH
    // ================================
    public void searchEvents() {
        if (keyword != null && !keyword.trim().isEmpty()) {
            eventList = eventEJB.searchEvents(keyword);
        } else {
            loadAllEvents();
        }
    }

    // ================================
    // ➕ CREATE EVENT
    // ================================
    public void createEvent() {
        eventEJB.createEvent(selectedEvent);
        selectedEvent = new Events(); // reset form
        loadAllEvents();
    }

    // ================================
    // ✏️ UPDATE EVENT
    // ================================
    public void updateEvent() {
        eventEJB.updateEvent(selectedEvent);
        loadAllEvents();
    }

    // ================================
    // 🔄 UPDATE STATUS
    // ================================
    public void updateStatus(int eventId, String status) {
        eventEJB.updateEventStatus(eventId, status);
        loadAllEvents();
    }

    // ================================
    // 🖼️ UPLOAD BANNER
    // ================================
    public void uploadBanner(int eventId, String path) {
        eventEJB.uploadEventBanner(eventId, path);
        loadAllEvents();
    }

    // ================================
    // ❌ DELETE EVENT
    // ================================
    public void deleteEvent(int eventId) {
        eventEJB.deleteEvent(eventId);
        loadAllEvents();
    }

    // ================================
    // 📊 COUNT
    // ================================
    public Long getRegistrationCount(int eventId) {
        return eventEJB.getEventRegistrationCount(eventId);
    }

    // ================================
    // 🔁 GETTERS & SETTERS
    // ================================
    public Collection<Events> getEventList() {
        return eventList;
    }

    public void setEventList(Collection<Events> eventList) {
        this.eventList = eventList;
    }

    public Events getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Events selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}