/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.dao;

/**
 *
 * @author dnlau
 */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lau.kass2.models.Role;

@ApplicationScoped
public class RoleDAO {

    @PersistenceContext(unitName = "KassPU")
    private EntityManager em;

    public List<Role> findAll() {
        return em.createQuery("SELECT r FROM Role r ORDER BY r.roleName", Role.class).getResultList();
    }
    
    public void create(Role role) {
        em.persist(role);
    }

    public void update(Role role) {
        em.merge(role);
    }

    public void delete(Long roleId) {
        Role role = em.find(Role.class, roleId);
        if (role != null) {
            em.remove(role);
        }
    }
}
