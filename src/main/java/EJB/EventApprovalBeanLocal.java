/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Approvals;
import jakarta.ejb.Local;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author OS
 */
@Local
public interface EventApprovalBeanLocal {
    
    Collection<Approvals> getPendingApprovals();

    void approveEvent(int eventId, int adminUserId, String remark);

    void rejectEvent(int eventId, int adminUserId, String remark);

    Approvals getApprovalById(int approvalId);

    Collection<Approvals> getApprovalHistoryByEvent(int eventId);

    Collection<Approvals> getApprovalsByAdmin(int adminUserId);

    Collection<Approvals> getAllApproved();

    Collection<Approvals> getAllRejected();

    Approvals getLatestApprovalByEvent(int eventId);

    Map<String, Long> getDashboardStats();

    void sendApprovalNotification(String organizerEmail, String eventTitle, String decision, String remark);

    void submitForApproval(int eventId, int adminUserId);
}
