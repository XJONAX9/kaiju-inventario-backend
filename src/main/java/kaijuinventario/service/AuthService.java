package kaijuinventario.service;

import kaijuinventario.dto.KaijuDtos.LoginRequest;
import kaijuinventario.dto.KaijuDtos.LoginResponse;
import kaijuinventario.dto.KaijuDtos.RegistroRequest;
import kaijuinventario.dto.KaijuDtos.UsuarioResponse;
import kaijuinventario.entity.UsuarioKaiju;
import kaijuinventario.exception.AuthException;
import kaijuinventario.repository.UsuarioKaijuRepository;
import kaijuinventario.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final Set<String> ROLES = Set.of("BODEGA", "ADMIN");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioKaijuRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioKaijuRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioResponse registrar(RegistroRequest req) {
        if (req == null) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Datos inválidos");
        }

        String nombre = req.nombreCompleto() == null ? "" : req.nombreCompleto().trim();
        String email = req.email() == null ? "" : req.email().trim().toLowerCase();
        String rol = req.rol() == null ? "" : req.rol().trim().toUpperCase();
        String password = req.password() == null ? "" : req.password();

        if (nombre.isEmpty()) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        String rut = normalizarRut(req.rut());
        if (rut == null) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "RUT inválido");
        }
        if (!EMAIL.matcher(email).matches()) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Formato de email inválido");
        }
        if (password.length() < 6) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
        }
        if (rol.isEmpty()) {
            rol = "BODEGA";
        }
        if (!ROLES.contains(rol)) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Rol inválido (BODEGA o ADMIN)");
        }
        if (rol.equals("ADMIN") && repository.existsByRol("ADMIN")) {
            throw new AuthException(HttpStatus.CONFLICT, "Ya hay un administrador registrado");
        }

        if (repository.existsByRut(rut)) {
            throw new AuthException(HttpStatus.CONFLICT, "Ya existe un operador con ese RUT");
        }
        if (repository.existsByEmail(email)) {
            throw new AuthException(HttpStatus.CONFLICT, "Ya existe un operador con ese email");
        }

        UsuarioKaiju usuario = new UsuarioKaiju();
        usuario.setNombreCompleto(nombre);
        usuario.setRut(rut);
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRol(rol);

        return aRespuesta(repository.save(usuario));
    }

    public LoginResponse login(LoginRequest req) {
        String email = (req == null || req.email() == null) ? "" : req.email().trim().toLowerCase();
        String password = (req == null || req.password() == null) ? "" : req.password();

        UsuarioKaiju usuario = repository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()))
                .orElseThrow(() -> new AuthException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos"));

        return new LoginResponse(
                jwtService.generarToken(usuario),
                "Bearer",
                jwtService.getExpiresIn(),
                aRespuesta(usuario)
        );
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(this::aRespuesta).toList();
    }

    public void eliminar(Long id) {
        UsuarioKaiju usuario = repository.findById(id)
                .orElseThrow(() -> new AuthException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        repository.delete(usuario);
    }

    private UsuarioResponse aRespuesta(UsuarioKaiju u) {
        return new UsuarioResponse(u.getId(), u.getRut(), u.getNombreCompleto(), u.getEmail(), u.getRol());
    }

    static String normalizarRut(String rutOriginal) {
        if (rutOriginal == null) return null;
        String limpio = rutOriginal.replace(".", "").replace("-", "").trim().toUpperCase();
        if (limpio.length() < 8 || limpio.length() > 9) return null;

        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        if (!cuerpo.chars().allMatch(Character::isDigit)) return null;
        if (calcularDv(cuerpo) != dv) return null;

        return cuerpo + "-" + dv;
    }

    private static char calcularDv(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * multiplicador;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }
        int resultado = 11 - (suma % 11);
        if (resultado == 11) return '0';
        if (resultado == 10) return 'K';
        return (char) ('0' + resultado);
    }
}
