package kaijuinventario.service;

import kaijuinventario.entity.MovimientoInventario;
import kaijuinventario.repository.MovimientoInventarioRepository;
import kaijuinventario.repository.ProductoRepository;
import kaijuinventario.entity.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioService {
    private final MovimientoInventarioRepository repository;

    public List<MovimientoInventario> listar() {
        return repository.findAll();
    }

    private final ProductoRepository productoRepository;

    public MovimientoInventarioService(
            MovimientoInventarioRepository repository,
            ProductoRepository productoRepository
    ) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public MovimientoInventario guardar(
        MovimientoInventario movimiento
) {
    Producto producto = productoRepository
            .findById(movimiento.getCodigoProducto())
            .orElse(null);

    if (producto == null) {
        return null;
    }

    if ("ENTRADA".equalsIgnoreCase(
            movimiento.getTipoMovimiento()
    )) {
        producto.setStock(
                producto.getStock()
                        + movimiento.getCantidad()
        );
    } else if ("SALIDA".equalsIgnoreCase(
        movimiento.getTipoMovimiento()
    )) {
        if (movimiento.getCantidad() > producto.getStock()) {
            throw new RuntimeException(
                    "Stock insuficiente para realizar la salida"
            );
        }

        producto.setStock(
                producto.getStock()
                        - movimiento.getCantidad()
        );
    }

    productoRepository.save(producto);

    return repository.save(movimiento);
}
}
