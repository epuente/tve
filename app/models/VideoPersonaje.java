package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VideoPersonaje extends models.utils.PlantillaModelo{

    @ManyToOne
    public VtkCatalogo catalogo;

    public String paterno;

    public String materno;

    public String nombre;


}
