package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folio_ticket")
    private Integer folio_ticket; // PK 

    @Column(name = "tipo_documento", length = 20)
    private String tipo_documento; 

    @Column(name = "estado_documento", length = 20)
    private String estado_documento; 

    @Column(name = "fecha_transaccion")
    private LocalDate fecha_transaccion; 

    @Column(name = "hora_transaccion")
    private LocalTime hora_transaccion; 

    @Column(name = "folio_referencia")
    private Integer folio_referencia; // FK [cite: 132, 133]

    @Column(name = "total_bruto", precision = 10, scale = 2)
    private BigDecimal total_bruto; // [cite: 136, 137]

    @Column(name = "porcentaje_descuento", precision = 5, scale = 3)
    private BigDecimal porcentaje_descuento; // [cite: 140, 141]

    @Column(name = "total_descuento", precision = 10, scale = 2)
    private BigDecimal total_descuento; // [cite: 144, 145]

    @Column(name = "total_neto", precision = 10, scale = 2)
    private BigDecimal total_neto; // [cite: 156, 157]

    @Column(name = "id_cliente")
    private Integer id_cliente; // FK [cite: 163, 164]

    @Column(name = "id_usuario")
    private Integer id_usuario; // FK [cite: 167, 168]

    @Column(columnDefinition = "TEXT")
    private String notas; // [cite: 171, 172]

    // --- Getters y Setters ---

    public Integer getFolio_ticket() {
        return folio_ticket;
    }

    public void setFolio_ticket(Integer folio_ticket) {
        this.folio_ticket = folio_ticket;
    }

    public String getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public String getEstado_documento() {
        return estado_documento;
    }

    public void setEstado_documento(String estado_documento) {
        this.estado_documento = estado_documento;
    }

    public LocalDate getFecha_transaccion() {
        return fecha_transaccion;
    }

    public void setFecha_transaccion(LocalDate fecha_transaccion) {
        this.fecha_transaccion = fecha_transaccion;
    }

    public LocalTime getHora_transaccion() {
        return hora_transaccion;
    }

    public void setHora_transaccion(LocalTime hora_transaccion) {
        this.hora_transaccion = hora_transaccion;
    }

    public Integer getFolio_referencia() {
        return folio_referencia;
    }

    public void setFolio_referencia(Integer folio_referencia) {
        this.folio_referencia = folio_referencia;
    }

    public BigDecimal getTotal_bruto() {
        return total_bruto;
    }

    public void setTotal_bruto(BigDecimal total_bruto) {
        this.total_bruto = total_bruto;
    }

    public BigDecimal getPorcentaje_descuento() {
        return porcentaje_descuento;
    }

    public void setPorcentaje_descuento(BigDecimal porcentaje_descuento) {
        this.porcentaje_descuento = porcentaje_descuento;
    }

    public BigDecimal getTotal_descuento() {
        return total_descuento;
    }

    public void setTotal_descuento(BigDecimal total_descuento) {
        this.total_descuento = total_descuento;
    }

    public BigDecimal getTotal_neto() {
        return total_neto;
    }

    public void setTotal_neto(BigDecimal total_neto) {
        this.total_neto = total_neto;
    }

    public Integer getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
