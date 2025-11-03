package lau.kass2.models;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customers")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nit;

    @Column(name = "nombre", nullable = false)
    private String name;

    // Constructores, Getters y Setters
    public Customer() {}

    public Customer(String nit, String name) {
        this.nit = nit;
        this.name = name;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}