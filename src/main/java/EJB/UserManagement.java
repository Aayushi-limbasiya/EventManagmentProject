/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Collection;

/**
 *
 * @author OS
 */
@Stateless
public class UserManagement implements UserManagementLocal {

    @PersistenceContext(unitName = "jpu")
    EntityManager em;

    // ── 1. REPLACE registerUser() ─────────────────────────────
    @Override
    public void registerUser(Users user) {

        // Smart auto-verification based on role
        if (user.getRoleId() != null) {
            int roleId = user.getRoleId().getRoleId();
            if (roleId == 3) {
                // Participant → auto verify → can login immediately
                user.setVerifiedStatus("Verified");
            } else if (roleId == 2) {
                // Organizer → pending → Admin must verify first
                user.setVerifiedStatus("Pending");
            } else if (roleId == 1) {
                throw new RuntimeException(
                    "Admin accounts cannot be created via registration."
                );
            }
        } else {
            user.setVerifiedStatus("Verified");
        }

        user.setCreatedAt(new java.util.Date());
        em.persist(user);

        // Send welcome email after registration
        try {
            String roleName = (user.getRoleId() != null)
                ? user.getRoleId().getRoleName() : "User";

            String subject = "Welcome to Event Management System!";
            String body = "Dear " + user.getName() + ",\n\n"
                + "Your account has been created successfully.\n\n"
                + "Account Details:\n"
                + "  Name   : " + user.getName() + "\n"
                + "  Email  : " + user.getEmail() + "\n"
                + "  Role   : " + roleName + "\n"
                + "  Status : " + user.getVerifiedStatus() + "\n\n"
                + ("Pending".equals(user.getVerifiedStatus())
                    ? "Your organizer account is pending admin verification.\n"
                    + "You will receive an email once your account is approved.\n\n"
                    : "You can now login and start exploring events.\n\n")
                + "Regards,\nEvent Management Team";

            EmailUtil.sendEmail(user.getEmail(), subject, body);
        } catch (Exception e) {
            System.out.println("Registration email failed: " + e.getMessage());
        }
    }
    @Override
    public Users getUserById(int userId) {
        return em.find(Users.class, userId);
    }

    @Override
    public void updateUser(Users user) {
        em.merge(user);
    }

    @Override
    public void uploadProfilePhoto(int userId, String photoPath) {
        Users u = em.find(Users.class, userId);

        if (u != null) {
            u.setProfilePhoto(photoPath);
            em.merge(u);
        }
    }

    @Override
    public void verifyAccount(int userId) {
        Users u = em.find(Users.class, userId);
        if (u != null) {
            u.setVerifiedStatus("Verified");
            em.merge(u);

            // Send account verified email to organizer
            try {
                String subject = "Account Verified - Event Management System";
                String body = "Dear " + u.getName() + ",\n\n"
                    + "Congratulations! Your organizer account has been verified by the admin.\n\n"
                    + "You can now login and start creating events.\n\n"
                    + "Account Details:\n"
                    + "  Name  : " + u.getName() + "\n"
                    + "  Email : " + u.getEmail() + "\n"
                    + "  Status: Verified\n\n"
                    + "Regards,\nEvent Management Team";

                EmailUtil.sendEmail(u.getEmail(), subject, body);
            } catch (Exception e) {
                System.out.println("Verify account email failed: " + e.getMessage());
            }
        }
    }

    @Override
    public Collection<Users> searchUsers(String keyword) {
        TypedQuery<Users> q = em.createNamedQuery("Users.searchUsers", Users.class);

        q.setParameter("keyword", "%" + keyword + "%");

        return q.getResultList();
    }

    @Override
    public void blockUser(int userId) {
        Users u = em.find(Users.class, userId);
        if (u != null) {
            u.setVerifiedStatus("Blocked");
            em.merge(u);

            // Send blocked notification email to user
            try {
                String subject = "Account Blocked - Event Management System";
                String body = "Dear " + u.getName() + ",\n\n"
                    + "Your account has been blocked by the administrator.\n\n"
                    + "You will not be able to login until your account is unblocked.\n\n"
                    + "If you believe this is a mistake, please contact the admin.\n\n"
                    + "Regards,\nEvent Management Team";

                EmailUtil.sendEmail(u.getEmail(), subject, body);
            } catch (Exception e) {
                System.out.println("Block user email failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void unblockUser(int userId) {
        Users u = em.find(Users.class, userId);
        if (u != null) {
            u.setVerifiedStatus("Verified");
            em.merge(u);

            // Send unblocked notification email to user
            try {
                String subject = "Account Unblocked - Event Management System";
                String body = "Dear " + u.getName() + ",\n\n"
                    + "Good news! Your account has been unblocked by the administrator.\n\n"
                    + "You can now login and use the platform again.\n\n"
                    + "Regards,\nEvent Management Team";

                EmailUtil.sendEmail(u.getEmail(), subject, body);
            } catch (Exception e) {
                System.out.println("Unblock user email failed: " + e.getMessage());
            }
        }
    }
    
    @Override
    public Collection<Users> getUsersByRole(int roleId) {
        // 1. Find the Role entity first
        Entity.Roles role = em.find(Entity.Roles.class, roleId);

        if (role == null) {
            return java.util.Collections.emptyList();
        }

        // 2. Execute query
        TypedQuery<Users> q = em.createNamedQuery("Users.getUsersByRole", Users.class);

        // CRITICAL: The string "roleId" here must match the :roleId in your Entity's @NamedQuery
        q.setParameter("roleId", role);

        return q.getResultList();
    }

    @Override
    public Collection<Users> getAllUsers() {

        return em.createNamedQuery("Users.findAll", Users.class).getResultList();
    }

}
