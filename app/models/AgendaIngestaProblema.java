package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaIngestaProblema extends models.utils.PlantillaModelo {
	@ManyToOne
	public AgendaIngesta agendaingesta;
	
	@Column(length = 500)
	public String descripcion;
}
