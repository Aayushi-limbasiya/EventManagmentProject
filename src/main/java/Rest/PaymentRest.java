/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;

import EJB.PaymentBeanLocal;
import Entity.Payments;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class PaymentRest {

    @EJB
    private PaymentBeanLocal paymentBean;

    // ─────────────────────────────────────────────────────────────
    // 1. MAKE A PAYMENT
    // POST /payments/make
    // Body: { "registrationId": 1, "amount": 500.00, "paymentMethod": "Card" }
    // ─────────────────────────────────────────────────────────────
    @POST
    @Path("/make")
    public Response makePayment(Map<String, Object> body) {
        try {
            int registrationId = Integer.parseInt(body.get("registrationId").toString());
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String paymentMethod = body.get("paymentMethod").toString();

            paymentBean.makePayment(registrationId, amount, paymentMethod);
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("message", "Payment initiated successfully with status: Pending"))
                    .build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. GET PAYMENT BY ID
    // GET /payments/{paymentId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/{paymentId}")
    public Response getPaymentById(@PathParam("paymentId") int paymentId) {
        Payments payment = paymentBean.getPaymentById(paymentId);
        if (payment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Payment not found with ID: " + paymentId))
                    .build();
        }
        return Response.ok(payment).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 3. GET PAYMENT STATUS BY REGISTRATION ID
    // GET /payments/status/{registrationId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/status/{registrationId}")
    public Response getPaymentStatus(@PathParam("registrationId") int registrationId) {
        String status = paymentBean.getPaymentStatus(registrationId);
        return Response.ok(Map.of("registrationId", registrationId, "status", status)).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 4. VERIFY PAYMENT (Admin) — Pending → Paid
    // PUT /payments/{paymentId}/verify
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{paymentId}/verify")
    public Response verifyPayment(@PathParam("paymentId") int paymentId) {
        try {
            paymentBean.verifyPayment(paymentId);
            return Response.ok(Map.of("message", "Payment ID " + paymentId + " verified successfully. Status set to Paid.")).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. MARK PAYMENT AS FAILED
    // PUT /payments/{paymentId}/fail
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{paymentId}/fail")
    public Response markPaymentFailed(@PathParam("paymentId") int paymentId) {
        try {
            paymentBean.markPaymentFailed(paymentId);
            return Response.ok(Map.of("message", "Payment ID " + paymentId + " marked as Failed.")).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 6. PROCESS REFUND FOR A SINGLE PAYMENT
    // PUT /payments/{paymentId}/refund
    // Body: { "reason": "Event cancelled" }
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/{paymentId}/refund")
    public Response processRefund(@PathParam("paymentId") int paymentId, Map<String, String> body) {
        try {
            String reason = body != null && body.get("reason") != null ? body.get("reason") : "No reason provided";
            paymentBean.processRefund(paymentId, reason);
            return Response.ok(Map.of("message", "Refund processed for Payment ID: " + paymentId)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 7. BULK REFUND BY EVENT
    // PUT /payments/refund/event/{eventId}
    // ─────────────────────────────────────────────────────────────
    @PUT
    @Path("/refund/event/{eventId}")
    public Response processRefundByEvent(@PathParam("eventId") int eventId) {
        try {
            paymentBean.processRefundByEvent(eventId);
            return Response.ok(Map.of("message", "Bulk refund processed for all Paid payments under Event ID: " + eventId)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 8. GENERATE RECEIPT (only for Paid payments)
    // GET /payments/{paymentId}/receipt
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/{paymentId}/receipt")
    public Response generateReceipt(@PathParam("paymentId") int paymentId) {
        try {
            Payments payment = paymentBean.generateReceipt(paymentId);
            return Response.ok(payment).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 9. GET PAYMENT HISTORY BY USER
    // GET /payments/user/{userId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/user/{userId}")
    public Response getPaymentHistoryByUser(@PathParam("userId") int userId) {
        Collection<Payments> payments = paymentBean.getPaymentHistoryByUser(userId);
        return Response.ok(payments).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 10. GET ALL PAYMENTS BY EVENT
    // GET /payments/event/{eventId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}")
    public Response getPaymentsByEvent(@PathParam("eventId") int eventId) {
        Collection<Payments> payments = paymentBean.getPaymentsByEvent(eventId);
        return Response.ok(payments).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 11. GET ALL PENDING PAYMENTS (Admin)
    // GET /payments/pending
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/pending")
    public Response getPendingVerifications() {
        Collection<Payments> payments = paymentBean.getPendingVerifications();
        return Response.ok(payments).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 12. GET PAYMENTS BY METHOD
    // GET /payments/method/{paymentMethod}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/method/{paymentMethod}")
    public Response getPaymentsByMethod(@PathParam("paymentMethod") String paymentMethod) {
        Collection<Payments> payments = paymentBean.getPaymentsByMethod(paymentMethod);
        return Response.ok(payments).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 13. GET TOTAL REVENUE (All Events)
    // GET /payments/revenue/total
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/revenue/total")
    public Response getTotalRevenue() {
        BigDecimal total = paymentBean.getTotalRevenue();
        return Response.ok(Map.of("totalRevenue", total)).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 14. GET TOTAL REVENUE BY EVENT
    // GET /payments/revenue/event/{eventId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/revenue/event/{eventId}")
    public Response getTotalRevenueByEvent(@PathParam("eventId") int eventId) {
        BigDecimal total = paymentBean.getTotalRevenueByEvent(eventId);
        return Response.ok(Map.of("eventId", eventId, "totalRevenue", total)).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 15. GET PAYMENT REPORT (Admin Dashboard)
    // GET /payments/report
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/report")
    public Response getPaymentReport() {
        Map<String, Object> report = paymentBean.getPaymentReport();
        return Response.ok(report).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 16. GET PAYMENTS BY EVENT AND METHOD
    // GET /payments/event/{eventId}/method/{paymentMethod}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/event/{eventId}/method/{paymentMethod}")
    public Response getPaymentsByEventAndMethod(
            @PathParam("eventId") int eventId,
            @PathParam("paymentMethod") String paymentMethod) {
        Collection<Payments> payments = paymentBean.getPaymentsByEventAndMethod(eventId, paymentMethod);
        return Response.ok(payments).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 17. GET ALL REFUNDED PAYMENTS
    // GET /payments/refunded
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/refunded")
    public Response getRefundedPayments() {
        Collection<Payments> payments = paymentBean.getRefundedPayments();
        return Response.ok(payments).build();
    }

    // ─────────────────────────────────────────────────────────────
    // 18. GET REFUNDED PAYMENTS BY EVENT
    // GET /payments/refunded/event/{eventId}
    // ─────────────────────────────────────────────────────────────
    @GET
    @Path("/refunded/event/{eventId}")
    public Response getRefundedPaymentsByEvent(@PathParam("eventId") int eventId) {
        Collection<Payments> payments = paymentBean.getRefundedPaymentsByEvent(eventId);
        return Response.ok(payments).build();
    }
}
