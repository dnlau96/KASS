/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.beans;

/**
 *
 * @author dnlau
 */
// Paquete: lau.kass2.beans

import lau.kass2.models.Product;
import lau.kass2.services.ProductService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal; 
import java.util.List;
import org.primefaces.PrimeFaces; 

@Named("productBean") 
@ViewScoped
public class ProductBean implements Serializable {

    @Inject
    private ProductService productService; 

    private List<Product> products;
    private Product selectedProduct;

    @PostConstruct
    public void init() {
        this.products = productService.findAll();
    }

    
    public void openNew() {
        this.selectedProduct = new Product();
        // Usamos TUS campos de Product.java
        this.selectedProduct.setCurrentStock(0); 
        this.selectedProduct.setSalePrice(BigDecimal.ZERO);
    }

    
    public void saveProduct() {
        if (this.selectedProduct.getId() == null) {
            // ID es nulo, es un producto nuevo
            productService.create(this.selectedProduct);
            addMessage("Success", "Product Created");
        } else {
            // ID existe, es una actualización
            productService.update(this.selectedProduct);
            addMessage("Success", "Product Updated");
        }
        
        // Refresca la lista y esconde el diálogo
        this.products = productService.findAll();
        PrimeFaces.current().executeScript("PF('productDialog').hide()");
        PrimeFaces.current().ajax().update("form:dt-products"); 
    }

    
    
public void deleteProduct() {
    // Le pasamos solo el ID, no el objeto entero
    productService.delete(this.selectedProduct.getId()); 
    
    this.products.remove(this.selectedProduct);
    this.selectedProduct = null;
    addMessage("Success", "Product Deleted");
    PrimeFaces.current().ajax().update("form:dt-products");
}

    // --- Getters y Setters (Necesarios para JSF) ---
    
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }
    
    // --- Método de ayuda para mensajes ---
    private void addMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }
}