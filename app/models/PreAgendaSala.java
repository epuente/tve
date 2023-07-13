package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class PreAgendaSala  extends models.utils.PlantillaModelo{

	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Sala sala;
	

	@OneToMany(mappedBy="preagendasala", cascade = CascadeType.ALL)
	public List<PreAgendaSalaUsoCabina> usoscabina;
	
	public static Model.Finder<Long,PreAgendaSala> find = new Model.Finder<Long,PreAgendaSala>(Long.class, PreAgendaSala.class);
}
