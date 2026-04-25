package com.miempresa.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MODULO")
public class Modulo {

    public Modulo() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    private Integer idModulo;

    @Column(name = "nombre_modulo", length = 50, nullable = false, unique = true)
    private String nombreModulo;

    // --- Getters y Setters ---

    public Integer getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public String getNombreModulo() {
        return nombreModulo;
    }

    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }
}
