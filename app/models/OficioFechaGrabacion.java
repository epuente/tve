package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.data.format.Formats;

@Entity
public class OficioFechaGrabacion extends models.utils.PlantillaModelo {
	
	/**
	 * 
	 */
	
	
	@ManyToOne
	public Oficio oficio;
	
	@NotNull	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")	
	public Date inicio;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date fin;	
	

	

}
