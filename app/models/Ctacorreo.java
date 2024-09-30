package models;

import classes.ColorConsola;
import classes.Notificaciones.AuxNotificacion;
import classes.Notificaciones.Notificacion;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static play.mvc.Controller.session;


@Entity
public class Ctacorreo  extends models.utils.PlantillaModelo  {

	@NotNull
	public String hostname;

	@NotNull
	public String puerto;
	public String cuenta;
	public String contrasenia;
	public boolean activa;
	public static Model.Finder<Long,Ctacorreo> find = new Finder<Long,Ctacorreo>(Long.class, Ctacorreo.class);



/*
	@PostPersist
	@PostUpdate
	@PostRemove
	public void callbacks(){
		System.out.println(ColorConsola.ANSI_PURPLE+"\n\n\n\n\n\n\n desde callbacksPost en models.CtaCorreo"+ColorConsola.ANSI_RESET);
		Notificacion noti = Notificacion.getInstance();
		noti.recargar();
		System.out.println(ColorConsola.ANSI_PURPLE+"    "+this.id +"  activa "+this.activa+ColorConsola.ANSI_RESET);
		System.out.println(ColorConsola.ANSI_PURPLE+"FIN CTACORREO callbacksPost\n\n"+ColorConsola.ANSI_RESET);

	}
*/
	/*
	AQUI ME QUEDÉ:    Probar funcionamiento de otro()
	https://refactorizando.com/uso-de-completablefuture-en-java/
	public void otro(){
		CompletableFuture<Void> myFuture = CompletableFuture.runAsync(() -> {
			Notificacion noti = Notificacion.getInstance();
			noti.recargar();
		});
	}
	*/
    public static Page<Ctacorreo> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
		System.out.println(" * * * * * * * * filtr :"+filtro);
		System.out.println(" * * * * * * * * filtr tam :"+filtro.length());
		Page<Ctacorreo> p = Ebean.find(Ctacorreo.class)
				.where("  unaccent(lower(hostname)) like :cadena OR puerto like :cadena  OR  unaccent(lower(cuenta)) like :cadena ")
            .setParameter("cadena", "%"+filtro.toLowerCase()+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(true)
                .getPage(page);
		System.out.println(" * * * * * * * * registros:"+p.getList().size());
        return p;
    }

	// Pone todas las cuentas de correo registradas como NO ACTIVAS
	public void resetActiva() {
		//for( Ctacorreo cta: find.where().eq("activa", true).findList()) {
		for( Ctacorreo cta: Ebean.find(Ctacorreo.class).where().eq("activa", true).findList()) {
			cta.activa=false;
			cta.update();
		}
	}


	// Regresa true si una de las cuentas se encuenta activa
	public static boolean hayCtaActiva(){
		List<Ctacorreo> x = Ebean.find(Ctacorreo.class).where().eq("activa", true).findList();
		return x.size()>0;
	}



}
