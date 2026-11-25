package kaijuinventario.security;

import kaijuinventario.entity.UsuarioKaiju;
import kaijuinventario.exception.AuthException;
import kaijuinventario.repository.UsuarioKaijuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class Acceso {
    private final JwtService jwtService;
    private final UsuarioKaijuRepository repository;

    public Acceso(JwtService jwtService, UsuarioKaijuRepository repository) {
        this.jwtService = jwtService;
        this.repository = repository;
    }

    public Optional<UsuarioKaiju> usuarioOpcional(String authorization) {
        Long id = jwtService.idDelToken(authorization);
        return id == null ? Optional.empty() : repository.findById(id);
    }

    public UsuarioKaiju autenticar(String authorization) {
        return usuarioOpcional(authorization)
                .orElseThrow(() -> new AuthException(
                        HttpStatus.UNAUTHORIZED, "Sesión no válida. Inicia sesión nuevamente"));
    }

    public UsuarioKaiju requerirRol(String authorization, String... rolesPermitidos) {
        UsuarioKaiju usuario = autenticar(authorization);
        if (!Arrays.asList(rolesPermitidos).contains(usuario.getRol())) {
            throw new AuthException(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción");
        }
        return usuario;
    }
}
