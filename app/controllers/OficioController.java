package controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import models.Folio;
import models.Oficio;
import play.mvc.Controller;
import play.mvc.Result;


public class OficioController extends ControladorDefault{
	


	public static Result ajaxList(){
		System.out.println("Desde OficioController.ajaxList - DT");
		System.out.println("pars "+request()+"\n\n\n\n");
		JSONObject json = new JSONObject();		
		int filtrados = 0;
		int sinFiltro = 0;
		
		List<String> listaCols = Arrays.asList("oficio", "urremitente.nombreLargo", "descripcion");
		
		String filtro = request().getQueryString("search[value]");
		System.out.println("filtro "+filtro);

		int colOrden =   Integer.parseInt( request().getQueryString("order[0][column]")   );
		String tipoOrden = request().getQueryString("order[0][dir]");		
		int numPag = 0;
		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));		
		int numRegistros = Integer.parseInt(request().getQueryString("length"));
		
		Query<Oficio> q1 = Oficio.find;
		Query<Oficio> q2 = Oficio.find.where("lower(oficio) like :cadena "
				+ "or lower(urremitente.nombreLargo) like :cadena "
				+ "or lower(descripcion) like :cadena")
				.setParameter("cadena", "%"+filtro.toLowerCase()+"%")	;
		
		Page<Oficio> oficios = Oficio.find				
				.fetch("urremitente")
				.fetch("folios")
				.where("lower(oficio) like :cadena "
						+ "or lower(urremitente.nombreLargo) like :cadena "
						+ "or lower(descripcion) like :cadena "
						+ "or lower(titulo) like :cadena")
				.setParameter("cadena", "%"+filtro.toLowerCase()+"%")
			 	.orderBy( listaCols.get(colOrden) +" "+tipoOrden )
				.findPagingList(numRegistros)
				.setFetchAhead(false)
				.getPage(numPag);
		

		
		filtrados = q2.findList().size();
		sinFiltro = q1.findList().size();
		
		try {
			json.put("draw",  new Date().getTime()  );
			json.put("recordsTotal",  sinFiltro );
			json.put("recordsFiltered", filtrados);

			JSONArray losDatos = new JSONArray();
			for( Oficio o : oficios.getList()  ){
				JSONObject datoP = new JSONObject();
				datoP.put("id", o.id);
				datoP.put("oficio", o.oficio);
				datoP.put("ur", o.urremitente.nombreLargo);
				datoP.put("descripcion", o.descripcion);
				datoP.put("titulo", o.titulo);
				//datoP.put("folios", o.folios.stream().map(e -> e.folio.toString() ).reduce("", String::concat));
				
				JSONArray arrayFolios = new JSONArray();
				
				for ( Folio f : o.folios  ) {
					JSONObject losFolios = new JSONObject();
					losFolios.put("id", f.id);
					losFolios.put("numero", f.numfolio);
				//	stringFolios+=f.folio.toString()+", ";
					arrayFolios.put(losFolios);
				}
				
				
				//datoP.put("folios", stringFolios.substring(0, stringFolios.length()-2));
				datoP.put("arrFolios", arrayFolios);
				losDatos.put(datoP);
			}
			if ( oficios.getTotalRowCount()>0 ){
				json.put("data", losDatos);
			} else {
				json.put("data", new JSONArray() );
				return ok( json.toString()  );
			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println(json.toString());
		return ok( json.toString()  );		
	}	
	
	
}
