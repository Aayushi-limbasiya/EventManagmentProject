/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.PaymentBeanLocal;
import Entity.Payments;
import jakarta.ejb.EJB;
import jakarta.annotation.PostConstruct;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Named("paymentBean")
@ViewScoped
public class PaymentBean implements Serializable {

    @EJB
    private PaymentBeanLocal paymentService;

    private List<Payments> payments = new ArrayList<>();

    private int registrationId;
    private int paymentId;
    private int userId;
    private int eventId;

    private BigDecimal amount;
    private String paymentMethod;

    private String paymentStatus;
    private BigDecimal totalRevenue;
    private Map<String, Object> report;

    private String refundReason;

    private Payments selectedPayment;

    // ===============================
    // 🔹 AUTO-LOAD pending on page open
    // ===============================
    @PostConstruct
    public void init() {
        try {
            loadPending();
        } catch (Exception e) {
            // ignore — page still renders with empty list
        }
    }

    // ===============================
    // 🔹 MAKE PAYMENT
    // ===============================
    public void makePayment() {
        try {
            paymentService.makePayment(registrationId, amount, paymentMethod);
            showMessage("Payment initiated (Pending)");
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 VERIFY PAYMENT
    // ===============================
    public void verify(int id) {
        try {
            paymentService.verifyPayment(id);
            showMessage("Payment verified");
            loadPending();
        } catch (Exception e) {
            showMessage("Verification failed");
        }
    }

    // ===============================
    // 🔹 MARK FAILED
    // ===============================
    public void markFailed(int id) {
        try {
            paymentService.markPaymentFailed(id);
            showMessage("Marked as Failed");
        } catch (Exception e) {
            showMessage("Error marking failed");
        }
    }

    // ===============================
    // 🔹 REFUND
    // ===============================
    public void refund(int id) {
        try {
            paymentService.processRefund(id, refundReason);
            showMessage("Refund processed");
        } catch (Exception e) {
            showMessage("Refund failed");
        }
    }

    public void refundByEvent() {
        try {
            paymentService.processRefundByEvent(eventId);
            showMessage("Bulk refund completed");
        } catch (Exception e) {
            showMessage("Bulk refund failed");
        }
    }

    // ===============================
    // 🔹 LOAD DATA
    // ===============================
    public void loadByUser() {
        payments = new ArrayList<>(
            paymentService.getPaymentHistoryByUser(userId)
        );
    }

    public void loadByEvent() {
        payments = new ArrayList<>(
            paymentService.getPaymentsByEvent(eventId)
        );
    }

    public void loadPending() {
        payments = new ArrayList<>(
            paymentService.getPendingVerifications()
        );
    }

    public void loadByMethod() {
        payments = new ArrayList<>(
            paymentService.getPaymentsByMethod(paymentMethod)
        );
    }

    public void loadRefunded() {
        payments = new ArrayList<>(
            paymentService.getRefundedPayments()
        );
    }

    // ===============================
    // 🔹 PAYMENT STATUS
    // ===============================
    public void checkStatus() {
        paymentStatus = paymentService.getPaymentStatus(registrationId);
    }

    // ===============================
    // 🔹 RECEIPT
    // ===============================
    public void generateReceipt(int id) {
        try {
            selectedPayment = paymentService.generateReceipt(id);
            showMessage("Receipt generated");
        } catch (Exception e) {
            showMessage("Error generating receipt");
        }
    }

    // ===============================
    // 🔹 REVENUE
    // ===============================
    public void loadTotalRevenue() {
        totalRevenue = paymentService.getTotalRevenue();
    }

    public void loadRevenueByEvent() {
        totalRevenue = paymentService.getTotalRevenueByEvent(eventId);
    }

    // ===============================
    // 🔹 REPORT
    // ===============================
    public void loadReport() {
        report = paymentService.getPaymentReport();
    }

    // ===============================
    // 🔹 MESSAGE HELPER
    // ===============================
    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ===============================
    // 🔹 KPI HELPERS (for admin_payments.xhtml)
    // ===============================

    /** Total revenue, never null (safe for EL) */
    public BigDecimal getTotalRevenueSafe() {
        try {
            BigDecimal t = paymentService.getTotalRevenue();
            return (t != null) ? t : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /** Count of payments awaiting verification */
    public long getPendingCount() {
        try {
            Collection<Payments> c = paymentService.getPendingVerifications();
            return (c != null) ? c.size() : 0;
        } catch (Exception e) { return 0; }
    }

    /** Count of refunded payments */
    public long getRefundedCount() {
        try {
            Collection<Payments> c = paymentService.getRefundedPayments();
            return (c != null) ? c.size() : 0;
        } catch (Exception e) { return 0; }
    }

    /** Total transactions on the platform (from report map) */
    public long getTotalTransactions() {
        try {
            Map<String, Object> r = paymentService.getPaymentReport();
            if (r != null && r.get("totalTransactions") != null) {
                return Long.parseLong(r.get("totalTransactions").toString());
            }
        } catch (Exception ignore) { }
        return getPendingCount() + getRefundedCount();
    }

    // ===============================
    // 🔹 GETTERS & SETTERS
    // ===============================

    public List<Payments> getPayments() {
        return payments;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public Map<String, Object> getReport() {
        return report;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public Payments getSelectedPayment() {
        return selectedPayment;
    }
}
