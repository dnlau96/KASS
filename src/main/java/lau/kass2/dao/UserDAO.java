package lau.kass2.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import lau.kass2.models.User;

@ApplicationScoped 
public class UserDAO {

    @PersistenceContext(unitName = "KassPU")
    private EntityManager em;

    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                     .setParameter("username", username)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public User findByBadgeCode(String badgeCode) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.badgeCode = :badgeCode", User.class)
                     .setParameter("badgeCode", badgeCode)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // --- MÉTODOS CRUD  ---

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.username", User.class).getResultList();
    }
    
    public void create(User user) {
        em.persist(user);
    }

    public void update(User user) {
        em.merge(user);
    }

    public void delete(Long userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            em.remove(user);
        }
    }
}