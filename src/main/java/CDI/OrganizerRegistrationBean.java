package CDI;

import EJB.EventManagmentLocal;
import EJB.RegistrationBeanLocal;
import Entity.Events;
import Entity.Registrations;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI Bean for Organizer registration and participant management.
 * Handles: load registrations by event, approve, reject, participant counts.
 */
@Named("orgRegBean")
@ViewScoped
public class OrganizerRegistrationBean implements Serializable {

    @EJB private RegistrationBeanLocal regService;
    @EJB private EventManagmentLocal   eventService;

    // ✅ Use @Inject — CDI beans are NOT in the HTTP session map by EL name
    @Inject
    private AuthBean authBean;

    private List<Events>        myEvents        = new ArrayList<>();
    private List<Registrations> pendingRegs     = new ArrayList<>();
    private List<Registrations> approvedRegs    = new ArrayList<>();
    private List<Registrations> rejectedRegs    = new ArrayList<>();
    private List<Registrations> allParticipants = new ArrayList<>();

    private int    selectedEventId   = 0;
    private String selectedEventName = "";
    private int    expandedEventId   = 0;

    // ── LOAD ORGANIZER EVENTS ───────────────────────────────
    public void loadMyEvents() {
        try {
            int uid = getLoggedInUserId();
            if (uid > 0) {
                myEvents = new ArrayList<>(eventService.getEventsByOrganizer(uid));
            }
        } catch (Exception e) {
            addMsg("Error: " + e.getMessage());
        }
    }

    // ── LOAD REGISTRATIONS FOR SELECTED EVENT ───────────────
    public void loadRegistrationsForEvent(int eventId) {
        this.selectedEventId = eventId;
        try {
            Events ev = eventService.getEventById(eventId);
            selectedEventName = ev != null ? ev.getTitle() : "";

            List<Registrations> all = new ArrayList<>(
                regService.getAllRegistrationsByEvent(eventId));

            pendingRegs  = new ArrayList<>();
            approvedRegs = new ArrayList<>();
            rejectedRegs = new ArrayList<>();

            for (Registrations r : all) {
                String s = r.getStatus();
                if ("Confirmed".equalsIgnoreCase(s) || "Approved".equalsIgnoreCase(s)) {
                    approvedRegs.add(r);
                } else if ("Cancelled".equalsIgnoreCase(s) || "Rejected".equalsIgnoreCase(s)) {
                    rejectedRegs.add(r);
                } else {
                    pendingRegs.add(r);
                }
            }
        } catch (Exception e) {
            addMsg("Error loading registrations: " + e.getMessage());
        }
    }

    // ── APPROVE ─────────────────────────────────────────────
    public String approveRegistration(int regId) {
        try {
            regService.approveRegistration(regId);
            loadRegistrationsForEvent(selectedEventId);
            return "organizer_registrations?faces-redirect=true&approved=true&eventId=" + selectedEventId;
        } catch (Exception e) {
            addMsg("Error approving: " + e.getMessage());
            return null;
        }
    }

    // ── REJECT ──────────────────────────────────────────────
    public String rejectRegistration(int regId) {
        try {
            regService.cancelRegistration(regId);
            loadRegistrationsForEvent(selectedEventId);
            return "organizer_registrations?faces-redirect=true&rejected=true&eventId=" + selectedEventId;
        } catch (Exception e) {
            addMsg("Error rejecting: " + e.getMessage());
            return null;
        }
    }

    // ── PARTICIPANTS ─────────────────────────────────────────
    public void loadParticipants(int eventId) {
        try {
            this.expandedEventId = eventId;
            allParticipants = new ArrayList<>(regService.getParticipantsByEvent(eventId));
        } catch (Exception e) {
            addMsg("Error loading participants.");
        }
    }

    public void toggleExpand(int eventId) {
        if (this.expandedEventId == eventId) {
            this.expandedEventId = 0;
            allParticipants      = new ArrayList<>();
        } else {
            loadParticipants(eventId);
        }
    }

    public long getParticipantCount(int eventId) {
        try { return regService.getConfirmedCount(eventId); }
        catch (Exception e) { return 0; }
    }

    // ── HELPER ──────────────────────────────────────────────
    private int getLoggedInUserId() {
        if (authBean != null && authBean.getLoggedInUser() != null) {
            return authBean.getLoggedInUser().getUserId();
        }
        return 0;
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
    }

    // ── GETTERS / SETTERS ───────────────────────────────────
    public List<Events>        getMyEvents()         { if (myEvents.isEmpty()) loadMyEvents(); return myEvents; }
    public List<Registrations> getPendingRegs()      { return pendingRegs; }
    public List<Registrations> getApprovedRegs()     { return approvedRegs; }
    public List<Registrations> getRejectedRegs()     { return rejectedRegs; }
    public List<Registrations> getAllParticipants()   { return allParticipants; }
    public int    getSelectedEventId()               { return selectedEventId; }
    public void   setSelectedEventId(int id)         { this.selectedEventId = id; }
    public String getSelectedEventName()             { return selectedEventName; }
    public int    getExpandedEventId()               { return expandedEventId; }
    public void   setExpandedEventId(int id)         { this.expandedEventId = id; }
}
