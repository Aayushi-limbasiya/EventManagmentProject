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
import jakarta.persistence.Lob;
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
@Table(name = "approval")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Approval.findAll", query = "SELECT a FROM Approval a"),
    @NamedQuery(name = "Approval.findByApprovalId", query = "SELECT a FROM Approval a WHERE a.approvalId = :approvalId"),
    @NamedQuery(name = "Approval.findByEventId", query = "SELECT a FROM Approval a WHERE a.eventId = :eventId"),
    @NamedQuery(name = "Approval.findByAdminId", query = "SELECT a FROM Approval a WHERE a.adminId = :adminId"),
    @NamedQuery(name = "Approval.findByDecision", query = "SELECT a FROM Approval a WHERE a.decision = :decision"),
    @NamedQuery(name = "Approval.findByDecisionAt", query = "SELECT a FROM Approval a WHERE a.decisionAt = :decisionAt")})
public class Approval implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "approval_id")
    private Long approvalId;
    @Column(name = "event_id")
    private Integer eventId;
    @Column(name = "admin_id")
    private Integer adminId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "decision")
    private String decision;
    @Lob
    @Size(max = 65535)
    @Column(name = "remark")
    private String remark;
    @Basic(optional = false)
    @NotNull
    @Column(name = "decision_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date decisionAt;

    public Approval() {
    }

    public Approval(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Approval(Long approvalId, String decision, Date decisionAt) {
        this.approvalId = approvalId;
        this.decision = decision;
        this.decisionAt = decisionAt;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDecisionAt() {
        return decisionAt;
    }

    public void setDecisionAt(Date decisionAt) {
        this.decisionAt = decisionAt;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (approvalId != null ? approvalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Approval)) {
            return false;
        }
        Approval other = (Approval) object;
        if ((this.approvalId == null && other.approvalId != null) || (this.approvalId != null && !this.approvalId.equals(other.approvalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Approval[ approvalId=" + approvalId + " ]";
    }
    
}
