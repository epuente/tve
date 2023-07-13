package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class PersonalCambioHorario  extends models.utils.PlantillaModelo{


	
	@ManyToOne
	public Personal personal;
	
	@NotNull
	public Date fechadesde;
	@NotNull
	public Date fechahasta;
	
	@NotNull
	public Date horariodesde;
	@NotNull
	public Date horariohasta;
	
	@NotNull
	public Boolean excluyente;
	

	
	public static Model.Finder<Long,PersonalCambioHorario> find = new Model.Finder<Long,PersonalCambioHorario>(Long.class, PersonalCambioHorario.class);		

}
