/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Events;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author OS
 */
@Local
public interface EventManagmentLocal {
    
    void createEvent(Events event);
    void updateEvent(Events event);
    void deleteEvent(int eventId);
    Events getEventById(int eventId);
    Collection<Events> getAllEvents();

    // Status Management
    void updateEventStatus(int eventId, String status);

    // Search & Filter
    Collection<Events> searchEvents(String keyword);
    Collection<Events> getEventsByStatus(String status);
    Collection<Events> getEventsByOrganizer(int userId);

    // Upcoming & Past
    Collection<Events> getUpcomingEvents();
    Collection<Events> getPastEvents();

    // Extra Features
    void uploadEventBanner(int eventId, String imagePath);
    Long getEventRegistrationCount(int eventId);
    
}



