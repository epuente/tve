package controllers;

import classes.CUrl;
import classes.ColorConsola;
import classes.Duracion;
import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.videoteca.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.nodes.Document;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import resumable.HttpUtils;
import resumable.ResumableInfo;
import resumable.ResumableInfoStorage;
import views.html.videoteca.createForm3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.data.Form.form;

import org.jsoup.*;

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

        // Filtrar por usuario actual
        int totalCatalogador = total.stream().filter(f -> f.catalogador.id ==  Long.parseLong(session("usuario"))).collect(Collectors.toList()).size();

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
        retorno.put("totalCatalogador", totalCatalogador);
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


    //@BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength =  2 * 1024 * 1024) // 2 MB
    //@BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength =  10 * 1024 * 1024 * 1024) // 10 GB MB
    //@BodyParser.Of(BodyParser.AnyContent.class)
    public static Result catalogoCreate2(){
        Form<VtkCatalogo> forma = play.data.Form.form(VtkCatalogo.class);
        List<TipoCredito> tipos = TipoCredito.find.all();
        List<TipoCredito> tiposOrdenados = tipos.stream()
                .sorted(Comparator.comparing(TipoCredito::getId))
                .collect(Collectors.toList());
        List<VtkCampo> campos = VtkCampo.find.all();
        //response().setHeader(CONTENT_LENGTH, "10737418240");
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
        } catch(Exception e){
            Ebean.rollbackTransaction();
            System.out.println("Ocurrió un error al intentar grabar el registro. " + e);
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println("Se aplicó rollback a la transacción");
        } finally {
            Ebean.endTransaction();
        }
        return redirect( routes.VideotecaController.catalogo() );
    }




    private static VtkCatalogo losDatos(Form<VtkCatalogo> forma, DynamicForm fd) throws JSONException {
        System.out.println("llegando a losDatos");
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

        if (forma.field("traduccionaudio").value()!=null)
            if (forma.field("traduccionaudio").value().toString().compareTo("1")==0) {
                vtk.traduccionaudio = true;
            }

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


        System.out.println("001 "+forma.field("txtLosCreditos").value());
        System.out.println("002 "+forma.field("txaCreditos").value());

        System.out.println("006 "+forma.field("pruebaCreditos").value());

        // CREDITOS
        if (forma.field("pruebaCreditos").value()!=null) {
            String texto = forma.field("pruebaCreditos").value();
            System.out.println("texto: "+texto);

            JSONArray arr = new JSONArray(texto);
            System.out.println("arr: "+arr.length());
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



        System.out.println("008 "+forma.field("txaTranzapp").value());

        // Tranzapp
        if (forma.field("txaTranzapp").value()!=null) {
            String texto = forma.field("txaTranzapp").value();
            System.out.println("texto: " + texto);

            JSONArray arr = new JSONArray(texto);
            System.out.println("arr: " + arr.length());
            if (arr != null && arr.length() > 0) {
                List<VtkEvidencia> evidencias = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    VtkEvidencia evidencia = new VtkEvidencia();
                    JSONObject aux = arr.getJSONObject(i);
                    evidencia.nombrearchivo = aux.getString("archivo");
                    evidencia.ligadescarga = aux.getString("lDescarga");
                    evidencia.ligaborrado = aux.getString("lBorrado");
                    evidencias.add(evidencia);
                }
                vtk.evidencias = evidencias;
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
            //if (1==1)
            //    throw new Exception();
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
            if (forma.field("txtLosCreditos").value()!=null) {
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

            // Tranzapp - Evidencias que ya estaban en la DB
            if (forma.field("txaTranzappDB").value()!=null) {
                String texto = forma.field("txaTranzappDB").value();
                JSONArray arr = new JSONArray(texto);
                Long[] arrIds = new Long[arr.length()];
                List<Long> arr2 = new ArrayList<>();
                if (arr != null && arr.length() > 0) {
                    List<VtkEvidencia> evidencias = new ArrayList<>();
                    int ii=0;
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject aux = arr.getJSONObject(i);
                        arrIds[ii++] =   Long.parseLong(aux.get("id").toString());
                        arr2.add(    Long.parseLong(aux.get("id").toString())  );
                    }
                }
                System.out.println("arreglo de los ids en la DB: "+ Arrays.toString(arrIds));

                // Conserva los ids que permanecieron en el formulario
                List<VtkEvidencia> permanecen = VtkEvidencia.find.where().eq("catalogo.id", vtk.id).in("id", arr2).findList();
                vtk.evidencias.addAll(permanecen);
            }


            // Tranzapp - Evidencias
            if (forma.field("txaTranzapp").value()!=null) {
                String texto = forma.field("txaTranzapp").value();
                JSONArray arr = new JSONArray(texto);
                if (arr != null && arr.length() > 0) {
                    List<VtkEvidencia> evidencias = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        VtkEvidencia evidencia = new VtkEvidencia();
                        JSONObject aux = arr.getJSONObject(i);
                        evidencia.nombrearchivo = aux.getString("archivo");
                        evidencia.ligadescarga = aux.getString("lDescarga");
                        evidencia.ligaborrado = aux.getString("lBorrado");
                        evidencia.id = aux.optLong("id");
                        evidencias.add(evidencia);
                    }
                    vtk.evidencias.addAll(evidencias);
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






    // El comando a ejecutar es algo como:
    ///       usr/bin/curl -X POST --http1.0 -F time=month -F file=@/home/eduardo/archivoPrueba.txt;filename=estees.txt https://tranzapp.dev.ipn.mx/script.php
    public static String lecturaComandoNombre(String tipo, String archivo, String nombre){
        String line = "";
        String aux="";
        try {
            //String cmd = "/home/eduardo/tranzAppAPI.sh send /home/eduardo/archivoPrueba.txt";
            // En el archivo /home/eduardo/tranzAppAPI2.sh se llama a:
            //        /usr/bin/curl -X POST --http1.0 -F time=month -F file=@rutaCompletaDelArchivoASubir https://tranzapp.dev.ipn.mx/script.php
            //String cmdAPI = "/home/eduardo/tranzAppAPI3.sh "+tipo+" "+archivo+" "+nombre;
            String cmdAPI = "/home/eduardo/tranzAppAPI3.sh "+tipo+" "+archivo+" "+nombre;
            System.out.println("comando:"+cmdAPI);
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec(cmdAPI);

            pr.waitFor();
            System.out.println("Lectura terminada");
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            System.out.println("Retorno: -----");
            while ((line=buf.readLine())!=null) {
                System.out.println(line);
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


    public static Result auxLecturaComando() throws JSONException {
        System.out.println("Desde auxLecturaComando.............");
        JsonNode json = request().body().asJson();
        System.out.println(json);
        String tipo = json.findValue("tipo").toString();
        String archivo = json.findValue("archivo").toString();
        System.out.println(tipo+" -  "+archivo);

        File file = new File(archivo);
        String ruta = file.getAbsoluteFile().getPath();
        ruta = ruta.replaceAll("\"", "");


        System.out.println("ruta "+ruta);


        //String[] aux = lecturaComandoNombre(tipo, archivo, archivo).split("\r");
        String[] aux = lecturaComandoNombre(tipo, "\""+ruta+"\"", archivo).split("\r");
        System.out.println("aux:"+ Arrays.toString(aux));
        System.out.println("aux taam:"+aux.length);
        JSONObject jo = new JSONObject();
        jo.put("descarga", aux[0]);
//        jo.put("borrado", aux[1]);
        return ok(jo.toString());
    }


    // 10 gigas  = 10 485 760 kb

    //@BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength = 10 * 1024 * 1024 * 1024) // 10 GB
    //@BodyParser.Of(value = BodyParser.AnyContent.class, maxLength = 10 * 1024 * 1024 * 1024)
    //@BodyParser.Of(BodyParser.AnyContent.class)
    @BodyParser.Of(value = BodyParser.MultipartFormData.class, maxLength = 10)
    public static Result uploadForma(){
        System.out.println("Desde VideotecaController.uploadForma....");
        ObjectNode retorno = Json.newObject();
        String urlTranzapp="https://tranzapp.dev.ipn.mx/";
        String apipage = "script.php";
        String downloadpage="f.php";
        Http.MultipartFormData body = request().body().asMultipartFormData();

        //System.out.println("\nMultipartFormData body: "+body);
//        System.out.println("\nMultipartFormData body.getFiles size: "+body.getFiles().size());

  //      Logger.debug("body "+body.getFiles());






        try {
            Logger.info("isMaxSizeExceeded "+request().body().isMaxSizeExceeded()  );
            Logger.info(".version "+request().version()  );
            Logger.info(".path "+request().path()  );
            Logger.info(".headers "+request().headers()  );

            Map<String, String[]> m = request().headers();

            String[] rf = m.get("Referer");
            for ( String aux : rf){
                System.out.println("Referer "+aux);
            }

            Logger.debug("HEADERS");
            m.forEach((key, values) -> {
                // process key and values
                System.out.println("  ->"+key);
                for ( String v: values) {
                    System.out.println("     " + v);
                }
            });


            System.out.println("\n  m.size() "+ m.size());





            List<Http.MultipartFormData.FilePart> archs = body.getFiles();

            Logger.debug("body, num file: "+archs.size());

            retorno.put("estado", "error");

            for (Http.MultipartFormData.FilePart arch : archs) {
                Path p = Paths.get(arch.getFile().getPath());
                System.out.println(" ");
                Logger.debug("   "+arch.getKey()+ " -> "+ arch.getFilename()+"  ");
                Logger.debug("   "+p.toString()+"   "+p.getFileSystem()+"    "+p.getNameCount());
                Logger.debug("             "+p.toString()+" "+"'"+arch.getFilename()+"'");

                Logger.info("Enviando a tranzapp...");
                String datos = lecturaComandoNombre("send", p.toString(),  arch.getFilename().replaceAll(" ","_"));
                Logger.info("...");
                if (!datos.contains("Error")) {
                    String[] arrString = datos.split("\r");
                    System.out.println("--->arrString:" + Arrays.toString(arrString));
                    retorno.put("nombreArchivo", arch.getFilename());
                    retorno.put("estado", "subido");
                    retorno.put("archivo", "archivo");
                    retorno.put("idArchivo", arrString[0]);
                    retorno.put("cveBorrado", arrString[1]);
                    retorno.put("descargar", urlTranzapp + downloadpage + "?h=" + arrString[0]);
                    retorno.put("borrar", urlTranzapp + downloadpage + "?h=" + arrString[0] + "&d=" + arrString[1]);
                }


                //ObjectNode datos = javaCurl(arch.getFilename().replaceAll(" ", "_"));

            }
        } catch (Exception e){
            System.out.println("Ocurrio un errorrrrr "+e.getCause()+"\n"+e.getCause()+"\n"+e.getMessage());
            e.printStackTrace();
        }
        return ok(retorno);
    }



    private static CUrl.Resolver<Document> htmlResolver = new CUrl.Resolver<Document>() {
        @SuppressWarnings("unchecked")
        @Override
        public org.jsoup.nodes.Document resolve(int httpCode, byte[] responseBody) throws Throwable {
            String html = new String(responseBody, "UTF-8");
            return Jsoup.parse(html);
        }
    };

    // Ejecuta el comando curl (una version de java) y regresa un json
    // El problema con este método es que recibe la ruta temporal y no se le puede cambiar el nombre del archivo a guardar en tranzap como se hace con un curl desde la linea de comando
    public static Result javaCurl(){
        System.out.println("Desde javaCurl........");
        ObjectNode retorno = Json.newObject();
        retorno.put("estado", "error");

        CUrl curl = new CUrl("https://tranzapp.dev.ipn.mx/script.php")
                .form("file", new CUrl.FileIO("/home/eduardo/archivoPrueba.txt"))
                .form("time", "day")
                ;
        org.jsoup.nodes.Document html = curl.exec(htmlResolver, null);
        System.out.println("comando:  "+curl);
        // curl --url "https://tranzapp.dev.ipn.mx/script.php" -X "POST" --http1.0 -F "time=month" -F "file=@/home/eduardo/archivPrueba.txt"

        String[] valores = html.select("body").text().split(" ");
        if (curl.getHttpCode()==200) {
            retorno.put("estado", "ok");
            retorno.put("idArchivo", valores[0]);
            retorno.put("cveBorrado", valores[1]);
        }
        return ok(retorno);
    }




    public static Result eliminaTranzapp() throws IOException {
        JsonNode json = request().body().asJson();

        Logger.debug("eliminaTranzapp2 recibe:"+json);

        //org.jsoup.nodes.Document html = Jsoup.connect("https://tranzapp.dev.ipn.mx/script.php?h=1N05ADR7&d=ec40a").post();
        org.jsoup.nodes.Document html = Jsoup.connect(json.findValue("liga").asText()).post();
        System.out.println(html.select("body").text());
        return ok ();
    }

    public static Result dragdrop(){
        return ok (views.html.videoteca.dragdrop.render());
    }

    public static Result pruebaTA(){
        System.out.println("\n\n\n\nDesde VTKController.pruebaTA");
        DynamicForm fd = DynamicForm.form().bindFromRequest();
        System.out.println(fd);


        return ok( "ok" );
    }

    public static Result pruebaResumable(){
        return ok (views.html.videoteca.resumable.render());
    }





    public static Result pruebaRecepcionResumable2() {
        System.out.println("pruebaRecepcionResumable2!");
        String urlTranzapp="https://tranzapp.dev.ipn.mx/";
        String apipage = "script.php";
        String downloadpage="f.php";
        try {
            int resumableChunkNumber = getResumableChunkNumber(request());

            ResumableInfo info = getResumableInfo(request());

            Logger.info(info.resumableFilename);
            RandomAccessFile raf = new RandomAccessFile(info.resumableFilename, "rw");

            //Seek to position
            raf.seek((resumableChunkNumber - 1) * (long)info.resumableChunkSize);

            //Save to file
            InputStream is = new FileInputStream(request().body().asRaw().asFile());
            long readed = 0;
            long content_length = request().body().asRaw().size();
            //byte[] bytes = new byte[1024 * 100];
            byte[] bytes = new byte[1024 * 10];
            while(readed < content_length) {
                int r = is.read(bytes);
                if (r < 0)  {
                    break;
                }
                raf.write(bytes, 0, r);
                readed += r;
            }
            raf.close();
            is.close();


            //Mark as uploaded.
            info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
            if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
                ResumableInfoStorage.getInstance().remove(info);
                System.out.println("terminado!!!!!");

                System.out.println(info.resumableFilename);
                System.out.println(info.resumableFilePath);
                System.out.println(info.resumableRelativePath);
                System.out.println(  info.uploadedChunks.size() );

                // Ya se subió al server, ahora enviarlo a tranzapp
                String datos = lecturaComandoNombre("send", info.resumableFilename,  info.resumableFilename);
                JSONObject retorno = new JSONObject();
                retorno.put("estado", "error");
                if (!datos.contains("Error")) {
                    String[] arrString = datos.split("\r");
                    System.out.println("--->arrString:" + Arrays.toString(arrString));
                    retorno.put("nombreArchivo", info.resumableFilename);
                    retorno.put("estado", "subido");
                    retorno.put("archivo", "archivo");
                    retorno.put("idArchivo", arrString[0]);
                    retorno.put("cveBorrado", arrString[1]);
                    retorno.put("descargar", urlTranzapp + downloadpage + "?h=" + arrString[0]);
                    retorno.put("borrar", urlTranzapp + downloadpage + "?h=" + arrString[0] + "&d=" + arrString[1]);

                    System.out.println(retorno);
                    Path filePath = Paths.get(info.resumableFilename);
                    try {
                        Files.delete(filePath);
                        Logger.info("archivo temporal eliminado: "+filePath);
                    } catch (IOException e) {
                        System.err.println("Error deleting file: " + e.getMessage());
                    }
                }

                return ok("All finished.");
            } else {
                return ok("Upload");
            }
        } catch (Exception e) {
            System.out.println("EXC EPTIOB "+e.getCause()+  "   "+e.getMessage());
            e.printStackTrace();
            return internalServerError();
        }
    }



    public static Result uploadStatus() {
        System.out.println("UPLOAD STATUS!");
        try {

            int resumableChunkNumber        = getResumableChunkNumber(request());

            ResumableInfo info = getResumableInfo(request());

            if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
                return ok("Uploaded."); //This Chunk has been Uploaded.
            } else {
                return notFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError();
        }
    }


    private static int getResumableChunkNumber(Http.Request request) {
        return HttpUtils.toInt(request.getQueryString("resumableChunkNumber"), -1);
    }

    private static ResumableInfo getResumableInfo(Http.Request request) throws Exception {
//        String base_dir = "C:/TMP/";
        String base_dir = System.getProperty("java.io.tmpdir") + File.pathSeparator + "UPLOAD_DIR/";

        int resumableChunkSize          = HttpUtils.toInt(request.getQueryString("resumableChunkSize"), -1);
        long resumableTotalSize         = HttpUtils.toLong(request.getQueryString("resumableTotalSize"), -1);
        String resumableIdentifier      = request.getQueryString("resumableIdentifier");
        String resumableFilename        = request.getQueryString("resumableFilename").replaceAll(" ", "_");
        String resumableRelativePath    = request.getQueryString("resumableRelativePath");
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        new File(base_dir).mkdir();
        String resumableFilePath        = new File(base_dir, resumableFilename).getAbsolutePath() + ".temp";

        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
        if (!info.vaild())         {
            storage.remove(info);
            throw new Exception("Invalid request params.");
        }
        return info;
    }






}



