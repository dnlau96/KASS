package lau.kass2.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal; // Importación correcta
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;
import lau.kass2.services.ProductoService;
import jakarta.inject.Inject;
import lau.kass2.models.ItemVenta;
import lau.kass2.models.Producto;
import lau.kass2.services.ProductoService;

@Named
@ViewScoped
public class PosBean implements Serializable {

    @Inject
    private ProductoService productoService;

    private String barcode;
    private String searchTerm;

    // El carrito ahora es una lista de ItemVenta
    private List<ItemVenta> carrito = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private Producto foundProduct; // Para el diálogo de confirmación
    private int foundProductQuantity = 1;

    private List<Producto> searchResults; // Para el diálogo de búsqueda

    // El método init ahora puede estar vacío
    @PostConstruct
    public void init() {
    }

    public void findProductAndShowDialog() {
        if (barcode != null && !barcode.trim().isEmpty()) {
            this.foundProduct = productoService.findByBarcode(barcode);
            this.foundProductQuantity = 1;

            if (this.foundProduct == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "No Encontrado", "El producto con código " + barcode + " no existe.");
            }
        }
    }

    public void addProductToSale() {
        if (foundProduct != null) {
            // Busca si el producto ya está en el carrito
            carrito.stream()
                .filter(item -> item.getProducto().getId().equals(foundProduct.getId()))
                .findFirst()
                .ifPresentOrElse(
                    item -> item.incrementarCantidad(foundProductQuantity), // Si ya existe, incrementa la cantidad
                    () -> {
                        // Si no existe, crea un nuevo ItemVenta y lo añade al carrito
                        carrito.add(new ItemVenta(foundProduct, foundProductQuantity));
                    }
                );

            addMessage(FacesMessage.SEVERITY_INFO, "Producto Agregado", foundProduct.getNombre());
            this.barcode = "";
            updateTotal();
            this.foundProduct = null;
        }
    }

    public void performSearch() {
        this.searchResults = productoService.searchByName(searchTerm);
    }

    public void addProductFromSearch(Producto productoSeleccionado) {
        this.foundProduct = productoSeleccionado;
        this.foundProductQuantity = 1;
        addProductToSale();
    }

    public void updateTotal() {
        totalAmount = carrito.stream()
                             .map(ItemVenta::getSubtotal)
                             .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void checkout() {
        if (carrito.isEmpty()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Venta Vacía", "No hay productos para procesar el pago.");
            return;
        }
        // Aquí iría la lógica para llamar al KardexService y registrar la salida
        addMessage(FacesMessage.SEVERITY_INFO, "Pago Exitoso", "Venta completada por un total de Q" + String.format("%.2f", totalAmount));
        carrito.clear();
        totalAmount = BigDecimal.ZERO;
    }

    // --- Getters y Setters (solo los necesarios para la vista) ---

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public List<ItemVenta> getCarrito() { return carrito; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public List<Producto> getSearchResults() { return searchResults; }
    public Producto getFoundProduct() { return foundProduct; }
    public int getFoundProductQuantity() { return foundProductQuantity; }
    public void setFoundProductQuantity(int foundProductQuantity) { this.foundProductQuantity = foundProductQuantity; }
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}