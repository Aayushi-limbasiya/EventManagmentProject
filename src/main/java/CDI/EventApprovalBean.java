/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.EventApprovalBeanLocal;
import Entity.Approvals;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Named("approvalBean")
@ViewScoped
public class EventApprovalBean implements Serializable {

    @EJB
    private EventApprovalBeanLocal approvalEJB;

    private Collection<Approvals> approvalList;
    private Approvals selectedApproval;
    private Map<String, Long> dashboardStats;

    private int eventId;
    private int adminUserId;
    private String remark;

    // ================================
    // 📊 DASHBOARD
    // ================================
    public void loadDashboard() {
        dashboardStats = approvalEJB.getDashboardStats();
    }

    // ================================
    // 📋 LOAD LISTS
    // ================================
    public void loadPending() {
        approvalList = approvalEJB.getPendingApprovals();
    }

    public void loadApproved() {
        approvalList = approvalEJB.getAllApproved();
    }

    public void loadRejected() {
        approvalList = approvalEJB.getAllRejected();
    }

    public void loadByAdmin(int adminUserId) {
        approvalList = approvalEJB.getApprovalsByAdmin(adminUserId);
    }

    public void loadEventHistory(int eventId) {
        approvalList = approvalEJB.getApprovalHistoryByEvent(eventId);
    }

    // ================================
    // 🔍 GET SINGLE
    // ================================
    public void loadApproval(int id) {
        selectedApproval = approvalEJB.getApprovalById(id);
    }

    public Approvals getLatestApproval(int eventId) {
        return approvalEJB.getLatestApprovalByEvent(eventId);
    }

    // ================================
    // 📤 SUBMIT FOR APPROVAL
    // ================================
    public void submitForApproval() {
        approvalEJB.submitForApproval(eventId, adminUserId);
        loadPending();
    }

    // ================================
    // ✅ APPROVE EVENT
    // ================================
    public void approve() {
        if (remark == null || remark.trim().isEmpty()) {
            remark = "Approved by admin";
        }

        approvalEJB.approveEvent(eventId, adminUserId, remark);
        loadPending(); // refresh list
    }

    // ================================
    // ❌ REJECT EVENT
    // ================================
    public void reject() {
        if (remark == null || remark.trim().isEmpty()) {
            remark = "Rejected by admin";
        }

        approvalEJB.rejectEvent(eventId, adminUserId, remark);
        loadPending(); // refresh list
    }

    // ================================
    // 🔁 GETTERS & SETTERS
    // ================================
    public Collection<Approvals> getApprovalList() {
        return approvalList;
    }

    public void setApprovalList(Collection<Approvals> approvalList) {
        this.approvalList = approvalList;
    }

    public Approvals getSelectedApproval() {
        return selectedApproval;
    }

    public void setSelectedApproval(Approvals selectedApproval) {
        this.selectedApproval = selectedApproval;
    }

    public Map<String, Long> getDashboardStats() {
        return dashboardStats;
    }

    public void setDashboardStats(Map<String, Long> dashboardStats) {
        this.dashboardStats = dashboardStats;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}