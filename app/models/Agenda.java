package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import play.db.ebean.Model;
@Entity
public class Agenda extends models.utils.PlantillaModelo implements Comparable<Agenda>{
	@ManyToOne
	public FolioProductorAsignado folioproductorasignado;
	
	@ManyToOne
	@NotNull
	public Fase fase;
	
	@NotNull
	public Date inicio;
	
	public Date getInicio() {
		return inicio;
	}

	@NotNull
	public Date fin;
	
	@ManyToOne
	@NotNull
	public Estado estado;
	
	@Column(length=600)
	public String observacion;

	@Version
	public Date version = new Date();
	

	@Transient
    public String tipo = "agenda";	
    
	public static Model.Finder<Long, Agenda> find = new Model.Finder<Long, Agenda>(Long.class, Agenda.class);

	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaSalida> salidas;

	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaPrevioAutorizaEquipo> previoautorizaequipo;

	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaPrevioAutorizaAccesorio> previoautorizaaccesorios;

	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaAccesorio> accesorios;
	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaEquipo> equipos;

	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaIngAdmonEqpo> ingsAdmonEqpo;
	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaFormatoEntrega> formatos;
	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaLocacion> locaciones;
	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaSala> salas;

	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaVehiculo> vehiculos;

	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaJunta> juntas;
	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaCuentaRol> cuentaRol;
	
	@OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL)
	public List<AgendaOperadorSala> operadoresSala;
	
	@OneToMany(mappedBy="agenda", cascade=CascadeType.ALL)
	public List<AgendaCancelacion> cancelaciones;
	
	@OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL)
	public List<AgendaIngesta> ingestas;
	
	public int compareTo(Agenda u) {
		    if (getInicio() == null || u.getInicio() == null) {
		      return 0;
		    }
		    return getInicio().compareTo(u.getInicio());
		  }


}
