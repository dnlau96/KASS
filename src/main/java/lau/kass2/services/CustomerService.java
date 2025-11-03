package lau.kass2.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lau.kass2.dao.CustomerDAO;
import lau.kass2.models.Customer;

@Stateless
public class CustomerService {

    @Inject
    private CustomerDAO customerDAO;

    public Customer findCustomerByNit(String nit) {
        return customerDAO.findByNit(nit);
    }
    
    @Transactional
    public Customer getDefaultCustomer() {
        Customer cf = customerDAO.findByNit("C/F");
        if (cf == null) {
            cf = new Customer("C/F", "Consumidor Final");
            customerDAO.create(cf);
        }
        return cf;
    }
    
    @Transactional
    public Customer findOrCreateCustomer(String nit, String name) {
        Customer customer = customerDAO.findByNit(nit);
        if (customer == null) {
            customer = new Customer(nit, name);
            customerDAO.create(customer);
        }
        return customer;
    }
}