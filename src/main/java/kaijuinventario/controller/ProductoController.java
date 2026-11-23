package kaijuinventario.controller;

import kaijuinventario.entity.Producto;
import kaijuinventario.service.ProductoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @GetMapping("/{codigo}")
    public Producto buscarPorCodigo(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo);
    }

    @PostMapping
    public Producto guardar(@RequestBody Producto producto) {
        return service.guardar(producto);
    }

    @PutMapping("/{codigo}")
    public Producto actualizar(
            @PathVariable String codigo,
            @RequestBody Producto productoActualizado
    ) {
        return service.actualizar(codigo, productoActualizado);
    }

    @DeleteMapping("/{codigo}")
    public void eliminar(@PathVariable String codigo) {
        service.eliminar(codigo);
    }
}