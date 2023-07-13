package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class PreAgendaIngAdmonEqpo  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	@NotNull
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Personal ingeniero;
	

	
	public static Finder<Long, PreAgendaIngAdmonEqpo> find = new Finder<Long, PreAgendaIngAdmonEqpo>(Long.class, PreAgendaIngAdmonEqpo.class);
}
