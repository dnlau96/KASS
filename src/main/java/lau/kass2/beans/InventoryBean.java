package lau.kass2.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import lau.kass2.models.Product;
import lau.kass2.services.ProductService;
import org.primefaces.PrimeFaces;

@Named(value = "inventoryBean") // Le damos un nombre explícito
@ViewScoped
public class InventoryBean implements Serializable {

    @Inject
    private ProductService productoService;

    private List<Product> productList;
    
    // Este objeto guardará el producto que estemos creando o editando en un diálogo
    private Product selectedProduct;

    @PostConstruct
    public void init() {
        // Al cargar la página, obtenemos todos los productos de la base de datos REAL
        this.productList = productoService.findAll();
    }
    
    // Prepara un nuevo objeto Producto para ser llenado en el formulario del diálogo
    public void openNew() {
        this.selectedProduct = new Product();
    }

    // Guarda los cambios (ya sea para un producto nuevo o uno existente)
    public void saveProduct() {
        if (this.selectedProduct.getId() == null) {
            // Si el ID es nulo, es un producto nuevo
            productoService.create(this.selectedProduct);
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto creado correctamente.");
        } else {
            // Si tiene ID, es una actualización
            productoService.update(this.selectedProduct);
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente.");
        }

        // Volvemos a cargar la lista de la base de datos para ver los cambios
        this.productList = productoService.findAll();
        
        // Ejecuta un script para cerrar el diálogo en la vista
        PrimeFaces.current().executeScript("PF('productDialogWidget').hide()");
        // Actualiza la tabla en la vista (asegúrate que el ID de tu form y tabla sean correctos)
        PrimeFaces.current().ajax().update("form:dt-products");
    }

    public void deleteProduct() {
        if (this.selectedProduct != null) {
            productoService.delete(this.selectedProduct.getId());
            this.selectedProduct = null;
            
            // Volvemos a cargar la lista
            this.productList = productoService.findAll();
            addMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado.");
        }
    }

    // --- Getters y Setters ---

    public List<Product> getProductList() {
        return productList;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    // --- Métodos de Utilidad ---
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}