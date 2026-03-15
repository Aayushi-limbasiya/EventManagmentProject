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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author OS
 */
@Entity
@Table(name = "venues")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Venues.findAll", query = "SELECT v FROM Venues v"),
    @NamedQuery(name = "Venues.findByVenueId", query = "SELECT v FROM Venues v WHERE v.venueId = :venueId"),
    @NamedQuery(name = "Venues.findByName", query = "SELECT v FROM Venues v WHERE v.name = :name"),
    @NamedQuery(name = "Venues.findByCapacity", query = "SELECT v FROM Venues v WHERE v.capacity = :capacity"),
    @NamedQuery(name = "Venues.findByLocation", query = "SELECT v FROM Venues v WHERE v.location = :location")})
public class Venues implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "venue_id")
    private Long venueId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "capacity")
    private int capacity;
    @Size(max = 200)
    @Column(name = "location")
    private String location;

    public Venues() {
    }

    public Venues(Long venueId) {
        this.venueId = venueId;
    }

    public Venues(Long venueId, String name, int capacity) {
        this.venueId = venueId;
        this.name = name;
        this.capacity = capacity;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (venueId != null ? venueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Venues)) {
            return false;
        }
        Venues other = (Venues) object;
        if ((this.venueId == null && other.venueId != null) || (this.venueId != null && !this.venueId.equals(other.venueId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Venues[ venueId=" + venueId + " ]";
    }
    
}
