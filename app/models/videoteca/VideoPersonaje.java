package models.videoteca;

import models.Personal;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VideoPersonaje extends models.utils.PlantillaModelo{
    /*
    @ManyToOne
    public VtkCatalogo catalogo;

    */


    @Column(length = 75)
    public String nombre;

    @ManyToOne (optional = false)
    public Personal catalogador;

    public static Model.Finder<Long,VideoPersonaje> find = new Model.Finder<Long,VideoPersonaje>(Long.class, VideoPersonaje.class);


}
