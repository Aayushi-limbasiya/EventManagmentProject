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
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;

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
        // ✅ Use getReference so JPA gets a managed proxy — avoids "detached entity" error
        if (event.getUserId() != null && event.getUserId().getUserId() != null) {
            Entity.Users managedUser = em.getReference(Entity.Users.class, event.getUserId().getUserId());
            event.setUserId(managedUser);
        }
        // ✅ Only set status if caller hasn't already set one (e.g. "Pending" from organizer)
        if (event.getStatus() == null || event.getStatus().trim().isEmpty()) {
            event.setStatus("Pending");
        }
        event.setCreatedAt(new java.util.Date());
        em.persist(event);
    }

    @Override
    public void updateEvent(Events event) {
         em.merge(event);
    }

    @Override
    public void deleteEvent(int eventId) {
        Events e = em.find(Events.class, eventId);
        if (e != null) {
            em.remove(e);
        }
    }

    @Override
    public Events getEventById(int eventId) {
       return em.find(Events.class, eventId);
    }

    @Override
    public Collection<Events> getAllEvents() {
         TypedQuery<Events> q = em.createNamedQuery("Events.findAll", Events.class);
        return q.getResultList();
    }

    @Override
    public void updateEventStatus(int eventId, String status) {
         Events e = em.find(Events.class, eventId);
        if (e != null) {
            e.setStatus(status);
            em.merge(e);
        }
    }

    @Override
    public Collection<Events> searchEvents(String keyword) {
         TypedQuery<Events> q = em.createQuery(
            "SELECT e FROM Events e WHERE e.title LIKE :kw OR e.description LIKE :kw",
            Events.class
        );
        q.setParameter("kw", "%" + keyword + "%");
        return q.getResultList();
    }

    @Override
    public Collection<Events> getEventsByStatus(String status) {
        TypedQuery<Events> q = em.createNamedQuery("Events.findByStatus", Events.class);
        q.setParameter("status", status);
        return q.getResultList();
    }

    @Override
    public Collection<Events> getEventsByOrganizer(int userId) {
        TypedQuery<Events> q = em.createQuery(
            "SELECT e FROM Events e WHERE e.userId.userId = :uid",
            Events.class
        );
        q.setParameter("uid", userId);
        return q.getResultList();
    }

    @Override
    public Collection<Events> getUpcomingEvents() {
       TypedQuery<Events> q = em.createQuery(
            "SELECT e FROM Events e WHERE e.createdAt >= :today",
            Events.class
        );
        q.setParameter("today", new Date());
        return q.getResultList();
    }

    @Override
    public Collection<Events> getPastEvents() {
        TypedQuery<Events> q = em.createQuery(
            "SELECT e FROM Events e WHERE e.createdAt < :today",
            Events.class
        );
        q.setParameter("today", new Date());
        return q.getResultList();
    }

    @Override
    public void uploadEventBanner(int eventId, String imagePath) {
         Events e = em.find(Events.class, eventId);
        if (e != null) {
            // You can add column in DB later if needed
            // e.setBanner(imagePath);
            em.merge(e);
        }
    }

    @Override
    public Long getEventRegistrationCount(int eventId) {
         Long count = em.createQuery(
            "SELECT COUNT(r) FROM Registrations r WHERE r.eventId.eventId = :eid",
            Long.class
        ).setParameter("eid", eventId)
         .getSingleResult();

        return count;
    }

    
}
