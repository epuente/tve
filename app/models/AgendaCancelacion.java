package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class AgendaCancelacion extends models.utils.PlantillaModelo {
	@ManyToOne
	public Agenda agenda;
	
	@ManyToOne
	public Estado estadoAnterior;
	
	@ManyToOne
	public MotivoCancelacion motivo;
	
	@Column(length=600)
	public String observacion;	

	public static Model.Finder<Long, AgendaCancelacion> find = new Model.Finder<Long, AgendaCancelacion>(Long.class, AgendaCancelacion.class);
}
