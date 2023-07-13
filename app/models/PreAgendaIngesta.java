package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class PreAgendaIngesta  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	public EstadoMaterial estado;
	
	public Boolean calificadoPrevio = false;
	public Boolean cambioFormato = false;
	
	
	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaFmtoSalida> formatossalida;
	
	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaAlmacenamiento> almacenamientos;
	
	public String datosVideo;
	public String datosAudio;
	
	
	
	
//	@OneToOne(mappedBy = "preagendaingesta")
//	public PreAgendaIngestaDetalle detalle;
	
/*	
	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaFormatoSalida> formatossalida;

	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaFormato> solicitudesIngestaFormato;

	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaFormatoOtro> solicitudesIngestaFormatoOtro;	


	
	@OneToMany(mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaDetalle> detalle;
	

	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaIdioma> idiomas;
	
	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaIdiomaOtro> idiomasOtro;

	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaDetalleVideo> detallesVideo;

	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngestaDetalleAudio> detallesAudio;
*/
/*	
	public Boolean problemaIngesta = true;
	
	public Boolean permanenciaProductor = true;

	public Boolean concluido = true;

	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
	public List<PreAgendaIngProblema> problemasIngesta;

*/
	
//	@OneToMany (mappedBy = "preagendaingesta", cascade = CascadeType.ALL)
//	public List<PreAgendaIngestaObservacion> observaciones;

	


}
