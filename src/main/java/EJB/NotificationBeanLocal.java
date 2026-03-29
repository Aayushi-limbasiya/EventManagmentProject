/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/SessionLocal.java to edit this template
 */
package EJB;

import Entity.Notifications;
import jakarta.ejb.Local;
import java.util.Collection;

/**
 *
 * @author OS
 */
@Local
public interface NotificationBeanLocal {
     void sendNotification(int userId, String message, String type, String channel);

    /**
     * Get all notifications for a specific user
     * Ordered by most recent first
     */
    Collection<Notifications> getNotificationsByUser(int userId);

    /**
     * Get notification by ID
     */
    Notifications getNotificationById(int notificationId);

    // ── EXTRA FUNCTIONS ───────────────────────────────────────

    /**
     * Send email notification
     * channel = Email
     * Calls sendNotification internally + prints email to console
     * TODO: Replace with JavaMail / SendGrid
     */
    void sendEmailNotification(int userId, String subject, String message, String type);

    /**
     * Send system (in-app) notification
     * channel = System
     */
    void sendSystemNotification(int userId, String message, String type);

    /**
     * Send SMS notification (optional)
     * channel = SMS
     * TODO: Replace with Twilio / MSG91
     */
    void sendSmsNotification(int userId, String message, String type);

    /**
     * Send event reminder notification to all confirmed participants
     * type = Reminder
     * Sends via both Email and System channel
     */
    void sendEventReminder(int eventId, String reminderMessage);

    /**
     * Send registration confirmation notification to a participant
     * type = Registration
     * channel = Email + System
     */
    void sendRegistrationConfirmation(int userId, int eventId);

    /**
     * Send payment confirmation notification to a participant
     * type = Payment
     * channel = Email + System
     */
    void sendPaymentConfirmation(int userId, int paymentId);

    /**
     * Admin broadcast message to ALL users
     * type = Broadcast
     * channel = System
     */
    void broadcastToAllUsers(String message);

    /**
     * Mark a single notification as Read
     */
    void markAsRead(int notificationId);

    /**
     * Mark a single notification as Unread
     */
    void markAsUnread(int notificationId);

    /**
     * Mark ALL notifications as Read for a user (bulk)
     */
    void markAllAsRead(int userId);

    /**
     * Get all unread notifications for a user
     */
    Collection<Notifications> getUnreadNotifications(int userId);

    /**
     * Get all read notifications for a user
     */
    Collection<Notifications> getReadNotifications(int userId);

    /**
     * Count unread notifications for a user (for badge/counter display)
     */
    long countUnreadNotifications(int userId);

    /**
     * Get notifications filtered by type for a user
     * type: Registration / Payment / Approval / Reminder / Broadcast
     */
    Collection<Notifications> getNotificationsByType(int userId, String type);

    /**
     * Get notifications filtered by channel for a user
     * channel: Email / System / SMS
     */
    Collection<Notifications> getNotificationsByChannel(int userId, String channel);

    /**
     * Get latest N notifications for a user
     */
    Collection<Notifications> getLatestNotifications(int userId, int limit);

    /**
     * Get all broadcast notifications (admin sent)
     */
    Collection<Notifications> getAllBroadcasts();

    /**
     * Get all event reminder notifications
     */
    Collection<Notifications> getAllReminders();

    /**
     * Delete a notification by ID
     */
    void deleteNotification(int notificationId);
}
