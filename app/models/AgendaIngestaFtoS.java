package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaIngestaFtoS extends models.utils.PlantillaModelo {
	@ManyToOne
	public AgendaIngesta agendaingesta;
	
	@ManyToOne
	public FormatoIngesta formatoingesta;
}
