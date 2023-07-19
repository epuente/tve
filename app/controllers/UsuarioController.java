package controllers;

import classes.AuxEventoOperadorSala;
import classes.ColorConsola;
import classes.EventoOperadorSala;
import classes.PersonalDisponible;
import com.avaje.ebean.*;
import com.avaje.ebean.annotation.Transactional;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.impossibl.postgres.api.jdbc.PGConnection;
import models.*;
import models.utils.PlantillaArchivo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.db.DB;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Result;
import views.html.usuario.misServicios;
import views.html.usuario.misServiciosAdmin;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static models.OperadorSala.find;

public class UsuarioController extends ControladorSeguro{

    public static Result misServicios(){
        UsuarioController.registraAcceso(request().path());
        return ok (misServicios.render(0L,0L));
    }
    public static Result misServicios( Long estado, Long id){
        UsuarioController.registraAcceso(request().path());
        return ok (misServicios.render(estado, id));
    }
    public static Result misServiciosDTSS(){
        Logger.info("\n\n\n>>  >> >>  Desde UsuarioController.misServiciosDTSS............");
        //System.out.println( "parametros "+ request() );
        JSONObject json2 = new JSONObject();
        UsuarioController.registraAcceso(request().path());
        int filtrados;
        int sinFiltro;
        Map<Integer, Integer> columnasOrdenables = new HashMap<Integer, Integer>();
        columnasOrdenables.put(0, 1);
        columnasOrdenables.put(1, 19);
        columnasOrdenables.put(2, 9);
        String estado = request().getQueryString("estado");
        String filtro = request().getQueryString("search[value]").toLowerCase();
        int colOrden =   Integer.parseInt( request().getQueryString("order[0][column]")   );
        String tipoOrden = request().getQueryString("order[0][dir]");
        /*
        System.out.println( "parametros order[0][column]:"+ colOrden);
        System.out.println( "parametros order[0][dir]:"+ tipoOrden);
        System.out.println( "estado :"+ estado);
        System.out.println( "filtrando :"+ filtro);
        System.out.println("rolActual: "+session("rolActual"));
        System.out.println("usuario: "+session("usuario"));
        */

        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        int numRegistros = Integer.parseInt(request().getQueryString("length"));
        //System.out.println("numPag: "+numPag);

        //System.out.println("**************************************************************************************"       );
        Page<Oficio> pagOficio = null;
        Query<Folio> qSinFiltro = Folio.find
                .fetch("oficio")
                .fetch("oficio.urremitente")
                .fetch("productoresAsignados")
                .fetch("productoresAsignados.preagendas")
                .fetch("productoresAsignados.preagendas.estado")
                .fetch("productoresAsignados.agenda")
                .fetch("productoresAsignados.agenda.estado")
                .fetch("estado")
                .orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden );
        List<Folio> sublist = null;
        List<Folio> lstConFiltro = null;

        //TODOS LOS ESTADOS DE LOS FOLIOS
        if (estado.compareTo("*")==0){

            // Es productor?
            if (session("rolActual").compareTo("100")==0) {
                qSinFiltro = qSinFiltro.where("(productoresAsignados.personal.id = " + session("usuario") + ") ");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());

                Logger.debug("numPag:" + numPag);
                Logger.debug("numRegistros:" + numRegistros);
                Logger.debug("lstConFiltro.size:" + lstConFiltro.size());
                System.out.println((numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
                if (numRegistros==-1)
                    sublist  = lstConFiltro.subList( 0, lstConFiltro.size());
                else
                    sublist  = lstConFiltro.subList( ((numPag)*numRegistros),       (numPag+1)*numRegistros>lstConFiltro.size()? lstConFiltro.size() : ((numPag+1)*numRegistros)+1  );
                Logger.debug("sublist size:" + sublist.size());

            }

            //Es administrador?
            if (session("rolActual").compareTo("1")==0){
                Logger.debug("Soy administrador");
                qSinFiltro = qSinFiltro.where("id is not null");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());

                Logger.debug("numPag:"+numPag);
                Logger.debug("numRegistros:"+numRegistros);
                Logger.debug("lstConFiltro.size:"+lstConFiltro.size());
                System.out.println(   (numPag+1)*numRegistros>lstConFiltro.size()? lstConFiltro.size() : ((numPag+1)*numRegistros)+1  );

                if (numRegistros==-1)
                    sublist  = lstConFiltro.subList( 0, lstConFiltro.size());
                else
                    sublist  = lstConFiltro.subList( ((numPag)*numRegistros),       (numPag+1)*numRegistros>lstConFiltro.size()? lstConFiltro.size() : ((numPag+1)*numRegistros)+1  );
            }

            // Es ingeniero encargado de la administración de equipo y accesorios?
            if (session("rolActual").compareTo("10")==0    ||  session("rolActual").compareTo("18")==0 ) {
                Logger.debug("Soy ung admon equipo");
                List<Long> foliosIds = new ArrayList<>();
                String cadena;
                List<AgendaIngAdmonEqpo> qAgeInges = AgendaIngAdmonEqpo.find.where().eq("ingeniero.id" ,session("usuario")).findList();
                Logger.debug("tam qAgeInges: " + qAgeInges.size());
                if ( qAgeInges.size()>0) {
                    foliosIds = qAgeInges.stream().map(m -> m.agenda.folioproductorasignado.folio.id).collect(Collectors.toList());
                    StringBuilder sb = new StringBuilder();
                    for (AgendaIngAdmonEqpo x : qAgeInges) {
                        sb.append(x.agenda.folioproductorasignado.folio.id);
                        sb.append(", ");
                    }
                    cadena = sb.toString();
                    if (cadena.length() > 2)
                        cadena = cadena.substring(0, cadena.length() - 2);
                    Logger.debug("foliosIds: " + foliosIds);
                    Logger.debug("cadena: " + cadena);
                    qSinFiltro = qSinFiltro.where().idIn(foliosIds)
                            .filterMany("productoresAsignados.agenda").in( "fase.id",Arrays.asList(2) ).query();
                    lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                            String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                    || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                    || f.oficio.descripcion.toLowerCase().contains(filtro)
                    ).collect(Collectors.toList());
                    if (numRegistros==-1)
                        sublist  = lstConFiltro.subList( 0, lstConFiltro.size());
                    else
                        sublist  = lstConFiltro.subList( ((numPag)*numRegistros), (numPag+1)*numRegistros>lstConFiltro.size()? lstConFiltro.size() : ((numPag+1)*numRegistros)+1  );

                } else {
                    qSinFiltro = Folio.find.where("id = 0");
                    lstConFiltro = qSinFiltro.findList();
                }
            }
           // sinFiltro = qSinFiltro.findList().size();
            //filtrados = sublist.size();

            Logger.debug("fin del compareTo \"*\"");

        } // fin del compareTo "*"

        //OFICIOS QUE NO HAN SIDO ASIGNADOS
        if (estado.compareTo("0")==0){
            System.out.println("x");
            List<Folio> f = Folio.find.fetch("oficio").select("id").findList();
            System.out.println("f size: "+f.size());
            Long[] arrIds = new Long[f.size()];
            System.out.println("arrIds size(1): "+arrIds.length);
            //arrIds = f.toArray(arrIds);
            System.out.println("arrIds size(2): "+arrIds.length);

            // Convertir List a array
            Long[] arrLong = new Long[f.size()];
            int ii=0;
            for ( Folio val : f  ) {
                arrLong[ii++] = val.oficio.id;
            }

            String arrToString = Arrays.toString(arrLong).substring(1, Arrays.toString(arrLong).length()-1);

            Query<Oficio> qo1 = Oficio.find.where("id  not in("+ arrToString +")" );
            Query<Oficio> qo2 = Oficio.find.where("id  not in("+ arrToString +")  AND ("+
                    "urremitente.nombreLargo like :cadena "+
                    "or descripcion like :cadena "+
                    ")").setParameter("cadena", "%"+filtro+"%");

            pagOficio = Oficio.find
                    .fetch("urremitente")
                    .where("id  not in("+ arrToString +") and "
                            + " ("+
                            "urremitente.nombreLargo like :cadena "+
                            "or descripcion like :cadena "+
                            ")").setParameter("cadena", "%"+filtro+"%")
                    .orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden )
                    .findPagingList(numRegistros)
                    .setFetchAhead(false)
                    .getPage(numPag);

            System.out.println("pagOficio list size "+pagOficio.getList().size());
            sinFiltro = qo1.findList().size();
            filtrados = qo2.findList().size();
        }    // fin del compareTo "0"

        //PRODUCTORES ASIGNADOS
        if (estado.compareTo("4")==0){
            // Es productor?
            if (session("rolActual").compareTo("100")==0) {
                //            qSinFiltro = Folio.find.where("(productoresAsignados.agenda.estado.id = 4 and  productoresAsignados.personal.id = "+session("usuario")+") ");
                qSinFiltro = qSinFiltro.where("productoresAsignados.folio.estado.id = 4 and  productoresAsignados.personal.id = " + session("usuario") );
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
            }
            // fin de estado 4 - productor

            //Es administrador?
            if (session("rolActual").compareTo("1")==0){
                System.out.println("------ rol 1 administrador");
/*				 q1 = Folio.find.where().ne("id", null);
				 q2 = q1.eq("productoresAsignados.folio.estado.id",4L)
						 .ilike("numfolio", "%"+filtro+"%")
						 .or(Expr.ilike("oficio.urremitente.nombreLargo", "%"+filtro+"%"), Expr.ilike("oficio.descripcion", "%"+filtro+"%"));*/
                qSinFiltro = qSinFiltro.where("id is not null and productoresAsignados.folio.estado.id = 4");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                //q2 = q1.query().where(" and cast(numfolio as varchar) ilike '%"+filtro+"%' or oficio.urremitente.nombreLargo ilike '%"+filtro+"%' or oficio.descripcion ilike '%"+filtro+"%'").where();

                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
            }
            //fin de estado 4 - productor


            filtrados = lstConFiltro.size();
            sinFiltro = qSinFiltro.findRowCount();
        }        // fin del compareTo "4"


        // El productor ha realizado solicitud?
        if (estado.compareTo("5")==0){
            // Es productor?
            if (session("rolActual").compareTo("100")==0) {
                System.out.println("soy productor con id "+session("usuario") );

//                qSinFiltro = qSinFiltro.where("(productoresAsignados.personal.id =" + session("usuario") + " and  productoresAsignados.agenda.estado.id = 5)");
                qSinFiltro = qSinFiltro.where("(productoresAsignados.preagendas.estado.id = 5 and  productoresAsignados.personal.id = " + session("usuario") + ") ");

                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);

            }
            // Es administrador?
            if (session("rolActual").compareTo("1")==0){
                System.out.println("Soy administrador en estado 5");
                //q1 = Folio.find.where(" (productoresAsignados.preagendas.estado.id = "+estado+") ");
                qSinFiltro = qSinFiltro.where("productoresAsignados.preagendas.estado.id  = 5");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);


                System.out.println("fin de administrador estado 5");
            }

            // Es ingeniero encargado de la adqministración de equipo y accesorios?
            if (session("rolActual").compareTo("10")==0){
                System.out.println("Soy ngeniero encargado de la adqministración de equipo y accesorios en estado 5");
                // buscar en AgendaIngAdmonEqpo los agenda.id (donde el ingeniero.id = session usuario)
                List<AgendaIngAdmonEqpo> qAgeInges = AgendaIngAdmonEqpo.find.where().eq("ingeniero.id" ,session("usuario")).findList();
                if ( qAgeInges.size()>0) {
                    List<Long> foliosIds = qAgeInges.stream().map(m -> m.agenda.folioproductorasignado.folio.id).collect(Collectors.toList());
                    StringBuilder sb = new StringBuilder();
                    for (AgendaIngAdmonEqpo x : qAgeInges) {
                        //cadena += x.agenda.folioproductorasignado.folio.id + ", ";
                        sb.append(x.agenda.folioproductorasignado.folio.id);
                        sb.append(", ");
                    }
                    String cadena = sb.toString();
                    if (cadena.length() > 2)
                        cadena = cadena.substring(0, cadena.length() - 2);

                    System.out.println("foliosIds: " + foliosIds);
                    System.out.println("cadena: " + cadena);

                    //q1 = Folio.find.where("id in (" + cadena + ") and (productoresAsignados.agenda.accesorios.autorizo=null)");
                    qSinFiltro = qSinFiltro.where("id in ("+cadena+") and productoresAsignados.agenda.accesorios.autorizo is null");
                    lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                            String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                    || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                    || f.oficio.descripcion.toLowerCase().contains(filtro)
                    ).collect(Collectors.toList());
                    if (numRegistros == -1)
                        sublist = lstConFiltro.subList(0, lstConFiltro.size());
                    else
                        sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);

                } else {
                    qSinFiltro = qSinFiltro.where("id = 0");
                    lstConFiltro = qSinFiltro.findList();
                }
            }
            filtrados = lstConFiltro == null?0:lstConFiltro.size();
            sinFiltro = qSinFiltro.findList().size();
            System.out.println("fin de estado 5");
        }// Fin del compareTo "5"

        // Los que se han autorizado por el administrador
        // (para el equipo/accesorios se requiere ademas que el ingeniero haga la asignación de equipo  (estado 8)  )
        if (estado.compareTo("7")==0){
            System.out.println("estado 7");

            // Es productor?
            if ( session("rolActual").compareTo("100")==0){
                qSinFiltro = qSinFiltro.where("(productoresAsignados.agenda.estado.id = 7 and productoresAsignados.personal.id = " + session("usuario") + ") ");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
            }


            //Es administrador?
            if (session("rolActual").compareTo("1")==0){
                System.out.println("estado 7 025");
                qSinFiltro = qSinFiltro.where("productoresAsignados.agenda.estado.id = 7");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
                System.out.println("estado 7 030");
            }

            // Es ingeniero encargado de la administración de equipo y accesorios?
            if (session("rolActual").compareTo("10")==0){
                System.out.println("estado 7 035");
                System.out.println("es ingeniero ");
                System.out.println("estado "+estado);
                 /*
				 String ingenieroId = estado.split("-")[1];
				 System.out.println("ingeniero "+ingenieroId);
				  */
                // buscar en AgendaIngAdmonEqpo los agenda.id (donde el ingeniero.id = session usuario)
                List<AgendaIngAdmonEqpo> qAgeInges = AgendaIngAdmonEqpo.find.where().eq("ingeniero.id" ,Long.parseLong(session("usuario"))).findList();
                System.out.println("qAgeInges.size "+qAgeInges.size());
                if ( qAgeInges.size()>0) {
                    List<Long> foliosIds = qAgeInges.stream().map(m -> m.agenda.folioproductorasignado.folio.id).collect(Collectors.toList());
                    String cadena = foliosIds.stream().distinct().map(String::valueOf)
                            .collect(Collectors.joining("-"));
                    System.out.println("foliosIds: " + foliosIds);
                    System.out.println("cadena: " + cadena);

                    qSinFiltro = qSinFiltro.where("id in ("+cadena+") and productoresAsignados.agenda.accesorios.autorizo is null and productoresAsignados.agenda.estado.id = 7 ");
                    lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                            String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                    || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                    || f.oficio.descripcion.toLowerCase().contains(filtro)
                    ).collect(Collectors.toList());
                    if (numRegistros == -1)
                        sublist = lstConFiltro.subList(0, lstConFiltro.size());
                    else
                        sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
                } else {
                    qSinFiltro =   qSinFiltro.where("id = 0");
                    lstConFiltro = qSinFiltro.findList();
                }
                System.out.println("estado 7 040");
            }
            System.out.println("estado 7 045");

            System.out.println("estado 7 047");
            sinFiltro = qSinFiltro.findList().size();
            System.out.println("estado 7 050");
            filtrados = lstConFiltro==null? 0: lstConFiltro.size();
            System.out.println("fin de estado 7");
        } // fin de estado compareTo "7"


        //El equipo y accesorios necesitan validarse/asignarse por el ingeniero
        if (estado.compareTo("8")==0) {
            System.out.println("Edo 8, EA asignados");
            // Es Productor?
            if (session("rolActual").compareTo("100")==0){
                qSinFiltro = qSinFiltro.where("(productoresAsignados.agenda.estado.id = 8 and productoresAsignados.personal.id = " + session("usuario") + ") ");
                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
            }


            // Es ingeniero encargado de la administración de equipo y accesorios?
            if (session("rolActual").compareTo("10")==0){
                System.out.println("es ingeniero ");
                System.out.println("estado "+estado);
                // buscar en AgendaIngAdmonEqpo los agenda.id (donde el ingeniero.id = session usuario)
                List<AgendaIngAdmonEqpo> qAgeInges = AgendaIngAdmonEqpo.find.where().eq("ingeniero.id" ,session("usuario")).findList();
                System.out.println("qAgeInges.size "+qAgeInges.size());
                if ( qAgeInges.size()>0) {
                    List<Long> foliosIds = qAgeInges.stream().map(m -> m.agenda.folioproductorasignado.folio.id).collect(Collectors.toList());
                    String cadena = foliosIds.stream().distinct().map(String::valueOf)
                            .collect(Collectors.joining("-"));
                    System.out.println("foliosIds: " + foliosIds);
                    System.out.println("cadena: " + cadena);
                    System.out.println("filtro: " + filtro);
                    qSinFiltro = qSinFiltro.where("id in ("+cadena+") and productoresAsignados.agenda.accesorios.autorizo is not null and productoresAsignados.agenda.estado.id = 8 ");
                    System.out.println(ColorConsola.ANSI_BLUE+"qSinFiltro.size:"+qSinFiltro.findList().size()+ColorConsola.ANSI_RESET);
                    lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                            String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                    || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                    || f.oficio.descripcion.toLowerCase().contains(filtro)
                    ).collect(Collectors.toList());
                    if (numRegistros == -1)
                        sublist = lstConFiltro.subList(0, lstConFiltro.size());
                    else
                        sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
                } else {
                    qSinFiltro = qSinFiltro.where("id = 0");
                    lstConFiltro = qSinFiltro.findList();
                    System.out.println(ColorConsola.ANSI_BLUE+"lstConFiltro.size:"+lstConFiltro.size());
                }
            }
        } // fin de estado compareTo "8"

        // Servicio terminado
        if (estado.compareTo("99")==0) {
            System.out.println("Edo 99, Terminado");
            // Es Productor?
            if (session("rolActual").compareTo("1") == 0)
                qSinFiltro = qSinFiltro.where("productoresAsignados.agenda.estado.id = 99");
            else
                qSinFiltro = qSinFiltro.where("(productoresAsignados.agenda.estado.id = 99 and productoresAsignados.personal.id = " + session("usuario") + ") ");

                lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                        String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                                || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                                || f.oficio.descripcion.toLowerCase().contains(filtro)
                ).collect(Collectors.toList());
                if (numRegistros == -1)
                    sublist = lstConFiltro.subList(0, lstConFiltro.size());
                else
                    sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
            //}
        }

        // Servicio terminado
        if (estado.compareTo("100")==0) {
            System.out.println("Edo 100, Cancelado");
            // Es Productor?
            if (session("rolActual").compareTo("1") == 0)
                qSinFiltro = qSinFiltro.where("productoresAsignados.agenda.estado.id = 100");
            else
                qSinFiltro = qSinFiltro.where("(productoresAsignados.agenda.estado.id = 100 and productoresAsignados.personal.id = " + session("usuario") + ") ");
            lstConFiltro = qSinFiltro.findList().stream().filter(f ->
                    String.valueOf(f.numfolio).toLowerCase().contains(filtro)
                            || f.oficio.urremitente.nombreLargo.toLowerCase().contains(filtro)
                            || f.oficio.descripcion.toLowerCase().contains(filtro)
            ).collect(Collectors.toList());
            if (numRegistros == -1)
                sublist = lstConFiltro.subList(0, lstConFiltro.size());
            else
                sublist = lstConFiltro.subList(((numPag) * numRegistros), (numPag + 1) * numRegistros > lstConFiltro.size() ? lstConFiltro.size() : ((numPag + 1) * numRegistros) + 1);
            //}
        }


        Logger.debug("lstConFiltro "+lstConFiltro);
        sinFiltro = qSinFiltro.findList().size();
        filtrados =  lstConFiltro==null?0 :  lstConFiltro.size();
        try {
            json2.put("draw",  new Date().getTime()  );
            json2.put("recordsTotal",  sinFiltro );
            json2.put("recordsFiltered", filtrados);

            JSONArray losDatos = new JSONArray();
            // Oficios sin asignar
            if (estado.compareTo("0")==0) {
                for( Oficio p : pagOficio.getList()  ){
                    Set<String> losEstados = new HashSet<String>();
                    JSONObject datoP = new JSONObject();
                    datoP.put("id", p.id);
                    datoP.put("folio", "No se le ha asignado folio");
                    datoP.put("oficioId", "ninguno");
                    datoP.put("ur", p.urremitente.nombreLargo);

                    losEstados.add("Por Asignar");
                    estado="2";

                    datoP.put("estado", (estado.compareTo("*")==0)?losEstados.toString():Estado.find.where().idEq(estado).findUnique().descripcion);

                    datoP.put("estadoId", (estado.compareTo("*")==0)?"*":estado);
                    datoP.put("descripcion", p.descripcion);
                    losDatos.put(datoP);
                }
                if ( pagOficio.getTotalRowCount()>0 ){
                    json2.put("data", losDatos);
                } else {
                    json2.put("data", new JSONArray() );
                    return ok( json2.toString()  );
                }
            } else {
                if (sublist !=null && sublist.size()>0) {
                    for (Folio p : sublist) {
                        Set<String> losEstados = new HashSet<>();
                        JSONObject datoP = new JSONObject();
                        datoP.put("id", p.id);
                        datoP.put("folio", p.numfolio);
                        datoP.put("folioId", p.id);
                        datoP.put("ur", p.oficio.urremitente.nombreLargo);

                        for (FolioProductorAsignado prod : p.productoresAsignados) {
                            if (prod.preagendas.isEmpty() && prod.agenda.isEmpty()) {
                                if (prod.folio.estado.id == 100)
                                    losEstados.add("Cancelado");
                                if (prod.folio.estado.id == 99)
                                    losEstados.add("Cerrado");
                                if (prod.folio.estado.id == 4)
                                    losEstados.add("Por atender");
                            } else {
                                if (session("rolActual").compareTo("10") != 0) {
                                    for (PreAgenda pre : prod.preagendas) {
                                        if (pre.estado.id == 4)
                                            losEstados.add("Por atender");
                                        if (pre.estado.id == 5)
                                            losEstados.add("Solicitudes");
                                        if (pre.estado.id == 7)
                                            losEstados.add("Autorizados");
                                        if (pre.estado.id > 7)
                                            losEstados.add("Terminados");
                                        if (pre.estado.id == 100)
                                            losEstados.add("cancelados");
                                    }
                                }
                                for (Agenda age : prod.agenda) {
                                    if (age.estado.id == 4)
                                        losEstados.add("Por atender");
                                    if (age.estado.id == 5)
                                        losEstados.add("Solicitudes");
                                    if (age.estado.id == 7)
                                        losEstados.add("Autorizados");
                                    if (age.estado.id == 8)
                                        losEstados.add("E/A Asignados");
                                    if (age.estado.id > 8)
                                        losEstados.add("Terminados");
                                    if (age.estado.id == 100)
                                        losEstados.add("Cancelados");
                                }
                            }
                        }
                        datoP.put("estado", (estado.compareTo("*") == 0) ? losEstados.toString() : Estado.find.where().idEq(estado).findUnique().descripcion);
                        datoP.put("estadoId", (estado.compareTo("*") == 0) ? "*" : estado);
                        datoP.put("descripcion", p.oficio.descripcion);
                        losDatos.put(datoP);
                    }
                    json2.put("data", losDatos);
                } else {
                    json2.put("data", new JSONArray() );
                    return ok( json2.toString()  );
                }
            }
        } catch (JSONException e) {
            System.out.println(ColorConsola.ANSI_RED+e.getMessage()+ColorConsola.ANSI_RESET);
            e.printStackTrace();
        }
       // System.out.println("retorno "+json2.toString()+"\n\n\n");
        return ok( json2.toString()  );
    }


    public static Result misServiciosAdmin(){
        UsuarioController.registraAcceso(request().path());
        return ok (misServiciosAdmin.render());
    }

    public static Result misServiciosRespDigi(){
        UsuarioController.registraAcceso(request().path());
        return ok (views.html.usuario.tablero.render());
    }

    public static Result misServiciosOperaSala(){
        UsuarioController.registraAcceso(request().path());
        return ok (views.html.usuario.misServiciosOperaSala.render());
    }

    public static Result ajaxDatos(){
        Object evento = null;
        System.out.println("Llegando a UsuarioController.ajaxDatos...");
        JsonNode json = request().body().asJson();

        System.out.println("UsuarioController.ajaxDatos recibe "+json);
        String tipo = json.findPath("tipo").asText();
        System.out.println("01");
        Long id = json.findPath("id").asLong();

        System.out.println( tipo);
        System.out.println(id);
        JsonContext jsonContext = Ebean.createJsonContext();

        if (tipo.contentEquals("mantto")){
            return ok ("{\"tipo\":\"mantto\"");
        }

        if (tipo.contentEquals("preagenda")){
            System.out.println("- preagenda");
            PreAgenda p = PreAgenda.find
                    .fetch("folioproductorasignado.folio")
                    .fetch("folioproductorasignado.folio.oficio")
                    .fetch("folioproductorasignado.personal")
                    .fetch("fase")
                    .fetch("estado")
                    .fetch("juntas")
                    .fetch("locaciones")
                    .fetch("salidas")
                    .fetch("vehiculos")
                    .fetch("personal")
                    .fetch("locutores.personal")
                    .fetch("locutores.personal.cuentas.roles.rol")
                    .fetch("operadoresSala")
                    .fetch("operadoresSala.personal")
                    .fetch("equipos.equipo")
                    .fetch("accesorios.accesorio")
                    .fetch("ingsAdmonEqpo")
                    .fetch("ingsAdmonEqpo.ingeniero")
                    .fetch("salas.sala")
                    .fetch("salas.usoscabina")
                    .fetch("formatos")
                    .fetch("formatos.formato")
                    .where().eq("id", id).findUnique();

            System.out.println("* * * UsuarioController.ajaxDatos regresa:    " + jsonContext.toJsonString(p));
            //return ok(jsonContext.toJsonString(p));
            evento = p;
        }
        if (tipo.contentEquals("agenda")){
            System.out.println("- agenda");
            evento = Agenda.find
                    .fetch("folioproductorasignado.folio")
                    .fetch("folioproductorasignado.folio.oficio")
                    .fetch("folioproductorasignado.personal")
                    .fetch("folioproductorasignado.agenda")
                    .fetch("folioproductorasignado.agenda.ingsAdmonEqpo")
                    .fetch("fase")
                    .fetch("estado")
                    .fetch("juntas")
                    .fetch("locaciones")
                    .fetch("salidas")
                    .fetch("vehiculos")
                    .fetch("vehiculos.vehiculo")
                    .fetch("cuentaRol")
                    .fetch("cuentaRol.cuentarol.cuenta")
                    .fetch("cuentaRol.cuentarol.cuenta.personal")
                    .fetch("cuentaRol.cuentarol.rol")
                    .fetch("operadoresSala")
                    .fetch("operadoresSala.personal")
                    .fetch("equipos.equipo")
                    .fetch("accesorios.accesorio")
                    .fetch("salas")
                    .fetch("salas.sala")
                    .fetch("salas.usoscabina")
                    .fetch("formatos")
                    .fetch("formatos.formato")
                    .fetch("previoautorizaequipo")
                    .fetch("previoautorizaequipo.preagendaequipo")
                    .fetch("previoautorizaequipo.preagendaequipo.equipo")
                    .fetch("previoautorizaaccesorios")
                    .fetch("previoautorizaaccesorios.preagendaaccesorio")
                    .fetch("previoautorizaaccesorios.preagendaaccesorio.accesorio")
                    .where().eq("id", id)
                    .findUnique();

            // Debido a que ademas del estado 7 se requiere el estado 8
            // El equipo y accesorios

            // Preagenda del mismo folioproductorasignado
            List<PreAgenda> x = Agenda.find.where().eq("id", id).findUnique().folioproductorasignado.preagendas;
            List<Long> lstIds = new ArrayList<Long>(Arrays.asList());
            x.forEach(preAgenda -> {
                preAgenda.equipos.forEach( equipo->{
                    System.out.println("equipo id "+equipo.id);
                    lstIds.add(equipo.id);
                });
            });


            List<Long> lstIds2 = x.stream()
                    .flatMap(preAgenda -> preAgenda.equipos.stream())
                    .peek(equipo -> System.out.println("equipo id " + equipo.id))
                    .map(equipo -> equipo.equipo.id)
                    .collect(Collectors.toList());

            List<Long> lstIds3 = x.stream()
                            .flatMap(preAgenda -> preAgenda.accesorios.stream())
                                    .map(acc -> acc.accesorio.id)
                                            .collect(Collectors.toList());


            System.out.println("arr Ids:"+lstIds);
            System.out.println("arr Ids2:"+lstIds2);
            System.out.println("arr Ids3:"+lstIds3);
        }
        System.out.println(  jsonContext.toJsonString(evento) );
        return ok (jsonContext.toJsonString(evento));
    }

    @Transactional
    public static Result ajaxGrabaEvento(){
        System.out.println("\n\n\n\n\n\n\n\n\nDesde UsuarioController.ajaxGrabaEvento ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        boolean retorno = false;
        PreAgenda obj = null;
        JsonNode json = request().body().asJson();
        System.out.println(json);
        UsuarioController.registraAcceso(request().path());
        FolioProductorAsignado fpa = FolioProductorAsignado.find.byId(  json.findPath("preagenda").findPath("folioproductorasignado").findPath("id").asLong()  );

        // Elimina nodo personalIds
        for (JsonNode personNode : json) {
            if (personNode instanceof ObjectNode) {
                ObjectNode object = (ObjectNode) personNode;
                object.remove("personalIds");
            }
        }

        //TimeZone.setDefault(TimeZone.getTimeZone("GMT-6"));
        //TimeZone.setDefault(TimeZone.getTimeZone("browser"));

        for (int j=0; j<fpa.preagendas.size();j++) {
            System.out.println("    j:"+j);
            for (int k=0; k<fpa.preagendas.get(j).personal.size();k++) {
                System.out.println("        k:"+k);
            }
        }


        System.out.println("10");
        boolean solicitaVehiculo =   json.findPath("preagenda").has("vehiculos");
        System.out.println("solicita v: "+solicitaVehiculo);
        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("20");
            System.out.println("json + traverse");
            System.out.println(json.findPath("preagenda").traverse());


            obj = mapper.readValue(json.findPath("preagenda").traverse(), PreAgenda.class);


            System.out.println("de json a objeto oki!!!");

            //Quitar nodo 'id'
            //Quitar nodo 'resourceId'
            for (JsonNode personNode : json) {
                if (personNode instanceof ObjectNode) {
                    ObjectNode object = (ObjectNode) personNode;
                    object.remove("id");
                    //    object.remove("vehiculos");
                }
            }


            // EL id de operador de sala esta vacio?
            for (int x = obj.operadoresSala.size()-1; x>=0;x--) {
                System.out.println("x - :"+x);

                PreAgendaOperadorSala aux = obj.operadoresSala.get(x);
                System.out.println("operadoresSala  aux.personal.id:"+aux.personal.id);
                if (aux.personal.id==null) {
                    obj.operadoresSala.remove(x);
                }
            }

            // Elimina las entradas de formatos de entrega con cantidad cero
            for (int x=obj.formatos.size()-1;x>=0; x--) {
                if (obj.formatos.get(x).cantidad==0)
                    obj.formatos.remove(x);
            }

            // Productor ha solicitado recursos?
            if (obj.estado.id == 5) {
                Estado edo = Estado.find.byId(5L);
                obj.estado = edo;
                fpa.preagendas.add(obj);
                fpa.folio.estado = edo;
                obj.equipos.forEach(e->e.estado = edo);
                obj.accesorios.forEach(a->a.estado = edo);
                fpa.folio.update();
            }


            obj.inicio =     sdf.parse(json.findValue("inicio").asText());
            obj.fin =     sdf.parse(json.findValue("fin").asText());
            if (obj.salidas!=null && obj.salidas.size()>0) {
                System.out.println("*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
                obj.salidas.get(0).salida = sdf.parse(json.findPath("salidas").findValue("salida").asText());
                Date hoy = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(hoy);
                System.out.println("calendar c   "+ sdf.format(c.getTime()));
                c.add(Calendar.HOUR_OF_DAY,-1);
                System.out.println("calendar c-1 "+ sdf.format(c.getTime()));
/*
                obj.salidas.get(     obj.salidas.size()-1  ).auditInsert = c.getTime();
                obj.salidas.get(     obj.salidas.size()-1  ).auditUpdate = c.getTime();

 */

            }

            Object someObject = obj;
            for (Field field : someObject.getClass().getDeclaredFields()) {
                field.setAccessible(true); // You might want to set modifier to public first.
                Object value = null;
                try {
                    value = field.get(someObject);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value != null) {
                    System.out.println("        "+field.getName() + " = "  + value);
                }
            }

            if (obj.salidas.size()!=0){
                obj.salidas.get(0).preagenda =  obj;
            }
            if (solicitaVehiculo){
                obj.vehiculos.forEach(Model::delete);
                PreAgendaVehiculo pasv = new PreAgendaVehiculo();
                pasv.preagenda = obj;
                obj.vehiculos.add( pasv );
            }
            if (obj.juntas.size()!=0){
                obj.juntas.get(0).preagenda =  obj;
            }
            if (obj.locaciones.size()!=0){
                obj.locaciones.get(0).preagenda =  obj;
            }

            for( PreAgendaAccesorio e :  obj.accesorios){
                System.out.println("       id accesorio :"+e.accesorio.id);
                e.preagenda =  obj;
            }

            if (obj.salas.size()!=0){
                obj.salas.get(0).preagenda = obj;
                if (obj.salas.get(0).usoscabina.size()!=0){
                    obj.salas.get(0).usoscabina.get(0).preagendasala = obj.salas.get(0);
                }
            }


            obj.folioproductorasignado.id = fpa.id;
            PreAgenda existePAS = PreAgenda.find.byId(obj.id);
            System.out.println("PreAgenda.find.byId(obj.id) == null    "+ existePAS);
            if (existePAS != null){
                System.out.println("existePAS != null");
                if (existePAS.salas.size() != 0)
                    PreAgendaSala.find.where().eq("preagenda.id", obj.id).findUnique().delete();
                PreAgenda.find.byId(obj.id).delete();
            }
            System.out.println("/*/*/*/*/*/*/*/");


            System.out.println("obj.personal.size() "+obj.personal.size());
            System.out.println("obj.locutores.size() "+obj.locutores.size());
            System.out.println("obj.formatos.size() "+obj.formatos.size());

            // Locutores
            if (obj.locutores.size()>0) {
                System.out.println("obj.locutores:"+obj.locutores);

                for(int x=0; x<obj.locutores.size();x++) {
                    PreAgendaLocutor locutor = obj.locutores.get(x);
                    System.out.println("locutor:"+locutor);
//					System.out.println("locutor.personal:"+locutor.personal);
//					System.out.println("locutor.personal.id:"+locutor.personal.id);
                    locutor.personal = Personal.find.ref(locutor.personal.id);
                    locutor.preagenda = obj;
                    locutor.id=null;
                }
            }

            // PreAgendaRol
            if (obj.personal.size()>0) {
                List<PreAgendaRol> nvos = new ArrayList<PreAgendaRol>();
                for (int x=0;x<obj.personal.size();x++) {
                    PreAgendaRol prol = obj.personal.get(x);
                    PreAgendaRol pasRol = new PreAgendaRol();
                    pasRol.id = null;
                    pasRol.rol = Rol.find.ref(prol.rol.id);
                    pasRol.cantidad = prol.cantidad;
                    nvos.add(pasRol);
                }
                obj.personal = nvos;
                fpa.preagendas.add(obj);
            }

            // Entrega
            if (obj.formatos.size()>0) {
                for ( int x=0; x<obj.formatos.size();x++) {
                    obj.formatos.get(x).preagenda = obj;
                    fpa.preagendas.add(obj);
                }
            }


            obj.salidas.forEach(x-> System.out.println("salidaa; "+x.salida )  );
            obj.salidas.forEach(x-> System.out.println("auditInsert; "+x.auditInsert )  );




            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++antes+++++++++++++++++++++");
            fpa.update();
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++despues+++++++++++++++++++");


            System.out.println("inicia propiedades del objeto... obj (de nuez)" );
            someObject = fpa;
            for (Field field : someObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(someObject);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value != null) {
                    System.out.println("    " + field.getName() + " = " + value);
                }
            }
            System.out.println("... termina propiedades del objeto obj (de nuez)");




           // System.out.println(""+fpa.preagendas.get(0).salidas.get(0).salida);
            retorno=true;
            System.out.println("   ........................... despues de update ");
        } catch (JsonParseException | JsonMappingException e) {
            System.out.println(ColorConsola.ANSI_RED);
            e.printStackTrace();
            System.out.println(ColorConsola.ANSI_RESET);
        } catch (IOException e) {
            System.out.println(ColorConsola.ANSI_RED);
            e.printStackTrace();
            System.out.println(ColorConsola.ANSI_RESET);
        } catch ( Exception e){
            System.out.println(ColorConsola.ANSI_RED+e.getMessage());
            e.printStackTrace();
            System.out.println(ColorConsola.ANSI_RESET);
        }

        System.out.println("retorno->"+"{\"agregado\":"+retorno+",\"id\":"+obj.id+"}");
        return ok( "{\"agregado\":"+retorno+",\"id\":"+obj.id+"}" );
    }


    public static Result ajaxActualizaEvento(){
        PreAgenda obj;
        JsonNode json = request().body().asJson();
        System.out.println("   ...........................   desde UsuarioController.ajaxActualizaEvento "+json);
        long laSala = json.findPath("salasSalaId").asLong();
        String usoCabina = json.findPath("usocabina").asText();
        int laFase = json.findPath("faseId").asInt();
        String elRecurso =  json.findPath("resourceId").asText();
        String tipoOperacion = json.findPath("tipoOperacion").asText();
        UsuarioController.registraAcceso(request().path());

        long auxFase = 1L;
        System.out.println("laSala:"+laSala);
        System.out.println("laFase:"+laFase);
        System.out.println("elRecurso:"+elRecurso);
        //Quitar nodo 'salasSalaId'
        for (JsonNode personNode : json) {
            if (personNode instanceof ObjectNode) {
                ObjectNode object = (ObjectNode) personNode;
                object.remove("salasSalaId");
                object.remove("usocabina");
                object.remove("faseId");
                object.remove("resourceId");
                object.remove("tipoOperacion");
            }
        }
        System.out.println("se eliminó el nodo salasSalaId, usocabina, faseId, resourceId, tipoOperacion");
        PreAgenda o = new PreAgenda();
        if (   (json.findPath("tipo").asText().compareTo("preagenda") == 0)  ||  (session("rolActual").compareTo("1")==0)
                ||  json.findPath("estado").findPath("id").asLong() == 5
                ||  json.findPath("estado").findPath("id").asLong() == 7   ){
            o = PreAgenda.find.byId( json.findPath("id").asLong()   );


            // Se eliminan todos los oneToMany, esto debido a que esta función se usa para actualizar los eventos que visualmente se cambian en la agenda
            // No se eliminan los oneToMany si tipoOperacion = "resize" y los resourceId son iguales

            System.out.println("*** *** *** *** *** TIPOOPERACION "+tipoOperacion+"    resourceIdAnterior "+json.findPath("resourceIdAnterior").asText()+"       resourceId "+elRecurso);
            System.out.println("*** *** *** *** *** TIPOOPERACION "+json.findPath("resourceIdAnterior").asText().compareTo(elRecurso));


            if (!tipoOperacion.contentEquals("resize")
                    &&   (json.findPath("resourceIdAnterior").asText().compareTo(elRecurso)!=0)){
                System.out.println("Eliminando oneToMany:  .....................................................................");
                o.locaciones.forEach( Model::delete);
                o.personal.forEach(Model::delete);
                o.locutores.forEach(Model::delete);
                o.accesorios.forEach(Model::delete);
                o.equipos.forEach(Model::delete);
                o.salas.forEach(Model::delete);
                o.salidas.forEach(Model::delete);
                o.formatos.forEach(Model::delete);
                o.vehiculos.forEach(Model::delete);
                o.juntas.forEach(Model::delete);
                o.update();
            }

            //Quitar nodos 'tipo' y 'resourceIdAnterior'
            for (JsonNode personNode : json) {
                if (personNode instanceof ObjectNode) {
                    ObjectNode object = (ObjectNode) personNode;
                    object.remove("tipo");
                    object.remove("resourceIdAnterior");
                }
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                obj = mapper.readValue(json.findPath("evento").traverse(), PreAgenda.class);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                o.inicio = sdf.parse(json.findPath("inicio").asText());
                o.fin = sdf.parse(json.findPath("fin").asText());

                //Cuando se trata de move, usar defaults
                if (tipoOperacion.contentEquals("move")) {
                    switch ( elRecurso.toLowerCase() ) {
                        case "a":
                        case "c": auxFase = 1L; break;
                        case "b": auxFase = 2L; break;
                        case "d":
                        case "e":
                        case "f": auxFase = 3L; break;
                        case "g": auxFase = 4L; break;
                        case "h": auxFase = 5L; break;
                    }
                    System.out.println("auxFase:"+auxFase);
                    o.fase = Fase.find.byId(auxFase);
                }


                // Este loop solo muestra en consola los nodos de json
                Object someObject = obj;
                for (Field field : someObject.getClass().getDeclaredFields()) {
                    field.setAccessible(true); // You might want to set modifier to public first.
                    Object value = null;
                    try {
                        value = field.get(someObject);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (value != null) {
                        System.out.println(field.getName() + "=" + value);
                    }
                }

                //Cuando se trata de move, usar defaults
                if (tipoOperacion.contentEquals("move")) {
                    if (laSala==0) {
                        System.out.println("se elimina la sala");
                        o.salas.forEach(Model::delete);
                    }
                    if (laSala!=0) {
                        if (o.salas.size()>0) {
                            System.out.println("se actualiza la sala");
                            o.salas.get(0).sala = Sala.find.byId(laSala);
                        }
                        if (o.salas.size()==0) {
                            System.out.println("se crea la sala");
                            o.salas =  new ArrayList<PreAgendaSala>();
                            PreAgendaSala nuevaSala = new PreAgendaSala();
                            nuevaSala.preagenda = o;
                            nuevaSala.sala =  Sala.find.byId(laSala);
                            o.salas.add(nuevaSala);
                        }
                        System.out.println("sala "+laSala);
                        if (!Objects.equals(usoCabina, "")) {
                            PreAgendaSalaUsoCabina uso = new PreAgendaSalaUsoCabina();
                            uso.preagendasala = o.salas.get(0);
                            uso.usocabina = usoCabina;
                            o.salas.get(0).usoscabina.add(uso);
                        }
                    }

                }


                if (obj.salidas.size() != 0){
                    o.salidas.get(0).preagenda =  obj;
                }
                if (obj.vehiculos.size() != 0){
                    o.vehiculos.get(0).preagenda =  obj;
                }
                if (obj.locaciones.size() != 0){
                    o.locaciones.get(0).preagenda =  obj;
                }
                if (obj.personal.size() != 0){
                    System.out.println("   evento con personal");
                    for( PreAgendaRol tp :  obj.personal){
                        System.out.println("       tipo personal :"+tp.rol.id +"     cantidad: "+tp.cantidad);
                        tp.preagenda =  obj;
                    }

                }


                System.out.println("   ........................... 00 folioproductorasignado "+o.folioproductorasignado);
                System.out.println("   ........................... 01 fase "+o.fase.id);
                System.out.println("   ........................... 02 inicio "+o.inicio);
                System.out.println("   ........................... 03 inicio "+o.fin);
                System.out.println("   ........................... 04 estado "+o.estado.id);

                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++antes+++++++++++++++++++++");
                o.update();
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++despues+++++++++++++++++++");
            } catch (JsonParseException e) {
                e.printStackTrace();
                return ok( "{\"actualizado\":\"false\"}" );
            } catch (JsonMappingException e) {
                e.printStackTrace();
                return ok( "{\"actualizado\":\"false\"}" );
            } catch (IOException e) {
                e.printStackTrace();
                return ok( "{\"actualizado\":\"false\"}" );
            } catch ( Exception e){
                System.out.println(ColorConsola.ANSI_RED+"Error!!!!!!!!!!!!!   "+e.getMessage()+ColorConsola.ANSI_RESET);
                e.printStackTrace();
                return ok( "{\"actualizado\":\"false\"}" );
            }


        }
        System.out.println("retorna " + "{\"actualizado\":"+(o.id != null)+",\"id\":"+o.id+"}"  );
        return ok( "{\"actualizado\":"+(o.id != null)+",\"id\":"+o.id+"}" );


    }


    @Transactional
    public static Result ajaxEliminaEvento(){
        System.out.println("   ...........................   desde ajaxEliminaEvento ");
        boolean eliminado = false;
        JsonNode json = request().body().asJson();
        System.out.println("json"+json);
        System.out.println("id:"+json.findPath("id").asText());
        System.out.println("tipo:"+json.findPath("tipo").asText());
        UsuarioController.registraAcceso(request().path());
        PreAgenda o = null;
        if (json.findPath("tipo").asText().compareTo("preagenda") == 0){
            o = PreAgenda.find.byId(json.findPath("id").asLong());
            try {
                if (o.salas.size()!=0){
                    PreAgendaSala.find.byId(o.salas.get(0).id).delete();
                }
                PreAgenda.find.byId(o.id).delete();
                eliminado = true;
            } catch ( Exception e){
                System.out.println("Error!!!!!!!!!!!!!   "+e.getMessage());
                System.out.println("Error!!!!!!!!!!!!!  regresando "+  "{\"eliminado\":"+eliminado+"}"  );
                return ok( "{\"eliminado\":"+eliminado+"}" );
            }
        }
        System.out.println("regresando "+  "{\"eliminado\":"+eliminado+"}"  );
        return ok( "{\"eliminado\":"+eliminado+"}" );
    }

    public static Result ajaxLocutoresDisponibles(){
        System.out.println("Buscando locutores disponibles......................................");
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println("REcibido en  ajaxLocutoresDisponibles......................................"+json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Buscar los locutores  ocupados en fecha y hora en preagenda
        List<Object> arr1 = auxLocutoresNoDisponiblesPreagenda(sdf.format(d), sdf.format(h));
        List<Object> arr2 = auxLocutoresNoDisponiblesAgenda(sdf.format(d), sdf.format(h));

        // Buscar los locutores ocupados en fecha y hora en agenda
        //List<Object> arr2 = PreAgendaLocutor.find.setRawSql(rawB2.create()).findIds();

        System.out.println("Locutores No disponibles preagenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+arr1.size());
        System.out.println("Locutores No disponibles    agenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+arr2.size());

        //List<Personal> disponibles = Personal.find.where().eq("tipo.id", 10).eq("activo","S")
        List<Personal> disponibles = Personal.find.where().eq("cuentas.roles.rol.id", 15).eq("activo","S")
                .and(
                        Expr.not(  Expr.in("id", arr1)),
                        Expr.not(  Expr.in("id", arr2))
                ).orderBy("nombre, paterno").findList();
        System.out.println("Locutores Disponibles    preagenda y agenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+disponibles.size()+"    ");
        disponibles.forEach(di-> System.out.println(di.nombreCompleto()));
        return ok(jsonContext.toJsonString(disponibles));
    }

    public static List<Object> auxLocutoresNoDisponiblesPreagenda(String d, String h){
        System.out.println("\n\n\nDesde UsuarioController.auxLocutoresNoDisponiblesPreagenda");
        String sql1 = "select t0.personal_id "
                + "from pre_agenda_locutor t0 "
                + "inner join pre_agenda t1 on t1.id = t0.preagenda_id "
                + "inner join folio_productor_asignado t2 on t2.id = t1.folioproductorasignado_id "
                + "inner join folio t3 on t3.id = t2.folio_id "
                + "where t1.estado_id in (4, 5 ) "
                +   "and t3.estado_id in (4,5,7) "
                +   "and ( "
                + "		('"+d+"' > t1.inicio  and '"+d+"' < t1.fin) "
                + "		or "
                + "		('"+h+"' > t1.inicio  and '"+h+"' < t1.fin) "
                + ") "
                +   "or ( "
                +   "   	('"+d+"' in (t1.inicio, t1.fin)  and '"+h+"' in (t1.inicio, t1.fin) )"
                + ") "
                + "or ( "
                + "	'"+d+"' < t1.inicio "
                + "	and "
                + "	'"+h+"'  > t1.fin "
                +   ")";
        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql1).columnMapping("t0.personal_id", "personal.id");
        return PreAgendaLocutor.find.setRawSql(rawB1.create()).findIds();
    }


    public static List<Object> auxLocutoresNoDisponiblesAgenda(String d, String h){
        System.out.println("\n\n\nDesde UsuarioController.auxLocutoresNoDisponiblesAgenda");
        System.out.println("\n\n\n\nd:"+d+"  h:"+h);
        String sql2 = "select t0.personal_id "
                + "from agenda_personal_rol t0 "
                + "inner join agenda t1 on t1.id = t0.agenda_id "
                + "inner join folio_productor_asignado t2 on t2.id = t1.folioproductorasignado_id "
                + "inner join folio t3 on t3.id = t2.folio_id "
                + "where t1.estado_id in (4, 5 ) "
                +   "and t3.estado_id in (4,5,7) "
                +   "and ( "
                + "		('"+d+"' > t1.inicio  and '"+d+"' < t1.fin) "
                + "		or "
                + "		('"+h+"' > t1.inicio  and '"+h+"' < t1.fin) "
                + ") "
                +   "or ( "
                +   "   	('"+d+"' in (t1.inicio, t1.fin)  and '"+h+"' in (t1.inicio, t1.fin) )"
                + ") "
                + "or ( "
                + "	'"+d+"' < t1.inicio "
                + "	and "
                + "	'"+h+"'  > t1.fin "
                +   ")";
        RawSqlBuilder rawB2 = RawSqlBuilder.parse(sql2).columnMapping("t0.personal_id", "personal.id");
        return PreAgendaLocutor.find.setRawSql(rawB2.create()).findIds();
    }


    public static List<Object> auxProductorNoDisponiblePreagenda(String d, String h, Long eventoId){
        String sql1 = "select distinct t2.personal_id "
                + "from pre_agenda t1 "
                + "inner join folio_productor_asignado t2 on t2.id = t1.folioproductorasignado_id and t1.id != "+eventoId+" "
                + "inner join personal p on t2.personal_id = p.id "
                + "inner join folio t3 on t3.id = t2.folio_id "
                + "where t1.estado_id in (4, 5 ) "
                + "and ("
                + "('"+d+"' < t1.inicio and	'"+h+"' < t1.fin and '"+h+"' > t1.inicio)"
                + " or "
                + "('"+d+"'= t1.inicio	and '"+h+"' = t1.fin )"
                + " or "
                + "('"+d+"' > t1.inicio and '"+h+"' > t1.fin and t1.fin < '"+h+"' and '"+d+"' < t1.fin )"
                + " or "
                + "('"+d+"' > t1.inicio and '"+h+"' < t1.fin)"
                + " or "
                + "('"+d+"' < t1.inicio and '"+h+"' > t1.fin)"
                + " or "
                + "('"+d+"' <= t1.inicio and '"+h+"' = t1.fin)"
                + " or "
                + "('"+d+"' = t1.inicio and '"+h+"' >= t1.fin)"
                + " or "
                + "('"+d+"' = t1.inicio and '"+h+"' < t1.fin )"
                + " or "
                + "('"+d+"' > t1.inicio and '"+h+"' = t1.fin )"
                + ")";
        System.out.println(sql1);
        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql1).columnMapping("t2.personal_id", "personal.id");
        List<Object> arr1 = PreAgenda.find.setRawSql(rawB1.create()).findIds();
        System.out.println(arr1.size());
        return arr1;
    }


    public static List<Object> auxProductorNoDisponibleAgenda(String d, String h, Long eventoId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            sdf.parse(d);
            sdf.parse(h);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String sql1 = "select distinct t2.personal_id "
                + "from agenda t1 "
                + "inner join folio_productor_asignado t2 on t2.id = t1.folioproductorasignado_id and t1.id != "+eventoId+" "
                + "inner join personal p on t2.personal_id = p.id "
                ///	+   "inner join personal_horario t4 on p.id = t4.personal_id "
                + "inner join folio t3 on t3.id = t2.folio_id "
                + "where t1.estado_id >= 7 "
                ///////			+   "and t3.estado_id  >= 7 "
                //	+   "and p.tipo_id = 2 "
                //	+   "and t1.id <> "+eventoId+" "
                ///	+   "and dayofweek('"+d+"') = t4.diasemana "
                + "and ("
                + "('"+d+"' < t1.inicio and	'"+h+"' < t1.fin and '"+h+"' > t1.inicio)"
                + " or "
                + "('"+d+"'= t1.inicio	and '"+h+"' = t1.fin )"
                + " or "
                + "('"+d+"' > t1.inicio	and '"+h+"' > t1.fin and t1.fin < '"+h+"' and '"+d+"' < t1.fin )"
                + " or "
                + "('"+d+"' > t1.inicio	and '"+h+"' < t1.fin)"
                + " or "
                + "('"+d+"' < t1.inicio	and '"+h+"' > t1.fin)"
                + " or "
                + "('"+d+"' <= t1.inicio and '"+h+"' = t1.fin)"
                + " or "
                + "('"+d+"' = t1.inicio	and '"+h+"' >= t1.fin)"
                + " or "
                + "('"+d+"' = t1.inicio and '"+h+"' < t1.fin )"
                + " or "
                + "('"+d+"' > t1.inicio and '"+h+"' = t1.fin )"
                + ")";
        System.out.println("query de auxProductorNoDisponibleAgenda: "+sql1);

        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql1).columnMapping("t2.personal_id", "cuentaRol.cuentarol.cuenta.personal.id");
        List<Object> arr1 = Agenda.find.setRawSql(rawB1.create()).findIds();
        System.out.println(arr1.size());
        return arr1;
    }

    // Checa si el operador de sala puede cubrir el horario del evento,
    // es decir si el horario del evento esta dentro del horario del operador
    public static List<Object> auxOperadorEnHorario(String d, String h, Long eventoId, Long salaId){
        //System.out.println("desde auxOperadorEnHorario  ");
        String sql="select t1.id ";
        sql+="from personal t1 ";
        sql+="inner join personal_horario t2 on t2.personal_id = t1.id ";
        sql+="inner join operador_sala t3 on t1.id = t3.personal_id and t3.sala_id = "+salaId+" ";
        sql+="where t1.activo ='S' ";
        //sql+="and DAYOFWEEK('"+d+"') = t2.diasemana ";
        sql+="and extract(isodow from date '"+d+"')+1 = t2.diasemana ";
        sql+="and(";
        sql+="  cast(replace('"+d+"','T',' ') as time) >= cast(t2.desde as time) ";
        sql+="  and ";
        sql+="  cast(replace('"+d+"','T',' ') as time) <= cast(t2.hasta as time) ";
        sql+=")";
        sql+="and(";
        sql+="  cast(replace('"+h+"','T',' ') as time) >= cast(t2.desde as time) ";
        sql+="  and ";
        sql+="  cast(replace('"+h+"','T',' ') as time) <= cast(t2.hasta as time) ";
        sql+=")";
        System.out.println("auxOperadorEnHorario  "+sql);
        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql).columnMapping("t1.id", "id");
        List<Object> arr1 = Personal.find.setRawSql(rawB1.create()).findIds();
        System.out.println("auxOperadorEnHorario: "+  arr1.size());
        return arr1;
    }


    // Esta función sustituye auxOperadorNoDisponiblePreagenda y auxOperadorNoDisponibleAgenda
    // Busca los operadores de sala no disponibles, es decir los que ya estan ocupados en la fecha y rango de tiempo indicados
    public static List<Object> auxOperadorNoDisponible(String d, String h, Long eventoId) {
        String sql = "select " +
                "id " +
                "from " +
                "personal p " +
                "where " +
                "p.id in ( " +
                "select " +
                "pasos.personal_id personalId " +
                "from " +
                "pre_agenda_sala pass " +
                "inner join pre_agenda pas on " +
                "pass.preagenda_id = pas.id " +
                "inner join pre_agenda_operador_sala pasos on " +
                "pas.id = pasos.preagenda_id " +
                "where " +
                "pas.estado_id = 5 " +
                "and (  " +
                "(cast(replace('"+d+"', 'T', ' ') as timestamp) > cast(pas.inicio as timestamp) " +
                "and  " +
                " cast(replace('"+d+"', 'T', ' ') as timestamp) < cast(pas.fin as timestamp) " +
                "or " +
                " cast(replace('"+h+"', 'T', ' ') as timestamp) > cast(pas.inicio as timestamp) " +
                "and cast(replace('"+h+"', 'T', ' ') as timestamp) < cast(pas.fin as timestamp)  " +
                ") " +
                "or ( " +
                "cast(pas.inicio as timestamp) > cast(replace('"+d+"', 'T', ' ') as timestamp) " +
                "and  " +
                "cast(pas.inicio as timestamp) < cast(replace('"+h+"', 'T', ' ') as timestamp) " +
                "or " +
                "cast(pas.fin as timestamp) > cast(replace('"+d+"', 'T', ' ') as timestamp) " +
                "and  " +
                "cast(pas.fin as timestamp) < cast(replace('"+h+"', 'T', ' ') as timestamp)  " +
                ")  " +
                ")  " +
                "union " +
                "select " +
                "asos.personal_id personalId " +
                "from " +
                "agenda_sala ass " +
                "inner join agenda ags on " +
                "ass.agenda_id = ags.id " +
                "inner join agenda_operador_sala asos on " +
                "ags.id = asos.agenda_id " +
                "where " +
                "ags.estado_id >= 7 " +
                "and (  " +
                "(cast(replace('"+d+"', 'T', ' ') as timestamp) > cast(ags.inicio as timestamp) " +
                "and  " +
                " cast(replace('"+d+"', 'T', ' ') as timestamp) < cast(ags.fin as timestamp) " +
                "or " +
                " cast(replace('"+h+"', 'T', ' ') as timestamp) > cast(ags.inicio as timestamp) " +
                "and cast(replace('"+h+"', 'T', ' ') as timestamp) < cast(ags.fin as timestamp)  " +
                ") " +
                "or ( " +
                "cast(ags.inicio as timestamp) > cast(replace('"+d+"', 'T', ' ') as timestamp) " +
                "and  " +
                "cast(ags.inicio as timestamp) < cast(replace('"+h+"', 'T', ' ') as timestamp) " +
                "or " +
                "cast(ags.fin as timestamp) > cast(replace('"+d+"', 'T', ' ') as timestamp) " +
                "and  " +
                "cast(ags.fin as timestamp) < cast(replace('"+h+"', 'T', ' ') as timestamp)  " +
                ")  " +
                ")  " +
                ")";
        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql).columnMapping("id", "id");
        List<Object> arr1 = Agenda.find.setRawSql(rawB1.create()).findIds();
        System.out.println("\n\nauxOperadorNoDisponible  sql***:"+sql);
        System.out.println("\n\nauxOperadorNoDisponible  size***:"+arr1.size());
        return arr1;
    }

    public static Result ajaxEstadoSala(){
        System.out.println("Desde UsuarioController.ajaxEstadoSala");
        JsonNode json = request().body().asJson();
        System.out.println("recibido en ajaxEstadoSala: "+json);
        JSONObject jo = new JSONObject();

        String desde = json.findValue("desde").asText();
        String hasta = json.findValue("hasta").asText();
        Long eventoId = json.findValue("eventoId").asLong();
        Long salaId = json.findValue("salaId").asLong();
        String operadorId = json.findValue("operadorId").asText();
        // En horario?
        List<Object> enHorario = auxOperadorEnHorario(desde, hasta, eventoId, salaId);
        //Operadores no disponibles
        List<Object> operdoresNoDispibles = auxOperadorNoDisponible(desde, hasta, 0L);

        System.out.println("operadorId:"+operadorId);
        for (Object op : operdoresNoDispibles) {
            System.out.println("operador->" + op);
        }

        try {
            List<Object> aux = operdoresNoDispibles.stream().filter(p -> p.toString().equals( operadorId)).collect(Collectors.toList());
            jo.put("enHorarioJornada", enHorario.size() > 0);
            System.out.println("disponible filtro size" +aux.size());
            System.out.println("disponible filtro size==0 "+(aux.size()==0));
            jo.put("operadorDisponible", aux.size()==0);

        }catch (JSONException e){
            System.out.println(ColorConsola.ANSI_RED+"ocurrió un error de ajax "+e+ColorConsola.ANSI_RESET);
        }
        System.out.println(  json.findValue("") );
        return ok(jo.toString());
    }


    public static Result ajaxRDisenioDisponibles(){
        System.out.println("Buscando responsables de diseño disponibles......................................");
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println("REcibido en  ajaxRDisenioDisponibles......................................"+json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n\n\nd:"+d+"  h:"+h);
        System.out.println("d:"+sdf.format(d)+"   h:"+sdf.format(h)+"\n\n");

        // Buscar los responsables de diseño ocupados en fecha y hora en agenda

        String sql1 ="select p.id "
                + "from personal p "
                + "	inner join folio_productor_asignado fpa "
                + "	inner join folio f on f.id = fpa.folio_id "
                + "	inner join agenda as2 on as2.folioproductorasignado_id = fpa.id "
                + "	inner join agenda_cuenta_rol aspr on aspr.agenda_id =as2.id "
                + "where fpa.folio_id = f.id and fpa.personal_id = p.id 	"
                + "	and as2.estado_id =7 "
                + "	and f.estado_id =4 "
                + "	and 	 (  ('"+sdf.format(d)+"' > as2.inicio  and '"+sdf.format(d)+"' < as2.fin) 		or      ('"+sdf.format(h)+"' > as2.inicio  and '"+sdf.format(h)+"' < as2.fin)   )   "
                + "	or ( 	    ('"+sdf.format(d)+"' in (as2.inicio, as2.fin)  and '"+sdf.format(h)+"' in (as2.inicio, as2.fin) )  )  "
                + "	or ( 	'"+sdf.format(d)+"' < as2.inicio 	and 	'"+sdf.format(h)+"'  > as2.fin )";
        System.out.println("\n\n\n\n\nBuscar al personal ocupados en fecha y hora en agenda "+sql1);
        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql1).columnMapping("p.id", "id");
        List<Object> noDisponibles = Personal.find.setRawSql(rawB1.create()).findIds();
        System.out.println("No disponibles    agenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+noDisponibles.size());
        // Responsable de diseño
        List<Personal> disponibles = Personal.find.where().eq("cuentas.roles.rol.id", 12).eq("activo","S")
                .not(Expr.in("id", noDisponibles))
                .orderBy("nombre, paterno").findList();
        return ok(jsonContext.toJsonString(disponibles));
    }


    // Busca que personal esta ocupado (productores, locutores, operadores de sala e ing. admon. e y a, ) entre las fechas <desde> y <hasta>
    // Regresa JSON
    public static Result previoAgendar() throws JSONException, ParseException {
        System.out.println("\n\n\nDesde previoAgendar");

        JsonNode json = request().body().asJson();
        System.out.println("Recibido en  previoAgendar......................................"+json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String strd =  json.findPath("inicio").asText();
        String strh =  json.findPath("fin").asText();
        String resource = json.findPath("resourceid").asText();
        //Busca al personal que esta ocupado (productores, locutores, operadores de sala e ing. admon. e y a, ) entre las fechas <desde> y <hasta>
        String sql1=
                // Preagenda - productor
                "select " +
                        "fpa.personal_id as id, '100' as rolid, 'productor' as rolpersonal, 'P' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, pas.id as eventoid " +
                        "from " +
                        "pre_agenda pas " +
                        "inner join folio_productor_asignado fpa on fpa.id = pas.folioproductorasignado_id " +
                        "inner join personal p2 on p2.id = fpa.personal_id  " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+
                        /*
                        "(inicio between '"+strd+"' and '"+strh+"' " +
                        "or fin between '"+strd+"' and '"+strh+"' " +
                        "or '"+strd+"' between inicio and fin " +
                        "or '"+strh+"' between inicio and fin) " +
                         */
                        "and estado_id in (4,5) " +
                        "union " +
                        // Preagenda - ingeniero Admon. Equipo y Accesorios
                        "select " +
                        "pasiae.ingeniero_id as id, " +
                        "'10' as rolid, " +
                        "'ingeniero admin' as rolpersonal, " +
                        "'P' as tipo, " +
                        "concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, " +
                        "pas.id as eventoid " +
                        "from " +
                        "pre_agenda pas " +
                        "inner join pre_agenda_ing_admon_eqpo pasiae on pasiae.preagenda_id = pas.id " +
                        "inner join personal p2 on p2.id = pasiae.ingeniero_id  " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+
                        /*
                        "(inicio between '"+strd+"' and '"+strh+"' " +
                        "or fin between '"+strd+"' and '"+strh+"' " +
                        "or '"+strd+"' between inicio and fin " +
                        "or '"+strh+"' between inicio and fin) " +
                         */
                        "and estado_id in (4,5) " +
                        "union " +
                        // Preagenda - operador de sala
                        "select pasos.personal_id as id, '16' as rolid, 'Operador de sala' as rolpersonal, 'P' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, pas.id as eventoid " +
                        "from " +
                        "pre_agenda pas " +
                        "inner join pre_agenda_operador_sala pasos on pasos.preagenda_id = pas.id " +
                        "inner join personal p2 on p2.id = pasos.personal_id  " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+
                        /*
                        "(inicio between '"+strd+"' and '"+strh+"' " +
                        "or fin between '"+strd+"' and '"+strh+"' " +
                        "or '"+strd+"' between inicio and fin " +
                        "or '"+strh+"' between inicio and fin) " +
                         */
                        "and estado_id in (4,5) " +
                        "union " +
                        // Preagenda - locutor
                        "select pasl.personal_id as id, '15' as rolid, 'Locutor' as rolpersonal, 'P' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, pas.id as eventoid " +
                        "from " +
                        "pre_agenda pas " +
                        "inner join pre_agenda_locutor pasl on pasl.preagenda_id = pas.id " +
                        "inner join personal p2 on p2.id = pasl.personal_id   " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+
                        /*
                        "(inicio between '"+strd+"' and '"+strh+"' " +
                        "or fin between '"+strd+"' and '"+strh+"' " +
                        "or '"+strd+"' between inicio and fin " +
                        "or '"+strh+"' between inicio and fin) " +
                         */
                        "and estado_id in (4,5) " +
                        "union " +
                        // Agenda - productor
                        "select  fpa.personal_id as id, '100' as rolid, 'productor' as rolpersonal, 'A' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, a.id as eventoid " +
                        "from " +
                        "agenda a " +
                        "inner join folio_productor_asignado fpa on fpa.id = a.folioproductorasignado_id " +
                        "inner join personal p2 on p2.id = fpa.personal_id   " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+
                        /*
                        "(inicio between '"+strd+"' and '"+strh+"' " +
                        "or fin between '"+strd+"' and '"+strh+"' " +
                        "or '"+strd+"' between inicio and fin " +
                        "or '"+strh+"' between inicio and fin) " +
                         */
                        "and estado_id in (7, 8) " +
                        "union " +
                        // Agenda - Ingeniero Admon. Equipo y Accesorios
                        "select asiae.ingeniero_id as id, '10' as rolid, 'ingeniero admin' as rolpersonal, 'A' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, a.id as eventoid " +
                        "from " +
                        "agenda a " +
                        "inner join agenda_ing_admon_eqpo asiae on asiae.agenda_id = a.id " +
                        "inner join personal p2 on p2.id = asiae.ingeniero_id  " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+

                        "and estado_id in (7, 8) " +
                        "union " +
                        // Agenda - operador de sala
                        "select aos.personal_id as id, '16' as rolid, 'Operador de sala' as rolpersonal, 'A' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, a.id as eventoid " +
                        "from " +
                        "agenda a " +
                        "inner join agenda_operador_sala aos on aos.agenda_id = a.id " +
                        "inner join personal p2 on p2.id = aos.personal_id   " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+

                        "and estado_id in (7, 8) " +
                        "union " +
                        // Agenda - locutor
                        "select p.id as id, '15' as rolid, 'Locutor' as rolpersonal, 'A' as tipo, concat (p.nombre, ' ', p.paterno ,' ',p.materno) as nombre, a.id as eventoid " +
                        "from " +
                        "agenda a " +
                        "inner join agenda_cuenta_rol acr on acr.agenda_id = a.id " +
                        "inner join cuenta_rol cr on cr.id = acr.cuentarol_id and cr.rol_id = 15 " +
                        "inner join cuenta c on c.id = cr.cuenta_id " +
                        "inner join personal p on p.id = c.personal_id " +
                        "where " +
                        "(inicio > '"+strd+"' and inicio < '"+strh +"' "+
                        "or "+
                        "fin > '"+strd+"' and fin < '"+strh+"' ) "+

                        "and estado_id in (7, 8)";


        Logger.debug("sql1:    "+sql1);

        Calendar cal = Calendar.getInstance();
        cal.setTime(  sdf.parse(strd)   );

        //System.out.println("sql1: "+sql1);
        List<SqlRow> rows =  Ebean.createSqlQuery(sql1).findList();

        //System.out.println("=====>resource "+resource);


        String resource0 = resource.substring(0,1);
        long salaid = 0L;
        Long operadorid = null;
        Personal operador = null;
        Sala sala = null;
        //System.out.println("=====>resource0 "+resource0);

        switch (resource0){
            case "a":
            case "b":
            case "h": salaid = 0L; break;
            case "c": salaid=1L; operadorid = Long.parseLong(resource.substring(1,resource.length()));   break;
            case "d": salaid=3L; operadorid = Long.parseLong(resource.substring(1,resource.length()));   break;
            case "e": salaid=2L; operadorid = Long.parseLong(resource.substring(1,resource.length()));   break;
            case "f": salaid=7L; operadorid = Long.parseLong(resource.substring(1,resource.length()));   break;
            case "g": salaid=4L; operadorid = Long.parseLong(resource.substring(1,resource.length()));   break;
        }

        //System.out.println("=====>operadorid "+operadorid);


        if (salaid!=0){
            operador = Personal.find.ref(operadorid);
            sala = Sala.find.ref(salaid);
        }
        boolean sePuedeAgendar=true;

        JSONObject retorno = new JSONObject();
        JSONArray arrMensajes = new JSONArray();

        if (operador!=null ) {
            for (SqlRow r : rows) {
                System.out.println("      operador "+ r.getLong("id") +"  "+operador.id);
                if (r.getLong("id")==operador.id){
                    sePuedeAgendar = false;
                   // mensaje = operador.nombreCompleto()+" que opera la "+sala.descripcion+" tiene otra actividad dentro de ese horario.";
                    arrMensajes.put(operador.nombreCompleto()+" que opera la "+sala.descripcion+" tiene otra actividad dentro de ese horario.");
                }
            }
        }

        //System.out.println("=====>sePuedeAgendar "+sePuedeAgendar);

        System.out.println("el día "+strd +" es "+cal.get(Calendar.DAY_OF_WEEK));
        System.out.println("resource "+resource);



        // Solo si el evento es grabacion portatil,  es posible agendarlo en días no laborables (sábado o domingo)
        if (!resource.equals("b") && ((cal.get(Calendar.DAY_OF_WEEK) == 7) || (cal.get(Calendar.DAY_OF_WEEK) == 1))){
            sePuedeAgendar=false;
            arrMensajes.put("Solo las grabaciones portátiles se pueden agendar en sábado o domingo.");
        }
        retorno.put("disponible", sePuedeAgendar);
        retorno.put("mensajes", arrMensajes);
        return ok( retorno.toString()  );
    }





    // Busca que personal esta ocupado (productores, locutores, operadores de sala e ing. admon. e y a, ) entre las fechas <desde> y <hasta>
    //Regresa JSON
    public static Result ajaxPersonalNoDisponible() throws JSONException {
        JsonNode json = request().body().asJson();
        System.out.println("Recibido en  ajaxPersonalNoDisponible......................................"+json);
        String strd =  json.findPath("inicio").asText();
        String strh =  json.findPath("fin").asText();
        String sql1=
                // Preagenda - productor
                "select " +
                "fpa.personal_id as id, '100' as rolid, 'productor' as rolpersonal, 'P' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, pas.id as eventoid " +
                "from " +
                "pre_agenda pas " +
                "inner join folio_productor_asignado fpa on fpa.id = pas.folioproductorasignado_id " +
                "inner join personal p2 on p2.id = fpa.personal_id  " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (4,5) " +
                "union " +
                // Preagenda - ingeniero Admon. Equipo y Accesorios
                "select " +
                "pasiae.ingeniero_id as id, " +
                "'10' as rolid, " +
                "'ingeniero admin' as rolpersonal, " +
                "'P' as tipo, " +
                "concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, " +
                "pas.id as eventoid " +
                "from " +
                "pre_agenda pas " +
                "inner join pre_agenda_ing_admon_eqpo pasiae on pasiae.preagenda_id = pas.id " +
                "inner join personal p2 on p2.id = pasiae.ingeniero_id  " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (4,5) " +
                "union " +
                // Preagenda - operador de sala
                "select pasos.personal_id as id, '16' as rolid, 'Operador de sala' as rolpersonal, 'P' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, pas.id as eventoid " +
                "from " +
                "pre_agenda pas " +
                "inner join pre_agenda_operador_sala pasos on pasos.preagenda_id = pas.id " +
                "inner join personal p2 on p2.id = pasos.personal_id  " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (4,5) " +
                "union " +
                // Preagenda - locutor
                "select pasl.personal_id as id, '15' as rolid, 'Locutor' as rolpersonal, 'P' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, pas.id as eventoid " +
                "from " +
                "pre_agenda pas " +
                "inner join pre_agenda_locutor pasl on pasl.preagenda_id = pas.id " +
                "inner join personal p2 on p2.id = pasl.personal_id   " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (4,5) " +
                "union " +
                // Agenda - productor
                "select  fpa.personal_id as id, '100' as rolid, 'productor' as rolpersonal, 'A' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, a.id as eventoid " +
                "from " +
                "agenda a " +
                "inner join folio_productor_asignado fpa on fpa.id = a.folioproductorasignado_id " +
                "inner join personal p2 on p2.id = fpa.personal_id   " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (7, 8) " +
                "union " +
                // Agenda - Ingeniero Admon. Equipo y Accesorios
                "select asiae.ingeniero_id as id, '10' as rolid, 'ingeniero admin' as rolpersonal, 'A' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, a.id as eventoid " +
                "from " +
                "agenda a " +
                "inner join agenda_ing_admon_eqpo asiae on asiae.agenda_id = a.id " +
                "inner join personal p2 on p2.id = asiae.ingeniero_id  " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (7, 8) " +
                "union " +
                // Agenda - operador de sala
                "select aos.personal_id as id, '16' as rolid, 'Operador de sala' as rolpersonal, 'A' as tipo, concat (p2.nombre, ' ', p2.paterno ,' ',p2.materno) as nombre, a.id as eventoid " +
                "from " +
                "agenda a " +
                "inner join agenda_operador_sala aos on aos.agenda_id = a.id " +
                "inner join personal p2 on p2.id = aos.personal_id   " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (7, 8) " +
                "union " +
                // Agenda - locutor
                "select " +
                "p.id as id, " +
                "'15' as rolid, " +
                "'Locutor' as rolpersonal, " +
                "'A' as tipo, " +
                "concat (p.nombre, ' ', p.paterno ,' ',p.materno) as nombre, " +
                "a.id as eventoid " +
                "from " +
                "agenda a " +
                "inner join agenda_cuenta_rol acr on acr.agenda_id = a.id " +
                "inner join cuenta_rol cr on cr.id = acr.cuentarol_id and cr.rol_id = 15 " +
                "inner join cuenta c on c.id = cr.cuenta_id " +
                "inner join personal p on p.id = c.personal_id " +
                "where " +
                "(inicio between '"+strd+"' and '"+strh+"' " +
                "or fin between '"+strd+"' and '"+strh+"' " +
                "or '"+strd+"' between inicio and fin " +
                "or '"+strh+"' between inicio and fin) " +
                "and estado_id in (7, 8)";

        System.out.println("sql1: "+sql1);
        List<SqlRow> rows =  Ebean.createSqlQuery(sql1).findList();
        JSONObject joRetorno = new JSONObject();
        JSONArray ja = new JSONArray();
        for ( SqlRow r : rows ){
            JSONObject jo = new JSONObject();
            jo.put("id", r.getLong("id"));
            jo.put("rolid", r.getString("rolid"));
            jo.put("rol", r.getString("rolpersonal"));
            jo.put("tipo", r.getString("tipo"));
            jo.put("nombre", r.getString("nombre"));
            jo.put("eventoid", r.getString("eventoid"));
            ja.put(jo);
        }
        joRetorno.put("personalNoDisponible", ja);
        System.out.println(  ColorConsola.ANSI_PURPLE+ joRetorno +ColorConsola.ANSI_RESET);
        return ok( joRetorno.toString());
    }


    // Busca que personal esta disponible entre las fechas <desde> y <hasta>
    //Recibe parametros JSON desde, hasta, rolId, tipo
    public static Result ajaxPersonalDisponible() {
        Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println("Recibido en ajaxPersonalDisponible......................................"+json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date desde = null;
        Date hasta = null;
        long fase = 0L;
        try {
            desde = sdf.parse(json.findPath("desde").asText());
            hasta = sdf.parse(json.findPath("hasta").asText());
            fase = json.findValue("fase").asLong();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sql1="select distinct p.id "
                + "from personal p "
                + "	inner join folio_productor_asignado fpa on fpa.personal_id = p.id "
                + "	inner join folio f on f.id = fpa.folio_id "
                + "	inner join agenda as2 on as2.folioproductorasignado_id = fpa.id "
                + "	inner join agenda_cuenta_rol aspr on aspr.agenda_id =as2.id "
                + "where fpa.folio_id = f.id and fpa.personal_id = p.id 	"
                + "	and as2.estado_id =7 "
                + "	and f.estado_id =4 "
                + "	and 	 (  ('"+sdf.format(desde)+"' > as2.inicio  and '"+sdf.format(desde)+"' < as2.fin) 		or      ('"+sdf.format(hasta)+"' > as2.inicio  and '"+sdf.format(hasta)+"' < as2.fin)   )   ";

        //	+ "	and 	 (  ( as2.inicio between '"+sdf.format(desde)+"' and '"+sdf.format(hasta)+"') or ( as2.fin between '"+sdf.format(desde)+"' and '"+sdf.format(hasta)+"'))";
                    /*
					+ "	and 	 (  ('"+sdf.format(desde)+"' > as2.inicio  and '"+sdf.format(desde)+"' < as2.fin) 		or      ('"+sdf.format(hasta)+"' > as2.inicio  and '"+sdf.format(hasta)+"' < as2.fin)   )   "
					+ "	or ( 	    ('"+sdf.format(desde)+"' in (as2.inicio, as2.fin)  and '"+sdf.format(hasta)+"' in (as2.inicio, as2.fin) )  )  "
					+ "	or ( 	'"+sdf.format(desde)+"' < as2.inicio 	and 	'"+sdf.format(hasta)+"'  > as2.fin )";
		*/
        System.out.println("query personal ocupado agenda: "+sql1);
        String sql2="select distinct p.id "
                + "from personal p "
                + "	inner join folio_productor_asignado fpa on fpa.personal_id = p.id "
                + "	inner join folio f on f.id = fpa.folio_id "
                + "	inner join pre_agenda pas on pas.folioproductorasignado_id = fpa.id "
                + "	inner join pre_agenda_rol pasr on pasr.preagenda_id =pas.id "
                + "where fpa.folio_id = f.id and fpa.personal_id = p.id 	"
                + "	and pas.estado_id =5 "
                + "	and f.estado_id =4 "
//				+ "	and 	 (  ( pas.inicio between '"+sdf.format(desde)+"' and '"+sdf.format(hasta)+"') or ( pas.fin between '"+sdf.format(desde)+"' and '"+sdf.format(hasta)+"'))";

                + "	and 	 (  ('"+sdf.format(desde)+"' > pas.inicio  and '"+sdf.format(desde)+"' < pas.fin) 		or      ('"+sdf.format(hasta)+"' > pas.inicio  and '"+sdf.format(hasta)+"' < pas.fin)   )   ";


        System.out.println("query personal ocupado preagenda sql1: "+sql1);
        System.out.println("query personal ocupado preagenda sql2: "+sql2);


        RawSqlBuilder rawB1 = RawSqlBuilder.parse(sql1).columnMapping("p.id", "id");
        RawSqlBuilder rawB2 = RawSqlBuilder.parse(sql2).columnMapping("p.id", "id");

        List<Object> noDisponibles = Personal.find.setRawSql(rawB1.create()).findIds();
        List<Object> noDisponibles2 = Personal.find.setRawSql(rawB2.create()).findIds();


        System.out.println("\nPersonal NO disponible en preagenda (sin importar su rol) "+noDisponibles2.size());
        System.out.println("\nPersonal NO disponible en agenda (sin importar su rol) "+noDisponibles.size());
        // Roles por fase
        List<RolFase> x = RolFase.find.where().eq("fase.id", fase).findList();
        List<Long> roles = new ArrayList<Long>();
        for (  RolFase r: x  ) {
            roles.add(r.rol.id);
        }

        for ( Long rr:roles  ) {
            System.out.println("rol de la fase "+rr);
        }

        Query<Personal> d45 = Personal.find
                .fetch("cuentas.roles.rol")
                .where()
                .eq("activo","S")
                .not(Expr.in("id", noDisponibles))
                .not(Expr.in("id", noDisponibles2))
                //	.filterMany("cuentas.roles.rol").in("cuentas.roles.rol.id", roles)
                .orderBy("nombre, paterno, materno");
        System.out.println("\n\n\nPersonal disponible (sin importar su rol) "+d45.findList().size());
        new ArrayList<Personal>();
        List<PersonalDisponible> pd = new ArrayList<>();

        StringBuilder cadena= new StringBuilder("[");

        for ( Personal p:d45.findList() ) {
            for ( CuentaRol r : p.cuentas.get(0).roles) {
                if ( roles.contains(r.rol.id) ) {
                    PersonalDisponible p1 = new PersonalDisponible();
                    p1.nombreCompleto = p.nombreCompleto();
                    p1.personalId = p.id;
                    p1.rol = r.rol.descripcion;
                    p1.rolId = r.rol.id;
                    pd.add(p1);
//                    cadena+="{\"nombreCompleto\":\""+p.nombreCompleto()+"\",  \"personalId\":"+p.id+",   \"rol\":\""+r.rol.descripcion+"\",  \"rolId\":"+r.rol.id+", \"cuentaRolId\":"+r.id+"},";
                    cadena.append("{\"nombreCompleto\":\"").append(p.nombreCompleto()).append("\",  \"personalId\":").append(p.id).append(",   \"rol\":\"").append(r.rol.descripcion).append("\",  \"rolId\":").append(r.rol.id).append(", \"cuentaRolId\":").append(r.id).append("},");
                }
            }
        }
        if (cadena.toString().length()>1)
            cadena = new StringBuilder(cadena.toString().substring(0, cadena.toString().length() - 1));
        cadena.append("]");
        System.out.println("cadena ->"+cadena.toString());
        return ok(cadena.toString());
    }

    public static Result ajaxEquiposDisponibles(){
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Buscando equipos disponibles entre "+d+" y "+h);
        // Buscar los equipos  no disponibles en preagenda
        List<PreAgendaEquipo> equiposNoDispPreAgenda = PreAgendaEquipo.find
                .where("((preagenda.inicio > '"+sdf.format(d)+"' and preagenda.inicio < '"+sdf.format(h)+"' )  or (preagenda.fin > '"+sdf.format(d)+"'  and preagenda.fin < '"+sdf.format(h)+"' ) ) or (preagenda.inicio = '"+sdf.format(d)+"'  or preagenda.fin = '"+sdf.format(h)+"' )")

                /*
				 .or(
						 Expr.and(
								 Expr.ge("preagenda.inicio", d), Expr.le("preagenda.inicio", h)
						 ), 
						 Expr.and(
								 Expr.ge("preagenda.fin", d), Expr.le("preagenda.fin", h)
						 )
				 )
				 */
                .findList();


        List<Long> arrEq1 = equiposNoDispPreAgenda.stream().map(m->m.equipo.id).collect(Collectors.toList());

        System.out.println("Equipos no disponibles en preagenda "+arrEq1.size());
        arrEq1.forEach(ndpa-> System.out.println("<<"+ndpa+">> " ));


        // Buscar los equipos no disponibles agenda

        List<AgendaEquipo> equiposNoDispAgenda = AgendaEquipo.find
                .where("((agenda.inicio > '"+sdf.format(d)+"' and agenda.inicio < '"+sdf.format(h)+"' )  or (agenda.fin > '"+sdf.format(d)+"'  and agenda.fin < '"+sdf.format(h)+"' ) ) or (agenda.inicio = '"+sdf.format(d)+"'  or agenda.fin = '"+sdf.format(h)+"' )")
                /*
                    .or(
                            Expr.and(
                                    Expr.ge("agenda.inicio", d), Expr.le("agenda.inicio", h)
                            ),
                            Expr.and(
                                    Expr.ge("agenda.fin", d), Expr.le("agenda.fin", h)
                            )
                    )
                    */
                .findList();


        List<Long> arrEq2 = equiposNoDispAgenda.stream().map(m-> m.equipo.id).collect(Collectors.toList());

        System.out.println("Equipos no disponibles en agenda "+arrEq2.size());
arrEq2.forEach(nda-> System.out.println("<<"+nda+">> " ));

        List<Equipo> disponibles = Equipo.find.where().in("estado.id", Arrays.asList(1,2)).and(
                Expr.not(  Expr.in("id", arrEq1)),
                Expr.not(  Expr.in("id", arrEq2))
        ).orderBy("descripcion").findList();


        System.out.println("Equipos DISPONIBLES "+disponibles.size());

        return ok(jsonContext.toJsonString(disponibles));


    }


    public static Result ajaxAccesoriosDisponibles(){
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Buscar los accesorios  no disponibles en preagenda
        List<PreAgendaAccesorio> accesoriosNoDispPreAgenda = PreAgendaAccesorio.find
                .where("((preagenda.inicio > '"+sdf.format(d)+"' and preagenda.inicio < '"+sdf.format(h)+"' )  or (preagenda.fin > '"+sdf.format(d)+"'  and preagenda.fin < '"+sdf.format(h)+"' ) ) or (preagenda.inicio = '"+sdf.format(d)+"'  or preagenda.fin = '"+sdf.format(h)+"' )")
                .findList();
        List<Long> arr1 = accesoriosNoDispPreAgenda.stream().map(m->m.accesorio.id).collect(Collectors.toList());

        // Buscar los accesorios no disponibles agenda
        List<AgendaAccesorio> accesoriosNoDispAgenda = AgendaAccesorio.find
                .where("((agenda.inicio > '"+sdf.format(d)+"' and agenda.inicio < '"+sdf.format(h)+"' )  or (agenda.fin > '"+sdf.format(d)+"'  and agenda.fin < '"+sdf.format(h)+"' ) ) or (agenda.inicio = '"+sdf.format(d)+"'  or agenda.fin = '"+sdf.format(h)+"' )")
                .findList();

        List<Long> arr2 = accesoriosNoDispAgenda.stream().map(m-> m.accesorio.id).collect(Collectors.toList());

        List<Accesorio> disponibles = Accesorio.find.where().in("estado.id", Arrays.asList(1,2)).and(
                Expr.not(  Expr.in("id", arr1)),
                Expr.not(  Expr.in("id", arr2))
        ).orderBy("descripcion").findList();

        return ok(jsonContext.toJsonString(disponibles));
    }


    public static Result ajaxVehiculosDisponibles(){
        System.out.println("........desde ajaxVehiculosDisponibles");
        System.out.println("........"+request().body());
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Buscar los vehiculos ocupados en fecha y hora en agenda
        List<AgendaVehiculo> vehiculosNoDispAgenda = AgendaVehiculo.find
                .where("((agenda.inicio > '"+sdf.format(d)+"' and agenda.inicio < '"+sdf.format(h)+"' )  or (agenda.fin > '"+sdf.format(d)+"'  and agenda.fin < '"+sdf.format(h)+"' ) ) or (agenda.inicio = '"+sdf.format(d)+"'  or agenda.fin = '"+sdf.format(h)+"' )")
                .findList();

        List<Long> arr2 = vehiculosNoDispAgenda.stream().map(m-> m.vehiculo.id).collect(Collectors.toList());
        List<Vehiculo> disponibles = Vehiculo.find.setDistinct(true)
                .where()
                .eq("activo", "S")
                .not(    Expr.in("id", arr2))
                .findList();
        return ok(jsonContext.toJsonString(disponibles));
    }


    public static Result ajaxPersonalDisponibles() throws JSONException{
        System.out.println("........desde ajaxPersonalDisponibles");
        Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println(json);
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Buscar personal ocupado en preagenda (productores, locutores, operadores de sala)
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

        ExpressionList<PreAgenda> el = PreAgenda.find
                .select("folioproductorasignado.personal.id, ingsAdmonEqpo.ingeniero.id, operadoresSala.personal.id")
                .setDistinct(true)

                .fetch("folioproductorasignado")
                .fetch("ingsAdmonEqpo")
                //.fetch("folioproductorasignado.personal")
                .fetch("operadoresSala")
                //.fetch("operadoresSala.personal")


                .where( "inicio between '"+sdf.format(d)+"' and '"+sdf.format(h)+"' or fin between '"+sdf.format(d)+"' and '"+sdf.format(h)+"' or '"+sdf.format(d)+"' between inicio and fin or '"+sdf.format(h)+"' between inicio and fin")
                .where();
/*
        List<PreAgenda> preOcupados = PreAgenda.find
                .select("folioproductorasignado.personal.id")
                .setDistinct(true)
                .fetch("folioproductorasignado")
                .fetch("folioproductorasignado.personal")
                .fetch("operadoresSala")
                .fetch("operadoresSala.personal")
                .where( "inicio between '"+sdf.format(d)+"' and '"+sdf.format(h)+"' or fin between '"+sdf.format(d)+"' and '"+sdf.format(h)+"' or '"+sdf.format(d)+"' between inicio and fin or '"+sdf.format(h)+"' between inicio and fin")
                .findList();
                  //.stream().map(m-> m.folioproductorasignado.personal.id).collect(Collectors.toList());

*/



        el.findList().forEach(x->System.out.println("perosnal.id ocupado ->"+x.folioproductorasignado.personal.id));
        System.out.println("->query<- "+el.query().getGeneratedSql());
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");








        // Buscar personal ocupado en fecha y hora en agenda
        List<AgendaCuentaRol> personalNoDispAgenda = AgendaCuentaRol.find
                .where("((agenda.inicio > '"+sdf.format(d)+"' and agenda.inicio < '"+sdf.format(h)+"' )  or (agenda.fin > '"+sdf.format(d)+"'  and agenda.fin < '"+sdf.format(h)+"' ) ) or (agenda.inicio = '"+sdf.format(d)+"'  or agenda.fin = '"+sdf.format(h)+"' )")
                .findList();

        List<Long> arr2 = personalNoDispAgenda.stream().map(m-> m.cuentarol.cuenta.personal.id).collect(Collectors.toList());
        List<RolFase> x = RolFase.find.where().eq("fase.id", json.findPath("fase").asLong()).findList();

        // Locutores ocupados  en fecha y hora en agenda
        List<Long> mapa = x.stream().map(m->m.rol.id).collect(Collectors.toList());

        System.out.println("  mapa   "+mapa);


        // hacer select en la tabla de roles (cuentaRol)

        List<CuentaRol> disp = CuentaRol.find
                .fetch("cuenta")
                .fetch("cuenta.personal")
                .where()
                .not (  Expr.in("cuenta.personal.id", arr2)  )
                .in("rol.id", mapa)
                .orderBy("cuenta.personal.nombre, cuenta.personal.paterno, cuenta.personal.materno")
                .findList();

        System.out.println("\n\ntam disp: "+disp.size()+"\n\n");
        disp.forEach(t-> System.out.println("Personal Disp::  "+  "  "+ t.cuenta.personal.nombreCompleto()+" "+t.rol.descripcion));
        List<Personal> disponibles = Personal.find.fetch("cuentas.roles.rol").setDistinct(true).where()
                .not(    Expr.in("id", arr2))
                .eq("activo", "S")
                .in("cuentas.roles.rol.id", mapa)
                .orderBy("nombre, paterno, materno")
                .findList();
        System.out.println("\n\ntam disponibles: "+disponibles.size()+"\n\n");

        disponibles.forEach(t-> System.out.println("Personal Disponible::  "+  "  "+ t.paterno+" "+t.materno+" "+t.nombre));
        JSONArray ja = new JSONArray();
        disp.forEach(q->{
            JSONObject jo = new JSONObject();
            try{
                jo.put("id",  String.valueOf(q.id));
                jo.put("id",  String.valueOf(q.cuenta.personal.id));
                jo.put("nombre", q.cuenta.personal.nombreCompleto());
                jo.put("rolId", q.rol.id);
                jo.put("rolDescripcion", q.rol.descripcion);
                ja.put(jo);
            } catch (JSONException e) {
                System.out.println("Excepción "+e);
            } catch (Exception e2) {
                System.out.println("Excepción JSON "+e2);
            }
        });

        System.out.println(  ja   );
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            jsonValues.add(ja.getJSONObject(i));
        }
        //Collections.sort( jsonValues, new Comparator<JSONObject>() {
        jsonValues.sort(new Comparator<JSONObject>() {
            private static final String KEY_NAME = "rolDescripcion";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    valA = a.get(KEY_NAME) + " " + a.get("nombre");
                    valB = b.get(KEY_NAME) + " " + b.get("nombre");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return valA.compareTo(valB);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < ja.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        System.out.println("\n\nsortedJsonArray");
        System.out.println("\n\n"+sortedJsonArray);


        return ok ( sortedJsonArray.toString()   );
    }


    public static Result ajaxBuscaFolio(){
        System.out.println("   ...........................   desde UsuarioController.ajaxBuscFolio ");
        JsonNode json = request().body().asJson();
        System.out.println("   ...........................   UsuarioController.ajaxBuscFolio recibe: "+json);
        Long folio = json.findPath("folio").asLong();
        FolioProductorAsignado fpa = null;
        String cadena = "{\"encontrado\": false}";
        System.out.println("folio :"+folio);
        //La búsqueda la realiza el productor?
        if ( session("rolActual").compareTo("100")==0){
            fpa = FolioProductorAsignado.find.where().eq("personal.id", session("usuario")).eq("folio.numfolio", folio).findUnique();
            if (fpa == null){
                cadena = "{\"error\": \"El folio no se encuentra o no ha sido asignado a usted.\"}";
            } else {
                cadena = "{\"encontrado\": true, "
                        + "\"title\": \""+fpa.folio.oficio.titulo.replace("\"", "'") +"\", "
                        + "\"descripcion\": \""+fpa.folio.oficio.descripcion.replace("\"", "'")+"\", "
                        + "\"fpaId\": "+fpa.id+", "
                        + "\"folioId\": "+fpa.folio.id+", "
                        + "\"folioNum\": "+fpa.folio.numfolio+", "
                        + "\"fechaEntrega\": \""+fpa.folio.fechaentrega+"\", "
                        + "\"productorId\": "+fpa.personal.id+", "
                        + "\"productorNombre\": \""+fpa.personal.nombreCompleto()+"\"}";
            }
        }

        //La búsqueda la realiza el administrador?
        if ( session("rolActual").compareTo("1")==0){
            //fpa = FolioProductorAsignado.find.where().eq("folio.folio", folio).findUnique();
            List<FolioProductorAsignado> fpas=FolioProductorAsignado.find.where().eq("folio.numfolio", folio).findList();
            if (fpas == null){
                cadena = "{\"error\": \"El folio buscado no existe.\"}";
            } else {
                fpa = FolioProductorAsignado.find.where().eq("folio.numfolio", folio).findList().get(0);
                cadena = "{\"encontrado\": true, "
                        + "\"title\": \""+fpa.folio.oficio.titulo.replace("\"", "'") +"\", "
                        + "\"descripcion\": \""+fpa.folio.oficio.descripcion.replace("\"", "'")+"\", "
                        + "\"fpaId\": "+fpa.id+", "
                        + "\"folioId\": "+fpa.folio.id+", "
                        + "\"folioNum\": "+fpa.folio.numfolio+", "
                        + "\"fechaEntrega\": \""+fpa.folio.fechaentrega+"\""
                        //	+ "\"productorId\": "+fpa.personal.id+", "
                        //	+ "\"productorNombre\": \""+fpa.personal.nombreCompleto()+"\"}";

                        + "}";
            }
        }
        return ok( cadena );
    }


    public static Result folioBuscarNumResguardo() throws JSONException {
        JsonNode json = request().body().asJson();
        JSONObject retorno = new JSONObject();
        System.out.println("   ...........................   desde UsuarioController.folioBuscarNumResguardo ");

        System.out.println("   ...........................   UsuarioController.folioBuscarNumResguardo recibe: "+json);
        String numero = json.findValue("nresguardo").asText();
        List<Folio> f = Folio.find.where().eq("numeroresguardo", numero).findList();
        System.out.println(f.size());
        retorno.put("existe", (f.size()!=0));
        System.out.println(retorno.toString());
        return ok (retorno.toString());
    }


    public static Result ajaxValidaEvento() throws JSONException {
        /*
		 * POSIBLES VALORES DE RETORNO
		 * valido:  true/false
		 * conflictos: array de eventos con empalmes
		 * descripcion: cuando sucede valido=false
		 */
        System.out.println("\n\n\n\n   >...........................   desde UsuarioController.ajaxValidaEvento ");
        JsonNode json = request().body().asJson();
        System.out.println("recibido en UsuarioController.ajaxValidaEvento "+json);
        Long eventoId = json.findPath("eventoId").asLong();
        String tipo = json.findPath("tipo").asText();
        String desde = json.findPath("inicio").asText();
        String hasta = json.findPath("fin").asText();
        int sala = json.findPath("salasSalaId").asInt();
        Long faseId;
        Long folio;
        System.out.println("desde " + desde);
        System.out.println("hasta " + hasta);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        Date h = null;
        JSONObject jobj = new JSONObject();

        //Quitar nodo 'salasSalaId'
        for (JsonNode personNode : json) {
            if (personNode instanceof ObjectNode) {
                ObjectNode object = (ObjectNode) personNode;
                object.remove("salasSalaId");
            }
        }
        System.out.println("se eliminó el nodo salasSalaId");
        try {
            d = sdf.parse(desde);
            h = sdf.parse(hasta);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String strd = sdf.format(d).replace("T"," ");
        String strh = sdf.format(h).replace("T"," ");


        System.out.println("eventoId " + eventoId);
        System.out.println("tipo " + tipo);
        System.out.println("strdesde  " + strd);
        System.out.println("strhasta  " + strh);
        System.out.println("sala  " + sala);
        System.out.println("json  " + json);

        if (eventoId != 0) {
            PreAgenda evento = PreAgenda.find.byId(eventoId);
            folio = evento.folioproductorasignado.folio.numfolio;
            faseId = evento.fase.id;
        } else {
            folio = json.findPath("folio").asLong();
            faseId = json.findPath("faseId").asLong();
        }
        jobj.put("valido", false);
        if (tipo.compareTo("preagenda") == 0) {

            // Hay otros eventos en este día/hora?

            ExpressionList<PreAgenda> col;
            /*
            if (sala == 0) {
                col = PreAgenda.find
                        .fetch("folioproductorasignado")
                        .fetch("folioproductorasignado.folio")
                        .fetch("folioproductorasignado.folio.oficio")
                        .fetch("salas")
                        .fetch("salas.sala")
                        .where()
                        .lt("estado.id", 7)
                        .disjunction()
                        .add(Expr.or(
                                //(Expr.and(Expr.lt("inicio", sdf.format(d)), Expr.gt("fin", sdf.format(d)))),
                                //(Expr.and(Expr.lt("inicio", sdf.format(h)), Expr.gt("fin", sdf.format(h))))
                                (Expr.and(Expr.lt("inicio", strd), Expr.gt("fin", strd))),
                                (Expr.and(Expr.lt("inicio", strh), Expr.gt("fin", strh)))
                        ))
                        .add(Expr.or(
                                //(Expr.and(Expr.gt("inicio", sdf.format(d)), Expr.lt("inicio", sdf.format(h)))),
                                //(Expr.and(Expr.gt("fin", sdf.format(d)), Expr.lt("fin", sdf.format(h))))
                                (Expr.and(Expr.gt("inicio", strd), Expr.lt("inicio", strh))),
                                (Expr.and(Expr.gt("fin", strd), Expr.lt("fin", strh)))
                        ))
                        .endJunction()
                        .ne("id", eventoId)
                        .eq("fase.id", faseId)
                        .eq("folio", folio);

            } else {
                col = PreAgenda.find
                        .fetch("folioproductorasignado")
                        .fetch("folioproductorasignado.folio")
                        .fetch("folioproductorasignado.folio.oficio")
                        .fetch("salas")
                        .fetch("salas.sala")
                        .where()
                        .lt("estado.id", 7)
                        .disjunction()
                        .add(Expr.or(
                                //(Expr.and(Expr.lt("inicio", sdf.format(d)), Expr.gt("fin", sdf.format(d)))),
                                //(Expr.and(Expr.lt("inicio", sdf.format(h)), Expr.gt("fin", sdf.format(h))))
                                (Expr.and(Expr.lt("inicio", strd), Expr.gt("fin", strd))),
                                (Expr.and(Expr.lt("inicio", strh), Expr.gt("fin", strh)))
                        ))
                        .add(Expr.or(
                                //(Expr.and(Expr.gt("inicio", sdf.format(d)), Expr.lt("inicio", sdf.format(h)))),
                                //(Expr.and(Expr.gt("fin", sdf.format(d)), Expr.lt("fin", sdf.format(h))))
                                (Expr.and(Expr.gt("inicio", strd), Expr.lt("inicio", strh))),
                                (Expr.and(Expr.gt("fin", strd), Expr.lt("fin", strh)))
                        ))
                        .endJunction()
                        .eq("salas.sala.id", sala)
                        .ne("id", eventoId)
                        .eq("fase.id", faseId)
                        .eq("folio", folio);
            }

             */


            col = PreAgenda.find
                    .fetch("folioproductorasignado")
                    .fetch("folioproductorasignado.folio")
                    .fetch("folioproductorasignado.folio.oficio")
                    .fetch("salas")
                    .fetch("salas.sala")
                    .where()
                    .lt("estado.id", 7)
                    .disjunction()
                    .add(Expr.or(
                            //(Expr.and(Expr.lt("inicio", sdf.format(d)), Expr.gt("fin", sdf.format(d)))),
                            //(Expr.and(Expr.lt("inicio", sdf.format(h)), Expr.gt("fin", sdf.format(h))))
                            (Expr.and(Expr.lt("inicio", strd), Expr.gt("fin", strd))),
                            (Expr.and(Expr.lt("inicio", strh), Expr.gt("fin", strh)))
                    ))
                    .add(Expr.or(
                            //(Expr.and(Expr.gt("inicio", sdf.format(d)), Expr.lt("inicio", sdf.format(h)))),
                            //(Expr.and(Expr.gt("fin", sdf.format(d)), Expr.lt("fin", sdf.format(h))))
                            (Expr.and(Expr.gt("inicio", strd), Expr.lt("inicio", strd))),
                            (Expr.and(Expr.gt("fin", strh), Expr.lt("fin", strh)))
                    ))
                    .endJunction()
                    .eq("numfolio", folio)
                    .eq("fase.id", faseId);

            if (eventoId != 0) {
                col.ne("id", eventoId);
            }
            if (sala != 0) {
                col.eq("salas.sala.id", sala);
            }

            List<PreAgenda> colision3 = col.findList();
            System.out.println("colisiones3: " + colision3.size() + "   " + col);

            if (colision3.size() != 0) {
                StringBuilder auxBuilder = new StringBuilder();
                for (PreAgenda c1 : colision3) {
                    auxBuilder.append("{\"id\":").append(c1.id).append(", \"numFolio\":").append(c1.folioproductorasignado.folio.numfolio).append(", \"descripcion\":\"").append(c1.folioproductorasignado.folio.oficio.descripcion.replaceAll("\"", "'")).append("\"},");
                }
                String aux = auxBuilder.toString();
                aux = "{\"conflictos\":[" + aux.substring(0, aux.length() - 1) + "]}";
                System.out.println("* * * * * * * * *" + aux);
                jobj.put("servicios", new JSONObject(aux));
            }
            jobj.put("valido", (colision3.size() == 0));
        }
        System.out.println("retornando " + jobj.toString());
        return ok(jobj.toString());
    }


    public static Result ajaxContarCantidadTipoPersonal(){
        System.out.println("   ...........................   desde UsuarioController.ajaxContarCantidadTipoPersonal ");
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println(json);
        Long idTipo = json.findPath("idTipo").asLong();
        List<Long> lista = null;

        json.fieldNames().forEachRemaining(h -> System.out.println("? "+h));

        List<Personal> p = Personal.find.fetch("cuentas.roles").where().idIn(lista).eq("cuentas.roles.rol.id", idTipo).findList();
        return ok(jsonContext.toJsonString(p.size()));
    }


    public static Result tablero(){
        System.out.println("desde UsuarioController.tablero");
        UsuarioController.registraAcceso(request().path());
        return ok( views.html.usuario.tablero.render());
        //return ok( views.html.usuario.htl.render());
    }


    public static Result ajaxTablero(){
        // Todos los asignados al productor
        List<Folio> folioAll = Folio.find.where().eq("productoresAsignados.personal.id", session("usuario")).findList();
        System.out.println(" productor id: "+session("usuario"));
        int folioCompletos = 0;
        int folioIncompletos = 0;
        int soloAsignados = 0;
        int oficiosCompartidos = 0;
        List<Agenda> fechasCaducadas = new ArrayList<>();
        TreeSet<String> eventos = new TreeSet<>();
        JsonContext jsonContext = Ebean.createJsonContext();
        //
        for ( Folio fol:  Folio.find
                .fetch("productoresAsignados.agenda.fase")
                .fetch("productoresAsignados.folio")
                .fetch("productoresAsignados.folio.oficio")
                .fetch("productoresAsignados.agenda")
                .where().eq("productoresAsignados.personal.id", session("usuario"))
                .orderBy("productoresAsignados.agenda.inicio")
                .findList()  ){
            for ( FolioProductorAsignado pa : fol.productoresAsignados){
                if (pa.agenda.size() >0 && (pa.agenda.size() ==
                        pa.agenda.stream().filter(p -> p.estado.id > 7).count())){
                    folioCompletos++;
                } else
                    folioIncompletos++;

                soloAsignados = (int) pa.preagendas.stream().filter(q -> q.estado.id == 4).count();

                for ( Agenda ags : pa.agenda ){
                    //System.out.println("    "+ags.fin+"     "+new Date());
                    if ( ags.estado.id ==7){
                        java.util.Date date = ags.inicio;
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String format = formatter.format(date);
                        //if(ags.inicio.after( new Date() )  )
                        eventos.add(format);
                        if(ags.fin.before( new Date() )  )
                            fechasCaducadas.add(ags);
                    }
                }
                Collections.sort(fechasCaducadas);
            }

            //OficiosCompartidos
            if ( Folio.find.where().ne("productoresAsignados.personal.id", session("usuario")).eq("oficio.id", fol.oficio.id).findList().size()>0 ){
                oficiosCompartidos++;
            }

            //Folios compartidos
            if ( Folio.find.where().ne("productoresAsignados.personal.id", session("usuario")).eq("id", fol.id).findList().size()>0 ){
            }
        }
        System.out.println("completos: "+folioCompletos);
        System.out.println("incompletos: "+folioIncompletos);
        System.out.println("soloAsignados: "+soloAsignados);
        System.out.println("oficiosCompartidos: "+oficiosCompartidos);

        // Los que tienen todo terminado en agenda


        soloAsignados = Folio.find.where().eq("estado.id",  4 ).eq("productoresAsignados.personal.id", session("usuario")).findList().size();


        //	**********   REGRESA EL NUME DE FOLIOS, NO EL NUM DE EVENTOS
        int solicitudes = PreAgenda.find.where()
                .eq("estado.id", 5)
                .eq("folioproductorasignado.personal.id", session("usuario"))
                //.gt("inicio", new Date())
                .findList().size();

        int autorizados = Agenda.find.where()
                .eq("estado.id", 7)
                .eq("folioproductorasignado.personal.id", session("usuario"))
                //.gt("inicio", new Date())
                .findList().size();
        int terminados = Agenda.find.where()
                .gt("estado.id", 7)
                .eq("folioproductorasignado.personal.id", session("usuario"))
                .findList().size();
        List<String> listaEventos = new ArrayList<> (eventos);
        System.out.println("Solicitudes "+solicitudes);
        System.out.println("Tamaño listaEventos "+listaEventos.size());
        String caducadosConComas = String.join("\", \"", listaEventos);

        if (listaEventos.size()==0) {
            return ok( "{\"oficioAll\":"+folioAll.size()+", \"soloAsignados\":"+soloAsignados+", \"solicitudes\":"+solicitudes+", \"autorizados\":"+autorizados+", \"terminados\":\""+ String.format("%,d", terminados)+"\""+
                    ", \"folioCompletos\":"+folioCompletos+", \"folioIncompletos\":"+folioIncompletos+
                    ", \"fechasCaducadas\":"+ jsonContext.toJsonString(fechasCaducadas)+
                    ", \"eventos\":[]"+
                    "}"    );
        } else {
            return ok( "{\"oficioAll\":"+folioAll.size()+", \"soloAsignados\":"+soloAsignados+", \"solicitudes\":"+solicitudes+", \"autorizados\":"+autorizados+", \"terminados\":\""+ String.format("%,d", terminados)+"\""+
                    ", \"folioCompletos\":"+folioCompletos+", \"folioIncompletos\":"+folioIncompletos+
                    ", \"fechasCaducadas\":"+ jsonContext.toJsonString(fechasCaducadas)+
                    ", \"eventos\":[\""+caducadosConComas +"\"]"+
                    "}"    );
        }
    }


    public static Result ajaxTableroTerminarEvento(){
        Agenda evento;
        System.out.println("\n\ndesde UsuarioController.ajaxTableroTerminarEvento\n\n");
        JsonNode json = request().body().asJson();
        System.out.println("json"+json);
        System.out.println("id:"+json.findPath("id").asText());
        System.out.println("tipo:"+json.findPath("tipo").asText());
        String eventoId = json.findPath("eventoId").asText();
        if ( session("rolActual").compareTo("1")==0) {
            evento = Agenda.find.where()
                    .eq("id", eventoId)
                    .findUnique();
        } else {
            evento = Agenda.find.where()
                    .eq("folioproductorasignado.personal.id", session("usuario"))
                    .eq("id", eventoId)
                    .findUnique();
        }
        System.out.println("evento "+evento);
        System.out.println("estado inicial "+evento.estado.id);
        evento.estado =  Estado.find.byId(evento.fase.id+10);
        evento.update();

        // Depues de  hacer la actualización en agenda,
        // se deben checar los otros eventos agendados para ese mismo folio
        for ( Folio fol:  Folio.find
                .fetch("productoresAsignados.agenda.fase")
                .fetch("productoresAsignados.folio")
                .fetch("productoresAsignados.folio.oficio")
                .fetch("productoresAsignados.agenda")
                .where().eq("productoresAsignados.personal.id", session("usuario")).findList()  ){
            for ( FolioProductorAsignado pa : fol.productoresAsignados){
                int folioCompletos=0;
                int folioIncompletos=0;
                if (pa.agenda.size() ==
                        pa.agenda.stream().filter(p -> p.estado.id > 7).count()){
                    folioCompletos++;
                } else
                    folioIncompletos++;

                if (folioCompletos == pa.agenda.size() && folioIncompletos!=0 ){
                    fol.estado= Estado.find.byId(99L);
                    fol.update();
                }

            }

        }
        System.out.println("estado final "+evento.estado.id+" "+(   evento.estado.id.compareTo(10L)==1));
        return ok("{\"teminado\":"+(evento.estado.id.compareTo(10L)==1)+"}");
    }


    public static Result ajaxTableroEventosDiaTimeLine(){
        System.out.println("desde UsuarioController.ajaxTableroEventosDiaTimeLine");
        JsonNode json = request().body().asJson();
        JsonContext jsonContext = Ebean.createJsonContext();
        System.out.println("recibidos: "+json);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date laFecha;
        String laFecha2 = "";
        try {
            laFecha = sdf.parse(json.findPath("fecha").asText());
            laFecha2 = sdf2.format(laFecha);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Agenda> r = new ArrayList<>();
        // Ingeniero, Resp. disitalizacion, camarografo, productor
        if (session("rolActual").compareTo("10")==0 || session("rolActual").compareTo("11")==0 || session("rolActual").compareTo("14")==0 || session("rolActual").compareTo("100")==0) {
            r = Agenda.find
                    .fetch("folioproductorasignado.personal")
                    .fetch("folioproductorasignado.folio")
                    .fetch("folioproductorasignado.folio.oficio")
                    .fetch("fase")
                    .fetch("estado")
                    .fetch("salidas")
                    .fetch("vehiculos.vehiculo")
                    .fetch("cuentaRol")
                    .fetch("cuentaRol.cuentarol")
                    .fetch("cuentaRol.cuentarol.cuenta")
                    .fetch("cuentaRol.cuentarol.cuenta.personal")
                    .fetch("locaciones")
                    .fetch("salas")
                    .fetch("salas.sala")
                    .fetch("salas.usoscabina")
                    .fetch("equipos")
                    .fetch("equipos.equipo")
                    .fetch("equipos.equipo.estado")
                    .fetch("accesorios")
                    .fetch("accesorios.accesorio")
                    .fetch("accesorios.accesorio.estado")
                    .fetch("formatos")
                    .fetch("formatos.formato")
                    .where().eq("folioproductorasignado.personal.id", session("usuario"))
                    .eq("estado.id", 7L)
                    .between("inicio", laFecha2+" 00:00:00", laFecha2+" 23:59:59")
                    .orderBy("inicio")
                    .findList();
        }
        // Administrador
        if (session("rolActual").compareTo("1")==0) {
            r = Agenda.find
                    .fetch("folioproductorasignado.personal")
                    .fetch("folioproductorasignado.folio")
                    .fetch("folioproductorasignado.folio.oficio")
                    .fetch("fase")
                    .fetch("estado")
                    .fetch("salidas")
                    .fetch("vehiculos.vehiculo")
                    .fetch("cuentaRol")
                    .fetch("cuentaRol.cuentarol")
                    .fetch("cuentaRol.cuentarol.cuenta")
                    .fetch("cuentaRol.cuentarol.cuenta.personal")
                    .fetch("locaciones")
                    .fetch("salas")
                    .fetch("salas.sala")
                    .fetch("salas.usoscabina")
                    .fetch("equipos")
                    .fetch("equipos.equipo")
                    .fetch("equipos.equipo.estado")
                    .fetch("accesorios")
                    .fetch("accesorios.accesorio")
                    .fetch("accesorios.accesorio.estado")
                    .fetch("formatos")
                    .fetch("formatos.formato")
                    .where()
                    .eq("estado.id", 7L)
                    .between("inicio", laFecha2+" 00:00:00", laFecha2+" 23:59:59")
                    .orderBy("inicio")
                    .findList();
        }
        System.out.println("No. eventos "+r.size());
        return ok(jsonContext.toJsonString(r));
    }


    // Busca toda la info del folio productor asignado (user en session)
    public static Result ajaxTodosLosDatosFolioPA(){
        System.out.println("Llegando a UsuarioController.ajaxTodosLosDatosFolioPA");
        JsonNode json = request().body().asJson();

        System.out.println("UsuarioController.ajaxTodosLosDatosFolioPA recibe "+json);
        String folioId = json.findPath("folioId").asText();
        JsonContext jsonContext = Ebean.createJsonContext();

        List<FolioProductorAsignado> fpa = FolioProductorAsignado.find
                .fetch("folio")
                .fetch("folio.oficio")
                .fetch("folio.oficio.fechagrabaciones")
                .fetch("folio.oficio.servicios")
                .fetch("folio.oficio.urremitente")
                .fetch("folio.oficio.productoresSolicitados")
                .fetch("folio.oficio.contactos")
                .fetch("folio.oficio.contactos.telefonos")
                .fetch("folio.oficio.contactos.correos")
                .fetch("preagendas")
                .fetch("preagendas.fase")
                .fetch("preagendas.estado")
                .fetch("preagendas.personal")
                .fetch("preagendas.personal.rol")
                .fetch("preagendas.locutores")
                .fetch("preagendas.locutores.personal")
                .fetch("preagendas.accesorios")
                .fetch("preagendas.accesorios.accesorio")
                .fetch("preagendas.equipos")
                .fetch("preagendas.equipos.equipo")
                .fetch("preagendas.locaciones")
                .fetch("preagendas.salas")
                .fetch("preagendas.salas.sala")
                .fetch("preagendas.salas.usoscabina")
                .fetch("preagendas.salidas")
                .fetch("preagendas.formatos")
                .fetch("preagendas.formatos.formato")
                .fetch("preagendas.vehiculos")
                .fetch("preagendas.juntas")
                .fetch("preagendas.cancelaciones")
                .fetch("preagendas.cancelaciones.motivo")
                .fetch("agenda")
                .fetch("agenda.fase")
                .fetch("agenda.estado")
                .fetch("agenda.cuentaRol")
                .fetch("agenda.cuentaRol.cuentarol.cuenta")
                .fetch("agenda.cuentaRol.cuentarol.cuenta.personal")
                .fetch("agenda.cuentaRol.cuentarol.rol")
                .fetch("agenda.accesorios")
                .fetch("agenda.accesorios.accesorio")
                .fetch("agenda.equipos")
                .fetch("agenda.equipos.equipo")
                .fetch("agenda.locaciones")
                .fetch("agenda.salas")
                .fetch("agenda.salas.sala")
                .fetch("agenda.salas.usoscabina")
                .fetch("agenda.salidas")
                .fetch("agenda.formatos")
                .fetch("agenda.formatos.formato")
                .fetch("agenda.vehiculos")
                .fetch("agenda.vehiculos.vehiculo")
                .fetch("agenda.juntas")
                .fetch("agenda.cancelaciones")
                .fetch("agenda.cancelaciones.motivo")
                .where().eq("folio.id", folioId)
                .or(
                        Expr.in("preagendas.estado.id", Arrays.asList(4,5)),
                        Expr.in("agenda.estado.id", Arrays.asList(7, 11, 12,13,14,15, 99))
                )
                .filterMany("preagendas").in("estado.id", Arrays.asList(4,5))
                .orderBy("preagendas.inicio asc, agenda.inicio")
                .findList();

        System.out.println(fpa.size());
        return ok( jsonContext.toJsonString(fpa) );
    }

    //Regresa lista JSON con los productores disponibles en la fecha y hora especificados
    //Recibe fecha inicial, fecha final y id del evento
    public static Result ajaxProductorDisponible(){
        System.out.println("\n\n\n\n\n\nDesde  UsuarioController.ajaxProductorDisponible ");
        System.out.println("Checando si el productor esta disponible ");
        JsonNode json = request().body().asJson();
        System.out.println("Recibido en  ajaxProductorDisponible......................................"+json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        Long eventoId = json.findPath("id").asLong();
        System.out.println("*************************************************** * * * * * * "+eventoId);
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Object> arr1 = auxProductorNoDisponiblePreagenda(sdf.format(d), sdf.format(h), eventoId);
        List<Object> arr2 = auxProductorNoDisponibleAgenda(sdf.format(d), sdf.format(h), eventoId);

        System.out.println("Productores No disponibles preagenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+arr1.size());
        arr1.forEach(System.out::println);
        System.out.println("Productores No disponibles    agenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+arr2.size());
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        arr2.forEach(System.out::println);
        //List<Object> disponibles = Personal.find.where().eq("cuentas.roles.rol.id", 2).eq("activo","S").and(
        List<Object> disponibles = Personal.find
                .where().eq("cuentas.roles.rol.id", 100).eq("activo","S").and(
                        Expr.not(  Expr.in("id", arr1)),
                        Expr.not(  Expr.in("id", arr2))
                )
                //.orderBy("nombre, paterno")
                .findIds();
        System.out.println("Productores Disponibles    preagenda y agenda de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+disponibles.size()+"    ");
        disponibles.forEach(p-> System.out.println(p+" - "+Personal.find.byId((Long) p).nombreCompleto()));
        return ok(Json.toJson(disponibles));
    }


    public static Result ajaxOperadorSalaDisponible() {
        System.out.println("\n\n\n\n\n\nDesde UsuarioController.ajaxOperadorSalaDisponible");
        System.out.println("Checando si existe operador disponible en el dia/horario");
        JsonNode json = request().body().asJson();
        System.out.println("Recibido en ajaxOperadorSalaDisponible......................................"+json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = null;
        Date h = null;
        Long eventoId = json.findPath("id").asLong();
        Long salaId = json.findPath("salaId").asLong();
        List<Object> disponibles = null;
        try {
            d = sdf.parse(json.findPath("inicio").asText());
            h = sdf.parse(json.findPath("fin").asText());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        disponibles = auxAjaxOperadorSalaDisponible(eventoId, salaId, d, h);
        if (disponibles != null) {
            disponibles.forEach(p-> System.out.println(">. "+p));
            return ok(Json.toJson(disponibles));
        } else {
            System.out.println("No hay operador para la sala "+ salaId);
            return ok(Json.toJson(""));
        }
    }


    public static List<Object> auxAjaxOperadorSalaDisponible(Long eventoId, Long salaId, Date desde, Date hasta) {
        System.out.println("desde auxAjaxOperadorSalaDisponible "+eventoId+" "+salaId+" "+desde+" "+hasta);
        List<Object> disponibles = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d = desde;
        Date h = hasta;
        List<Object> operadorSala = auxOperadorEnHorario(sdf.format(d), sdf.format(h), eventoId, salaId);
        if ( operadorSala.size()!=0) {
            List<Object> arr = auxOperadorNoDisponible(sdf.format(d), sdf.format(h), eventoId);
            disponibles = find
                    .where().eq("personal.activo", "S")
                    .eq("sala.id", salaId)
                    .not(Expr.in("personal.id", arr)
                    ).orderBy("personal.nombre, personal.paterno")
                    .findIds();
            //.stream().map(f->f.personal.id).collect(Collectors.toList());
        }
        System.out.println("Operadores disponibles de:"+sdf.format(d)+" hasta:"+sdf.format(h)+"  : "+disponibles.size());
        if (disponibles.size()>0) {
            for (Object x : disponibles) {
                String cadena = String.valueOf(x);
                Long l = Long.parseLong(cadena);
                System.out.println("operadorSala disponible: "+l+" - "+  find.byId(l).personal.nombreCompleto()    );
            }
        }
        return disponibles;
    }

    public static Result ajaxOperadoresSalas() throws JSONException {
        System.out.println("desde UsuarioController.ajaxOperadoresSalas.:");
        JsonContext jsonContext = Ebean.createJsonContext();

        List<OperadorSala> r = find
                .fetch("sala")
                .fetch("personal")
                .fetch("personal.horarios")
                .where()
                .eq("personal.activo", "S")
                .orderBy().asc("turno")
                .findList();
        System.out.println("Operadores tam:"+r.size());

        String sql = "select distinct p.id, p.turno " +
                "from personal p " +
                "inner join personal_horario ph on p.id = ph.personal_id " +
                "inner join operador_sala ops on  p.id = ops.personal_id " +
                "where p.activo = 'S'";


        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
      //  JSONObject retorno = new JSONObject();
        JSONArray ja = new JSONArray();
        for( SqlRow row :sqlRows ){

            //SEGUIR CON ESTO

            List<PersonalHorario> horarios = PersonalHorario.find.where().eq("personal.id", row.getLong("id")).findList();
            List<OperadorSala>salas = OperadorSala.find.where().eq("personal.id", row.getLong("id")).findList();
            for ( OperadorSala sala: salas ){
                JSONObject aux = new JSONObject();
                aux.put("personalId", row.getLong("id"));
                aux.put("sala", sala.sala.id);
                aux.put("turno", sala.turno);
                JSONArray jaH = new JSONArray();
                for ( PersonalHorario ph: horarios){
                    JSONObject joAuxH = new JSONObject();
                    joAuxH.put("diasemana", ph.diasemana);
                    joAuxH.put("desde", ph.desde);
                    joAuxH.put("hasta", ph.hasta);
                    jaH.put(joAuxH);
                }
                aux.put("Horarios",jaH);
                ja.put(aux);
            }

        }

        System.out.println(ja );



        //System.out.println(jsonContext.toJsonString(r));
        //return ok(jsonContext.toJsonString(r));
        return ok (ja.toString());
    }

    //Recibe id de personal y regresa Personal
    public static Result ajaxBuscaPersona() {
        System.out.println("desde UsuarioController.ajaxBuscaPersona");
        JsonNode json = request().body().asJson();
        System.out.println("json:"+json);
        Long id = json.findPath("id").asLong();
        JsonContext jsonContext = Ebean.createJsonContext();
        Personal p = Personal.find
                .fetch("cuentas.roles.rol")
                .fetch("horarios")
                .where().eq("id",id)
                .findUnique();

        return ok(jsonContext.toJsonString(p));
    }


    //Recibe id de tipopersonal y regresa objeto TipoPersonal
    public static Result ajaxBuscaTPersonal() {
        System.out.println("desde UsuarioController.ajaxBuscaTPersonal");
        JsonNode json = request().body().asJson();
        System.out.println("json:"+json);
        Long id = json.findPath("id").asLong();
        JsonContext jsonContext = Ebean.createJsonContext();
        Rol p = Rol.find
                .where().eq("id", id)
                .findUnique();
        System.out.println("retorno: "+jsonContext.toJsonString(p));
        return ok(jsonContext.toJsonString(p));
    }

    @Transactional
    public static Result ajaxCancelaEvento() {
        boolean retorno = true;
        AgendaCancelacion asc = new AgendaCancelacion();
        System.out.println("   ...........................   desde AdministracionController.ajaxCancelaEvento ");
        JsonNode json = request().body().asJson();
        System.out.println(json);
        try {
            Agenda servicio = Agenda.find.byId(json.findPath("eventoId").asLong());
            asc.agenda = servicio;
            asc.estadoAnterior = servicio.estado;
            asc.motivo = MotivoCancelacion.find.byId(json.findPath("motivoId").asLong());
            servicio.estado = Estado.find.byId(100L);
            servicio.cancelaciones.add(asc);
            servicio.update();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("error   "+e);
            retorno = false;
        }
        return ok( "{\"cancelado\": "+retorno+"}"   );
    }



    public static Result ajaxListaProductoresActivos() {
        JsonContext jsonContext = Ebean.createJsonContext();
        System.out.println("desde UsuarioController.ajaxListaProductoresActivos");
        List<Personal> productores = Personal.find
                .where().eq("tipo_id", 2)
                .eq("activo", "S")
                .orderBy("paterno, materno, nombre")
                .findList();
        return ok (jsonContext.toJsonString(productores));
    }

    public static Result misServiciosOperaSalaDTSS3(){
     //   AQUI ME QUEDE
        System.out.println(ColorConsola.ANSI_YELLOW+">> >> >> Desde UsuarioController.misServiciosOperaSalaDTSS3............");
      //  System.out.println( "parametros "+ request() );
        List<OperadorSala> operadoresSala = find
                .fetch("personal.horarios")
                .where()
                .eq("personal.id", session("usuario"))
                .findList();
        System.out.println( "operadoresSala ");
        for (OperadorSala operadorSala : operadoresSala) {
            System.out.println(operadorSala.id+" "+operadorSala.personal.nombreCompleto()+" sala "+operadorSala.sala.descripcion);
        }

        class OperadorSalaTurno{
            Long operadorId;
            Long salaId;
            String turno;
            int diaSemana;
            Date desde;
            Date hasta;
        }

       // Long laSala = operadoresSala.get(0).sala.id;
        List<OperadorSalaTurno> operadorSalasTurnos = new ArrayList<>();
        for ( OperadorSala op : operadoresSala ){
            for (PersonalHorario h :op.personal.horarios ){
                OperadorSalaTurno ost = new OperadorSalaTurno();
                ost.operadorId = op.id;
                ost.salaId = op.sala.id;
                ost.turno = op.turno;
                ost.diaSemana = h.diasemana;
                ost.desde = h.desde;
                ost.hasta = h.hasta;
                operadorSalasTurnos.add(ost);
            }
        }

        operadorSalasTurnos.forEach(x->{
            System.out.println("operador: "+x.operadorId);
            System.out.println("sala: "+x.salaId);
            System.out.println("turno: "+x.turno);
            System.out.println("dia: "+x.diaSemana);
            System.out.println("desde: "+x.desde);
            System.out.println("hasta: "+x.hasta+"\n\n");
        });

        JSONObject json;
        int filtrados = 0;
        int sinFiltro = 0;
        Map<Integer, Integer> columnasOrdenables = new HashMap<Integer, Integer>();
        columnasOrdenables.put(0, 1);
        columnasOrdenables.put(1, 19);
        columnasOrdenables.put(2, 9);
        System.out.println( columnasOrdenables.get(0)  );
        System.out.println( columnasOrdenables.get(1)  );
        String estado = request().getQueryString("estado");
        String filtro = request().getQueryString("search[value]");
        int colOrden =   Integer.parseInt( request().getQueryString("order[0][column]")   );
        String tipoOrden = request().getQueryString("order[0][dir]");
        System.out.println( "parametros order[0][column]:"+ colOrden);
        System.out.println( "parametros order[0][dir]:"+ tipoOrden);
        System.out.println( "estado :"+ estado);
        System.out.println("es *: "+(estado.compareTo("*")==0));
        System.out.println( "filtrando :"+ filtro);
        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        int numRegistros = Integer.parseInt(request().getQueryString("length"));

        Set<Long> setIdsPreagendas = new HashSet<Long>();
        Set<Long> setIdsAagendas = new HashSet<Long>();

        Query<Folio> qFolios = Ebean.find(Folio.class);
        Set<Long> losIdsFolio = new HashSet<Long>();


        List<AuxEventoOperadorSala> x = new EventoOperadorSala().obtener();

        List<Long> preIds =   x.stream().filter(f->f.tipo=="preagenda").map(m->m.id).collect(Collectors.toList());
        List<Long> ageIds =   x.stream().filter(f->f.tipo=="agenda").map(m->m.id).collect(Collectors.toList());

        System.out.println("preIds "+preIds);
        System.out.println("ageIds "+ageIds);


        List<PreAgenda> pragendas = preIds.size()==0?new ArrayList<>():PreAgenda.find.where().idIn(preIds).findList();
        List<Agenda> agendas = ageIds.size()==0?new ArrayList<>():Agenda.find.where().idIn(ageIds).findList();

        //*****************************
        // TODOS LOS SERVICIOS
        if (estado.compareTo("*")==0){
            pragendas.forEach(pre-> setIdsPreagendas.add(  pre.folioproductorasignado.folio.id ));
            agendas.forEach(pre-> setIdsAagendas.add(  pre.folioproductorasignado.folio.id ));

            losIdsFolio.addAll(setIdsPreagendas);
            losIdsFolio.addAll(setIdsAagendas);
        }

        // Solicitudes
        // Son las solicitudes que ha hecho el productor para usar sala/cabina
        if (estado.compareTo("5")==0){
            pragendas.stream().filter(f-> f.estado.id==5).forEach(pre-> setIdsPreagendas.add(  pre.folioproductorasignado.folio.id ));
            losIdsFolio.addAll(setIdsPreagendas);
            losIdsFolio.addAll(setIdsAagendas);
        }


        // SERVICIOS AUTORIZADOS
        if (estado.compareTo("7")==0){
            //	 System.out.println("estado 7");
            agendas.stream().filter(f->f.estado.id == 7).forEach(pre-> setIdsAagendas.add(  pre.folioproductorasignado.folio.id ));
            losIdsFolio.addAll(setIdsPreagendas);
            losIdsFolio.addAll(setIdsAagendas);
        }

        // SERVICIOS TERMINADOS
        if (estado.compareTo("99")==0){
            // System.out.println("estado 99");

            agendas.stream().filter(f->f.estado.id > 7 && f.estado.id<=99).forEach(pre-> setIdsAagendas.add(  pre.folioproductorasignado.folio.id ));
            losIdsFolio.addAll(setIdsPreagendas);
            losIdsFolio.addAll(setIdsAagendas);
        }


        qFolios.where().in("id", losIdsFolio);

        String.join(",", losIdsFolio.toString());
        Query<Folio> qFol2 = qFolios.copy();

        if (filtro.length()>0) {
            qFol2.fetch("oficio")
                    .fetch("oficio.urremitente")
                    .fetch("productoresAsignados")
                    .fetch("productoresAsignados.personal")
                    .where( "		(productoresAsignados.personal.nombre LIKE '%"+filtro+"%'"
                            + "		or folio like '%"+filtro+"%'"
                            + "		or oficio.descripcion like '%"+filtro+"%'"
                            + "		or UPPER( CONCAT(productoresAsignados.personal.nombre,\" \",productoresAsignados.personal.paterno,\" \",productoresAsignados.personal.materno)) like UPPER('%"+filtro+"%'"
                            + "		))"  );
        }

        sinFiltro = qFolios.findList().size();
        filtrados = qFol2.findList().size();

        json = generaDatosDesdeList2(sinFiltro, filtrados, estado, qFol2, colOrden, columnasOrdenables, numRegistros, numPag, tipoOrden );

        System.out.println(ColorConsola.ANSI_BLUE+"\n\nUsuarioController.misServiciosOperaSalaDTSS3 regresa:    "+json.toString()+ColorConsola.ANSI_RESET);

        System.out.println(ColorConsola.ANSI_RESET);
        return ok( json.toString()  );
    }


    private static JSONObject generaDatosDesdeList2(int sinFiltro, int filtrados, String estado, Query<Folio> folios,
                                                    int colOrden, Map<Integer, Integer> columnasOrdenables,
                                                    int numRegistros, int numPag, String tipoOrden) {
        JSONObject json2 = new JSONObject();
        Page<Folio> pagina = folios.findPagingList(numRegistros).setFetchAhead(false).getPage(numPag);
        try {
            json2.put("draw",  new Date().getTime()  );
            json2.put("recordsTotal",  sinFiltro );
            json2.put("recordsFiltered", filtrados);

            JSONArray losDatos = new JSONArray();
            // Oficios sin asignar
            if (pagina.getList().size() == 0) {
                json2.put("data", new JSONArray() );
            } else {
                for( Folio p : pagina.getList()  ){
                    Set<String> losEstados = new HashSet<String>();
                    JSONObject datoP = new JSONObject();
                    datoP.put("id", p.id);
                    datoP.put("folio", p.numfolio);
                    datoP.put("folioId", p.id);
                    datoP.put("ur", p.oficio.urremitente.nombreLargo);
                    datoP.put("productor", String.join(",", p.listaProductoresNombres()));

                    for ( FolioProductorAsignado prod : p.productoresAsignados){
                        if (prod.preagendas.isEmpty() && prod.agenda.isEmpty()){
                            if(prod.folio.estado.id == 100)
                                losEstados.add("Cancelado");
                            if(prod.folio.estado.id == 99)
                                losEstados.add("Cerrado");
                            if(prod.folio.estado.id == 4)
                                losEstados.add("Por atender");
                        }else{
                            for (PreAgenda pre :  prod.preagendas ){
                                if (pre.estado.id==4)
                                    losEstados.add( "Por atender" );
                                if (pre.estado.id==5)
                                    losEstados.add( "Solicitudes" );
                                if (pre.estado.id==7)
                                    losEstados.add( "Autorizados" );
                                if (pre.estado.id>7)
                                    losEstados.add( "Terminados" );
                                if (pre.estado.id==100)
                                    losEstados.add( "cancelados" );
                            }
                            for (Agenda age :  prod.agenda ){
                                if (age.estado.id==4)
                                    losEstados.add( "Por atender" );
                                if (age.estado.id==5)
                                    losEstados.add( "Solicitudes" );
                                if (age.estado.id==7)
                                    losEstados.add( "Autorizados" );
                                if (age.estado.id>7)
                                    losEstados.add( "Terminados" );
                                if (age.estado.id==100)
                                    losEstados.add( "Cancelados" );
                            }

                        }
                    }

                    datoP.put("estado", (estado.compareTo("*")==0)?String.join(",",losEstados):Estado.find.where().idEq(estado).findUnique().descripcion);
                    datoP.put("estadoId", (estado.compareTo("*")==0)?"*":estado);
                    datoP.put("descripcion", p.oficio.descripcion);
                    losDatos.put(datoP);
                }
                if ( pagina.getList().size()>0 ){
                    json2.put("data", losDatos);
                } else {
                    json2.put("data", new JSONArray() );
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json2;
    }


    public static Result ajaxPersonalPorRol() {
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        List<Personal> retorno = Personal.find
                .fetch("horarios")
                .where().eq("cuentas.roles.rol.id", json.findValue("id").asLong())
                .findList();
        return ok (jsonContext.toJsonString(retorno));
    }


    public static Result ajaxPersonalActivoPorRolTurno() {
        JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        List<Personal> retorno = Personal.find
                .fetch("horarios")
                .where()
                .eq("cuentas.roles.rol.id", json.findValue("id").asLong())
                .eq("activo", "S")
                .or(Expr.eq("turno", json.findValue("turno").asText()),  Expr.eq("turno", "A" )   )
                .orderBy("nombre, paterno, materno")
                .findList();
        return ok (jsonContext.toJsonString(retorno));
    }

    // Devuelve los operadores disponibles de la sala en el turno (fecha, hora de inicio, hora final)
    public static Result ajaxOperadoresDisponiblesPorSalaEvento() {
        Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println(json);
        Long sala = json.findValue("salaId").asLong();
        String fecha = json.findValue("fecha").asText();
        String desde = json.findValue("desde").asText();
        String hasta = json.findValue("hasta").asText();

        List<PreAgendaSala> pass = new ArrayList<PreAgendaSala>();
        try {
            Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fecha+" "+desde);
            Date date2=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fecha+" "+hasta);
            System.out.println(date1+"     "+date2);

            // Localiza los operadores de la sala ocupados en la fecha y hora
            pass = PreAgendaSala.find.where()
                    .eq("sala.id", sala)
                    //.between("preagenda.inicio", date1, date2)
                    //.between("preagenda.fin", date1, date2)
                    .betweenProperties("preagenda.inicio","preagenda.fin", date1)
                    .betweenProperties("preagenda.inicio","preagenda.fin", date2)
                    .findList();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ok ("{\"operadores\":"+pass.size()+"}");
    }


    // Regresa un list de los operadores de la sala
    public static Result ajaxOperadoresDeSala() {
        JsonNode json = request().body().asJson();
        JSONArray jArray = new JSONArray();
        System.out.println(json);
        Long sala = json.findValue("sala").asLong();
        List<OperadorSala> ops = find.where()
                .eq("sala.id", sala)
                .orderBy("turno, nombre, materno, nombre")
                .findList();

        for( OperadorSala op: ops) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("operador",  op.personal.nombreCompleto());
                JSONArray arrHorarios = (JSONArray) op.personal.horarios;
                jo.put("horario", arrHorarios);
                jArray.put(jo);

            } catch (JSONException e) {
                System.out.println("\n\n\n\nError en json");
                e.printStackTrace();
            }
        }
        return ok(jArray.toString());
    }

    // Regresa el archivo relacionado al oficio
    public static Result verOficio(Long id, String sufijo) throws SQLException {
        System.out.println("Desde verOficio.......  id "+id);
        System.out.println("id: "+id+"   sufijo: "+sufijo);
        PlantillaArchivo pa = new PlantillaArchivo();
        String ct = null;
        byte[] contenido = new byte[0];
        switch (sufijo.toLowerCase()) {
            case "":
            case "imagen":
                OficioImagen ima = OficioImagen.find.ref(id);
                pa.contenttype = ima.contenttype;
                pa.contenido = ima.contenido;
                break;
            case "respuesta":
                OficioRespuesta a = OficioRespuesta.find.ref(id);
                pa.contenttype = a.contenttype;
                pa.contenido = a.contenido;
                break;
            case "minuta":
                OficioMinuta m = OficioMinuta.find.ref(id);
                pa.contenttype = m.contenttype;
                pa.contenido = m.contenido;
                break;
            case "guion":
                OficioGuion g = OficioGuion.find.ref(id);
                pa.contenttype = g.contenttype;
                pa.contenido = g.contenido;
                break;
            case "entrada":
                OficioEntradaSalida es = OficioEntradaSalida.find.ref(id);
                pa.contenttype = es.contenttype;
                pa.contenido = es.contenido;
                break;
            case "bitacora":
                OficioBitacora b = OficioBitacora.find.ref(id);
                pa.contenttype = b.contenttype;
                pa.contenido = b.contenido;
                break;
            case "evidencia":
                OficioEvidenciaEntrega ee = OficioEvidenciaEntrega.find.ref(id);
                pa.contenttype = ee.contenttype;
                pa.contenido = ee.contenido;
                break;
            case "encuesta":
                OficioEncuesta en = OficioEncuesta.find.ref(id);
                pa.contenttype = en.contenttype;
                pa.contenido = en.contenido;
                break;
        }
        response().setContentType(pa.contenttype);
        return ok (pa.contenido);
    }



    // Regresa un json con los archivos relacionados al oficio
    public static Result oficioArchivos(){
        JsonNode json = request().body().asJson();
        Logger.debug("Desde oficioArchivos");
        System.out.println(json);
        Long id = json.findValue("oficioId").asLong();
        Oficio o = Oficio.find.byId(id);
        JSONObject joRetorno = new JSONObject();
        JSONArray jaAux = new JSONArray();
        JSONArray jaVacios = new JSONArray();
        try {
            //imagenes
            if (o !=null  &&  o.imagenes.size()>0) {
                for (OficioImagen ima : o.imagenes) {
                    JSONObject jo = new JSONObject();
                    jo.put("tipo", "Oficio");
                    jo.put("id", ima.id);
                    jo.put("nombrearchivo", ima.nombrearchivo);
                    jo.put("fecha", ima.auditUpdate);
                    jaAux.put(jo);
                }
                joRetorno.put("imagen", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(1);
            //oficiosrespuestas
            if (o !=null  &&  o.oficiosrespuestas.size()>0) {
                for (OficioRespuesta a : o.oficiosrespuestas) {
                    JSONObject jo = new JSONObject();
                    jo.put("tipo", "Oficio de respuesta");
                    jo.put("id", a.id);
                    jo.put("nombrearchivo", a.nombrearchivo);
                    jo.put("fecha", a.auditUpdate);
                    jaAux.put(jo);
                }
                joRetorno.put("respuesta", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(2);
            // minutas
            if (o !=null  &&  o.minutas.size()>0) {
                for (OficioMinuta a : o.minutas) {
                    JSONObject jo = new JSONObject();
                    jo.put("tipo", "Minuta de acuerdos");
                    jo.put("id", a.id);
                    jo.put("nombrearchivo", a.nombrearchivo);
                    jo.put("fecha", a.auditUpdate);
                    jaAux.put(jo);
                }
                joRetorno.put("minuta", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(3);
            // guiones
            if (o !=null  &&  o.guiones.size()>0) {
                for (OficioGuion a : o.guiones) {
                    JSONObject jo = new JSONObject();
                    jo.put("tipo", "Guión o escaleta");
                    jo.put("id", a.id);
                    jo.put("nombrearchivo", a.nombrearchivo);
                    jo.put("fecha", a.auditUpdate);
                    jaAux.put(jo);
                }
                joRetorno.put("guion", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(4);
            // entradassalida
            if (o !=null  &&  o.entradassalida != null) {
                JSONObject jo = new JSONObject();
                jo.put("tipo", "Entradas y salidas");
                jo.put("id", o.entradassalida.id);
                jo.put("nombrearchivo", o.entradassalida.nombrearchivo);
                jo.put("fecha", o.entradassalida.auditUpdate);
                jaAux.put(jo);
                joRetorno.put("guion", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(5);
            // bitacoras
            if (o !=null  &&  o.bitacoras.size()>0) {
                for (OficioBitacora a : o.bitacoras) {
                    JSONObject jo = new JSONObject();
                    jo.put("tipo", "Bitácoras");
                    jo.put("id", a.id);
                    jo.put("nombrearchivo", a.nombrearchivo);
                    jo.put("fecha", a.auditUpdate);
                    jaAux.put(jo);
                }
                joRetorno.put("bitacora", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(6);
            // evidenciaentrega
            if (o !=null  &&  o.evidenciaentrega != null) {
                JSONObject jo = new JSONObject();
                jo.put("tipo", "Evidencia de entrega");
                jo.put("id", o.evidenciaentrega.id);
                jo.put("nombrearchivo", o.evidenciaentrega.nombrearchivo);
                jo.put("fecha", o.evidenciaentrega.auditUpdate);
                jaAux.put(jo);
                joRetorno.put("evidencia", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(7);
            // encuesta
            if (o !=null  &&  o.encuesta != null) {
                JSONObject jo = new JSONObject();
                jo.put("tipo", "Encuesta de servicio");
                jo.put("id", o.encuesta.id);
                jo.put("nombrearchivo", o.encuesta.nombrearchivo);
                jo.put("fecha", o.encuesta.auditUpdate);
                jaAux.put(jo);
                joRetorno.put("encuesta", jaAux);
                jaAux = new JSONArray(new ArrayList<String>());
            } else
                jaVacios.put(8);

            joRetorno.put("vacios", jaVacios);


        } catch (JSONException e) {
            System.out.println("Error de JSON en UsuarioController.folioArchivos "+e);
        }
        return ok(joRetorno.toString());
    }



    public static Result infoCuentaRolPorPersona() throws JSONException {
        JsonNode json = request().body().asJson();
        Long idPersona = json.findValue("id").asLong();
        JSONObject retorno = new JSONObject();
        JSONArray arreglo = new JSONArray();
        retorno.put("estado", "no existe");


        List<CuentaRol> crs = CuentaRol.find.where().eq("cuenta.personal.id", idPersona).findList();
        if (crs.size()>0) {
            retorno.put("estado", "encontrado");
            for( CuentaRol cta:  crs ) {
                JSONObject detalle = new JSONObject();
                detalle.put("id", cta.id);
                detalle.put("cuentaId", cta.cuenta.id);
                detalle.put("rolId", cta.rol.id);
                detalle.put("rol", cta.rol.descripcion);
                arreglo.put(detalle);
            }
            retorno.put("datos", arreglo);
        } else {

        }
        return ok (retorno.toString());
    }



    //   REVISAR SI ES CONVENIENTE UNIR EL METODO validacionesAgenda3 y previoAgendar
    // Recibe en JSON inicio, fin, resourceId y evento
    //
    public static Result validacionesAgenda3() throws JSONException {
        System.out.println(ColorConsola.ANSI_BLUE + "Iniciando validaciones sobre agenda (UsuarioController.validacionesAgenda3)" + ColorConsola.ANSI_RESET);
        JsonNode json = request().body().asJson();
        System.out.println(json);
        String strDesde = json.findValue("inicio").asText();
        String strHasta = json.findValue("fin").asText();
        Long eventoId = json.findValue("evento").asLong();
        String resourceId = json.findValue("resourceId").asText();

        //Long salaId = json.findValue("salaId").asLong();
        JSONObject aux = resourceSalaYOperador(resourceId);
        Long salaId = aux.getLong("operadorId");
        Long operadorSalaId =  aux.getLong("salaId");
        final Long productorId =   json.has("productorId")?json.findValue("productorId").asLong() : 0L;

        Logger.debug("sala:"+salaId+"  operador:"+operadorSalaId);
        Logger.debug("productorId:"+productorId);


        if (resourceId.length()>1)
            operadorSalaId = Long.parseLong(resourceId.substring(1,resourceId.length()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        JSONObject retorno = new JSONObject();
        try {
            //¿EL PRODUCTOR ESTA DISPONIBLE?
            List<Object> prodNoDispPreAgenda = auxProductorNoDisponiblePreagenda(strDesde, strHasta, eventoId);
            List<Object> prodNoDispAgenda = auxProductorNoDisponibleAgenda(strDesde, strHasta, eventoId);
            System.out.println("tam 1:"+prodNoDispPreAgenda.size());
            prodNoDispPreAgenda.forEach(z->System.out.println("id productor no dis preagenda "+z));
            System.out.println("tam 2:"+prodNoDispAgenda.size());
            System.out.println("session usuario "+session("usuario"));

            List<Personal> productoresDisponibles = Personal.find
                    .where().eq("cuentas.roles.rol.id", 100).eq("activo","S")
                    .and(
                            Expr.not(  Expr.in("id", prodNoDispPreAgenda)),
                            Expr.not(  Expr.in("id", prodNoDispAgenda))
                    )
                    .findList();
            System.out.println("productoresDisponibles ");
            productoresDisponibles.forEach(f->System.out.println(f.id));
         //   List<Personal> dispo = productoresDisponibles.stream().filter(f -> f.id.equals( Long.parseLong(session("usuario")))).collect(Collectors.toList());
            Logger.debug("chequeando "+productorId);
            boolean productorDisponible = productorId==0? true : productoresDisponibles.stream()
                    .anyMatch(objeto -> objeto.id == productorId);

/*
            System.out.println("dispo tam "+dispo.size());
            System.out.println("dispo ");
            dispo.forEach(x->System.out.println(  x.id  ));
            System.out.println("count "+dispo.size());

 */
            retorno.put("productorDisponible",    productorDisponible);

            //EL OPERADOR ESTA DISPONIBLE?
            //El evento requiere sala?
            if (salaId!=0){
                System.out.println("operadorSalaId "+operadorSalaId);
                List<Object> disponibles = auxAjaxOperadorSalaDisponible(eventoId, salaId, sdf.parse(strDesde), sdf.parse(strHasta));
                System.out.println("disponibles "+disponibles);
                System.out.println("disponibles.size "+disponibles.size());
                if (disponibles.size()>0){
                    List<Personal> prodsDisp = Personal.find.where().in("id",   operadorSalaId).findList();
                    retorno.put("operadorSalaDisponible", prodsDisp.size()>0);
                } else {
                    retorno.put("operadorSalaDisponible", false);
                }
            }
        } catch (JSONException e) {
            System.out.println(ColorConsola.ANSI_RED + "Error en ajax. UsuarioController.validacionesAgenda3 " + ColorConsola.ANSI_RESET);
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(ColorConsola.ANSI_BLUE + "validacionesAgenda3 regresa:" +retorno.toString()+ ColorConsola.ANSI_RESET);
        return ok (retorno.toString());
    }


    private static JSONObject resourceSalaYOperador(String resource){
        Integer salaid=0;
        Integer operadorid=0;
        JSONObject retorno = new JSONObject();
        switch (resource){
            case "a":
            case "b":
            case "h": salaid = 0; break;
            case "c": salaid=1; operadorid = Integer.parseInt(resource.substring(1,resource.length()));   break;
            case "d": salaid=3; operadorid = Integer.parseInt(resource.substring(1,resource.length()));   break;
            case "e": salaid=2; operadorid = Integer.parseInt(resource.substring(1,resource.length()));   break;
            case "f": salaid=7; operadorid = Integer.parseInt(resource.substring(1,resource.length()));   break;
            case "g": salaid=4; operadorid = Integer.parseInt(resource.substring(1,resource.length()));   break;
        }
        try {
            retorno.put("salaId", salaid.toString());
            retorno.put("operadorId", operadorid.toString());
        } catch (JSONException e) {
            Logger.error("Error de json en UsuarioController.resourceSalaYOperador "+e);
            throw new RuntimeException(e);
        }
        return retorno;
    }

    public static Result todosIngenieros(){
        System.out.println("\n\n\n\todosIngenieros");
        JSONArray retorno = new JSONArray();
        for (  CuentaRol crol:  CuentaRol.find.where().eq("rol.id", 10)
                .eq("cuenta.personal.activo", "S")
                .findList() ){
            JSONObject jo = new JSONObject();
            try {
                jo.put("id", crol.cuenta.personal.id);
                jo.put("nombre", crol.cuenta.personal.nombreCompleto());
                retorno.put(jo);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("todosIngenieros retorno: "+retorno.toString());
        return ok(retorno.toString());
    }

    public static Result ingesta(){
        return ok (views.html.usuario.ingesta.render());
    }

    public static void registraAcceso(String ruta){
        RegistroAcceso ra = new RegistroAcceso();
        ra.usuario = Personal.find.setId(  Long.decode(session("usuario"))  ).findUnique();
        ra.ip = request().remoteAddress();
        ra.ruta = ruta;
        Logger.info("rolActual:"+session("rolActual")    );
        Logger.info("rolActual:"+session("rolActual5")    );
        System.out.println( session("rolActual") != null   );
        if ( session("rolActual") != null )
            ra.rol = Rol.find.setId(  Long.decode(session("rolActual"))  ).findUnique();
        ra.save();
    }

    public static Result rolPorFase(){
        Logger.debug("Desde UsuarioController.rolPorFase");
        JsonNode json = request().body().asJson();
        Logger.debug(String.valueOf(json));
        JSONArray jsonArray = new JSONArray();
        Long faseId = json.findValue("faseId").asLong();
        List<RolFase> roles = RolFase.find.where().eq("fase.id", faseId).findList();
       // AQUI ME QUEDE, HACE STREAM MAP PARA SCAR LOS IDS DE LOS RoleS Y REGRESARLOS COMO JSON

        List<Long> lstIds = roles.stream().map(m->m.rol.id).collect(Collectors.toList());
        for (Long value : lstIds) {
            jsonArray.put(value);
        }
        return ok (jsonArray.toString());
    }

}


