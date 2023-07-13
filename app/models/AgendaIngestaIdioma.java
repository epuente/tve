package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class AgendaIngestaIdioma extends models.utils.PlantillaModelo {
	@ManyToOne
	@NotNull
	public AgendaIngesta agendaingesta;
	
	@ManyToOne
	@NotNull
	public Idioma idioma;
}
