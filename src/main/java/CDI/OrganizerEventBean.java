package CDI;

import EJB.EventApprovalBeanLocal;
import EJB.EventManagmentLocal;
import Entity.Events;
import Entity.Users;
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
 * CDI Bean for Organizer event pages.
 * Handles: create, update, delete, load by organizer.
 *
 * KEY FIX: After creating an event, calls submitForApproval() to insert
 * a row in the approvals table with decision="Pending".
 * Without this, the admin's getPendingApprovals() returns nothing because
 * it queries the approvals table, not the events table.
 */
@Named("orgEventBean")
@ViewScoped
public class OrganizerEventBean implements Serializable {

    @EJB
    private EventManagmentLocal eventService;

    @EJB
    private EventApprovalBeanLocal approvalService;

    @Inject
    private AuthBean authBean;

    private List<Events> myEvents     = new ArrayList<>();
    private Events       editingEvent = new Events();
    private boolean      showEditForm = false;
    private int          deleteEventId;

    // ── LOAD organizer's own events ─────────────────────────
    public void loadMyEvents() {
        try {
            int userId = getLoggedInUserId();
            if (userId > 0) {
                myEvents = new ArrayList<>(eventService.getEventsByOrganizer(userId));
            }
        } catch (Exception e) {
            addMsg("Error loading events: " + e.getMessage());
        }
    }

    // ── CREATE ──────────────────────────────────────────────
    public String createEvent() {
        try {
            if (editingEvent.getTitle() == null || editingEvent.getTitle().trim().isEmpty()) {
                addMsg("Event title is required.");
                return null;
            }

            int uid = getLoggedInUserId();
            if (uid <= 0) {
                addMsg("Session expired. Please log in again.");
                return null;
            }

            // 1. Set organizer and status
            Users u = new Users();
            u.setUserId(uid);
            editingEvent.setUserId(u);
            editingEvent.setStatus("Pending");

            // 2. Persist event
            eventService.createEvent(editingEvent);

            // 3. ✅ Submit for approval — inserts row in approvals table
            //    so admin's Pending tab shows it immediately.
            //    Pass organizer's userId as the "adminUserId" placeholder
            //    (gets overwritten when admin actually acts on it)
            if (editingEvent.getEventId() != null) {
                approvalService.submitForApproval(editingEvent.getEventId(), uid);
            }

            editingEvent = new Events();
            return "organizer_my_events?faces-redirect=true&created=true";

        } catch (Exception e) {
            addMsg("Error creating event: " + e.getMessage());
            return null;
        }
    }

    // ── LOAD FOR EDIT ───────────────────────────────────────
    public void loadForEdit(int eventId) {
        try {
            editingEvent = eventService.getEventById(eventId);
            showEditForm = true;
        } catch (Exception e) {
            addMsg("Error loading event.");
        }
    }

    // ── UPDATE ──────────────────────────────────────────────
    public String updateEvent() {
        try {
            eventService.updateEvent(editingEvent);
            showEditForm = false;
            editingEvent = new Events();
            loadMyEvents();
            return "organizer_my_events?faces-redirect=true&updated=true";
        } catch (Exception e) {
            addMsg("Error updating: " + e.getMessage());
            return null;
        }
    }

    // ── DELETE ──────────────────────────────────────────────
    public String deleteEvent() {
        try {
            eventService.deleteEvent(deleteEventId);
            loadMyEvents();
            return "organizer_my_events?faces-redirect=true&deleted=true";
        } catch (Exception e) {
            addMsg("Cannot delete — event may have active registrations or approvals.");
            return null;
        }
    }

    // ── CANCEL EDIT ─────────────────────────────────────────
    public void cancelEdit() {
        editingEvent = new Events();
        showEditForm = false;
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
    public List<Events> getMyEvents()             { if (myEvents.isEmpty()) loadMyEvents(); return myEvents; }
    public Events       getEditingEvent()         { return editingEvent; }
    public void         setEditingEvent(Events e) { this.editingEvent = e; }
    public boolean      isShowEditForm()          { return showEditForm; }
    public void         setShowEditForm(boolean v){ this.showEditForm = v; }
    public int          getDeleteEventId()        { return deleteEventId; }
    public void         setDeleteEventId(int id)  { this.deleteEventId = id; }
}
