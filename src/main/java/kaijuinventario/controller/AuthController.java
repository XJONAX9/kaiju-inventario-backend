package kaijuinventario.controller;

import kaijuinventario.dto.KaijuDtos.LoginRequest;
import kaijuinventario.dto.KaijuDtos.LoginResponse;
import kaijuinventario.dto.KaijuDtos.RegistroRequest;
import kaijuinventario.dto.KaijuDtos.UsuarioResponse;
import kaijuinventario.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/api/v1/kaiju/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }
    @GetMapping("/usuarios")
    public List<UsuarioResponse> listar() {
        return service.listar();
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
}
