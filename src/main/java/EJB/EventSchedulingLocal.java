/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.EventSchedule;
import Entity.Venues;
import jakarta.ejb.Local;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author OS
 */
@Local
public interface EventSchedulingLocal {
    // Assign venue and schedule event
    void assignVenue(EventSchedule schedule);

    // Update event schedule
    void updateSchedule(EventSchedule schedule);

    // Delete schedule
    void deleteSchedule(Long scheduleId);

    // Get schedule by event
    Collection<EventSchedule> getScheduleByEvent(Long eventId);

    // Check venue availability
    boolean isVenueAvailable(Long venueId, Date startTime, Date endTime);

    // Get all venues
    Collection<Venues> getAllVenues();

    // Get venue by id
    Venues getVenueById(Long venueId);
}
