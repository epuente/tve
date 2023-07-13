package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class PreAgendaFormatoEntrega  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Formato formato;
	
	@NotNull
	public int cantidad;
	

}
