package models;

import play.db.ebean.Model;

import javax.persistence.Entity;

@Entity
public class TipoCredito extends models.utils.PlantillaCatalogo{
    public String accion;

    public static Model.Finder<Long,TipoCredito> find = new Model.Finder<Long,TipoCredito>(Long.class, TipoCredito.class);

}
