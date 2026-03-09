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
@Table(name = "registration")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Registration.findAll", query = "SELECT r FROM Registration r"),
    @NamedQuery(name = "Registration.findByRegistrationId", query = "SELECT r FROM Registration r WHERE r.registrationId = :registrationId"),
    @NamedQuery(name = "Registration.findByEventId", query = "SELECT r FROM Registration r WHERE r.eventId = :eventId"),
    @NamedQuery(name = "Registration.findByStudentId", query = "SELECT r FROM Registration r WHERE r.studentId = :studentId"),
    @NamedQuery(name = "Registration.findByStatus", query = "SELECT r FROM Registration r WHERE r.status = :status"),
    @NamedQuery(name = "Registration.findByRegisteredAt", query = "SELECT r FROM Registration r WHERE r.registeredAt = :registeredAt"),
    @NamedQuery(name = "Registration.findByAttendanceStatus", query = "SELECT r FROM Registration r WHERE r.attendanceStatus = :attendanceStatus")})
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "registration_id")
    private Long registrationId;
    @Column(name = "event_id")
    private Integer eventId;
    @Column(name = "student_id")
    private Integer studentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "registered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registeredAt;
    @Size(max = 20)
    @Column(name = "attendance_status")
    private String attendanceStatus;

    public Registration() {
    }

    public Registration(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Registration(Long registrationId, String status, Date registeredAt) {
        this.registrationId = registrationId;
        this.status = status;
        this.registeredAt = registeredAt;
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

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
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
        if (!(object instanceof Registration)) {
            return false;
        }
        Registration other = (Registration) object;
        if ((this.registrationId == null && other.registrationId != null) || (this.registrationId != null && !this.registrationId.equals(other.registrationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Registration[ registrationId=" + registrationId + " ]";
    }
    
}
