package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PreAgendaSalaUsoCabina  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	public PreAgendaSala preagendasala;
	
	
	@Column(length=1)
	public String usocabina;


}
