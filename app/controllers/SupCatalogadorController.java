package controllers;

import classes.Duracion;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
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
import play.mvc.Result;
import views.html.videoteca.catalogadores.*;

import java.util.*;

import static play.data.Form.form;

public class SupCatalogadorController extends ControladorSeguroSupCatalogador {

    public static Result lista(){
        Logger.debug("desde SupCatalogador.catalogo");
        return ok( lista.render());
    }

    public static Result listaDTSS(){
        System.out.println("Desde SupCatalogador.listaDTSS............"+new Date());
        /*
        System.out.println( "parametros:"+ request() );
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

        int totalCatalogadores = 0;
        for (Cuenta c : Cuenta.find.all()){
            for (CuentaRol crol : c.roles) {
                if (crol.rol.id == 132)
                    totalCatalogadores++;
            }
        }
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
            Personal aux = Personal.find.byId(id);
            jo.put("eliminado", aux.nombreCompleto());
            for ( CuentaRol x : aux.cuentas.get(0).roles){
                if (x.rol.id==132) {
                    //  x.delete();
                }
            }
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


    public static Result toggleActivo() throws JSONException {
        System.out.println("\n\n\nDesde SupCatalogadorController.toggleActivo");
        JsonNode json = request().body().asJson();
        System.out.println(json);
        Long id = json.findValue("id").asLong();
        JSONObject jo = new JSONObject().put("estado", "indefinido");


        Personal nvo = Personal.find.byId(id);
        if (nvo.activo.compareTo("S") ==0) {
            nvo.activo = "N";
            jo.put( "resultado", "inactivo");
        } else
            if (nvo.activo.compareTo("N")==0) {
                nvo.activo = "S";
                jo.put( "resultado", "activo");
            }
        nvo.update();
        return ok (jo.toString());
    }

    public static Result tablero(){
        Logger.debug("desde SupCatalogadorController.tablero");
        return ok( views.html.videoteca.catalogadores.tablero.render());
    }

    public static Result tablero2(){
        Logger.debug("desde SupCatalogadorController.tablero2");
        return ok( views.html.videoteca.catalogadores.tablero2.render());
    }

    public static Result tablero3(){
        Logger.debug("desde SupCatalogadorController.tablero2");
        return ok( views.html.videoteca.catalogadores.tablero3.render());
    }


    public static Result ajaxTablero() throws JSONException {
        Logger.debug("Desde SupCatalogadorController.ajaxTablero");
        JSONObject retorno = new JSONObject();
        JSONArray arrCatalogadoresLabels = new JSONArray();
        JSONArray arrCatalogadoresDatos = new JSONArray();
        int totalBitacoras=0;

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

        String query = "select  concat(p.paterno, ' ',p.materno, ' ', p.nombre) nombre ,  count(catalogador_id) total " +
                "from vtk_catalogo vc " +
                "inner join personal p on vc.catalogador_id = p.id " +
                "group by concat(p.paterno, ' ',p.materno, ' ', p.nombre), catalogador_id " +
                "order by total desc, concat(p.paterno, ' ',p.materno,' ',p.nombre), catalogador_id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(query).findList();

        for ( SqlRow row : sqlRows ){
            arrCatalogadoresLabels.put(row.getString("nombre"));
            arrCatalogadoresDatos.put(row.getLong("total"));
            totalBitacoras += row.getInteger("total");
        }

        // Series
        int totalSeries = Ebean.find(Serie.class).findRowCount();
        retorno.put("totalBitacoras", totalBitacoras);
        retorno.put("totalCatalogadores", totalCatalogadores);
        retorno.put("totalCatalogadoresActivos", totalCatalogadoresActivos);
        retorno.put("totalCatalogadoresInactivos", totalCatalogadoresInactivos);


        retorno.put("totalLabels", arrCatalogadoresLabels);
        retorno.put("totalDatos", arrCatalogadoresDatos);

        retorno.put("totalSeries", totalSeries);

        return ok (retorno.toString());

    }



    public static Result catalogo(){
        Logger.debug("desde SupCatalogadorController.catalogo");
        return ok( views.html.videoteca.catalogadores.catalogoVideotk.render());
    }

    public static Result catalogoDTSS(){
        System.out.println("Desde SupCatalogadorController.catalogoDTSS............"+new Date());
        /*
        System.out.println( "parametros 0:"+ request() );
        System.out.println( "parametros draw:"+ request().getQueryString("draw"));
        System.out.println( "parametros start:"+ request().getQueryString("start"));
        System.out.println( "parametros length:"+ request().getQueryString("length"));
        System.out.println( "parametros seach[value]:"+ request().getQueryString("search[value]"));

        System.out.println( "parametros order[0][column]:"+ request().getQueryString("order[0][column]"));
        System.out.println( "parametros order[0][dir]:"+ request().getQueryString("order[0][dir]"));

        System.out.println("  -  -  - - -1");

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
        return ok( json2.toString() );
    }


    public static Result catalogoInfo(Long id){
        System.out.println("\n\nDesde SupCatalogadorController.catalogoInfo");
        VtkCatalogo catalogo  = VtkCatalogo.find
                .fetch("niveles")
                .where().eq("id", id)
                .findUnique();

        Form<VtkCatalogo> forma = form(VtkCatalogo.class).fill(catalogo);
        Duracion d = new Duracion();
        if (catalogo.duracion!=null)
            d = new Duracion(catalogo.duracion);
        else
            d = new Duracion(0L);

        StringBuilder losCreditos= new StringBuilder();

        if (catalogo.creditos!=null) {
            String sql ="select distinct tipo_credito_id, tc.descripcion  " +
                    "from credito c inner join tipo_credito tc on c.tipo_credito_id = tc.id  " +
                    "where catalogo_id =" + id + " "+
                    "order by tipo_credito_id ";
            List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
            for (SqlRow sqlRow : sqlRows) {
                List<Credito> creditos = Credito.find.setDistinct(true).where()
                        .and(Expr.eq("catalogo_id", id), Expr.eq("tipoCredito.id", sqlRow.getLong("tipo_credito_id")))
                        .findList();
                losCreditos.append("<strong>"+sqlRow.getString(("descripcion") )+": </strong>");
                for ( Credito credito :creditos ) {
                    losCreditos.append(  credito.personas +", " );
                }
                if (losCreditos.toString().length()>2)
                    losCreditos.deleteCharAt(losCreditos.length() - 2);
                losCreditos.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
        }
        return ok( views.html.videoteca.catalogadores.infoForm2.render(id, forma, losCreditos.toString())  );

    }

    public static Result catalogoInfo2(Long id){
        System.out.println("\n\nDesde SupCatalogadorController.catalogoInfo2");
        VtkCatalogo catalogo = VtkCatalogo.find.byId(id);
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).fill(catalogo);
        Duracion d = new Duracion();
        if (catalogo.duracion!=null)
            d = new Duracion(catalogo.duracion);
        else
            d = new Duracion(0L);
        return ok( views.html.videoteca.catalogadores.infoForm2.render(id, forma, "jajaaj")  );
    }

    /*
    public static Result catalogoInfo3(Long id){
        System.out.println("\n\nDesde SupCatalogadorController.catalogoInfo2");
        VtkCatalogo catalogo = VtkCatalogo.find.byId(id);
        Form<VtkCatalogo> forma = form(VtkCatalogo.class).fill(catalogo);
        Duracion d = new Duracion();
        if (catalogo.duracion!=null)
            d = new Duracion(catalogo.duracion);
        else
            d = new Duracion(0L);
        StringBuilder losCreditos= new StringBuilder();
        if (catalogo.creditos!=null) {
            String sql ="select distinct tipo_credito_id, tc.descripcion  " +
                    "from credito c inner join tipo_credito tc on c.tipo_credito_id = tc.id  " +
                    "where catalogo_id =" + id +
                    "order by c.tipo_credito_id ";
            List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
            for (SqlRow sqlRow : sqlRows) {
                List<Credito> creditos = Credito.find.setDistinct(true).where()
                        .and(Expr.eq("catalogo_id", id), Expr.eq("tipoCredito.id", sqlRow.getLong("tipo_credito_id")))
                        .findList();
                losCreditos.append("<span class='bloque'><strong>"+sqlRow.getString(("descripcion") )+": </strong>");
                for ( Credito credito :creditos ) {
                    losCreditos.append(  credito.personas +", " );
                }
                if (losCreditos.toString().length()>2)
                    losCreditos.deleteCharAt(losCreditos.length() - 2);

                losCreditos.append("</span>");
            }
        }
        return ok( views.html.videoteca.catalogadores.infoForm3.render(id, forma, losCreditos.toString() )  );
    }
    */

    public static Result eliminaCatalogo() throws JSONException {
        JSONObject retorno = new JSONObject().put("estado","error");
        JsonNode json = request().body().asJson();
        VtkCatalogo aux = VtkCatalogo.find.byId(json.findValue("id").asLong());
        Ebean.delete(aux);
        retorno.put("estado", "eliminado");
        return ok(  retorno.toString()  );
    }


    public static Result chartCatPorFecha() throws JSONException {
        JSONObject retorno = new JSONObject();
        JSONArray ja = new JSONArray();

        JSONArray jaDataSets = new JSONArray();

        TreeSet<String> tsLabels = new TreeSet<>();

        JSONArray jaColores = coloresBG();

        // Primero seleccionar los catalogadores
        List<Personal> catalogadores = Ebean.find(Personal.class)
                .setDistinct(true)
                .where().eq("cuentas.roles.rol.id", 132)
                .orderBy("paterno, materno, nombre")
                .findList();

        System.out.println("Cantidad catalogadore43s "+  catalogadores.size()  );




        // Loop por catalogador
        int iColores=0;
        for ( Personal catalogador: catalogadores) {
            JSONObject joAux = new JSONObject();



            String query = "select to_char(vc.audit_update, 'YYYY MM') fecha, " +
                    "concat(p.paterno, ' ',p.materno, ' ', p.nombre ) nombre , count(catalogador_id) total " +
                    "from vtk_catalogo vc " +
                    "right join personal p on vc.catalogador_id = p.id " +
                    "inner join cuenta cta on cta.personal_id =  p.id " +
                    "inner join cuenta_rol cr on cta.id = cr.cuenta_id " +
                    "inner join rol r on cr.rol_id = r.id " +
                    "where r.id = 132 and to_char(vc.audit_update, 'YYYY MM') is not null "+
                    "and concat(p.paterno, ' ', p.materno,' ', p.nombre) = '"+catalogador.nombreCompletoOficial()+"' "+
                    "group by to_char(vc.audit_update, 'YYYY MM'), concat(p.paterno, ' ',p.materno, ' ',p.nombre) " +
                    "order by to_char(vc.audit_update, 'YYYY MM'), nombre";
            Logger.info(query);
            List<SqlRow> sqlRows = Ebean.createSqlQuery(query).findList();
            if (!sqlRows.isEmpty()) {
                joAux.put("label", catalogador.nombreCompletoOficial());
                JSONArray jaData = new JSONArray();
                JSONObject joDataset = new JSONObject();

                for (SqlRow r : sqlRows) {
                    tsLabels.add(r.getString("fecha"));
                    JSONObject joData = new JSONObject();
                    System.out.println("   " + r.getString("nombre") + "  " + r.getString("fecha") + "    " + r.getInteger("total"));

                    joData.put("x", r.getString("fecha"));
                    joData.put("y", r.getInteger("total"));

                    //joData.put("backgroundColor", jaColores.get(iColores) );
                    //joData.put("borderColor", jaColores.get(iColores) );

                    //iColores++;

                    jaData.put(joData);
                }

                if ( !sqlRows.isEmpty()  ) {
                    joDataset.put("label", catalogador.nombreCompletoOficial());
                    joDataset.put("data", jaData);
                    joDataset.put("backgroundColor", jaColores.get(iColores++));
                    if (iColores >= coloresBG().length())
                        iColores = 0;
                    jaDataSets.put(joDataset);
                }
            }
        }


        retorno.put("datasets", jaDataSets);
        retorno.put("labels", tsLabels);
        System.out.println("chartCatPorFecha regresa...");
        System.out.println(retorno.toString());

        return ok (retorno.toString());
    }


    // Estos son los colores que se usan para las series de las gráficas
    private static JSONArray coloresBG() {
        JSONArray jaColores = new JSONArray();
        //jaColores.put("#FF69B4");
        jaColores.put("rgb(255,105,180, 0.5");              //Hot pink

        //jaColores.put("#33CC33");

        //jaColores.put("#FFC080");
        jaColores.put("rgb(255, 192, 128, 0.5)");

        //jaColores.put("#6495ED");
        jaColores.put("rgb(100, 149, 237, 0.5)");           // CornflowerBlue

        //jaColores.put("#8B4513");
        jaColores.put("rgb(139, 69, 19, 0.5)");             // SaddleBrown

        //jaColores.put("#4B0082");
        jaColores.put("rgb(75, 0, 130, 0.5)");              // Indigo (morado)


        //jaColores.put("#008000");
        jaColores.put("rgb(0, 128, 0, 0.5)");               // verde

        //jaColores.put("#FFA07A");

        //jaColores.put("#8FBC8F");
        jaColores.put("rgb(143, 188, 143, 0.5)");           // DarkSeaGreen (verde claro)

        //jaColores.put("#FFD700");
        jaColores.put("rgb(255, 215, 0, 0.5)");             // Gold1 (Amarillo brillante)

        jaColores.put("rgb(255,140,0, 0.5)");               // Naranja

        //jaColores.put("#C71585");


        //jaColores.put("#4682B4");


        //jaColores.put("#964B00");
        jaColores.put("rgb(150, 75, 0, 0.5)");        //Café

        /*
        jaColores.put("#3E8E41");
        jaColores.put("#FFC0CB");
        jaColores.put("#6495ED");
        jaColores.put("#8B4513");
        jaColores.put("#4B0082");
        jaColores.put("#008000");

        */
        //jaColores.put("#FFA07A");
        //jaColores.put("rgb(255, 160, 122, 0.5)");                // LightSalmon1
        /*
        jaColores.put("#FFD700");
         */
        return jaColores;
    }


    private static JSONArray sortJsonArray(JSONArray jsonArray) throws JSONException {
        List<JSONObject> jsonObjectList = new ArrayList<>();

        // Populate the list with JSONObject from JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObjectList.add(jsonArray.getJSONObject(i));
        }

        // Sort the list using a comparator
        jsonObjectList.sort(Comparator.comparing((JSONObject o) -> {
                    try {
                        return o.getString("nombre");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }));

        // Create a new JSONArray from the sorted list
        return new JSONArray(jsonObjectList);
    }

    public static Result chartAreaTematica() throws JSONException {
        JSONObject retorno = new JSONObject();
        String sql ="select a.id, a.descripcion, count(*) total " +
                "from vtk_catalogo vc " +
                "inner join vtk_areatematica va on vc.id = va.catalogo_id " +
                "inner join areatematica a on va.areatematica_id = a.id " +
                "group by a.id, a.descripcion " +
                "order by total desc";
        JSONArray jaColores = coloresBG();
        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
        JSONArray jaDataset = new JSONArray();
        ArrayList<String> labels = new ArrayList<>();        
        ArrayList<Integer> numeros = new ArrayList<>();
        ArrayList<String> colores = new ArrayList<>();
        int iColores = 0;
        for (SqlRow row:sqlRows){
            /*
            JSONObject joData = new JSONObject();
            JSONArray jaData = new JSONArray();
            JSONObject joAux = new JSONObject();

            joData.put("x",row.getString("descripcion"));
            joData.put("y", row.getInteger("total"));
            jaData.put(joData);

            joAux.put("data", jaData);
            joAux.put("label", row.getString("descripcion"));
            jaDataset.put(joAux);
             */
            labels.add(row.getString("descripcion"));
            numeros.add(row.getInteger("total"));
           // colores.add(jaColores.get(iColores++));
        }
        JSONObject jo = new JSONObject();
        jo.put("label", "ds1");
        jo.put("data", numeros);
        jo.put("pointStyle", "circle");
        jo.put("pointRadius", 10);
        jo.put("pointHoverRadius", 15);
        jo.put("backgroundColor", coloresBG());
        jaDataset.put(jo);
        retorno.put("labels", labels);
        retorno.put("datasets",jaDataset);


        return ok (retorno.toString());
    }


}
