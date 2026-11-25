package kaijuinventario.controller;

import kaijuinventario.dto.KaijuDtos.ConfiguracionRequest;
import kaijuinventario.dto.KaijuDtos.ConfiguracionResponse;
import kaijuinventario.security.Acceso;
import kaijuinventario.service.ConfiguracionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kaiju/configuracion")
public class ConfiguracionController {
    private final ConfiguracionService service;
    private final Acceso acceso;

    public ConfiguracionController(ConfiguracionService service, Acceso acceso) {
        this.service = service;
        this.acceso = acceso;
    }

    @GetMapping
    public ConfiguracionResponse obtener(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        acceso.requerirRol(authorization, "ADMIN", "BODEGA");
        return service.obtener();
    }

    @PutMapping
    public ConfiguracionResponse actualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ConfiguracionRequest request
    ) {
        acceso.requerirRol(authorization, "ADMIN");
        return service.actualizar(request);
    }
}
