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
    @NamedQuery(name = "Certificates.findByCertificateNumber", query = "SELECT c FROM Certificates c WHERE c.certificateNumber = :certificateNumber"),
    @NamedQuery(name = "Certificates.findByIssueDate", query = "SELECT c FROM Certificates c WHERE c.issueDate = :issueDate"),
    @NamedQuery(name = "Certificates.findByCertificateFile", query = "SELECT c FROM Certificates c WHERE c.certificateFile = :certificateFile")})
public class Certificates implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "certificate_id")
    private Integer certificateId;
    @Size(max = 100)
    @Column(name = "certificate_number")
    private String certificateNumber;
    @Column(name = "issue_date")
    @Temporal(TemporalType.DATE)
    private Date issueDate;
    @Size(max = 255)
    @Column(name = "certificate_file")
    private String certificateFile;
    @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
    @ManyToOne
    private Registrations registrationId;

    public Certificates() {
    }

    public Certificates(Integer certificateId) {
        this.certificateId = certificateId;
    }

    public Integer getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Integer certificateId) {
        this.certificateId = certificateId;
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

    public Registrations getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Registrations registrationId) {
        this.registrationId = registrationId;
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
