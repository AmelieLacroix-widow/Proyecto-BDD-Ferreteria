package com.miempresa.ferreteria.model;

import jakarta.persistence.*;

@Entity
@Table(name = "USUARIO_MODULO")
@IdClass(UsuarioModuloId.class)
public class UsuarioModulo {

    public UsuarioModulo() {
    }

    // El nombre del atributo DEBE coincidir con UsuarioModuloId.idUsuario
    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // El nombre del atributo DEBE coincidir con UsuarioModuloId.idModulo
    @Id
    @Column(name = "id_modulo")
    private Integer idModulo;

    // Relaciones ManyToOne para poder navegar a los objetos completos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo", insertable = false, updatable = false)
    private Modulo modulo;

    // --- Getters y Setters ---

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }
}
