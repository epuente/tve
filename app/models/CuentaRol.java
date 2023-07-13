package models;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class CuentaRol  extends models.utils.PlantillaModelo{
	@ManyToOne
	public Cuenta cuenta;
	
	@ManyToOne
	public Rol rol;
	
    public static Model.Finder<Long,CuentaRol> find = new Model.Finder<Long,CuentaRol>(Long.class, CuentaRol.class);

}
