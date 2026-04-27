/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CDI;

import EJB.EventSchedulingLocal;
import Entity.EventSchedule;
import Entity.Venues;
import jakarta.ejb.EJB;
//import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("scheduleBean")
@ViewScoped
public class EventScheduleBean implements Serializable {

    @EJB
    private EventSchedulingLocal scheduleService;

    private List<EventSchedule> schedules = new ArrayList<>();
    private List<Venues> venues = new ArrayList<>();

    private int eventId;
    private int venueId;
    private int scheduleId;

    private String startTimeStr;
    private String endTimeStr;

    private int capacity;
    private boolean available;

    private EventSchedule selectedSchedule = new EventSchedule();
    private Venues selectedVenue = new Venues();

    // ===============================
    // 🔹 LOAD DATA
    // ===============================
    public void loadCalendar() {
        schedules = new ArrayList<>(
            scheduleService.getCalendarEvents()
        );
    }

    public void loadVenues() {
        venues = new ArrayList<>(
            scheduleService.getAllVenues()
        );
    }

    public void loadByEvent() {
        EventSchedule s = scheduleService.getScheduleByEvent(eventId);
        if (s != null) {
            schedules = List.of(s);
        }
    }

    public void loadVenueHistory() {
        schedules = new ArrayList<>(
            scheduleService.getVenueUsageHistory(venueId)
        );
    }

    // ===============================
    // 🔹 CHECK AVAILABILITY
    // ===============================
    public void checkAvailability() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = sdf.parse(startTimeStr);
            Date end   = sdf.parse(endTimeStr);

            available = scheduleService.checkVenueAvailability(venueId, start, end);

            showMessage(available ? "Venue Available" : "Venue Not Available");
        } catch (Exception e) {
            showMessage("Invalid date format");
        }
    }

    // ===============================
    // 🔹 ASSIGN SCHEDULE (called from organizer_schedule.xhtml)
    // ===============================
    public String assignSchedule() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date start = sdf.parse(startTimeStr);
            Date end   = sdf.parse(endTimeStr);

            scheduleService.assignVenue(eventId, venueId, start, end);

            // Reset form
            startTimeStr = null;
            endTimeStr   = null;
            eventId      = 0;
            venueId      = 0;
            schedules    = new ArrayList<>(); // force reload

            showMessage("Venue and schedule assigned successfully!");
            return "organizer_schedule?faces-redirect=true&scheduled=true";
        } catch (java.text.ParseException e) {
            showMessage("Invalid date/time format. Please use the date picker.");
            return null;
        } catch (Exception e) {
            showMessage("Error scheduling: " + e.getMessage());
            return null;
        }
    }

    // ===============================
    // 🔹 ASSIGN VENUE (original method kept)
    // ===============================
    public void assignVenue() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = sdf.parse(startTimeStr);
            Date end   = sdf.parse(endTimeStr);

            scheduleService.assignVenue(eventId, venueId, start, end);

            showMessage("Venue assigned successfully");
            loadCalendar();
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    // ===============================
    // 🔹 UPDATE SCHEDULE
    // ===============================
    public void updateSchedule() {
        try {
            scheduleService.updateSchedule(selectedSchedule);
            showMessage("Schedule updated");
        } catch (Exception e) {
            showMessage("Update failed");
        }
    }

    public void updateCapacity() {
        try {
            scheduleService.updateCapacity(scheduleId, capacity);
            showMessage("Capacity updated");
        } catch (Exception e) {
            showMessage("Error updating capacity");
        }
    }

    // ===============================
    // 🔹 VENUE MANAGEMENT
    // ===============================
    public void addVenue() {
        try {
            scheduleService.addVenue(selectedVenue);
            showMessage("Venue added");
            loadVenues();
        } catch (Exception e) {
            showMessage("Error adding venue");
        }
    }

    public void updateVenue() {
        try {
            scheduleService.updateVenue(selectedVenue);
            showMessage("Venue updated");
            loadVenues();
        } catch (Exception e) {
            showMessage("Error updating venue");
        }
    }

    public void deleteVenue(int id) {
        try {
            scheduleService.deleteVenue(id);
            showMessage("Venue deleted");
            loadVenues();
        } catch (Exception e) {
            showMessage("Error deleting venue");
        }
    }

    // ===============================
    // 🔹 MESSAGE HELPER
    // ===============================
    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ===============================
    // 🔹 GETTERS & SETTERS
    // ===============================

    public List<EventSchedule> getSchedules() {
        if (schedules.isEmpty()) loadCalendar();
        return schedules;
    }

    public List<Venues> getVenues() {
        if (venues.isEmpty()) loadVenues();
        return venues;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public EventSchedule getSelectedSchedule() {
        return selectedSchedule;
    }

    public void setSelectedSchedule(EventSchedule selectedSchedule) {
        this.selectedSchedule = selectedSchedule;
    }

    public Venues getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(Venues selectedVenue) {
        this.selectedVenue = selectedVenue;
    }
}
