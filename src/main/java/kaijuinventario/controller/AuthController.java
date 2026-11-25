package kaijuinventario.controller;

import kaijuinventario.dto.KaijuDtos.LoginRequest;
import kaijuinventario.dto.KaijuDtos.LoginResponse;
import kaijuinventario.dto.KaijuDtos.RegistroRequest;
import kaijuinventario.dto.KaijuDtos.UsuarioResponse;
import kaijuinventario.security.Acceso;
import kaijuinventario.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kaiju/auth")
public class AuthController {
    private final AuthService service;
    private final Acceso acceso;

    public AuthController(AuthService service, Acceso acceso) {
        this.service = service;
        this.acceso = acceso;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody RegistroRequest request) {
        return service.registrar(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/usuarios")
    public List<UsuarioResponse> listar(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        acceso.requerirRol(authorization, "ADMIN");
        return service.listar();
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        acceso.requerirRol(authorization, "ADMIN");
        service.eliminar(id);
    }
}
