package com.miempresa.ferreteria.model;

import java.io.Serializable;
import java.util.Objects;

public class DetalleTicketId implements Serializable {
    private Integer folio_ticket; // [cite: 83, 96]
    private String codigo_barras; // [cite: 86, 97]

    // Constructores, equals y hashCode son obligatorios para llaves compuestas
    public DetalleTicketId() {}

    public DetalleTicketId(Integer folio_ticket, String codigo_barras) {
        this.folio_ticket = folio_ticket;
        this.codigo_barras = codigo_barras;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleTicketId that = (DetalleTicketId) o;
        return Objects.equals(folio_ticket, that.folio_ticket) && 
               Objects.equals(codigo_barras, that.codigo_barras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folio_ticket, codigo_barras);
    }
}
