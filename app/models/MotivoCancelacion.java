package models;

import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class MotivoCancelacion  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,MotivoCancelacion> find = new Model.Finder<Long,MotivoCancelacion>(Long.class, MotivoCancelacion.class);
}
