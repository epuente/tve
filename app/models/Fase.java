package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class Fase  extends models.utils.PlantillaModelo{
	@Column(length=50)
	public String descripcion;
    public static Model.Finder<Long,Fase> find = new Model.Finder<Long,Fase>(Long.class, Fase.class);
}
