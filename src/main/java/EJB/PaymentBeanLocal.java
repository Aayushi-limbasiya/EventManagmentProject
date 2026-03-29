/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Payments;
import jakarta.ejb.Local;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author OS
 */
@Local
public interface PaymentBeanLocal {
    void makePayment(int registrationId, BigDecimal amount, String paymentMethod);

    String getPaymentStatus(int registrationId);

    Payments getPaymentById(int paymentId);

    Payments generateReceipt(int paymentId);

    void processRefund(int paymentId, String reason);

    void processRefundByEvent(int eventId);

    Collection<Payments> getPaymentHistoryByUser(int userId);

    Collection<Payments> getPaymentsByEvent(int eventId);

    void verifyPayment(int paymentId);

    void markPaymentFailed(int paymentId);

    Collection<Payments> getPendingVerifications();

    Collection<Payments> getPaymentsByMethod(String paymentMethod);

    BigDecimal getTotalRevenueByEvent(int eventId);

    BigDecimal getTotalRevenue();

    Map<String, Object> getPaymentReport();

    Collection<Payments> getPaymentsByEventAndMethod(int eventId, String paymentMethod);

    Collection<Payments> getRefundedPayments();

    Collection<Payments> getRefundedPaymentsByEvent(int eventId);
}
