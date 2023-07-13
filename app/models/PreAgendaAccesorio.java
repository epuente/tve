package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class PreAgendaAccesorio  extends models.utils.PlantillaModelo{

	@ManyToOne
	@NotNull
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Accesorio accesorio;

	@ManyToOne
	public Estado estado;

	public static Model.Finder<Long,PreAgendaAccesorio> find = new Model.Finder<Long,PreAgendaAccesorio>(Long.class, PreAgendaAccesorio.class);
}
