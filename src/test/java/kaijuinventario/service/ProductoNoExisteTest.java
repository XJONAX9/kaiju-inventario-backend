package kaijuinventario.service;

import kaijuinventario.entity.MovimientoInventario;
import kaijuinventario.repository.MovimientoInventarioRepository;
import kaijuinventario.repository.ProductoRepository;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ProductoNoExisteTest {

    @Test
    void debeRetornarNullCuandoProductoNoExiste() {

        ProductoRepository productoRepository =
                mock(ProductoRepository.class);

        MovimientoInventarioRepository movimientoRepository =
                mock(MovimientoInventarioRepository.class);

        when(productoRepository.findById("KJ999"))
                .thenReturn(Optional.empty());

        MovimientoInventario movimiento =
                new MovimientoInventario();

        movimiento.setCodigoProducto("KJ999");
        movimiento.setCantidad(5);
        movimiento.setTipoMovimiento("ENTRADA");

        MovimientoInventarioService service =
                new MovimientoInventarioService(
                        movimientoRepository,
                        productoRepository
                );

        assertNull(
                service.guardar(movimiento)
        );
    }
}