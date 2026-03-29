/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Certificates;
import Entity.Events;
import Entity.Feedback;
import Entity.Registrations;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
//import static jakarta.persistence.GenerationType.UUID;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author OS
 */
@Stateless
public class FeedbackCertificateBean implements FeedbackCertificateBeanLocal {

     @PersistenceContext(unitName = "jpu")
    EntityManager em;
    
    @Override
    public void submitFeedback(int userId, int eventId, int rating, String comment) {
          // Validate event
        Events event = em.find(Events.class, eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }

        // Validate user
        Users user = em.find(Users.class, userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Validate rating range
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5. Provided: " + rating);
        }

        // Check duplicate feedback
        if (hasSubmittedFeedback(userId, eventId)) {
            throw new RuntimeException("Feedback already submitted for this event by user ID: " + userId);
        }

        // Create and save feedback
        Feedback feedback = new Feedback();
        feedback.setEventId(event);
        feedback.setUserId(user);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setCreatedAt(new Date());
        em.persist(feedback);
    }

    @Override
    public void updateFeedback(int feedbackId, int rating, String comment) {
           Feedback feedback = em.find(Feedback.class, feedbackId);
        if (feedback == null) {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5. Provided: " + rating);
        }
        feedback.setRating(rating);
        feedback.setComment(comment);
        em.merge(feedback);
    }

    @Override
    public void deleteFeedback(int feedbackId) {
         Feedback feedback = em.find(Feedback.class, feedbackId);
        if (feedback != null) {
            em.remove(feedback);
        } else {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }
    }

    @Override
    public Feedback getFeedbackById(int feedbackId) {
        return em.find(Feedback.class, feedbackId);
    }

    @Override
    public Collection<Feedback> getFeedbackByEvent(int eventId) {
         TypedQuery<Feedback> q =
            em.createNamedQuery("Feedback.findByEvent", Feedback.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public Collection<Feedback> getFeedbackByUser(int userId) {
         TypedQuery<Feedback> q =
            em.createNamedQuery("Feedback.findByUser", Feedback.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public double getAverageRating(int eventId) {
          TypedQuery<Double> q =
            em.createNamedQuery("Feedback.getAverageRatingByEvent", Double.class);
        q.setParameter("eventId", eventId);
        Double avg = q.getSingleResult();
        return avg != null ? avg : 0.0;
    }

    @Override
    public long getFeedbackCount(int eventId) {
         TypedQuery<Long> q =
            em.createNamedQuery("Feedback.countByEvent", Long.class);
        q.setParameter("eventId", eventId);
        return q.getSingleResult();
    }

    @Override
    public Map<Integer, Long> getRatingDistribution(int eventId) {
          TypedQuery<Object[]> q =
            em.createNamedQuery("Feedback.getRatingDistribution", Object[].class);
        q.setParameter("eventId", eventId);
        List<Object[]> results = q.getResultList();

        Map<Integer, Long> distribution = new HashMap<>();
        // Initialize all ratings 1-5 with 0
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        // Fill in actual counts
        for (Object[] row : results) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            distribution.put(rating, count);
        }
        return distribution;
    }

    @Override
    public Map<Integer, Double> getOrganizerFeedbackAnalytics(int organizerId) {
         TypedQuery<Events> eventQuery = em.createQuery(
            "SELECT e FROM Events e WHERE e.userId.userId = :organizerId", Events.class);
        eventQuery.setParameter("organizerId", organizerId);
        Collection<Events> events = eventQuery.getResultList();

        Map<Integer, Double> analytics = new HashMap<>();
        for (Events event : events) {
            double avg = getAverageRating(event.getEventId());
            analytics.put(event.getEventId(), avg);
        }
        return analytics;
    }

    @Override
    public Collection<Feedback> getFeedbackByOrganizer(int organizerId) {
         TypedQuery<Feedback> q =
            em.createNamedQuery("Feedback.findByOrganizer", Feedback.class);
        q.setParameter("organizerId", organizerId);
        return q.getResultList();
    }

    @Override
    public List<Object[]> getEventRatingReport() {
         TypedQuery<Object[]> q =
            em.createNamedQuery("Feedback.getTopRatedEvents", Object[].class);
        return q.getResultList();
    }

    @Override
    public boolean hasSubmittedFeedback(int userId, int eventId) {
         TypedQuery<Feedback> q =
            em.createNamedQuery("Feedback.checkAlreadySubmitted", Feedback.class);
        q.setParameter("userId", userId);
        q.setParameter("eventId", eventId);
        return !q.getResultList().isEmpty();
    }

    @Override
    public void generateCertificate(int registrationId) {
        
        // Step 1: Get registration
        Registrations reg = em.find(Registrations.class, registrationId);
        if (reg == null) {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }

        // Step 2: Check registration is Confirmed
        if (!"Confirmed".equals(reg.getStatus())) {
            throw new RuntimeException("Certificate can only be issued for Confirmed registrations. Current: "
                + reg.getStatus());
        }

        // Step 3: Check not already issued
        if (isCertificateIssued(registrationId)) {
            throw new RuntimeException("Certificate already issued for registration ID: " + registrationId);
        }

        // Step 4: Generate unique certificate number
        int eventId = reg.getEventId().getEventId();
        int userId  = reg.getUserId().getUserId();
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String certNumber = "CERT-" + eventId + "-" + userId + "-" + uniquePart;

        // Step 5: Create and save certificate
        Certificates cert = new Certificates();
        cert.setRegistrationId(reg);
        cert.setCertificateNumber(certNumber);
        cert.setIssueDate(new Date());
        cert.setCertificateFile(null); // File path set later after PDF generation
        em.persist(cert);

        System.out.println("Certificate issued: " + certNumber
            + " for Registration ID: " + registrationId);
    }

    @Override
    public void generateCertificatesForEvent(int eventId) {
         TypedQuery<Registrations> q = em.createQuery(
            "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.status = 'Confirmed'",
            Registrations.class);
        q.setParameter("eventId", eventId);
        Collection<Registrations> registrations = q.getResultList();

        if (registrations.isEmpty()) {
            System.out.println("No confirmed participants found for event ID: " + eventId);
            return;
        }

        int issued = 0;
        int skipped = 0;
        for (Registrations reg : registrations) {
            if (!isCertificateIssued(reg.getRegistrationId())) {
                generateCertificate(reg.getRegistrationId());
                issued++;
            } else {
                skipped++;
            }
        }
        System.out.println("Certificates generated for event ID: " + eventId
            + " | Issued: " + issued + " | Skipped (already issued): " + skipped);
    }

    @Override
    public Certificates getCertificateById(int certificateId) {
          return em.find(Certificates.class, certificateId);
    }

    @Override
    public Certificates getCertificateByRegistration(int registrationId) {
         TypedQuery<Certificates> q =
            em.createNamedQuery("Certificates.findByRegistration", Certificates.class);
        q.setParameter("registrationId", registrationId);
        Collection<Certificates> result = q.getResultList();
        return result.isEmpty() ? null : result.iterator().next();
    }

    @Override
    public Certificates verifyCertificate(String certificateNumber) {
          TypedQuery<Certificates> q =
            em.createNamedQuery("Certificates.verifyCertificate", Certificates.class);
        q.setParameter("certificateNumber", certificateNumber);
        Collection<Certificates> result = q.getResultList();
        if (result.isEmpty()) {
            System.out.println("Certificate verification FAILED for number: " + certificateNumber);
            return null;
        }
        System.out.println("Certificate verification SUCCESS for number: " + certificateNumber);
        return result.iterator().next();
    }

    @Override
    public Collection<Certificates> getCertificatesByUser(int userId) {
         TypedQuery<Certificates> q =
            em.createNamedQuery("Certificates.findByUser", Certificates.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public Collection<Certificates> getCertificatesByEvent(int eventId) {
         TypedQuery<Certificates> q =
            em.createNamedQuery("Certificates.findByEvent", Certificates.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public void updateCertificateFile(int certificateId, String filePath) {
          Certificates cert = em.find(Certificates.class, certificateId);
        if (cert == null) {
            throw new RuntimeException("Certificate not found with ID: " + certificateId);
        }
        cert.setCertificateFile(filePath);
        em.merge(cert);
    }

    @Override
    public long getCertificateCount(int eventId) {
           TypedQuery<Long> q =
            em.createNamedQuery("Certificates.countByEvent", Long.class);
        q.setParameter("eventId", eventId);
        return q.getSingleResult();
    }

    @Override
    public boolean isCertificateIssued(int registrationId) {
           TypedQuery<Certificates> q =
            em.createNamedQuery("Certificates.checkAlreadyIssued", Certificates.class);
        q.setParameter("registrationId", registrationId);
        return !q.getResultList().isEmpty();
    }
    
}
