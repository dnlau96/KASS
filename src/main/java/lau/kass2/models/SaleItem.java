/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.models;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents an item within a sale, typically in a shopping cart.
 * This is a transient object, not a JPA entity.
 */
public class SaleItem implements Serializable {

    // Note: The type 'Item' is used as provided. 
    // For consistency, you might want to rename your entity class from 'Item' to 'Product'.
    private Product product;
    private int quantity;

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // --- Getters and Setters ---
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Utility method to increase the quantity of this item.
     * @param amountToAdd The number of items to add.
     */
    public void incrementQuantity(int amountToAdd) {
        this.quantity += amountToAdd;
    }

    /**
     * Utility method to calculate the subtotal for this line item.
     * @return The calculated subtotal (price * quantity).
     */
    public BigDecimal getSubtotal() {
        // Ensure the product is not null to avoid NullPointerException
        if (this.product == null || this.product.getSalePrice() == null) {
            return BigDecimal.ZERO;
        }
        return this.product.getSalePrice().multiply(BigDecimal.valueOf(this.quantity));
    }
}