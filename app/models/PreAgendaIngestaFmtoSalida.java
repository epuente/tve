package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PreAgendaIngestaFmtoSalida extends models.utils.PlantillaModelo {

	
	@ManyToOne
	public PreAgendaIngesta preagendaingesta;
	
	@ManyToOne
	public FormatoIngesta formatoingesta;
	

}
