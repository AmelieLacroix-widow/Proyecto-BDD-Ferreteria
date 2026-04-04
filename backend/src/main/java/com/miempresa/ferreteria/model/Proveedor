package com.miempresa.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor; // PK 

    @Column(name = "nombre_proveedor", length = 200)
    private String nombreProveedor; // [cite: 149]

    @Column(length = 20)
    private String telefono; // [cite: 152]

    @Column(length = 100)
    private String correo; // [cite: 153]

    @Column(length = 300)
    private String direccion; // [cite: 154]

    @Column(columnDefinition = "TEXT")
    private String notas; // [cite: 171]

    // --- Getters y Setters ---

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
