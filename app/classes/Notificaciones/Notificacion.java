package classes.Notificaciones;

import classes.ColorConsola;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import models.*;
import play.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static play.mvc.Controller.session;

public class Notificacion {
    private static Notificacion INSTANCE = new Notificacion();

    // Constructor del singleton
    public Notificacion() {
        System.out.println(ColorConsola.ANSI_BLUE+"\n\n\n\nCONSTRUCTOR Notificacion\n\n\n"+ColorConsola.ANSI_RESET);
        this.datos = new ArrayList<>();
        //this.recargar();
    }

    public void recargar(){
        //CompletableFuture<Void> myFuture = CompletableFuture.runAsync(() -> {
            System.out.println(ColorConsola.ANSI_BLUE+"\n\n\n\n\n\n\n    Desde classes.Notificacion.recargar ASYNC"+ColorConsola.ANSI_RESET);
            //String elUsuario = session("usuario");
            //String elRolActual = session("rolActual");
            Notificacion noti = Notificacion.getInstance();
            Calendar c = Calendar.getInstance();
            c.setTime( new Date()  );
            c.add(Calendar.HOUR,-1);
            List<FolioProductorAsignado> fpas = FolioProductorAsignado.find.all();
            //System.out.println(ColorConsola.ANSI_BLUE+"elUsuario "+elUsuario+"    elRolActual:"+elRolActual+ColorConsola.ANSI_RESET+ColorConsola.ANSI_RESET);

            //if (elUsuario!=null && elRolActual!=null) {
                List<AuxNotificacion> nuevo = new ArrayList<>();
                List<FolioProductorAsignado> fpa2 = FolioProductorAsignado.find
                        .where()
                        .ge("folio.estado.id", 4L)
                        .lt("folio.estado.id", 99L)
                        .findList();

                System.out.println(ColorConsola.ANSI_BLUE+"    fpa2.size "+fpa2.size()+ColorConsola.ANSI_RESET);
                for ( FolioProductorAsignado fpa : fpa2) {
                    System.out.println(ColorConsola.ANSI_BLUE+"    fpa.folio.estado.id:"+fpa.folio.estado.id+ColorConsola.ANSI_RESET);
                    //System.out.println(ColorConsola.ANSI_BLUE+"    --- "+(Arrays.asList(new long[]{1, 100}).contains(elRolActual))+ColorConsola.ANSI_RESET);

                    List<Long> soloPre = fpa.preagendas.stream()
                            .filter(f->f.estado.id==5).map(m->m.id)
                            .collect(Collectors.toList());

                    List<Long> soloAgeAut = fpa.agenda.stream()
                            .filter(f->f.estado.id==7).map(m->m.id)
                            .collect(Collectors.toList());
                    List<Long> soloAgeEAAut = fpa.agenda.stream()
                            .filter(f->f.estado.id==8).map(m->m.id)
                            .collect(Collectors.toList());


                    if (fpa.folio.estado.id == 4) {
                        AuxNotificacion apa = new AuxNotificacion();
                        apa.fecha = fpa.folio.auditUpdate;
                        apa.roles = new ArrayList<>();
                        apa.liga="";
                        apa.personalId = fpa.personal.id;
                        apa.tipoNotificacion="success";
                        apa.descripcion = "Folio asignado: "+fpa.folio.numfolio+" - "+(fpa.folio.oficio.titulo==null?fpa.folio.oficio.descripcion:fpa.folio.oficio.titulo);
                        apa.fase = "";
                        apa.roles.add(100L);
                        apa.roles.add(1L);
                        apa.liga = "/usuario/misServicios/4/"+fpa.folio.numfolio;
                        if (  fpa.folio.fechaCaducada() ) {
                            apa.tipoNotificacion = "error";
                            apa.descripcion="Se sobrepasó la fecha de entrega del folio "+fpa.folio.numfolio +" - "+(fpa.folio.oficio.titulo==null?fpa.folio.oficio.descripcion:fpa.folio.oficio.titulo);
                        }
                        nuevo.add(apa);
                    }

                    if (fpa.folio.estado.id >= 5) {
                        for ( PreAgenda pre : fpa.preagendas.stream().filter(f->f.estado.id==5).collect(Collectors.toList())) {
                            AuxNotificacion apa = new AuxNotificacion();
                            apa.fecha = fpa.folio.auditUpdate;
                            apa.roles = new ArrayList<>();
                            apa.liga="";
                            apa.personalId = fpa.personal.id;
                            apa.tipoNotificacion="success";
                            apa.descripcion = "Solicitó recursos folio: " + fpa.folio.numfolio + " - " + (fpa.folio.oficio.titulo == null ? fpa.folio.oficio.descripcion : fpa.folio.oficio.titulo);
                            apa.fase = pre.fase.descripcion;
                            apa.roles.add(100L);
                            apa.roles.add(1L);
                            apa.liga = "/usuario/misServicios/5/" + fpa.folio.numfolio;
                            if (fpa.folio.fechaCaducada()) {
                                apa.tipoNotificacion = "error";
                                apa.descripcion = "Se sobrepasó la fecha de entrega del folio " + fpa.folio.numfolio + " - " + (fpa.folio.oficio.titulo == null ? fpa.folio.oficio.descripcion : fpa.folio.oficio.titulo);
                            }
                            nuevo.add(apa);
                        }
                        for ( Agenda agenda : fpa.agenda.stream().filter(f->f.estado.id==7).collect(Collectors.toList())){
                            AuxNotificacion apa = new AuxNotificacion();
                            apa.fecha = fpa.folio.auditUpdate;
                            apa.roles = new ArrayList<>();
                            apa.liga="";
                            apa.personalId = fpa.personal.id;
                            apa.tipoNotificacion="success";
                            apa.descripcion = "Folio autorizado: " + fpa.folio.numfolio + " - " + (fpa.folio.oficio.titulo == null ? fpa.folio.oficio.descripcion : fpa.folio.oficio.titulo);
                            apa.fase = agenda.fase.descripcion;
                            apa.roles.add(100L);
                            apa.roles.add(1L);
                            apa.liga = "/usuario/misServicios/7/" + fpa.folio.numfolio;
                            if (fpa.folio.fechaCaducada()) {
                                apa.tipoNotificacion = "error";
                                apa.descripcion = "Se sobrepasó la fecha de entrega del folio " + fpa.folio.numfolio + " - " + (fpa.folio.oficio.titulo == null ? fpa.folio.oficio.descripcion : fpa.folio.oficio.titulo);
                            }
                            nuevo.add(apa);
                        }
                    }

                    //this.datos.add(apa);
                    //nuevo.add(apa);
                }






                // Config del sistema
                // List de los ids de los administradores

                List<CuentaRol> ctasRoles = CuentaRol.find
                        .where()
                        .eq("rol.id", 1)
                        .eq("cuenta.personal.activo", "S")
                        .findList();
                List<Long>  productoresIds = ctasRoles.stream().map(m->m.cuenta.personal.id).collect(Collectors.toList());
                Ctacorreo cc = Ctacorreo.find
                        .where().eq("activa", true)
                        .findUnique();
                System.out.println(ColorConsola.ANSI_BLUE+"    cc:"+cc+ColorConsola.ANSI_RESET);
                if (cc == null){
                    for ( Long prodId :  productoresIds) {
                        AuxNotificacion apa = new AuxNotificacion();
                        apa.roles = new ArrayList<>();
                        apa.roles.add(1L);
                        apa.descripcion = "No existe una cuenta ACTIVA de correo del sistema";
                        apa.tipoNotificacion = "error";
                        apa.liga = "/correosSalida";
                        apa.personalId = prodId;
                        apa.fecha = new Date(System.currentTimeMillis() - 3600 * 1000);
                        nuevo.add(apa);
                        //noti.datos = new ArrayList<>(nuevo);
                    }
                }
                System.out.println(ColorConsola.ANSI_BLUE+"    --- despues de ctacorreo"+ColorConsola.ANSI_RESET);
                this.datos = new ArrayList<>();
                this.datos = nuevo;

                System.out.println(ColorConsola.ANSI_BLUE+"    se recargaron "+this.datos.size()+" registros"+ColorConsola.ANSI_RESET);
            //}
        //});
    }


    public static Notificacion getInstance() {
        return INSTANCE;
    }

    public List<AuxNotificacion> datos;

}
