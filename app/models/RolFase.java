package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class RolFase  extends models.utils.PlantillaModelo{



	@ManyToOne
	public Fase fase;
	
	@ManyToOne
	public Rol rol;
	

	
	public static Model.Finder<Long,RolFase> find = new Model.Finder<Long,RolFase>(Long.class, RolFase.class);		
	
}
