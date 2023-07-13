package models;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class Idioma  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,Idioma> find = new Model.Finder<Long,Idioma>(Long.class, Idioma.class);
}
