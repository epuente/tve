package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class PalabraClave extends models.utils.PlantillaCatalogo {

    @NotNull
    @Column(length=200)
    public String descripcion;
    @ManyToOne
    VtkCatalogo catalogo;

}
