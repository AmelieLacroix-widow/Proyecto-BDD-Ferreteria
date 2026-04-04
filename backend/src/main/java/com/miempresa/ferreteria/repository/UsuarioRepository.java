package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Tabla: usuario — PK: idUsuario (Integer)
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por nombre de usuario exacto.
     * Usado en AuthService para recuperar el hash y compararlo con SHA-256.
     *
     * SELECT * FROM usuario WHERE nombre_usuario = ?
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Verifica si ya existe ese nombre de usuario.
     * Previene duplicados antes de crear un usuario nuevo.
     *
     * SELECT COUNT(*) > 0 FROM usuario WHERE nombre_usuario = ?
     */
    boolean existsByNombreUsuario(String nombreUsuario);
}
