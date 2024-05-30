package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.data.Form;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Result;
import views.html.catalogos.Sala.createForm;
import views.html.catalogos.Sala.editForm;
import views.html.catalogos.Sala.list;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.data.Form.form;
import play.db.ebean.Model;
public class AdminSalaController extends ControladorSeguroAdministrador {

    public static Result GO_HOME = redirect(
            routes.AdminSalaController.list()
        );	
	
    public static Result list(){
    	System.out.println("Desde SalaController.list");
    	return ok (list.render());
    }
    
	public static Result ajaxList(){
		System.out.println("DEsde AdminSalaController.ajaxList");
		String filtro = request().getQueryString("search[value]");
		String colOrden =  request().getQueryString("order[0][column]");
		String tipoOrden = request().getQueryString("order[0][dir]");

System.out.println( "parametros nombre columna a ordenar:"+ request().getQueryString("columns["+colOrden+"][data]"));
		String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");
		
		List<Sala> ps = Sala.find.all();
		int numPag = 0;
		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
		Page<Sala> pagp = Sala.page(numPag , 
							Integer.parseInt(request().getQueryString("length"))
							, filtro
							, nombreColOrden
							, tipoOrden
							);

	
		pagp.getList().forEach(x->{
			System.out.println("cliclo "+x.descripcion);
			for (OperadorSala op : x.operadores) {
				System.out.println("    " + op.personal.id + " " + op.personal.nombreCompleto());
			}
		});
		
		
		System.out.println("Despues del pagp");
		response().setContentType("application/json");
		JSONObject json2 = new JSONObject();
		try {
			json2.put("draw",  request().getQueryString("draw")+1  );
			json2.put("recordsTotal", ps.size());
			json2.put("recordsFiltered", pagp.getTotalRowCount());

			JSONArray losDatos = new JSONArray();			
			for( Sala p : pagp.getList()  ){
				JSONObject datoP = new JSONObject();
				datoP.put("id", p.id);
				datoP.put("descripcion", p.descripcion);				
				JSONArray operadores = new JSONArray();
				for (OperadorSala operdor : p.operadores) {
					if (operdor.personal != null) {
						JSONObject operador = new JSONObject();
						operador.put("nombre", operdor.personal.nombreCompleto());
						operador.put("turno", operdor.turno.compareToIgnoreCase("M")==0?"Matutino":"Vespertino");
						operador.put("personalId", operdor.personal.id);
						operadores.put(operador);
					}
				}
				datoP.put("operadores", operadores);
				
				if (p.mantenimiento != null) {
					System.out.println("... # mantos para la sala:"+p.mantenimiento.size());
					Date masCercano = new Date();
					System.out.println("---");
					System.out.println("masCercano "+masCercano);

					p.mantenimiento.sort(new Comparator<SalaMantenimiento>(){
						@Override
						public int compare(SalaMantenimiento s1, SalaMantenimiento s2) {
							return s1.desde.compareTo(s2.desde);													
						}
					});
					
					for (SalaMantenimiento sm : p.mantenimiento){
						Date hoy = Calendar.getInstance().getTime();
						if (sm != null  &&  p.mantenimiento.get(0).hasta.after(hoy))
							datoP.put("manttoProximo", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(p.mantenimiento.get(0).desde) + " - "+new SimpleDateFormat("HH:mm").format(p.mantenimiento.get(0).hasta));
					}					
				}
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
		System.out.println(json2.toString());
		return ok( json2.toString() );
		
		
	
		
	}	
	
    public static Result create(){    	
    	Form<Sala> forma = play.data.Form.form(Sala.class);
    	List<Rol> roles = Rol.find.all();
        return ok(
        		createForm.render(forma, roles)
        );   	
    }	
    
    
    public static Result save(){
        Form<Sala> forma = form(Sala.class).bindFromRequest();
System.out.println(forma);
        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, Rol.find.all()));
        }
        forma.get().save();
	
        flash("success", "Se agregó la sala "+forma.get().descripcion);
        return list();   	
    }


	public static Result save2(){
		System.out.println("\n\n\n\ndesde AdminSalaController.update2....");
		JsonNode json = request().body().asJson();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(json);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		mapper.setDateFormat(df);


		try {
			Sala sala = mapper.readValue(json.traverse(), Sala.class);
			sala.save();
		} catch (IOException e) {
			System.out.println("ERRRRROR "+e.getMessage());
			throw new RuntimeException(e);
		}

		return ok("{\"estado\":\"creado\"}");


	}

    
    
    public static Result edit(Long id) {
    	Sala aux = Sala.find.byId(id);
    	Form<Sala> forma = form(Sala.class).fill( aux  );
	        return ok( editForm.render(id, forma, Rol.find.all(), "edit"  ));

    }    
    @Transactional
    public static Result update() {
		System.out.println("\n\n\n\ndesde AdminSalaController.update....");
        Form<Sala> forma = form(Sala.class).bindFromRequest();
        Sala sala = forma.get();
		System.out.println(forma);
        Sala x = new Sala();        
        x.id =  sala.id;
        x.descripcion = sala.descripcion;
        OperadorSala.find.where().eq("sala.id", x.id).findList().forEach(Model::delete);
        SalaMantenimiento.find.where().eq("sala.id", x.id).findList().forEach(Model::delete);
        for (int ix=0;ix<=1;ix++) {
       	    Long idOp = sala.operadores.get(ix).personal.id;
        	System.out.println("    idOp:"+idOp);
        	String t = sala.operadores.get(ix).turno;
        	System.out.println("    t:"+t);
        	if (idOp!=null) {
        		OperadorSala op = new OperadorSala();
        		op.sala = Sala.find.setId(x.id).findUnique();
        		op.personal = Personal.find.setId(idOp).findUnique();
        		op.turno = t;    
        		op.save();
        	}
        }
        
        System.out.println("tam mantenimiento "+sala.mantenimiento.size());
        
        for (int im=0; im<sala.mantenimiento.size();im++) {
        	SalaMantenimiento m = new SalaMantenimiento();
        	m.sala = Sala.find.setId(x.id).findUnique();
        	m.desde = sala.mantenimiento.get(im).desde;
        	m.hasta = sala.mantenimiento.get(im).hasta;
        	m.tipo = TipoMantenimiento.find.setId(sala.mantenimiento.get(im).tipo.id).findUnique();
        	m.motivo = sala.mantenimiento.get(im).motivo;
        	m.save();
        }
        
        
        x.update();
        flash("success", "Se actualizó la sala: "+x.descripcion);
        return ok ( Json.parse("{\"estado\":\"actualizado\"}"));
        //return redirect( routes.AdminSalaController.list());
    }    
    
    

    public static Result update2() {
		Ebean.beginTransaction();
		try {
			System.out.println("\n\n\n\ndesde AdminSalaController.update2....");
			JsonNode json = request().body().asJson();
			ObjectMapper mapper = new ObjectMapper();
			System.out.println(json);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
			mapper.setDateFormat(df);

			Sala sala = mapper.convertValue(json, Sala.class);

			for (int i = 0; i < sala.mantenimiento.size(); i++) {
				System.out.println("- - d " + sala.mantenimiento.get(i).desde);
				System.out.println("- - h " + sala.mantenimiento.get(i).hasta);
			}

			Sala x = Ebean.find(Sala.class).setId(sala.id).findUnique();
			x.descripcion = sala.descripcion;
			OperadorSala.find.where().eq("sala.id", x.id).findList().forEach(Ebean::delete);
			SalaMantenimiento.find.where().eq("sala.id", x.id).findList().forEach(Ebean::delete);
			for (int ix = 0; ix < sala.operadores.size(); ix++) {
				Long idOp = sala.operadores.get(ix).personal.id;
				System.out.println("    idOp   (personal id) :" + idOp);
				String t = sala.operadores.get(ix).turno;
				System.out.println("    t turno:" + t);
				if (idOp != null) {
					OperadorSala op = new OperadorSala();
					//op.sala = Sala.find.setId(x.id).findUnique();
					op.sala= Ebean.find(Sala.class).setId(x.id).findUnique();
					//op.personal = Personal.find.setId(idOp).findUnique();
					op.personal = Ebean.find(Personal.class).setId(idOp).findUnique();
					op.turno = t;
				//	op.id = new Date().getTime();
					//op.save();
					Ebean.save(op);
				}
			}

			System.out.println("tam mantenimiento " + sala.mantenimiento.size());


			for (int im = 0; im < sala.mantenimiento.size(); im++) {
				System.out.println("   desde " + sala.mantenimiento.get(im).desde);
				System.out.println("   hasta " + sala.mantenimiento.get(im).hasta);

				SalaMantenimiento m = new SalaMantenimiento();
				m.sala = Sala.find.setId(x.id).findUnique();
				m.desde = sala.mantenimiento.get(im).desde;
				m.hasta = sala.mantenimiento.get(im).hasta;
				m.tipo = TipoMantenimiento.find.setId(sala.mantenimiento.get(im).tipo.id).findUnique();
				m.motivo = sala.mantenimiento.get(im).motivo;
				//m.save();
				Ebean.save(m);
			}


			//x.update();
			Ebean.save(x);
			Ebean.commitTransaction();
			flash("success", "Se actualizó la sala: " + x.descripcion);
			return ok(Json.parse("{\"estado\":\"actualizado\"}"));
		} catch (Exception e){
			System.out.println("Error en AdminSalaController.update2. se realiza rollback "+e+" *** "+e.getMessage()+" --- "+e.getCause());
			Ebean.rollbackTransaction();
			return ok(Json.parse("{\"estado\":\"error\"}"));
        }finally {
            Ebean.endTransaction();
        }

    }
    
    public static Result delete(Long id) {
    	System.out.println("Desde SalaController.delete");
        Sala x = Sala.find.ref(id);
        String nombre = x.descripcion;
        x.delete();
        flash("success", "Se eliminó la sala "+nombre);
        return list();
    }  	
	
	
}
