package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Producto;
import com.miempresa.ferreteria.model.Departamento;
import com.miempresa.ferreteria.model.Proveedor;
import com.miempresa.ferreteria.repository.ProductoRepository;
import com.miempresa.ferreteria.repository.DetalleTicketRepository;
import com.miempresa.ferreteria.repository.DepartamentoRepository;
import com.miempresa.ferreteria.repository.ProveedorRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final DetalleTicketRepository detalleRepo;
    private final DepartamentoRepository departamentoRepo;
    private final ProveedorRepository proveedorRepo;

    public ProductoService(ProductoRepository repo,
                           DetalleTicketRepository detalleRepo,
                           DepartamentoRepository departamentoRepo,
                           ProveedorRepository proveedorRepo) {
        this.repo = repo;
        this.detalleRepo = detalleRepo;
        this.departamentoRepo = departamentoRepo;
        this.proveedorRepo = proveedorRepo;
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

    // 🔥 MÉTODO CORREGIDO
    public Producto guardar(Producto p) {

        // 🔹 Validar y cargar Departamento desde BD
        if (p.getDepartamento() != null) {
            Integer idDep = p.getDepartamento().getIdDepartamento();

            Departamento dep = departamentoRepo.findById(idDep)
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado con id: " + idDep));

            p.setDepartamento(dep);
        }

        // 🔹 Validar y cargar Proveedor desde BD
        if (p.getProveedor() != null) {
            Integer idProv = p.getProveedor().getIdProveedor();

            Proveedor prov = proveedorRepo.findById(idProv)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + idProv));

            p.setProveedor(prov);
        }

        return repo.save(p);
    }

    public int actualizarExistencia(String codigoBarras, BigDecimal existencia) {
        return repo.actualizarExistencia(codigoBarras, existencia);
    }

    public boolean eliminar(String codigoBarras) {
        if (detalleRepo.existsByCodigoBarras(codigoBarras)) {
            return false;
        }
        repo.deleteById(codigoBarras);
        return true;
    }
}