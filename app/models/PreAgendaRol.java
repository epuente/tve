package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class PreAgendaRol  extends models.utils.PlantillaModelo{
	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Rol rol;
	
	@NotNull
	public int cantidad;
	

	
    public static Model.Finder<Long,PreAgenda> find = new Model.Finder<Long,PreAgenda>(Long.class, PreAgenda.class);


}
