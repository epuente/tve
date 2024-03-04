package controllers;

import classes.ColorConsola;
import classes.Duracion;
import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import models.videoteca.*;
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


    public static Result listDTSS() throws JSONException {
        System.out.println("Desde AdminVideotecaController.ajaxList");
        String filtro = request().getQueryString("search[value]");
        String colOrden =  request().getQueryString("order[0][column]");
        String tipoOrden = request().getQueryString("order[0][dir]");

        System.out.println( "parametros nombre columna a ordenar:"+ request().getQueryString("columns["+colOrden+"][data]"));
        String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");
        System.out.println("-10");
        List<Sala> ps = Sala.find.all();
        System.out.println("-20");
        int numPag = 0;
        if (Integer.parseInt(request().getQueryString("start")) != 0)
            numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
        System.out.println("-30");
        /*
		Page<Sala> pagp = Sala.page(numPag ,
							Integer.parseInt(request().getQueryString("length"))
							, filtro
							, nombreColOrden
							, tipoOrden
							);
		*/
        com.avaje.ebean.Page<VtkCatalogo> pagp = null;
        System.out.println("-40");
        pagp = VtkCatalogo.page(numPag ,
                Integer.parseInt(request().getQueryString("length"))
                , filtro
                , nombreColOrden
                , tipoOrden
        );
        System.out.println("-50");

        System.out.println("-60 pagp "+pagp);

        if (pagp !=null)
            pagp.getList().forEach(x->{
                System.out.println("id "+x.id);
                for (VtkAreatematica at : x.areastematicas) {
                    System.out.println("    " + at);
                }
            });


        System.out.println("Despues del pagp");
        response().setContentType("application/json");
        JSONObject json2 = new JSONObject();
        try {
            json2.put("draw",  request().getQueryString("draw")+1  );
            json2.put("recordsTotal", ps.size());
            json2.put("recordsFiltered", pagp!=null?pagp.getTotalRowCount():0);

            JSONArray losDatos = new JSONArray();
            if (pagp!=null)
                for( VtkCatalogo p : pagp.getList()  ){
                    JSONObject datoP = new JSONObject();
                    datoP.put("id", p.id);
                    datoP.put("clave", p.clave);
                    datoP.put("serie", p.serie==null?"<i>No definida</i>":p.serie.descripcion);
                    datoP.put("titulo", p.titulo);
                    datoP.put("obra", p.obra);
                    datoP.put("catalogador", p.catalogador.nombreCompleto());
                    datoP.put("productor", p.validador!=null?p.validador.nombreCompleto()+"&nbsp;&nbsp;<small><a href=\"javascript:catalogoAsignaProd("+p.id+")\" class=\"pull-right\">Cambiar</a></small>":null);
                    JSONArray areastematicas = new JSONArray();
                    for (VtkAreatematica at : p.areastematicas) {
                        if (at.areatematica!=null) {
                            JSONObject area = new JSONObject();
                            area.put("descripcion", at.areatematica.descripcion);
                            areastematicas.put(area);
                            if (at.areatematica.id == 99)
                                datoP.put("areaTematicaOtra", p.areaTematicaOtra);
                        }
                    }
                    datoP.put("areastematicas", areastematicas);
                    losDatos.put(datoP);
                }

            System.out.println("-30 "+ losDatos);

            if ( pagp!=null && pagp.getTotalRowCount()>0 ){
                json2.put("data", losDatos);
                System.out.println("retornando "+ json2.toString()  );
            } else {
                json2.put("data", new JSONArray() );
                return ok( json2.toString()  );
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(json2.toString());
        return ok( json2.toString() );

    }

    public static Result asignarProductor() throws JSONException {

        Logger.debug("Desde AdminVideotecaController.asignarProductor");

        JsonNode json = request().body().asJson();
        String clave = json.findValue("clave").asText();
        Long idProductor = json.findValue("idProductor").asLong();
        JSONObject jo = new JSONObject();
        jo.put("estado", "sin asignar");

        VtkCatalogo vc = VtkCatalogo.find.where().eq("clave", clave).findUnique();
        if (vc != null ){
            vc.validador = Personal.find.byId(idProductor);
            vc.update();
            jo.put("estado", "correcto");
        }


        return ok (jo.toString());

    }

}

