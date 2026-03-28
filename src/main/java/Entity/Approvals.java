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
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author OS
 */
@Entity
@Table(name = "approvals")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Approvals.findAll", query = "SELECT a FROM Approvals a"),
    @NamedQuery(name = "Approvals.findByApprovalId", query = "SELECT a FROM Approvals a WHERE a.approvalId = :approvalId"),
    @NamedQuery(name = "Approvals.findByDecision", query = "SELECT a FROM Approvals a WHERE a.decision = :decision"),
    @NamedQuery(name = "Approvals.findByDecisionAt", query = "SELECT a FROM Approvals a WHERE a.decisionAt = :decisionAt")})
public class Approvals implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "approval_id")
    private Integer approvalId;
    @Size(max = 20)
    @Column(name = "decision")
    private String decision;
    @Lob
    @Size(max = 65535)
    @Column(name = "remark")
    private String remark;
    @Column(name = "decision_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date decisionAt;
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    @ManyToOne
    private Events eventId;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    private Users userId;

    public Approvals() {
    }

    public Approvals(Integer approvalId) {
        this.approvalId = approvalId;
    }

    public Integer getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Integer approvalId) {
        this.approvalId = approvalId;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (approvalId != null ? approvalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Approvals)) {
            return false;
        }
        Approvals other = (Approvals) object;
        if ((this.approvalId == null && other.approvalId != null) || (this.approvalId != null && !this.approvalId.equals(other.approvalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Approvals[ approvalId=" + approvalId + " ]";
    }
    
}
