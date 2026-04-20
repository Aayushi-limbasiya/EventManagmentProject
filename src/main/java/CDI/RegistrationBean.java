/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.RegistrationBeanLocal;
import Entity.Registrations;
import jakarta.ejb.EJB;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Named("registrationBean")
@ViewScoped
public class RegistrationBean implements Serializable {

    @EJB
    private RegistrationBeanLocal registrationService;

    private List<Registrations> registrations = new ArrayList<>();

    private int userId;
    private int eventId;
    private int registrationId;

    private String qrValue;
    private long confirmedCount;

    // ===============================
    // 🔹 REGISTER FOR EVENT
    // ===============================
    public void register() {
        try {
            String status = registrationService.registerForEvent(userId, eventId);
            showMessage("Registration successful: " + status);
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 CANCEL REGISTRATION
    // ===============================
    public void cancel(int regId) {
        try {
            registrationService.cancelRegistration(regId);
            showMessage("Registration cancelled");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Error cancelling");
        }
    }

    // ===============================
    // 🔹 APPROVE REGISTRATION
    // ===============================
    public void approve(int regId) {
        try {
            registrationService.approveRegistration(regId);
            showMessage("Registration approved");
            loadByEvent();
        } catch (Exception e) {
            showMessage("Error approving");
        }
    }

    // ===============================
    // 🔹 MARK ATTENDANCE
    // ===============================
    public void markPresent(int regId) {
        try {
            registrationService.markAttendance(regId, eventId);
            showMessage("Marked Present");
        } catch (Exception e) {
            showMessage("Error marking attendance");
        }
    }

    public void markAbsent(int regId) {
        try {
            registrationService.markAbsent(regId);
            showMessage("Marked Absent");
        } catch (Exception e) {
            showMessage("Error marking absent");
        }
    }

    // ===============================
    // 🔹 LOAD DATA
    // ===============================
    public void loadByUser() {
        try {
            registrations = new ArrayList<>(
                registrationService.getRegisteredEventsByUser(userId)
            );
        } catch (Exception e) {
            showMessage("Error loading user registrations");
        }
    }

    public void loadByEvent() {
        try {
            registrations = new ArrayList<>(
                registrationService.getAllRegistrationsByEvent(eventId)
            );
        } catch (Exception e) {
            showMessage("Error loading event registrations");
        }
    }

    public void loadParticipants() {
        registrations = new ArrayList<>(
            registrationService.getParticipantsByEvent(eventId)
        );
    }

    public void loadWaitlist() {
        registrations = new ArrayList<>(
            registrationService.getWaitlistByEvent(eventId)
        );
    }

    public void loadPending() {
        registrations = new ArrayList<>(
            registrationService.getPendingApprovalsByEvent(eventId)
        );
    }

    // ===============================
    // 🔹 COUNT
    // ===============================
    public void loadConfirmedCount() {
        confirmedCount = registrationService.getConfirmedCount(eventId);
    }

    // ===============================
    // 🔹 QR CODE
    // ===============================
    public void generateQR(int regId) {
        try {
            qrValue = registrationService.generateQRCodeValue(regId);
            showMessage("QR Generated");
        } catch (Exception e) {
            showMessage("Error generating QR");
        }
    }

    // ===============================
    // 🔹 CHECK REGISTRATION
    // ===============================
    public boolean isAlreadyRegistered() {
        return registrationService.isAlreadyRegistered(userId, eventId);
    }

    // ===============================
    // 🔹 MESSAGE HELPER
    // ===============================
    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ===============================
    // 🔹 GETTERS & SETTERS
    // ===============================

    public List<Registrations> getRegistrations() {
        return registrations;
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

    public String getQrValue() {
        return qrValue;
    }

    public long getConfirmedCount() {
        return confirmedCount;
    }
}