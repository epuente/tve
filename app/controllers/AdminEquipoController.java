package controllers;

import static play.data.Form.form;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Equipo;
import models.Rol;
import play.data.Form;
import play.mvc.Result;
import views.html.catalogos.Equipo.*;

public class AdminEquipoController extends ControladorSeguroAdminEA{

    public static Result GO_HOME = redirect(
            routes.AdminEquipoController.list()
        );	
	
	public static Result list(){
		return ok(list.render( )   );
		
	}	
	
    public static Result create(){    	
    	Form<Equipo> forma = play.data.Form.form(Equipo.class);
    	List<Rol> roles = Rol.find.all();
        return ok(
        		createForm.render(forma, roles)
        );   	
    }	
    
    
    public static Result save(){
        Form<Equipo> forma = form(Equipo.class).bindFromRequest();
System.out.println(forma);        
        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, Rol.find.all()));
        }
        forma.get().save();
	
        flash("success", "Se agregó el equipo");
        return list();   	
    }   
    
    
    public static Result edit(Long id) {
    	Equipo aux = Equipo.find.byId(id);
    	Form<Equipo> forma = form(Equipo.class).fill( aux  );
	        return ok( editForm.render(id, forma, Rol.find.all())  );

    }    

    public static Result update(Long id) {
        Form<Equipo> forma = form(Equipo.class).bindFromRequest();
        if(forma.hasErrors()) {
            return badRequest(editForm.render(id, forma, Rol.find.all() ));
        }
        forma.get().update(id);
        flash("success", "Se actualizó el equipo");
        return GO_HOME;
    }    
    
    
    public static Result delete(Long id) {
    	System.out.println("Desde EquipoController.delete");
        Equipo x = Equipo.find.ref(id);
        String nombre = x.descripcion;
        x.delete();
        flash("success", "Se eliminó "+nombre);
        return list();
    }     

	public static Result listDTSS(){
		System.out.println("Desde AdminEquipoController.listDTSS............"+new Date());
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
		List<Equipo> ps = Equipo.find.all();
		int numPag = 0;
		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
		com.avaje.ebean.Page<Equipo> pagp = Equipo.page(numPag , 
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
			for( Equipo p : pagp.getList()  ){
				JSONObject datoP = new JSONObject();
				datoP.put("id", p.id);
				datoP.put("descripcion", p.descripcion);
				datoP.put("estado", p.estado.descripcion);
				datoP.put("serie", p.serie);
				datoP.put("marca", p.marca);
				datoP.put("modelo", p.modelo);
				datoP.put("observacion", p.observacion);
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
//System.out.println("retornando:  "+json2.toString());		
		return ok( json2.toString() );  
		
	}       
    
    
}
