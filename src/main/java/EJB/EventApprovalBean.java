/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Approvals;
import Entity.Events;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author OS
 */
@Stateless
public class EventApprovalBean implements EventApprovalBeanLocal {
    
     @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @Override
    public Collection<Approvals> getPendingApprovals() {
         TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.getPendingApprovals", Approvals.class);
        return q.getResultList();
    }

    @Override
    public void approveEvent(int eventId, int adminUserId, String remark) {
         // Step 1: Find existing approval record for this event
        Approvals approval = getOrCreateApproval(eventId, adminUserId);

        // Step 2: Update approval record
        approval.setDecision("Approved");
        approval.setRemark(remark);
        approval.setDecisionAt(new Date());
        approval.setUserId(em.find(Users.class, adminUserId));
        em.merge(approval);

        // Step 3: Auto activate event → set event status = Approved
        Events event = em.find(Events.class, eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
        event.setStatus("Approved");
        em.merge(event);

        // Step 4: Send email notification to organizer
        String organizerEmail = event.getUserId().getEmail();
        sendApprovalNotification(organizerEmail, event.getTitle(), "Approved", remark);
    }

    @Override
    public void rejectEvent(int eventId, int adminUserId, String remark) {
          // Step 1: Find existing approval record for this event
        Approvals approval = getOrCreateApproval(eventId, adminUserId);

        // Step 2: Update approval record
        approval.setDecision("Rejected");
        approval.setRemark(remark);
        approval.setDecisionAt(new Date());
        approval.setUserId(em.find(Users.class, adminUserId));
        em.merge(approval);

        // Step 3: Update event status = Rejected
        Events event = em.find(Events.class, eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
        event.setStatus("Rejected");
        em.merge(event);

        // Step 4: Send email notification to organizer
        String organizerEmail = event.getUserId().getEmail();
        sendApprovalNotification(organizerEmail, event.getTitle(), "Rejected", remark);
    }

    @Override
    public Approvals getApprovalById(int approvalId) {
        return em.find(Approvals.class, approvalId);
    }

    @Override
    public Collection<Approvals> getApprovalHistoryByEvent(int eventId) {
         TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.getHistoryByEvent", Approvals.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public Collection<Approvals> getApprovalsByAdmin(int adminUserId) {
        TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.findByAdmin", Approvals.class);
        q.setParameter("userId", adminUserId);
        return q.getResultList();
    }

    @Override
    public Collection<Approvals> getAllApproved() {
          TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.getApprovedApprovals", Approvals.class);
        return q.getResultList();
    }

    @Override
    public Collection<Approvals> getAllRejected() {
           TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.getRejectedApprovals", Approvals.class);
        return q.getResultList();
    }

    @Override
    public Approvals getLatestApprovalByEvent(int eventId) {
         TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.getLatestByEvent", Approvals.class);
        q.setParameter("eventId", eventId);
        q.setMaxResults(1);
        Collection<Approvals> result = q.getResultList();
        return result.isEmpty() ? null : result.iterator().next();
    }

    @Override
    public Map<String, Long> getDashboardStats() {
         Map<String, Long> stats = new HashMap<>();

        TypedQuery<Long> pendingQ =
            em.createNamedQuery("Approvals.countPending", Long.class);
        stats.put("pending", pendingQ.getSingleResult());

        TypedQuery<Long> approvedQ =
            em.createNamedQuery("Approvals.countApproved", Long.class);
        stats.put("approved", approvedQ.getSingleResult());

        TypedQuery<Long> rejectedQ =
            em.createNamedQuery("Approvals.countRejected", Long.class);
        stats.put("rejected", rejectedQ.getSingleResult());

        return stats;
    }

    @Override
    public void sendApprovalNotification(String organizerEmail, String eventTitle, String decision, String remark) {
         String message = "Dear Organizer,\n\n"
            + "Your event \"" + eventTitle + "\" has been " + decision + ".\n"
            + "Remark: " + (remark != null ? remark : "No remarks provided") + "\n\n"
            + "Thank you,\nEvent Management Team";

        // TODO: Integrate real email service here
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + organizerEmail);
        System.out.println("Subject: Event " + decision + " - " + eventTitle);
        System.out.println(message);
        System.out.println("==========================");
    }

    @Override
    public void submitForApproval(int eventId, int adminUserId) {
         Events event = em.find(Events.class, eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }
        if (!"Pending".equals(event.getStatus())) {
            throw new RuntimeException("Event must be in Pending status to submit for approval. Current: " + event.getStatus());
        }

        Approvals approval = new Approvals();
        approval.setEventId(event);
        approval.setUserId(em.find(Users.class, adminUserId));
        approval.setDecision("Pending");
        approval.setRemark("Awaiting admin review");
        approval.setDecisionAt(new Date());
        em.persist(approval);
    }
    
     private Approvals getOrCreateApproval(int eventId, int adminUserId) {
        TypedQuery<Approvals> q =
            em.createNamedQuery("Approvals.getLatestByEvent", Approvals.class);
        q.setParameter("eventId", eventId);
        q.setMaxResults(1);
        Collection<Approvals> result = q.getResultList();

        if (result.isEmpty()) {
            throw new RuntimeException("No approval record found for event ID: " + eventId
                + ". Please submit the event for approval first.");
        }
        return result.iterator().next();
    }

}
