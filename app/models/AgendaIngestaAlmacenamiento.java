package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaIngestaAlmacenamiento extends models.utils.PlantillaModelo {
	@ManyToOne
	public AgendaIngesta agendaingesta;
	
	@ManyToOne
	public MedioAlmacenamiento medioalmacenamiento;
}
