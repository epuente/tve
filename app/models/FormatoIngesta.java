package models;

import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class FormatoIngesta  extends models.utils.PlantillaCatalogo{
	/**
	 * Formato de tipo de cámaras:
	 * 	por ejemplo: XDCAM, DVCAM
	 */
	public static Model.Finder<Long,FormatoIngesta> find = new Model.Finder<>(Long.class, FormatoIngesta.class);
}
