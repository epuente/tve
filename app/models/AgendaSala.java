package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;


@Entity
public class AgendaSala  extends models.utils.PlantillaModelo{
	@ManyToOne
	@Column(unique=true)
	public Agenda agenda;
	
	@ManyToOne
	@NotNull
	public Sala sala;


	@OneToMany(mappedBy="agendasala", cascade = CascadeType.ALL)
	public List<AgendaSalaUsoCabina> usoscabina;
	
	public static  Model.Finder<Long, AgendaSala> find = new Model.Finder<Long, AgendaSala>(Long.class, AgendaSala.class);


}
