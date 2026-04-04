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
 * Tabla: pago — PK: id_pago (Integer, snake_case según la entidad)
 *
 * Relación 1-a-1 con Ticket (UQ folio_ticket en BD).
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    /**
     * Busca el pago asociado a un ticket.
     * Relación 1-a-1: cada ticket cobrado tiene exactamente un registro de pago.
     * Usado en VentasService para verificar si un ticket ya fue cobrado.
     *
     * SELECT * FROM pago WHERE folio_ticket = ?
     */
    Optional<Pago> findByFolio_ticket(Integer folioTicket);

    /**
     * Verifica si un ticket ya tiene un pago registrado.
     * Guard rápido antes de intentar cobrar un ticket.
     *
     * SELECT COUNT(*) > 0 FROM pago WHERE folio_ticket = ?
     */
    boolean existsByFolio_ticket(Integer folioTicket);

    /**
     * Devuelve todos los pagos por método de pago.
     * Útil para reportes de métodos más usados o conciliación.
     *
     * SELECT * FROM pago WHERE metodo_pago = ?
     */
    List<Pago> findByMetodo_pago(String metodoPago);

    /**
     * Suma el monto_efectivo de todos los pagos de tickets cobrados en una fecha.
     * Usado por CorteService para calcular total_efectivo_dia.
     *
     * SELECT SUM(p.monto_efectivo) FROM pago p
     * JOIN ticket t ON p.folio_ticket = t.folio_ticket
     * WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado'
     */
    @Query("SELECT COALESCE(SUM(p.monto_efectivo), 0) FROM Pago p JOIN Ticket t ON p.folio_ticket = t.folio_ticket WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado'")
    BigDecimal sumEfectivoByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el monto_tarjeta de los pagos del día.
     * Usado por CorteService para calcular total_tarjeta_dia.
     */
    @Query("SELECT COALESCE(SUM(p.monto_tarjeta), 0) FROM Pago p JOIN Ticket t ON p.folio_ticket = t.folio_ticket WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado'")
    BigDecimal sumTarjetaByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el monto_transferencia de los pagos del día.
     * Usado por CorteService para calcular total_transferencia_dia.
     */
    @Query("SELECT COALESCE(SUM(p.monto_transferencia), 0) FROM Pago p JOIN Ticket t ON p.folio_ticket = t.folio_ticket WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado'")
    BigDecimal sumTransferenciaByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el monto_credito de los pagos del día.
     * Usado por CorteService para calcular total_credito_dia.
     */
    @Query("SELECT COALESCE(SUM(p.monto_credito), 0) FROM Pago p JOIN Ticket t ON p.folio_ticket = t.folio_ticket WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado'")
    BigDecimal sumCreditoByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Suma el monto_cheque de los pagos del día.
     * Usado por CorteService para calcular total_cheque_dia.
     */
    @Query("SELECT COALESCE(SUM(p.monto_cheque), 0) FROM Pago p JOIN Ticket t ON p.folio_ticket = t.folio_ticket WHERE t.fecha_transaccion = :fecha AND t.estado_documento = 'Pagado'")
    BigDecimal sumChequeByFecha(@Param("fecha") LocalDate fecha);
}
