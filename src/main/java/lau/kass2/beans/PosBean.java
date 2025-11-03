package lau.kass2.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lau.kass2.models.Customer;
import lau.kass2.models.Product;
import lau.kass2.models.SaleItem;
import lau.kass2.models.User; // Importa User
import lau.kass2.services.ProductService;
import lau.kass2.services.UserService; // Importa UserService
import org.primefaces.PrimeFaces;
import lau.kass2.services.InventoryService;
import lau.kass2.beans.AdminBean;
import lau.kass2.services.SaleService;

import java.util.Date; 
import lau.kass2.models.Sale;
import lau.kass2.models.SaleDetail;
import lau.kass2.services.SaleService;
import lau.kass2.services.CustomerService;

@Named("posBean")
@ViewScoped
public class PosBean implements Serializable {

    @Inject
    private ProductService productService;
    @Inject
    private UserService userService; // Inyecta el servicio de usuario
    
    @Inject
    private InventoryService inventoryService;

    @Inject
    private AdminBean adminBean;
    
    @Inject
    private CustomerService customerService;
    
    @Inject
    private SaleService saleService;
    
    // Propiedades del Cliente
    private String customerNit;
    private String customerName;
    private Customer currentCustomer;

    // Propiedades de la Venta
    private String barcode;
    private String searchTerm;
    private List<SaleItem> shoppingCart = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Propiedades de Búsqueda y Confirmación
    private Product foundProduct;
    private int foundProductQuantity = 1;
    private List<Product> searchResults = new ArrayList<>();
    private final List<String> alphabet = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

    // Propiedades de Autorización
    private SaleItem itemToRemove;
    private String authorizationCode;

    @PostConstruct
    public void init() {
        this.currentCustomer = new Customer("C/F", "Consumidor Final");
    }

    /**
     * Lógica para identificar al cliente.
     * AHORA también añade el producto que estaba pendiente.
     */
    public void registerCustomer() {
        // Si el usuario SÍ escribió datos, se registran
        if (customerNit != null && !customerNit.isBlank() && customerName != null && !customerName.isBlank()) {
            this.currentCustomer = new Customer(customerNit, customerName);
            addMessage(FacesMessage.SEVERITY_INFO, "Cliente Identificado", this.customerName);
        
        } else {
            // Si el usuario dejó los campos vacíos (o cerró), se asume C/F
            this.currentCustomer = new Customer("C/F", "Consumidor Final");
            // Opcional: puedes poner un mensaje de info o no poner nada
            // addMessage(FacesMessage.SEVERITY_INFO, "Cliente", "Consumidor Final seleccionado.");
        }
        
        // Cierra el diálogo en AMBOS casos (éxito o vacío)
        PrimeFaces.current().executeScript("PF('customerDialogWidget').hide()");

        //  Si hay un producto pendiente (foundProduct), lo añade.
        if (this.foundProduct != null) {
            addProductToSale();
        }
    }
    
    /**
     * Lógica de escaneo. AHORA decide qué diálogo mostrar.
     */
    public void findProductByBarcode() {
        if (this.barcode != null && this.barcode.length() == 13) {
            Product locatedProduct = productService.findByBarcode(this.barcode);
            if (locatedProduct != null) {
                this.foundProduct = locatedProduct;
                this.foundProductQuantity = 1;
                
                // --- NUEVA LÓGICA ---
                // Verifica si es el primer producto Y el cliente es "C/F"
                if (shoppingCart.isEmpty() && currentCustomer.getNit().equals("C/F")) {
                    // SÍ: Es el primer producto, pide el NIT.
                    PrimeFaces.current().ajax().addCallbackParam("productFound", true);
                    PrimeFaces.current().ajax().addCallbackParam("showCustomerDialog", true); // Flag para el cliente
                } else {
                    // NO: Es el segundo producto (o el cliente ya está identificado).
                    PrimeFaces.current().ajax().addCallbackParam("productFound", true);
                    PrimeFaces.current().ajax().addCallbackParam("showCustomerDialog", false); // Flag para confirmación normal
                }
                // --- FIN NUEVA LÓGICA ---
                
            } else {
                addMessage(FacesMessage.SEVERITY_WARN, "No Encontrado", "El producto con código " + this.barcode + " no existe.");
                clearBarcode();
                PrimeFaces.current().ajax().addCallbackParam("productFound", false);
            }
        }
    }

    public void addProductToSale() {
        if (foundProduct != null) {
            shoppingCart.stream()
                .filter(item -> item.getProduct().getId().equals(foundProduct.getId()))
                .findFirst()
                .ifPresentOrElse(
                    item -> item.incrementQuantity(foundProductQuantity),
                    () -> shoppingCart.add(new SaleItem(foundProduct, foundProductQuantity))
                );
            addMessage(FacesMessage.SEVERITY_INFO, "Producto Agregado", foundProduct.getName());
            updateTotal();
            clearBarcodeAndFocus();
            this.foundProduct = null; // Limpia el producto pendiente
        }
    }
    
    public void cancelAddProduct() {
        this.foundProduct = null;
        clearBarcodeAndFocus();
    }
    
    private void clearBarcode() {
        this.barcode = "";
    }
    
    private void clearBarcodeAndFocus() {
        clearBarcode();
        PrimeFaces.current().focus("mainForm:barcode"); 
    }

    public void performSearch() {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            this.searchResults = productService.searchByName(searchTerm);
        }
    }

    public void clearSearchResults() {
        this.searchTerm = "";
        if (this.searchResults != null) {
            this.searchResults.clear();
        }
    }
    
    public void addProductFromSearch(Product selectedProduct) {
        this.foundProduct = selectedProduct;
        this.foundProductQuantity = 1;
        // Reusa la misma lógica de "primer producto"
        if (shoppingCart.isEmpty() && currentCustomer.getNit().equals("C/F")) {
            PrimeFaces.current().executeScript("PF('customerDialogWidget').show()");
        } else {
            addProductToSale();
            PrimeFaces.current().executeScript("PF('searchDialogWidget').hide()");
        }
    }
    
    public void filterByFirstLetter(String letter) {
        this.searchResults = productService.findByFirstLetter(letter);
    }

    public void updateTotal() {
        totalAmount = shoppingCart.stream()
            .map(SaleItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void checkout() {
        if (shoppingCart.isEmpty()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Carrito Vacío", "No hay productos para procesar.");
            return;
        }

        try {
            // 1. Obtener Cliente y Usuario
            Customer customer = customerService.findOrCreateCustomer(currentCustomer.getNit(), currentCustomer.getName());
            
            User currentUser = adminBean.getCurrentUser();
            if (currentUser == null) {
                 currentUser = userService.validateCredentialsAndGetUser("admin", "admin"); // Fallback por si no hay login
                 if (currentUser == null) {
                     addMessage(FacesMessage.SEVERITY_FATAL, "Error de Sesión", "No se pudo identificar al vendedor. Inicie sesión de nuevo.");
                     return;
                 }
            }

            // 2. Llamar al servicio para que haga todo el trabajo
            Sale completedSale = saleService.createSale(shoppingCart, customer, currentUser, totalAmount);

            // 3. Limpiar todo y dar mensaje de éxito
            addMessage(FacesMessage.SEVERITY_INFO, "¡Pago Exitoso!", "Venta #" + completedSale.getId() + " registrada. Gracias por su compra.");
            
            shoppingCart.clear();
            totalAmount = BigDecimal.ZERO;
            this.currentCustomer = new Customer("C/F", "Consumidor Final");
            this.customerNit = null;
            this.customerName = null;
            
            // Actualiza la vista
            PrimeFaces.current().ajax().update(":mainForm:cartSection", ":mainForm:totalPanel", ":customerInfoPanel", ":mainForm:actionsPanelWrapper");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_FATAL, "Error Grave", "No se pudo registrar la venta: " + e.getMessage());
            e.printStackTrace(); // Imprime el error en el log de Payara
        }
    }
    
    // --- Lógica de Autorización ---
    public void selectItemForRemoval(SaleItem item) {
        this.itemToRemove = item;
        this.authorizationCode = null;
    }

    public void authorizeAndRemoveItem() {
        if (this.authorizationCode == null || this.itemToRemove == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Acción inválida.");
            return;
        }
        User authorizedUser = userService.authorizeActionByBadge(this.authorizationCode);
        if (authorizedUser != null) {
            this.shoppingCart.remove(this.itemToRemove);
            updateTotal();
            addMessage(FacesMessage.SEVERITY_INFO, "Autorizado", "Producto eliminado por " + authorizedUser.getUsername());
            PrimeFaces.current().executeScript("PF('authorizationDialogWidget').hide()");
            PrimeFaces.current().ajax().update(":mainForm:cartTable", ":mainForm:totalPanel");
        } else {
            addMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado", "El código de supervisor no es válido.");
        }
        this.authorizationCode = "";
        this.itemToRemove = null;
    }
    
    

    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // --- Getters y Setters ---
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public List<SaleItem> getShoppingCart() { return shoppingCart; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public List<Product> getSearchResults() { return searchResults; }
    public Product getFoundProduct() { return foundProduct; }
    public int getFoundProductQuantity() { return foundProductQuantity; }
    public void setFoundProductQuantity(int foundProductQuantity) { this.foundProductQuantity = foundProductQuantity; }
    public Customer getCurrentCustomer() { return currentCustomer; }
    public String getCustomerNit() { return customerNit; }
    public void setCustomerNit(String customerNit) { this.customerNit = customerNit; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public List<String> getAlphabet() { return alphabet; }
    public SaleItem getItemToRemove() { return itemToRemove; }
    public void setItemToRemove(SaleItem itemToRemove) { this.itemToRemove = itemToRemove; }
    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
}