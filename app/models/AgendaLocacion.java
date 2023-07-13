package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class AgendaLocacion extends models.utils.PlantillaModelo {
	@ManyToOne
	public Agenda agenda;
	public String locacion;
	public static Model.Finder<Long, AgendaLocacion> find = new Model.Finder<Long, AgendaLocacion>(Long.class, AgendaLocacion.class);
}
