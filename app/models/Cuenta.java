package models;

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class Cuenta  extends models.utils.PlantillaModelo{


	@ManyToOne
	public Personal personal;
	
	@NotNull
	@Column(length=15)
	public String username;
	
	@NotNull
	@Column(length=15)
	public String password;
	
	
	@OneToMany(mappedBy="cuenta", cascade = CascadeType.ALL )
	public List<CuentaRol> roles;



    public static Model.Finder<Long,Cuenta> find = new Model.Finder<Long,Cuenta>(Long.class, Cuenta.class);
    
	public static Personal autenticar(String usuario, String password) {
		Cuenta c = Cuenta.find.where()
				.ieq("username", usuario).ieq("password",password)
				.eq("personal.activo", "S")
				//.order("field(cuentarol.rol_id, 127, 100,15)")

				.findUnique();
		return (c==null)? null: c.personal;
    }    

}
