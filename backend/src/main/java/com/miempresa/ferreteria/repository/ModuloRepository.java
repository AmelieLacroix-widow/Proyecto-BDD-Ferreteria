package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Modulo.
 * Tabla: modulo — PK: id_modulo (Integer, snake_case según la entidad)
 *
 * Los módulos son fijos del sistema (insertados en el SQL inicial)
 * y no se crean ni eliminan desde la aplicación.
 */
@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {

    /**
     * Busca un módulo por su nombre exacto.
     * Usado en AuthService para obtener el id_modulo a partir del nombre
     * ("Ventas", "Productos", "Inventario", etc.)
     *
     * Nota: el campo en la entidad se llama nombre_modulo (snake_case),
     * por lo que el derived query usa ese nombre exacto.
     *
     * SELECT * FROM modulo WHERE nombre_modulo = ?
     */
    Optional<Modulo> findByNombre_modulo(String nombreModulo);
}
