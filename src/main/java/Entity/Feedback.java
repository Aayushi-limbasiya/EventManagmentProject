package Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "feedback")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feedback.findAll", query = "SELECT f FROM Feedback f"),
    @NamedQuery(name = "Feedback.findByFeedbackId", query = "SELECT f FROM Feedback f WHERE f.feedbackId = :feedbackId"),
    @NamedQuery(name = "Feedback.findByRating", query = "SELECT f FROM Feedback f WHERE f.rating = :rating"),
    @NamedQuery(name = "Feedback.findByCreatedAt", query = "SELECT f FROM Feedback f WHERE f.createdAt = :createdAt"),
    @NamedQuery(name = "Feedback.findByEvent",
        query = "SELECT f FROM Feedback f WHERE f.eventId.eventId = :eventId ORDER BY f.createdAt DESC"),
    @NamedQuery(name = "Feedback.findByUser",
        query = "SELECT f FROM Feedback f WHERE f.userId.userId = :userId ORDER BY f.createdAt DESC"),
    @NamedQuery(name = "Feedback.checkAlreadySubmitted",
        query = "SELECT f FROM Feedback f WHERE f.userId.userId = :userId AND f.eventId.eventId = :eventId"),
    @NamedQuery(name = "Feedback.getAverageRatingByEvent",
        query = "SELECT AVG(f.rating) FROM Feedback f WHERE f.eventId.eventId = :eventId"),
    @NamedQuery(name = "Feedback.countByEvent",
        query = "SELECT COUNT(f) FROM Feedback f WHERE f.eventId.eventId = :eventId"),
    @NamedQuery(name = "Feedback.findByEventAndRating",
        query = "SELECT f FROM Feedback f WHERE f.eventId.eventId = :eventId AND f.rating = :rating"),
    @NamedQuery(name = "Feedback.getTopRatedEvents",
        query = "SELECT f.eventId.eventId, AVG(f.rating) AS avgRating FROM Feedback f GROUP BY f.eventId.eventId ORDER BY avgRating DESC"),
    @NamedQuery(name = "Feedback.findByOrganizer",
        query = "SELECT f FROM Feedback f WHERE f.eventId.userId.userId = :organizerId ORDER BY f.createdAt DESC"),
    @NamedQuery(name = "Feedback.getRatingDistribution",
        query = "SELECT f.rating, COUNT(f) FROM Feedback f WHERE f.eventId.eventId = :eventId GROUP BY f.rating ORDER BY f.rating ASC")
})
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @Column(name = "rating")
    private Integer rating;

    @Lob
    @Size(max = 65535)
    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // ── FIXES circular JSON loop ──────────────────────────────────
    @JsonIgnoreProperties({
        "feedbackCollection", "registrationCollection",
        "approvalCollection", "notificationCollection",
        "eventScheduleCollection", "password",
        "resetToken", "resetTokenExpiry", "userId"
    })
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    @ManyToOne
    private Events eventId;

    // ── FIXES circular JSON loop ──────────────────────────────────
    @JsonIgnoreProperties({
        "feedbackCollection", "registrationCollection",
        "approvalCollection", "notificationCollection",
        "password", "resetToken", "resetTokenExpiry"
    })
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;

    public Feedback() {}

    public Feedback(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getFeedbackId() { return feedbackId; }
    public void setFeedbackId(Integer feedbackId) { this.feedbackId = feedbackId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Events getEventId() { return eventId; }
    public void setEventId(Events eventId) { this.eventId = eventId; }

    public Users getUserId() { return userId; }
    public void setUserId(Users userId) { this.userId = userId; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (feedbackId != null ? feedbackId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Feedback)) return false;
        Feedback other = (Feedback) object;
        return !((this.feedbackId == null && other.feedbackId != null) ||
                 (this.feedbackId != null && !this.feedbackId.equals(other.feedbackId)));
    }

    @Override
    public String toString() {
        return "Entity.Feedback[ feedbackId=" + feedbackId + " ]";
    }
}