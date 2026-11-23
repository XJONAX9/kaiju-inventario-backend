package kaijuinventario.controller;

import kaijuinventario.entity.MovimientoInventario;
import kaijuinventario.service.MovimientoInventarioService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;

    public MovimientoInventarioController(
            MovimientoInventarioService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<MovimientoInventario> listar() {
        return service.listar();
    }

    @PostMapping
    public Object guardar(
        @RequestBody MovimientoInventario movimiento
    ) {
        try {
            return service.guardar(movimiento);
        } catch (RuntimeException e) {
            return java.util.Map.of(
                    "mensaje",
                    e.getMessage()
            );
        }
    }
}