package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.data.validation.Constraints.Email;
import play.db.ebean.Model;
@Entity
public class PersonalCorreo  extends models.utils.PlantillaModelo{

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	@ManyToOne
	@NotNull
	public Personal personal;
	
	@Email
	@NotNull	
	public String email;
	

	
	public static Model.Finder<Long,PersonalCorreo> find = new Model.Finder<Long,PersonalCorreo>(Long.class, PersonalCorreo.class);

	
}
