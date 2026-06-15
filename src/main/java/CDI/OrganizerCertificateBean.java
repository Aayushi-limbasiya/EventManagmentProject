package CDI;

import EJB.EventManagmentLocal;
import EJB.FeedbackCertificateBeanLocal;
import Entity.Certificates;
import Entity.Events;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CDI bean for organizer_certificates.xhtml —
 * select one of my events → bulk-issue certificates → see issued list.
 * EL name: orgCertBean
 */
@Named("orgCertBean")
@ViewScoped
public class OrganizerCertificateBean implements Serializable {

    @EJB private FeedbackCertificateBeanLocal certService;
    @EJB private EventManagmentLocal          eventService;
    @Inject private AuthBean authBean;

    private List<Events>       myEvents          = new ArrayList<>();
    private List<Certificates> eventCertificates = new ArrayList<>();
    private int  selectedEventId;
    private long issuedCount;
    private boolean loaded = false;

    public void ensureLoaded() { if (!loaded) loadEvents(); }

    public void loadEvents() {
        try {
            int uid = currentUserId();
            if (uid <= 0) return;
            Collection<Events> evs = eventService.getEventsByOrganizer(uid);
            myEvents = (evs != null) ? new ArrayList<>(evs) : new ArrayList<>();
            loaded = true;
        } catch (Exception ignore) { }
    }

    /** Issue certificates for ALL confirmed participants of the selected event */
    public void issueForEvent() {
        if (selectedEventId <= 0) { msg("Please select an event first."); return; }
        try {
            certService.generateCertificatesForEvent(selectedEventId);
            msg("Certificates issued for all confirmed participants ✓");
            loadEventCertificates();
        } catch (Exception e) {
            msg("Issue failed: " + e.getMessage());
        }
    }

    /** Load issued certificates of the selected event */
    public void loadEventCertificates() {
        if (selectedEventId <= 0) { eventCertificates = new ArrayList<>(); return; }
        try {
            Collection<Certificates> c = certService.getCertificatesByEvent(selectedEventId);
            eventCertificates = (c != null) ? new ArrayList<>(c) : new ArrayList<>();
            issuedCount = certService.getCertificateCount(selectedEventId);
        } catch (Exception e) {
            msg("Could not load certificates: " + e.getMessage());
        }
    }

    private int currentUserId() {
        return (authBean != null && authBean.getLoggedInUser() != null)
                ? authBean.getLoggedInUser().getUserId() : 0;
    }
    private void msg(String m) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(m));
    }

    // getters/setters
    public List<Events>       getMyEvents()          { ensureLoaded(); return myEvents; }
    public List<Certificates> getEventCertificates() { return eventCertificates; }
    public long getIssuedCount()                     { return issuedCount; }
    public int  getSelectedEventId()                 { return selectedEventId; }
    public void setSelectedEventId(int id)           { this.selectedEventId = id; }
}
