package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id_pago; // PK [cite: 184, 185]

    @Column(name = "folio_ticket")
    private Integer folio_ticket; // FK [cite: 186, 187]

    @Column(name = "metodo_pago", length = 20)
    private String metodo_pago; // [cite: 193, 194]

    @Column(name = "monto_efectivo", precision = 10, scale = 2)
    private BigDecimal monto_efectivo; // [cite: 197, 198]

    @Column(name = "pago_con", precision = 10, scale = 2)
    private BigDecimal pago_con; // [cite: 201, 202]

    @Column(name = "cambio", precision = 10, scale = 2)
    private BigDecimal cambio; // [cite: 205, 206]

    @Column(name = "monto_tarjeta", precision = 10, scale = 2)
    private BigDecimal monto_tarjeta; // [cite: 209, 210]

    @Column(name = "referencia_tarjeta", length = 100)
    private String referencia_tarjeta; // [cite: 213, 214]

    @Column(name = "voucher_tarjeta")
    private Boolean voucher_tarjeta; // [cite: 219, 226]

    @Column(name = "monto_transferencia", precision = 10, scale = 2)
    private BigDecimal monto_transferencia; // [cite: 220, 226]

    @Column(name = "referencia_transferencia", length = 100)
    private String referencia_transferencia; // [cite: 221, 227]

    @Column(name = "voucher_transferencia")
    private Boolean voucher_transferencia; // [cite: 222, 228]

    @Column(name = "monto_cheque", precision = 10, scale = 2)
    private BigDecimal monto_cheque; // [cite: 223, 230]

    @Column(name = "referencia_cheque", length = 100)
    private String referencia_cheque; // [cite: 224, 229]

    @Column(name = "monto_credito", precision = 10, scale = 2)
    private BigDecimal monto_credito; // [cite: 225, 230]

    // --- Getters y Setters ---

    public Integer getId_pago() { return id_pago; }
    public void setId_pago(Integer id_pago) { this.id_pago = id_pago; }

    public Integer getFolio_ticket() { return folio_ticket; }
    public void setFolio_ticket(Integer folio_ticket) { this.folio_ticket = folio_ticket; }

    public String getMetodo_pago() { return metodo_pago; }
    public void setMetodo_pago(String metodo_pago) { this.metodo_pago = metodo_pago; }

    public BigDecimal getMonto_efectivo() { return monto_efectivo; }
    public void setMonto_efectivo(BigDecimal monto_efectivo) { this.monto_efectivo = monto_efectivo; }

    public BigDecimal getPago_con() { return pago_con; }
    public void setPago_con(BigDecimal pago_con) { this.pago_con = pago_con; }

    public BigDecimal getCambio() { return cambio; }
    public void setCambio(BigDecimal cambio) { this.cambio = cambio; }

    public BigDecimal getMonto_tarjeta() { return monto_tarjeta; }
    public void setMonto_tarjeta(BigDecimal monto_tarjeta) { this.monto_tarjeta = monto_tarjeta; }

    public String getReferencia_tarjeta() { return referencia_tarjeta; }
    public void setReferencia_tarjeta(String referencia_tarjeta) { this.referencia_tarjeta = referencia_tarjeta; }

    public Boolean getVoucher_tarjeta() { return voucher_tarjeta; }
    public void setVoucher_tarjeta(Boolean voucher_tarjeta) { this.voucher_tarjeta = voucher_tarjeta; }

    public BigDecimal getMonto_transferencia() { return monto_transferencia; }
    public void setMonto_transferencia(BigDecimal monto_transferencia) { this.monto_transferencia = monto_transferencia; }

    public String getReferencia_transferencia() { return referencia_transferencia; }
    public void setReferencia_transferencia(String referencia_transferencia) { this.referencia_transferencia = referencia_transferencia; }

    public Boolean getVoucher_transferencia() { return voucher_transferencia; }
    public void setVoucher_transferencia(Boolean voucher_transferencia) { this.voucher_transferencia = voucher_transferencia; }

    public BigDecimal getMonto_cheque() { return monto_cheque; }
    public void setMonto_cheque(BigDecimal monto_cheque) { this.monto_cheque = monto_cheque; }

    public String getReferencia_cheque() { return referencia_cheque; }
    public void setReferencia_cheque(String referencia_cheque) { this.referencia_cheque = referencia_cheque; }

    public BigDecimal getMonto_credito() { return monto_credito; }
    public void setMonto_credito(BigDecimal monto_credito) { this.monto_credito = monto_credito; }
}
