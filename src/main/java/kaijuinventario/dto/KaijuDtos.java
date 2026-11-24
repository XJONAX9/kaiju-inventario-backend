package kaijuinventario.dto;

public class KaijuDtos {

    public record RegistroRequest(
            String nombreCompleto,
            String rut,
            String email,
            String password,
            String rol
    ) {}

    public record LoginRequest(
            String email,
            String password
    ) {}

    public record UsuarioResponse(
            Long id,
            String rut,
            String nombreCompleto,
            String email,
            String rol
    ) {}

    public record LoginResponse(
            String accessToken,
            String tokenType,
            long expiresIn,
            UsuarioResponse usuario
    ) {}
}
