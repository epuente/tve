package controllers;

import classes.AuxVTKCredito;
import classes.CapitalizaCadena;
import classes.Duracion;
import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import jdk.nashorn.internal.parser.JSONParser;
import models.*;
import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import scala.util.parsing.json.JSON;
import views.html.operacionNoPermitida;
import views.html.videoteca.*;
import views.html.catalogos.Accesorio.editForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//import org.apache.commons.lang3.text.WordUtils.*;

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
           // arrSeriesLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully( s.descripcion));
            arrSeriesLabels.put( new CapitalizaCadena(s.descripcion).modificado());
            arrSeriesDatos.put(c);
          //  CapitalizaCadena n = new CapitalizaCadena(s.descripcion);
        }

        for (Areatematica at : Areatematica.find.all()){
            //arrAreaLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully( at.descripcion));
            arrAreaLabels.put( new CapitalizaCadena(at.descripcion).modificado());
            arrAreaDatos.put(  VtkCatalogo.find.where().eq("areatematica.id", at.id).findRowCount() );
        }

        for (VtkFormato f : VtkFormato.find.all()){
            //arrFormatoLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully(f.descripcion));
            //arrFormatoLabels.put(new CapitalizaCadena(f.descripcion).modificado());
            arrFormatoLabels.put(f.descripcion);
            arrFormatoDatos.put( VtkCatalogo.find.where().eq("formato.id", f.id).findRowCount());
        }

        for (Produccion p: Produccion.find.all()){
            arrProdLabels.put( p.sigla );
            arrProdDatos.put( VtkCatalogo.find.where().eq("produccion.id", p.id).findRowCount());
        }


        for (Idioma i : Idioma.find.all()){
            //arrIdiomaLabels.put(org.apache.commons.lang3.text.WordUtils.capitalizeFully(i.descripcion));
            arrIdiomaLabels.put( new CapitalizaCadena(i.descripcion).modificado());
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


    public static Result catalogoCreate(){
        Form<VtkCatalogo> forma = play.data.Form.form(VtkCatalogo.class);
        /*
        List<String> arrCreditos = Arrays.asList("Productores", "Asistentes", "Ponentes", "Realizadores", "Audio" , "Edición");

        List<AuxVTKCredito> lstAuxVTKCreditos = new ArrayList<>();

        lstAuxVTKCreditos.add(  new AuxVTKCredito(1L, "Productores", "producción")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(2L, "Asistentes", "asistencia")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(3L, "Ponentes", "ponencia")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(4L, "Realizadores", "realización")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(5L, "Locutores / voz", "Locución o voz")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(6L, "Camarógrafos", "grabación de video / levantamiento de imágen")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(7L, "Editores", "edición de audio y/o video")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(8L, "Postproducción", "postproducción")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(9L, "Calificadores", "calificación")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(10L, "Guionistas", "guión")  );
        lstAuxVTKCreditos.add(  new AuxVTKCredito(99L, "Otros", "postproducción")  );


        List<TipoCredito> lstTc =new ArrayList<>();

        lstAuxVTKCreditos.forEach(tc->{
            TipoCredito tc0 = new TipoCredito();
            tc0.id = tc.id;
            tc0.descripcion = tc.descripcion;
            tc0.accion = tc.accion;
            tc0.save();
        });
*/


        return ok(
                views.html.videoteca.createForm.render(forma, TipoCredito.find.all())
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
        JSONArray ja = new JSONArray();
        String campo = json.findValue("campo").asText();
        String cadena = json.findValue("cadena").asText();
        Logger.debug("campo "+campo);
        String query1 = "";
        if (campo.compareTo("ur")==0)
            query1 ="select id, nombre_largo from unidad_responsable s where unaccent(nombre_largo) ilike unaccent('%"+cadena+"%')";
        if (campo.compareTo("serie")==0)
            query1 ="select id, descripcion from serie s where unaccent(descripcion) ilike unaccent('%"+cadena+"%')";
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
        String query3 = "SELECT id, descripcion, ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"'))" +
                "FROM serie s " +
                "WHERE to_tsvector(coalesce(descripcion, ' ')) @@ to_tsquery('"+ts+"') " +
                "ORDER BY ts_rank(to_tsvector(coalesce(descripcion, ' ')), to_tsquery('"+ts+"')) desc";


        final RawSql rawSql3 = RawSqlBuilder.unparsed(query3)
                .columnMapping("id", "id")
                .columnMapping("descripcion", "descripcion")
                .create();
        List<Serie> list2 = Serie.find.setRawSql(rawSql3).findList();
        List<Serie> list3 = new ArrayList<Serie>();
        Logger.debug("list2 tam: "+list2.size());

        /*
        System.out.println("list1");
        for (Serie s : list1){
            System.out.println("    "+s.id+" "+s.descripcion);
        }
         */

        //ja.put(list1);
        if ( !list1.isEmpty() &&  list1.size()>0)
            list3.addAll(list1);
        /*
        System.out.println("list2");
        for (Serie s : list2){
            System.out.println("    "+s.id+" "+s.descripcion);
        }
         */
        list3.addAll(list2);
        ja.put(list3);

        // Para el siguiente código se agregaron los metodos equals y hasCode en la clase Serie
        // Elimina elementos de la lista repetidos
        List<Serie> uniqueSeriesList = list3.stream()
                .distinct()
                .collect(Collectors.toList());

        // Imprime la lista de objetos Serie únicos
        /*
        for (Serie serie : uniqueSeriesList) {
            System.out.println(serie);
        }
         */

        /*
        System.out.println("list resultado   uniqueSeriesList");
        for (Serie s : uniqueSeriesList){
            System.out.println("    "+s.id+" "+s.descripcion);
        }
        */

        Logger.debug("uniqueSeriesList (TOTAL) tam: "+uniqueSeriesList.size());
        jo.put("coincidencias", uniqueSeriesList);
        return ok ( jo.toString() );
    }


    public static Result nuevaSerie() throws JSONException {
        Logger.debug("Desde VideotecaController.nuevaSerie");
        JSONObject joRetorno = new JSONObject();
        JsonNode json = request().body().asJson();
        Serie serie = new Serie();
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
        UnidadResponsable ur = new UnidadResponsable();
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
        if (campo.compareTo("ur")==0){
            query1 = "select id, nombre_largo from unidad_responsable where unaccent(nombre_largo) = unaccent('"+cadena+"')";
        }
        if (campo.compareTo("serie")==0) {
            query1 = "select id, descripcion from serie s where unaccent(descripcion) = unaccent('" + cadena + "')";
        }
        List<SqlRow> sqlRows1 = Ebean.createSqlQuery(query1).findList();
        /*
        final RawSql rawSql1 = RawSqlBuilder.unparsed(query1)
                .columnMapping("id", "id")
                .columnMapping("descripcion", "descripcion")
                .create();
        List<Serie> list1 = Serie.find.setRawSql(rawSql1).findList();
         */
        Logger.debug("list1 EXISTE tam: "+sqlRows1.size());
        jo.put("coincidencias", sqlRows1.size());
        Logger.debug(jo.toString());
        return ok ( jo.toString() );
    }



    public static Result cargaInicial() throws ParseException {

        String query ="select * from temporal order by id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(query).findList();
        int total = sqlRows.size();

        String formatoFecha = "yyyy-MM-dd'T'HH:mm:ss";

        SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha);

        for (SqlRow r :sqlRows ){
            VtkCatalogo catalogo = new VtkCatalogo();
            Long id = r.getLong("id");
            Long serie = r.getLong("serie");
            System.out.println("   id -->"+r.getString("id")+" serie "+serie+ " "+r.getString("titulo"));

            catalogo.id = id;
            catalogo.serie =  Serie.find.byId(serie);
            catalogo.titulo = r.getString("titulo");
            catalogo.sinopsis = r.getString("sinopsis");
            catalogo.clave = r.getString("clave");
            catalogo.obra = r.getString("numobra");
            catalogo.formato = VtkFormato.find.byId(r.getLong("formato"));

            // PALABRAS CLAVE
            String strPalabrasClave = r.getString("palabrasclave");
            // Convertir un texto con comas a un arreglo
            String[] arrPalabrasClave = strPalabrasClave.split(",");
            for (String pc : arrPalabrasClave){
                PalabraClave palabraClave = new PalabraClave();
                palabraClave.descripcion = pc.trim();
                //palabraClave.save();
                System.out.println("                "+palabraClave.descripcion);
                catalogo.palabrasClave.add(palabraClave);
            }


            /*
            // CREDITOS
            String aux = r.getString("creditos");

            while (aux.length()>1){

                // Encontrar PRODUCTOR ó PRODUCTORES ó PRODUCCION en la cadena hasta el fin de la linea ó , o .
                if ( aux.toUpperCase().contains("PRODUCTOR:") || aux.toUpperCase().contains("PRODUCTORES:") || aux.toUpperCase().contains("PRODUCCION:")) {
                    aux.replace("PRODUCTOR:", "");
                    aux.replace("PRODUCTORES:", "");
                    aux.replace("PRODUCCION:", "");
                    int sub = aux.indexOf(",");
                    if (sub == -1) {
                        sub = aux.indexOf(".");
                        if (sub == -1) {
                            sub = aux.indexOf(" ");
                            if (sub == -1) {

                            }

                        }
                    }
                }

            }


            */


            System.out.println("     duracion "+r.getString("duracion"));
            Duracion duracion = new Duracion();
            duracion.convertir(r.getString("duracion"));

            catalogo.duracion =  duracion.totalSegundos();
            catalogo.idioma = Idioma.find.byId(r.getLong("idioma"));

            catalogo.produccion = Produccion.find.byId(  r.getLong("produccion"));




            catalogo.fechaProduccion =   r.getString("anioproduccion");
            if (r.getLong("disponibilidad")!=null)
                catalogo.disponibilidad = Disponibilidad.find.byId(r.getLong("disponibilidad"));

            catalogo.areatematica = Areatematica.find.byId(r.getLong("areatematica"));

            catalogo.save();

        }

        return ok ( views.html.videoteca.vtkCargaInicial.render(total)  );
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
            Logger.debug("003 duracion " + d);
            Logger.debug("004 TP "+TipoCredito.find.all());
            return ok( views.html.videoteca.editForm.render(id, forma, TipoCredito.find.all(), d)  );
        } else {
            return ok (views.html.operacionNoPermitida.render());
        }
    }

    public static Result save() throws JSONException {
        System.out.println("\n\n\nDesde VideotecaController.save");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();

        DynamicForm fd = DynamicForm.form().bindFromRequest();

        System.out.println(forma);
        System.out.println("--------------------------");
        System.out.println(fd);




        VtkCatalogo vtk = forma.get();

        Personal usuarioActual = Personal.find.byId(Long.parseLong(session("usuario")));

        // Convertir duracion (hh:mm:ss) a segundos
        Logger.debug(forma.field("duracionStr").value());
        if (  forma.field("duracionStr").value().compareTo("hhh:mm:ss")!=0 ) {
            Duracion duracion = new Duracion();
            duracion.convertir(forma.field("duracionStr").value());
            vtk.duracion = duracion.totalSegundos();
        }

        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, TipoCredito.find.all() ));
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
        vtk.palabrasClave.clear();
        vtk.palabrasClave = new ArrayList<>();
        Logger.debug("iniciando ciclo: ");
        for (int x=0; x< jsonArr.length();x++){
            PalabraClave pc = new PalabraClave();
            pc.descripcion = jsonArr.getJSONObject(x).get("descripcion").toString();
            pc.catalogador = usuarioActual;
            Logger.debug(pc.descripcion+"  -  "+pc.catalogador.nombreCompleto());
            vtk.palabrasClave.add(pc);
        }

        // Quitar formato  de obra que viene de la forma
        if (  vtk.obra.compareTo("__/__")==0  )
            vtk.obra = null;


        // CREDITOS
        String texto = forma.field("txaCreditos").value();
        try {
            JSONObject jsonObject = new JSONObject( texto   );
            JSONArray c = jsonObject.getJSONArray("losDatos");
            for (int i = 0 ; i < c.length(); i++) {
                JSONObject obj = c.getJSONObject(i);
                String A = obj.getString("tipo");
                String B = obj.getString("creditos");
                String[] arrCreditos = B.split(",");
                List<Credito> creditos = new ArrayList<>();
                for (String elCredito : arrCreditos) {
                    Credito cred = new Credito();
                    cred.tipoCredito = TipoCredito.find.byId( Long.parseLong(A));
                    cred.personas = elCredito;
                    cred.catalogador = usuarioActual;
                    vtk.creditos.add(cred);

                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        vtk.catalogador = usuarioActual;
        vtk.save();
        vtk.refresh();
        Long idVTK = vtk.id;

        /////////// TimeLine
        vtk.timeline.clear();
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
                    vp.catalogo = VtkCatalogo.find.byId(idVTK);
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


        flash("success", "Se agregó al acervo");
        return redirect( routes.VideotecaController.catalogo() );
    }


    // Usando dynamic form
    public static Result save3() {
        System.out.println("\n\n\nDesde VideotecaController.save3");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();

        DynamicForm fd = DynamicForm.form().bindFromRequest();

        System.out.println(forma);



        return redirect( routes.VideotecaController.catalogo() );
    }

    public static Result save2() throws JSONException {
        Logger.debug("Desde VideotecaController.save2");
        JsonNode json = request().body().asJson();
        Logger.debug(json.toString());


        JSONObject retorno = new JSONObject();
        retorno.put("estado", "indefinido");
        VtkCatalogo catalogo = Json.fromJson(json, VtkCatalogo.class);
        catalogo.catalogador = Personal.find.byId(  Long.parseLong(session("usuario"))  );

        catalogo.save();
        retorno.put("estado", "ok");
        return ok (retorno.toString());
    }


    public static Result update(){
        System.out.println("\n\n\nDesde VideotecaController.update");
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).bindFromRequest();
        System.out.println(forma);
        VtkCatalogo obj = forma.get();
        VtkCatalogo vtk = VtkCatalogo.find.byId(obj.id);
        vtk.creditos.clear();
        // Convertir duracion (hh:mm:ss) a segundos
        Duracion duracion = new Duracion();
        duracion.convertir(forma.field("duracionStr").value());
        vtk.duracion = duracion.totalSegundos();

        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, TipoCredito.find.all() ));
        }

        System.out.println("duracion:"+vtk.duracion);

        vtk.folio = obj.folio;
        vtk.unidadresponsable = UnidadResponsable.find.byId(obj.unidadresponsable.id);
        vtk.eventos = obj.eventos;
        vtk.niveles = obj.niveles;
        //vtk.esAreaCentral = obj.esAreaCentral;
        vtk.folioDEV = obj.folioDEV;
        vtk.titulo = obj.titulo;
        vtk.sinopsis = obj.sinopsis;
        vtk.serie = obj.serie;
        vtk.clave = obj.clave;
        vtk.obra = obj.obra;
        vtk.formato = obj.formato;
        vtk.palabrasClave = obj.palabrasClave;
        vtk.idioma = obj.idioma;
        vtk.creditos = obj.creditos;
        vtk.produccion = obj.produccion;
        vtk.fechaProduccion = obj.fechaProduccion;
        vtk.disponibilidad = obj.disponibilidad;
        vtk.areatematica = obj.areatematica;
        vtk.nresguardo = obj.nresguardo;
        vtk.liga = obj.liga;
        vtk.catalogador = Personal.find.byId(Long.parseLong(session("usuario")));
        vtk.timeline = obj.timeline;
        vtk.audio = obj.audio;
        vtk.video = obj.video;
        vtk.observaciones = obj.observaciones;


        /*
        Long elId = 1L;
        if ( VtkCatalogo.find.all().size()>0)
            elId = Long.parseLong(Ebean.createSqlQuery("select max(id) x from vtk_catalogo").findUnique().getString("x"))+1;
        vtk.id =  elId;
        */


        // CREDITOS
        String texto = forma.field("txaCreditos").value();
        if (texto!=null)
            try {
                JSONObject jsonObject = new JSONObject( texto   );
                JSONArray c = jsonObject.getJSONArray("losDatos");
                for (int i = 0 ; i < c.length(); i++) {
                    JSONObject objt = c.getJSONObject(i);
                    String A = objt.getString("tipo");
                    String B = objt.getString("creditos");
                    String[] arrCreditos = B.split(",");
                    List<Credito> creditos = new ArrayList<>();
                    for (String elCredito : arrCreditos) {
                        Credito cred = new Credito();
                        cred.tipoCredito = TipoCredito.find.byId( Long.parseLong(A));
                        cred.personas = elCredito;
                        vtk.creditos.add(cred);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

       // vtk.catalogador = Personal.find.byId( Long.parseLong(session("usuario")));
        vtk.update();
        flash("success", "Se actualizó el registro");
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

}
