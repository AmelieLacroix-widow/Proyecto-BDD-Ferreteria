package com.miempresa.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DEPARTAMENTO")
public class Departamento {

    public Departamento() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Integer idDepartamento;

    @Column(name = "nombre_departamento", length = 100, nullable = false)
    private String nombreDepartamento;

    @Column(name = "descripcion_departamento", length = 200)
    private String descripcionDepartamento;

    // --- Getters y Setters ---

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public String getDescripcionDepartamento() {
        return descripcionDepartamento;
    }

    public void setDescripcionDepartamento(String descripcionDepartamento) {
        this.descripcionDepartamento = descripcionDepartamento;
    }
}
