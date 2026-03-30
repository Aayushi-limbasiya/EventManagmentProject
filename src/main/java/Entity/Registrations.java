/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "registrations")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Registrations.findAll", query = "SELECT r FROM Registrations r"),
    @NamedQuery(name = "Registrations.findByRegistrationId", query = "SELECT r FROM Registrations r WHERE r.registrationId = :registrationId"),
    @NamedQuery(name = "Registrations.findByStatus", query = "SELECT r FROM Registrations r WHERE r.status = :status"),
    @NamedQuery(name = "Registrations.findByAttendanceStatus", query = "SELECT r FROM Registrations r WHERE r.attendanceStatus = :attendanceStatus"),
    @NamedQuery(name = "Registrations.findByRegisteredAt", query = "SELECT r FROM Registrations r WHERE r.registeredAt = :registeredAt"),
     @NamedQuery(name = "Registrations.findByEvent",
        query = "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId"),

    // Get all registrations by a specific user
    @NamedQuery(name = "Registrations.findByUser",
        query = "SELECT r FROM Registrations r WHERE r.userId.userId = :userId"),
    
     @NamedQuery(name = "Registrations.checkAlreadyRegistered",
        query = "SELECT r FROM Registrations r WHERE r.userId.userId = :userId AND r.eventId.eventId = :eventId"),

    // Count confirmed registrations for an event (for capacity check)
    @NamedQuery(name = "Registrations.countConfirmedByEvent",
        query = "SELECT COUNT(r) FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.status = 'Confirmed'"),

    // Get waitlisted registrations for an event
    @NamedQuery(name = "Registrations.getWaitlistByEvent",
        query = "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.status = 'Waitlist' ORDER BY r.registeredAt ASC"),

    // Get participant list for an event (organizer view)
    @NamedQuery(name = "Registrations.getParticipantsByEvent",
        query = "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.status = 'Confirmed'"),

    // Get pending approval registrations for an event
    @NamedQuery(name = "Registrations.getPendingApprovalByEvent",
        query = "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.status = 'Pending'"),

    // Get registrations by event and attendance status
    @NamedQuery(name = "Registrations.findByEventAndAttendance",
        query = "SELECT r FROM Registrations r WHERE r.eventId.eventId = :eventId AND r.attendanceStatus = :attendanceStatus"),

    // Get registration by QR / registration ID for check-in
    @NamedQuery(name = "Registrations.findForCheckIn",
        query = "SELECT r FROM Registrations r WHERE r.registrationId = :registrationId AND r.eventId.eventId = :eventId")

})
public class Registrations implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "registration_id")
    private Integer registrationId;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Size(max = 20)
    @Column(name = "attendance_status")
    private String attendanceStatus;
    @Column(name = "registered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    @ManyToOne
    private Events eventId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;
    @OneToMany(mappedBy = "registrationId")
    private Collection<Certificates> certificatesCollection;
    @OneToMany(mappedBy = "registrationId")
    private Collection<Payments> paymentsCollection;

    public Registrations() {
    }

    public Registrations(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Events getEventId() {
        return eventId;
    }

    public void setEventId(Events eventId) {
        this.eventId = eventId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Certificates> getCertificatesCollection() {
        return certificatesCollection;
    }

    public void setCertificatesCollection(Collection<Certificates> certificatesCollection) {
        this.certificatesCollection = certificatesCollection;
    }

    @XmlTransient
    @JsonbTransient
    public Collection<Payments> getPaymentsCollection() {
        return paymentsCollection;
    }

    public void setPaymentsCollection(Collection<Payments> paymentsCollection) {
        this.paymentsCollection = paymentsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (registrationId != null ? registrationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Registrations)) {
            return false;
        }
        Registrations other = (Registrations) object;
        if ((this.registrationId == null && other.registrationId != null) || (this.registrationId != null && !this.registrationId.equals(other.registrationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Registrations[ registrationId=" + registrationId + " ]";
    }
    
}
