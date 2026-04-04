package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Modulo.
 * Tabla: MODULO — PK: idModulo (Integer)
 *
 * Los módulos son fijos del sistema (insertados en el SQL inicial)
 * y no se crean ni eliminan desde la aplicación.
 */
@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {

    /**
     * Busca un módulo por su nombre exacto.
     * Usado en AuthService para obtener el idModulo a partir del nombre
     * ("Ventas", "Productos", "Inventario", etc.)
     *
     * SELECT * FROM MODULO WHERE nombre_modulo = ?
     */
    Optional<Modulo> findByNombreModulo(String nombreModulo);
}
