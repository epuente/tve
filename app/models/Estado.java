package models;

import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class Estado  extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Estado> find = new Model.Finder<>(Long.class, Estado.class);
}
