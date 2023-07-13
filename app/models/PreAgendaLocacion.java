package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PreAgendaLocacion  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	public PreAgenda preagenda;
	
	public String locacion;
	


}
