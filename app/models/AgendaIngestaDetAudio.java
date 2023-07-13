package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaIngestaDetAudio extends models.utils.PlantillaModelo {
	@ManyToOne
	public AgendaIngesta agendaingesta;
	
	@Column(length = 300)
	public String detalle;
}
