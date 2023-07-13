package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaIngestaFto extends models.utils.PlantillaModelo {
	@ManyToOne
	public AgendaIngesta agendaingesta;
	
	@ManyToOne
	public FormatoIngesta formatoingesta;

	public int cantidadtarjetas;
}
