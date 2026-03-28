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
         em.persist(user);
        sendRegistrationEmail(user.getEmail());
    }

    @Override
    public Users loginUser(String email, String password) {
        TypedQuery<Users> q = em.createNamedQuery("Users.login", Users.class);
        q.setParameter("email", email);
        q.setParameter("password", password);

        Collection<Users> users = q.getResultList();

        if (users.isEmpty()) {
            return null;
        }

        return users.iterator().next();
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
    public void changePassword(int userId, String newPassword) {
        Users u = em.find(Users.class, userId);

        if (u != null) {
            u.setPassword(newPassword);
            em.merge(u);
        }
    }

    @Override
    public void forgotPassword(String email) {
          TypedQuery<Users> q = em.createNamedQuery("Users.findByEmail", Users.class);
        q.setParameter("email", email);

        Collection<Users> users = q.getResultList();

        if (!users.isEmpty()) {

            Users u = users.iterator().next();

            // here you can generate reset link
            System.out.println("Reset password link sent to: " + u.getEmail());
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
         TypedQuery<Users> q = em.createQuery(
        "SELECT u FROM Users u WHERE u.name LIKE :k OR u.email LIKE :k", Users.class);

        q.setParameter("k", "%" + keyword + "%");

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
        TypedQuery<Users> q = em.createNamedQuery("Users.findByRole", Users.class);
        q.setParameter("roleId", roleId);
//        em.createNamedQuery("Users.getUsersByRole", Users.class);

        return q.getResultList();
    }

    @Override
    public Collection<Users> getAllUsers() {
         TypedQuery<Users> q = em.createNamedQuery("Users.findAll", Users.class);
        return q.getResultList();
    }

    @Override
    public void sendRegistrationEmail(String email) {
          System.out.println("Registration email sent to: " + email);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
