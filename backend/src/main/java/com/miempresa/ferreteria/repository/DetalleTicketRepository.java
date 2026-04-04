package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.DetalleTicket;
import com.miempresa.ferreteria.model.DetalleTicketId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad DetalleTicket.
 * Tabla: detalle_ticket — PK compuesta: (folio_ticket, codigo_barras)
 * Clase de ID compuesto: DetalleTicketId
 */
@Repository
public interface DetalleTicketRepository extends JpaRepository<DetalleTicket, DetalleTicketId> {

    /**
     * Devuelve todos los renglones de un ticket.
     * Operación central del módulo de Ventas: construye la tabla de productos
     * visible en pantalla para un ticket abierto.
     *
     * SELECT * FROM detalle_ticket WHERE folio_ticket = ?
     */
    List<DetalleTicket> findByFolio_ticket(Integer folioTicket);

    /**
     * Busca un renglón específico de un ticket por código de barras.
     * Usado en VentasService para verificar si el producto ya existe en el ticket
     * antes de agregar (incrementar cantidad) o modificar.
     *
     * SELECT * FROM detalle_ticket WHERE folio_ticket = ? AND codigo_barras = ?
     */
    Optional<DetalleTicket> findByFolio_ticketAndCodigo_barras(Integer folioTicket, String codigoBarras);

    /**
     * Elimina todos los renglones de un ticket.
     * Usado al cancelar un ticket para limpiar su detalle antes de cambiar estado.
     *
     * DELETE FROM detalle_ticket WHERE folio_ticket = ?
     */
    @Modifying
    @Transactional
    void deleteByFolio_ticket(Integer folioTicket);

    /**
     * Elimina un renglón específico de un ticket (botón "Eliminar" del prototipo).
     *
     * DELETE FROM detalle_ticket WHERE folio_ticket = ? AND codigo_barras = ?
     */
    @Modifying
    @Transactional
    void deleteByFolio_ticketAndCodigo_barras(Integer folioTicket, String codigoBarras);

    /**
     * Verifica si un producto tiene al menos un renglón en cualquier ticket.
     * Usado en ProductosService antes de intentar eliminar un producto:
     * si la FK tiene ON DELETE RESTRICT, esto evita el error de BD.
     *
     * SELECT COUNT(*) > 0 FROM detalle_ticket WHERE codigo_barras = ?
     */
    boolean existsByCodigo_barras(String codigoBarras);

    /**
     * Devuelve todos los renglones donde aparece un producto específico.
     * Útil para el historial de ventas de un producto o para auditoría.
     *
     * SELECT * FROM detalle_ticket WHERE codigo_barras = ?
     */
    List<DetalleTicket> findByCodigo_barras(String codigoBarras);

    /**
     * Cuenta cuántos renglones (productos distintos) tiene un ticket.
     * Usado para mostrar "Total de artículos" en la pantalla de cobro.
     *
     * SELECT COUNT(*) FROM detalle_ticket WHERE folio_ticket = ?
     */
    long countByFolio_ticket(Integer folioTicket);

    /**
     * Suma las cantidades de un producto vendido entre dos fechas.
     * Útil para reportes de productos más vendidos.
     *
     * SELECT SUM(d.cantidad) FROM detalle_ticket d
     * JOIN ticket t ON d.folio_ticket = t.folio_ticket
     * WHERE d.codigo_barras = :codigoBarras
     *   AND t.fecha_transaccion BETWEEN :desde AND :hasta
     *   AND t.estado_documento = 'Pagado'
     */
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM DetalleTicket d JOIN Ticket t ON d.folio_ticket = t.folio_ticket WHERE d.codigo_barras = :codigoBarras AND t.fecha_transaccion BETWEEN :desde AND :hasta AND t.estado_documento = 'Pagado'")
    java.math.BigDecimal sumCantidadVendidaByProductoAndFechas(
            @Param("codigoBarras") String codigoBarras,
            @Param("desde") java.time.LocalDate desde,
            @Param("hasta") java.time.LocalDate hasta);
}
