package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VideoPersonaje extends models.utils.PlantillaModelo{

    @ManyToOne
    public VtkCatalogo catalogo;

    @Column(length = 50)
    public String paterno;

    @Column(length = 50)
    public String materno;

    @Column(length = 50)
    public String nombre;


}
