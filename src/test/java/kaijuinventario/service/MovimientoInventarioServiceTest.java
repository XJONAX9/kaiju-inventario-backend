package kaijuinventario.service;

import kaijuinventario.entity.MovimientoInventario;
import kaijuinventario.entity.Producto;
import kaijuinventario.repository.MovimientoInventarioRepository;
import kaijuinventario.repository.ProductoRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class MovimientoInventarioServiceTest {
    @Test
    void debeLanzarErrorCuandoNoHayStockSuficiente() {
        ProductoRepository productoRepository =
                mock(ProductoRepository.class);
        MovimientoInventarioRepository movimientoRepository =
                mock(MovimientoInventarioRepository.class);
        Producto producto = new Producto();
        producto.setCodigo("KJ001");
        producto.setNombre("Poleron Negro");
        producto.setStock(5);
        producto.setStockMinimo(2);
        when(productoRepository.findById("KJ001"))
                .thenReturn(java.util.Optional.of(producto));
        MovimientoInventario movimiento =
                new MovimientoInventario();
        movimiento.setCodigoProducto("KJ001");
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(10);
        MovimientoInventarioService service =
                new MovimientoInventarioService(
                        movimientoRepository,
                        productoRepository
                );
        assertThrows(
                RuntimeException.class,
                () -> service.guardar(movimiento)
        );
    }
}