package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class AgendaEquipo extends models.utils.PlantillaModelo {
	@ManyToOne
	public Agenda agenda;
	
	@ManyToOne
	@NotNull
	public Equipo equipo;

	// ¿Cual de los ingenierios autorizó la asignación de equipo?
	@ManyToOne
	public Personal autorizo;

	public static Model.Finder<Long, AgendaEquipo> find = new Model.Finder<Long, AgendaEquipo>(Long.class, AgendaEquipo.class);
}
