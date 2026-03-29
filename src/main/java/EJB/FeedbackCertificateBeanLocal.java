/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Certificates;
import Entity.Feedback;
import jakarta.ejb.Local;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author OS
 */
@Local
public interface FeedbackCertificateBeanLocal {
   
    void submitFeedback(int userId, int eventId, int rating, String comment);

    /**
     * Update existing feedback (Participant)
     */
    void updateFeedback(int feedbackId, int rating, String comment);

    /**
     * Delete feedback by ID (Admin)
     */
    void deleteFeedback(int feedbackId);

    /**
     * Get feedback by ID
     */
    Feedback getFeedbackById(int feedbackId);

    /**
     * Get all feedback for a specific event (Organizer / Admin)
     */
    Collection<Feedback> getFeedbackByEvent(int eventId);

    /**
     * Get all feedback submitted by a user (Participant)
     */
    Collection<Feedback> getFeedbackByUser(int userId);

   
    double getAverageRating(int eventId);

    long getFeedbackCount(int eventId);

    /**
     * Get rating distribution for an event
     * Returns Map: {1=count, 2=count, 3=count, 4=count, 5=count}
     * Used for organizer analytics chart
     */
    Map<Integer, Long> getRatingDistribution(int eventId);

    /**
     * Get feedback analytics for all events of an organizer
     * Returns Map: {eventId → averageRating}
     */
    Map<Integer, Double> getOrganizerFeedbackAnalytics(int organizerId);

    /**
     * Get all feedback for events managed by an organizer
     */
    Collection<Feedback> getFeedbackByOrganizer(int organizerId);

    /**
     * Get event rating report (all events with avg rating)
     * Returns List of Object[] {eventId, avgRating}
     * Ordered by highest rated first
     */
    List<Object[]> getEventRatingReport();

    /**
     * Check if user already submitted feedback for an event
     */
    boolean hasSubmittedFeedback(int userId, int eventId);

    // ════════════════════════════════════════════════
    // CERTIFICATE - BASIC FUNCTIONS
    // ════════════════════════════════════════════════

    /**
     * Auto generate certificate for a registration
     * Called after event is marked Completed
     * Generates unique certificate number
     * Sets issueDate = today
     */
    void generateCertificate(int registrationId);

    /**
     * Auto generate certificates for ALL confirmed participants of an event
     * Called when admin marks event as Completed
     */
    void generateCertificatesForEvent(int eventId);

    /**
     * Get certificate by ID
     */
    Certificates getCertificateById(int certificateId);

    /**
     * Get certificate by registration ID
     */
    Certificates getCertificateByRegistration(int registrationId);

    // ════════════════════════════════════════════════
    // CERTIFICATE - EXTRA FUNCTIONS
    // ════════════════════════════════════════════════

    /**
     * Verify certificate using certificate number (public)
     * Returns certificate details if valid, null if not found
     */
    Certificates verifyCertificate(String certificateNumber);

    /**
     * Get all certificates for a user (Participant download list)
     */
    Collection<Certificates> getCertificatesByUser(int userId);

    /**
     * Get all certificates issued for an event (Admin / Organizer)
     */
    Collection<Certificates> getCertificatesByEvent(int eventId);

    /**
     * Update certificate file path after PDF is generated
     * Called after actual PDF file is created and stored
     */
    void updateCertificateFile(int certificateId, String filePath);

    /**
     * Get count of certificates issued for an event
     */
    long getCertificateCount(int eventId);

    /**
     * Check if certificate already issued for a registration
     */
    boolean isCertificateIssued(int registrationId);
}
