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
public class UserManagment implements UserManagmentLocal {

    @PersistenceContext(unitName= "jpu")
    EntityManager em;
    
    @Override
    public void registerUser(Users user) {
        em.persist(user);
    }

    @Override
    public Users loginUser(String email, String password) {
        TypedQuery<Users> q = em.createNamedQuery("User.login", Users.class);
        q.setParameter("email", email);
        q.setParameter("password", password);

        Collection<Users> users = q.getResultList();

        if(users.isEmpty()){
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
    public void deleteUser(int userId) {
       Users u = em.find(Users.class, userId);
        if(u != null){
            em.remove(u);
        }
    }

    @Override
    public Collection<Users> getAllUsers() {
       TypedQuery<Users> q = em.createNamedQuery("User.findAll", Users.class);
        return q.getResultList();
    }

    @Override
    public Collection<Users> getUsersByRole(String role) {
      TypedQuery<Users> q = em.createNamedQuery("User.findByRole", Users.class);
        q.setParameter("role", role);

        return q.getResultList();

    }

    @Override
    public void uploadProfilePhoto(int userId, String photoPath) {
        Users u = em.find(Users.class, userId);

        if(u != null){
            u.setProfilePhoto(photoPath);
            em.merge(u);
        }
    }
    
    
}
