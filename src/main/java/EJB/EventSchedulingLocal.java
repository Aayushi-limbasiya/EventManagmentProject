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
   void assignVenue(int eventId, int venueId, Date startTime, Date endTime);
    
    void updateSchedule(EventSchedule schedule);

    EventSchedule getScheduleByEvent(int eventId);

    // 🔹 EXTRA FUNCTIONS
    
    boolean checkVenueAvailability(int venueId, Date startTime, Date endTime);
    
    boolean preventScheduleConflict(int venueId, Date startTime, Date endTime);
    
    void updateCapacity(int scheduleId, int capacity);

    // 🔹 VENUE MANAGEMENT (ADMIN)
    
    void addVenue(Venues venue);
    
    void updateVenue(Venues venue);
    
    void deleteVenue(int venueId);
    
    Collection<Venues> getAllVenues();

    // 🔹 ANALYTICS
    
    Collection<EventSchedule> getVenueUsageHistory(int venueId);
    
    Collection<EventSchedule> getCalendarEvents();
}
