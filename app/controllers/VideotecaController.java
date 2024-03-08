package controllers;

import classes.ColorConsola;
import classes.Duracion;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import models.videoteca.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.html.videoteca.createForm3;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;



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
            Logger.debug(at.id +" - "+at.descripcion);
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
        Logger.debug(strQAnio);

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
        System.out.println( "parametros 0:"+ request() );
        System.out.println( "parametros draw:"+ request().getQueryString("draw"));
        System.out.println( "parametros start:"+ request().getQueryString("start"));
        System.out.println( "parametros length:"+ request().getQueryString("length"));
        System.out.println( "parametros seach[value]:"+ request().getQueryString("search[value]"));

        System.out.println( "parametros order[0][column]:"+ request().getQueryString("order[0][column]"));
        System.out.println( "parametros order[0][dir]:"+ request().getQueryString("order[0][dir]"));


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
        System.out.println("retornando:  "+json2.toString());
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

/*
    public static Result tsQuery() throws JSONException {
        JsonNode json = request().body().asJson();
        System.out.println("Desde tsQuery");
        System.out.println(json);
        String cadena = json.findValue("cadena").asText();

        System.out.println(cadena);

        String query = "SELECT plainto_tsquery('spanish', '"+cadena+"')";

        SqlRow sqlRow = Ebean.createSqlQuery(query).findUnique();
        String ts1 = sqlRow.getString("plainto_tsquery").toString();
        System.out.println("tsQuery:  "+ts1);

        String ts2 = ts1.replaceAll("'","");
        System.out.println("tsQuery2:  "+ts2);

        JSONObject jo = new JSONObject();
        jo.put("tsquery", ts2);

        return ok (  jo.toString() );
    }
*/
    /*
    public static Result tsCoincidencias() throws JSONException {
        JsonNode json = request().body().asJson();
        System.out.println("Desde tsCoincidencias");
        System.out.println(json);
        String cadena = json.findValue("cadena").asText();

        String query = "SELECT id, descripcion, ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+cadena+"'))" +
                "FROM serie s " +
                "WHERE to_tsvector(coalesce(descripcion, ' ')) @@ to_tsquery('"+cadena+"') " +
                "ORDER BY ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+cadena+"')) desc";

        System.out.println(query);

        List<SqlRow> sqlRows = Ebean.createSqlQuery(query).findList();
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();

        for (SqlRow r :sqlRows ){
            JSONObject joAux = new JSONObject();
            System.out.println("    -->"+r.getString("descripcion"));
            //ja.put(r.getString("descripcion"));
            joAux.put("id", r.getLong("id"));
            joAux.put("descripcion", r.getString("descripcion"));
            ja.put(joAux);
        }
        jo.put("estado","ok");
        jo.put("coincidencias", ja);
        return ok ( jo.toString() );
    }
    */

    // Aqui se usan 2 metodos, el primero es el clásico de busqueda aproximada (comparar una cadena con ilike y unaccent)
    // El segundo usa textsearch con ts_vector y ts_query
    // Se regresa un dataset con la combinacion de los dos resultados, poniendo primero los resultados de la comparación clásica
    public static Result textsearch() throws JSONException {
        System.out.println("\n\n\n\nDesde VTKController.textsearch");
        JsonNode json = request().body().asJson();
        System.out.println(json);
        JSONObject jo = new JSONObject();
    //    JSONArray ja = new JSONArray();
        String campo = json.findValue("campo").asText();
        String cadena = json.findValue("cadena").asText();
        Logger.debug("campo "+campo);
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
        Logger.debug(query1);
        List<SqlRow> sqlRows1 = Ebean.createSqlQuery(query1).findList();

        final RawSql rawSql1 = RawSqlBuilder.unparsed(query1)
                .columnMapping("id", "id")
                .columnMapping("descripcion", "descripcion")
                .create();
        List<Serie> list1 = Serie.find.setRawSql(rawSql1).findList();
        Logger.debug("list1 tam: "+list1.size());


        // Ahora se usa textsearch
        // Obtener tsquery
        String queryTSQuery = "SELECT plainto_tsquery('spanish', '"+cadena+"')";
        SqlRow sqlRow2 = Ebean.createSqlQuery(queryTSQuery).findUnique();
        //Se obtiene el ts_query y le quita las comillas simples
        String ts = sqlRow2.getString("plainto_tsquery").toString().replaceAll("'","");;
        Logger.debug("ts: '"+ts+"'");
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
        Logger.debug("query3:    "+query3);


        final RawSql rawSql3 = RawSqlBuilder.unparsed(query3)
                .columnMapping("id", "id")
                .columnMapping("descripcion", "descripcion")
                .create();
        List<Serie> list2 = Serie.find.setRawSql(rawSql3).findList();

        List<Serie> list3 = new ArrayList<Serie>();
        Logger.debug("list2 tam: "+list2.size());


        //ja.put(list1);
        if ( !list1.isEmpty() &&  list1.size()>0)
            list3.addAll(list1);

        Logger.debug("100");

        if ( !list2.isEmpty() &&  list2.size()>0)
            list3.addAll(list2);
        Logger.debug("200");

        Logger.debug("300");

        // Para el siguiente código se agregaron los metodos equals y hasCode en la clase Serie
        // Elimina elementos de la lista repetidos
        List<Serie> uniqueSeriesList = list3.stream()
                .distinct()
                .collect(Collectors.toList());
        Logger.debug("400");

        // Lista de objetos Serie únicos se pasan a un array de json
        JSONArray jaUnique = new JSONArray();
        for (Serie serie : uniqueSeriesList) {
            //System.out.println("    "+serie.id+" - "+serie.descripcion);
            JSONObject joUnique = new JSONObject();
            joUnique.put("id", serie.id);
            joUnique.put("descripcion", serie.descripcion);
            jaUnique.put(  joUnique  );
        }

        Logger.debug("uniqueSeriesList (TOTAL) tam: "+uniqueSeriesList.size());
        //jo.put("coincidencias", uniqueSeriesList);
        jo.put("coincidencias", jaUnique);
//        Logger.debug("500");
//        Logger.debug(jo.toString()    );
        return ok ( jo.toString() );
    }


    public static Result nuevaSerie() throws JSONException {
        Logger.debug("Desde VideotecaController.nuevaSerie");
        JSONObject joRetorno = new JSONObject();
        JsonNode json = request().body().asJson();
        Logger.debug(String.valueOf(json));
        joRetorno.put("estado", "error");


        //JsonNode personJson = Json.toJson(person);
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

    /*
    public static Result nuevaProduccion() throws JSONException {
        Logger.debug("Desde VideotecaController.nuevaProduccion");
        JSONObject joRetorno = new JSONObject();
        JsonNode json = request().body().asJson();
        Logger.debug(String.valueOf(json));
        joRetorno.put("estado", "error");

        Produccion p = Json.fromJson(json, Produccion.class);
        p.catalogador = Personal.find.byId(  Long.parseLong(session("usuario"))  );
        p.save();
        p.refresh();
        joRetorno.put("estado", "ok");
        joRetorno.put("id", p.id);
        joRetorno.put("descripcion", p.descripcion);
        return ok (joRetorno.toString());
    }
     */

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



/*
    public static Result serieList(){
        Map<String, String> seriesOptions = Serie.options();
        JSONArray ja = new JSONArray();
        seriesOptions.forEach((k, v)->{
           JSONObject jo = new JSONObject();
            try {
                jo.put("id", k);
                jo.put("descripcion", v);
                ja.put(jo);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return ok (ja.toString());
    }

 */




    // Aqui se usa 1 metodo,  (compara con la totalidad del campo insensible a mayúsculas y unaccent)
    public static Result textsearchCampoCompleto() throws JSONException {
        System.out.println("\n\n\n\nDesde VideotecaController.textsearchCampoCompleto");
        JsonNode json = request().body().asJson();
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        String campo = json.findValue("campo").asText();
        String cadena = json.findValue("cadena").asText();
        Logger.debug(cadena +" - "+campo);
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
        Logger.debug("list1 EXISTE tam: "+sqlRows1.size());
        jo.put("coincidencias", sqlRows1.size());
        Logger.debug(jo.toString());
        return ok ( jo.toString() );
    }





    public static Result catalogoEdit(Long id){
        VtkCatalogo catalogo = VtkCatalogo.find.byId(id);
        //Logger.debug(  session("usuario") +"  -  "+ );
        if (session("usuario").compareTo( Long.toString(catalogo.catalogador.id))==0) {
            Logger.debug("001 id "+id);
            Form<VtkCatalogo> forma = form(VtkCatalogo.class).fill(catalogo);
            Logger.debug("002 FORMA "+forma);
            Duracion d = new Duracion();
            if (catalogo.duracion!=null)
                d = new Duracion(catalogo.duracion);
            else
                d = new Duracion(0L);
            Logger.debug("003 duracion " + d.horas+":"+d.minutos+":"+d.segundos);
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
        System.out.println("\n\n\nDesde VideotecaController.save...");
        DynamicForm fd = DynamicForm.form().bindFromRequest();
        System.out.println(fd);
        System.out.println("--------------------------");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();
        System.out.println(forma);

        Ebean.beginTransaction();
        try {
            if (forma.hasErrors()) {
                return badRequest(createForm3.render(forma, TipoCredito.find.all(), VtkCampo.find.all()));
            }
            //VtkCatalogo vtk = forma.get();
            VtkCatalogo vtk = losDatos(forma, fd);
            System.out.println("Despues de losDatos");
            vtk.eventos.removeIf(r -> r.servicio == null);
            vtk.niveles.removeIf(r -> r.nivel == null);
            vtk.timeline.clear();
            //
            System.out.println("Antes de save");
            Ebean.save(vtk);
            System.out.println("Despues de save");


            Ebean.refresh(vtk);
            System.out.println("Despues de refresh");

            Long idVTK = vtk.id;
            System.out.println("Despues de idVTK");

            /////////// TimeLine
            Logger.debug("--->  Antes del return de losDatos " + forma.field("txaTimeLine").value());


            if (forma.field("txaTimeLine").value() != null   &&  forma.field("txaTimeLine").value()!="" ){
                JSONArray jsonArrTimeLine = new JSONArray(forma.field("txaTimeLine").value());
                for (int i = 0; i < jsonArrTimeLine.length(); i++) {
                    VtkTimeLine tl = new VtkTimeLine();
                    JSONObject objTL = jsonArrTimeLine.getJSONObject(i);

                    if (objTL.get("desde").toString().length() != 0) {
                        Duracion dDesde = new Duracion();
                        dDesde.convertir(objTL.get("desde").toString());
                        tl.desde = dDesde.totalSegundos();
                    }
                    if (objTL.get("hasta").toString().length() != 0) {
                        Duracion dHasta = new Duracion();
                        dHasta.convertir(objTL.get("hasta").toString());
                        tl.hasta = dHasta.totalSegundos();
                    }
                    if (objTL.get("nombre").toString().length() != 0) {
                        Long idVP = null;
                        VideoPersonaje vp = new VideoPersonaje();
                        String elNombre = objTL.get("nombre").toString();
                        // Busca en VideoPersonaje que exista la persona, sino lo crea
                        List<VideoPersonaje> existePersonaje = VideoPersonaje.find.where().ilike("nombre", elNombre).findList();
                        if (existePersonaje.size() == 0) {
                            //vp.catalogo = VtkCatalogo.find.byId(idVTK);
                            vp.nombre = elNombre;
                            vp.catalogador =  Personal.find.byId( Long.parseLong(session("usuario")));
                            //vp.save();
                            Ebean.save(vp);
                            //vp.refresh();
                            Ebean.refresh(vp);
                            idVP = vp.id;
                            tl.personaje = vp;
                        }
                        if (existePersonaje.size() != 0) {
                            tl.personaje = existePersonaje.get(0);
                        }


                    }
                    if (objTL.get("grado").toString().length() != 0) {
                        tl.gradoacademico = objTL.get("grado").toString();
                    }
                    if (objTL.get("cargo").toString().length() != 0) {
                        tl.cargo = objTL.get("cargo").toString();
                    }
                    if (objTL.get("tema").toString().length() != 0) {
                        tl.tema = objTL.get("tema").toString();
                    }
                    tl.catalogador = Personal.find.byId( Long.parseLong(session("usuario"))  );
                    vtk.timeline.add(tl);
                }
            }

            vtk.timeline.forEach(tm->System.out.println("- - - -Desde "+tm.desde+" "+tm.hasta+" "+tm.personaje.nombre));


            //vtk.update();
            Ebean.update(vtk);
            Ebean.commitTransaction();
            flash("success", "Se agregó al acervo");
        } catch(Exception e) {
            Ebean.rollbackTransaction();
            System.out.println("Ocurrió un error al intentar grabar el registro. "+e);
        }finally {
            Ebean.endTransaction();
        }
        return redirect( routes.VideotecaController.catalogo() );
    }




    private static VtkCatalogo losDatos(Form<VtkCatalogo> forma, DynamicForm fd) throws JSONException {
        VtkCatalogo vtk = forma.get();
        Personal usuarioActual = Personal.find.byId(Long.parseLong(session("usuario")));

        // Convertir duracion (hh:mm:ss) a segundos
        Logger.debug(forma.field("duracionStr").value());
        if (  forma.field("duracionStr").value().compareTo("hh:mm:ss")!=0 ) {
            Duracion duracion = new Duracion();
            duracion.convertir(forma.field("duracionStr").value());
            vtk.duracion = duracion.totalSegundos();
        }



        System.out.println("duracion:"+vtk.duracion);
        Long elId = 1L;
        if ( VtkCatalogo.find.all().size()>0)
            elId = Long.parseLong(Ebean.createSqlQuery("select max(id) x from vtk_catalogo").findUnique().getString("x"))+1;
        vtk.id =  elId;

        Logger.debug("accesibilidad_video: "+forma.field("accesibilidad_video").value());
        Logger.debug("accesibilidad_audio: "+forma.field("accesibilidad_audio").value());

        if (forma.field("accesibilidad_audio").value()!=null)
            if (forma.field("accesibilidad_audio").value().toString().compareTo("1")==0) {
                Logger.debug("ACC AUDIO OK");
                vtk.accesibilidadAudio = true;
            }
        if (forma.field("accesibilidad_video").value()!=null)
            if (forma.field("accesibilidad_video").value().toString().compareTo("1")==0) {
                Logger.debug("ACC VIDEO OK");
                vtk.accesibilidadVideo = true;
            }


        // Palabras clave  -  agregar id del catalogador
        vtk.palabrasClave.forEach(pc->pc.catalogador = usuarioActual );


        Logger.debug("txaPalabrasClave .........");
        Logger.debug(forma.data().toString());
        Logger.debug(   fd.field("palabrasClaveStr").value() );

        if (fd.field("palabrasClaveStr").value()!="") {
            JSONArray jsonArr = new JSONArray(forma.field("palabrasClaveStr").value());
            Logger.debug("--->002"+String.valueOf(jsonArr));
            ////// Palabras Clave
            vtk.palabrasClave.clear();
            vtk.palabrasClave = new ArrayList<>();
            Logger.debug("iniciando ciclo: ");

            for (int x=0; x< jsonArr.length();x++){
                PalabraClave pc = new PalabraClave();
                pc.descripcion = jsonArr.getJSONObject(x).get("value").toString();
                pc.catalogador = usuarioActual;
                Logger.debug(pc.descripcion+"  -  "+pc.catalogador.nombreCompleto());
                vtk.palabrasClave.add(pc);
            }
        }
        System.out.println("...000...");
        JSONArray jsonArrTL = new JSONArray();

        System.out.println("...001...");
        if (forma.field("txaTimeLine")!=null) {
            System.out.println(forma.field("txaTimeLine").value());
            if (forma.field("txaTimeLine").value() != null) {
                System.out.println(forma.field("txaTimeLine").value());
                if (forma.field("txaTimeLine").value() != null)
                    jsonArrTL = new JSONArray(forma.field("txaTimeLine").value());
            }

        }


        // Quitar formato  de obra que viene de la forma
        if (  vtk.obra.compareTo("__/__")==0  )
            vtk.obra = null;


        Logger.debug("--->003 "+forma.field("txtLosCreditos").value());

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

        Logger.debug("--->004 ");

        Logger.debug("--->008 Despues del texto ");


        vtk.catalogador = usuarioActual;
        Logger.debug("--->009  Antes del return de losDatos");

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
            VtkCatalogo db = VtkCatalogo.find.byId(forma.get().id);

            VtkCatalogo vtk = forma.get();
            vtk.catalogador = usuarioActual;

            // CREDITOS
            Logger.debug("--CREDITOS--");
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
                        Logger.debug("persona-credito:" + credito.personas);
                        creditos.add(credito);
                        vtk.creditos.add(credito);
                    }
                    //vtk.creditos = creditos;
                }
            }


            // Convertir duracion (hh:mm:ss) a segundos
            Logger.debug(forma.field("duracionStr").value());
            if (  forma.field("duracionStr").value().compareTo("hh:mm:ss")!=0 ) {
                Duracion duracion = new Duracion();
                duracion.convertir(forma.field("duracionStr").value());
                vtk.duracion = duracion.totalSegundos();
            }

            // Palabras clave
            System.out.println("PC: "+fd.field("palabrasClaveStr").value());
            //db.palabrasClave.forEach(pc->Ebean.delete(pc));
            for(int i=0; i<db.palabrasClave.size(); i++){
                System.out.println("borrando pc");
                Ebean.delete(db.palabrasClave.get(i));
            }
            System.out.println("PC: "+fd.field("palabrasClaveStr").value().length());

            if (fd.field("palabrasClaveStr").value().length()!=0) {
                System.out.println("entrando al ciclo de PC");

                JSONArray jsonArr = new JSONArray(forma.field("palabrasClaveStr").value());
                Logger.debug("--->002"+String.valueOf(jsonArr));
                ////// Palabras Clave
                vtk.palabrasClave.clear();
                vtk.palabrasClave = new ArrayList<>();
                vtk.catalogador = usuarioActual;
                Logger.debug("iniciando ciclo: ");
                Logger.debug("tam jsonArr: "+jsonArr.length());

                for (int x=0; x< jsonArr.length();x++){
                    PalabraClave pc = new PalabraClave();
                    String valor = jsonArr.getJSONObject(x).get("value").toString();
                    Logger.debug("valor:"+valor);
                    pc.descripcion = valor;
                    Logger.debug("usuarioActual:"+usuarioActual);
                    pc.catalogador = usuarioActual;
                    Logger.debug(pc.descripcion+"  -  "+pc.catalogador.nombreCompleto());
                    vtk.palabrasClave.add(pc);
                }
            }


            if (forma.field("accesibilidad_audio").value()!=null)
                if (forma.field("accesibilidad_audio").value().toString().compareTo("1")==0) {
                    Logger.debug("ACC AUDIO OK");
                    vtk.accesibilidadAudio = true;
                }
            if (forma.field("accesibilidad_video").value()!=null)
                if (forma.field("accesibilidad_video").value().toString().compareTo("1")==0) {
                    Logger.debug("ACC VIDEO OK");
                    vtk.accesibilidadVideo = true;
                }

           // vtk.palabrasClave.forEach(pc->pc.catalogador = usuarioActual );


            /////////// TimeLine
            Logger.debug("--->  Antes del return de losDatos - TIMELINE  " + forma.field("txaTimeLine").value());


            if (forma.field("txaTimeLine").value() != null   &&  forma.field("txaTimeLine").value()!="" ){
                JSONArray jsonArrTimeLine = new JSONArray(forma.field("txaTimeLine").value());
                for (int i = 0; i < jsonArrTimeLine.length(); i++) {
                    VtkTimeLine tl = new VtkTimeLine();
                    JSONObject objTL = jsonArrTimeLine.getJSONObject(i);

                    if (!objTL.isNull("desde") && objTL.get("desde").toString().length() != 0) {
                        Duracion dDesde = new Duracion();
                        Logger.debug("enviando (desde) "+objTL.get("desde").toString());
                        dDesde.convertir(objTL.get("desde").toString());
                        tl.desde = dDesde.totalSegundos();
                        Logger.debug("convertido (desde) "+tl.desde);
                    }
                    if (!objTL.isNull("hasta") && objTL.get("hasta").toString().length() != 0) {
                        Duracion dHasta = new Duracion();
                        Logger.debug("enviando (hasta) "+objTL.get("hasta").toString());
                        dHasta.convertir(objTL.get("hasta").toString());
                        tl.hasta = dHasta.totalSegundos();
                        Logger.debug("convertido (hasta) "+tl.hasta);
                    }
                    if (!objTL.isNull("nombre") && objTL.get("nombre").toString().length() != 0) {
                        Long idVP = null;
                        VideoPersonaje vp = new VideoPersonaje();
                        String elNombre = objTL.get("nombre").toString();
                        // Busca en VideoPersonaje que exista la persona, sino lo crea
                        List<VideoPersonaje> existePersonaje = VideoPersonaje.find.where().ilike("nombre", elNombre).findList();
                        if (existePersonaje.size() == 0) {
                            //vp.catalogo = VtkCatalogo.find.byId(idVTK);
                            vp.nombre = elNombre;
                            vp.catalogador =  Personal.find.byId( Long.parseLong(session("usuario")));
                            //vp.save();
                            Ebean.save(vp);
                            //vp.refresh();
                            Ebean.refresh(vp);
                            idVP = vp.id;
                            tl.personaje = vp;
                        }
                        if (existePersonaje.size() != 0) {
                            tl.personaje = existePersonaje.get(0);
                        }


                    }

                    if (!objTL.isNull("grado") &&  objTL.get("grado").toString().length() != 0) {
                        tl.gradoacademico = objTL.get("grado").toString();
                    }
                    if (!objTL.isNull("cargo") && objTL.get("cargo").toString().length() != 0) {
                        tl.cargo = objTL.get("cargo").toString();
                    }
                    if (!objTL.isNull("tema") && objTL.get("tema").toString().length() != 0) {
                        tl.tema = objTL.get("tema").toString();
                    }
                    tl.catalogador = Personal.find.byId( Long.parseLong(session("usuario"))  );
                    vtk.timeline.add(tl);
                }
            }

            vtk.timeline.forEach(tm->System.out.println("- - - -Desde "+tm.desde+" "+tm.hasta+" "+tm.personaje.nombre));
            Ebean.update(vtk);
            Ebean.commitTransaction();
            flash("success", "Se actualizó al acervo ");

            /*
            System.out.println("\n\n\nDesde VideotecaController.update");
            Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();
            DynamicForm fd = DynamicForm.form().bindFromRequest();
            System.out.println(forma);
            System.out.println("--------------------------");
            System.out.println(fd);

            if (forma.hasErrors()) {
                return badRequest(createForm.render(forma, TipoCredito.find.all(), VtkCampo.find.all()));
            }


            //VtkCatalogo vtk = forma.get();

            VtkCatalogo vtk = losDatos(forma, fd);

            System.out.println("Despues de losDatos");

            vtk.eventos.removeIf(r -> r.servicio == null);
            vtk.niveles.removeIf(r -> r.nivel == null);
            vtk.timeline.clear();


            System.out.println("vtk.id "+vtk.id);
            //
            System.out.println("  ------- -");


         //   vtk.id=  forma.get().id;
            System.out.println("id "+vtk.id);
            System.out.println("Antes de update");
            Ebean.update(vtk);
            System.out.println("Despues de update");



            /////////// TimeLine
            Logger.debug("--->  Antes del return de losDatos " + forma.field("txaTimeLine").value());


            if (forma.field("txaTimeLine").value() != null   &&  forma.field("txaTimeLine").value()!="" ){
                JSONArray jsonArrTimeLine = new JSONArray(forma.field("txaTimeLine").value());
                for (int i = 0; i < jsonArrTimeLine.length(); i++) {
                    VtkTimeLine tl = new VtkTimeLine();
                    JSONObject objTL = jsonArrTimeLine.getJSONObject(i);

                    if (objTL.get("desde").toString().length() != 0) {
                        Duracion dDesde = new Duracion();
                        dDesde.convertir(objTL.get("desde").toString());
                        tl.desde = dDesde.totalSegundos();
                    }
                    if (objTL.get("hasta").toString().length() != 0) {
                        Duracion dHasta = new Duracion();
                        dHasta.convertir(objTL.get("hasta").toString());
                        tl.hasta = dHasta.totalSegundos();
                    }
                    if (objTL.get("nombre").toString().length() != 0) {
                        Long idVP = null;
                        VideoPersonaje vp = new VideoPersonaje();
                        String elNombre = objTL.get("nombre").toString();
                        // Busca en VideoPersonaje que exista la persona, sino lo crea
                        List<VideoPersonaje> existePersonaje = VideoPersonaje.find.where().ilike("nombre", elNombre).findList();
                        if (existePersonaje.size() == 0) {
                            //vp.catalogo = VtkCatalogo.find.byId(idVTK);
                            vp.nombre = elNombre;
                            vp.catalogador =  Personal.find.byId( Long.parseLong(session("usuario")));
                            //vp.save();
                            Ebean.save(vp);
                            //vp.refresh();
                            Ebean.refresh(vp);
                            idVP = vp.id;
                            tl.personaje = vp;
                        }
                        if (existePersonaje.size() != 0) {
                            tl.personaje = existePersonaje.get(0);
                        }


                    }
                    if (objTL.get("grado").toString().length() != 0) {
                        tl.gradoacademico = objTL.get("grado").toString();
                    }
                    if (objTL.get("cargo").toString().length() != 0) {
                        tl.cargo = objTL.get("cargo").toString();
                    }
                    if (objTL.get("tema").toString().length() != 0) {
                        tl.tema = objTL.get("tema").toString();
                    }
                    tl.catalogador = Personal.find.byId( Long.parseLong(session("usuario"))  );
                    vtk.timeline.add(tl);
                }
            }

            vtk.timeline.forEach(tm->System.out.println("- - - -Desde "+tm.desde+" "+tm.hasta+" "+tm.personaje.nombre));

            if (1!=1)
                throw new RuntimeException("mi excepcion");

            //vtk.update();
            Ebean.update(vtk);
            Ebean.commitTransaction();
            flash("success", "Se actualizó el acervo");
            */
        } catch(Exception e) {
            flash("danger", "No se aplicarón los cambios. No fué posible la actualización ");
            Ebean.rollbackTransaction();
            System.out.println(ColorConsola.ANSI_RED+"Ocurrió un error al intentar actualizar el registro. "+e+ColorConsola.ANSI_RESET);
            e.printStackTrace();

//            return redirect( routes.VideotecaController.catalogo() );
        }finally {
            Ebean.endTransaction();
        }
        return redirect( routes.VideotecaController.catalogo());
    }


    public static Result update2() throws JSONException {
        System.out.println("\n\n\nDesde VideotecaController.update2");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();

        DynamicForm fd = DynamicForm.form().bindFromRequest();

        if(forma.hasErrors()) {
            return badRequest(createForm3.render(forma, TipoCredito.find.all(), VtkCampo.find.findList() ));
        }


        System.out.println(forma);
        System.out.println("--------------------------");
        System.out.println(fd);

        //VtkCatalogo vtk = forma.get();
        VtkCatalogo vtk = VtkCatalogo.find.byId(forma.get().id);
        //vtk = losDatos(forma, fd);

        /////////////////////////////////////////////////////////
        Personal usuarioActual = Personal.find.byId(Long.parseLong(session("usuario")));

        // Convertir duracion (hh:mm:ss) a segundos
        Logger.debug(forma.field("duracionStr").value());
        if (  forma.field("duracionStr").value().compareTo("hh:mm:ss")!=0 ) {
            Duracion duracion = new Duracion();
            duracion.convertir(forma.field("duracionStr").value());
            vtk.duracion = duracion.totalSegundos();
        }



        System.out.println("duracion:"+vtk.duracion);
        Long elId = 1L;
        if ( VtkCatalogo.find.all().size()>0)
            elId = Long.parseLong(Ebean.createSqlQuery("select max(id) x from vtk_catalogo").findUnique().getString("x"))+1;
        vtk.id =  elId;

        // Palabras clave  -  agregar id del catalogador
        vtk.palabrasClave.forEach(pc->pc.catalogador = usuarioActual );


        Logger.debug("txaPalabrasClave");
        Logger.debug(forma.data().toString());
        Logger.debug(   fd.field("txaPalabrasClave").value() );



        JSONArray jsonArr = new JSONArray(forma.field("txaPalabrasClave").value());
        JSONArray jsonArrTL = new JSONArray(forma.field("txaTimeLine").value());

        Logger.debug(String.valueOf(jsonArr));
        ////// Palabras Clave
        for ( PalabraClave palabra : vtk.palabrasClave ) {
            palabra.delete();
        }

        vtk.palabrasClave = new ArrayList<>();
        Logger.debug("iniciando ciclo: ");
        Logger.debug("jsonArr.length() "+jsonArr.length());
        for (int x=0; x< jsonArr.length();x++){
            PalabraClave pc = new PalabraClave();
          //  pc.catalogo =  VtkCatalogo.find.byId(vtk.id);
            pc.descripcion = jsonArr.getJSONObject(x).get("descripcion").toString();
            pc.catalogador = usuarioActual;
            Logger.debug(pc.descripcion+"  -  "+pc.catalogador.nombreCompleto());
            vtk.palabrasClave.add(pc);
        }

        Logger.debug("vtk.obra: "+ vtk.obra);
        // Quitar formato  de obra que viene de la forma
        if (  vtk.obra!=null )
            if (  vtk.obra.compareTo("__/__")==0  )
                vtk.obra = null;

        Logger.debug("forma.field(\"txaCreditos\").value(): "+ forma.field("txaCreditos").value());
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
        Logger.info("fin de creditos");

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


    public static Result catalogoDelete() throws JSONException {
        JSONObject retorno = new JSONObject().put("estado","error");
        JsonNode json = request().body().asJson();
        VtkCatalogo aux = VtkCatalogo.find.byId(json.findValue("id").asLong());
        if (session("usuario").compareTo( Long.toString(aux.catalogador.id))==0) {
            aux.delete();
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
        Logger.debug("f:"+f);
        VtkCatalogo c = VtkCatalogo.find.where().eq("clave", f).findUnique();
        Logger.debug("c:"+c);
        if (c==null){
            jo.put("estado", "correcto");
        } else {
            jo.put("id", c.id);
            jo.put("clave", c.clave);
        }
        Logger.debug(jo.toString());
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




}
