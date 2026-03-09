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
@Table(name = "certificates")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Certificates.findAll", query = "SELECT c FROM Certificates c"),
    @NamedQuery(name = "Certificates.findByCertificateId", query = "SELECT c FROM Certificates c WHERE c.certificateId = :certificateId"),
    @NamedQuery(name = "Certificates.findByEventId", query = "SELECT c FROM Certificates c WHERE c.eventId = :eventId"),
    @NamedQuery(name = "Certificates.findByUserId", query = "SELECT c FROM Certificates c WHERE c.userId = :userId"),
    @NamedQuery(name = "Certificates.findByRegistrationId", query = "SELECT c FROM Certificates c WHERE c.registrationId = :registrationId"),
    @NamedQuery(name = "Certificates.findByCertificateNumber", query = "SELECT c FROM Certificates c WHERE c.certificateNumber = :certificateNumber"),
    @NamedQuery(name = "Certificates.findByIssueDate", query = "SELECT c FROM Certificates c WHERE c.issueDate = :issueDate"),
    @NamedQuery(name = "Certificates.findByCertificateFile", query = "SELECT c FROM Certificates c WHERE c.certificateFile = :certificateFile")})
public class Certificates implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "certificate_id")
    private Long certificateId;
    @Column(name = "event_id")
    private Integer eventId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "registration_id")
    private Integer registrationId;
    @Size(max = 100)
    @Column(name = "certificate_number")
    private String certificateNumber;
    @Column(name = "issue_date")
    @Temporal(TemporalType.DATE)
    private Date issueDate;
    @Size(max = 255)
    @Column(name = "certificate_file")
    private String certificateFile;

    public Certificates() {
    }

    public Certificates(Long certificateId) {
        this.certificateId = certificateId;
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
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

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(String certificateFile) {
        this.certificateFile = certificateFile;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (certificateId != null ? certificateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Certificates)) {
            return false;
        }
        Certificates other = (Certificates) object;
        if ((this.certificateId == null && other.certificateId != null) || (this.certificateId != null && !this.certificateId.equals(other.certificateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Certificates[ certificateId=" + certificateId + " ]";
    }
    
}
