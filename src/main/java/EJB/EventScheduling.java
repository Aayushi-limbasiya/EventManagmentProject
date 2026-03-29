/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.EventSchedule;
import Entity.Events;
import Entity.Venues;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author OS
 */
@Stateless
public class EventScheduling implements EventSchedulingLocal {
    
    @PersistenceContext(unitName= "jpu")
    EntityManager em;

    @Override
    public void assignVenue(int eventId, int venueId, Date startTime, Date endTime) {
         if (!checkVenueAvailability(venueId, startTime, endTime)) {
            throw new RuntimeException("Venue not available!");
        }

        EventSchedule schedule = new EventSchedule();

        Events event = em.find(Events.class, eventId);
        Venues venue = em.find(Venues.class, venueId);

        schedule.setEventId(event);
        schedule.setVenueId(venue);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        em.persist(schedule);
    }

    @Override
    public void updateSchedule(EventSchedule schedule) {
        em.merge(schedule);
    }

    @Override
    public EventSchedule getScheduleByEvent(int eventId) {
        TypedQuery<EventSchedule> q = em.createQuery(
                "SELECT s FROM EventSchedule s WHERE s.eventId.eventId = :eid",
                EventSchedule.class);

        q.setParameter("eid", eventId);

        return q.getResultList().isEmpty() ? null : q.getResultList().get(0);
    }

    @Override
    public boolean checkVenueAvailability(int venueId, Date startTime, Date endTime) {
          TypedQuery<EventSchedule> q = em.createQuery(
            "SELECT s FROM EventSchedule s WHERE s.venueId.venueId = :vid " +
            "AND (s.startTime < :endTime AND s.endTime > :startTime)",
            EventSchedule.class);

        q.setParameter("vid", venueId);
        q.setParameter("startTime", startTime);
        q.setParameter("endTime", endTime);

        return q.getResultList().isEmpty();
    }

    @Override
    public boolean preventScheduleConflict(int venueId, Date startTime, Date endTime) {
                return checkVenueAvailability(venueId, startTime, endTime);
    }

    @Override
    public void updateCapacity(int scheduleId, int capacity) {
          EventSchedule schedule = em.find(EventSchedule.class, scheduleId);

        if (schedule != null) {
            schedule.setCapacity(capacity);
            em.merge(schedule);
        }
    }

    @Override
    public void addVenue(Venues venue) {
        em.persist(venue);
    }

    @Override
    public void updateVenue(Venues venue) {
        em.merge(venue);
    }

    @Override
    public void deleteVenue(int venueId) {
        Venues v = em.find(Venues.class, venueId);

        if (v != null) {
            em.remove(v);
        }
    }

    @Override
    public Collection<Venues> getAllVenues() {
         TypedQuery<Venues> q = em.createQuery(
                "SELECT v FROM Venues v", Venues.class);

        return q.getResultList();
    }

    @Override
    public Collection<EventSchedule> getVenueUsageHistory(int venueId) {
           TypedQuery<EventSchedule> q = em.createQuery(
            "SELECT s FROM EventSchedule s WHERE s.venueId.venueId = :vid",
            EventSchedule.class);

        q.setParameter("vid", venueId);

        return q.getResultList();
    }

    @Override
    public Collection<EventSchedule> getCalendarEvents() {
         TypedQuery<EventSchedule> q = em.createQuery(
                "SELECT s FROM EventSchedule s ORDER BY s.startTime",
                EventSchedule.class);

        return q.getResultList();
    }

   
}
