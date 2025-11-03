package lau.kass2.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime; // <-- CORREGIDO: Importa LocalDateTime
import java.util.List;
import lau.kass2.models.InventoryMovement;
import lau.kass2.models.Product;
import lau.kass2.models.User;
import jakarta.inject.Inject; // <-- AÑADIDO

@Stateless
public class InventoryService {

    @PersistenceContext(unitName = "KassPU")
    private EntityManager em;
    
    @Inject
    private ProductService productService; // Inyecta ProductService

    @Transactional
    public InventoryMovement recordMovement(Product product, User user, String movementType, int quantity, String reference) {
        
        Product managedProduct = em.find(Product.class, product.getId());
        if (managedProduct == null) {
            throw new RuntimeException("Producto no encontrado para el Kardex: " + product.getId());
        }

        int oldStock = managedProduct.getCurrentStock();
        int newStock = oldStock + quantity;

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(managedProduct);
        movement.setUser(user);
        
        
        movement.setMovementDate(LocalDateTime.now()); // Usa LocalDateTime.now()
        
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setReference(reference);
        movement.setResultingStock(newStock);

        em.persist(movement);

        
        managedProduct.setCurrentStock(newStock);
        em.merge(managedProduct);

        return movement;
    }

    public List<InventoryMovement> getKardexForProduct(Long productId) {
        return em.createQuery("SELECT m FROM InventoryMovement m WHERE m.product.id = :productId ORDER BY m.movementDate DESC", InventoryMovement.class)
                 .setParameter("productId", productId)
                 .getResultList();
    }
}