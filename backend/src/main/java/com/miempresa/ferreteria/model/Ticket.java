package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "TICKET")
public class Ticket {

    public Ticket() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folio_ticket")
    private Integer folioTicket;

    @Column(name = "tipo_documento", length = 20, nullable = false)
    private String tipoDocumento;

    @Column(name = "estado_documento", length = 15, nullable = false)
    private String estadoDocumento;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDate fechaTransaccion;

    @Column(name = "hora_transaccion", nullable = false)
    private LocalTime horaTransaccion;

    // Auto-referencia: un Re-Ticket o Devolución puede apuntar al ticket original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_referencia")
    private Ticket ticketReferencia;

    @Column(name = "total_bruto", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalBruto;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 3, nullable = false)
    private BigDecimal porcentajeDescuento;

    @Column(name = "total_descuento", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalDescuento;

    @Column(name = "total_neto", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalNeto;

    // Relación ManyToOne con Cliente (nullable: una venta puede ser sin cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Relación ManyToOne con Usuario (nullable = false: toda venta requiere cajero)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // --- Getters y Setters ---

    public Integer getFolioTicket() {
        return folioTicket;
    }

    public void setFolioTicket(Integer folioTicket) {
        this.folioTicket = folioTicket;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public LocalDate getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDate fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public LocalTime getHoraTransaccion() {
        return horaTransaccion;
    }

    public void setHoraTransaccion(LocalTime horaTransaccion) {
        this.horaTransaccion = horaTransaccion;
    }

    public Ticket getTicketReferencia() {
        return ticketReferencia;
    }

    public void setTicketReferencia(Ticket ticketReferencia) {
        this.ticketReferencia = ticketReferencia;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal totalBruto) {
        this.totalBruto = totalBruto;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public BigDecimal getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(BigDecimal totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal totalNeto) {
        this.totalNeto = totalNeto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}