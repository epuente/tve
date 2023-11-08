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

    @Column(length = 50)
    public String gradoacademico;

    @Column(length = 50)
    public String cargo;


    public Long desde;
    public Long hasta;

    @Column(length = 200)
    public String tema;
}
