package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class AgendaIngestaObservacion extends models.utils.PlantillaModelo {
	@ManyToOne
	public AgendaIngesta agendaingesta;
	
	@Column (length = 500)
	public String observacion;
	
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaAlmacenamiento> almacenamientos;
}
