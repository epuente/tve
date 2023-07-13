package models;

import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class MedioAlmacenamiento  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,MedioAlmacenamiento> find = new Model.Finder<Long,MedioAlmacenamiento>(Long.class, MedioAlmacenamiento.class);
}
