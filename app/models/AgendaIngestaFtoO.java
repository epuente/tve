package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class AgendaIngestaFtoO extends models.utils.PlantillaModelo {
	@ManyToOne
	@NotNull
	public AgendaIngesta agendaingesta;
	
	@NotNull
	public String descripcion;
	
	public int cantidadtarjetas;
}
