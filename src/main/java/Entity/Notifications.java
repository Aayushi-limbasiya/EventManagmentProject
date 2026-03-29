/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author OS
 */
@Entity
@Table(name = "notifications")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Notifications.findAll", query = "SELECT n FROM Notifications n"),
    @NamedQuery(name = "Notifications.findByNotificationId", query = "SELECT n FROM Notifications n WHERE n.notificationId = :notificationId"),
    @NamedQuery(name = "Notifications.findByType", query = "SELECT n FROM Notifications n WHERE n.type = :type"),
    @NamedQuery(name = "Notifications.findByChannel", query = "SELECT n FROM Notifications n WHERE n.channel = :channel"),
    @NamedQuery(name = "Notifications.findByStatus", query = "SELECT n FROM Notifications n WHERE n.status = :status"),
    @NamedQuery(name = "Notifications.findBySentAt", query = "SELECT n FROM Notifications n WHERE n.sentAt = :sentAt"),
      // Get all notifications for a specific user
    @NamedQuery(name = "Notifications.findByUser",
        query = "SELECT n FROM Notifications n WHERE n.userId.userId = :userId ORDER BY n.sentAt DESC"),

    // Get unread notifications for a user (status = Unread)
    @NamedQuery(name = "Notifications.getUnreadByUser",
        query = "SELECT n FROM Notifications n WHERE n.userId.userId = :userId AND n.status = 'Unread' ORDER BY n.sentAt DESC"),

    // Get read notifications for a user (status = Read)
    @NamedQuery(name = "Notifications.getReadByUser",
        query = "SELECT n FROM Notifications n WHERE n.userId.userId = :userId AND n.status = 'Read' ORDER BY n.sentAt DESC"),

    // Get notifications by user and type
    // type values: Registration / Payment / Approval / Reminder / Broadcast
    @NamedQuery(name = "Notifications.findByUserAndType",
        query = "SELECT n FROM Notifications n WHERE n.userId.userId = :userId AND n.type = :type ORDER BY n.sentAt DESC"),

    // Get notifications by user and channel
    // channel values: Email / System / SMS
    @NamedQuery(name = "Notifications.findByUserAndChannel",
        query = "SELECT n FROM Notifications n WHERE n.userId.userId = :userId AND n.channel = :channel ORDER BY n.sentAt DESC"),

    // Count unread notifications for a user (badge count)
    @NamedQuery(name = "Notifications.countUnreadByUser",
        query = "SELECT COUNT(n) FROM Notifications n WHERE n.userId.userId = :userId AND n.status = 'Unread'"),

    // Get all broadcast notifications (type = Broadcast)
    @NamedQuery(name = "Notifications.getBroadcasts",
        query = "SELECT n FROM Notifications n WHERE n.type = 'Broadcast' ORDER BY n.sentAt DESC"),

    // Get all event reminder notifications (type = Reminder)
    @NamedQuery(name = "Notifications.getReminders",
        query = "SELECT n FROM Notifications n WHERE n.type = 'Reminder' ORDER BY n.sentAt DESC"),

    // Mark all notifications as Read for a user (bulk update)
    @NamedQuery(name = "Notifications.markAllReadByUser",
        query = "UPDATE Notifications n SET n.status = 'Read' WHERE n.userId.userId = :userId AND n.status = 'Unread'"),

    // Get latest N notifications for a user
    @NamedQuery(name = "Notifications.getLatestByUser",
        query = "SELECT n FROM Notifications n WHERE n.userId.userId = :userId ORDER BY n.sentAt DESC")
})

public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "notification_id")
    private Integer notificationId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "message")
    private String message;
    @Size(max = 30)
    @Column(name = "type")
    private String type;
    @Size(max = 20)
    @Column(name = "channel")
    private String channel;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;

    public Notifications() {
    }

    public Notifications(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Notifications(Integer notificationId, String message) {
        this.notificationId = notificationId;
        this.message = message;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationId != null ? notificationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notifications)) {
            return false;
        }
        Notifications other = (Notifications) object;
        if ((this.notificationId == null && other.notificationId != null) || (this.notificationId != null && !this.notificationId.equals(other.notificationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Notifications[ notificationId=" + notificationId + " ]";
    }
    
}
