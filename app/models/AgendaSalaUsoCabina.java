package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AgendaSalaUsoCabina  extends models.utils.PlantillaModelo{

	@ManyToOne
	public AgendaSala agendasala;
	
	@Column(length=1)
	public String usocabina;


}
