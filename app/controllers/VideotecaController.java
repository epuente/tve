package controllers;

import akka.event.EventBus;
import classes.CUrl;
import classes.ColorConsola;
import classes.Duracion;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.utils.PlantillaArchivo;
import models.videoteca.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import views.html.videoteca.createForm3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.data.Form.form;

public class VideotecaController extends ControladorSeguroVideoteca{
    public static Result tablero(){
        Logger.debug("desde VideotecaController.tablero");
        return ok( views.html.usuario.tableroVideotk.render());
    }

    public static Result ajaxTablero() throws JSONException {
        System.out.println("Desde VideotecaController.ajaxTablero");
        JSONObject retorno = new JSONObject();

        JSONArray arrSeriesLabels = new JSONArray();
        JSONArray arrSeriesDatos = new JSONArray();

        JSONArray arrAreaLabels = new JSONArray();
        JSONArray arrAreaDatos = new JSONArray();

        JSONArray arrFormatoLabels = new JSONArray();
        JSONArray arrFormatoDatos = new JSONArray();

        JSONArray arrIdiomaLabels = new JSONArray();
        JSONArray arrIdiomaDatos = new JSONArray();

        JSONArray arrProdLabels = new JSONArray();
        JSONArray arrProdDatos = new JSONArray();

        JSONArray arrAniosProdLabels = new JSONArray();
        JSONArray arrAniosProdDatos = new JSONArray();


        List<VtkCatalogo> total = VtkCatalogo.find.all();
        List<Serie> series = Serie.find.all();
        for( Serie  s : series){
            long c = VtkCatalogo.find.where().eq("serie.id", s.id).findRowCount();
            //arrSeriesLabels.put( new CapitalizaCadena(s.descripcion).modificado());
            arrSeriesLabels.put( s.descripcion);
            arrSeriesDatos.put(c);
          //  CapitalizaCadena n = new CapitalizaCadena(s.descripcion);
        }

        for (Areatematica at : Areatematica.find.all()){
            //arrAreaLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully( at.descripcion));
            //arrAreaLabels.put( new CapitalizaCadena(at.descripcion).modificado());
            arrAreaLabels.put( at.descripcion);
            arrAreaDatos.put(  VtkCatalogo.find.where().eq("areastematicas.areatematica.id", at.id).findRowCount() );
        }

        for (VtkFormato f : VtkFormato.find.all()){
            //arrFormatoLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully(f.descripcion));
            //arrFormatoLabels.put(new CapitalizaCadena(f.descripcion).modificado());
            arrFormatoLabels.put(f.descripcion);
            arrFormatoDatos.put( VtkCatalogo.find.where().eq("formato.id", f.id).findRowCount());
        }

        for (Produccion p: Produccion.find.all()){
            arrProdLabels.put( p.descripcion );
            arrProdDatos.put( VtkCatalogo.find.where().eq("produccion.id", p.id).findRowCount());
        }


        for (Idioma i : Idioma.find.all()){
            //arrIdiomaLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully(i.descripcion));
            //arrIdiomaLabels.put( new CapitalizaCadena(i.descripcion).modificado());
            arrIdiomaLabels.put( i.descripcion);
            arrIdiomaDatos.put( VtkCatalogo.find.where().eq("idioma.id", i.id).findRowCount());
        }

        // Fecha de producción
        String strQAnio = "select extract('Year' from vc.fecha_produccion) anio,  count(*) valor "+
            "from vtk_catalogo vc "+
            "group by extract('Year' from vc.fecha_produccion) "+
            "order by extract('Year' from vc.fecha_produccion)";

        strQAnio = "select extract ('Year' from TO_DATE(vc.fecha_produccion, 'DD-MM-YYYY')) anio,  count(*) valor "+
                "from vtk_catalogo vc "+
                "group by extract('Year' from TO_DATE(vc.fecha_produccion, 'DD-MM-YYYY')) "+
                "order by extract('Year' from TO_DATE(vc.fecha_produccion, 'DD-MM-YYYY'))";

        List<SqlRow> rowsAnios = Ebean.createSqlQuery(strQAnio).findList();

        for ( SqlRow anio : rowsAnios ){
            JSONObject o = new JSONObject();
            arrAniosProdLabels.put( anio.getString("anio") );
            arrAniosProdDatos.put( anio.getLong("valor"));
        }



        retorno.put("total", total.size());
        retorno.put("series", series.size());
        retorno.put("seriesLabels", arrSeriesLabels);
        retorno.put("seriesDatos", arrSeriesDatos);
        retorno.put("areaLabels", arrAreaLabels);
        retorno.put("areaDatos", arrAreaDatos);
        retorno.put("formatoLabels", arrFormatoLabels);
        retorno.put("formatoDatos", arrFormatoDatos);
        retorno.put("idiomaLabels", arrIdiomaLabels);
        retorno.put("idiomaDatos", arrIdiomaDatos);
        retorno.put("prodLabels", arrProdLabels);
        retorno.put("prodDatos", arrProdDatos);
        retorno.put("aniosProdLabels", arrAniosProdLabels);
        retorno.put("aniosProdDatos", arrAniosProdDatos);

        return ok ( retorno.toString() );
    }

    public static Result catalogo(){
        Logger.debug("desde VideotecaController.catalogo");
        return ok( views.html.videoteca.catalogoVideotk.render());
    }

    public static Result catalogoDTSS(){
        System.out.println("Desde VideotecaController.catalogoDTSS............"+new Date());
        /*
        System.out.println( "parametros 0:"+ request() );
        System.out.println( "parametros draw:"+ request().getQueryString("draw"));
        System.out.println( "parametros start:"+ request().getQueryString("start"));
        System.out.println( "parametros length:"+ request().getQueryString("length"));
        System.out.println( "parametros seach[value]:"+ request().getQueryString("search[value]"));

        System.out.println( "parametros order[0][column]:"+ request().getQueryString("order[0][column]"));
        System.out.println( "parametros order[0][dir]:"+ request().getQueryString("order[0][dir]"));
        */

        String filtro = request().getQueryString("search[value]");
        String colOrden =  request().getQueryString("order[0][column]");
        String tipoOrden = request().getQueryString("order[0][dir]");
        String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");

        List<VtkCatalogo> cat = null;
        // Es supervisor de catalogadores?
        if (session("rolActual").compareTo("133")==0){
            cat = VtkCatalogo.find.all();
        }
        // Es catalogador?
        if (session("rolActual").compareTo("132")==0){
            cat = VtkCatalogo.find.where().eq("catalogador.id", Long.valueOf(session("usuario"))).findList();
        }
        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        com.avaje.ebean.Page<VtkCatalogo> pag = VtkCatalogo.page(numPag ,
                Integer.parseInt(request().getQueryString("length"))
                , filtro
                , nombreColOrden
                , tipoOrden
        );



        response().setContentType("application/json");
        JSONObject json2 = new JSONObject();
        try {
            json2.put("draw",  request().getQueryString("draw")+1  );
            json2.put("recordsTotal", cat.size());
            JSONArray losDatos = new JSONArray();

            if (cat.isEmpty()){
                json2.put("recordsFiltered", 0);
                //losDatos.put(new JSONObject());
                json2.put("data", new JSONArray());
            } else {
                json2.put("recordsFiltered", pag.getTotalRowCount());
                if (pag.getTotalRowCount()==0) {
                    json2.put("data", new JSONArray());
                }
                for( VtkCatalogo p : pag.getList()  ){
                    JSONObject datoP = new JSONObject();
                    datoP.put("id", p.id);
                    datoP.put("clave", p.clave);
                    datoP.put("sinopsis", p.sinopsis);
                    datoP.put("titulo", p.titulo);
                    if (p.serie!=null)
                        datoP.put("serie", p.serie.descripcion);
                    datoP.put("obra", p.obra);
                    losDatos.put(datoP);
                    json2.put("data", losDatos);
                }
            }
        } catch (JSONException e) {
            System.out.println("ERROR - VideotecaController.catalogoDTSS ");
            e.printStackTrace();
        }
       // System.out.println("retornando:  "+json2.toString());
        return ok( json2.toString() );
    }



    public static Result catalogoCreate2(){
        Form<VtkCatalogo> forma = play.data.Form.form(VtkCatalogo.class);
        List<TipoCredito> tipos = TipoCredito.find.all();
        List<TipoCredito> tiposOrdenados = tipos.stream()
                .sorted(Comparator.comparing(TipoCredito::getId))
                .collect(Collectors.toList());
        List<VtkCampo> campos = VtkCampo.find.all();
        return ok(
                views.html.videoteca.createForm3.render(forma, tiposOrdenados, campos)
        );
    }


    // Aqui se usan 2 metodos, el primero es el clásico de busqueda aproximada (comparar una cadena con ilike y unaccent)
    // El segundo usa textsearch con ts_vector y ts_query
    // Se regresa un dataset con la combinacion de los dos resultados, poniendo primero los resultados de la comparación clásica
    public static Result textsearch() throws JSONException {
        System.out.println("\n\n\n\nDesde VTKController.textsearch");
        JsonNode json = request().body().asJson();
        System.out.println(json);
        JSONObject jo = new JSONObject();
        String campo = json.findValue("campo").asText();
        String cadena = json.findValue("cadena").asText();
        // Primero la búsqueda tradicional
        String query1 = "";
        if (campo.compareTo("ur")==0)
            query1 ="select id, nombre_largo from unidad_responsable s where unaccent(nombre_largo) ilike unaccent('%"+cadena+"%') order by nombre_largo";
        if (campo.compareTo("serie")==0)
            query1 ="select id, descripcion from serie s where unaccent(descripcion) ilike unaccent('%"+cadena+"%') order by descripcion";
        if (campo.compareTo("produccion")==0)
            query1 ="select id, descripcion from produccion s where unaccent(descripcion) ilike unaccent('%"+cadena+"%') order by descripcion";
        if (campo.compareTo("areatematica")==0)
            query1 ="select id, descripcion from areatematica s where unaccent(descripcion) ilike unaccent('%"+cadena+"%') order by descripcion";
        List<SqlRow> sqlRows1 = Ebean.createSqlQuery(query1).findList();

        final RawSql rawSql1 = RawSqlBuilder.unparsed(query1)
                .columnMapping("id", "id")
                .columnMapping("descripcion", "descripcion")
                .create();
        List<Serie> list1 = Serie.find.setRawSql(rawSql1).findList();


        // Ahora se usa textsearch
        // Obtener tsquery
        String queryTSQuery = "SELECT plainto_tsquery('spanish', '"+cadena+"')";
        SqlRow sqlRow2 = Ebean.createSqlQuery(queryTSQuery).findUnique();
        //Se obtiene el ts_query y le quita las comillas simples
        String ts = sqlRow2.getString("plainto_tsquery").toString().replaceAll("'","");;
        String query3 ="";
        if (campo.compareTo("ur")==0)
            query3 = "SELECT id, nombre_largo, ts_rank(to_tsvector(coalesce(nombre_largo, ' ')), to_tsquery('"+ts+"'))" +
                "FROM unidad_responsable s " +
                "WHERE to_tsvector(coalesce(nombre_largo, ' ')) @@ to_tsquery('"+ts+"') " +
                "ORDER BY ts_rank(to_tsvector(coalesce(nombre_largo, ' ')), to_tsquery('"+ts+"')) desc";
        if (campo.compareTo("serie")==0)
            query3 = "SELECT id, descripcion, ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"'))" +
                    "FROM serie s " +
                    "WHERE to_tsvector(coalesce(descripcion, ' ')) @@ to_tsquery('"+ts+"') " +
                    "ORDER BY ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"')) desc";
        if (campo.compareTo("produccion")==0)
            query3 = "SELECT id, descripcion, ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"'))" +
                    "FROM produccion s " +
                    "WHERE to_tsvector(coalesce(descripcion, ' ')) @@ to_tsquery('"+ts+"') " +
                    "ORDER BY ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"')) desc";
        if (campo.compareTo("areatematica")==0)
            query3 = "SELECT id, descripcion, ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"'))" +
                    "FROM areatematica s " +
                    "WHERE to_tsvector(coalesce(descripcion, ' ')) @@ to_tsquery('"+ts+"') " +
                    "ORDER BY ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"')) desc";

        final RawSql rawSql3 = RawSqlBuilder.unparsed(query3)
                .columnMapping("id", "id")
                .columnMapping("descripcion", "descripcion")
                .create();
        List<Serie> list2 = Serie.find.setRawSql(rawSql3).findList();

        List<Serie> list3 = new ArrayList<Serie>();
        if ( !list1.isEmpty() &&  list1.size()>0)
            list3.addAll(list1);
        if ( !list2.isEmpty() &&  list2.size()>0)
            list3.addAll(list2);

        // Para el siguiente código se agregaron los metodos equals y hasCode en la clase Serie
        // Elimina elementos de la lista repetidos
        List<Serie> uniqueSeriesList = list3.stream()
                .distinct()
                .collect(Collectors.toList());
        // Lista de objetos Serie únicos se pasan a un array de json
        JSONArray jaUnique = new JSONArray();
        for (Serie serie : uniqueSeriesList) {
            JSONObject joUnique = new JSONObject();
            joUnique.put("id", serie.id);
            joUnique.put("descripcion", serie.descripcion);
            jaUnique.put(  joUnique  );
        }
        jo.put("coincidencias", jaUnique);
        return ok ( jo.toString() );
    }


    public static Result nuevaSerie() throws JSONException {
        Logger.debug("Desde VideotecaController.nuevaSerie");
        JSONObject joRetorno = new JSONObject();
        JsonNode json = request().body().asJson();
        Logger.debug(String.valueOf(json));
        joRetorno.put("estado", "error");

        Serie s = Json.fromJson(json, Serie.class);
        s.catalogador = Personal.find.byId(  Long.parseLong(session("usuario"))  );
        s.save();
        s.refresh();
        joRetorno.put("estado", "ok");
        joRetorno.put("id", s.id);
        joRetorno.put("descripcion", s.descripcion);
        return ok (joRetorno.toString());
    }


    public static Result nuevaUR() throws JSONException {
        Logger.debug("Desde VideotecaController.nuevaUR");
        JSONObject joRetorno = new JSONObject();
        JsonNode json = request().body().asJson();
        Logger.debug(String.valueOf(json));
        joRetorno.put("estado", "error");
        UnidadResponsable s = Json.fromJson(json, UnidadResponsable.class);
        s.catalogador = Personal.find.byId(  Long.parseLong(session("usuario"))  );
        s.save();
        s.refresh();
        joRetorno.put("estado", "ok");
        joRetorno.put("id", s.id);
        joRetorno.put("descripcion", s.nombreLargo);
        return ok (joRetorno.toString());
    }

    public static Result nuevaAreaTematica() throws JSONException {
        Logger.debug("Desde VideotecaController.nuevaAreaTematica");
        JSONObject joRetorno = new JSONObject();
        JsonNode json = request().body().asJson();
        Logger.debug(String.valueOf(json));
        joRetorno.put("estado", "error");

        Areatematica at = Json.fromJson(json, Areatematica.class);
        at.catalogador = Personal.find.byId(  Long.parseLong(session("usuario"))  );
        at.save();
        at.refresh();
        joRetorno.put("estado", "ok");
        joRetorno.put("id", at.id);
        joRetorno.put("descripcion", at.descripcion);
        return ok (joRetorno.toString());
    }


    // Aqui se usa 1 metodo,  (compara con la totalidad del campo insensible a mayúsculas y unaccent)
    public static Result textsearchCampoCompleto() throws JSONException {
        System.out.println("\n\n\n\nDesde VideotecaController.textsearchCampoCompleto");
        JsonNode json = request().body().asJson();
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        String campo = json.findValue("campo").asText();
        String cadena = json.findValue("cadena").asText();
        String query1 = "";
        if (campo.compareTo("ur")==0)
            query1 = "select id, nombre_largo from unidad_responsable where unaccent(nombre_largo) = unaccent('"+cadena+"')";
        if (campo.compareTo("serie")==0)
            query1 = "select id, descripcion from serie s where unaccent(descripcion) = unaccent('" + cadena + "')";
        if (campo.compareTo("produccion")==0)
            query1 = "select id, descripcion from produccion s where unaccent(descripcion) = unaccent('" + cadena + "')";
        if (campo.compareTo("areatematica")==0)
            query1 = "select id, descripcion from areatematica s where unaccent(descripcion) = unaccent('" + cadena + "')";
        List<SqlRow> sqlRows1 = Ebean.createSqlQuery(query1).findList();
        jo.put("coincidencias", sqlRows1.size());
        return ok ( jo.toString() );
    }


    public static Result catalogoEdit(Long id){
        VtkCatalogo catalogo = VtkCatalogo.find.byId(id);
        if (session("usuario").compareTo( Long.toString(catalogo.catalogador.id))==0) {
            Form<VtkCatalogo> forma = form(VtkCatalogo.class).fill(catalogo);
            Duracion d = new Duracion();
            if (catalogo.duracion!=null)
                d = new Duracion(catalogo.duracion);
            else
                d = new Duracion(0L);
            List<TipoCredito> tipos = TipoCredito.find.all();
            List<TipoCredito> tiposOrdenados = tipos.stream()
                    .sorted(Comparator.comparing(TipoCredito::getId))
                    .collect(Collectors.toList());

            return ok( views.html.videoteca.editForm.render(id, forma, tiposOrdenados, VtkCampo.find.all(), d )  );
        } else {
            return ok (views.html.operacionNoPermitida.render());
        }
    }

    public static Result save() throws JSONException {
        System.out.println("\n\n\n\n\nDesde VideotecaController.save...");
        DynamicForm fd = DynamicForm.form().bindFromRequest();
        System.out.println(fd);
        System.out.println("--------------------------");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();
        System.out.println(forma);

        /*
        if (fd!=null)
            return ok("...");

         */


        Ebean.beginTransaction();
        try {
            if (forma.hasErrors()) {
                return badRequest(createForm3.render(forma, TipoCredito.find.all(), VtkCampo.find.all()));
            }
            VtkCatalogo vtk = losDatos(forma, fd);
           // System.out.println("Despues de losDatos");
            vtk.eventos.removeIf(r -> r.servicio == null);
            vtk.niveles.removeIf(r -> r.nivel == null);
            vtk.timeline.clear();
            Ebean.save(vtk);
            Ebean.refresh(vtk);
            Long idVTK = vtk.id;
            /////////// TimeLine
            if (forma.field("txaTimeLine").value() != null   &&  forma.field("txaTimeLine").value()!="" ){
                JSONArray jsonArrTimeLine = new JSONArray(forma.field("txaTimeLine").value());
                for (int i = 0; i < jsonArrTimeLine.length(); i++) {
                    VtkTimeLine tl = new VtkTimeLine();
                    JSONObject objTL = jsonArrTimeLine.getJSONObject(i);

                    if (objTL.has("desde") && !Objects.equals(objTL.get("desde").toString(), "hh:mm:ss") && !objTL.get("desde").toString().isEmpty()){
                            Duracion dDesde = new Duracion();
                            dDesde.convertir(objTL.get("desde").toString());
                            tl.desde = dDesde.totalSegundos();
                        }
                    if (objTL.has("hasta") && !Objects.equals(objTL.get("hasta").toString(), "hh:mm:ss"))
                        if (objTL.get("hasta").toString().length() != 0) {
                            Duracion dHasta = new Duracion();
                            dHasta.convertir(objTL.get("hasta").toString());
                            tl.hasta = dHasta.totalSegundos();
                        }
                    if (objTL.has("nombre")) {
                        if (!objTL.get("nombre").toString().isEmpty()) {
                            Long idVP = null;
                            VideoPersonaje vp = new VideoPersonaje();
                            String elNombre = objTL.get("nombre").toString();
                            // Busca en VideoPersonaje que exista la persona, sino lo crea
                            List<VideoPersonaje> existePersonaje = VideoPersonaje.find.where().ilike("nombre", elNombre).findList();
                            if (existePersonaje.size() == 0) {
                                vp.nombre = elNombre;
                                vp.catalogador = Personal.find.byId(Long.parseLong(session("usuario")));
                                Ebean.save(vp);
                                Ebean.refresh(vp);
                                idVP = vp.id;
                                tl.personaje = vp;
                            }
                            if (existePersonaje.size() != 0) {
                                tl.personaje = existePersonaje.get(0);
                            }
                        }
                    }
                    if (objTL.has("grado"))
                        if (objTL.get("grado").toString().length() != 0) {
                            tl.gradoacademico = objTL.get("grado").toString();
                        }
                    if (objTL.has("cargo"))
                        if (objTL.get("cargo").toString().length() != 0) {
                            tl.cargo = objTL.get("cargo").toString();
                        }
                    if (objTL.has("tema"))
                        if (objTL.get("tema").toString().length() != 0) {
                            tl.tema = objTL.get("tema").toString();
                        }
                    tl.catalogador = Personal.find.byId( Long.parseLong(session("usuario"))  );
                    vtk.timeline.add(tl);
                }
            }
            Ebean.update(vtk);
            Ebean.commitTransaction();
            flash("success", "Se agregó al acervo");
        } catch(Exception e) {
            Ebean.rollbackTransaction();
            System.out.println("Ocurrió un error al intentar grabar el registro. "+e);
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println("Se aplicó rollback a la transacción");
        }finally {
            Ebean.endTransaction();
        }
        return redirect( routes.VideotecaController.catalogo() );
    }




    private static VtkCatalogo losDatos(Form<VtkCatalogo> forma, DynamicForm fd) throws JSONException {
        VtkCatalogo vtk = forma.get();
        Personal usuarioActual = Personal.find.byId(Long.parseLong(session("usuario")));

        // Convertir duracion (hh:mm:ss) a segundos
        if (  forma.field("duracionStr").value().compareTo("hh:mm:ss")!=0 ) {
            Duracion duracion = new Duracion();
            duracion.convertir(forma.field("duracionStr").value());
            vtk.duracion = duracion.totalSegundos();
        }

        Long elId = 1L;
        if ( VtkCatalogo.find.all().size()>0)
            elId = Long.parseLong(Ebean.createSqlQuery("select max(id) x from vtk_catalogo").findUnique().getString("x"))+1;
        vtk.id =  elId;

        if (forma.field("accesibilidad_audio").value()!=null)
            if (forma.field("accesibilidad_audio").value().toString().compareTo("1")==0) {
                vtk.accesibilidadAudio = true;
            }
        if (forma.field("accesibilidad_video").value()!=null)
            if (forma.field("accesibilidad_video").value().toString().compareTo("1")==0) {
                vtk.accesibilidadVideo = true;
            }

        // Palabras clave  -  agregar id del catalogador
        vtk.palabrasClave.forEach(pc->pc.catalogador = usuarioActual );
        if (fd.field("palabrasClaveStr").value()!="") {
            JSONArray jsonArr = new JSONArray(forma.field("palabrasClaveStr").value());
            ////// Palabras Clave
            vtk.palabrasClave.clear();
            vtk.palabrasClave = new ArrayList<>();
            for (int x=0; x< jsonArr.length();x++){
                PalabraClave pc = new PalabraClave();
                pc.descripcion = jsonArr.getJSONObject(x).get("value").toString();
                pc.catalogador = usuarioActual;
                vtk.palabrasClave.add(pc);
            }
        }

        JSONArray jsonArrTL = new JSONArray();
        if (forma.field("txaTimeLine")!=null) {
            if (forma.field("txaTimeLine").value() != null) {
                if (forma.field("txaTimeLine").value() != null)
                    jsonArrTL = new JSONArray(forma.field("txaTimeLine").value());
            }
        }


        // Quitar formato  de obra que viene de la forma
        if (  vtk.obra.compareTo("__/__")==0  )
            vtk.obra = null;

        // CREDITOS
        if (forma.field("txtLosCreditos")!=null) {
            String texto = forma.field("txtLosCreditos").value();
            JSONArray arr = new JSONArray(texto);
            if (arr != null && arr.length() > 0) {
                List<Credito> creditos = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    Credito credito = new Credito();
                    JSONObject aux = arr.getJSONObject(i);
                    JSONObject tc = aux.getJSONObject("tipoCredito");
                    credito.tipoCredito = TipoCredito.find.byId(Long.parseLong(tc.get("id").toString()));
                    credito.personas = aux.getString("personas");
                    credito.catalogador = usuarioActual;
                    creditos.add(credito);
                }
                vtk.creditos = creditos;
            }
        }

        // Elimina de VtkDisponibilidad las disponibilidades iguales a null
        if (vtk.disponibilidades != null) {
            vtk.disponibilidades = vtk.disponibilidades.stream().filter(f -> f.disponibilidad != null).collect(Collectors.toList());
            if (  vtk.disponibilidades.stream().noneMatch(p->p.disponibilidad.id==999))
                vtk.disponibilidadOtra = "";
        }


        // Elimina de VtkAreatematica las areastematica iguales a null
        if (vtk.areastematicas != null){
            vtk.areastematicas = vtk.areastematicas.stream().filter(f -> f.areatematica != null).collect(Collectors.toList());
            if (vtk.areastematicas.stream().noneMatch(p->p.areatematica.id==999))
                vtk.areaTematicaOtra = "";
        }


        vtk.catalogador = usuarioActual;
        return vtk;
    }


    public static Result update() throws JSONException, IOException {
        System.out.println("\n\n\nDesde VideotecaController.update");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();
        DynamicForm fd = DynamicForm.form().bindFromRequest();
        System.out.println(forma);
        System.out.println("--------------------------");
        System.out.println(fd);
        Personal usuarioActual = Personal.find.byId(Long.parseLong(session("usuario")));

        Ebean.beginTransaction();
        try {
            VtkCatalogo db = VtkCatalogo.find
                    .fetch("niveles")
                    .where().eq("id", forma.get().id)
                    .findUnique();

            VtkCatalogo aux3 = forma.get();
            // Eliminar los null que pudieran existir en los OneToMany
            aux3.eventos = aux3.eventos.stream().filter(f->f.servicio!=null).collect(Collectors.toList());
            aux3.niveles = aux3.niveles.stream().filter(f->f.nivel!=null).collect(Collectors.toList());
            aux3.disponibilidades = aux3.disponibilidades.stream().filter(f->f.disponibilidad!=null).collect(Collectors.toList());
            aux3.areastematicas = aux3.areastematicas.stream().filter(f->f.areatematica!=null).collect(Collectors.toList());
            aux3.creditos = aux3.creditos.stream().filter(f->f.tipoCredito!=null || (f.personas!=null && f.personas.length()>0) ).collect(Collectors.toList());
            aux3.palabrasClave = aux3.palabrasClave.stream().filter(f->f.descripcion!=null && f.descripcion.length()>0).collect(Collectors.toList());

            // Si dispobilidades y/o areastemáticas tiene difetene a Otra (id!=999) eliminar el texto de disponibilidadotra y/o areatematicaotra
            if (aux3.disponibilidades.stream().noneMatch(p->p.disponibilidad.id==999))
                aux3.disponibilidadOtra = "";
            if (aux3.areastematicas.stream().noneMatch(p->p.areatematica.id==999))
                aux3.areaTematicaOtra = "";

            //Eventos
            for ( VtkEvento e :db.eventos){
                e.delete();
            }
            //Niveles
            for ( VtkNivel n :db.niveles){
                n.delete();
            }

            VtkCatalogo vtk = forma.get();
            vtk.catalogador = usuarioActual;

            //Disponibilidad
            db.disponibilidades.forEach(Model::delete);
            db.disponibilidadOtra=null;

            //Areas tematicas
            db.areastematicas.forEach(Model::delete);
            db.areaTematicaOtra=null;
            Ebean.update(db);


            // CREDITOS
            if (forma.field("txtLosCreditos")!=null) {
                String texto = forma.field("txtLosCreditos").value();
                JSONArray arr = new JSONArray(texto);
                if (arr != null && arr.length() > 0) {
                    List<Credito> creditos = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {

                        Credito credito = new Credito();
                        JSONObject aux = arr.getJSONObject(i);
                        JSONObject tc = aux.getJSONObject("tipoCredito");
                        credito.tipoCredito = TipoCredito.find.byId(Long.parseLong(tc.get("id").toString()));
                        credito.personas = aux.getString("personas");
                        credito.catalogador = usuarioActual;
                        creditos.add(credito);
                        vtk.creditos.add(credito);
                    }
                    //vtk.creditos = creditos;
                }
            }


            // Convertir duracion (hh:mm:ss) a segundos
            if (  forma.field("duracionStr").value().compareTo("hh:mm:ss")!=0 ) {
                Duracion duracion = new Duracion();
                duracion.convertir(forma.field("duracionStr").value());
                vtk.duracion = duracion.totalSegundos();
            }

            // Palabras clave
            for(int i=0; i<db.palabrasClave.size(); i++){
                Ebean.delete(db.palabrasClave.get(i));
            }
            if (fd.field("palabrasClaveStr").value().length()!=0) {
                JSONArray jsonArr = new JSONArray(forma.field("palabrasClaveStr").value());
                ////// Palabras Clave
                vtk.palabrasClave.clear();
                vtk.palabrasClave = new ArrayList<>();
                vtk.catalogador = usuarioActual;
                for (int x=0; x< jsonArr.length();x++){
                    PalabraClave pc = new PalabraClave();
                    String valor = jsonArr.getJSONObject(x).get("value").toString();
                    pc.descripcion = valor;
                    pc.catalogador = usuarioActual;
                    vtk.palabrasClave.add(pc);
                }
            }


            if (forma.field("accesibilidad_audio").value()!=null)
                if (forma.field("accesibilidad_audio").value().toString().compareTo("1")==0) {
                    vtk.accesibilidadAudio = true;
                }
            if (forma.field("accesibilidad_video").value()!=null)
                if (forma.field("accesibilidad_video").value().toString().compareTo("1")==0) {
                    vtk.accesibilidadVideo = true;
                }
            /////////// TimeLine
            if (forma.field("txaTimeLine").value() != null   &&  forma.field("txaTimeLine").value()!="" ){
                JSONArray jsonArrTimeLine = new JSONArray(forma.field("txaTimeLine").value());
                for (int i = 0; i < jsonArrTimeLine.length(); i++) {
                    VtkTimeLine tl = new VtkTimeLine();
                    JSONObject objTL = jsonArrTimeLine.getJSONObject(i);

                    if (objTL.has("desde") && !objTL.get("desde").toString().isEmpty() && !Objects.equals(objTL.get("desde").toString(), "hh:mm:ss")) {
                        Duracion dDesde = new Duracion();
                        dDesde.convertir(objTL.get("desde").toString());
                        tl.desde = dDesde.totalSegundos();
                    }
                    if (objTL.has("hasta") && !objTL.get("hasta").toString().isEmpty() && !Objects.equals(objTL.get("hasta").toString(), "hh:mm:ss")) {
                        Duracion dHasta = new Duracion();
                        dHasta.convertir(objTL.get("hasta").toString());
                        tl.hasta = dHasta.totalSegundos();
                    }
                    if (objTL.has("nombre") && !objTL.get("nombre").toString().isEmpty()) {
                        Long idVP = null;
                        VideoPersonaje vp = new VideoPersonaje();
                        String elNombre = objTL.get("nombre").toString();
                        // Busca en VideoPersonaje que exista la persona, sino lo crea
                        List<VideoPersonaje> existePersonaje = VideoPersonaje.find.where().ilike("nombre", elNombre).findList();
                        if (existePersonaje.size() == 0) {
                            vp.nombre = elNombre;
                            vp.catalogador =  Personal.find.byId( Long.parseLong(session("usuario")));
                            Ebean.save(vp);
                            Ebean.refresh(vp);
                            idVP = vp.id;
                            tl.personaje = vp;
                        }
                        if (existePersonaje.size() != 0) {
                            tl.personaje = existePersonaje.get(0);
                        }
                    }

                    if (objTL.has("grado") && !objTL.get("grado").toString().isEmpty()) {
                        tl.gradoacademico = objTL.get("grado").toString();
                    }
                    if (objTL.has("cargo") && !objTL.get("cargo").toString().isEmpty()) {
                        tl.cargo = objTL.get("cargo").toString();
                    }
                    if (objTL.has("tema") && !objTL.get("tema").toString().isEmpty()) {
                        tl.tema = objTL.get("tema").toString();
                    }
                    tl.catalogador = Personal.find.byId( Long.parseLong(session("usuario"))  );
                    vtk.timeline.add(tl);
                }
            }
            Ebean.update(vtk);
            Ebean.commitTransaction();
            flash("success", "Se actualizó al acervo ");
        } catch(Exception e) {
            flash("danger", "No se aplicarón los cambios. No fué posible la actualización ");
            System.out.println(ColorConsola.ANSI_RED+"Ocurrió un error al intentar actualizar el registro. "+e+ColorConsola.ANSI_RESET);
            e.printStackTrace();
            Ebean.rollbackTransaction();
        }finally {
            Ebean.endTransaction();
        }
        return redirect( routes.VideotecaController.catalogo());
    }

    /*
    public static Result update2() throws JSONException {
        System.out.println("\n\n\nDesde VideotecaController.update2");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();
        DynamicForm fd = DynamicForm.form().bindFromRequest();
        if(forma.hasErrors()) {
            return badRequest(createForm3.render(forma, TipoCredito.find.all(), VtkCampo.find.findList() ));
        }
        VtkCatalogo vtk = VtkCatalogo.find.byId(forma.get().id);
        /////////////////////////////////////////////////////////
        Personal usuarioActual = Personal.find.byId(Long.parseLong(session("usuario")));

        // Convertir duracion (hh:mm:ss) a segundos
        if (  forma.field("duracionStr").value().compareTo("hh:mm:ss")!=0 ) {
            Duracion duracion = new Duracion();
            duracion.convertir(forma.field("duracionStr").value());
            vtk.duracion = duracion.totalSegundos();
        }
        Long elId = 1L;
        if ( VtkCatalogo.find.all().size()>0)
            elId = Long.parseLong(Ebean.createSqlQuery("select max(id) x from vtk_catalogo").findUnique().getString("x"))+1;
        vtk.id =  elId;

        // Palabras clave  -  agregar id del catalogador
        vtk.palabrasClave.forEach(pc->pc.catalogador = usuarioActual );

        JSONArray jsonArr = new JSONArray(forma.field("txaPalabrasClave").value());
        JSONArray jsonArrTL = new JSONArray(forma.field("txaTimeLine").value());

        ////// Palabras Clave
        for ( PalabraClave palabra : vtk.palabrasClave ) {
            palabra.delete();
        }

        vtk.palabrasClave = new ArrayList<>();
        for (int x=0; x< jsonArr.length();x++){
            PalabraClave pc = new PalabraClave();
            pc.descripcion = jsonArr.getJSONObject(x).get("descripcion").toString();
            pc.catalogador = usuarioActual;
            vtk.palabrasClave.add(pc);
        }

        // Quitar formato  de obra que viene de la forma
        if (  vtk.obra!=null )
            if (  vtk.obra.compareTo("__/__")==0  )
                vtk.obra = null;

        // CREDITOS
        String texto = forma.field("txaCreditos").value();
        if (!texto.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(texto);
                JSONArray c = jsonObject.getJSONArray("losDatos");
                for (int i = 0; i < c.length(); i++) {
                    JSONObject obj = c.getJSONObject(i);
                    String A = obj.getString("tipo");
                    String B = obj.getString("creditos");
                    String[] arrCreditos = B.split(",");
                    List<Credito> creditos = new ArrayList<>();
                    for (String elCredito : arrCreditos) {
                        Credito cred = new Credito();
                        cred.tipoCredito = TipoCredito.find.byId(Long.parseLong(A));
                        cred.personas = elCredito;
                        cred.catalogador = usuarioActual;
                        vtk.creditos.add(cred);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        vtk.catalogador = usuarioActual;
        //////////////////////////////////////////////////////////


        vtk.update(vtk.id);
        vtk.refresh();
        Long idVTK = vtk.id;

        /////////// TimeLine
        vtk.timeline.clear();
        for ( VtkTimeLine tl : vtk.timeline ) {
            tl.delete();
        }
        JSONArray jsonArrTimeLine = new JSONArray(forma.field("txaTimeLine").value());
        for (int i=0; i< jsonArrTimeLine.length(); i++){
            VtkTimeLine tl = new VtkTimeLine();
            JSONObject objTL = jsonArrTimeLine.getJSONObject(i);

            if (objTL.get("desde").toString().length()!=0) {
                Duracion dDesde = new Duracion();
                dDesde.convertir(objTL.get("desde").toString());
                tl.desde = dDesde.totalSegundos();
            }
            if (objTL.get("hasta").toString().length()!=0) {
                Duracion dHasta = new Duracion();
                dHasta.convertir(objTL.get("hasta").toString());
                tl.hasta = dHasta.totalSegundos();
            }
            if (objTL.get("nombre").toString().length()!=0){
                Long idVP = null;
                VideoPersonaje vp = new VideoPersonaje();
                String elNombre = objTL.get("nombre").toString();
                // Busca en VideoPersonaje que exista la persona, sino lo crea
                List<VideoPersonaje> existePersonaje = VideoPersonaje.find.where().ilike("nombre", elNombre).findList();
                if ( existePersonaje.size()==0 ){
                    //vp.catalogo = VtkCatalogo.find.byId(idVTK);
                    vp.nombre = elNombre;
                    vp.save();
                    vp.refresh();
                    idVP = vp.id;
                    tl.personaje = vp;
                }
                if ( existePersonaje.size()!=0 ){
                    tl.personaje = existePersonaje.get(0);
                }


            }
            if (objTL.get("grado").toString().length()!=0){
                tl.gradoacademico = objTL.get("grado").toString();
            }
            if (objTL.get("cargo").toString().length()!=0){
                tl.cargo = objTL.get("cargo").toString();
            }
            if (objTL.get("tema").toString().length()!=0){
                tl.tema = objTL.get("tema").toString();
            }

            vtk.timeline.add(tl);
        }
        vtk.update();


        flash("success", "Se actualizó al acervo");
        return redirect( routes.VideotecaController.catalogo() );
    }

     */


    public static Result catalogoDelete() throws JSONException {
        JSONObject retorno = new JSONObject().put("estado","error");
        JsonNode json = request().body().asJson();
        VtkCatalogo aux = VtkCatalogo.find.byId(json.findValue("id").asLong());
        if (session("usuario").compareTo( Long.toString(aux.catalogador.id))==0) {
            //aux.delete();
            Ebean.delete(aux);
            retorno.put("estado", "eliminado");
            return ok(  retorno.toString()  );
        } else {
            return ok (views.html.operacionNoPermitida.render());
        }
    }

    public static Result buscaClaveID() throws JSONException {
        JsonNode json = request().body().asJson();
        JSONObject jo = new JSONObject();
        jo.put("estado", "error");
        String f = json.findValue("id").asText();
        VtkCatalogo c = VtkCatalogo.find.where().eq("clave", f).findUnique();
        if (c==null){
            jo.put("estado", "correcto");
        } else {
            jo.put("id", c.id);
            jo.put("clave", c.clave);
        }
        return ok ( jo.toString()  );
    }


    public static Result manual(){
        Form<VtkCatalogo> forma = play.data.Form.form(VtkCatalogo.class);
        List<TipoCredito> tipos = TipoCredito.find.all();
        List<TipoCredito> tiposOrdenados = tipos.stream()
                .sorted(Comparator.comparing(TipoCredito::getId))
                .collect(Collectors.toList());
        List<VtkCampo> campos = VtkCampo.find.all();
        return ok(
                views.html.videoteca.createForm3.render(forma, tiposOrdenados, campos)
        );
    }


    public static Result tranzapp(){
        return ok(views.html.videoteca.tranzapp.render());
    }

    public static Result tranzappUpload() throws JSONException, IOException {
        System.out.println("Desde tranzappUpload");
        Http.MultipartFormData body = request().body().asMultipartFormData();

        // Para obtener el nombre de los archivos
        List<Http.MultipartFormData.FilePart> archivos = body.getFiles();
        Logger.debug("body, num file: "+archivos.size());
        JSONArray ja = new JSONArray();
        for (Http.MultipartFormData.FilePart arch : archivos) {
            JSONObject jo = new JSONObject();
            Path p = Paths.get(arch.getFile().getPath());
            System.out.println("nombnre: "+p +"   "+arch.getFilename());
        }

       //--------------




        String urlTranzapp="https://tranzapp.dev.ipn.mx/";
        String apipage = "script.php";
        String downloadpage="f.php";

        System.out.println("10   "+request().body());
        Logger.debug("body "+body.getFiles());
        List<Http.MultipartFormData.FilePart> archs = body.getFiles();
        Logger.debug("body, num file: "+archs.size());
        JSONObject retorno = new JSONObject();

        if (1==6)
        for (Http.MultipartFormData.FilePart arch : archs) {
            JSONObject jo = new JSONObject();
            Path p = Paths.get(arch.getFile().getPath());

            Logger.debug("   "+arch.getKey()+ " -> "+ arch.getFile().getAbsolutePath()+"    "+arch.getFile().getName()+"  "+arch.getFilename());


            //String datos ="";
            String datos = lecturaComando("send", arch.getFile().getAbsolutePath());
            //System.out.println("-->>>>>>>>ya se hizo la lecturaComando");

            //System.out.println("-->>>>>>>>datos:"+sb.toString());
            String[] arrString = datos.split("\r");
            System.out.println("--->arrString:"+ Arrays.toString(arrString));

            jo.put("archivo", "archivo");
            jo.put("idArchivo", arrString[0]);
            jo.put("cveBorrado", arrString[1]);
            jo.put("descargar",   urlTranzapp+downloadpage+"?h="+arrString[0] );
            jo.put("borrar",   urlTranzapp+downloadpage+"?h="+arrString[0]+"&d="+arrString[1]);

            ja.put(jo);
        }
        retorno.put("datos", ja);
        System.out.println("El retornorno en json¸\n\n"+retorno);
        return ok( retorno.toString() );
    }


    // El comando a ejecutar es algo como:
    ///       usr/bin/curl -X POST --http1.0 -F time=month -F file=@/home/eduardo/archivoPrueba.txt https://tranzapp.dev.ipn.mx/script.php
    public static String lecturaComando(String tipo, String archivo){
            String line = "";
            String aux="";
            try {
                //String cmd = "/home/eduardo/tranzAppAPI.sh send /home/eduardo/archivoPrueba.txt";
                // En el archivo /home/eduardo/tranzAppAPI2.sh se llama a:
                //        /usr/bin/curl -X POST --http1.0 -F time=month -F file=@rutaCompletaDelArchivoASubir https://tranzapp.dev.ipn.mx/script.php
                String cmdAPI = "/home/eduardo/tranzAppAPI2.sh "+tipo+" "+archivo;
                //System.out.println("comando:"+cmdAPI);
                Runtime run = Runtime.getRuntime();
                Process pr = run.exec(cmdAPI);

                pr.waitFor();
                //System.out.println("Lectura terminada");
                BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

                //System.out.println("Retorno: -----");
                while ((line=buf.readLine())!=null) {
                    //System.out.println(line);
                    aux+=line+ "\r";
                }
                //System.out.println("---------------");
            } catch(java.io.IOException e) {
                System.out.print(e.getMessage());
            } catch (java.lang.InterruptedException e) {
                System.out.print(e.getMessage());
            }
            //System.out.println(aux);
            return aux;
    }


    public static Result tranzappUploadDirecto() throws IOException, JSONException {
        String urlTranzapp="https://tranzapp.dev.ipn.mx/";
        String apipage = "script.php";
        String downloadpage="f.php";
        ObjectNode retorno = Json.newObject();
        System.out.println("recibiendo");
        retorno.put("estado", "error");




        try {
            File file = request().body().asRaw().asFile();
            System.out.println("File getName "+ file.getName());
            String datos = lecturaComando("send", file.getAbsolutePath());
            System.out.println("datos de regreso del comando "+ datos);
            if (!datos.contains("Error")) {
                String[] arrString = datos.split("\r");
                System.out.println("--->arrString:" + Arrays.toString(arrString));
                retorno.put("estado", "subido");
                retorno.put("archivo", "archivo");
                retorno.put("idArchivo", arrString[0]);
                retorno.put("cveBorrado", arrString[1]);
                retorno.put("descargar", urlTranzapp + downloadpage + "?h=" + arrString[0]);
                retorno.put("borrar", urlTranzapp + downloadpage + "?h=" + arrString[0] + "&d=" + arrString[1]);
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("Ocurrió un error "+e.getMessage());
            return ok (retorno);
        }
        System.out.println(retorno);
        return ok(retorno);
    }




    public static Result tranzappUploadDirecto2() throws IOException, JSONException {
        String urlTranzapp="https://tranzapp.dev.ipn.mx/";
        String apipage = "script.php";
        String downloadpage="f.php";
        ObjectNode retorno = Json.newObject();
        System.out.println("recibiendo");
        retorno.put("estado", "error");

        Http.MultipartFormData body = request().body().asMultipartFormData();
//        System.out.println("\nMultipartFormData body.getFiles size: "+body.getFiles().size());

        JsonNode json = request().body().asJson();

        System.out.println("json:   "+json);


        DynamicForm fd = DynamicForm.form().bindFromRequest();
        System.out.println(fd);


        if (1==4) {
            List<Http.MultipartFormData.FilePart> archs = body.getFiles();
            Logger.debug("body, num file: " + archs.size());
            for (Http.MultipartFormData.FilePart arch : archs) {
                Logger.debug("   " + arch.getKey() + " -> " + arch.getFilename());
                String[] aux1 = arch.getKey().split("-");
                int tipo = Integer.parseInt(aux1[1]);
                int sec = Integer.parseInt(aux1[2]);
                PlantillaArchivo pa = new PlantillaArchivo();
                Path p = Paths.get(arch.getFile().getPath());
                byte[] byteFile = null;
                try {
                    byteFile = Files.readAllBytes(p);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pa.nombrearchivo = arch.getFilename();
                pa.contenttype = arch.getContentType();
                pa.contenido = byteFile;

            }


            File file = request().body().asRaw().asFile();
            System.out.println("File getName " + file.getName());
            String datos = lecturaComando("send", file.getAbsolutePath());
            System.out.println("datos de regreso del comando " + datos);
            if (!datos.contains("Error")) {
                String[] arrString = datos.split("\r");
                System.out.println("--->arrString:" + Arrays.toString(arrString));
                retorno.put("estado", "subido");
                retorno.put("archivo", "archivo");
                retorno.put("idArchivo", arrString[0]);
                retorno.put("cveBorrado", arrString[1]);
                retorno.put("descargar", urlTranzapp + downloadpage + "?h=" + arrString[0]);
                retorno.put("borrar", urlTranzapp + downloadpage + "?h=" + arrString[0] + "&d=" + arrString[1]);
            }
        }
        System.out.println(retorno);
        return ok(retorno);
    }


}
