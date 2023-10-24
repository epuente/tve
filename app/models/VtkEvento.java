package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkEvento extends models.utils.PlantillaModelo{

    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public Servicio servicio;

}
