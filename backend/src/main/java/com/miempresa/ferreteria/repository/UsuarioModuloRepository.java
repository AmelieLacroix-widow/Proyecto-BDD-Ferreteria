package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.UsuarioModulo;
import com.miempresa.ferreteria.model.UsuarioModuloId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad UsuarioModulo.
 * Tabla: usuario_modulo — PK compuesta: (id_usuario, id_modulo)
 * Clase de ID compuesto: UsuarioModuloId
 */
@Repository
public interface UsuarioModuloRepository extends JpaRepository<UsuarioModulo, UsuarioModuloId> {

    /**
     * Devuelve todos los registros de módulos asignados a un usuario.
     * Usado en AuthService para construir la lista de módulos autorizados
     * que se devuelve al cliente tras el login.
     *
     * SELECT * FROM usuario_modulo WHERE id_usuario = ?
     */
    List<UsuarioModulo> findById_usuario(Integer idUsuario);

    /**
     * Devuelve todos los usuarios que tienen acceso a un módulo concreto.
     * Útil para auditoría o administración de permisos.
     *
     * SELECT * FROM usuario_modulo WHERE id_modulo = ?
     */
    List<UsuarioModulo> findById_modulo(Integer idModulo);

    /**
     * Verifica si un usuario tiene acceso a un módulo específico.
     * Usado como guard en los controllers para validar permisos antes de procesar
     * una operación sensible.
     *
     * SELECT COUNT(*) > 0 FROM usuario_modulo WHERE id_usuario = ? AND id_modulo = ?
     */
    boolean existsById_usuarioAndId_modulo(Integer idUsuario, Integer idModulo);

    /**
     * Devuelve los nombres de los módulos autorizados para un usuario.
     * Hace JOIN con MODULO para evitar N+1 queries al construir el LoginResponse.
     *
     * SELECT m.nombre_modulo FROM usuario_modulo um
     * JOIN modulo m ON um.id_modulo = m.id_modulo
     * WHERE um.id_usuario = :idUsuario
     */
    @Query("SELECT m.nombre_modulo FROM UsuarioModulo um JOIN Modulo m ON um.id_modulo = m.id_modulo WHERE um.id_usuario = :idUsuario")
    List<String> findNombresModulosByIdUsuario(@Param("idUsuario") Integer idUsuario);

    /**
     * Elimina todos los módulos asignados a un usuario.
     * Usado al eliminar un usuario o al resetear sus permisos.
     *
     * DELETE FROM usuario_modulo WHERE id_usuario = ?
     */
    void deleteById_usuario(Integer idUsuario);
}
