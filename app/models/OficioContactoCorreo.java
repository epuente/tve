package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Email;
import play.db.ebean.Model;
@Entity
public class OficioContactoCorreo  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	public OficioContacto oficioContactos;
	
	@Column(length=50)
	@Email
	public String correo;
	

	
	
	public static Model.Finder<Long,OficioContactoCorreo> find = new Model.Finder<Long,OficioContactoCorreo>(Long.class, OficioContactoCorreo.class);		

}
