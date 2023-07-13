package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class Formato  extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Formato> find = new Model.Finder<Long,Formato>(Long.class, Formato.class);
}
