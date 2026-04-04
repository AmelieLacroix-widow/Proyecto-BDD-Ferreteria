package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Proveedor.
 * Tabla: PROVEEDOR — PK: idProveedor (Integer)
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    /**
     * Búsqueda de proveedores por nombre parcial (sin distinción de mayúsculas).
     * Usado en el dropdown de selección de proveedor al crear/editar un producto.
     *
     * SELECT * FROM PROVEEDOR WHERE nombre_proveedor LIKE %?%
     */
    List<Proveedor> findByNombreProveedorContainingIgnoreCase(String nombre);
}
