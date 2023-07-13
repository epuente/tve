package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class RolDerecho  extends models.utils.PlantillaModelo{
	@ManyToOne
	@NotNull
	public Rol rol;
	
	@ManyToOne
	public Ambito ambito;
	
	public Boolean Lectura;
	public Boolean Escritura;
	public Boolean Ejecucion;

}
