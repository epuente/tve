package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class OficioProductorSolicitado  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	public Oficio oficio;	
	
	@ManyToOne
	public Personal personal;
	


	public static Model.Finder<Long,OficioProductorSolicitado> find = new Model.Finder<Long,OficioProductorSolicitado>(Long.class, OficioProductorSolicitado.class);	

}
