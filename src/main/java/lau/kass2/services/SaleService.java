/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.services;

/**
 *
 * @author dnlau
 */
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lau.kass2.models.*; 

@Stateless
public class SaleService {

    @PersistenceContext(unitName = "KassPU")
    private EntityManager em;
    
    @Inject
    private InventoryService inventoryService; 
    
    @Inject 
    private CustomerService customerService;
    
    @Inject 
    private UserService userService; // <-- 1. ASEGÚRATE DE INYECTAR ESTO

   
    @Transactional
    public Sale createSale(List<SaleItem> shoppingCart, 
            String customerNit,
            String customerName, 
            BigDecimal totalAmount // <-- (Ya no recibe el ID de usuario, ¡correcto!)
    ) throws Exception {
       
        User managedUser = userService.findUserByUsername("caja_pos");
        
        if (managedUser == null) {
            
            throw new Exception("El usuario 'admin' (requerido para autocobro) NO EXISTE en la base de datos.");
        }
        
        Customer managedCustomer = customerService.findOrCreateCustomer(customerNit, customerName);
        
        // 4. Crear la Venta
        Sale sale = new Sale();
        sale.setCustomer(managedCustomer);
        sale.setTotal(totalAmount);
        sale.setSaleDate(new Date());
        sale.setUser(managedUser); // <-- Usa el usuario "admin"

        // 5. Preparar Detalles y Validar Stock
        List<SaleDetail> details = new ArrayList<>();
        for (SaleItem item : shoppingCart) {
            Product dbProduct = em.find(Product.class, item.getProduct().getId()); 
            if (dbProduct == null) {
                 throw new Exception("Producto con ID " + item.getProduct().getId() + " no existe.");
            }
            if (dbProduct.getCurrentStock() < item.getQuantity()) {
                throw new Exception("No hay suficiente stock para: " + dbProduct.getName());
            }
            
            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(dbProduct);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(dbProduct.getSalePrice());
            detail.setSubtotal(item.getSubtotal());
            details.add(detail);
        }
        sale.setDetails(details);
        
        // 6. Guardar
        em.persist(sale);
        
        // 7. Mover Kardex
        String saleReference = "Venta #" + sale.getId();
        
        for (SaleDetail detail : details) { 
            inventoryService.recordMovement(
                detail.getProduct(), 
                managedUser, // <-- Pasa el usuario "admin"
                "SALIDA_VENTA",
                -detail.getQuantity(), 
                saleReference
            );
        }
        
        return sale;
    }

    @Transactional
public List<Sale> getAllSales() {
    String jpql = "SELECT s FROM Sale s " +
                  "JOIN FETCH s.customer " +
                  "JOIN FETCH s.user " +
                  "ORDER BY s.saleDate DESC";
    
    // Asegúrate de que 'em' (EntityManager) esté inyectado en SaleService
    return em.createQuery(jpql, Sale.class).getResultList();
}
}