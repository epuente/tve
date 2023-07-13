package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class PreAgendaCancelacion  extends models.utils.PlantillaModelo{
	
	
	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	public Estado estadoAnterior;
	
	@ManyToOne
	public MotivoCancelacion motivo;
	
	@Column(length=600)
	public String observacion;	


	
	public static Model.Finder<Long, PreAgendaCancelacion> find = new Model.Finder<Long, PreAgendaCancelacion>(Long.class, PreAgendaCancelacion.class);

}
