package controllers;

import classes.ColorConsola;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.libs.Json;
import play.mvc.Result;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminEAController extends ControladorSeguroAdminEA{
	
	public static Result agenda(Long idPorRevisar) throws JSONException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println("desde IngenieriaController.agenda");
		List<Folio> f = Folio.find.where().in("estado.id",  Arrays.asList(4, 5, 7) ).findList();
		
		Date fecha =  PreAgenda.find.byId(idPorRevisar).inicio;
		System.out.println("id preagenda:"+idPorRevisar);
		System.out.println("inicio:"+sdf.format(fecha));


		JSONArray losDatos = new JSONArray();



		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd mm:ss:ms");


	// Generar JSON con los eventos

for( Folio folio : f){
	for( FolioProductorAsignado pa : folio.productoresAsignados ){
		for(PreAgenda pre : pa.preagendas ){
			if(pre.estado.id == 4 || pre.estado.id == 5) {
				JSONObject jobject = new JSONObject();
					jobject.put("id", "pre"+pre.id );
					if(pre.fase.id==1L){
						if(pre.salas.size()==0){
							jobject.put("resourceId",'a');
							jobject.put("overlap", true);
							jobject.put("selectOverlap", true);
						}
						} else {
							jobject.put("resourceId","c");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
							jobject.put("salaId", pre.salas.get(0).sala.id);
						}
					
					if(pre.fase.id==2L){
						jobject.put("resourceId", 'b');
						jobject.put("overlap", true);
						jobject.put("selectOverLap", true);
					}
					if(pre.fase.id==3L && pre.salas.size()!=0 ){
						if(pre.salas.get(0).sala.id == 3L){
							jobject.put("resourceId", 'd');
						}
						if(pre.salas.get(0).sala.id == 2L){
							jobject.put("resourceId", 'e');
						}
						if(pre.salas.get(0).sala.id == 7L){
							jobject.put("resourceId",'f');
						}
						
						jobject.put("salaId", pre.salas.get(0).sala.id);
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
					}
					if(pre.fase.id==4L && pre.salas.size()!=0){
						if(pre.salas.get(0).sala.id == 3L){jobject.put("resourceId",'d');}
						if(pre.salas.get(0).sala.id == 2L){jobject.put("resourceId",'e');}
						if(pre.salas.get(0).sala.id == 7L){jobject.put("resourceId",'f');}
						if(pre.salas.get(0).sala.id == 4L){jobject.put("resourceId",'g');}
						if(pre.salas.get(0).sala.id == 1L){jobject.put("resourceId",'c');}
						jobject.put("salaId", pre.salas.get(0).sala.id);
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
					}
					if(pre.fase.id==5L){
						jobject.put("resourceId", 'h');
						jobject.put("overlap", true);
						jobject.put("selectOverlap", true);
					}
					if(pre.fase.id==6L){
						jobject.put("resourceId", 'i');
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
						jobject.put("salaId", pre.salas.get(0).sala.id);
					}
					jobject.put("start",  sdf2.format(pre.inicio));
					jobject.put("end", sdf2.format(pre.fin));
					jobject.put("selectOverlap", false);
					
					jobject.put("title", pre.folioproductorasignado.folio.numfolio+" "+pre.folioproductorasignado.folio.oficio.titulo);
					jobject.put("descripcion", pre.folioproductorasignado.folio.oficio.descripcion);
					jobject.put("tipo", "preagenda");
					jobject.put("id", pre.id);
					jobject.put("folioId", pre.folioproductorasignado.folio.id);
					jobject.put("folioNum", pre.folioproductorasignado.folio.numfolio);
					jobject.put("faseId", pre.fase.id);
					jobject.put("faseDescripcion", pre.fase.descripcion);
					jobject.put("estadoId", pre.estado.id);
					jobject.put("estadoDescripcion", pre.estado.descripcion);
					jobject.put("productorId", pre.folioproductorasignado.personal.id);
					jobject.put("productorNombre", pre.folioproductorasignado.personal.nombreCompleto());
					jobject.put("fpaId", pre.folioproductorasignado.id);
					losDatos.put(jobject);
			}	
			
		}
		
		for(Agenda age : pa.agenda ){
			if( (age.estado.id >= 7 || age.estado.id < 99)  && age.inicio!= null  ) {
				JSONObject jobject = new JSONObject();
				jobject.put("id", "age"+age.id);
					if(age.fase.id==1L){
						if(age.salas.size()==0){
							jobject.put("resourceId", "a");
							jobject.put("overlap", true);
							jobject.put("selectOverlap", true);
						} else {
							jobject.put("resourceId", "c");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
					}
					if (age.fase.id==2L){jobject.put("resourceId", "b");  jobject.put("overlap", true);}
					if (age.fase.id==3L && age.salas.size()!=0 ){
						if (age.salas.get(0).sala.id == 3L){
							jobject.put("resourceId", "d");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if (age.salas.get(0).sala.id == 2L){
							jobject.put("resourceId", "e");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if (age.salas.get(0).sala.id == 7L){
							jobject.put("resourceId", "f");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						jobject.put("salaId", age.salas.get(0).sala.id);
					}
					if (age.fase.id==4L && age.salas.size()!=0){
						if(age.salas.get(0).sala.id == 3L){
							jobject.put("resourceId", "d");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if(age.salas.get(0).sala.id == 2L){
							jobject.put("resourceId", "e");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if(age.salas.get(0).sala.id == 7L){
							jobject.put("resourceId","f");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if (age.salas.get(0).sala.id == 4L){
							jobject.put("resourceId", "g");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if(age.salas.get(0).sala.id == 1L){
							jobject.put("resourceId", "c");
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						jobject.put("salaId",  age.salas.get(0).sala.id);
					}
					if (age.fase.id==5L){
						jobject.put("resourceId", "h");
						jobject.put("overlap", true);
						jobject.put("selectOverlap", true);
					}
					if (age.fase.id==6L){
						jobject.put("resourceId", "i");
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
						jobject.put("salaId", age.salas.get(0).sala.id);
					}
					jobject.put("start", sdf2.format(age.inicio));
					jobject.put("end",   sdf2.format(age.fin));
					jobject.put("selectOverlap",  false);
				
					jobject.put("title", age.folioproductorasignado.folio.numfolio+" "+age.folioproductorasignado.folio.oficio.titulo);
					jobject.put("descripcion", age.folioproductorasignado.folio.oficio.descripcion);
					jobject.put("tipo", "agenda");
					jobject.put("id", age.id);
					jobject.put("folioId", age.folioproductorasignado.folio.id);
					jobject.put("folioNum", age.folioproductorasignado.folio.numfolio);
					jobject.put("faseId", age.fase.id);
					jobject.put("faseDescripcion", age.fase.descripcion);						
					jobject.put("estadoId", age.estado.id);
					jobject.put("estadoDescripcion", age.estado.descripcion);
					jobject.put("productorId", age.folioproductorasignado.personal.id);
					jobject.put("productorNombre", age.folioproductorasignado.personal.nombreCompleto());
					jobject.put("fpaId", age.folioproductorasignado.id);
					losDatos.put(jobject);	
			}
			
		}			
		
		
	}
	
}



			return ok(views.html.usuario.agenda.render(f,  sdf.format(fecha), null, null,  OperadorSala.find.all(), losDatos.toString()    ));

		
	}	
	


	
	
	
	
	public static Result ajaxEliminaEventoAdmin(){
System.out.println("   ...........................   desde AdministracionController.ajaxEliminaEventoAdmin ");		
 		boolean eliminado = false;
		JsonNode json = request().body().asJson();
System.out.println(json);		
System.out.println("id:"+json.findPath("id").asText());		
System.out.println("tipo:"+json.findPath("tipo").asText());
		
	    try {
			if (json.findPath("tipo").asText().compareTo("preagenda") == 0){
System.out.println("preagenda.....");				
				PreAgenda o = PreAgenda.find.byId(json.findPath("id").asLong());
				o.delete();
				eliminado = (PreAgenda.find.byId(json.findPath("id").asLong())==null);
			}
			if (json.findPath("tipo").asText().compareTo("agenda")==0){
System.out.println("agenda.....");				
				Agenda.find.byId(json.findPath("id").asLong()).delete();
				eliminado = (Agenda.find.byId(json.findPath("id").asLong())==null);
			}			
		} catch ( Exception e){
			System.out.println("Error!!!!!!!!!!!!!   "+e);
			//e.printStackTrace();
			System.out.println("Error!!!!!!!!!!!!!  regresando "+  "{\"eliminado\":"+eliminado+"}"  );
		} finally {
			System.out.println("Eli9mn9do: "+eliminado);
				
		}
	    return ok( "{\"eliminado\":"+eliminado+"}" );
	    
		
	}	
	
	
	/*
	public static Result ajaxActualizaEventoAdmin(){
System.out.println("   ...........................   desde ajaxActualizaEventoAdmin ");		
		Agenda obj;
		JsonNode json = request().body().asJson();
		
System.out.println(json);

		Agenda o = new Agenda();

		if (   json.findPath("tipo").asText().compareTo("agenda") == 0    
				||  json.findPath("estado").findPath("id").asLong() == 5   
				||  json.findPath("estado").findPath("id").asLong() == 7   ){
			
			o = Agenda.find.byId( json.findPath("id").asLong()   );
		
			//Quitar nodo 'tipo'
			for (JsonNode personNode : json) {
			    if (personNode instanceof ObjectNode) {
			        ObjectNode object = (ObjectNode) personNode;
			        object.remove("tipo");
			    }
			}



			  try {

				ObjectMapper mapper = new ObjectMapper();				
				obj = mapper.readValue(json.findPath("evento").traverse(), Agenda.class);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


				o.inicio = sdf.parse(json.findPath("inicio").asText()); 						
				o.fin = sdf.parse(json.findPath("fin").asText());
				
				
				// Este loop solo muestra en consola los nodos de json
				Object someObject = obj;
				for (Field field : someObject.getClass().getDeclaredFields()) {
				    field.setAccessible(true); // You might want to set modifier to public first.
				    Object value = null;
					try {
						value = field.get(someObject);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					} 
				    if (value != null) {
				        System.out.println(field.getName() + "=" + value);
				    }
				}				
				



				if (obj.salidas != null){
					o.salidas.get(0).agenda =  obj;
				}
				if (obj.vehiculos != null){
					o.vehiculos.get(0).agenda =  obj;
				}
				if (obj.locaciones.get(0)!= null){
					o.locaciones.get(0).agenda =  obj;
				}
				if (obj.cuentaRol != null){
					System.out.println("   evento con personal");
					for( AgendaCuentaRol tp :  obj.cuentaRol){
					//	System.out.println("       personal :"+tp.personalRol.id );
						tp.agenda =  obj;
						o.cuentaRol.add(tp);
					}
					
				}

				
				System.out.println("   ........................... 00 folioproductorasignado "+o.folioproductorasignado);
				System.out.println("   ........................... 01 fase "+o.fase.id);
				System.out.println("   ........................... 02 inicio "+o.inicio);
				System.out.println("   ........................... 03 inicio "+o.fin);
				System.out.println("   ........................... 04 estado "+o.estado.id);
				

				
				
				
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++antes+++++++++++++++++++++");
				o.update();
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++despues+++++++++++++++++++");				
				
				

				
				
			} catch (JsonParseException | JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} catch ( Exception e){
				System.out.println("Error!!!!!!!!!!!!!   "+e.getMessage());
				e.printStackTrace();
			}

			  
			
			
		}
		   //   return ok( "{\"agregado\":"+(obj.id != null)+"}" );
			return ok( "{\"actualizado\":"+(o.id != null)+",\"id\":"+o.id+"}" );
		    
		  
		
	}
	*/
	
	public static Result tableroEA(){
		System.out.println("desde AdminEAController.tableroEA");
		return ok( views.html.usuario.tableroEA.render());
	}

	public static Result ajaxTableroEA() throws JSONException{
		System.out.println("Desde AdminEAController.ajaxTableroEA");
		JSONObject retorno = new JSONObject();
		JSONArray arrEquipo = new JSONArray();
		JSONArray arrAccesorios = new JSONArray();
		List<EstadoEquipoAccesorioVehiculo> estados = EstadoEquipoAccesorioVehiculo.find.all();
		retorno.put("estado", "ok");
		JSONArray jaEstados = new JSONArray();
		JSONArray jaEstados2 = new JSONArray();
		for ( EstadoEquipoAccesorioVehiculo estado:estados ) {
			JSONObject jo = new JSONObject();
			JSONObject jo2 = new JSONObject();

			jo.put("id", estado.id);
			jo.put("descripcion", estado.descripcion);
			jo.put("cantidad", 0);
			
			jo2.put("id", estado.id);
			jo2.put("descripcion", estado.descripcion);
			jo2.put("cantidad", 0);
			
			jaEstados.put(jo);
			jaEstados2.put(jo2);
		}
		
		
		
		for (Equipo equipo : Equipo.find.all()) {
			JSONObject jo = new JSONObject();
			try {
			jo.put("id", equipo.id);
			jo.put("descripcion", equipo.descripcion);
			jo.put("estadoId", equipo.estado.id);
			jo.put("estadoDescripcion", equipo.estado.descripcion);
			jo.put("marca", equipo.marca);
			jo.put("modelo", equipo.modelo);
			jo.put("observacion", equipo.observacion);
			jo.put("serie", equipo.serie);
			arrEquipo.put(jo);
			for (int i = 0; i < jaEstados.length(); i++) {
				JSONObject joEstado = jaEstados.getJSONObject(i);
				if ( joEstado.get("id")==equipo.estado.id) {
					joEstado.put("cantidad",  Integer.parseInt(joEstado.get("cantidad").toString())+1);
				}
			}
			
			}catch(JSONException je) {
				System.out.println("error json");
				retorno.put("estado", "error en json");
			}
		}	
		
		
		for (Accesorio accesorio : Accesorio.find.all()) {
			JSONObject jo = new JSONObject();
			try {
			jo.put("id", accesorio.id);
			jo.put("descripcion", accesorio.descripcion);
			jo.put("estadoId", accesorio.estado.id);
			jo.put("estadoDescripcion", accesorio.estado.descripcion);
			jo.put("modelo", accesorio.modelo);
			jo.put("observacion", accesorio.observacion);
			jo.put("serie", accesorio.serie);
			arrAccesorios.put(jo);
			
			for (int i = 0; i < jaEstados2.length(); i++) {
				JSONObject joEstado2 = jaEstados2.getJSONObject(i);
				if ( joEstado2.get("id")==accesorio.estado.id) {
					joEstado2.put("cantidad",  Integer.parseInt(joEstado2.get("cantidad").toString())+1);
				}
			}			
		
			}catch(JSONException je) {
				System.out.println("error json");
				retorno.put("estado", "error en json");
			}
		}			

		retorno.put("equipo", arrEquipo);
		retorno.put("accesorios", arrAccesorios);
		retorno.put("equipoEdos", jaEstados);
		retorno.put("accesoriosEdos", jaEstados2);
		System.out.println(retorno);
		return ok(  retorno.toString()   );
	}		

	
	public static Result misServicios(){
		return ok (views.html.usuario.misServiciosAdminEA.render(0L,0L));
	}
	
	public static Result misServiciosDTSS(){
		System.out.println("\n\n\n>>  >> >>  Desde IngenieriaController.misServiciosDTSS............");
		System.out.println( "parametros "+ request() );		
		JSONObject json2 = new JSONObject();		
		int filtrados = 0;
		int sinFiltro = 0;
		Map<Integer, Integer> columnasOrdenables = new HashMap<Integer, Integer>();
		columnasOrdenables.put(0, 1);
		columnasOrdenables.put(1, 19);
		columnasOrdenables.put(2, 9);
		System.out.println( columnasOrdenables.get(0)  );		
		System.out.println( columnasOrdenables.get(1)  );
		//Page<Folio> otro = null;

		
		String estado = request().getQueryString("estado");
		
		String filtro = request().getQueryString("search[value]");
		Integer colOrden =   Integer.parseInt( request().getQueryString("order[0][column]")   );
		String tipoOrden = request().getQueryString("order[0][dir]");
		System.out.println( "parametros order[0][column]:"+ colOrden);
		System.out.println( "parametros order[0][dir]:"+ tipoOrden);		
		System.out.println( "estado :"+ estado);	
		System.out.println( "filtrando :"+ filtro);
		int numPag = 0;
		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));		
		int numRegistros = Integer.parseInt(request().getQueryString("length"));
		
		System.out.println("**************************************************************************************"       );

		Page<Oficio> pagOficio = null;		 
		 Page<Folio> serv = null;
		 
		 if (estado.compareTo("*")==0){
			 Query<Folio> q1 = Folio.find.where("productoresAsignados.personal.id = "+session("usuario"));
			 Query<Folio> q2 = Folio.find.where("productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%");
			 
			 serv = Folio.find
					 .fetch("oficio")
					 .fetch("oficio.urremitente")
					 .fetch("productoresAsignados")
					 .fetch("productoresAsignados.preagendas")
					 .fetch("productoresAsignados.preagendas.estado")		
					 .fetch("productoresAsignados.agenda")
					 .fetch("productoresAsignados.agenda.estado")
					 .fetch("estado")					 
					 .where("productoresAsignados.personal.id = "+session("usuario")+" and "
			 		+ " ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%")					 
					 	.orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden )
						.findPagingList(numRegistros)
						.setFetchAhead(false)
						.getPage(numPag);
			 
			 if (session("rolActual").compareTo("1")==0){
				 q1 = Folio.find.query();
				 q2 = Folio.find.where(
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "
						    ).setParameter("cadena", "%"+filtro+"%");				 
				 serv = Folio.find
						 .fetch("oficio")
						 .fetch("oficio.urremitente")
						 .fetch("productoresAsignados")
						 .fetch("productoresAsignados.preagendas")
						 .fetch("productoresAsignados.preagendas.estado")		
						 .fetch("productoresAsignados.agenda")
						 .fetch("productoresAsignados.agenda.estado")
						 .fetch("estado")					 
						 .where(
								"folio like :cadena "+
								"or oficio.urremitente.nombreLargo like :cadena "+
								"or oficio.descripcion like :cadena "
							    ).setParameter("cadena", "%"+filtro+"%")					 
						 	.orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden )
							.findPagingList(numRegistros)
							.setFetchAhead(false)
							.getPage(numPag);				 
			 }
			 
		 
			 
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();	
		 }		 
		 //Oficios que no han sido asignados
		 if (estado.compareTo("0")==0){
			 //SEGUIR CHECANDO  QUE PASA CON EL ARREGLO DE LOS IDS
			 System.out.println("x");
			 List<Folio> f = Folio.find.fetch("oficio").select("id").findList();
			 
			 System.out.println("f size: "+f.size());
			 
			 Long[] arrIds = new Long[f.size()];
			 
			 System.out.println("arrIds size(1): "+arrIds.length);
			 //arrIds = f.toArray(arrIds);
			 System.out.println("arrIds size(2): "+arrIds.length);
			
			
			 // Convertir List a array
			 Long[] arrLong = new Long[f.size()];
			 int ii=0;
			 for ( Folio val : f  ) {				 
				 arrLong[ii++] = val.oficio.id;
			 }
			 System.out.println("arrLong: "+arrLong.length);
			 System.out.println(Arrays.toString(arrLong));
			 String arrToString = Arrays.toString(arrLong).substring(1, Arrays.toString(arrLong).length()-1);
			 
			 Query<Oficio> q1 = Oficio.find.where("id  not in("+ arrToString +")" );
			 Query<Oficio> q2 = Oficio.find.where("id  not in("+ arrToString +")  AND ("+
						"urremitente.nombreLargo like :cadena "+
						"or descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%");
			 
			 pagOficio = Oficio.find
					 .fetch("urremitente")
					 .where("id  not in("+ arrToString +") and "
			 		+ " ("+
							"urremitente.nombreLargo like :cadena "+
							"or descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%")					 
					 	.orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden )
						.findPagingList(numRegistros)
						.setFetchAhead(false)
						.getPage(numPag);
			 
			System.out.println("pagOficio list size "+pagOficio.getTotalRowCount());
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();	
		 }			 
		 
		 if (estado.compareTo("4")==0){
			 Query<Folio> q1 = Folio.find.where("productoresAsignados.personal.id = "+session("usuario"));
			 Query<Folio> q2 = Folio.find.where("productoresAsignados.personal.id = "+session("usuario")+" and productoresAsignados.folio.estado.id = 4 "+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%");
			 
			 
			 serv = Folio.find.select("")
					 .fetch("oficio")
					 .fetch("oficio.urremitente")
					 .fetch("productoresAsignados")
					 
					 .fetch("estado")					 
					 .where("productoresAsignados.personal.id = "+session("usuario")+" and "
			 	//	+ "estado.id = 4 AND ("+
					+ "productoresAsignados.folio.estado.id = 4 AND ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%")					 
					 	.orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden )
						.findPagingList(numRegistros)
						.setFetchAhead(false)
						.getPage(numPag);
			 
			 if (session("rolActual").compareTo("1")==0){
				 System.out.println("------ rol 1 administrador");
				 q1 = Folio.find.query();
				 q2 = Folio.find.where("productoresAsignados.folio.estado.id = 4 AND ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%");
				 
				 
				 serv = Folio.find.select("")
						 .fetch("oficio")
					//	 .fetch("oficio.urremitente")
						 .fetch("productoresAsignados")
						 .fetch("productoresAsignados.preagendas")
						 .fetch("productoresAsignados.preagendas.estado")
						// .fetch("estado")
						 					 
						 .where(
								"productoresAsignados.folio.estado.id = 4 AND ("+
								"folio like :cadena "+
								"or oficio.urremitente.nombreLargo like :cadena "+
								"or oficio.descripcion like :cadena "+
							    ")").setParameter("cadena", "%"+filtro+"%")					 
						 	.orderBy( "c"+    (colOrden==0?columnasOrdenables.get(0)  :  columnasOrdenables.get(colOrden)-1)  +" "+tipoOrden )
							.findPagingList(numRegistros)
							.setFetchAhead(false)
							.getPage(numPag);				 
			 }
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();	
		 }		 
		 
		 if (estado.compareTo("5")==0){
			 Query<Folio> q1 = Folio.find.where(" (productoresAsignados.preagendas.estado.id = "+estado+") and productoresAsignados.personal.id = "+session("usuario"));
			 Query<Folio> q2 = Folio.find.where(" (productoresAsignados.preagendas.estado.id = "+estado+") and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%");
			 serv = Folio.find
					 .fetch("oficio")
					 .fetch("oficio.urremitente")
					 .fetch("productoresAsignados")
					 .fetch("productoresAsignados.preagendas")
					 .fetch("productoresAsignados.preagendas.estado")
					 .where(" (productoresAsignados.preagendas.estado.id = "+estado+") and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%")
					 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
					.findPagingList(numRegistros)
					.setFetchAhead(false)
					.getPage(numPag);	 
			 if (session("rolActual").compareTo("1")==0){
				 q1 = Folio.find.where(" (productoresAsignados.preagendas.estado.id = "+estado+") ");
				 q2 = Folio.find.where(" (productoresAsignados.preagendas.estado.id = "+estado+")  AND ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%");
				 serv = Folio.find
						 .fetch("oficio")
						 .fetch("oficio.urremitente")
						 .fetch("productoresAsignados")
						 .fetch("productoresAsignados.preagendas")
						 .fetch("productoresAsignados.preagendas.estado")
						 .where(" (productoresAsignados.preagendas.estado.id = "+estado+") and ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%")
						 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
						.findPagingList(numRegistros)
						.setFetchAhead(false)
						.getPage(numPag);				 
			 }
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();
		 }
		 if (estado.compareTo("7")==0){
			 Query<Folio> q1 = Folio.find.where(" (productoresAsignados.agenda.estado.id = "+estado+") and productoresAsignados.personal.id = "+session("usuario"));
			 Query<Folio> q2 = Folio.find.where(" (productoresAsignados.agenda.estado.id = "+estado+") and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
				"folio like :cadena "+
				"or oficio.urremitente.nombreLargo like :cadena "+
				"or oficio.descripcion like :cadena "+
			    ")").setParameter("cadena", "%"+filtro+"%");	
			 serv = Folio.find
				 .fetch("oficio")
				 .fetch("oficio.urremitente")	
				 .fetch("productoresAsignados")
				 .fetch("productoresAsignados.agenda")
				 .fetch("productoresAsignados.agenda.estado")
				 .where(" (productoresAsignados.agenda.estado.id = "+estado+") and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
					"folio like :cadena "+
					"or oficio.urremitente.nombreLargo like :cadena "+
					"or oficio.descripcion like :cadena "+
				    ")").setParameter("cadena", "%"+filtro+"%")
				 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
				.findPagingList(numRegistros)
				.setFetchAhead(false)
				.getPage(numPag);	
			 if (session("rolActual").compareTo("1")==0){
				 q1 = Folio.find.where(" (productoresAsignados.agenda.estado.id = "+estado+") ");
				 q2 = Folio.find.where(" (productoresAsignados.agenda.estado.id = "+estado+") and ("+
					"folio like :cadena "+
					"or oficio.urremitente.nombreLargo like :cadena "+
					"or oficio.descripcion like :cadena "+
				    ")").setParameter("cadena", "%"+filtro+"%");	
				 serv = Folio.find
					 .fetch("oficio")
					 .fetch("oficio.urremitente")	
					 .fetch("productoresAsignados")
					 .fetch("productoresAsignados.agenda")
					 .fetch("productoresAsignados.agenda.estado")
					 .where(" (productoresAsignados.agenda.estado.id = "+estado+") and ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%")
					 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
					.findPagingList(numRegistros)
					.setFetchAhead(false)
					.getPage(numPag);				 
			 }
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();			 
		 }
		 if (estado.compareTo("99") ==0){
			 Query<Folio> q1 = Folio.find.where(" (productoresAsignados.agenda.estado.id > 7) and productoresAsignados.personal.id = "+session("usuario"));
			 Query<Folio> q2 = Folio.find.where(" (productoresAsignados.agenda.estado.id > 7) and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%");	
			 serv = Folio.find
					 .fetch("oficio")
					 .fetch("oficio.urremitente")	
					 .fetch("productoresAsignados")
					 .fetch("productoresAsignados.agenda")
					 .fetch("productoresAsignados.agenda.estado")
					 .where(" ((productoresAsignados.agenda.estado.id > 7) or  (productoresAsignados.folio.estado.id = 99)   ) and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%")
					 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
					.findPagingList(numRegistros)
					.setFetchAhead(false)
					.getPage(numPag);	
			 if (session("rolActual").compareTo("1")==0){
				 q1 = Folio.find.where(" (productoresAsignados.agenda.estado.id > 7) ");
				 q2 = Folio.find.where(" (productoresAsignados.agenda.estado.id > 7)  AND ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%");	
				 serv = Folio.find
						 .fetch("oficio")
						 .fetch("oficio.urremitente")	
						 .fetch("productoresAsignados")
						 .fetch("productoresAsignados.agenda")
						 .fetch("productoresAsignados.agenda.estado")
						 .where(" ((productoresAsignados.agenda.estado.id > 7) or  (productoresAsignados.folio.estado.id = 99)   ) and ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%")
						 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
						.findPagingList(numRegistros)
						.setFetchAhead(false)
						.getPage(numPag);				 
			 }
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();			 
		 }				
		 if (estado.compareTo("100") ==0){
			 Query<Folio> q1 = Folio.find.where(" productoresAsignados.personal.id = "+session("usuario"));
			 Query<Folio> q2 = Folio.find.where(" productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%");	
			 serv = Folio.find
					 .fetch("oficio")
					 .fetch("oficio.urremitente")	
					 .fetch("productoresAsignados")
					 .fetch("productoresAsignados.agenda")
					 .fetch("productoresAsignados.agenda.estado")
					 .where(" (  (productoresAsignados.preagendas.estado.id = 100) or  (productoresAsignados.agenda.estado.id = 100) or  (productoresAsignados.folio.estado.id = 100)   ) and productoresAsignados.personal.id = "+session("usuario")+" AND ("+
						"folio like :cadena "+
						"or oficio.urremitente.nombreLargo like :cadena "+
						"or oficio.descripcion like :cadena "+
					    ")").setParameter("cadena", "%"+filtro+"%")
					 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
					.findPagingList(numRegistros)
					.setFetchAhead(false)
					.getPage(numPag);	
			 if (session("rolActual").compareTo("1")==0){
				 q1 = Folio.find.where(" (productoresAsignados.preagendas.estado.id =100) or (productoresAsignados.agenda.estado.id = 100) ");
				 q2 = Folio.find.where(" ((productoresAsignados.preagendas.estado.id =100) or (productoresAsignados.agenda.estado.id = 100))  AND ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%");	
				 serv = Folio.find
						 .fetch("oficio")
						 .fetch("oficio.urremitente")	
						 .fetch("productoresAsignados")
						 .fetch("productoresAsignados.agenda")
						 .fetch("productoresAsignados.agenda.estado")
						 .where(" (  ((productoresAsignados.preagendas.estado.id =100) or  (productoresAsignados.agenda.estado.id =100)) or  (productoresAsignados.folio.estado.id = 100)   ) and ("+
							"folio like :cadena "+
							"or oficio.urremitente.nombreLargo like :cadena "+
							"or oficio.descripcion like :cadena "+
						    ")").setParameter("cadena", "%"+filtro+"%")
						 .orderBy( "c"+columnasOrdenables.get(colOrden)+" "+tipoOrden )
						.findPagingList(numRegistros)
						.setFetchAhead(false)
						.getPage(numPag);				 
			 }
			filtrados = q2.findList().size();
			sinFiltro = q1.findList().size();			 
		 }	               

		 

		
		System.out.println("**************************************************************************************"       );		


		
		try {
			json2.put("draw",  new Date().getTime()  );
			json2.put("recordsTotal",  sinFiltro );
//			json2.put("recordsFiltered", ppre3.getTotalRowCount());
			json2.put("recordsFiltered", filtrados);

			JSONArray losDatos = new JSONArray();
			// Oficios sin asignar
			if (estado.compareTo("0")==0) {
				for( Oficio p : pagOficio.getList()  ){
					Set<String> losEstados = new HashSet<String>();
					JSONObject datoP = new JSONObject();
					datoP.put("id", p.id);
					datoP.put("folio", "No se le ha asignado folio");
					datoP.put("oficioId", "ninguno");
					datoP.put("ur", p.urremitente.nombreLargo);
					
					losEstados.add("Por Asignar");
					estado="2";
					
					
					datoP.put("estado", (estado.compareTo("*")==0)?losEstados.toString():Estado.find.where().idEq(estado).findUnique().descripcion);
					
					datoP.put("estadoId", (estado.compareTo("*")==0)?"*":estado);
					datoP.put("descripcion", p.descripcion);				
					losDatos.put(datoP);
				}
				if ( pagOficio.getTotalRowCount()>0 ){
					json2.put("data", losDatos);
				} else {
					json2.put("data", new JSONArray() );
					return ok( json2.toString()  );
				}	
			} else {
				for( Folio p : serv.getList()  ){
					Set<String> losEstados = new HashSet<String>();
					JSONObject datoP = new JSONObject();
					datoP.put("id", p.id);
					datoP.put("folio", p.numfolio);
					datoP.put("folioId", p.id);
					datoP.put("ur", p.oficio.urremitente.nombreLargo);
					
					for ( FolioProductorAsignado prod : p.productoresAsignados){
						//System.out.println("losEstados "+ prod.folio.folio +":"+losEstados.size());
						if (prod.preagendas.isEmpty() && prod.agenda.isEmpty()){
							if(prod.folio.estado.id == 100)							
								losEstados.add("Cancelado");
							if(prod.folio.estado.id == 99)							
								losEstados.add("Cerrado");
							if(prod.folio.estado.id == 4)							
								losEstados.add("Por atender");					
						}else{
							for (PreAgenda pre :  prod.preagendas ){
								if (pre.estado.id==4)
									losEstados.add( "Por atender" );
								if (pre.estado.id==5)
									losEstados.add( "Solicitudes" );
								if (pre.estado.id==7)
									losEstados.add( "Autorizados" );
								if (pre.estado.id>7)
									losEstados.add( "Terminados" );
								if (pre.estado.id==100)
									losEstados.add( "cancelados" );	
							}
							for (Agenda age :  prod.agenda ){
								if (age.estado.id==4)
									losEstados.add( "Por atender" );
								if (age.estado.id==5)
									losEstados.add( "Solicitudes" );
								if (age.estado.id==7)
									losEstados.add( "Autorizados" );
								if (age.estado.id==8)
									losEstados.add( "E/A Asignados" );
								if (age.estado.id>7 && age.estado.id<100)
									losEstados.add( "Terminados" );
								if (age.estado.id==100)
									losEstados.add( "Cancelados" );								
							}						
						
						}
					}
					
					
					datoP.put("estado", (estado.compareTo("*")==0)?losEstados.toString():Estado.find.where().idEq(estado).findUnique().descripcion);
					
					datoP.put("estadoId", (estado.compareTo("*")==0)?"*":estado);
					datoP.put("descripcion", p.oficio.descripcion);				
					losDatos.put(datoP);
				}
				if ( serv.getTotalRowCount()>0 ){
					json2.put("data", losDatos);
				} else {
					json2.put("data", new JSONArray() );
					return ok( json2.toString()  );
				}					
			}
		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("retorno "+json2.toString()+"\n\n\n");
		return ok( json2.toString()  );
	}

	// Recibe_JSON  folioproductorasignado.id,  evento(Agenda.class),
	public static Result asignaEquipoAccesorios(){
		//Ebean.beginTransaction();
		Transaction transaction = Ebean.beginTransaction();
		Agenda nvo = new Agenda();
		try {
			System.out.println("\n\n\n\n\nDesde AdminEAController.asignaEquipoAccesorios");
			Agenda obj;
			//Agenda age = new Agenda();
			JsonNode json = request().body().asJson();
			System.out.println(json);
			FolioProductorAsignado fpa = FolioProductorAsignado.find
					.byId(json.findPath("folioproductorasignado")
							.findPath("id").asLong());
			//json.findPath("evento").fieldNames().forEachRemaining(h -> System.out.println("? " + h));

			// Quita el nodo personalIds
			ObjectNode objectNode = (ObjectNode) json;
			objectNode.remove("personalIds");
			JsonNode updatedJsonNode = Json.parse(objectNode.toString());
			json = updatedJsonNode;

			ObjectMapper mapper = new ObjectMapper();
			obj = mapper.readValue(json.traverse(), Agenda.class);
			System.out.println("id: " + obj.id + " edo: " + obj.estado.id);
			obj.estado = Estado.find.byId(8L);
			for(  AgendaEquipo eqo : obj.equipos ){
					eqo.autorizo = Personal.find.byId(  Long.parseLong(session("usuario")) );
			}
			nvo = Agenda.find.byId( obj.id );
			nvo.estado = Estado.find.byId(8L);
			nvo.equipos.forEach(AgendaEquipo::delete);
			nvo.accesorios.forEach(AgendaAccesorio::delete);
			for ( AgendaEquipo e: obj.equipos){
				nvo.equipos.add(e);
			}
			for ( AgendaAccesorio a: obj.accesorios){
				nvo.accesorios.add(a);
			}

			Ebean.update(nvo);
			//Ebean.commitTransaction();
			transaction.commit();
		} catch (Exception e) {
			System.out.println(ColorConsola.ANSI_RED+"Error!!!!!!!!!!!!!   " + e);
			e.printStackTrace();
			//Ebean.rollbackTransaction();
			transaction.rollback();
			System.out.println("se aplicó rollback"+ColorConsola.ANSI_RESET);
			nvo.id = null;
		} finally {
			//Ebean.endTransaction();
			transaction.end();
		}
		System.out.println("id generado en agenda despues de asignar:" + nvo.id);
		return ok("{\"asignado\":" + (nvo.id != null) + ",\"id\":" + nvo.id + "}");
	}



}
