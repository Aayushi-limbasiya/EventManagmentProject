/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.EventSchedule;
import Entity.Events;
import Entity.Registrations;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author OS
 */
@Stateless
public class RegistrationBean implements RegistrationBeanLocal {
    
   @PersistenceContext(unitName = "jpu")
     EntityManager em;

    @Override
    public String registerForEvent(int userId, int eventId) {
        // Step 1: Check duplicate registration
        if (isAlreadyRegistered(userId, eventId)) {
            throw new RuntimeException("User is already registered for this event.");
        }

        // Step 2: Get event
        Events event = em.find(Events.class, eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }

        // Step 3: Get event capacity from event_schedule
        TypedQuery<EventSchedule> scheduleQuery = em.createQuery(
            "SELECT s FROM EventSchedule s WHERE s.eventId.eventId = :eventId", EventSchedule.class);
        scheduleQuery.setParameter("eventId", eventId);
        Collection<EventSchedule> schedules = scheduleQuery.getResultList();

        int capacity = 0;
        if (!schedules.isEmpty()) {
            capacity = schedules.iterator().next().getCapacity();
        }

        // Step 4: Count current confirmed registrations
        long confirmed = getConfirmedCount(eventId);

        // Step 5: Decide status
        String status;
        if (capacity == 0 || confirmed < capacity) {
            status = "Confirmed";
        } else {
            status = "Waitlist";
        }

        // Step 6: Create registration
        Registrations reg = new Registrations();
        reg.setEventId(event);
        reg.setUserId(em.find(Users.class, userId));
        reg.setStatus(status);
        reg.setAttendanceStatus(null);
        reg.setRegisteredAt(new Date());
        em.persist(reg);

        return status; // Return status so REST can inform the user
    }

    @Override
    public void cancelRegistration(int registrationId) {
         Registrations reg = em.find(Registrations.class, registrationId);
        if (reg == null) {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }

        int eventId = reg.getEventId().getEventId();
        reg.setStatus("Cancelled");
        em.merge(reg);

        // Promote first waitlisted person to Confirmed
        TypedQuery<Registrations> waitlistQuery =
            em.createNamedQuery("Registrations.getWaitlistByEvent", Registrations.class);
        waitlistQuery.setParameter("eventId", eventId);
        waitlistQuery.setMaxResults(1);
        Collection<Registrations> waitlist = waitlistQuery.getResultList();

        if (!waitlist.isEmpty()) {
            Registrations promoted = waitlist.iterator().next();
            promoted.setStatus("Confirmed");
            em.merge(promoted);
            System.out.println("Waitlist user promoted to Confirmed: " + promoted.getRegistrationId());
        }
    }

    @Override
    public Collection<Registrations> getRegisteredEventsByUser(int userId) {
         TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.findByUser", Registrations.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public Collection<Registrations> getWaitlistByEvent(int eventId) {
        TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.getWaitlistByEvent", Registrations.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public void approveRegistration(int registrationId) {
         Registrations reg = em.find(Registrations.class, registrationId);
        if (reg == null) {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }
        if (!"Pending".equals(reg.getStatus())) {
            throw new RuntimeException("Only Pending registrations can be approved. Current: " + reg.getStatus());
        }
        reg.setStatus("Confirmed");
        em.merge(reg);
    }

    @Override
    public void markAttendance(int registrationId, int eventId) {
         TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.findForCheckIn", Registrations.class);
        q.setParameter("registrationId", registrationId);
        q.setParameter("eventId", eventId);
        Collection<Registrations> result = q.getResultList();

        if (result.isEmpty()) {
            throw new RuntimeException("No registration found for check-in. ID: " + registrationId);
        }

        Registrations reg = result.iterator().next();
        if (!"Confirmed".equals(reg.getStatus())) {
            throw new RuntimeException("Cannot check-in. Registration status is: " + reg.getStatus());
        }

        reg.setAttendanceStatus("Present");
        em.merge(reg);
    }

    @Override
    public void markAbsent(int registrationId) {
         Registrations reg = em.find(Registrations.class, registrationId);
        if (reg != null) {
            reg.setAttendanceStatus("Absent");
            em.merge(reg);
        } else {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }
    }

    @Override
    public Collection<Registrations> getParticipantsByEvent(int eventId) {
          TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.getParticipantsByEvent", Registrations.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public Collection<Registrations> getPendingApprovalsByEvent(int eventId) {
          TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.getPendingApprovalByEvent", Registrations.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public Registrations getRegistrationById(int registrationId) {
         return em.find(Registrations.class, registrationId);
    }

    @Override
    public Collection<Registrations> getAllRegistrationsByEvent(int eventId) {
          TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.findByEvent", Registrations.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public boolean isAlreadyRegistered(int userId, int eventId) {
         TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.checkAlreadyRegistered", Registrations.class);
        q.setParameter("userId", userId);
        q.setParameter("eventId", eventId);
        Collection<Registrations> result = q.getResultList();
        return !result.isEmpty();
    }

    @Override
    public long getConfirmedCount(int eventId) {
         TypedQuery<Long> q =
            em.createNamedQuery("Registrations.countConfirmedByEvent", Long.class);
        q.setParameter("eventId", eventId);
        return q.getSingleResult();
    }

    @Override
    public Collection<Registrations> getByEventAndAttendance(int eventId, String attendanceStatus) {
         TypedQuery<Registrations> q =
            em.createNamedQuery("Registrations.findByEventAndAttendance", Registrations.class);
        q.setParameter("eventId", eventId);
        q.setParameter("attendanceStatus", attendanceStatus);
        return q.getResultList();
    }

    @Override
    public Registrations getRegistrationConfirmation(int registrationId) {
         Registrations reg = em.find(Registrations.class, registrationId);
        if (reg == null) {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }
        return reg;
    }

    @Override
    public String generateQRCodeValue(int registrationId) {
        Registrations reg = em.find(Registrations.class, registrationId);
        if (reg == null) {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }
        return "REG-" + reg.getRegistrationId()
             + "-EVENT-" + reg.getEventId().getEventId()
             + "-USER-" + reg.getUserId().getUserId();
    }
    

}
