package com.miempresa.ferreteria.model;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioModuloId implements Serializable {

    // Los nombres DEBEN ser idénticos a los atributos @Id de UsuarioModulo
    private Integer idUsuario;
    private Integer idModulo;

    public UsuarioModuloId() {}

    public UsuarioModuloId(Integer idUsuario, Integer idModulo) {
        this.idUsuario = idUsuario;
        this.idModulo = idModulo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioModuloId that = (UsuarioModuloId) o;
        return Objects.equals(idUsuario, that.idUsuario) &&
               Objects.equals(idModulo, that.idModulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idModulo);
    }
}