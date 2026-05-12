package CDI;

import EJB.EventManagmentLocal;
import EJB.RegistrationBeanLocal;
import Entity.Registrations;
import Entity.Events;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * CDI Bean for User — My Registrations + Browse Events pages.
 */
@Named("userRegBean")
@ViewScoped
public class UserRegBean implements Serializable {

    @EJB
    private RegistrationBeanLocal registrationService;

    @EJB
    private EventManagmentLocal eventService;

    // ✅ Use @Inject — CDI beans are NOT in the HTTP session map by EL name
    @Inject
    private AuthBean authBean;

    private List<Registrations> myRegistrations = new ArrayList<>();
    private List<Events>        allEvents        = new ArrayList<>();

    // ── Load all registrations for the currently logged-in user ──────────────
    public void loadMyRegistrations() {
        try {
            int uid = getLoggedInUserId();
            if (uid > 0) {
                myRegistrations = new ArrayList<>(
                    registrationService.getRegisteredEventsByUser(uid));
            }
        } catch (Exception e) {
            showMessage("Could not load your registrations: " + e.getMessage());
        }
    }

    // ── Load all approved events for browse page ──────────────────────────────
    public void loadAllEvents() {
        try {
            Collection<Events> all = eventService.getAllEvents();
            allEvents = new ArrayList<>();
            for (Events ev : all) {
                if ("Approved".equalsIgnoreCase(ev.getStatus())) {
                    allEvents.add(ev);
                }
            }
        } catch (Exception e) {
            showMessage("Could not load events: " + e.getMessage());
        }
    }

    // ── Register for an event ─────────────────────────────────────────────────
    public String registerForEvent(int eventId) {
        try {
            int uid = getLoggedInUserId();
            if (uid <= 0) {
                showMessage("Please log in to register.");
                return null;
            }
            if (registrationService.isAlreadyRegistered(uid, eventId)) {
                showMessage("You have already applied for this event.");
                return null;
            }
            String result = registrationService.registerForEvent(uid, eventId);
            return "user_my_registrations?faces-redirect=true&registered=true";
        } catch (Exception e) {
            showMessage("Error registering: " + e.getMessage());
            return null;
        }
    }

    // ── Cancel a pending registration ─────────────────────────────────────────
    public void cancelRegistration(int registrationId) {
        try {
            registrationService.cancelRegistration(registrationId);
            showMessage("Registration cancelled successfully.");
            loadMyRegistrations();
        } catch (Exception e) {
            showMessage("Error cancelling registration: " + e.getMessage());
        }
    }

    // ── Check if already registered for a specific event ─────────────────────
    public boolean isRegistered(int eventId) {
        try {
            int uid = getLoggedInUserId();
            if (uid <= 0) return false;
            return registrationService.isAlreadyRegistered(uid, eventId);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Count helpers ─────────────────────────────────────────────────────────
    public long getTotalCount() {
        return myRegistrations.size();
    }

    public long getPendingCount() {
        return myRegistrations.stream()
            .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()))
            .count();
    }

    public long getConfirmedCount() {
        return myRegistrations.stream()
            .filter(r -> "Confirmed".equalsIgnoreCase(r.getStatus()))
            .count();
    }

    public long getRejectedCount() {
        return myRegistrations.stream()
            .filter(r -> "Rejected".equalsIgnoreCase(r.getStatus()))
            .count();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private int getLoggedInUserId() {
        if (authBean != null && authBean.getLoggedInUser() != null) {
            return authBean.getLoggedInUser().getUserId();
        }
        return 0;
    }

    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(msg));
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public List<Registrations> getMyRegistrations() {
        if (myRegistrations.isEmpty()) loadMyRegistrations();
        return myRegistrations;
    }

    public List<Events> getAllEvents() {
        if (allEvents.isEmpty()) loadAllEvents();
        return allEvents;
    }

    public void setMyRegistrations(List<Registrations> r) { this.myRegistrations = r; }
}
