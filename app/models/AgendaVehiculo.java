package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class AgendaVehiculo  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	

	

	@ManyToOne
	/*@Column(unique = true)*/
	public Agenda agenda;
	
	@ManyToOne
	@NotNull
	public Vehiculo vehiculo;



	
	@Version
	public Date version = new Date();
	
	public static Model.Finder<Long, AgendaVehiculo> find = new Model.Finder<Long, AgendaVehiculo>(Long.class, AgendaVehiculo.class);
	
	
	
	
}
