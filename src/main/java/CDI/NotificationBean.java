/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.NotificationBeanLocal;
import Entity.Notifications;
import jakarta.ejb.EJB;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("notificationBean")
@ViewScoped
public class NotificationBean implements Serializable {

    @EJB
    private NotificationBeanLocal notificationService;

    private List<Notifications> notifications = new ArrayList<>();

    private int userId;
    private int notificationId;
    private int eventId;

    private String message;
    private String type;
    private String channel;

    private long unreadCount;

    // ===============================
    // 🔹 LOAD NOTIFICATIONS
    // ===============================
    public void loadAll() {
        notifications = new ArrayList<>(
            notificationService.getNotificationsByUser(userId)
        );
    }

    public void loadUnread() {
        notifications = new ArrayList<>(
            notificationService.getUnreadNotifications(userId)
        );
    }

    public void loadRead() {
        notifications = new ArrayList<>(
            notificationService.getReadNotifications(userId)
        );
    }

    public void loadLatest(int limit) {
        notifications = new ArrayList<>(
            notificationService.getLatestNotifications(userId, limit)
        );
    }

    public void loadByType() {
        notifications = new ArrayList<>(
            notificationService.getNotificationsByType(userId, type)
        );
    }

    public void loadByChannel() {
        notifications = new ArrayList<>(
            notificationService.getNotificationsByChannel(userId, channel)
        );
    }

    // ===============================
    // 🔹 COUNT (Badge)
    // ===============================
    public void loadUnreadCount() {
        unreadCount = notificationService.countUnreadNotifications(userId);
    }

    // ===============================
    // 🔹 SEND NOTIFICATIONS
    // ===============================
    public void send() {
        try {
            notificationService.sendNotification(userId, message, type, channel);
            showMessage("Notification sent");
        } catch (Exception e) {
            showMessage("Error sending notification");
        }
    }

    public void sendReminder() {
        try {
            notificationService.sendEventReminder(eventId, message);
            showMessage("Reminder sent");
        } catch (Exception e) {
            showMessage("Error sending reminder");
        }
    }

    public void broadcast() {
        try {
            notificationService.broadcastToAllUsers(message);
            showMessage("Broadcast sent");
        } catch (Exception e) {
            showMessage("Error broadcasting");
        }
    }

    // ===============================
    // 🔹 MARK READ / UNREAD
    // ===============================
    public void markRead(int id) {
        notificationService.markAsRead(id);
        loadAll();
    }

    public void markUnread(int id) {
        notificationService.markAsUnread(id);
        loadAll();
    }

    public void markAllRead() {
        notificationService.markAllAsRead(userId);
        loadAll();
    }

    // ===============================
    // 🔹 DELETE
    // ===============================
    public void delete(int id) {
        notificationService.deleteNotification(id);
        showMessage("Notification deleted");
        loadAll();
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

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}