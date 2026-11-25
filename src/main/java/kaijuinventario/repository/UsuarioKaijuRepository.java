package kaijuinventario.repository;

import kaijuinventario.entity.UsuarioKaiju;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioKaijuRepository extends JpaRepository<UsuarioKaiju, Long> {
    Optional<UsuarioKaiju> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);

    boolean existsByRol(String rol);
}
