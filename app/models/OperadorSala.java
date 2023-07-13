package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;

@Entity
public class OperadorSala  extends models.utils.PlantillaModelo{
	@ManyToOne
	public Personal personal;
	
	@ManyToOne
	@NotNull
	public Sala sala;
	
	@Column(length=1)
	@NotNull	
	public String turno;
	
	@Version
	public java.util.Date version;


	public static Model.Finder<Long,OperadorSala> find = new Model.Finder<Long,OperadorSala>(Long.class, OperadorSala.class);

}
