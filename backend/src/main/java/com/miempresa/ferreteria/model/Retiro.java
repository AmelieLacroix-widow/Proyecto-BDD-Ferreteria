package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "RETIRO")
public class Retiro {

    public Retiro() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retiro")
    private Integer idRetiro;

    @Column(name = "fecha_retiro", nullable = false)
    private LocalDate fechaRetiro;

    @Column(name = "hora_retiro", nullable = false)
    private LocalTime horaRetiro;

    @Column(name = "monto_retiro", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoRetiro;

    @Column(name = "descripcion_retiro", length = 300)
    private String descripcionRetiro;

    // Relación ManyToOne con Usuario (antes era solo un Integer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // --- Getters y Setters ---

    public Integer getIdRetiro() {
        return idRetiro;
    }

    public void setIdRetiro(Integer idRetiro) {
        this.idRetiro = idRetiro;
    }

    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public LocalTime getHoraRetiro() {
        return horaRetiro;
    }

    public void setHoraRetiro(LocalTime horaRetiro) {
        this.horaRetiro = horaRetiro;
    }

    public BigDecimal getMontoRetiro() {
        return montoRetiro;
    }

    public void setMontoRetiro(BigDecimal montoRetiro) {
        this.montoRetiro = montoRetiro;
    }

    public String getDescripcionRetiro() {
        return descripcionRetiro;
    }

    public void setDescripcionRetiro(String descripcionRetiro) {
        this.descripcionRetiro = descripcionRetiro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
