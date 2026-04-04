package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PAGO")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    // La columna folio_ticket tiene UNIQUE en SQL → relación @OneToOne, no @ManyToOne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_ticket", nullable = false, unique = true)
    private Ticket ticket;

    @Column(name = "metodo_pago", length = 20, nullable = false)
    private String metodoPago;

    @Column(name = "monto_efectivo", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoEfectivo;

    @Column(name = "pago_con", precision = 10, scale = 2)
    private BigDecimal pagoCon;

    @Column(name = "cambio", precision = 10, scale = 2)
    private BigDecimal cambio;

    @Column(name = "monto_tarjeta", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTarjeta;

    @Column(name = "referencia_tarjeta", length = 100)
    private String referenciaTarjeta;

    @Column(name = "voucher_tarjeta")
    private Boolean voucherTarjeta;

    @Column(name = "monto_transferencia", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTransferencia;

    @Column(name = "referencia_transferencia", length = 100)
    private String referenciaTransferencia;

    @Column(name = "voucher_transferencia")
    private Boolean voucherTransferencia;

    @Column(name = "monto_cheque", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoCheque;

    @Column(name = "referencia_cheque", length = 100)
    private String referenciaCheque;

    @Column(name = "monto_credito", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoCredito;

    // --- Getters y Setters ---

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMontoEfectivo() {
        return montoEfectivo;
    }

    public void setMontoEfectivo(BigDecimal montoEfectivo) {
        this.montoEfectivo = montoEfectivo;
    }

    public BigDecimal getPagoCon() {
        return pagoCon;
    }

    public void setPagoCon(BigDecimal pagoCon) {
        this.pagoCon = pagoCon;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    public BigDecimal getMontoTarjeta() {
        return montoTarjeta;
    }

    public void setMontoTarjeta(BigDecimal montoTarjeta) {
        this.montoTarjeta = montoTarjeta;
    }

    public String getReferenciaTarjeta() {
        return referenciaTarjeta;
    }

    public void setReferenciaTarjeta(String referenciaTarjeta) {
        this.referenciaTarjeta = referenciaTarjeta;
    }

    public Boolean getVoucherTarjeta() {
        return voucherTarjeta;
    }

    public void setVoucherTarjeta(Boolean voucherTarjeta) {
        this.voucherTarjeta = voucherTarjeta;
    }

    public BigDecimal getMontoTransferencia() {
        return montoTransferencia;
    }

    public void setMontoTransferencia(BigDecimal montoTransferencia) {
        this.montoTransferencia = montoTransferencia;
    }

    public String getReferenciaTransferencia() {
        return referenciaTransferencia;
    }

    public void setReferenciaTransferencia(String referenciaTransferencia) {
        this.referenciaTransferencia = referenciaTransferencia;
    }

    public Boolean getVoucherTransferencia() {
        return voucherTransferencia;
    }

    public void setVoucherTransferencia(Boolean voucherTransferencia) {
        this.voucherTransferencia = voucherTransferencia;
    }

    public BigDecimal getMontoCheque() {
        return montoCheque;
    }

    public void setMontoCheque(BigDecimal montoCheque) {
        this.montoCheque = montoCheque;
    }

    public String getReferenciaCheque() {
        return referenciaCheque;
    }

    public void setReferenciaCheque(String referenciaCheque) {
        this.referenciaCheque = referenciaCheque;
    }

    public BigDecimal getMontoCredito() {
        return montoCredito;
    }

    public void setMontoCredito(BigDecimal montoCredito) {
        this.montoCredito = montoCredito;
    }
}