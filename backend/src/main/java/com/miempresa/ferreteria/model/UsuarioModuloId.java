package com.miempresa.ferreteria.model;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioModuloId implements Serializable {
    private Integer id_usuario; // [cite: 16]
    private Integer id_modulo; // [cite: 17]

    public UsuarioModuloId() {}

    public UsuarioModuloId(Integer id_usuario, Integer id_modulo) {
        this.id_usuario = id_usuario;
        this.id_modulo = id_modulo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioModuloId that = (UsuarioModuloId) o;
        return Objects.equals(id_usuario, that.id_usuario) && 
               Objects.equals(id_modulo, that.id_modulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_usuario, id_modulo);
    }
}