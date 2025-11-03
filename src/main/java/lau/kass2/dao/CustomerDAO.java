package lau.kass2.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lau.kass2.models.Customer;

@ApplicationScoped
public class CustomerDAO {

    @PersistenceContext(unitName = "KassPU")
    private EntityManager em;

    public Customer findByNit(String nit) {
        try {
            return em.createQuery("SELECT c FROM Customer c WHERE c.nit = :nit", Customer.class)
                     .setParameter("nit", nit)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Transactional
    public void create(Customer customer) {
        em.persist(customer);
    }
}