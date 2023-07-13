package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.CuentaRol;
import models.PreAgenda;
import models.PreAgendaRol;
import models.Rol;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ValidacionesController extends Controller{
	
	
	public static Result  ajaxValidaCantidadPerfiles() throws JSONException {
		// Cuenta cuantos perfiles personal asignado
		// Tipos de personal
		JsonNode json = request().body().asJson();
		JsonNode jcrol =  json.findPath("ctaRol");
		JSONObject json2 = new JSONObject();	
		
		System.out.println("\n\n\n\n\n\n\nDesde ValidacionesController.ajaxValidaCantidadPerfiles    " +json);
		System.out.println("id del evento (ajaxValidaCantidadPerfiles) "+json.findPath("id").asLong());
		System.out.println("id del estado "+json.findPath("estado").findPath("id").asLong());
		
		List<Rol> tsp = Rol.find.all();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Rol tp : tsp) {
			if ( map.get("totalTipo"+tp.id) == null ) {
				map.put("totalTipo"+tp.id, 0);				
			}
		}
		for (JsonNode c  : jcrol)  {
			Long idCR = c.findPath("id").asLong();
			System.out.println("--->>>> >>>> "+idCR);
			//Buscar el rol
			Rol rol= CuentaRol.find.ref(idCR).rol;
			map.put("totalTipo"+rol.id, map.get("totalTipo"+rol.id)+1);
		}
		
		JSONArray jArray1 = new JSONArray();
		for (Entry<String, Integer> entry : map.entrySet())
		{			 
		    System.out.println("MAPA "+entry.getKey() + "/" + entry.getValue());
		    PreAgenda g = PreAgenda.find.fetch("personal").where().eq("id",json.findPath("id").asLong()).findUnique();
		    List<PreAgendaRol> personal = g.personal.stream().filter(f->f.rol.id ==  Long.parseLong(entry.getKey().substring(9)) ).collect(Collectors.toList());
		    if (personal.size()>0) {
		    	int solicitado = personal.get(0).cantidad;
			    if ( solicitado  > entry.getValue()  ) {
			    	JSONObject jo = new JSONObject();
			    	jo.put("tipo",Rol.find.byId( Long.parseLong(entry.getKey().substring(9))).descripcion);
			    	jo.put("solicitado", solicitado);
			    	jo.put("asignado", entry.getValue());
			    	jArray1.put( jo);
			    }
		    }
		}	
		
		json2.put("errores", jArray1);	
		System.out.println("retorno "+json2.toString());
		return ok(json2.toString());		
	}
	
	
	/* VALIDACIONES:
	 * 	*	La fecha de inicio debe ser anterior a la fecha de fin
	 * 	*	Si existe fecha de salida esta debeser anterior a la fecha de inicio
	 *
	 */
	
/*	Retorna JSON
		valido:   true / false
		En caso de ser valido=false, regresa un arreglo con la/las descripciones de los errores encontrados*/
	
	
	
	
	public static Result  ajaxValidaPrevioAutorizar() throws ParseException {
		JsonNode json = request().body().asJson();
		System.out.println("Desde ValidacionesController.ajaxValidaPrevioAutorizar");
		System.out.println(json);
		JSONObject json2 = new JSONObject();		
		JSONArray jArray = new JSONArray();
		
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date inicio =  sdf.parse( json.findPath("inicio").asText()  );
		Date fin =  sdf.parse( json.findPath("fin").asText()  );
		System.out.println(Objects.equals(json.findPath("salidas").asText(), ""));
		Date salida=null;		
		if (!Objects.equals(json.findPath("salidas").asText(), "") && !Objects.equals(json.findPath("salidas").findPath("salida").asText(), ""))
			salida =  sdf.parse( json.findPath("salidas").findPath("salida").asText()  );
		Long fase = json.findPath("fase").findPath("id").asLong();
		
		
		System.out.println("id del evento "+json.findPath("id").asLong());
		System.out.println("id del estado "+json.findPath("estado").findPath("id").asLong());
		System.out.println("personalIds "+json.findPath("personalIds"));
		System.out.println("personalIds traverse "+json.findPath("personalIds").traverse());
		System.out.println(salida);
		
		if (json.findPath("estado.id").asLong() == 5L) {
		}
		if (json.findPath("estado.id").asLong() >= 7L) {
		}		
		JSONObject error = new JSONObject();
		try {
			json2.put("valido", true);
			
			System.out.println(json.findPath("estado").findPath("id").asLong());
			System.out.println(json.findPath("estado").findPath("id").asLong() >= 5L);
			
			if (json.findPath("estado").findPath("id").asLong() >= 5L) {
			//	PreAgenda x = PreAgenda.find.byId(json.findPath("id").asLong());
				////////////////////////////////////   Y LA HORAS  DESDE EL FORMULARIO????????
				System.out.println(inicio+"    "+fin);
				System.out.println(inicio.after(fin));
				if (inicio.after(fin)) {
					json2.put("valido", false);					
					error.put("descripcion", "La hora de inicio debe ser antes de la hora de finalización.");
					jArray.put(error);
				}
////////////////////////////////////Y LA HORA DE SALIDA DESDE EL FORMULARIO????????	
				
				System.out.println("inicio "+inicio);
				if (salida != null) {
					if (salida.after(inicio)) {
						json2.put("valido", false);
						jArray.put(  (new JSONObject()).put("descripcion", "La hora de salida no puede ser posterior a la hora de inicio del evento")   );
					}
				}
				
				if (fase ==2 ) {
					if (json.findPath("personalIds").size()== 0) {
						json2.put("valido", false);
						jArray.put(  (new JSONObject()).put("descripcion", "No se ha asignado personal para cubrir la grabación")   );						
					}
				}
				

				
				
			}
			
			json2.put("errores", jArray);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("regresando: "+json2.toString());
		return ok(json2.toString());
		
	}

}
