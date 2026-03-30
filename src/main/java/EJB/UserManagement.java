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

        @Override
    public void registerUser(Users user) {

        // Smart auto-verification based on role
        // roleId 1 = Admin, roleId 2 = Organizer, roleId 3 = Participant

        if (user.getRoleId() != null) {
            int roleId = user.getRoleId().getRoleId();

            if (roleId == 3) {
                // Participant → auto verify immediately → can login right away
                user.setVerifiedStatus("Verified");

            } else if (roleId == 2) {
                // Organizer → keep Pending → Admin must verify manually
                // Admin checks organization details before approving
                user.setVerifiedStatus("Pending");

            } else if (roleId == 1) {
                // Admin → should not be created via API
                // Throw error to prevent unauthorized admin creation
                throw new RuntimeException(
                    "Admin accounts cannot be created via registration."
                );
            }
        } else {
            // No role provided → default to Participant + Verified
            user.setVerifiedStatus("Verified");
        }

        // Set registration timestamp
        user.setCreatedAt(new java.util.Date());

        em.persist(user);
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
        }
    }

    @Override
    public void unblockUser(int userId) {
        Users u = em.find(Users.class, userId);

        if (u != null) {
            u.setVerifiedStatus("Verified");
            em.merge(u);
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
