/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Payments;
import Entity.Registrations;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author OS
 */
@Stateless
public class PaymentBean implements PaymentBeanLocal {
    
    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @Override
    public void makePayment(int registrationId, BigDecimal amount, String paymentMethod) {
          // Step 1: Validate registration
        Registrations reg = em.find(Registrations.class, registrationId);
        if (reg == null) {
            throw new RuntimeException("Registration not found with ID: " + registrationId);
        }

        // Step 2: Validate payment method
        if (!paymentMethod.equals("Card") && !paymentMethod.equals("UPI") &&
            !paymentMethod.equals("Wallet")) {
            throw new RuntimeException("Invalid payment method: " + paymentMethod
                + ". Allowed: Card, UPI, Wallet");
        }

        // Step 3: Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero.");
        }

        // Step 4: Create payment record
        Payments payment = new Payments();
        payment.setRegistrationId(reg);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("Pending");
        payment.setPaymentDate(new Date());
        em.persist(payment);
    }

    @Override
    public String getPaymentStatus(int registrationId) {
          TypedQuery<Payments> q =
            em.createNamedQuery("Payments.findByRegistration", Payments.class);
        q.setParameter("registrationId", registrationId);
        Collection<Payments> result = q.getResultList();
        if (result.isEmpty()) {
            return "No payment found for registration ID: " + registrationId;
        }
        return result.iterator().next().getPaymentStatus();
    }

    @Override
    public Payments getPaymentById(int paymentId) {
           return em.find(Payments.class, paymentId);
    }

    @Override
    public Payments generateReceipt(int paymentId) {
         Payments payment = em.find(Payments.class, paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        if (!"Paid".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Receipt can only be generated for Paid payments. Current: "
                + payment.getPaymentStatus());
        }
        return payment;
    }

    @Override
    public void processRefund(int paymentId, String reason) {
         Payments payment = em.find(Payments.class, paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        if (!"Paid".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Only Paid payments can be refunded. Current: "
                + payment.getPaymentStatus());
        }
        payment.setPaymentStatus("Refunded");
        em.merge(payment);
        System.out.println("Refund processed | Payment ID: " + paymentId + " | Reason: " + reason);
    }

    @Override
    public void processRefundByEvent(int eventId) {
         TypedQuery<Payments> q =
            em.createNamedQuery("Payments.findByEvent", Payments.class);
        q.setParameter("eventId", eventId);
        Collection<Payments> payments = q.getResultList();

        int refundCount = 0;
        for (Payments payment : payments) {
            if ("Paid".equals(payment.getPaymentStatus())) {
                payment.setPaymentStatus("Refunded");
                em.merge(payment);
                refundCount++;
            }
        }
        System.out.println("Bulk refund completed | Event ID: " + eventId
            + " | Total refunded: " + refundCount);
    }

    @Override
    public Collection<Payments> getPaymentHistoryByUser(int userId) {
           TypedQuery<Payments> q =
            em.createNamedQuery("Payments.findByUser", Payments.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public Collection<Payments> getPaymentsByEvent(int eventId) {
         TypedQuery<Payments> q =
            em.createNamedQuery("Payments.findByEvent", Payments.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }

    @Override
    public void verifyPayment(int paymentId) {
         Payments payment = em.find(Payments.class, paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        if (!"Pending".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Only Pending payments can be verified. Current: "
                + payment.getPaymentStatus());
        }
        payment.setPaymentStatus("Paid");
        em.merge(payment);
    }

    @Override
    public void markPaymentFailed(int paymentId) {
           Payments payment = em.find(Payments.class, paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        payment.setPaymentStatus("Failed");
        em.merge(payment);
    }

    @Override
    public Collection<Payments> getPendingVerifications() {
          TypedQuery<Payments> q =
            em.createNamedQuery("Payments.getPendingVerification", Payments.class);
        return q.getResultList();
    }

    @Override
    public Collection<Payments> getPaymentsByMethod(String paymentMethod) {
          TypedQuery<Payments> q =
            em.createNamedQuery("Payments.findByPaymentMethod", Payments.class);
        q.setParameter("paymentMethod", paymentMethod);
        return q.getResultList();
    }

    @Override
    public BigDecimal getTotalRevenueByEvent(int eventId) {
         TypedQuery<BigDecimal> q =
            em.createNamedQuery("Payments.getTotalRevenueByEvent", BigDecimal.class);
        q.setParameter("eventId", eventId);
        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalRevenue() {
          TypedQuery<BigDecimal> q =
            em.createNamedQuery("Payments.getTotalRevenue", BigDecimal.class);
        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public Map<String, Object> getPaymentReport() {
         Map<String, Object> report = new HashMap<>();

        String[] statuses = {"Paid", "Pending", "Refunded", "Failed"};
        for (String status : statuses) {
            TypedQuery<Long> countQ =
                em.createNamedQuery("Payments.countByStatus", Long.class);
            countQ.setParameter("paymentStatus", status);
            report.put("total" + status, countQ.getSingleResult());
        }

        report.put("totalRevenue", getTotalRevenue());
        return report;
    }

    @Override
    public Collection<Payments> getPaymentsByEventAndMethod(int eventId, String paymentMethod) {    
          TypedQuery<Payments> q =
            em.createNamedQuery("Payments.findByEventAndMethod", Payments.class);
        q.setParameter("eventId", eventId);
        q.setParameter("paymentMethod", paymentMethod);
        return q.getResultList();
    }

    @Override
    public Collection<Payments> getRefundedPayments() {
         TypedQuery<Payments> q =
            em.createNamedQuery("Payments.getRefundedPayments", Payments.class);
        return q.getResultList();
    }

    @Override
    public Collection<Payments> getRefundedPaymentsByEvent(int eventId) {
           TypedQuery<Payments> q =
            em.createNamedQuery("Payments.getRefundedByEvent", Payments.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }
    
}
