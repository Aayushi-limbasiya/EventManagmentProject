package CDI;

import EJB.EventManagmentLocal;
import EJB.RegistrationBeanLocal;
import EJB.FeedbackCertificateBeanLocal;
import Entity.Events;
import Entity.Registrations;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * CDI Bean for the Organizer Dashboard.
 * Aggregates LIVE data for the logged-in organizer:
 *   - total events, registrations, average rating, waitlist count
 *   - list of own events (with per-event registered / waitlist counts)
 *   - recent registrations across all own events
 *
 * EL name: orgDashBean
 */
@Named("orgDashBean")
@ViewScoped
public class OrganizerDashboardBean implements Serializable {

    @EJB private EventManagmentLocal        eventService;
    @EJB private RegistrationBeanLocal      registrationService;
    @EJB private FeedbackCertificateBeanLocal feedbackService;

    @Inject private AuthBean authBean;

    private List<Events>        myEvents          = new ArrayList<>();
    private List<Registrations> recentRegistrations = new ArrayList<>();

    private int    totalEvents;
    private int    pendingEvents;
    private int    totalRegistrations;
    private int    waitlistCount;
    private double avgRating;
    private int    reviewCount;
    private boolean loaded = false;

    // ── Auto-load on first access ─────────────────────────────────
    public void ensureLoaded() {
        if (!loaded) load();
    }

    public void load() {
        try {
            int uid = getOrganizerId();
            if (uid <= 0) return;

            // 1. All events for this organizer
            myEvents = new ArrayList<>(eventService.getEventsByOrganizer(uid));
            totalEvents = myEvents.size();

            // 2. Count pending events + gather registrations
            pendingEvents = 0;
            totalRegistrations = 0;
            waitlistCount = 0;
            double ratingSum = 0;
            int ratedEvents = 0;
            List<Registrations> allRegs = new ArrayList<>();

            for (Events ev : myEvents) {
                String st = ev.getStatus();
                if (st != null && st.equalsIgnoreCase("Pending")) pendingEvents++;

                // registrations for this event
                Collection<Registrations> regs =
                    registrationService.getAllRegistrationsByEvent(ev.getEventId());
                if (regs != null) {
                    for (Registrations r : regs) {
                        allRegs.add(r);
                        totalRegistrations++;
                        if ("Waitlist".equalsIgnoreCase(r.getStatus())) waitlistCount++;
                    }
                }

                // average rating for this event
                try {
                    double a = feedbackService.getAverageRating(ev.getEventId());
                    if (a > 0) { ratingSum += a; ratedEvents++; }
                } catch (Exception ignore) { }
            }

            // 3. Average rating across rated events
            avgRating = (ratedEvents > 0) ? (ratingSum / ratedEvents) : 0.0;

            // 4. Review count (feedback received by organizer)
            try {
                Collection<?> fb = feedbackService.getFeedbackByOrganizer(uid);
                reviewCount = (fb != null) ? fb.size() : 0;
            } catch (Exception ignore) { reviewCount = 0; }

            // 5. Recent registrations — newest first, top 5
            allRegs.sort(Comparator.comparing(
                Registrations::getRegisteredAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
            recentRegistrations = allRegs.size() > 5
                ? new ArrayList<>(allRegs.subList(0, 5))
                : allRegs;

            loaded = true;
        } catch (Exception e) {
            // keep zeros on error
        }
    }

    // ── Per-event helpers (used in the events grid) ────────────────
    public long getRegisteredCount(int eventId) {
        try {
            Collection<Registrations> regs =
                registrationService.getAllRegistrationsByEvent(eventId);
            if (regs == null) return 0;
            return regs.stream()
                .filter(r -> !"Waitlist".equalsIgnoreCase(r.getStatus()))
                .count();
        } catch (Exception e) { return 0; }
    }

    public long getWaitlistForEvent(int eventId) {
        try {
            Collection<Registrations> regs =
                registrationService.getAllRegistrationsByEvent(eventId);
            if (regs == null) return 0;
            return regs.stream()
                .filter(r -> "Waitlist".equalsIgnoreCase(r.getStatus()))
                .count();
        } catch (Exception e) { return 0; }
    }

    // ── Auth helper ────────────────────────────────────────────────
    private int getOrganizerId() {
        if (authBean != null && authBean.getLoggedInUser() != null) {
            return authBean.getLoggedInUser().getUserId();
        }
        return 0;
    }

    // ── Getters (trigger lazy load) ────────────────────────────────
    public List<Events> getMyEvents()                { ensureLoaded(); return myEvents; }
    public List<Registrations> getRecentRegistrations() { ensureLoaded(); return recentRegistrations; }
    public int    getTotalEvents()        { ensureLoaded(); return totalEvents; }
    public int    getPendingEvents()      { ensureLoaded(); return pendingEvents; }
    public int    getTotalRegistrations() { ensureLoaded(); return totalRegistrations; }
    public int    getWaitlistCount()      { ensureLoaded(); return waitlistCount; }
    public double getAvgRating()          { ensureLoaded(); return avgRating; }
    public int    getReviewCount()        { ensureLoaded(); return reviewCount; }
}
