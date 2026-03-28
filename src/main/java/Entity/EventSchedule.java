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
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author OS
 */
@Entity
@Table(name = "event_schedule")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EventSchedule.findAll", query = "SELECT e FROM EventSchedule e"),
    @NamedQuery(name = "EventSchedule.findByScheduleId", query = "SELECT e FROM EventSchedule e WHERE e.scheduleId = :scheduleId"),
    @NamedQuery(name = "EventSchedule.findByStartTime", query = "SELECT e FROM EventSchedule e WHERE e.startTime = :startTime"),
    @NamedQuery(name = "EventSchedule.findByEndTime", query = "SELECT e FROM EventSchedule e WHERE e.endTime = :endTime"),
    @NamedQuery(name = "EventSchedule.findByCapacity", query = "SELECT e FROM EventSchedule e WHERE e.capacity = :capacity"),
    // 🔹 FIND SCHEDULE BY EVENT ID
        @NamedQuery(
            name = "EventSchedule.findByEvent",
            query = "SELECT e FROM EventSchedule e WHERE e.eventId.eventId = :eventId"
        ),

        // 🔹 FIND SCHEDULE BY VENUE ID
        @NamedQuery(
            name = "EventSchedule.findByVenue",
            query = "SELECT e FROM EventSchedule e WHERE e.venueId.venueId = :venueId"
        ),

        // 🔹 CHECK VENUE AVAILABILITY (CONFLICT CHECK)
        @NamedQuery(
            name = "EventSchedule.checkVenueAvailability",
            query = "SELECT e FROM EventSchedule e WHERE e.venueId.venueId = :venueId " +
                    "AND (e.startTime < :endTime AND e.endTime > :startTime)"
        ),

        // 🔹 FIND UPCOMING EVENTS
        @NamedQuery(
            name = "EventSchedule.findUpcoming",
            query = "SELECT e FROM EventSchedule e WHERE e.startTime > CURRENT_TIMESTAMP ORDER BY e.startTime ASC"
        ),

        // 🔹 FIND PAST EVENTS
        @NamedQuery(
            name = "EventSchedule.findPast",
            query = "SELECT e FROM EventSchedule e WHERE e.endTime < CURRENT_TIMESTAMP ORDER BY e.startTime DESC"
        ),

        // 🔹 GET VENUE USAGE HISTORY
        @NamedQuery(
            name = "EventSchedule.venueUsageHistory",
            query = "SELECT e FROM EventSchedule e WHERE e.venueId.venueId = :venueId ORDER BY e.startTime DESC"
        ),

        // 🔹 CALENDAR VIEW (ALL EVENTS SORTED)
        @NamedQuery(
            name = "EventSchedule.calendarView",
            query = "SELECT e FROM EventSchedule e ORDER BY e.startTime ASC"
        )

})
public class EventSchedule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "schedule_id")
    private Integer scheduleId;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Column(name = "capacity")
    private Integer capacity;
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    @ManyToOne
    private Events eventId;
    @JoinColumn(name = "venue_id", referencedColumnName = "venue_id")
    @ManyToOne
    private Venues venueId;

    public EventSchedule() {
    }

    public EventSchedule(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Events getEventId() {
        return eventId;
    }

    public void setEventId(Events eventId) {
        this.eventId = eventId;
    }

    public Venues getVenueId() {
        return venueId;
    }

    public void setVenueId(Venues venueId) {
        this.venueId = venueId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scheduleId != null ? scheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventSchedule)) {
            return false;
        }
        EventSchedule other = (EventSchedule) object;
        if ((this.scheduleId == null && other.scheduleId != null) || (this.scheduleId != null && !this.scheduleId.equals(other.scheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.EventSchedule[ scheduleId=" + scheduleId + " ]";
    }
    
}
