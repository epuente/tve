package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;

@Entity
public class AgendaCuentaRol extends models.utils.PlantillaModelo {
	@OneToOne
	@NotNull
	public Agenda agenda;
	/*
	@ManyToOne
	public Personal personal;
	*/
	@ManyToOne
	public CuentaRol cuentarol;

	public static Model.Finder<Long, AgendaCuentaRol> find = new Model.Finder<Long, AgendaCuentaRol>(Long.class, AgendaCuentaRol.class);
}
