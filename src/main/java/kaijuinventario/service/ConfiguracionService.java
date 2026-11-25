package kaijuinventario.service;

import kaijuinventario.dto.KaijuDtos.ConfiguracionRequest;
import kaijuinventario.dto.KaijuDtos.ConfiguracionResponse;
import kaijuinventario.entity.ConfiguracionKaiju;
import kaijuinventario.exception.AuthException;
import kaijuinventario.repository.ConfiguracionKaijuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracionService {
    private final ConfiguracionKaijuRepository repository;

    public ConfiguracionService(ConfiguracionKaijuRepository repository) {
        this.repository = repository;
    }

    public ConfiguracionResponse obtener() {
        return new ConfiguracionResponse(cargar().isModoEspecial());
    }

    public ConfiguracionResponse actualizar(ConfiguracionRequest req) {
        if (req == null || req.modoEspecial() == null) {
            throw new AuthException(HttpStatus.BAD_REQUEST, "Falta el campo modoEspecial (true o false)");
        }
        ConfiguracionKaiju config = cargar();
        config.setModoEspecial(req.modoEspecial());
        return new ConfiguracionResponse(repository.save(config).isModoEspecial());
    }

    private ConfiguracionKaiju cargar() {
        return repository.findById(ConfiguracionKaiju.ID_UNICO)
                .orElseGet(() -> repository.save(new ConfiguracionKaiju()));
    }
}
