/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.EventSchedule;
import Entity.Venues;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
    public void assignVenue(EventSchedule schedule) {
         em.persist(schedule);
    }

    @Override
    public void updateSchedule(EventSchedule schedule) {
          em.merge(schedule);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
         EventSchedule schedule = em.find(EventSchedule.class, scheduleId);

        if(schedule != null){
            em.remove(schedule);
        }
    }

    @Override
    public Collection<EventSchedule> getScheduleByEvent(Long eventId) {
        
        Query q = em.createNamedQuery("EventSchedule.findByEventId");
        q.setParameter("eventId", eventId);

        return q.getResultList();
    }

    @Override
    public boolean isVenueAvailable(Long venueId, Date startTime, Date endTime) {
       Query q = em.createNamedQuery("EventSchedule.checkVenueAvailability");

        q.setParameter("venueId", venueId);
        q.setParameter("startTime", startTime);
        q.setParameter("endTime", endTime);

        return q.getResultList().isEmpty();
    }

    @Override
    public Collection<Venues> getAllVenues() {
        Query q = em.createNamedQuery("Venues.findAll");

        return q.getResultList();
    }

    @Override
    public Venues getVenueById(Long venueId) {
       Query q = em.createNamedQuery("Venues.findByVenueId");
        q.setParameter("venueId", venueId);

        return (Venues) q.getSingleResult();
    }

}
