package controllers;

import classes.ListaPersonal;
import classes.ListaPersonal.lista;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import models.videoteca.Serie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.Form;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import views.html.personal.*;

import static play.data.Form.form;


public class AdminPersonalController extends ControladorSeguroAdministrador{
	
    public static Result GO_HOME = redirect(
            routes.AdminPersonalController.list()
        );	
	
	public static Result list(){
		return ok(list.render() );
		
	}	
	
    public static Result create(){    	
    	Form<Personal> forma = play.data.Form.form(Personal.class);
    	List<Rol> roles = Rol.find.all();
        return ok(
        		createForm.render(forma, roles)
        );   	
    }	
    
    
    public static Result save(){
        Form<Personal> forma = form(Personal.class).bindFromRequest();
System.out.println(forma);        
        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, Rol.find.all()));
        }
        forma.get().save();
	
        flash("success", "Se agregó el personal");
        return list();   	
    }   
    
    
    public static Result edit(Long id) {
    	Personal aux = Personal.find.byId(id);
		if (aux.cuentas != null){
	        Form<Personal> forma = form(Personal.class).fill(
	            aux
	        );
	        return ok( views.html.personal.editForm.render(id, forma, Rol.find.all())  );
		} else {
			return ok( views.html.personal.editForm.render(id, null, Rol.find.all())  );
		}
    }    

    public static Result update(Long id) {
		System.out.println("desde AdminPersonalController.update");

		Form<Personal> forma = form(Personal.class).bindFromRequest();
		System.out.println(forma);
		System.out.println("...000");
		if(forma.hasErrors()) {
			System.out.println("...forma con errores");
			System.out.println(forma);
			return badRequest(editForm.render(id, forma, Rol.find.all() ));
		}
		System.out.println("...forma sin errores");
		System.out.println("tc: "+     forma.get().tipocontrato.id );

		//Si se trata de freelance no se incluye la tabla de horarios
		if (forma.get().tipocontrato.id ==3){
			forma.get().horarios.clear();
		}


		forma.data().forEach((k,v)->System.out.println("campo : " + k + " valor : " + v));

		Personal nvo = forma.get();
        Personal dbP = Personal.find.byId(id);

		//for	nvo.cuentas
        Ebean.beginTransaction();
        try {
            dbP.numEmpleado = nvo.numEmpleado;
            dbP.paterno = nvo.paterno;
            dbP.materno = nvo.materno;
            dbP.nombre = nvo.nombre;
            dbP.tipocontrato = nvo.tipocontrato;
            dbP.activo = nvo.activo;
            dbP.turno = nvo.turno;

            // Horarios
            Ebean.delete(dbP.horarios);
            dbP.horarios = nvo.horarios;

            // Correos
            Ebean.delete(dbP.correos);
            dbP.correos = nvo.correos;

            // Cuentas
            Ebean.delete(dbP.cuentas.get(0).roles);
            dbP.cuentas.get(0).roles = nvo.cuentas.get(0).roles.stream().filter(f->f.rol!=null).collect(Collectors.toList());
            Ebean.update(dbP);
            Ebean.commitTransaction();
        } catch(Exception e) {
            Ebean.rollbackTransaction();
            System.out.println("Ocurrió un error al intentar actualizar al personal "+nvo.nombreCompleto()+" "+e);
            throw new RuntimeException(e);
        }finally {
            Ebean.endTransaction();
        }



		//nvo.update();
        /*
        for (CuentaRol cr : nvo.cuentas.get(0).roles){
            System.out.println("cr: "+cr.rol);
            if (cr.rol==null)
                CuentaRol.find.setId(cr.id).findUnique().delete();
        }

         */

        return GO_HOME;
    }    
    
    
    public static Result delete(Long id) {
    	System.out.println("Desde AdminPersonalController.delete");
        Personal x = Personal.find.ref(id);
       	x.delete();
        return ok ( );
    }     
    
    public static Result ajaxLocutoresActivos(){    	
    	JsonContext jsonContext = Ebean.createJsonContext();
	    List<Personal> locutoresActivos = Personal.find.where().eq("cuentas.roles.rol.id", 15).eq("activo", "S").findList();
	    System.out.println("............    locutores activos:"+locutoresActivos.size());	    
	    return ok(jsonContext.toJsonString(locutoresActivos));
    }
    

    
	public static Result listDTSS(){
		System.out.println("\n\n\n\n\n\n\n>>  >>  >>  Desde PersonalController.listDTSS............");
		System.out.println( "parametros 0:"+ request() );
		System.out.println( "parametros ?:"+ request().queryString()  );
		
		int regInicio = Integer.parseInt( request().getQueryString("start"));
		int regsPagina = Integer.parseInt( request().getQueryString("length") );
		

		
		
		System.out.println( "parametros draw:"+ request().getQueryString("draw"));
		System.out.println( "parametros start:"+ regInicio);
		System.out.println( "parametros length:"+ regsPagina);
		System.out.println( "parametros seach[value]:"+ request().getQueryString("search[value]"));
		
		System.out.println( "parametros order[0][column]:"+ request().getQueryString("order[0][column]"));
		System.out.println( "parametros order[0][dir]:"+ request().getQueryString("order[0][dir]"));

		


		String filtro = request().getQueryString("search[value]");
		String colOrden =  request().getQueryString("order[0][column]");
		String tipoOrden = request().getQueryString("order[0][dir]");

		System.out.println( "parametros nombre columna a ordenar:"+ request().getQueryString("columns["+colOrden+"][data]"));
		String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");
		
		int total = Personal.find.all().size();
		int numPag = 0;
		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
		ListaPersonal lp = new ListaPersonal();
		List<lista> pagina = ListaPersonal.Pagina(lp, numPag ,
							Integer.parseInt(request().getQueryString("length"))
							, filtro
							, nombreColOrden
							, tipoOrden
							);
		response().setContentType("application/json");
		JSONObject json2 = new JSONObject();
		try {
			json2.put("draw",  System.currentTimeMillis() );
			json2.put("recordsTotal", total);
			json2.put("recordsFiltered", pagina.size());
			System.out.println("json2 "+json2);
			JSONArray losDatos = new JSONArray();	
			
			int bloqueDesde = regInicio;
			int bloqueHasta = 0;
			// Cuando se muestran todos los registros en una página (cuando length = -1)
			if(request().getQueryString("length").compareTo("-1")==0) {
				bloqueDesde=0;
				bloqueHasta=pagina.size();
			} else {			
				bloqueHasta = bloqueDesde+regsPagina;
				if (bloqueHasta>pagina.size())
					bloqueHasta=pagina.size();

			}
			System.out.println("bloque "+bloqueDesde+" ... "+bloqueHasta);
			for ( lista p : pagina.subList( bloqueDesde,  bloqueHasta)  ) {
				JSONObject datoP = new JSONObject();
				datoP.put("id", p.id);
				datoP.put("paterno", p.paterno);
				datoP.put("materno", p.materno);
				datoP.put("nombre", p.nombre);
				datoP.put("numEmpleado", p.numEmpleado);
				datoP.put("roles", p.roles );				
				datoP.put("contrato", p.tipocontrato);
				datoP.put("turno", p.turno);
				datoP.put("activo", p.estado);
				losDatos.put(datoP);
			}
			if ( pagina.size()>0 ){
				json2.put("data", losDatos);
			} else {
				json2.put("data", new JSONArray() );
				return ok( json2.toString()  );
			}
			
		} catch (JSONException e) {
			System.out.println("Ocurrió un error "+e);
			e.printStackTrace();
		}	
		//System.out.println(json2.toString());
		return ok( json2.toString() );  
		
	}    

	
	public static Result ajaxPersonalBorrable() {
		System.out.println("desde AdminPersonalController.ajaxPersonalBorrable");
		JsonNode json = request().body().asJson();
		Long id = json.findValue("id").asLong();
		boolean valor = false;
		boolean borrado = false;
		int fpa = FolioProductorAsignado.find.where().eq("personal.id", id).findRowCount();
		int pas = PreAgendaLocutor.find.where().eq("personal.id", id).findRowCount();
		int aspr = AgendaCuentaRol.find.where().eq("id", id).findRowCount();
	    int op = OperadorSala.find.where().eq("personal.id", id).findRowCount();

//        int series = Serie.find.where().eq("personal.id", id).findRowCount();
	    
	    System.out.println(fpa+" - "+pas+" - "+aspr+" - "+op );
	    
	    valor = ((fpa + pas + aspr + op)==0);
	    if (valor) {
            List<RegistroAcceso> ra = Ebean.find(RegistroAcceso.class).where().eq("usuario.id", id).findList();
            Ebean.delete(ra);
	        Personal x = Personal.find.ref(id);
	       	Ebean.delete(x);
	       	borrado = true;
	    }
		return ok ( "{\"borrable\":"+ valor+", \"borrado\":"+borrado+" }"); 		
	}

	public static Result ajaxEsNumEmpleadoDuplicado() {
		JsonNode json = request().body().asJson();
		String num = json.findValue("numero").asText();
		boolean duplicado = Personal.find.where().eq("numEmpleado", num).findRowCount()!=0;
		return ok ( "{\"duplicado\":"+ duplicado+" }"); 		
	}	
	
	
    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }	
	
}

