package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Producto;
import com.miempresa.ferreteria.repository.DetalleTicketRepository;
import com.miempresa.ferreteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final DetalleTicketRepository detalleRepo;

    public ProductoService(ProductoRepository repo, DetalleTicketRepository detalleRepo) {
        this.repo = repo;
        this.detalleRepo = detalleRepo;
    }

    public List<Producto> todos() {
        return repo.findAll();
    }

    public Optional<Producto> obtener(String codigoBarras) {
        return repo.findById(codigoBarras);
    }

    public List<Producto> buscar(String texto) {
        return repo.findByDescripcionContainingIgnoreCaseOrCodigoBarrasContaining(texto, texto);
    }

    public List<Producto> porDepartamento(Integer idDepartamento) {
        return repo.findByDepartamento_IdDepartamento(idDepartamento);
    }

    public List<Producto> porProveedor(Integer idProveedor) {
        return repo.findByProveedor_IdProveedor(idProveedor);
    }

    public List<Producto> inventarioActivo() {
        return repo.findByUsaInventarioTrue();
    }

    public List<Producto> bajoMinimo() {
        return repo.findProductosBajoMinimo();
    }

    public BigDecimal costoTotalInventario() {
        return repo.calcularCostoTotalInventario();
    }

    public BigDecimal cantidadTotalInventario() {
        return repo.calcularCantidadTotalInventario();
    }

    public Producto guardar(Producto p) {
        return repo.save(p);
    }

    public int actualizarExistencia(String codigoBarras, BigDecimal existencia) {
        return repo.actualizarExistencia(codigoBarras, existencia);
    }

    /**
     * Elimina un producto solo si no tiene renglones en ningún ticket.
     * La FK en DETALLE_TICKET es ON DELETE RESTRICT, por lo que la BD
     * rechazaría el DELETE. Esta validación previa devuelve un mensaje claro
     * en lugar de dejar explotar una excepción de integridad referencial.
     *
     * @return true si se eliminó, false si tiene ventas registradas.
     */
    public boolean eliminar(String codigoBarras) {
        if (detalleRepo.existsByCodigoBarras(codigoBarras)) {
            return false;
        }
        repo.deleteById(codigoBarras);
        return true;
    }
}
