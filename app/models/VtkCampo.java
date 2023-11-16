package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class VtkCampo extends models.utils.PlantillaModelo{
    public String label;
    public String nombre;

    @Column(length = 2000)
    public String indicaciones;

    public static Model.Finder<Long,VtkCampo> find = new Model.Finder<Long,VtkCampo>(Long.class, VtkCampo.class);

}
