package controllers;

import static play.data.Form.form;

import java.util.List;

import classes.Notificaciones.Notificacion;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;

import models.Ctacorreo;
import play.data.Form;
import play.mvc.Result;
import views.html.admin.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AdminCorreoController extends ControladorSeguroAdministrador{
	
    public static Result GO_HOME = redirect(
            routes.AdminCorreoController.list()
        );	
	
	public static Result list(){
		return  correosSalida();
		
	}	
	
    @SuppressWarnings("static-access")
	public static Result create(){    	
    	Form<Ctacorreo> forma = play.data.Form.form(Ctacorreo.class);    	
    	Ctacorreo nueva = new Ctacorreo();
		Ctacorreo activa = nueva.find.where().eq("activa", true).findUnique();
        return ok(
        		createForm.render(forma, activa)
        );   	
    }	
    
    
    public static Result save(){
        Form<Ctacorreo> forma = form(Ctacorreo.class).bindFromRequest();
        System.out.println("Desde AdminCorreoController.save.........................");
		Ctacorreo activa = Ctacorreo.find.where().eq("activa", true).findUnique();
System.out.println(forma);
        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, activa));
        }
        Ctacorreo x = forma.get();
        
        if (x.activa) {        	
        	x.resetActiva();
        }       

        x.save();
		Notificacion noti = Notificacion.getInstance();
		noti.recargar();
        flash("success", "Se agregó la cuenta de correo de salida");
        return redirect ( routes.AdminCorreoController.correosSalida());   	
    }   
    
    
    @SuppressWarnings("static-access")
	public static Result edit(Long id) {
    	Ctacorreo aux = Ctacorreo.find.byId(id);
		Ctacorreo activa = Ctacorreo.find.where().eq("activa", true).findUnique();

		if (aux.cuenta != null){
	        Form<Ctacorreo> forma = form(Ctacorreo.class).fill(aux);
	        return ok( editForm.render(id, forma, activa)  );
		} else {
			return ok( editForm.render(id, null, activa)  );
		}
    }    

    public static Result update(Long id) {
    	System.out.println("desde AdminCtacorreoController.update");
        Form<Ctacorreo> forma = form(Ctacorreo.class).bindFromRequest();
        System.out.println(forma);
        System.out.println("...000");
        Ctacorreo x = forma.get(); 
        if (x.activa) {        	
        	x.resetActiva();
        }          
        x.update(id);
		Notificacion noti = Notificacion.getInstance();
		noti.recargar();
        flash("success", "Se actualizó el Ctacorreo");
        return GO_HOME;
    }    
    
    
    public static Result delete() {
    	String estado="error";
    	JsonNode json = request().body().asJson();
    	System.out.println("Desde AdminCtacorreoController.delete");
        Ctacorreo x = Ctacorreo.find.ref(json.findValue("id").asLong());
       	x.delete();
		Notificacion noti = Notificacion.getInstance();
		noti.recargar();
       	estado="borrado";
        return ok (  "{\"estado\": \""+estado+"\"}"  );
    }	
	
    
    
	public static Result correosSalida() {
		System.out.println("Desde AdministracionController.correosSalida");
		List<Ctacorreo> cuentas = Ctacorreo.find.all();
		return ok( correos.render(cuentas)  );		
	}
		    
	
	public static Result listDTSS(){
		System.out.println(">>  >>  >>  Desde AdminCorreoController.listDTSS............");
		System.out.println( "parametros 0:"+ request() );
		//System.out.println( "parametros 10:"+ request().body());
		//System.out.println( "parametros 20:"+ request().body().asJson() );
		/*
		System.out.println( "parametros ?:"+ request().queryString()  );
		
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

	//	System.out.println( "parametros nombre columna a ordenar:"+ request().getQueryString("columns["+colOrden+"][data]"));
		String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");
		if (request().getQueryString("order[0][column]").compareTo("5")==0)
			nombreColOrden = "c11";
		
		List<Ctacorreo> ps = Ctacorreo.find.all();

		int numPag = 0;
		

		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
		Page<Ctacorreo> pag = Ctacorreo.page(numPag , 
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
			json2.put("recordsFiltered", pag.getTotalRowCount());
			JSONArray losDatos = new JSONArray();	
			
			for( Ctacorreo p : pag.getList()  ){
				JSONObject datoP = new JSONObject();
				datoP.put("id", p.id);
				datoP.put("hostname", p.hostname);
				datoP.put("puerto", p.puerto);
				datoP.put("cuenta", p.cuenta);
//				datoP.put("activa", p.activa?"Si":"No");
				datoP.put("activa", p.activa==false?"No":"Si");

				losDatos.put(datoP);
			}
			if ( pag.getTotalRowCount()>0 ){
				json2.put("data", losDatos);
			} else {
				json2.put("data", new JSONArray() );
				return ok( json2.toString()  );
			}
		} catch (JSONException e) {
			System.out.println("Ocurrió un error "+e);
			e.printStackTrace();
		}	
		return ok( json2.toString() );  
		
	}   
	
	public static Result existeActiva() {
//		Ctacorreo aux = Ctacorreo.buscarActiva();
		Ctacorreo activa = Ctacorreo.find.where().eq("activa", true).findUnique();

		System.out.println(activa);
		String cadena = "{\"existe\":"+((activa==null)?false:true);
		if (activa!=null) {
			cadena+=" ,\"cuenta\":\""+activa.cuenta+"\"";
		}
				cadena+="}";
		System.out.println(cadena);
		return ok (  cadena );
	}



	private static final String key = "aesEncryptionKey";
	private static final String initVector = "encryptionIntVec";
	public static String encrypt(String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}



	public static String decrypt(String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}



	public static Result pruebaEncrypt(){
		String originalString = "password";
		System.out.println("Original String to encrypt - " + originalString);
		String encryptedString = encrypt(originalString);
		System.out.println("Encrypted String - " + encryptedString);
		String decryptedString = decrypt(encryptedString);
		System.out.println("After decryption - " + decryptedString);
		return ok("OK");
	}
	
}

