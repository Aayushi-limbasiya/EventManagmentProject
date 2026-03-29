/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Events;
import Entity.Notifications;
import Entity.Payments;
import Entity.Registrations;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author OS
 */
@Stateless
public class NotificationBean implements NotificationBeanLocal {
    
    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    @Override
    public void sendNotification(int userId, String message, String type, String channel) {
        
        // Validate user
        Users user = em.find(Users.class, userId);
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Validate channel
        if (!channel.equals("Email") && !channel.equals("System") && !channel.equals("SMS")) {
            throw new RuntimeException("Invalid channel: " + channel
                + ". Allowed: Email, System, SMS");
        }

        // Validate type
        if (!type.equals("Registration") && !type.equals("Payment") &&
            !type.equals("Approval") && !type.equals("Reminder") &&
            !type.equals("Broadcast")) {
            throw new RuntimeException("Invalid type: " + type
                + ". Allowed: Registration, Payment, Approval, Reminder, Broadcast");
        }

        // Create notification record
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setChannel(channel);
        notification.setStatus("Unread");
        notification.setSentAt(new Date());
        em.persist(notification);
    }

    @Override
    public Collection<Notifications> getNotificationsByUser(int userId) {
        TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.findByUser", Notifications.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public Notifications getNotificationById(int notificationId) {
      return em.find(Notifications.class, notificationId);
    }

    @Override
    public void sendEmailNotification(int userId, String subject, String message, String type) {
         // Save to DB
        sendNotification(userId, message, type, "Email");

        // TODO: Replace with real email service
        Users user = em.find(Users.class, userId);
        System.out.println("====== EMAIL SENT ======");
        System.out.println("To      : " + user.getEmail());
        System.out.println("Subject : " + subject);
        System.out.println("Message : " + message);
        System.out.println("========================");
    }

    @Override
    public void sendSystemNotification(int userId, String message, String type) {
         sendNotification(userId, message, type, "System");
    }

    @Override
    public void sendSmsNotification(int userId, String message, String type) {
          sendNotification(userId, message, type, "SMS");

        Users user = em.find(Users.class, userId);
        System.out.println("====== SMS SENT ======");
        System.out.println("To      : " + user.getPhone());
        System.out.println("Message : " + message);
        System.out.println("======================");
    }

    @Override
    public void sendEventReminder(int eventId, String reminderMessage) {
         // Get all confirmed registrations for this event
        TypedQuery<Registrations> q = em.createQuery(
            "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.status = 'Confirmed'",
            Registrations.class);
        q.setParameter("eventId", eventId);
        Collection<Registrations> registrations = q.getResultList();

        if (registrations.isEmpty()) {
            System.out.println("No confirmed participants found for event ID: " + eventId);
            return;
        }

        // Send to each confirmed participant
        for (Registrations reg : registrations) {
            int userId = reg.getUserId().getUserId();
            // Send via both Email and System channel
            sendNotification(userId, reminderMessage, "Reminder", "Email");
            sendNotification(userId, reminderMessage, "Reminder", "System");
        }

        System.out.println("Event reminder sent to " + registrations.size()
            + " participants for event ID: " + eventId);
    }

    @Override
    public void sendRegistrationConfirmation(int userId, int eventId) {
         Events event = em.find(Events.class, eventId);
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + eventId);
        }

        String message = "Your registration for event \"" + event.getTitle()
            + "\" has been confirmed. Thank you!";

        // Send via Email
        sendEmailNotification(userId, "Registration Confirmed - " + event.getTitle(),
            message, "Registration");

        // Send via System
        sendSystemNotification(userId, message, "Registration");
    }

    @Override
    public void sendPaymentConfirmation(int userId, int paymentId) {
          Payments payment = em.find(Payments.class, paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }

        String eventTitle = payment.getRegistrationId().getEventId().getTitle();
        String message = "Payment of Rs. " + payment.getAmount()
            + " for event \"" + eventTitle + "\" has been received successfully."
            + " Payment ID: " + paymentId;

        // Send via Email
        sendEmailNotification(userId, "Payment Confirmed - " + eventTitle,
            message, "Payment");

        // Send via System
        sendSystemNotification(userId, message, "Payment");
    }

    @Override
    public void broadcastToAllUsers(String message) {
         TypedQuery<Users> q = em.createNamedQuery("Users.findAll", Users.class);
        Collection<Users> allUsers = q.getResultList();

        if (allUsers.isEmpty()) {
            System.out.println("No users found to broadcast.");
            return;
        }

        for (Users user : allUsers) {
            sendNotification(user.getUserId(), message, "Broadcast", "System");
        }

        System.out.println("Broadcast sent to " + allUsers.size() + " users.");
    }

    @Override
    public void markAsRead(int notificationId) {
        Notifications n = em.find(Notifications.class, notificationId);
        if (n == null) {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        n.setStatus("Read");
        em.merge(n);
    }

    @Override
    public void markAsUnread(int notificationId) {
         Notifications n = em.find(Notifications.class, notificationId);
        if (n == null) {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        n.setStatus("Unread");
        em.merge(n);
    }

    @Override
    public void markAllAsRead(int userId) {
           int updated = em.createNamedQuery("Notifications.markAllReadByUser")
            .setParameter("userId", userId)
            .executeUpdate();
        System.out.println("Marked " + updated + " notifications as Read for user ID: " + userId);
    }

    @Override
    public Collection<Notifications> getUnreadNotifications(int userId) {
         TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.getUnreadByUser", Notifications.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public Collection<Notifications> getReadNotifications(int userId) {
          TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.getReadByUser", Notifications.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    @Override
    public long countUnreadNotifications(int userId) {
           TypedQuery<Long> q =
            em.createNamedQuery("Notifications.countUnreadByUser", Long.class);
        q.setParameter("userId", userId);
        return q.getSingleResult();
    }

    @Override
    public Collection<Notifications> getNotificationsByType(int userId, String type) {
           TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.findByUserAndType", Notifications.class);
        q.setParameter("userId", userId);
        q.setParameter("type", type);
        return q.getResultList();
    }

    @Override
    public Collection<Notifications> getNotificationsByChannel(int userId, String channel) {
          TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.findByUserAndChannel", Notifications.class);
        q.setParameter("userId", userId);
        q.setParameter("channel", channel);
        return q.getResultList();
    }

    @Override
    public Collection<Notifications> getLatestNotifications(int userId, int limit) {
         TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.getLatestByUser", Notifications.class);
        q.setParameter("userId", userId);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public Collection<Notifications> getAllBroadcasts() {
          TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.getBroadcasts", Notifications.class);
        return q.getResultList();
    }

    @Override
    public Collection<Notifications> getAllReminders() {
          TypedQuery<Notifications> q =
            em.createNamedQuery("Notifications.getReminders", Notifications.class);
        return q.getResultList();
    }

    @Override
    public void deleteNotification(int notificationId) {
         Notifications n = em.find(Notifications.class, notificationId);
        if (n != null) {
            em.remove(n);
        } else {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
    }

  
}
