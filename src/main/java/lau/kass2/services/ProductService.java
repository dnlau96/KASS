package lau.kass2.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import lau.kass2.models.Product;

/**
 * Business service for managing all operations
 * related to products in the database.
 * Uses @Stateless EJB to handle transactions automatically.
 */
@Stateless
public class ProductService {

    // Injects the EntityManager to interact with the database.
    // "KassPU" must match the name of your persistence-unit in persistence.xml
    @PersistenceContext(unitName = "KassPU")
    private EntityManager em;

    /**
     * Finds a product by its 13-digit barcode.
     * @param barcode The barcode to search for.
     * @return The Product object if found, otherwise null.
     */
    public Product findByBarcode(String barcode) {
        try {
            // CORRECTION: The query now uses the English entity name 'Product' and field 'barcode'
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p WHERE p.barcode = :barcode", Product.class);
            query.setParameter("barcode", barcode);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // It's normal for a product not to be found, so we return null.
            return null;
        }
    }
    
    /**
     * Returns a list of all products in the database.
     * @return A list of products.
     */
    public List<Product> findAll() {
        // CORRECTION: Query uses 'Product' and orders by the 'name' field
        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p ORDER BY p.name", Product.class);
        return query.getResultList();
    }

    /**
     * Searches for products whose name contains the search term.
     * The search is case-insensitive.
     * @param name The text to search for within product names.
     * @return A list of matching products.
     */
    public List<Product> searchByName(String name) {
        // CORRECCIÓN: Limpiamos el término de búsqueda y nos aseguramos de que no esté vacío.
        if (name == null || name.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        String searchTerm = "%" + name.toLowerCase().trim() + "%";
        
        return em.createQuery("SELECT p FROM Product p WHERE LOWER(p.name) LIKE :searchTerm", Product.class)
                 .setParameter("searchTerm", searchTerm)
                 .getResultList();
    }
    
    /**
     * NUEVO MÉTODO: Busca productos que empiecen con una letra específica.
     */
    public List<Product> findByFirstLetter(String letter) {
        if (letter == null || letter.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String searchPattern = letter.toLowerCase().trim() + "%";
        
        return em.createQuery("SELECT p FROM Product p WHERE LOWER(p.name) LIKE :pattern ORDER BY p.name", Product.class)
                 .setParameter("pattern", searchPattern)
                 .getResultList();
    }
    
    /**
     * Saves a new product to the database.
     * @param product The new product to create.
     */
    public void create(Product product) {
        if (product != null) {
            em.persist(product);
        }
    }

    /**
     * Updates the data of an existing product.
     * @param product The product with the modified data.
     * @return The updated product.
     */
    public Product update(Product product) {
        if (product != null) {
            return em.merge(product);
        }
        return null;
    }

    /**
     * Deletes a product from the database using its ID.
     * @param id The ID of the product to delete.
     */
    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }
}