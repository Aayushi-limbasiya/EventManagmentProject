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
@Table(name = "auth_tokens")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuthTokens.findAll", query = "SELECT a FROM AuthTokens a"),
    @NamedQuery(name = "AuthTokens.findByTokenId", query = "SELECT a FROM AuthTokens a WHERE a.tokenId = :tokenId"),
    @NamedQuery(name = "AuthTokens.findByCreatedAt", query = "SELECT a FROM AuthTokens a WHERE a.createdAt = :createdAt"),
    @NamedQuery(name = "AuthTokens.findByExpiresAt", query = "SELECT a FROM AuthTokens a WHERE a.expiresAt = :expiresAt"),
    @NamedQuery(name = "AuthTokens.findByIsRevoked", query = "SELECT a FROM AuthTokens a WHERE a.isRevoked = :isRevoked"),
    // Find valid active token for a user
    @NamedQuery(name = "AuthTokens.findActiveByUser",
        query = "SELECT t FROM AuthTokens t WHERE t.userId.userId = :userId " +
                "AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP"),

    // Find token by token string value (for validation)
    @NamedQuery(name = "AuthTokens.findByToken",
        query = "SELECT t FROM AuthTokens t WHERE t.token = :token " +
                "AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP"),

    // Revoke all tokens for a user (logout all devices)
    @NamedQuery(name = "AuthTokens.revokeAllByUser",
        query = "UPDATE AuthTokens t SET t.isRevoked = true WHERE t.userId.userId = :userId"),

    // Find all tokens for a user
    @NamedQuery(name = "AuthTokens.findAllByUser",
        query = "SELECT t FROM AuthTokens t WHERE t.userId.userId = :userId ORDER BY t.createdAt DESC")



})
public class AuthTokens implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "token_id")
    private Integer tokenId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "token")
    private String token;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
    @Column(name = "is_revoked")
    private Boolean isRevoked;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;

    public AuthTokens() {
    }

    public AuthTokens(Integer tokenId) {
        this.tokenId = tokenId;
    }

    public AuthTokens(Integer tokenId, String token, Date expiresAt) {
        this.tokenId = tokenId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public Integer getTokenId() {
        return tokenId;
    }

    public void setTokenId(Integer tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsRevoked() {
        return isRevoked;
    }

    public void setIsRevoked(Boolean isRevoked) {
        this.isRevoked = isRevoked;
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
        hash += (tokenId != null ? tokenId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuthTokens)) {
            return false;
        }
        AuthTokens other = (AuthTokens) object;
        if ((this.tokenId == null && other.tokenId != null) || (this.tokenId != null && !this.tokenId.equals(other.tokenId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.AuthTokens[ tokenId=" + tokenId + " ]";
    }
    
}
