package kaijuinventario.controller;

import kaijuinventario.entity.Producto;
import kaijuinventario.entity.UsuarioKaiju;
import kaijuinventario.exception.AuthException;
import kaijuinventario.security.Acceso;
import kaijuinventario.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService service;
    private final Acceso acceso;

    public ProductoController(ProductoService service, Acceso acceso) {
        this.service = service;
        this.acceso = acceso;
    }

    @GetMapping
    public List<Producto> listar(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        acceso.requerirRol(authorization, "ADMIN", "BODEGA");
        return service.listar();
    }

    @GetMapping("/{codigo}")
    public Producto buscarPorCodigo(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String codigo
    ) {
        acceso.requerirRol(authorization, "ADMIN", "BODEGA");
        return service.buscarPorCodigo(codigo);
    }

    @PostMapping
    public Producto guardar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Producto producto
    ) {
        UsuarioKaiju usuario = acceso.requerirRol(authorization, "ADMIN", "BODEGA");

        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "El código del producto es obligatorio");
        }
        if (service.buscarPorCodigo(producto.getCodigo()) != null) {
            throw new AuthException(HttpStatus.CONFLICT, "Ya existe un producto con ese código");
        }

        producto.setCreadoPor(usuario.getNombreCompleto());
        producto.setCreadoPorEmail(usuario.getEmail());
        return service.guardar(producto);
    }

    @PutMapping("/{codigo}")
    public Producto actualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String codigo,
            @RequestBody Producto productoActualizado
    ) {
        acceso.requerirRol(authorization, "ADMIN", "BODEGA");
        return service.actualizar(codigo, productoActualizado);
    }

    @DeleteMapping("/{codigo}")
    public void eliminar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable String codigo
    ) {
        acceso.requerirRol(authorization, "ADMIN", "BODEGA");
        service.eliminar(codigo);
    }
}
