/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Registrations;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author OS
 */
@Local
public interface RegistrationBeanLocal {
    String registerForEvent(int userId, int eventId);

    void cancelRegistration(int registrationId);

    Collection<Registrations> getRegisteredEventsByUser(int userId);

    
    Collection<Registrations> getWaitlistByEvent(int eventId);

    void approveRegistration(int registrationId);

    void markAttendance(int registrationId, int eventId);

   
    void markAbsent(int registrationId);

    Collection<Registrations> getParticipantsByEvent(int eventId);

  
    Collection<Registrations> getPendingApprovalsByEvent(int eventId);

    Registrations getRegistrationById(int registrationId);

    Collection<Registrations> getAllRegistrationsByEvent(int eventId);

    boolean isAlreadyRegistered(int userId, int eventId);

    
    long getConfirmedCount(int eventId);

    Collection<Registrations> getByEventAndAttendance(int eventId, String attendanceStatus);

    Registrations getRegistrationConfirmation(int registrationId);

    String generateQRCodeValue(int registrationId);
}
