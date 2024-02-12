package controllers;

import classes.ColorConsola;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import models.*;
import models.videoteca.VtkCatalogo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.mvc.Result;
import views.html.usuario.miCatalogoVideotecaProductor;

import java.util.*;
import java.util.stream.Collectors;

public class ProductorVideotecaController extends ControladorSeguroProductor{

    public static Result list(){
        return ok(   miCatalogoVideotecaProductor.render(0L, "")   );
    }


    public static Result listDTSS(){
        Logger.info("\n\n\n>>  >> >>  Desde ProductorVideotecaController.listDTSS............");
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
        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        int numRegistros = Integer.parseInt(request().getQueryString("length"));
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


        String q ="        select \tvc.clave, \n" +
                "                (select  string_agg(s.descripcion, ', ') from vtk_evento ve inner join servicio s on ve.servicio_id  = s.id  where ve.catalogo_id = vc.id ) eventos,\n" +
                "                (select  string_agg(n3.descripcion, ', ') from vtk_nivel n2 inner join nivel n3 on n2.nivel_id = n3.id  where n2.catalogo_id = vc.id) niveles,\n" +
                "        \t\tvc.titulo ,        \t\t        \t\t        \t\t                \n" +
                "                (select string_agg(a.descripcion, ', ') from vtk_areatematica va inner join areatematica a on va.areatematica_id = a.id  where catalogo_id = vc.id) areasTematicas, \n" +
                "                (select vf.descripcion  from vtk_formato vf where vc.formato_id =  vf.id) formato, \n" +
                "                vc.sinopsis,\n" +
                "                (select ta.descripcion from tipo_audio ta where vc.audio_id = ta.id) audio,\n" +
                "                case \n" +
                "                \twhen calidad_audio='B' then 'Buena'\n" +
                "                \twhen calidad_audio='M' then 'Mala'\n" +
                "                \twhen calidad_audio='FO' then 'Fallas de orígen'\n" +
                "                \twhen calidad_audio='' then ''\n" +
                "                end calidadAudio,\n" +
                "                (select tv.descripcion from tipo_video tv  where vc.video_id = tv.id) video,\n" +
                "                case \n" +
                "                \twhen calidad_video='B' then 'Buena'\n" +
                "                \twhen calidad_video='M' then 'Mala'\n" +
                "                \twhen calidad_video='FO' then 'Fallas de orígen'\n" +
                "                \twhen calidad_video='' then ''\n" +
                "                end calidadVideo,    \n" +
                "                vc.observaciones,\n" +
                "                vc.folio,\n" +
                "                ur.nombre_largo instanciaSolicitante,\n" +
                "                vc.folio_dev folioDev,\n" +
                "                s2.descripcion serie ,                \n" +
                "                (select string_agg(lasPersonas, ';  ') from\n" +
                "\t\t\t\t\t(select  concat( tc.descripcion ,': ', string_agg( c2.personas , ', ')) lasPersonas\n" +
                "\t\t\t\t\tfrom tipo_credito tc \n" +
                "\t\t\t\t\tleft join credito c2 on tc.id = c2.tipo_credito_id  \n" +
                "\t\t\t\t\twhere  c2.personas is not null and c2.catalogo_id = vc.id\n" +
                "\t\t\t\t\tgroup by tc.id\n" +
                "\t\t\t\t\torder by tc.id\n" +
                "\t\t\t\t\t) otroo\n" +
                "\t\t\t\t) Creditos,  \n" +
                "\t\t\t\tvc.obra,\n" +
                "\t\t\t\tvc.duracion,\n" +
                "\t\t\t\t(select string_agg(pc.descripcion,', ')\n" +
                "\t\t\t\tfrom palabra_clave pc where pc.catalogo_id =vc.id ) palabrasClave,\n" +
                "\t\t\t\tp.descripcion produccion,\n" +
                "\t\t\t\ti.descripcion idioma,\t\t\t\t\n" +
                "\t\t\t\tcase\n" +
                "\t\t\t\t\twhen vc.accesibilidad_audio=true then 'Lenguaje de señas'\n" +
                "\t\t\t\tend accesibilidadAudio,\n" +
                "\t\t\t\tcase\n" +
                "\t\t\t\t\twhen vc.accesibilidad_video=true then 'Subtitulos descriptivos'\n" +
                "\t\t\t\tend accesibilidadVideo,\n" +
                "\t\t\t\ttg.descripcion grabacion,\n" +
                "\t\t\t\tvc.fecha_produccion,\n" +
                "\t\t\t\tvc.fecha_publicacion,\n" +
                "\t\t\t\td.descripcion disponibilidad,\n" +
                "\t\t\t\tconcat(p2.nombre, ' ', p2.paterno,' ', p2.materno) catalogador\n" +
                "        from vtk_catalogo vc\n" +
                "        inner join personal p2 on vc.catalogador_id = p2.id\n" +
                "        left join unidad_responsable ur on vc.unidadresponsable_id = ur.id\n" +
                "        left join serie s2 on vc.serie_id =s2.id \n" +
                "        left join produccion p on vc.produccion_id = p.id  \n" +
                "       \tleft join idioma i on vc.idioma_id = i.id \n" +
                "      \tleft join tipo_grabacion tg on vc.tipograbacion_id = tg.id \n" +
                "\t    left join disponibilidad d on vc.disponibilidad_id = d.id ";




/*

        select 	vc.*, vc.clave, vc.titulo ,
                s.descripcion servicio,
                ur.nombre_largo,
                (select  array_to_string(array_agg(s.descripcion), ', ') from vtk_evento ve inner join servicio s on ve.servicio_id  = s.id  where ve.catalogo_id = vc.id) servicios,
                (select  array_to_string(array_agg(n3.descripcion), ', ') from vtk_nivel n2 inner join nivel n3 on n2.nivel_id = n3.id  where n2.catalogo_id = vc.id) niveles,
                (select  array_to_string(array_agg(pc.descripcion), ', ') from palabra_clave pc where pc.catalogo_id = vc.id ) palabrasClave
        from vtk_catalogo vc
        inner join vtk_evento ve on vc.id = ve.catalogo_id
        inner join servicio s on ve.servicio_id = s.id
        left join unidad_responsable ur on vc.unidadresponsable_id = ur.id
*/






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
                            .filterMany("productoresAsignados.agenda").in( "fase.id", Arrays.asList(2) ).query();
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

                    datoP.put("estado", (estado.compareTo("*")==0)?losEstados.toString(): Estado.find.where().idEq(estado).findUnique().descripcion);

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
                        datoP.put("oficioId",  p.oficio.id );
                        datoP.put("tieneArchs",  p.oficio.tieneArchivos() );
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

}

