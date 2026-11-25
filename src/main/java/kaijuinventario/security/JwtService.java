package kaijuinventario.security;

import kaijuinventario.entity.UsuarioKaiju;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtService {
    private static final Pattern SUB = Pattern.compile("\"sub\":\"(\\d+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

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

    public Long idDelToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String[] partes = authorization.substring(7).trim().split("\\.");
        if (partes.length != 3) {
            return null;
        }
        try {
            byte[] esperada = firmar(partes[0] + "." + partes[1]);
            byte[] recibida = Base64.getUrlDecoder().decode(partes[2]);
            if (!MessageDigest.isEqual(esperada, recibida)) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            Matcher exp = EXP.matcher(payload);
            Matcher sub = SUB.matcher(payload);
            if (!exp.find() || !sub.find()) {
                return null;
            }
            if (Long.parseLong(exp.group(1)) < System.currentTimeMillis() / 1000) {
                return null;
            }
            return Long.parseLong(sub.group(1));
        } catch (IllegalArgumentException e) {
            return null;
        }
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
