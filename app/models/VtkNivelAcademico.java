package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkNivelAcademico extends models.utils.PlantillaModelo{

    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public NivelAcademico nivel;

}
