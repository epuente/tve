package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import play.db.ebean.Model;

@Entity

public class AgendaIngesta extends models.utils.PlantillaModelo {
	@ManyToOne
	@Column(unique = true)
	public Agenda agenda;
	
	@ManyToOne
	public EstadoMaterial estado;
	
	public boolean calificadoPrevio;
	public boolean cambioFormato;
	// Cuando se indica cambio de formato, se muestra la lista de formatos
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaFtoS> formatosalida;
	
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaFto> solicitudesIngestaFormato;
	
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaFtoO> solicitudesIngestaFormatoOtro;

	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaIdioma> idiomas;
	
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaIdiomaOtro> idiomasOtro;
	
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaDetVideo> detallesVideo;

	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaDetAudio> detallesAudio;

	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaProblema> problemasIngesta;
	
	public boolean permanenciaProductor;
	
	public boolean concluido;

	// ¿Cual de los operadores registró esta información?
	@ManyToOne
	@NotNull
	public Personal autorizo;

	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaAlmacenamiento> almacenamientos;
	
	@OneToMany (mappedBy = "agendaingesta", cascade = CascadeType.ALL)
	public List<AgendaIngestaObservacion> observaciones;
	
	public static Model.Finder<Long, AgendaIngesta> find = new Model.Finder<Long, AgendaIngesta>(Long.class, AgendaIngesta.class);

}
