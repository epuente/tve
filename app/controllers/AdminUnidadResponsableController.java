package controllers;

import com.avaje.ebean.Page;
import models.UnidadResponsable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.catalogos.UnidadResponsable.*;

import java.util.List;

import static play.data.Form.form;

public class AdminUnidadResponsableController extends Controller{
	
    public static Result list() {
        return ok(list.render());
    }	
	
	
	public static Result create(){
        Form<UnidadResponsable> urForm = form(UnidadResponsable.class);
        return ok(
        		createForm.render(urForm)
        );		
	}
	

	public static Result save(){
		System.out.println("Desde UnidadResponsableController.save");
        Form<UnidadResponsable> urForm = form(UnidadResponsable.class).bindFromRequest();
        if (urForm.hasErrors()){
        	return badRequest(createForm.render(urForm));
        }
        urForm.get().save();
        flash("success", "Se agregó la unidad académica " + urForm.get().nombreLargo + ".");
        return redirect (controllers.routes.AdminUnidadResponsableController.list());
                
	}    public static Result edit(Long id) {
        Form<UnidadResponsable> urForm = form(UnidadResponsable.class).fill(
            UnidadResponsable.find.byId(id)
        );
        return ok(
        		editForm.render(id, urForm)
        );
    }
    
    public static Result update(Long id) {
        Form<UnidadResponsable> urForm = form(UnidadResponsable.class).bindFromRequest();
        if(urForm.hasErrors()) {
            return badRequest(editForm.render(id, urForm));
        }
        urForm.get().update(id);
        flash("success", "Se actualizó la unidad académica " + urForm.get().nombreLargo + ".");
        return list();
    }    
	
    public static Result delete(Long id) {
    	System.out.println("DEsde UnidadREsponsable.delete");
        UnidadResponsable x = UnidadResponsable.find.ref(id);
        String nombre = x.nombreLargo;
        x.delete();
        flash("success", "Se eliminó la unidad académica "+nombre);
        return list();
    }    
    
    
	public static Result listDTSS(){
		System.out.println("Desde UnidadresponsableController.listDTSS............");
		System.out.println( "parametros 0:"+ request() );
		System.out.println( "parametros 10:"+ request().body());
		System.out.println( "parametros 20:"+ request().body().asJson() );

		System.out.println( "parametros ?:"+ request().queryString()  );

		System.out.println( "parametros draw:"+ request().getQueryString("draw"));
		System.out.println( "parametros start:"+ request().getQueryString("start"));
		System.out.println( "parametros length:"+ request().getQueryString("length"));
		System.out.println( "parametros seach[value]:"+ request().getQueryString("search[value]"));

		System.out.println( "parametros order[0][column]:"+ request().getQueryString("order[0][column]"));
		System.out.println( "parametros order[0][dir]:"+ request().getQueryString("order[0][dir]"));


		String filtro = request().getQueryString("search[value]");
		String colOrden =  request().getQueryString("order[0][column]");
		String tipoOrden = request().getQueryString("order[0][dir]");

		System.out.println( "parametros nombre columna a ordenar:"+ request().getQueryString("columns["+colOrden+"][data]"));
		String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");
		
		List<UnidadResponsable> ps = UnidadResponsable.find.all();
		
		
		int numPag = 0;
		

		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
		Page<UnidadResponsable> pagp = UnidadResponsable.page(numPag , 
							Integer.parseInt(request().getQueryString("length"))
							, filtro
							, nombreColOrden
							, tipoOrden
							);

	
		
		response().setContentType("application/json");
		JSONObject json2 = new JSONObject();
		try {
			json2.put("draw",  request().getQueryString("draw")+1  );
			json2.put("recordsTotal", ps.size());
			json2.put("recordsFiltered", pagp.getTotalRowCount());

			JSONArray losDatos = new JSONArray();			
			for( UnidadResponsable p : pagp.getList()  ){
				JSONObject datoP = new JSONObject();
				datoP.put("id", p.id);
				datoP.put("nombreLargo", p.nombreLargo);
				datoP.put("nombreCorto", p.nombreCorto);
				datoP.put("areaConocimiento", p.areaConocimiento);
				datoP.put("nivel", p.nivel);
				losDatos.put(datoP);
			}
			if ( pagp.getTotalRowCount()>0 ){
				json2.put("data", losDatos);
			} else {
				json2.put("data", new JSONArray() );
				return ok( json2.toString()  );
			}			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("retornando:  "+json2.toString());
		return ok( json2.toString() );  
		
	}      
    
    
}
