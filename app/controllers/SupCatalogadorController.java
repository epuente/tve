package controllers;

import classes.CapitalizaCadena;
import classes.Duracion;
import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
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


import views.html.videoteca.catalogadores.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;

public class SupCatalogadorController extends ControladorSeguroSupCatalogador {


    public static Result lista(){
        Logger.debug("desde SupCatalogador.catalogo");
        return ok( lista.render());
    }

    public static Result listaDTSS(){
        System.out.println("Desde SupCatalogador.listaDTSS............"+new Date());
        System.out.println( "parametros:"+ request() );
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

        int totalCatalogadores = 0;
        for (Cuenta c : Cuenta.find.all()){
            for (CuentaRol crol : c.roles) {
                if (crol.rol.id == 132)
                    totalCatalogadores++;
            }
        }

        Logger.debug("totalCatalogadores "+totalCatalogadores);

        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        Page<Personal> pag = Catalogador.page2(numPag ,
                Integer.parseInt(request().getQueryString("length"))
                , filtro
                , nombreColOrden
                , tipoOrden
        );

        response().setContentType("application/json");
        JSONObject json2 = new JSONObject();
        try {
            json2.put("draw",  request().getQueryString("draw")+1  );
            json2.put("recordsTotal", totalCatalogadores);
            JSONArray losDatos = new JSONArray();

            if (totalCatalogadores ==0){
                json2.put("recordsFiltered", 0);
                //losDatos.put(new JSONObject());
                json2.put("data", new JSONArray());
            } else {
                json2.put("recordsFiltered",  totalCatalogadores<pag.getTotalRowCount()?totalCatalogadores:pag.getTotalRowCount() );
                for( Personal p : pag.getList()  ){
                    JSONObject datoP = new JSONObject();
                    datoP.put("id", p.id);
                    datoP.put("nombre", p.nombre);
                    datoP.put("paterno", p.paterno);
                    datoP.put("materno", p.materno);
                    losDatos.put(datoP);
                    json2.put("data", losDatos);
                }
            }
        } catch (JSONException e) {
            System.out.println("ERROR - SupCatalogador.catalogoDTSS ");
            e.printStackTrace();
        }
        System.out.println("retornando:  "+json2.toString());
        return ok( json2.toString() );
    }


    public static Result catalogadorCreate(){
        Form<Personal> forma = Form.form(Personal.class);
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
                createForm.render(forma)
        );
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

    public static Result create() {
        Form<Personal> forma = play.data.Form.form(Personal.class);
        return ok(views.html.videoteca.catalogadores.createForm.render(forma));
    }

    public static Result catalogadorEdit(Long id){
        Personal cat = Personal.find.byId(id);
        Form<Personal> forma = form(Personal.class).fill( cat  );
        return ok( editForm.render(id, forma)  );
    }

    public static Result save(){
        System.out.println("\n\n\nDesde SupCatalogador.save");
       // Form<Personal> forma = form(Personal.class).bindFromRequest();
        DynamicForm forma = form().bindFromRequest();



        System.out.println(forma);
        if(forma.hasErrors()) {
           // return badRequest(createForm.render(forma));
        }


        Personal nuevo = new Personal();
        nuevo.nombre = forma.get("nombre");
        nuevo.paterno = forma.get("paterno");
        nuevo.materno = forma.get("materno");
        nuevo.activo = "S";
        nuevo.numEmpleado =  Long.toString( new Date().getTime() );
        nuevo.turno = "A";
        nuevo.tipocontrato = TipoContrato.find.byId(2L);
        nuevo.cuentas = new ArrayList<>();

        PersonalCorreo correo = new PersonalCorreo();
        correo.email = forma.get("correos[0");

        nuevo.correos.add(correo);

        CuentaRol cr = new CuentaRol();
        Cuenta cta = new Cuenta();
        cta.username = forma.get("username");
        cta.password = forma.get("password");

        Rol rol = new Rol();
        rol = Rol.find.byId(132L);
        cr.rol = rol;
        cta.roles.add(cr);
        nuevo.cuentas.add(cta);
        nuevo.save();
        //forma.get().save();
        flash("success", "Se agregó el catalogador");
        return redirect( routes.SupCatalogadorController.lista() );
    }

    public static Result edit(Long id){
        Personal per = Personal.find.byId(id);
        Form<Personal> forma = form(Personal.class).fill( per  );
        return ok( views.html.videoteca.catalogadores.editForm.render(id, forma)  );
    }

    public static Result update(Long id) {
        System.out.println("\n\nDesde SupCatalogadorController.update");
        Form<Personal> formaP = form(Personal.class).bindFromRequest();
        DynamicForm forma = form().bindFromRequest();
        System.out.println(forma);
        System.out.println("...000");
        if(forma.hasErrors()) {
            System.out.println("...forma con errores");
            System.out.println(forma);
           // return badRequest(views.html.videoteca.catalogadores.editForm.render(id, forma ));
        }
        System.out.println("...forma sin errores");
        //Catalogador nvo = forma.get();
        Personal nvo = Personal.find.byId(id);
        nvo.nombre = forma.get("nombre");
        nvo.paterno = forma.get("paterno");
        nvo.materno = forma.get("materno");
        nvo.cuentas.get(0).username = forma.get("username");
        nvo.cuentas.get(0).password = forma.get("password");

        Logger.debug("correo-> "+forma.get("correos[0].email"));
        Logger.debug("correo-> "+formaP.field("correos[0].email").value());


        PersonalCorreo pcorreo = new PersonalCorreo();
        pcorreo.email = formaP.field("correos[0].email").value();
        nvo.correos.get(0).setEmail(formaP.field("correos[0].email").value());


        nvo.update(id);
        return redirect( routes.SupCatalogadorController.lista() );
    }


    public static Result delete() throws JSONException {
        System.out.println("\n\n\nDesde SupCatalogadorController.delete");
        JsonNode json = request().body().asJson();
        System.out.println(json);
        Long id = json.findValue("id").asLong();
        JSONObject jo = new JSONObject().put("estado", "indefinido");

        // Busca que no tenga registros
        List<VtkCatalogo> registros = VtkCatalogo.find.where().eq("catalogador.id", id).findList();
        long existencias = registros.size();
        if (existencias > 0){
            jo.put("estado","error");
            jo.put("descripcion", "tiene "+existencias+" registros");
        } else {
            //Catalogador aux = Catalogador.find.byId(id);
            Logger.debug("id:"+id);
            Personal aux = Personal.find.byId(id);
            jo.put("eliminado", aux.nombreCompleto());
            for ( CuentaRol x : aux.cuentas.get(0).roles){
                if (x.rol.id==132) {
                    //  x.delete();
                }
            }

            Logger.debug("aux.cuentas.get(0).roles.size() "+aux.cuentas.get(0).roles.size());

            if (aux.cuentas.get(0).roles.size()==1) {
                for ( Cuenta c :  aux.cuentas ){
                    c.delete();
                }
               aux.delete();
               jo.put("adicional", "si");
            }
            jo.put("estado", "borrado");
        }
        return ok (  jo.toString()    );
    }

    public static Result tablero(){
        Logger.debug("desde SupCatalogadorController.tablero");
        return ok( views.html.videoteca.catalogadores.tablero.render());
    }

    public static Result ajaxTablero() throws JSONException {
        Logger.debug("Desde SupCatalogadorController.ajaxTablero");
        JSONObject retorno = new JSONObject();
        JSONArray arrCatalogadoresLabels = new JSONArray();
        JSONArray arrCatalogadoresDatos = new JSONArray();

        String[] coloresChart = {"#707e8d", "#c9b8d0",  "#BDC3C7", "#7fd2c1", "#9cd1f5", "#e6e6ab"     ,"#ddabe6", "#6a665a", "#635e85"};


        int totalCatalogadores = 0;
        int totalCatalogadoresInactivos=0;
        int totalCatalogadoresActivos=0;
        for (Cuenta c : Cuenta.find.all()){
            for (CuentaRol crol : c.roles) {
                if (crol.rol.id == 132) {
                    totalCatalogadores++;
                    if (c.personal.activo.compareTo("S")==0)
                        totalCatalogadoresActivos++;
                    if (c.personal.activo.compareTo("N")==0)
                        totalCatalogadoresInactivos++;
                }
            }
        }


        // Genera datos para la graficación

        // Gráfica 1 - total
        //List<VtkCatalogo> catalogadoresAgrupados = VtkCatalogo.find.select("count(catalogador_id)").orderBy().asc("catalogador_id").findList();

        String query = "select  concat(p.nombre,' ',p.paterno, ' ',p.materno) nombre ,  count(catalogador_id) total \n" +
                "from vtk_catalogo vc " +
                "inner join personal p on vc.catalogador_id = p.id " +
                "group by concat(p.nombre,' ',p.paterno, ' ',p.materno), catalogador_id " +
                "order by concat(p.nombre,' ',p.paterno, ' ',p.materno), catalogador_id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(query).findList();

        for ( SqlRow row : sqlRows ){
            arrCatalogadoresLabels.put(row.getString("nombre"));
            arrCatalogadoresDatos.put(row.getLong("total"));
        }

        // Gráfica 2 - por año mes, catalogador
        String query2 = "select  concat(EXTRACT('YEAR' FROM vc.audit_update),' ', EXTRACT('MONTH' FROM vc.audit_update)) fecha, concat(p.nombre,' ',p.paterno, ' ',p.materno) nombre , count(catalogador_id) total \n" +
                "from vtk_catalogo vc " +
                "right join personal p on vc.catalogador_id = p.id " +
                "inner join cuenta cta on cta.personal_id =  p.id " +
                "inner join cuenta_rol cr on cta.id = cr.cuenta_id " +
                "inner join rol r on cr.rol_id = r.id where r.id = 132 "+
                "group by concat(EXTRACT('YEAR' FROM vc.audit_update),' ', EXTRACT('MONTH' FROM vc.audit_update)), concat(p.nombre,' ',p.paterno, ' ',p.materno), catalogador_id " +
                "order by concat(EXTRACT('YEAR' FROM vc.audit_update),' ', EXTRACT('MONTH' FROM vc.audit_update)), concat(p.nombre,' ',p.paterno, ' ',p.materno), catalogador_id ";
        List<SqlRow> sqlRows2 = Ebean.createSqlQuery(query2).findList();

        for (SqlRow s : sqlRows2){
            Logger.debug(" -->fecha "+s.getString("fecha")+ "  nombre "+s.getString("nombre")+"    "+s.getLong("total"));
        }


        JSONObject joGraf2 = new JSONObject();
        JSONArray jaDatasets = new JSONArray();

        // Los labels de la gráfica es la columna fecha (año mes), unicos (sin repetirse)
        List<String> labelsG2 = new ArrayList<>();
        labelsG2 = sqlRows2.stream().map(m->m.getString("fecha")).filter(f->f.length()>3).distinct().collect(Collectors.toList());

        // Obtener los nombre de los catalogadores, unicos (sin repetirse)
        List<String> catalogadores = new ArrayList<>();
        catalogadores = sqlRows2.stream().map(m->m.getString("nombre")).distinct().collect(Collectors.toList());


        joGraf2.put("labels", labelsG2);



        int color=0;
        for ( String c : catalogadores ){
            JSONObject joDS = new JSONObject();
            joDS.put("label", c);
            joDS.put("backgroundColor", coloresChart[color++]);
            JSONArray jaData = new JSONArray();
            for ( String f :labelsG2 ){
                List<String> aux = sqlRows2.stream().filter(fil -> fil.getString("fecha").compareTo(f) == 0 && fil.getString("nombre").compareTo(c) == 0).map(m -> m.getString("total")).collect(Collectors.toList());
                if (aux.size()==0){
                    jaData.put(0);
                } else {
                    jaData.put( String.join(",", aux)   );
                }
            }
            joDS.put("data", jaData);
            jaDatasets.put(joDS);
        }

        joGraf2.put("datasets", jaDatasets );
        retorno.put("grafica2", joGraf2);
/*
        for ( SqlRow row : sqlRows2 ){
            arrfechaLabels.put(row.getString("nombre"));
            arrfechalogadoresDatos.put(row.getLong("total"));
        }
*/



        retorno.put("totalCatalogadores", totalCatalogadores);
        retorno.put("totalCatalogadoresActivos", totalCatalogadoresActivos);
        retorno.put("totalCatalogadoresInactivos", totalCatalogadoresInactivos);


        retorno.put("totalLabels", arrCatalogadoresLabels);
        retorno.put("totalDatos", arrCatalogadoresDatos);

        return ok (retorno.toString());

    }



    public static Result catalogo(){
        Logger.debug("desde SupCatalogadorController.catalogo");
        return ok( views.html.videoteca.catalogadores.catalogoVideotk.render());
    }

    public static Result catalogoDTSS(){
        System.out.println("Desde SupCatalogadorController.catalogoDTSS............"+new Date());
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





        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        com.avaje.ebean.Page<VtkCatalogo> pag = VtkCatalogo.page(numPag ,
                Integer.parseInt(request().getQueryString("length"))
                , filtro
                , nombreColOrden
                , tipoOrden
        );
        Logger.debug("pag: "+pag);
        Logger.debug("pag getTotalRowCount: "+pag.getTotalRowCount());


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
                    datoP.put("catalogador", p.catalogador.nombreCompleto() );
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


    public static Result catalogoInfo(Long id){
        System.out.println("\n\nDesde SupCatalogadorController.catalogoInfo");
        VtkCatalogo catalogo = VtkCatalogo.find.byId(id);
        //Logger.debug(  session("usuario") +"  -  "+ );

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

        return ok( views.html.videoteca.catalogadores.infoForm.render(id, forma)  );

}


}
