package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class PreAgendaEquipo  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	@NotNull
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Equipo equipo;

	@ManyToOne
	public Estado estado;
	

	
	public static Model.Finder<Long,PreAgendaEquipo> find = new Model.Finder<Long,PreAgendaEquipo>(Long.class, PreAgendaEquipo.class);


}
