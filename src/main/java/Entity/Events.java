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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author OS
 */
@Entity
@Table(name = "events")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Events.findAll", query = "SELECT e FROM Events e"),
    @NamedQuery(name = "Events.findByEventId", query = "SELECT e FROM Events e WHERE e.eventId = :eventId"),
    @NamedQuery(name = "Events.findByTitle", query = "SELECT e FROM Events e WHERE e.title = :title"),
    @NamedQuery(name = "Events.findByStatus", query = "SELECT e FROM Events e WHERE e.status = :status"),
    @NamedQuery(name = "Events.findByCreatedAt", query = "SELECT e FROM Events e WHERE e.createdAt = :createdAt"),
     @NamedQuery(name = "Events.searchByKeyword",
        query = "SELECT e FROM Events e WHERE e.title LIKE :keyword OR e.description LIKE :keyword"),

    // 🔹 FILTERS
    @NamedQuery(name = "Events.findByOrganizer",
        query = "SELECT e FROM Events e WHERE e.userId.userId = :userId"),

    // 🔹 STATUS BASED FILTER
    @NamedQuery(name = "Events.findApprovedEvents",
        query = "SELECT e FROM Events e WHERE e.status = 'Approved'"),

    @NamedQuery(name = "Events.findPendingEvents",
        query = "SELECT e FROM Events e WHERE e.status = 'Pending'"),

    @NamedQuery(name = "Events.findDraftEvents",
        query = "SELECT e FROM Events e WHERE e.status = 'Draft'"),

    @NamedQuery(name = "Events.findCompletedEvents",
        query = "SELECT e FROM Events e WHERE e.status = 'Completed'"),

    // 🔹 UPCOMING & PAST EVENTS
    @NamedQuery(name = "Events.findUpcomingEvents",
        query = "SELECT e FROM Events e WHERE e.createdAt >= :today"),

    @NamedQuery(name = "Events.findPastEvents",
        query = "SELECT e FROM Events e WHERE e.createdAt < :today")

})
public class Events implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_id")
    private Integer eventId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Size(max = 30)
    @Column(name = "status")
    private String status;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @OneToMany(mappedBy = "eventId")
    private Collection<Feedback> feedbackCollection;
    @OneToMany(mappedBy = "eventId")
    private Collection<Registrations> registrationsCollection;
    @OneToMany(mappedBy = "eventId")
    private Collection<Approvals> approvalsCollection;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;
    @OneToMany(mappedBy = "eventId")
    private Collection<EventSchedule> eventScheduleCollection;

    public Events() {
    }

    public Events(Integer eventId) {
        this.eventId = eventId;
    }

    public Events(Integer eventId, String title) {
        this.eventId = eventId;
        this.title = title;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @XmlTransient
    public Collection<Feedback> getFeedbackCollection() {
        return feedbackCollection;
    }

    public void setFeedbackCollection(Collection<Feedback> feedbackCollection) {
        this.feedbackCollection = feedbackCollection;
    }

    @XmlTransient
    public Collection<Registrations> getRegistrationsCollection() {
        return registrationsCollection;
    }

    public void setRegistrationsCollection(Collection<Registrations> registrationsCollection) {
        this.registrationsCollection = registrationsCollection;
    }

    @XmlTransient
    public Collection<Approvals> getApprovalsCollection() {
        return approvalsCollection;
    }

    public void setApprovalsCollection(Collection<Approvals> approvalsCollection) {
        this.approvalsCollection = approvalsCollection;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @XmlTransient
    public Collection<EventSchedule> getEventScheduleCollection() {
        return eventScheduleCollection;
    }

    public void setEventScheduleCollection(Collection<EventSchedule> eventScheduleCollection) {
        this.eventScheduleCollection = eventScheduleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Events)) {
            return false;
        }
        Events other = (Events) object;
        if ((this.eventId == null && other.eventId != null) || (this.eventId != null && !this.eventId.equals(other.eventId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Events[ eventId=" + eventId + " ]";
    }
    
}
