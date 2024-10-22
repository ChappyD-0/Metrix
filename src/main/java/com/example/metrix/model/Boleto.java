package com.example.metrix.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "boleto")
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El código del boleto no puede estar en blanco.")
    @Column(nullable = false, unique = true)  // Marcamos 'unique' para evitar duplicados.
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)  // FetchType.LAZY para mejorar el rendimiento.
    @JoinColumn(name = "funcion_id", nullable = false)
    @JsonBackReference(value = "funcion-boletos")
    private Funcion funcion;

    @ManyToOne(fetch = FetchType.LAZY)  // Mismo fetch lazy para evitar cargar demasiadas relaciones.
    @JoinColumn(name = "compra_id", nullable = false)
    @JsonBackReference
    private Compra compra;

    // Constructor por defecto
    public Boleto() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }
}
