package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;

@Entity
public class AgendaSalida  extends models.utils.PlantillaModelo{

	@ManyToOne
	@Column(unique = true)
	public Agenda agenda;
	
	@NotNull
	public Date salida;

	public static Model.Finder<Long, AgendaSalida> find = new Model.Finder<Long, AgendaSalida>(Long.class, AgendaSalida.class);
}
