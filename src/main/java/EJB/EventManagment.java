/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Events;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Collection;

/**
 *
 * @author OS
 */
@Stateless
public class EventManagment implements EventManagmentLocal {
    
    @PersistenceContext(unitName= "jpu")
    EntityManager em;

    @Override
    public void createEvent(Events event) {
         em.persist(event);
    }

    @Override
    public void updateEvent(Events event) {
                em.merge(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
       Events event = em.find(Events.class, eventId);
        if(event != null){
            em.remove(event);
        }
    }

    @Override
    public Events getEventById(Long eventId) {
       Query q = em.createNamedQuery("Events.findByEventId");
        q.setParameter("eventId", eventId);

        return (Events) q.getSingleResult();
    }

    @Override
    public Collection<Events> getAllEvents() {
         Query q = em.createNamedQuery("Events.findAll");

        return q.getResultList();
    }

    @Override
    public Collection<Events> getEventsByOrganizer(Long organizerId) {
         Query q = em.createNamedQuery("Events.findByOrganizerId");
        q.setParameter("organizerId", organizerId);

        return q.getResultList();
    }

}
