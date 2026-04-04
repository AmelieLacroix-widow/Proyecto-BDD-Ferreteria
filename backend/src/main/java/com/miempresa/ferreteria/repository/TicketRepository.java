package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Ticket.
 * Tabla: ticket — PK: folio_ticket (Integer, snake_case según la entidad)
 *
 * Tipos de documento: Ticket | Cotización | Re-Ticket | Devolución
 * Estados:            Activo | Pagado | Cancelado
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    /**
     * Devuelve todos los tickets con estado "Activo" de un usuario.
     * Usado para mostrar las pestañas de tickets abiertos en el módulo de Ventas.
     *
     * SELECT * FROM ticket WHERE id_usuario = ? AND estado_documento = 'Activo'
     */
    List<Ticket> findById_usuarioAndEstado_documento(Integer idUsuario, String estadoDocumento);

    /**
     * Devuelve todos los tickets de una fecha específica.
     * Usado por el módulo de Corte para calcular los totales del día.
     *
     * SELECT * FROM ticket WHERE fecha_transaccion = ?
     */
    List<Ticket> findByFecha_transaccion(LocalDate fechaTransaccion);

    /**
     * Devuelve los tickets pagados de una fecha específica.
     * Más preciso que el anterior para calcular totales reales del corte
     * (excluye tickets cancelados o activos sin cobrar).
     *
     * SELECT * FROM ticket
     * WHERE fecha_transaccion = ?
     *   AND estado_documento = 'Pagado'
     *   AND tipo_documento = 'Ticket'
     */
    List<Ticket> findByFecha_transaccionAndEstado_documentoAndTipo_documento(
            LocalDate fechaTransaccion, String estadoDocumento, String tipoDocumento);

    /**
     * Devuelve los tickets de un cliente específico.
     * Usado en el historial de ventas del cliente (módulo de Clientes).
     *
     * SELECT * FROM ticket WHERE id_cliente = ?
     */
    List<Ticket> findById_cliente(Integer idCliente);

    /**
     * Devuelve los tickets de un cliente filtrados por tipo de documento.
     * Permite separar Ventas / Créditos / Cotizaciones en el historial.
     *
     * SELECT * FROM ticket WHERE id_cliente = ? AND tipo_documento = ?
     */
    List<Ticket> findById_clienteAndTipo_documento(Integer idCliente, String tipoDocumento);

    /**
     * Tickets en un rango de fechas, útil para reportes.
     *
     * SELECT * FROM ticket WHERE fecha_transaccion BETWEEN ? AND ?
     */
    List<Ticket> findByFecha_transaccionBetween(LocalDate desde, LocalDate hasta);

    /**
     * Tickets que referencian a otro ticket (Re-Tickets y Devoluciones).
     * Permite trazabilidad: dado un folio original, saber qué documentos derivaron de él.
     *
     * SELECT * FROM ticket WHERE folio_referencia = ?
     */
    List<Ticket> findByFolio_referencia(Integer folioReferencia);

    /**
     * Suma el total_neto de todos los tickets Pagados de tipo Ticket en una fecha.
     * Usado por CorteService para calcular total_ventas_dia sin traer todas las entidades.
     *
     * SELECT SUM(total_neto) FROM ticket
     * WHERE fecha_transaccion = :fecha
     *   AND estado_documento = 'Pagado'
     *   AND tipo_documento = 'Ticket'
     */
    @Query("SELECT COALESCE(SUM(t.total_neto), 0) FROM Ticket t WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado' AND t.tipo_documento = 'Ticket'")
    java.math.BigDecimal sumTotalNetoPagadoByFecha(@Param("fecha") LocalDate fecha);
}
