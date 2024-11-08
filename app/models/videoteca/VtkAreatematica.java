package models.videoteca;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkAreatematica extends models.utils.PlantillaModelo {
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public Areatematica areatematica;
}
