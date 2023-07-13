package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
@Entity
public class OficioContacto  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	public Oficio oficio;	
	
	@Column(length=150)
	public String nombre;

	

	
	
	@OneToMany(mappedBy="oficioContactos", cascade=CascadeType.ALL)
	public List<OficioContactoTelefono> telefonos;
	
	@OneToMany(mappedBy="oficioContactos", cascade=CascadeType.ALL)
	public List<OficioContactoCorreo> correos;	
	
	
	public static Model.Finder<Long,OficioContacto> find = new Model.Finder<Long,OficioContacto>(Long.class, OficioContacto.class);	
}
