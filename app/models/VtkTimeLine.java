package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Duration;

@Entity
public class VtkTimeLine extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public VideoPersonaje personaje;


    public String gradoacademico;

    public String cargo;


    public Duration desde;
    public Duration hasta;

    public String tema;
}
