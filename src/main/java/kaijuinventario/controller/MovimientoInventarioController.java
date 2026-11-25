package kaijuinventario.controller;

import kaijuinventario.entity.MovimientoInventario;
import kaijuinventario.entity.UsuarioKaiju;
import kaijuinventario.security.Acceso;
import kaijuinventario.service.MovimientoInventarioService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoInventarioController {
    private final MovimientoInventarioService service;
    private final Acceso acceso;

    public MovimientoInventarioController(
            MovimientoInventarioService service,
            Acceso acceso
    ) {
        this.service = service;
        this.acceso = acceso;
    }

    @GetMapping
    public List<MovimientoInventario> listar(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        acceso.requerirRol(authorization, "ADMIN", "BODEGA");
        return service.listar();
    }

    @PostMapping
    public Object guardar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody MovimientoInventario movimiento
    ) {
        UsuarioKaiju usuario = acceso.requerirRol(authorization, "ADMIN", "BODEGA");

        movimiento.setId(null);
        movimiento.setRegistradoPor(usuario.getNombreCompleto());

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
