package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import play.db.ebean.Model;
@Entity
public class PreAgenda  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	
	
	@ManyToOne 
	public FolioProductorAsignado folioproductorasignado;
	
	@ManyToOne
	@NotNull
	public Fase fase;
	
	@NotNull
	public Date inicio;
	
	@NotNull
	public Date fin;
	
	@ManyToOne
	@NotNull
	public Estado estado;
	
	@Column(length=600)
	public String observacion;
	

	
    @Transient
    public String tipo = "preagenda";	
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaRol> personal;

	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaOperadorSala> operadoresSala;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaLocutor> locutores;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaAccesorio> accesorios;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaEquipo> equipos;

	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaIngAdmonEqpo> ingsAdmonEqpo;

	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaLocacion> locaciones;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaSala> salas;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaSalida> salidas;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaFormatoEntrega> formatos;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaVehiculo> vehiculos;
	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaJunta> juntas;

	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaCancelacion> cancelaciones;

	
	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
	public List<PreAgendaIngesta> ingestas;
	/*
	@OneToOne(mappedBy="preagenda", cascade=CascadeType.ALL)
	public PreAgendaDigitalizacion digitalizacion;	
	
	*/

//	@OneToMany(mappedBy="preagenda", cascade=CascadeType.ALL)
//	public List<PreAgendaIngesta2> ingestas2;		
	
	public static Model.Finder<Long,PreAgenda> find = new Model.Finder<Long,PreAgenda>(Long.class, PreAgenda.class);
		
	
}
