package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class PreAgendaVehiculo  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	public PreAgenda preagenda;
	
	
	public static Model.Finder<Long,PreAgendaVehiculo> find = new Model.Finder<Long,PreAgendaVehiculo>(Long.class, PreAgendaVehiculo.class);
}
