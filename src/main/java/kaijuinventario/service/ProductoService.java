package kaijuinventario.service;

import kaijuinventario.entity.Producto;
import kaijuinventario.repository.ProductoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto buscarPorCodigo(String codigo) {
        return repository.findById(codigo).orElse(null);
    }

    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    public Producto actualizar(String codigo, Producto productoActualizado) {
        Producto producto = repository.findById(codigo).orElse(null);

        if (producto == null) {
            return null;
        }

        producto.setCodigo(productoActualizado.getCodigo());
        producto.setNombre(productoActualizado.getNombre());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setStock(productoActualizado.getStock());
        producto.setStockMinimo(productoActualizado.getStockMinimo());

        return repository.save(producto);
    }

    public void eliminar(String codigo) {
        repository.deleteById(codigo);
    }
}
