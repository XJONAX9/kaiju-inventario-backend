package kaijuinventario.security;

import kaijuinventario.entity.UsuarioKaiju;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Component
public class JwtService {

    private final byte[] secret;
    private final long expiresInSegundos;

    public JwtService(
            @Value("${kaiju.jwt.secret}") String secret,
            @Value("${kaiju.jwt.expires-in:3600}") long expiresInSegundos
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expiresInSegundos = expiresInSegundos;
    }

    public long getExpiresIn() {
        return expiresInSegundos;
    }

    public String generarToken(UsuarioKaiju usuario) {
        long ahora = System.currentTimeMillis() / 1000;
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + usuario.getId() + "\""
                + ",\"rol\":\"" + usuario.getRol() + "\""
                + ",\"iat\":" + ahora
                + ",\"exp\":" + (ahora + expiresInSegundos) + "}";

        String contenido = base64Url(header.getBytes(StandardCharsets.UTF_8))
                + "." + base64Url(payload.getBytes(StandardCharsets.UTF_8));
        return contenido + "." + base64Url(firmar(contenido));
    }

    private byte[] firmar(String contenido) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }

    private static String base64Url(byte[] datos) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(datos);
    }
}
