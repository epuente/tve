package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class AgendaIngestaIdiomaOtro extends models.utils.PlantillaModelo {
	@ManyToOne
	@NotNull
	public AgendaIngesta agendaingesta;
	
	@Column(length = 100)
	@NotNull
	public String descripcion;
}
