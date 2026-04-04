package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Ticket.
 * Tabla: TICKET — PK: folioTicket (Integer)
 *
 * Tipos de documento: Ticket | Cotización | Re-Ticket | Devolución
 * Estados:            Activo | Pagado | Cancelado
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    /**
     * Devuelve todos los tickets con estado "Activo" de un usuario.
     * Usado para mostrar las pestañas de tickets abiertos en el módulo de Ventas.
     * Navega por la relación @ManyToOne: usuario.idUsuario
     *
     * SELECT * FROM TICKET WHERE id_usuario = ? AND estado_documento = 'Activo'
     */
    List<Ticket> findByUsuario_IdUsuarioAndEstadoDocumento(Integer idUsuario, String estadoDocumento);

    /**
     * Devuelve todos los tickets de una fecha específica.
     * Usado por el módulo de Corte para calcular los totales del día.
     *
     * SELECT * FROM TICKET WHERE fecha_transaccion = ?
     */
    List<Ticket> findByFechaTransaccion(LocalDate fechaTransaccion);

    /**
     * Devuelve los tickets pagados de una fecha y tipo específicos.
     * Más preciso para calcular totales reales del corte
     * (excluye tickets cancelados o activos sin cobrar).
     *
     * SELECT * FROM TICKET
     * WHERE fecha_transaccion = ?
     *   AND estado_documento = 'Pagado'
     *   AND tipo_documento = 'Ticket'
     */
    List<Ticket> findByFechaTransaccionAndEstadoDocumentoAndTipoDocumento(
            LocalDate fechaTransaccion, String estadoDocumento, String tipoDocumento);

    /**
     * Devuelve los tickets de un cliente específico.
     * Usado en el historial de ventas del cliente (módulo de Clientes).
     * Navega por la relación @ManyToOne: cliente.idCliente
     *
     * SELECT * FROM TICKET WHERE id_cliente = ?
     */
    List<Ticket> findByCliente_IdCliente(Integer idCliente);

    /**
     * Devuelve los tickets de un cliente filtrados por tipo de documento.
     * Permite separar Ventas / Créditos / Cotizaciones en el historial.
     * Navega por la relación @ManyToOne: cliente.idCliente
     *
     * SELECT * FROM TICKET WHERE id_cliente = ? AND tipo_documento = ?
     */
    List<Ticket> findByCliente_IdClienteAndTipoDocumento(Integer idCliente, String tipoDocumento);

    /**
     * Tickets en un rango de fechas, útil para reportes.
     *
     * SELECT * FROM TICKET WHERE fecha_transaccion BETWEEN ? AND ?
     */
    List<Ticket> findByFechaTransaccionBetween(LocalDate desde, LocalDate hasta);

    /**
     * Tickets que referencian a otro ticket (Re-Tickets y Devoluciones).
     * Permite trazabilidad: dado un folio original, saber qué documentos derivaron de él.
     * Navega por la relación @ManyToOne: ticketReferencia.folioTicket
     *
     * SELECT * FROM TICKET WHERE folio_referencia = ?
     */
    List<Ticket> findByTicketReferencia_FolioTicket(Integer folioTicket);

    /**
     * Suma el totalNeto de todos los tickets Pagados de tipo Ticket en una fecha.
     * Usado por CorteService para calcular total_ventas_dia sin traer todas las entidades.
     *
     * SELECT SUM(total_neto) FROM TICKET
     * WHERE fecha_transaccion = :fecha
     *   AND estado_documento = 'Pagado'
     *   AND tipo_documento = 'Ticket'
     */
    @Query("SELECT COALESCE(SUM(t.totalNeto), 0) FROM Ticket t WHERE t.fechaTransaccion = :fecha AND t.estadoDocumento = 'Pagado' AND t.tipoDocumento = 'Ticket'")
    BigDecimal sumTotalNetoPagadoByFecha(@Param("fecha") LocalDate fecha);
}
