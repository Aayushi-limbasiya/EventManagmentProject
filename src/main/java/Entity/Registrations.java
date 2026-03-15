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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
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
    @NamedQuery(name = "Registrations.findByEventId", query = "SELECT r FROM Registrations r WHERE r.eventId = :eventId"),
    @NamedQuery(name = "Registrations.findByUserId", query = "SELECT r FROM Registrations r WHERE r.userId = :userId"),
    @NamedQuery(name = "Registrations.findByStatus", query = "SELECT r FROM Registrations r WHERE r.status = :status"),
    @NamedQuery(name = "Registrations.findByAttendanceStatus", query = "SELECT r FROM Registrations r WHERE r.attendanceStatus = :attendanceStatus"),
    @NamedQuery(name = "Registrations.findByRegisteredAt", query = "SELECT r FROM Registrations r WHERE r.registeredAt = :registeredAt")})
public class Registrations implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "registration_id")
    private Long registrationId;
    @Column(name = "event_id")
    private Integer eventId;
    @Column(name = "user_id")
    private Integer userId;
    @Size(max = 20)
    @Column(name = "status")
    private String status;
    @Size(max = 20)
    @Column(name = "attendance_status")
    private String attendanceStatus;
    @Column(name = "registered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;

    public Registrations() {
    }

    public Registrations(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
