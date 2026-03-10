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

    // Update Event
    void updateEvent(Events event);

    // Delete Event
    void deleteEvent(Long eventId);

    // View Single Event
    Events getEventById(Long eventId);

    // View All Events
    Collection<Events> getAllEvents();

    // View Events By Organizer
    Collection<Events> getEventsByOrganizer(Long organizerId);
}
