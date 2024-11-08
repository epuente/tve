package models;

import javax.persistence.Entity;

import play.db.ebean.Model;

/*
 * Catálogo de estado del material ingestado
 * por ejemplo: Aceptable, Fallas de orígen
 * */

@Entity
public class EstadoMaterial  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,EstadoMaterial> find = new Model.Finder<>(Long.class, EstadoMaterial.class);
}
