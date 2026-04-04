package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Retiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Retiro.
 * Tabla: RETIRO — PK: idRetiro (Integer)
 *
 * Registra salidas de efectivo de caja que no son ventas
 * (gastos, retiros del dueño, etc.). Alimenta totalGastosDia del Corte.
 */
@Repository
public interface RetiroRepository extends JpaRepository<Retiro, Integer> {

    /**
     * Devuelve todos los retiros de una fecha específica.
     * Principal query de CorteService para sumar totalGastosDia.
     *
     * SELECT * FROM RETIRO WHERE fecha_retiro = ?
     */
    List<Retiro> findByFechaRetiro(LocalDate fechaRetiro);

    /**
     * Devuelve los retiros de un rango de fechas.
     * Usado en reportes de gastos por período.
     *
     * SELECT * FROM RETIRO WHERE fecha_retiro BETWEEN ? AND ?
     */
    List<Retiro> findByFechaRetiroBetween(LocalDate desde, LocalDate hasta);

    /**
     * Devuelve los retiros registrados por un usuario específico.
     * Navega por la relación @ManyToOne: usuario.idUsuario
     *
     * SELECT * FROM RETIRO WHERE id_usuario = ?
     */
    List<Retiro> findByUsuario_IdUsuario(Integer idUsuario);

    /**
     * Retiros de un usuario en una fecha concreta.
     * Útil para el detalle de gastos del corte de un cajero.
     * Navega por la relación @ManyToOne: usuario.idUsuario
     *
     * SELECT * FROM RETIRO WHERE id_usuario = ? AND fecha_retiro = ?
     */
    List<Retiro> findByUsuario_IdUsuarioAndFechaRetiro(Integer idUsuario, LocalDate fechaRetiro);

    /**
     * Suma el total de retiros de una fecha.
     * Usado por CorteService para calcular totalGastosDia directamente en BD.
     *
     * SELECT SUM(monto_retiro) FROM RETIRO WHERE fecha_retiro = :fecha
     */
    @Query("SELECT COALESCE(SUM(r.montoRetiro), 0) FROM Retiro r WHERE r.fechaRetiro = :fecha")
    BigDecimal sumMontoByFecha(@Param("fecha") LocalDate fecha);
}
