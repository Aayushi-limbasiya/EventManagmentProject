package CDI;

import EJB.EventSchedulingLocal;
import Entity.Venues;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI Bean for Admin Venues page.
 * Handles: add, update, delete, load all venues.
 */
@Named("adminVenueBean")
@ViewScoped
public class AdminVenueBean implements Serializable {

    @EJB
    private EventSchedulingLocal scheduleService;

    private List<Venues> allVenues    = new ArrayList<>();
    private Venues       selectedVenue = new Venues();
    private int          deleteVenueId;

    public void loadVenues() {
        try {
            allVenues = new ArrayList<>(scheduleService.getAllVenues());
        } catch (Exception e) {
            addMsg("Error loading venues: " + e.getMessage());
        }
    }

    // ── SAVE (ADD or UPDATE) ─────────────────────────────────
    public String saveVenue() {
        try {
            if (selectedVenue.getName() == null || selectedVenue.getName().trim().isEmpty()) {
                addMsg("Venue name is required.");
                return null;
            }
            if (selectedVenue.getCapacity() <= 0) {
                addMsg("Capacity must be greater than 0.");
                return null;
            }

            // ✅ venueId is Integer (nullable) — check for null or 0
            Integer vid = selectedVenue.getVenueId();
            if (vid == null || vid == 0) {
                scheduleService.addVenue(selectedVenue);
            } else {
                scheduleService.updateVenue(selectedVenue);
            }

            selectedVenue = new Venues();
            loadVenues();
            return "admin_venues?faces-redirect=true&saved=true";
        } catch (Exception e) {
            addMsg("Error saving venue: " + e.getMessage());
            return null;
        }
    }

    // ── DELETE ──────────────────────────────────────────────
    public String deleteVenue() {
        try {
            scheduleService.deleteVenue(deleteVenueId);
            loadVenues();
            return "admin_venues?faces-redirect=true&deleted=true";
        } catch (Exception e) {
            addMsg("Cannot delete — venue may be in use by an active event.");
            return null;
        }
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ── GETTERS / SETTERS ────────────────────────────────────
    public List<Venues> getAllVenues()           { if (allVenues.isEmpty()) loadVenues(); return allVenues; }
    public Venues       getSelectedVenue()      { return selectedVenue; }
    public void         setSelectedVenue(Venues v) { this.selectedVenue = v; }
    public int          getDeleteVenueId()      { return deleteVenueId; }
    public void         setDeleteVenueId(int id){ this.deleteVenueId = id; }
}
