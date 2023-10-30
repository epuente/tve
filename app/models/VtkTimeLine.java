package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Duration;

@Entity
public class VtkTimeLine extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public VideoPersonaje personaje;

    @Column(length = 100)
    public String gradoacademico;

    @Column(length = 100)
    public String cargo;


    public Duration desde;
    public Duration hasta;

    @Column(length = 200)
    public String tema;
}
