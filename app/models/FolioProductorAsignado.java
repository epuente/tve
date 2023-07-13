package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import classes.ColorConsola;
import classes.Notificaciones.Notificacion;
import play.db.ebean.Model;

import static play.mvc.Controller.session;

@Entity
public class FolioProductorAsignado  extends models.utils.PlantillaModelo{
	
	/**
	 * Los folios asignados al productor
	 */

	@ManyToOne
	@NotNull	
	public Folio folio;
	
	@ManyToOne
	@NotNull
	public Personal personal;
	

	@OneToMany(mappedBy="folioproductorasignado", cascade = CascadeType.ALL)
	public List<Agenda> agenda;
	
	@OneToMany(mappedBy="folioproductorasignado", cascade = CascadeType.ALL)
	public List<PreAgenda> preagendas;


    public static Model.Finder<Long,FolioProductorAsignado> find = new Model.Finder<Long,FolioProductorAsignado>(Long.class, FolioProductorAsignado.class);

    
    public boolean tieneEstado(Long estado){
    	boolean encontrado = false;
    	if (estado <= 4 ){
    		encontrado |= this.folio.estado.id == estado; 
        }    		
    	
    	for(PreAgenda ad : this.preagendas){
    		encontrado |= ad.estado.id == estado;
    	}
    	
    	for(Agenda ad : this.agenda){
    		encontrado |= ad.estado.id == estado;
    	}    	
		return encontrado;
    }    
    
    
    public boolean estaTerminado(Long estado){
    	boolean encontrado = false;
    	for(Agenda ad : this.agenda){
    		encontrado |= (ad.estado.id >=11 && ad.estado.id < 99);
    	}    	
		return encontrado;
    }    
    
    public List<String> listaEstadosServicios(){
    	List<String> lista = new ArrayList<String>();
		for ( PreAgenda pags : this.preagendas) {
			lista.add(pags.estado.descripcion);
		}
    	for (Agenda ags : this.agenda) {
    		lista.add(ags.estado.descripcion);
    	}
    	return lista.stream().distinct().collect(Collectors.toList());
    }
    
    /*
    public boolean preAgendaServicioTieneEstado(Long estado){
    	boolean encontrado = false;
    	for(PreAgenda ad : this.preagendas){
    		encontrado |= ad.estado.id == estado;
    	}
		return encontrado;
    }    
    
    public boolean agendaServicioTieneEstado(Long estado){
    	boolean encontrado = false;
    	for(Agenda ad : this.agenda){
    		encontrado |= ad.estado.id == estado;
    	}
		return encontrado;
    }
     */



	/*
	@PostPersist
	@PostUpdate
	@PostRemove
	public void callbacksPost(){
		System.out.println(ColorConsola.ANSI_PURPLE+"\n\n\n\n\n\n\n desde callbacksPost en modeld.FolioProductorAsignado"+ColorConsola.ANSI_RESET);
		Calendar c = Calendar.getInstance();
		c.setTime( new Date()  );
		c.add(Calendar.HOUR,-1);
		if (session("rolActual").compareTo("1")==0) {
			System.out.println(ColorConsola.ANSI_PURPLE+"rol 1 -"+ColorConsola.ANSI_RESET);
			Notificacion noti = Notificacion.getInstance();
			noti.recargar();
		}
	}
	*/
}
