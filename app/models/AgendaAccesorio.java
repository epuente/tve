package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class AgendaAccesorio extends models.utils.PlantillaModelo {
	@ManyToOne
	public Agenda agenda;
	
	@ManyToOne
	@NotNull
	public Accesorio accesorio;

	// ¿Cual de los ingenierios autorizó la asignación de accesorios?
	@ManyToOne
	public Personal autorizo;

	public static Model.Finder<Long, AgendaAccesorio> find = new Model.Finder<Long, AgendaAccesorio>(Long.class, AgendaAccesorio.class);
}
