package CDI;

import EJB.UserManagementLocal;
import EJB.EventManagmentLocal;
import EJB.EventApprovalBeanLocal;
import EJB.EventSchedulingLocal;
import Entity.Users;
import Entity.Events;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CDI Bean for the Admin Dashboard + Analytics.
 * Aggregates LIVE platform-wide data:
 *   - total users, active events, pending approvals, venues
 *   - recent events (for the dashboard table)
 *   - chart data: events by status, users by role, approvals breakdown
 *
 * EL name: adminDashBean
 */
@Named("adminDashBean")
@ViewScoped
public class AdminDashboardBean implements Serializable {

    @EJB private UserManagementLocal     userService;
    @EJB private EventManagmentLocal     eventService;
    @EJB private EventApprovalBeanLocal  approvalService;
    @EJB private EventSchedulingLocal    venueService;

    private boolean loaded = false;

    // headline counts
    private int totalUsers;
    private int totalEvents;
    private int activeEvents;
    private int pendingApprovals;
    private int approvedCount;
    private int rejectedCount;
    private int totalVenues;

    // recent events for the table
    private List<Events> recentEvents = new ArrayList<>();

    // chart data
    private Map<String, Long> eventsByStatus = new LinkedHashMap<>();
    private Map<String, Long> usersByRole    = new LinkedHashMap<>();

    public void ensureLoaded() { if (!loaded) load(); }

    public void load() {
        try {
            // ── Users ──────────────────────────────────────────────
            Collection<Users> users = userService.getAllUsers();
            List<Users> userList = (users != null) ? new ArrayList<>(users) : new ArrayList<>();
            totalUsers = userList.size();

            usersByRole = new LinkedHashMap<>();
            for (Users u : userList) {
                String role = (u.getRoleId() != null && u.getRoleId().getRoleName() != null)
                        ? u.getRoleId().getRoleName() : "Unknown";
                usersByRole.merge(role, 1L, Long::sum);
            }

            // ── Events ─────────────────────────────────────────────
            Collection<Events> events = eventService.getAllEvents();
            List<Events> evList = (events != null) ? new ArrayList<>(events) : new ArrayList<>();
            totalEvents = evList.size();

            eventsByStatus = new LinkedHashMap<>();
            activeEvents = 0;
            for (Events e : evList) {
                String st = (e.getStatus() != null) ? e.getStatus() : "Unknown";
                eventsByStatus.merge(st, 1L, Long::sum);
                if ("Approved".equalsIgnoreCase(st) || "Live".equalsIgnoreCase(st)) {
                    activeEvents++;
                }
            }

            // newest events first (by createdAt), top 5 for the table
            evList.sort((a, b) -> {
                if (a.getCreatedAt() == null) return 1;
                if (b.getCreatedAt() == null) return -1;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });
            recentEvents = evList.size() > 5 ? new ArrayList<>(evList.subList(0, 5)) : evList;

            // ── Approvals (reuse existing EJB stats) ───────────────
            Map<String, Long> stats = approvalService.getDashboardStats();
            if (stats != null) {
                pendingApprovals = stats.getOrDefault("pending",  0L).intValue();
                approvedCount    = stats.getOrDefault("approved", 0L).intValue();
                rejectedCount    = stats.getOrDefault("rejected", 0L).intValue();
            }

            // ── Venues ─────────────────────────────────────────────
            try {
                Collection<?> venues = venueService.getAllVenues();
                totalVenues = (venues != null) ? venues.size() : 0;
            } catch (Exception ignore) { totalVenues = 0; }

            loaded = true;
        } catch (Exception e) {
            // keep zeros on error
        }
    }

    // ── Chart helpers — return JSON-ready arrays for Chart.js ─────
    /** Labels for events-by-status pie, e.g. ["Approved","Pending","Rejected"] */
    public String getEventStatusLabelsJson() { return toJsonLabels(eventsByStatus); }
    /** Values for events-by-status pie, e.g. [12,8,3] */
    public String getEventStatusValuesJson() { return toJsonValues(eventsByStatus); }

    /** Labels for users-by-role bar */
    public String getUserRoleLabelsJson() { return toJsonLabels(usersByRole); }
    /** Values for users-by-role bar */
    public String getUserRoleValuesJson() { return toJsonValues(usersByRole); }

    /** Approvals breakdown values [pending, approved, rejected] */
    public String getApprovalValuesJson() {
        return "[" + pendingApprovals + "," + approvedCount + "," + rejectedCount + "]";
    }

    private String toJsonLabels(Map<String, Long> m) {
        ensureLoaded();
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String k : m.keySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(k.replace("\"", "")).append("\"");
            first = false;
        }
        return sb.append("]").toString();
    }

    private String toJsonValues(Map<String, Long> m) {
        ensureLoaded();
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Long v : m.values()) {
            if (!first) sb.append(",");
            sb.append(v);
            first = false;
        }
        return sb.append("]").toString();
    }

    // ── Getters (lazy-load on first access) ───────────────────────
    public int getTotalUsers()       { ensureLoaded(); return totalUsers; }
    public int getTotalEvents()      { ensureLoaded(); return totalEvents; }
    public int getActiveEvents()     { ensureLoaded(); return activeEvents; }
    public int getPendingApprovals() { ensureLoaded(); return pendingApprovals; }
    public int getApprovedCount()    { ensureLoaded(); return approvedCount; }
    public int getRejectedCount()    { ensureLoaded(); return rejectedCount; }
    public int getTotalVenues()      { ensureLoaded(); return totalVenues; }
    public List<Events> getRecentEvents()        { ensureLoaded(); return recentEvents; }
    public Map<String, Long> getEventsByStatus() { ensureLoaded(); return eventsByStatus; }
    public Map<String, Long> getUsersByRole()    { ensureLoaded(); return usersByRole; }
}