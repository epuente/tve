package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class OficioContactoTelefono  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	public OficioContacto oficioContactos;
	
	@Column(length=25)
	public String telefono;
	

	
	public static Model.Finder<Long,OficioContactoTelefono> find = new Model.Finder<Long,OficioContactoTelefono>(Long.class, OficioContactoTelefono.class);		
	
}
