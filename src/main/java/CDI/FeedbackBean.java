package CDI;

import EJB.EventManagmentLocal;
import EJB.FeedbackCertificateBeanLocal;
import EJB.RegistrationBeanLocal;
import Entity.Events;
import Entity.Feedback;
import Entity.Registrations;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.*;

@Named("feedbackBean")
@ViewScoped
public class FeedbackBean implements Serializable {

    @EJB
    private FeedbackCertificateBeanLocal feedbackService;

    // ✅ Added for feedback pages
    @EJB
    private RegistrationBeanLocal regService;

    @EJB
    private EventManagmentLocal eventService;

    // ✅ Added — injected instead of using raw userId field
    @Inject
    private AuthBean authBean;

    // ─── existing fields ───────────────────────────────────
    private List<Feedback> feedbackList = new ArrayList<>();
    private int userId;
    private int eventId;
    private int organizerId;
    private int feedbackId;
    private int rating;
    private String comment = "";
    private double averageRating;
    private long totalFeedback;
    private Map<Integer, Long>   ratingDistribution  = new HashMap<>();
    private Map<Integer, Double> organizerAnalytics  = new HashMap<>();
    private List<Map<String, Object>> topRatedEvents = new ArrayList<>();
    private boolean alreadySubmitted;

    // ─── NEW: cached lists for feedback pages ─────────────
    private List<Events>   myRegisteredEvents = null;
    private List<Feedback> myFeedbackList     = null;
    private List<Feedback> organizerFeedback  = null;

    // ═══════════════════════════════════════════════════════
    // 🔹 NEW METHODS — used by user_feedback.xhtml
    // ═══════════════════════════════════════════════════════

    /** Read-only email shown in the feedback form */
    public String getUserEmail() {
        if (authBean != null && authBean.getLoggedInUser() != null)
            return authBean.getLoggedInUser().getEmail();
        return "";
    }

    /** Events the logged-in user registered for and hasn't reviewed yet */
    public List<Events> getMyRegisteredEvents() {
        if (myRegisteredEvents == null) {
            myRegisteredEvents = new ArrayList<>();
            try {
                int uid = getLoggedInUserId();
                if (uid > 0) {
                    for (Registrations r : regService.getRegisteredEventsByUser(uid)) {
                        Events ev = r.getEventId();
                        if (ev != null && !feedbackService.hasSubmittedFeedback(uid, ev.getEventId())) {
                            myRegisteredEvents.add(ev);
                        }
                    }
                }
            } catch (Exception e) {
                showMessage("Could not load events: " + e.getMessage());
            }
        }
        return myRegisteredEvents;
    }

    /** All feedback submitted by the logged-in user */
    public List<Feedback> getMyFeedbackList() {
        if (myFeedbackList == null) {
            myFeedbackList = new ArrayList<>();
            try {
                int uid = getLoggedInUserId();
                if (uid > 0)
                    myFeedbackList = new ArrayList<>(feedbackService.getFeedbackByUser(uid));
            } catch (Exception ignored) {}
        }
        return myFeedbackList;
    }

    /** All feedback received by the logged-in organizer */
    public List<Feedback> getOrganizerFeedback() {
        if (organizerFeedback == null) {
            organizerFeedback = new ArrayList<>();
            try {
                int uid = getLoggedInUserId();
                if (uid > 0)
                    organizerFeedback = new ArrayList<>(feedbackService.getFeedbackByOrganizer(uid));
            } catch (Exception ignored) {}
        }
        return organizerFeedback;
    }

    /** Selected event ID field (bound to dropdown in feedback form) */
    public int getSelectedEventId()        { return eventId; }
    public void setSelectedEventId(int id) { this.eventId = id; }

    /** Submit feedback from the user feedback page — with full validation */
    public String submitFeedback() {
        // Validate
        if (eventId <= 0) {
            showMessage("Please select an event.");
            return null;
        }
        if (rating < 1 || rating > 5) {
            showMessage("Please select a star rating (1–5).");
            return null;
        }
        if (comment == null || comment.trim().isEmpty()) {
            showMessage("Please write your feedback before submitting.");
            return null;
        }
        if (comment.trim().length() < 10) {
            showMessage("Feedback must be at least 10 characters.");
            return null;
        }
        if (comment.trim().length() > 1000) {
            showMessage("Feedback cannot exceed 1000 characters.");
            return null;
        }

        int uid = getLoggedInUserId();
        if (uid <= 0) { showMessage("Session expired. Please log in again."); return null; }

        if (feedbackService.hasSubmittedFeedback(uid, eventId)) {
            showMessage("You have already submitted feedback for this event.");
            return null;
        }

        try {
            feedbackService.submitFeedback(uid, eventId, rating, comment.trim());
            return "user_feedback?faces-redirect=true&submitted=true";
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
            return null;
        }
    }

    // ═══════════════════════════════════════════════════════
    // 🔹 ORIGINAL METHODS — kept exactly as they were
    // ═══════════════════════════════════════════════════════

    public void loadByEvent() {
        feedbackList = new ArrayList<>(feedbackService.getFeedbackByEvent(eventId));
    }

    public void loadByUser() {
        feedbackList = new ArrayList<>(feedbackService.getFeedbackByUser(userId));
    }

    public void loadByOrganizer() {
        feedbackList = new ArrayList<>(feedbackService.getFeedbackByOrganizer(organizerId));
    }

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

    public void checkAlreadySubmitted() {
        alreadySubmitted = feedbackService.hasSubmittedFeedback(userId, eventId);
    }

    public void submit() {
        try {
            feedbackService.submitFeedback(userId, eventId, rating, comment);
            showMessage("Feedback submitted successfully");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    public void update() {
        try {
            feedbackService.updateFeedback(feedbackId, rating, comment);
            showMessage("Feedback updated");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Update failed");
        }
    }

    public void delete(int id) {
        try {
            feedbackService.deleteFeedback(id);
            showMessage("Feedback deleted");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Delete failed");
        }
    }

    // ─── Helper ────────────────────────────────────────────
    private int getLoggedInUserId() {
        if (authBean != null && authBean.getLoggedInUser() != null)
            return authBean.getLoggedInUser().getUserId();
        return 0;
    }

    private void showMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
    }

    // ─── Getters / Setters (all original ones kept) ────────
    public List<Feedback> getFeedbackList()            { return feedbackList; }
    public int    getUserId()                          { return userId; }
    public void   setUserId(int userId)                { this.userId = userId; }
    public int    getEventId()                         { return eventId; }
    public void   setEventId(int eventId)              { this.eventId = eventId; }
    public int    getOrganizerId()                     { return organizerId; }
    public void   setOrganizerId(int organizerId)      { this.organizerId = organizerId; }
    public int    getFeedbackId()                      { return feedbackId; }
    public void   setFeedbackId(int feedbackId)        { this.feedbackId = feedbackId; }
    public int    getRating()                          { return rating; }
    public void   setRating(int rating)                { this.rating = rating; }
    public String getComment()                         { return comment; }
    public void   setComment(String comment)           { this.comment = comment; }
    public double getAverageRating()                   { return averageRating; }
    public long   getTotalFeedback()                   { return totalFeedback; }
    public Map<Integer, Long>          getRatingDistribution()  { return ratingDistribution; }
    public Map<Integer, Double>        getOrganizerAnalytics()  { return organizerAnalytics; }
    public List<Map<String, Object>>   getTopRatedEvents()      { return topRatedEvents; }
    public boolean isAlreadySubmitted()                { return alreadySubmitted; }
}