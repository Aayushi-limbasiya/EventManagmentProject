package CDI;

import EJB.PaymentBeanLocal;
import EJB.RegistrationBeanLocal;
import Entity.Payments;
import Entity.Registrations;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CDI Bean for the USER payment pages:
 *   - user_payments.xhtml          (history + "Pay Now" list)
 *   - user_payment_checkout.xhtml  (simulated gateway)
 *
 * EL name: userPayBean
 */
@Named("userPayBean")
@ViewScoped
public class UserPaymentBean implements Serializable {

    @EJB private PaymentBeanLocal      paymentService;
    @EJB private RegistrationBeanLocal registrationService;

    @Inject private AuthBean authBean;

    /** Confirmed registrations that still need payment (fee > 0, no Paid/Pending payment yet) */
    private List<Registrations> payableRegistrations = new ArrayList<>();

    /** Full payment history of the logged-in user */
    private List<Payments> myPayments = new ArrayList<>();

    /* checkout form state */
    private int        registrationId;
    private String     eventTitle;
    private BigDecimal amount = BigDecimal.ZERO;
    private String     paymentMethod = "UPI";

    /* fake gateway inputs (never stored — simulation only) */
    private String upiId;
    private String cardNumber;
    private String cardName;
    private String cardExpiry;
    private String cardCvv;

    private boolean loaded = false;

    // ─────────────────────────────────────────────────────────────
    public void ensureLoaded() { if (!loaded) load(); }

    public void load() {
        int uid = currentUserId();
        if (uid <= 0) return;
        try {
            // 1. payment history
            Collection<Payments> hist = paymentService.getPaymentHistoryByUser(uid);
            myPayments = (hist != null) ? new ArrayList<>(hist) : new ArrayList<>();

            // 2. payable = Confirmed registrations with fee > 0 and no Paid/Pending payment
            payableRegistrations = new ArrayList<>();
            Collection<Registrations> regs = registrationService.getRegisteredEventsByUser(uid);
            if (regs != null) {
                for (Registrations r : regs) {
                    if (!"Confirmed".equalsIgnoreCase(r.getStatus())) continue;

                    BigDecimal fee = (r.getEventId() != null && r.getEventId().getFee() != null)
                            ? r.getEventId().getFee() : BigDecimal.ZERO;
                    if (fee.compareTo(BigDecimal.ZERO) <= 0) continue; // free event

                    if (!hasActivePayment(r.getRegistrationId())) {
                        payableRegistrations.add(r);
                    }
                }
            }
            loaded = true;
        } catch (Exception e) {
            msg("Could not load payments: " + e.getMessage());
        }
    }

    /** true if a Paid or Pending payment already exists for this registration */
    private boolean hasActivePayment(int regId) {
        for (Payments p : myPayments) {
            if (p.getRegistrationId() != null
                    && p.getRegistrationId().getRegistrationId() == regId) {
                String st = p.getPaymentStatus();
                if ("Paid".equalsIgnoreCase(st) || "Pending".equalsIgnoreCase(st)) return true;
            }
        }
        return false;
    }

    // ── Checkout: prefill from query params (regId) ──────────────
    public void initCheckout() {
        if (registrationId <= 0) return;
        try {
            Registrations r = registrationService.getRegistrationById(registrationId);
            if (r != null && r.getEventId() != null) {
                eventTitle = r.getEventId().getTitle();
                amount     = (r.getEventId().getFee() != null)
                             ? r.getEventId().getFee() : BigDecimal.ZERO;
            }
        } catch (Exception e) {
            msg("Could not load registration: " + e.getMessage());
        }
    }

    // ── Submit payment (simulated gateway) ───────────────────────
    public String submitPayment() {
        try {
            if (registrationId <= 0) { msg("Invalid registration."); return null; }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                msg("Invalid amount."); return null;
            }
            // basic fake-gateway validation
            if ("UPI".equals(paymentMethod)
                    && (upiId == null || !upiId.contains("@"))) {
                msg("Please enter a valid UPI ID (e.g. name@bank)."); return null;
            }
            if ("Card".equals(paymentMethod)) {
                if (cardNumber == null || cardNumber.replace(" ", "").length() < 12) {
                    msg("Please enter a valid card number."); return null;
                }
                if (cardCvv == null || cardCvv.length() < 3) {
                    msg("Please enter a valid CVV."); return null;
                }
            }

            paymentService.makePayment(registrationId, amount, paymentMethod);

            // redirect back to payments page with success flag
            return "user_payments.xhtml?faces-redirect=true&amp;paid=true";
        } catch (Exception e) {
            msg("Payment failed: " + e.getMessage());
            return null;
        }
    }

    // ── helpers ──────────────────────────────────────────────────
    private int currentUserId() {
        return (authBean != null && authBean.getLoggedInUser() != null)
                ? authBean.getLoggedInUser().getUserId() : 0;
    }

    private void msg(String m) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(m));
    }

    // counts for the summary cards
    public long getPaidCount() {
        ensureLoaded();
        return myPayments.stream().filter(p -> "Paid".equalsIgnoreCase(p.getPaymentStatus())).count();
    }
    public long getPendingCount() {
        ensureLoaded();
        return myPayments.stream().filter(p -> "Pending".equalsIgnoreCase(p.getPaymentStatus())).count();
    }
    public BigDecimal getTotalSpent() {
        ensureLoaded();
        return myPayments.stream()
                .filter(p -> "Paid".equalsIgnoreCase(p.getPaymentStatus()))
                .map(Payments::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ── getters / setters ────────────────────────────────────────
    public List<Registrations> getPayableRegistrations() { ensureLoaded(); return payableRegistrations; }
    public List<Payments>      getMyPayments()           { ensureLoaded(); return myPayments; }

    public int getRegistrationId()              { return registrationId; }
    public void setRegistrationId(int id)       { this.registrationId = id; }
    public String getEventTitle()               { return eventTitle; }
    public void setEventTitle(String t)         { this.eventTitle = t; }
    public BigDecimal getAmount()               { return amount; }
    public void setAmount(BigDecimal a)         { this.amount = a; }
    public String getPaymentMethod()            { return paymentMethod; }
    public void setPaymentMethod(String m)      { this.paymentMethod = m; }
    public String getUpiId()                    { return upiId; }
    public void setUpiId(String u)              { this.upiId = u; }
    public String getCardNumber()               { return cardNumber; }
    public void setCardNumber(String c)         { this.cardNumber = c; }
    public String getCardName()                 { return cardName; }
    public void setCardName(String c)           { this.cardName = c; }
    public String getCardExpiry()               { return cardExpiry; }
    public void setCardExpiry(String c)         { this.cardExpiry = c; }
    public String getCardCvv()                  { return cardCvv; }
    public void setCardCvv(String c)            { this.cardCvv = c; }
}
