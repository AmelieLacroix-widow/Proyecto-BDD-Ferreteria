package com.miempresa.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_modulo")
@IdClass(UsuarioModuloId.class)
public class UsuarioModulo {

    @Id
    @Column(name = "id_usuario")
    private Integer id_usuario; // PK FK [cite: 15, 16]

    @Id
    @Column(name = "id_modulo")
    private Integer id_modulo; // PK FK [cite: 15, 17]

    // --- Getters y Setters ---

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getId_modulo() {
        return id_modulo;
    }

    public void setId_modulo(Integer id_modulo) {
        this.id_modulo = id_modulo;
    }
}