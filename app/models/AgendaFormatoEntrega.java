package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class AgendaFormatoEntrega extends models.utils.PlantillaModelo {
	@ManyToOne
	public Agenda agenda;
	
	@ManyToOne
	@NotNull
	public Formato formato;
}
