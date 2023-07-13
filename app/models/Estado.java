package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class Estado  extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Estado> find = new Model.Finder<Long,Estado>(Long.class, Estado.class);
}
