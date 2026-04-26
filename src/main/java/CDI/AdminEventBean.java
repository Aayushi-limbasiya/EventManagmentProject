package CDI;

import EJB.EventManagmentLocal;
import Entity.Events;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI Bean for Admin All Events page.
 * Handles: load all events, filter by status.
 */
@Named("adminEventBean")
@ViewScoped
public class AdminEventBean implements Serializable {

    @EJB
    private EventManagmentLocal eventService;

    private List<Events> allEvents    = new ArrayList<>();
    private List<Events> filteredEvents = new ArrayList<>();
    private String filterStatus;

    // ── LOAD ────────────────────────────────────────────────
    public void init() {
        loadAllEvents();
    }

    public void loadAllEvents() {
        try {
            allEvents = new ArrayList<>(eventService.getAllEvents());
            filteredEvents = new ArrayList<>(allEvents);
        } catch (Exception e) {
            addMsg("Error loading events: " + e.getMessage());
        }
    }

    // ── FILTER ──────────────────────────────────────────────
    public void applyFilter() {
        if (filterStatus == null || filterStatus.isEmpty()) {
            filteredEvents = new ArrayList<>(allEvents);
        } else {
            filteredEvents = new ArrayList<>();
            for (Events e : allEvents) {
                if (filterStatus.equalsIgnoreCase(e.getStatus())) {
                    filteredEvents.add(e);
                }
            }
        }
    }

    // ── HELPER ──────────────────────────────────────────────
    private void addMsg(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ── GETTERS / SETTERS ────────────────────────────────────
    public List<Events> getAllEvents() {
        if (allEvents.isEmpty()) loadAllEvents();
        return allEvents;
    }

    public List<Events> getFilteredEvents() { return filteredEvents; }

    public String getFilterStatus() { return filterStatus; }
    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
        applyFilter();
    }
}
