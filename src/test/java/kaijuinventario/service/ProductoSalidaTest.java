package kaijuinventario.service;

import kaijuinventario.entity.MovimientoInventario;
import kaijuinventario.entity.Producto;
import kaijuinventario.repository.MovimientoInventarioRepository;
import kaijuinventario.repository.ProductoRepository;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductoSalidaTest {
    @Test
    void debeDisminuirStockCuandoEsSalida() {
        ProductoRepository productoRepository =
                mock(ProductoRepository.class);
        MovimientoInventarioRepository movimientoRepository =
                mock(MovimientoInventarioRepository.class);
        Producto producto = new Producto();
        producto.setCodigo("KJ001");
        producto.setStock(20);
        producto.setStockMinimo(2);
        when(productoRepository.findById("KJ001"))
                .thenReturn(Optional.of(producto));
        MovimientoInventario movimiento =
                new MovimientoInventario();
        movimiento.setCodigoProducto("KJ001");
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(5);
        MovimientoInventarioService service =
                new MovimientoInventarioService(
                        movimientoRepository,
                        productoRepository
                );
        service.guardar(movimiento);
        assertEquals(
                15,
                producto.getStock()
        );
    }
}