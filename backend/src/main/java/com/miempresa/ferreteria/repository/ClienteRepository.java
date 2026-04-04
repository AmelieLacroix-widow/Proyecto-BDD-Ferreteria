package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Cliente.
 * Tabla: cliente — PK: idCliente (Integer, camelCase)
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Búsqueda por nombre parcial (sin distinción de mayúsculas).
     * Usado en la ventana "Asignar Cliente" del módulo de Ventas
     * y en el listado del módulo de Clientes.
     *
     * SELECT * FROM cliente WHERE nombres LIKE %?%
     */
    List<Cliente> findByNombresContainingIgnoreCase(String nombres);

    /**
     * Búsqueda por apellido paterno parcial.
     * Complementa la búsqueda por nombre.
     *
     * SELECT * FROM cliente WHERE apellido_paterno LIKE %?%
     */
    List<Cliente> findByApellidoPaternoContainingIgnoreCase(String apellidoPaterno);

    /**
     * Devuelve clientes con crédito habilitado (tiene_credito = TRUE).
     * Popula la pestaña "Deudores" del módulo de Clientes.
     *
     * SELECT * FROM cliente WHERE tiene_credito = TRUE
     */
    List<Cliente> findByTieneCreditoTrue();

    /**
     * Devuelve clientes sin crédito (tiene_credito = FALSE).
     * Popula la pestaña "No Deudores" del módulo de Clientes.
     *
     * SELECT * FROM cliente WHERE tiene_credito = FALSE
     */
    List<Cliente> findByTieneCreditoFalse();

    /**
     * Búsqueda por teléfono exacto.
     * Útil para evitar duplicados al registrar un nuevo cliente.
     *
     * SELECT * FROM cliente WHERE telefono = ?
     */
    List<Cliente> findByTelefono(String telefono);
}
