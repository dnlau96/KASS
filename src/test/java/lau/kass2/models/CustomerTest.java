package lau.kass2.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private Customer customer;

    @BeforeEach
    public void setUp() {
        // Inicializamos un objeto limpio antes de cada test
        customer = new Customer();
    }

    @Test
    public void testConstructorConParametros() {
        // Probamos el constructor que recibe NIT y Nombre
        Customer instance = new Customer("123456-K", "Tienda Central");
        
        assertAll("Verificación de constructor",
            () -> assertEquals("123456-K", instance.getNit()),
            () -> assertEquals("Tienda Central", instance.getName()),
            () -> assertNull(instance.getId(), "El ID debe ser nulo antes de persistir")
        );
    }

    @Test
    public void testSetAndGetName() {
        String nombre = "Distribuidora Lau";
        customer.setName(nombre);
        assertEquals(nombre, customer.getName(), "El nombre no coincide");
    }

    @Test
    public void testSetAndGetNit() {
        String nit = "998877-0";
        customer.setNit(nit);
        assertEquals(nit, customer.getNit(), "El NIT no coincide");
    }

    @Test
    public void testIdAccess() {
        Long idValue = 100L;
        customer.setId(idValue);
        assertEquals(idValue, customer.getId());
    }
}