package controllers;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import models.Accesorio;
import models.Rol;
import models.Serie;
import models.VtkCatalogo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.html.catalogos.Accesorio.createForm;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class VideotecaController extends ControladorSeguroVideoteca{
    public static Result tablero(){
        Logger.debug("desde VideotecaController.tablero");
        return ok( views.html.usuario.tableroVideotk.render());
    }

    public static Result ajaxTablero() throws JSONException {
        System.out.println("Desde VideotecaController.ajaxTablero");
        JSONObject retorno = new JSONObject();
        JSONArray arrEquipo = new JSONArray();
        JSONArray arrAccesorios = new JSONArray();
        retorno.put("catalogo", "[]");
        return ok ( retorno.toString() );
    }

    public static Result catalogo(){
        Logger.debug("desde VideotecaController.catalogo");
        return ok( views.html.usuario.catalogoVideotk.render());
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

        List<VtkCatalogo> cat = VtkCatalogo.find.all();
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
                    datoP.put("sinopsis", p.sinopsis);
                    datoP.put("titulo", p.titulo);
                    datoP.put("serie", p.serie.descripcion);
                    losDatos.put(datoP);
                    json2.put("data", losDatos);
                }
            }

/*

            if ( pag.getTotalRowCount()>0 ){
                json2.put("data", losDatos);
            } else {
                json2.put("data", new JSONArray() );
                return ok( json2.toString()  );
            }
*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR - VideotecaController.catalogoDTSS ");
            e.printStackTrace();
        }
        System.out.println("retornando:  "+json2.toString());
        return ok( json2.toString() );

    }


    public static Result catalogoCreate(){
        Form<VtkCatalogo> forma = play.data.Form.form(VtkCatalogo.class);
        List<String> arrCreditos = Arrays.asList("Productores", "Asistentes", "Ponentes", "Realizadores", "Audio" , "Edición");
        return ok(
                views.html.videoteca.createForm.render(forma, arrCreditos)
        );
    }

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


    // Aqui se usan 2 metodos, el primero es el clásico (comparar una cadena con ilike y unaccent)
    // El segundo usa textsearch con ts_vector y ts_query
    // Se regresa un dataset con la combinacion de los dos resultados, poniendo primero los resultados de la comparación clásica
    public static Result textsearch() throws JSONException {
        System.out.println("\n\n\n\nDesde VTKController.textsearch");
        JsonNode json = request().body().asJson();
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        String cadena = json.findValue("cadena").asText();
        String query1 = "select id, descripcion from serie s where unaccent(descripcion) ilike unaccent('%"+cadena+"%')";
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
        s.save();
        s.refresh();
        joRetorno.put("estado", "ok");
        joRetorno.put("id", s.id);
        return ok (joRetorno.toString());
    }


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

}
