package kaijuinventario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracion_kaiju")
public class ConfiguracionKaiju {
    public static final Long ID_UNICO = 1L;

    @Id
    private Long id = ID_UNICO;

    @Column(nullable = false)
    private boolean modoEspecial = false;

    public ConfiguracionKaiju() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isModoEspecial() { return modoEspecial; }
    public void setModoEspecial(boolean modoEspecial) { this.modoEspecial = modoEspecial; }
}
