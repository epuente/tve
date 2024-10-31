package models.videoteca;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkEvidencia extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    public String nombrearchivo;

    public String ligadescarga;

    public String ligaborrado;

    public static Model.Finder<Long,VtkEvidencia> find = new Model.Finder<Long,VtkEvidencia>(Long.class, VtkEvidencia.class);


}
