package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
public class PersonalAvatar  extends models.utils.PlantillaArchivo{
	@OneToOne
	public Personal personal;
}
