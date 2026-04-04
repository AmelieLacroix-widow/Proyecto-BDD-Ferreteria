package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "retiro")
public class Retiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retiro")
    private Integer id_retiro; // PK [cite: 10, 11]

    @Column(name = "fecha_retiro")
    private LocalDate fecha_retiro; // 

    @Column(name = "hora_retiro")
    private LocalTime hora_retiro; // 

    @Column(name = "monto_retiro", precision = 10, scale = 2)
    private BigDecimal monto_retiro; // 

    @Column(name = "descripcion_retiro", length = 300)
    private String descripcion_retiro; // 

    @Column(name = "id_usuario")
    private Integer id_usuario; // FK 

    // --- Getters y Setters ---

    public Integer getId_retiro() {
        return id_retiro;
    }

    public void setId_retiro(Integer id_retiro) {
        this.id_retiro = id_retiro;
    }

    public LocalDate getFecha_retiro() {
        return fecha_retiro;
    }

    public void setFecha_retiro(LocalDate fecha_retiro) {
        this.fecha_retiro = fecha_retiro;
    }

    public LocalTime getHora_retiro() {
        return hora_retiro;
    }

    public void setHora_retiro(LocalTime hora_retiro) {
        this.hora_retiro = hora_retiro;
    }

    public BigDecimal getMonto_retiro() {
        return monto_retiro;
    }

    public void setMonto_retiro(BigDecimal monto_retiro) {
        this.monto_retiro = monto_retiro;
    }

    public String getDescripcion_retiro() {
        return descripcion_retiro;
    }

    public void setDescripcion_retiro(String descripcion_retiro) {
        this.descripcion_retiro = descripcion_retiro;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }
}
