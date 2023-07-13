package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class PersonalHorario extends models.utils.PlantillaModelo {


	
	@ManyToOne
	@NotNull
	public Personal personal;
	
	@NotNull
	public int diasemana;
	
	@Formats.DateTime(pattern="HH:mm")
	public Date desde;
	
	@Formats.DateTime(pattern="HH:mm")
	public Date hasta;

	public static Model.Finder<Long,PersonalHorario> find = new Model.Finder<Long,PersonalHorario>(Long.class, PersonalHorario.class);


}
