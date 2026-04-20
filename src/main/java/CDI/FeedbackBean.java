/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.FeedbackCertificateBeanLocal;
import Entity.Feedback;
import jakarta.ejb.EJB;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.util.*;

@Named("feedbackBean")
@ViewScoped
public class FeedbackBean implements Serializable {

    @EJB
    private FeedbackCertificateBeanLocal feedbackService;

    private List<Feedback> feedbackList = new ArrayList<>();

    private int userId;
    private int eventId;
    private int organizerId;
    private int feedbackId;

    private int rating;
    private String comment;

    private double averageRating;
    private long totalFeedback;

    private Map<Integer, Long> ratingDistribution = new HashMap<>();
    private Map<Integer, Double> organizerAnalytics = new HashMap<>();

    private List<Map<String, Object>> topRatedEvents = new ArrayList<>();

    private boolean alreadySubmitted;

    // ===============================
    // 🔹 LOAD DATA
    // ===============================
    public void loadByEvent() {
        feedbackList = new ArrayList<>(
            feedbackService.getFeedbackByEvent(eventId)
        );
    }

    public void loadByUser() {
        feedbackList = new ArrayList<>(
            feedbackService.getFeedbackByUser(userId)
        );
    }

    public void loadByOrganizer() {
        feedbackList = new ArrayList<>(
            feedbackService.getFeedbackByOrganizer(organizerId)
        );
    }

    // ===============================
    // 🔹 ANALYTICS
    // ===============================
    public void loadAverageRating() {
        averageRating = feedbackService.getAverageRating(eventId);
    }

    public void loadCount() {
        totalFeedback = feedbackService.getFeedbackCount(eventId);
    }

    public void loadDistribution() {
        ratingDistribution = feedbackService.getRatingDistribution(eventId);
    }

    public void loadOrganizerAnalytics() {
        organizerAnalytics = feedbackService.getOrganizerFeedbackAnalytics(organizerId);
    }

    public void loadTopRatedEvents() {
        List<Object[]> report = feedbackService.getEventRatingReport();

        topRatedEvents = new ArrayList<>();
        for (Object[] row : report) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("eventId", row[0]);
            map.put("averageRating", row[1]);
            topRatedEvents.add(map);
        }
    }

    // ===============================
    // 🔹 CHECK
    // ===============================
    public void checkAlreadySubmitted() {
        alreadySubmitted = feedbackService.hasSubmittedFeedback(userId, eventId);
    }

    // ===============================
    // 🔹 SUBMIT
    // ===============================
    public void submit() {
        try {
            feedbackService.submitFeedback(userId, eventId, rating, comment);
            showMessage("Feedback submitted successfully");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 UPDATE
    // ===============================
    public void update() {
        try {
            feedbackService.updateFeedback(feedbackId, rating, comment);
            showMessage("Feedback updated");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Update failed");
        }
    }

    // ===============================
    // 🔹 DELETE
    // ===============================
    public void delete(int id) {
        try {
            feedbackService.deleteFeedback(id);
            showMessage("Feedback deleted");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Delete failed");
        }
    }

    // ===============================
    // 🔹 MESSAGE HELPER
    // ===============================
    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ===============================
    // 🔹 GETTERS & SETTERS
    // ===============================

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public long getTotalFeedback() {
        return totalFeedback;
    }

    public Map<Integer, Long> getRatingDistribution() {
        return ratingDistribution;
    }

    public Map<Integer, Double> getOrganizerAnalytics() {
        return organizerAnalytics;
    }

    public List<Map<String, Object>> getTopRatedEvents() {
        return topRatedEvents;
    }

    public boolean isAlreadySubmitted() {
        return alreadySubmitted;
    }
}
