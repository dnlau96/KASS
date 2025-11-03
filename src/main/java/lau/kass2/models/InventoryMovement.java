package lau.kass2.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    
    @Column(name = "movement_type", nullable = false, length = 50)
    private String movementType;

    // Cantidad: +100 si es ingreso, -2 si es una venta o ajuste
    @Column(name = "quantity", nullable = false)
    private int quantity;

    
    @Column(name = "reference", length = 255)
    private String reference;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "resulting_stock", nullable = false)
    private int resultingStock;
    
    @PrePersist
    public void prePersist() {
        this.movementDate = LocalDateTime.now();
    }
    
    @Transient // Le dice a la BD que ignore este método
    public Date getMovementDateAsDate() {
        if (this.movementDate == null) {
            return null;
        }
        // Convierte el objeto 'LocalDateTime' a 'java.util.Date'
        return java.sql.Timestamp.valueOf(this.movementDate);
    }

    public int getResultingStock() {
        return resultingStock;
    }

    public void setResultingStock(int resultingStock) {
        this.resultingStock = resultingStock;
    }

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
}