package models.videoteca;

import models.Nivel;
import models.videoteca.VtkCatalogo;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkNivel extends models.utils.PlantillaModelo{

    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public Nivel nivel;

    public static Model.Finder<Long,VtkNivel> find = new Model.Finder<Long,VtkNivel>(Long.class, VtkNivel.class);


}
