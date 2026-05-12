package CDI;

import EJB.UserManagementLocal;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * CDI Bean for all 3 profile pages.
 * @RequestScoped — created fresh per request, no view-state issues.
 */
@Named("profileBean")
@RequestScoped
public class UserProfileBean implements Serializable {

    @EJB
    private UserManagementLocal userService;

    @Inject
    private AuthBean authBean;

    // Editable fields
    private String  editName;
    private String  editPhone;
    private String  editOrgName;
    private String  editOrgType;
    private String  editOrgAddress;
    private boolean editMode = false;

    // ── Load for edit — USER (participant) ──────────────────
    public String loadForEdit() {
        populateEditFields();
        return null; // stay on same page — bean keeps populated values
    }

    // ── Load for edit — ORGANIZER ────────────────────────────
    public String loadForEditOrganizer() {
        populateEditFields();
        return null; // stay on same page — bean keeps populated values
    }

    private void populateEditFields() {
        Users u = getUser();
        if (u == null) return;
        editName       = u.getName();
        editPhone      = u.getPhone();
        editOrgName    = u.getOrganizationName();
        editOrgType    = u.getOrganizationType();
        editOrgAddress = u.getOrganizationAddress();
        editMode       = true;
    }

    public String cancelEdit() {
        editMode = false;
        return null; // stay on same page, server renders view section
    }

    public String cancelEditOrganizer() {
        editMode = false;
        return null;
    }

    // ── Validation — called by BOTH save methods ─────────────
    private boolean validatePersonal() {
        boolean ok = true;

        if (editName != null)  editName  = editName.trim();
        if (editPhone != null) editPhone = editPhone.trim();

        // NAME — required
        if (editName == null || editName.isEmpty()) {
            addMsg("Full name is required.");
            ok = false;
        } else if (editName.length() < 2) {
            addMsg("Name must be at least 2 characters.");
            ok = false;
        } else if (editName.length() > 60) {
            addMsg("Name must be at most 60 characters.");
            ok = false;
        } else if (editName.matches(".*\\d.*")) {
            addMsg("Name cannot contain numbers.");
            ok = false;
        } else if (!editName.matches("[a-zA-Z\\s.'-]+")) {
            addMsg("Name can only contain letters, spaces, dots, hyphens or apostrophes.");
            ok = false;
        }

        // PHONE — required
        if (editPhone == null || editPhone.isEmpty()) {
            addMsg("Phone number is required.");
            ok = false;
        } else {
            String digits = editPhone.replaceAll("[\\s\\-().+]", "");
            if (!digits.matches("\\d+")) {
                addMsg("Phone number cannot contain letters or special characters.");
                ok = false;
            } else if (digits.length() < 10) {
                addMsg("Phone number must have at least 10 digits.");
                ok = false;
            } else if (digits.length() > 15) {
                addMsg("Phone number must have at most 15 digits.");
                ok = false;
            }
        }

        return ok;
    }

    // ── Extra validation for organizer org fields ────────────
    private boolean validateOrganizer() {
        boolean ok = true;

        if (editOrgName != null)    editOrgName    = editOrgName.trim();
        if (editOrgAddress != null) editOrgAddress = editOrgAddress.trim();

        // ORG NAME — required
        if (editOrgName == null || editOrgName.isEmpty()) {
            addMsg("Organization name is required.");
            ok = false;
        } else if (editOrgName.length() < 2) {
            addMsg("Organization name must be at least 2 characters.");
            ok = false;
        } else if (editOrgName.length() > 100) {
            addMsg("Organization name must be at most 100 characters.");
            ok = false;
        } else if (editOrgName.matches(".*[<>\"'%;()&+].*")) {
            addMsg("Organization name contains invalid characters.");
            ok = false;
        }

        // ORG TYPE — required (must not be empty selection)
        if (editOrgType == null || editOrgType.trim().isEmpty()) {
            addMsg("Organization type is required. Please select one.");
            ok = false;
        }

        // ORG ADDRESS — required
        if (editOrgAddress == null || editOrgAddress.isEmpty()) {
            addMsg("Organization address is required.");
            ok = false;
        } else if (editOrgAddress.length() < 5) {
            addMsg("Organization address must be at least 5 characters.");
            ok = false;
        } else if (editOrgAddress.length() > 200) {
            addMsg("Organization address must be at most 200 characters.");
            ok = false;
        }

        return ok;
    }

    // ── Save — participant: name + phone only ────────────────
    public String saveParticipant() {
        editMode = true; // keep edit form open until save succeeds
        if (!validatePersonal()) return null;
        Users u = getUser();
        if (u == null) { addMsg("Session expired. Please log in again."); return null; }
        try {
            // Load fresh from DB to avoid detached entity issues
            Users fresh = userService.getUserById(u.getUserId());
            fresh.setName(editName);
            fresh.setPhone(editPhone);
            userService.updateUser(fresh);
            // Update session bean too
            u.setName(editName);
            u.setPhone(editPhone);
            editMode = false;
            return "user_profile?faces-redirect=true&updated=true";
        } catch (Exception e) {
            addMsg("Error: " + e.getMessage());
            return null;
        }
    }

    // ── Save — organizer: name + phone + org fields ──────────
    public String saveOrganizer() {
        editMode = true; // keep edit form open until save succeeds
        boolean personalOk  = validatePersonal();
        boolean organizerOk = validateOrganizer();
        if (!personalOk || !organizerOk) return null;
        Users u = getUser();
        if (u == null) { addMsg("Session expired. Please log in again."); return null; }
        try {
            Users fresh = userService.getUserById(u.getUserId());
            fresh.setName(editName);
            fresh.setPhone(editPhone);
            fresh.setOrganizationName(editOrgName);
            fresh.setOrganizationType(editOrgType);
            fresh.setOrganizationAddress(editOrgAddress);
            userService.updateUser(fresh);
            // Update session
            u.setName(editName);
            u.setPhone(editPhone);
            u.setOrganizationName(editOrgName);
            u.setOrganizationType(editOrgType);
            u.setOrganizationAddress(editOrgAddress);
            editMode = false;
            return "organizer_profile?faces-redirect=true&updated=true";
        } catch (Exception e) {
            addMsg("Error: " + e.getMessage());
            return null;
        }
    }

    // ── Helpers ──────────────────────────────────────────────
    public Users getUser() {
        return (authBean != null) ? authBean.getLoggedInUser() : null;
    }

    /** Convenience: safe name (never null) */
    public String getUserName() {
        Users u = getUser();
        return (u != null && u.getName() != null) ? u.getName() : "";
    }

    public String getUserInitials() {
        String n = getUserName();
        return n.length() >= 2 ? n.substring(0, 2).toUpperCase()
             : n.length() == 1 ? n.toUpperCase() : "??";
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(msg));
    }

    // ── Getters / Setters ────────────────────────────────────
    public String  getEditName()             { return editName; }
    public void    setEditName(String v)     { this.editName = v; }
    public String  getEditPhone()            { return editPhone; }
    public void    setEditPhone(String v)    { this.editPhone = v; }
    public String  getEditOrgName()          { return editOrgName; }
    public void    setEditOrgName(String v)  { this.editOrgName = v; }
    public String  getEditOrgType()          { return editOrgType; }
    public void    setEditOrgType(String v)  { this.editOrgType = v; }
    public String  getEditOrgAddress()       { return editOrgAddress; }
    public void    setEditOrgAddress(String v){ this.editOrgAddress = v; }
    public boolean isEditMode()              { return editMode; }
    public void    setEditMode(boolean v)    { this.editMode = v; }
}
