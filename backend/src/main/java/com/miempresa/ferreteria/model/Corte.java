package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CORTE")
public class Corte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corte")
    private Integer idCorte;

    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    @Column(name = "fondo_inicial", precision = 10, scale = 2, nullable = false)
    private BigDecimal fondoInicial;

    @Column(name = "total_ventas_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalVentasDia;

    @Column(name = "total_ventas_con_dcto", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalVentasConDcto;

    @Column(name = "total_ventas_sin_dcto", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalVentasSinDcto;

    @Column(name = "venta_neta", precision = 10, scale = 2, nullable = false)
    private BigDecimal ventaNeta;

    @Column(name = "total_efectivo_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalEfectivoDia;

    @Column(name = "total_tarjeta_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalTarjetaDia;

    @Column(name = "total_transferencia_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalTransferenciaDia;

    @Column(name = "total_credito_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalCreditoDia;

    @Column(name = "total_cheque_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalChequeDia;

    @Column(name = "total_gastos_dia", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalGastosDia;

    @Column(name = "fondo_final", precision = 10, scale = 2, nullable = false)
    private BigDecimal fondoFinal;

    @Column(name = "total_entregado", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalEntregado;

    @Column(name = "diferencia", precision = 10, scale = 2, nullable = false)
    private BigDecimal diferencia;

    // Conteo de billetes — el original omitía billetes_1000, se agrega aquí
    @Column(name = "billetes_1000", nullable = false)
    private Integer billetes1000;

    @Column(name = "billetes_500", nullable = false)
    private Integer billetes500;

    @Column(name = "billetes_200", nullable = false)
    private Integer billetes200;

    @Column(name = "billetes_100", nullable = false)
    private Integer billetes100;

    @Column(name = "billetes_50", nullable = false)
    private Integer billetes50;

    @Column(name = "billetes_20", nullable = false)
    private Integer billetes20;

    @Column(name = "monedas", precision = 10, scale = 2, nullable = false)
    private BigDecimal monedas;

    @Column(name = "dolares", precision = 10, scale = 2, nullable = false)
    private BigDecimal dolares;

    @Column(name = "venta_sin_ticket", precision = 10, scale = 2, nullable = false)
    private BigDecimal ventaSinTicket;

    @Column(name = "entrada_caja", precision = 10, scale = 2, nullable = false)
    private BigDecimal entradaCaja;

    // Relación ManyToOne con Usuario (antes era solo un Integer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // --- Getters y Setters ---

    public Integer getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(Integer idCorte) {
        this.idCorte = idCorte;
    }

    public LocalDate getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(LocalDate fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public BigDecimal getFondoInicial() {
        return fondoInicial;
    }

    public void setFondoInicial(BigDecimal fondoInicial) {
        this.fondoInicial = fondoInicial;
    }

    public BigDecimal getTotalVentasDia() {
        return totalVentasDia;
    }

    public void setTotalVentasDia(BigDecimal totalVentasDia) {
        this.totalVentasDia = totalVentasDia;
    }

    public BigDecimal getTotalVentasConDcto() {
        return totalVentasConDcto;
    }

    public void setTotalVentasConDcto(BigDecimal totalVentasConDcto) {
        this.totalVentasConDcto = totalVentasConDcto;
    }

    public BigDecimal getTotalVentasSinDcto() {
        return totalVentasSinDcto;
    }

    public void setTotalVentasSinDcto(BigDecimal totalVentasSinDcto) {
        this.totalVentasSinDcto = totalVentasSinDcto;
    }

    public BigDecimal getVentaNeta() {
        return ventaNeta;
    }

    public void setVentaNeta(BigDecimal ventaNeta) {
        this.ventaNeta = ventaNeta;
    }

    public BigDecimal getTotalEfectivoDia() {
        return totalEfectivoDia;
    }

    public void setTotalEfectivoDia(BigDecimal totalEfectivoDia) {
        this.totalEfectivoDia = totalEfectivoDia;
    }

    public BigDecimal getTotalTarjetaDia() {
        return totalTarjetaDia;
    }

    public void setTotalTarjetaDia(BigDecimal totalTarjetaDia) {
        this.totalTarjetaDia = totalTarjetaDia;
    }

    public BigDecimal getTotalTransferenciaDia() {
        return totalTransferenciaDia;
    }

    public void setTotalTransferenciaDia(BigDecimal totalTransferenciaDia) {
        this.totalTransferenciaDia = totalTransferenciaDia;
    }

    public BigDecimal getTotalCreditoDia() {
        return totalCreditoDia;
    }

    public void setTotalCreditoDia(BigDecimal totalCreditoDia) {
        this.totalCreditoDia = totalCreditoDia;
    }

    public BigDecimal getTotalChequeDia() {
        return totalChequeDia;
    }

    public void setTotalChequeDia(BigDecimal totalChequeDia) {
        this.totalChequeDia = totalChequeDia;
    }

    public BigDecimal getTotalGastosDia() {
        return totalGastosDia;
    }

    public void setTotalGastosDia(BigDecimal totalGastosDia) {
        this.totalGastosDia = totalGastosDia;
    }

    public BigDecimal getFondoFinal() {
        return fondoFinal;
    }

    public void setFondoFinal(BigDecimal fondoFinal) {
        this.fondoFinal = fondoFinal;
    }

    public BigDecimal getTotalEntregado() {
        return totalEntregado;
    }

    public void setTotalEntregado(BigDecimal totalEntregado) {
        this.totalEntregado = totalEntregado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public Integer getBilletes1000() {
        return billetes1000;
    }

    public void setBilletes1000(Integer billetes1000) {
        this.billetes1000 = billetes1000;
    }

    public Integer getBilletes500() {
        return billetes500;
    }

    public void setBilletes500(Integer billetes500) {
        this.billetes500 = billetes500;
    }

    public Integer getBilletes200() {
        return billetes200;
    }

    public void setBilletes200(Integer billetes200) {
        this.billetes200 = billetes200;
    }

    public Integer getBilletes100() {
        return billetes100;
    }

    public void setBilletes100(Integer billetes100) {
        this.billetes100 = billetes100;
    }

    public Integer getBilletes50() {
        return billetes50;
    }

    public void setBilletes50(Integer billetes50) {
        this.billetes50 = billetes50;
    }

    public Integer getBilletes20() {
        return billetes20;
    }

    public void setBilletes20(Integer billetes20) {
        this.billetes20 = billetes20;
    }

    public BigDecimal getMonedas() {
        return monedas;
    }

    public void setMonedas(BigDecimal monedas) {
        this.monedas = monedas;
    }

    public BigDecimal getDolares() {
        return dolares;
    }

    public void setDolares(BigDecimal dolares) {
        this.dolares = dolares;
    }

    public BigDecimal getVentaSinTicket() {
        return ventaSinTicket;
    }

    public void setVentaSinTicket(BigDecimal ventaSinTicket) {
        this.ventaSinTicket = ventaSinTicket;
    }

    public BigDecimal getEntradaCaja() {
        return entradaCaja;
    }

    public void setEntradaCaja(BigDecimal entradaCaja) {
        this.entradaCaja = entradaCaja;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

