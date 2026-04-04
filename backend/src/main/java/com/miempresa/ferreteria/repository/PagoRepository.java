package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pago.
 * Tabla: PAGO — PK: idPago (Integer)
 *
 * Relación 1-a-1 con Ticket (campo 'ticket' en la entidad, UQ folio_ticket en BD).
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    /**
     * Busca el pago asociado a un ticket.
     * Relación @OneToOne: navega por ticket.folioTicket
     * Usado en VentasService para verificar si un ticket ya fue cobrado.
     *
     * SELECT * FROM PAGO WHERE folio_ticket = ?
     */
    Optional<Pago> findByTicket_FolioTicket(Integer folioTicket);

    /**
     * Verifica si un ticket ya tiene un pago registrado.
     * Guard rápido antes de intentar cobrar un ticket.
     *
     * SELECT COUNT(*) > 0 FROM PAGO WHERE folio_ticket = ?
     */
    boolean existsByTicket_FolioTicket(Integer folioTicket);

    /**
     * Devuelve todos los pagos por método de pago.
     * Útil para reportes de métodos más usados o conciliación.
     *
     * SELECT * FROM PAGO WHERE metodo_pago = ?
     */
    List<Pago> findByMetodoPago(String metodoPago);

    /**
     * Suma el montoEfectivo de todos los pagos de tickets cobrados en una fecha.
     * Usado por CorteService para calcular total_efectivo_dia.
     * Navega por la relación @OneToOne hacia Ticket.
     */
    @Query("SELECT COALESCE(SUM(p.montoEfectivo), 0) FROM Pago p WHERE p.ticket.fechaTransaccion = :fecha AND p.ticket.estadoDocumento = 'Pagado'")
    BigDecimal sumEfectivoByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el montoTarjeta de los pagos del día.
     * Usado por CorteService para calcular total_tarjeta_dia.
     */
    @Query("SELECT COALESCE(SUM(p.montoTarjeta), 0) FROM Pago p WHERE p.ticket.fechaTransaccion = :fecha AND p.ticket.estadoDocumento = 'Pagado'")
    BigDecimal sumTarjetaByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el montoTransferencia de los pagos del día.
     * Usado por CorteService para calcular total_transferencia_dia.
     */
    @Query("SELECT COALESCE(SUM(p.montoTransferencia), 0) FROM Pago p WHERE p.ticket.fechaTransaccion = :fecha AND p.ticket.estadoDocumento = 'Pagado'")
    BigDecimal sumTransferenciaByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el montoCredito de los pagos del día.
     * Usado por CorteService para calcular total_credito_dia.
     */
    @Query("SELECT COALESCE(SUM(p.montoCredito), 0) FROM Pago p WHERE p.ticket.fechaTransaccion = :fecha AND p.ticket.estadoDocumento = 'Pagado'")
    BigDecimal sumCreditoByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el montoCheque de los pagos del día.
     * Usado por CorteService para calcular total_cheque_dia.
     */
    @Query("SELECT COALESCE(SUM(p.montoCheque), 0) FROM Pago p WHERE p.ticket.fechaTransaccion = :fecha AND p.ticket.estadoDocumento = 'Pagado'")
    BigDecimal sumChequeByFecha(@Param("fecha") LocalDate fecha);
}
