package models;

import classes.ColorConsola;
import classes.Notificaciones.Notificacion;
import controllers.PersonalController;
import controllers.UsuarioController;
import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Folio  extends models.utils.PlantillaModelo{

	@Column(unique=true)
	public Long numfolio;

	@ManyToOne
	public Oficio oficio;

	@ManyToOne
	public Estado estado;

	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date fechaentrega;

	@Column(length = 20, unique = true)
	public String numeroresguardo;

	@Column(length=600)
	public String observacion;


	@OneToMany(mappedBy="folio", cascade = CascadeType.ALL)
	public List<FolioProductorAsignado> productoresAsignados;

	@OneToOne
	public FolioCancelacion foliocancelacion;

    public static Model.Finder<Long,Folio> find = new Model.Finder<Long,Folio>(Long.class, Folio.class);

    public List<String> listaProductoresNombres() {
		List<String> nombres = new ArrayList<String>();
		for (FolioProductorAsignado fpa: this.productoresAsignados) {
			nombres.add(fpa.personal.nombreCompleto());
		}
		return nombres;
    }

	// Cada que se actualiza el folio, verificar si todos los estados de sus preagendas y agendas
	public void actualizaEstados(){
		List<Long> estados = new ArrayList<>();

		for (FolioProductorAsignado fpa :  this.productoresAsignados){
			for ( PreAgenda preagenda : fpa.preagendas  ){
				estados.add( preagenda.estado.id );
			}
			for ( Agenda agenda : fpa.agenda  ){
				estados.add( agenda.estado.id );
			}
		}


		Logger.debug("Estados del folio:");
		Logger.debug(Arrays.toString(estados.toArray()));

		List<Long> distintos = estados.stream().distinct().collect(Collectors.toList());

		Logger.debug("Distinct de estados del folio:");
		Logger.debug(Arrays.toString(distintos.toArray()));

		//Todos los estados son el mismo
		if (distintos.size()==1){
			this.estado.id = distintos.get(0);
			if ( this.estado.id != distintos.get(0) && distintos.get(0) != 99)
				this.update();
		}

	}

	public boolean fechaCaducada(){
		return ( new Date().after(this.fechaentrega)  );
	}
    /*
	@PostPersist
	@PostUpdate
	@PostRemove
	public void callbacksPost(){
		System.out.println(ColorConsola.ANSI_PURPLE+"\n\n\n\n\n\n\n desde callbacksPost en modeld.FolioProductorAsignado"+ColorConsola.ANSI_RESET);
		Calendar c = Calendar.getInstance();
		c.setTime( new Date()  );
		c.add(Calendar.HOUR,-1);
		Notificacion noti = Notificacion.getInstance();
		noti.recargar();
	}
    */

	@PostPersist
	@PostUpdate
	public void callbackPost(){
		HisFolio hf = new HisFolio();
		hf.folio = this;
		hf.estado = this.estado;
		Personal x = PersonalController.buscar(Long.parseLong(play.mvc.Controller.session("usuario")));
		hf.usuario = x;
		hf.rol = Rol.find.byId(  Long.parseLong( play.mvc.Controller.session("rolActual")  ));
		hf.save();
	}
}
