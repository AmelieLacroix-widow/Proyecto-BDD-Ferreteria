package com.miempresa.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modulo")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Integer id_modulo; // PK [cite: 1, 4, 5]

    @Column(name = "nombre_modulo", length = 50)
    private String nombre_modulo; // [cite: 1, 4, 5]

    // --- Getters y Setters ---

    public Integer getId_modulo() {
        return id_modulo;
    }

    public void setId_modulo(Integer id_modulo) {
        this.id_modulo = id_modulo;
    }

    public String getNombre_modulo() {
        return nombre_modulo;
    }

    public void setNombre_modulo(String nombre_modulo) {
        this.nombre_modulo = nombre_modulo;
    }
}
