package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VideoPersonaje extends models.utils.PlantillaModelo{

    @ManyToOne
    public VtkCatalogo catalogo;


    @Column(length = 200)
    public String nombre;

    public static Model.Finder<Long,VideoPersonaje> find = new Model.Finder<Long,VideoPersonaje>(Long.class, VideoPersonaje.class);


}
