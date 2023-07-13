package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PreAgendaJunta  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	public PreAgenda preagenda;
	
	


}
