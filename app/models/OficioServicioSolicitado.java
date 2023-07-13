package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity 
public class OficioServicioSolicitado  extends models.utils.PlantillaModelo{
	@ManyToOne
	public Oficio oficio;
	
	@ManyToOne
	@NotNull
	public Servicio servicio;

	public static Model.Finder<Long,OficioServicioSolicitado> find = new Model.Finder<Long,OficioServicioSolicitado>(Long.class, OficioServicioSolicitado.class);	
}
