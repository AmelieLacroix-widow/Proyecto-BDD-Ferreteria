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
 * Tabla: retiro — PK: id_retiro (Integer, snake_case según la entidad)
 *
 * Registra salidas de efectivo de caja que no son ventas
 * (gastos, retiros del dueño, etc.). Alimenta total_gastos_dia del Corte.
 */
@Repository
public interface RetiroRepository extends JpaRepository<Retiro, Integer> {

    /**
     * Devuelve todos los retiros de una fecha específica.
     * Principal query de CorteService para sumar total_gastos_dia.
     *
     * SELECT * FROM retiro WHERE fecha_retiro = ?
     */
    List<Retiro> findByFecha_retiro(LocalDate fechaRetiro);

    /**
     * Devuelve los retiros de un rango de fechas.
     * Usado en reportes de gastos por período.
     *
     * SELECT * FROM retiro WHERE fecha_retiro BETWEEN ? AND ?
     */
    List<Retiro> findByFecha_retiroBetween(LocalDate desde, LocalDate hasta);

    /**
     * Devuelve los retiros registrados por un usuario específico.
     *
     * SELECT * FROM retiro WHERE id_usuario = ?
     */
    List<Retiro> findById_usuario(Integer idUsuario);

    /**
     * Retiros de un usuario en una fecha concreta.
     * Útil para el detalle de gastos del corte de un cajero.
     *
     * SELECT * FROM retiro WHERE id_usuario = ? AND fecha_retiro = ?
     */
    List<Retiro> findById_usuarioAndFecha_retiro(Integer idUsuario, LocalDate fechaRetiro);

    /**
     * Suma el total de retiros de una fecha.
     * Usado por CorteService para calcular total_gastos_dia directamente en BD.
     *
     * SELECT SUM(monto_retiro) FROM retiro WHERE fecha_retiro = :fecha
     */
    @Query("SELECT COALESCE(SUM(r.monto_retiro), 0) FROM Retiro r WHERE r.fecha_retiro = :fecha")
    BigDecimal sumMontoByFecha(@Param("fecha") LocalDate fecha);
}
