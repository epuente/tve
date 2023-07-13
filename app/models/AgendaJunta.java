package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaJunta extends models.utils.PlantillaModelo {
	@ManyToOne
	@Column(unique = true)
	public Agenda agenda;
}
