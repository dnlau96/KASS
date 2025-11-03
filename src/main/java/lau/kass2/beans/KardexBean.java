package lau.kass2.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import lau.kass2.models.InventoryMovement;
import lau.kass2.models.Product;
import lau.kass2.services.InventoryService;
import lau.kass2.services.ProductService;
// Imports que faltaban
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lau.kass2.models.User;
import java.util.ArrayList;

@Named("kardexBean")
@ViewScoped
public class KardexBean implements Serializable {
    
    @Inject
    private ProductService productService;
    
    @Inject
    private InventoryService inventoryService;
    
    @Inject
    private AdminBean adminBean;
    
    private Long selectedProductId;
    private Integer quantity;
    private String reference;
    
    private List<Product> allProducts;
    private List<InventoryMovement> movements;
    
    private Long adjSelectedProductId;
    private Integer adjQuantity;
    private String adjMovementType;
    private String adjReference;
    
    private List<String> adjustmentTypes;
    private Long searchProductId;
    
    // Propiedad que faltaba para el 'onProductSelect'
    private Product selectedProduct;

    @PostConstruct
    public void init() {
        this.allProducts = productService.findAll();
        // CORREGIDO: Llama a un método que existe
        // this.movements = inventoryService.findAllMovements(); <-- ESTE NO EXISTE
        // Dejamos que el usuario filtre primero
        this.movements = new ArrayList<>();
        this.reference = "Factura #";
        
        this.adjustmentTypes = new ArrayList<>();
        this.adjustmentTypes.add("AJUSTE_ERROR_INGRESO");
        this.adjustmentTypes.add("AJUSTE_FALTANTE");
        this.adjustmentTypes.add("AJUSTE_SOBRANTE");
        this.adjustmentTypes.add("AJUSTE_DAÑADO");
    }

    // --- MÉTODOS CORREGIDOS ---

    /**
     * CORREGIDO: Renombrado de 'onProductSelect' (en mi código anterior) a 'searchMovements'
     * para que coincida con tu lógica
     */
    public void searchMovements() {
        if (searchProductId != null) {
            // Llama al método que SÍ existe en tu InventoryService
            this.movements = inventoryService.getKardexForProduct(searchProductId);
            this.selectedProduct = productService.findById(searchProductId); // Usa findById
        } else {
            this.movements = new ArrayList<>(); // Muestra vacío si no hay producto
        }
    }
    
    public void clearSearch() {
        this.searchProductId = null;
        this.movements = new ArrayList<>();
        this.selectedProduct = null;
    }

    public void saveStockEntry() {
        try {
            Product selectedProduct = productService.findById(selectedProductId);
            User currentUser = adminBean.getCurrentUser();

            if (selectedProduct == null || currentUser == null) {
                addMessage("Error", "No se pudo encontrar el producto o el usuario.", FacesMessage.SEVERITY_ERROR);
                return;
            }
            if (this.quantity == null || this.quantity <= 0) {
                 addMessage("Error", "La cantidad debe ser mayor a cero.", FacesMessage.SEVERITY_WARN);
                return;
            }

            inventoryService.recordMovement(
                selectedProduct, 
                currentUser, 
                "INGRESO_FACTURA", 
                this.quantity, 
                this.reference
            );
            
            addMessage("¡Éxito!", "Ingreso de " + this.quantity + " unidades de " + selectedProduct.getName() + " registrado.", FacesMessage.SEVERITY_INFO);
            
            // Limpia y recarga
            this.movements = new ArrayList<>(); // Limpia la tabla
            this.quantity = null;
            this.selectedProductId = null;
            this.reference = "Factura #";
            this.searchProductId = null;
            this.selectedProduct = null;

        } catch (Exception e) {
            addMessage("Error Grave", "No se pudo registrar el movimiento: " + e.getMessage(), FacesMessage.SEVERITY_FATAL);
        }
    }
    
    public void saveAdjustment() {
        try {
            Product selectedProduct = productService.findById(adjSelectedProductId);
            User currentUser = adminBean.getCurrentUser();

            if (selectedProduct == null || currentUser == null) {
                addMessage("Error", "No se pudo encontrar el producto o el usuario.", FacesMessage.SEVERITY_ERROR);
                return;
            }
            // ... (tus otras validaciones están bien) ...
            if (this.adjQuantity == null || this.adjQuantity == 0) {
                 addMessage("Error", "La cantidad no puede ser cero.", FacesMessage.SEVERITY_WARN);
                return;
            }
            if (this.adjReference == null || this.adjReference.trim().isEmpty()) {
                 addMessage("Error", "La Razón/Referencia es obligatoria.", FacesMessage.SEVERITY_WARN);
                return;
            }
            if (this.adjMovementType == null) {
                 addMessage("Error", "Debe seleccionar un Tipo de Ajuste.", FacesMessage.SEVERITY_WARN);
                return;
            }

            inventoryService.recordMovement(
                selectedProduct, 
                currentUser, 
                this.adjMovementType, 
                this.adjQuantity, 
                this.adjReference
            );

            addMessage("¡Ajuste Exitoso!", "Ajuste de " + this.adjQuantity + " unidades de " + selectedProduct.getName() + " registrado.", FacesMessage.SEVERITY_INFO);
            
            // Limpia y recarga
            this.movements = new ArrayList<>();
            this.adjQuantity = null;
            this.adjSelectedProductId = null;
            this.adjReference = "";
            this.adjMovementType = null;
            this.searchProductId = null;
            this.selectedProduct = null;

        } catch (Exception e) {
            addMessage("Error Grave", "No se pudo registrar el ajuste: " + e.getMessage(), FacesMessage.SEVERITY_FATAL);
        }
    }
    
    // --- Getters y Setters (los que tenías) ---
    // ... (Tu bloque de getters y setters de la línea 175 a 259 está bien) ...
    public Long getSearchProductId() { return searchProductId; }
    public void setSearchProductId(Long searchProductId) { this.searchProductId = searchProductId; }
    public ProductService getProductService() { return productService; }
    public void setProductService(ProductService productService) { this.productService = productService; }
    public InventoryService getInventoryService() { return inventoryService; }
    public void setInventoryService(InventoryService inventoryService) { this.inventoryService = inventoryService; }
    public AdminBean getAdminBean() { return adminBean; }
    public void setAdminBean(AdminBean adminBean) { this.adminBean = adminBean; }
    public Long getAdjSelectedProductId() { return adjSelectedProductId; }
    public void setAdjSelectedProductId(Long adjSelectedProductId) { this.adjSelectedProductId = adjSelectedProductId; }
    public Integer getAdjQuantity() { return adjQuantity; }
    public void setAdjQuantity(Integer adjQuantity) { this.adjQuantity = adjQuantity; }
    public String getAdjMovementType() { return adjMovementType; }
    public void setAdjMovementType(String adjMovementType) { this.adjMovementType = adjMovementType; }
    public String getAdjReference() { return adjReference; }
    public void setAdjReference(String adjReference) { this.adjReference = adjReference; }
    public List<String> getAdjustmentTypes() { return adjustmentTypes; }
    public void setAdjustmentTypes(List<String> adjustmentTypes) { this.adjustmentTypes = adjustmentTypes; }
    public Long getSelectedProductId() { return selectedProductId; }
    public void setSelectedProductId(Long selectedProductId) { this.selectedProductId = selectedProductId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public List<Product> getAllProducts() { return allProducts; }
    public void setAllProducts(List<Product> allProducts) { this.allProducts = allProducts; }
    public List<InventoryMovement> getMovements() { return movements; }
    public void setMovements(List<InventoryMovement> movements) { this.movements = movements; }
    
    // AÑADE ESTE GETTER QUE FALTABA
    public Product getSelectedProduct() { return selectedProduct; }

    private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}