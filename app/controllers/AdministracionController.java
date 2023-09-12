package controllers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.avaje.ebean.Expr;
import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import actions.miCorreo2;
import models.Agenda;
import play.libs.Json;
import play.mvc.Result;
import views.html.usuario.agenda;
import play.db.ebean.Model;
public class AdministracionController extends ControladorSeguroAdministrador{
	
	public static Result agenda(Long idPorRevisar) throws JSONException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
System.out.println("desde AdministracionController.agenda");		
		List<Folio> f = Folio.find.where().in("estado.id",  Arrays.asList(4, 5, 7) ).findList();
		
		Date fecha =  PreAgenda.find.byId(idPorRevisar).inicio;
System.out.println("id preagenda:"+idPorRevisar);
System.out.println("inicio:"+sdf.format(fecha));


JSONArray losDatos = new JSONArray();



SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd mm:ss:ms");


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



			return ok(agenda.render(f,  sdf.format(fecha), null, null,  OperadorSala.find.all(), losDatos.toString()    ));

		
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
					for( AgendaCuentaRol tp :  obj.cuentaRol) {
						//System.out.println("       personal :"+tp.personal.id );
						
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
				  System.out.println("Error!!!!!!!!!!!!!   "+e.getMessage());
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
	
	public static Result tablero(){
		 Folio.find.where().in("estado.id",  Arrays.asList(4, 5, 7) ).findList();
		return ok( views.html.usuario.tableroAdmin.render());
	}	
	
	
	public static Result ajaxTablero(){
		System.out.println("Desde AdministracionController.ajaxTablero");
		List<Agenda> fechasCaducadas = new ArrayList<Agenda>();
		List<PreAgenda> fechasCaducadasPreAgenda = new ArrayList<PreAgenda>();
		TreeSet<String> eventos = new TreeSet<String>(); 
		String jsonString;

		 int oficioAll = Oficio.find.findRowCount();
		 //int asignadas = Folio.find.where().eq("estado.id", 4).findRowCount();
		 List<Folio> soloAsignados = Folio.find.where().eq("estado.id","4").findList();
		 /*
		 System.out.println("\nsoloAsignados (Folio edo 4)"+
				 soloAsignados.stream()
         .map( x-> x.id).collect(Collectors.toList()));
		 */
		 
		 List<Folio> solicitudes = Folio.find.where().eq("productoresAsignados.preagendas.estado.id","5").setOrderBy("id").findList();
		 /*System.out.println("\nsolicitudes (PreAgenda edo 5)    "+
		 solicitudes.stream()
                 .map( x-> x.id).collect(Collectors.toList()));
		 */
		 
		 List<Folio> autorizados = Folio.find.where()
				 //.eq("productoresAsignados.agenda.estado.id",7)
				 .and(Expr.gt("productoresAsignados.agenda.estado.id",6), Expr.lt("productoresAsignados.agenda.estado.id", 10))
				 .setOrderBy("id")
				 .findList();
		 /*
		 System.out.println("\nautorizados (Agenda edo 7)   "+
		 autorizados.stream()
                 .map( x-> x.id).collect(Collectors.toList()));
                 		 
		 */
		 
		 List<Folio> terminados = Folio.find.where().gt("productoresAsignados.agenda.estado.id", 10).setOrderBy("id").findList();
		 /*
		 System.out.println("\nterminados (Agenda edo > 7)   "+
		 terminados.stream()
                 .map( x-> x.id).collect(Collectors.toList()));
		 */
		 
		 int todos = 0;
		 int todosTerminados = 0;
		 int contadorTerminados = 0;
		 int contadorForzados = 0;
		 ArrayList<Long> arrForzados = new ArrayList<>();
		 
		 
		 for (Folio f : Folio.find.where().ge("estado.id", 7).findList()){
			 //System.out.print("Folio: "+f.folio+" "+f.oficio.descripcion);
			 for (FolioProductorAsignado fpa : f.productoresAsignados){
				 todos = fpa.agenda.size();
				 todosTerminados = (int) fpa.agenda.stream().filter(p -> p.estado.id > 7 || p.estado.id == 99).count();
			 }
			 
			// System.out.print("todos: "+todos+"  todosTerminados: "+todosTerminados+"  ");
			 
			 if ( (todos == todosTerminados) ){
				 if (todos!=0){
				//	 System.out.print("folio id:"+f.id +"  CONCLUIDO   "  +f.oficio.descripcion+  "\n");
					 contadorTerminados++;
				 } else {
					 contadorForzados++;
					 arrForzados.add(f.numfolio);
				//	 System.out.print("folio id:"+f.id +"  FORZADO   "  +f.oficio.descripcion+  "\n");
				 }
			 } else {
				// System.out.print("folio id:"+f.id +"  FALTAN "+(todos - todosTerminados)+"      "+f.oficio.descripcion+"\n");
			 }
			 
			 
		 }
		 
		 System.out.println("TERMINADOS "+contadorTerminados);
		 System.out.println("FORZADOS "+contadorForzados);
		 System.out.println("FORZADOS "+arrForzados.size());
		 
		 
		
		// Folio.find.where().in("folio", arrForzados).findList();
		 
		 
		 /*
		 if (arrForzados.size()>0)
		 for (int cv = 0; cv < arrForzados.size(); cv++){
			 Folio.find.where().eq("folio", arrForzados.get(cv)).findUnique().delete();
		 }*/
		 
		 // Obtener los eventos programados y autorizados a partir de la fecha de hoy
		 
			for ( Folio fol:  Folio.find
					.fetch("productoresAsignados.agenda.fase")
					.fetch("productoresAsignados.folio")
					.fetch("productoresAsignados.folio.oficio")
					.fetch("productoresAsignados.agenda")
					////////.where().eq("productoresAsignados.personal.id", session("usuario"))
					.orderBy("productoresAsignados.agenda.inicio")
					.findList()  ){
				for ( FolioProductorAsignado pa : fol.productoresAsignados){
						for ( Agenda ags : pa.agenda ){
							System.out.println("    "+ags.fin+"     "+new Date());
							if ( ags.estado.id ==7 || ags.estado.id ==8){
								java.util.Date date = ags.inicio;
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								String format = formatter.format(date);
								if(ags.fin.after( new Date() )  )
									eventos.add(format);
								// fechas caducadas en agenda
								if(ags.fin.before( new Date() )  ) {
									fechasCaducadas.add(ags);
									System.out.println("    ya pasó en agenda");
								}
							}
						}

						// Calcular fechas caducadas en preagenda

						for ( PreAgenda pags : pa.preagendas ){
							if ( pags.estado.id ==4 || pags.estado.id ==5){
								java.util.Date date = pags.inicio;
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								String format = formatter.format(date);
								if(pags.fin.before( new Date() )  ) {
									fechasCaducadasPreAgenda.add(pags);
									System.out.println("    ya pasó en preagenda");
								}
							}
						}
						Collections.sort(fechasCaducadas);
				}
			}

			System.out.println("eventos size "+eventos.size());
			List<String> listaEventos = new ArrayList<> (eventos);
			String eventosProximos = String.join("\", \"", listaEventos);

			System.out.println("eventosProximos>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+eventosProximos);
			
			JsonContext jsonContext = Ebean.createJsonContext();
			//Personal
			List<Personal> persona = Personal.find										
					.fetch("horarios")
					.fetch("cuentas")
					.fetch("cuentas.roles")
					.fetch("cuentas.roles.rol")
					.fetch("cuentas.roles.rol.derechos")
					.where().eq("activo", "S")
					.order("paterno, materno, nombre, horarios.diasemana")
					.findList();
			jsonString = jsonContext.toJsonString(persona);
		//System.out.println(jsonString);
		return ok( "{\"oficioAll\":"+oficioAll
				+", \"soloAsignados\":"+soloAsignados.size()
				+", \"solicitudes\":"+solicitudes.size()
				+", \"autorizados\":"+autorizados.size()
				+", \"terminados\":"+terminados.size()
				+", \"fechasCaducadas\":"+ jsonContext.toJsonString(fechasCaducadas)
				+", \"fechasCaducadasPreAgenda\":"+ jsonContext.toJsonString(fechasCaducadasPreAgenda)
				+", \"eventosProximos\":"+"[\""+eventosProximos +"\"]"
				+", \"personal\":"+jsonString +"}"    );
		
	}
	

	public static Result ajaxSolicitaPerfiles(){
		boolean retorno = true;
		System.out.println("   ...........................   desde AdministracionController.ajaxSolicitaPerfiles ");
		JsonNode json = request().body().asJson();
		System.out.println(json);
		if (json.findPath("estadoId").asLong() == 5){		
			PreAgenda solP = PreAgenda.find.byId(json.findPath("fpaId").asLong());
			retorno = (   (solP.personal.size()>0) ||  (solP.vehiculos.size()>0)     );
		}
		
		System.out.println(   "{\"conSolicitud\": "+retorno+"}"      );
		return ok( "{\"conSolicitud\": "+retorno+"}"   );	
	}
	
	

	public static Result colaCorreosEnviar() {
		System.out.println("desde AdministracionController.colaCorreosEnviar");
		ColaCorreos.find.all().forEach(Model::delete);
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/YYYY");
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		
		Date hoy = new Date();
		  System.out.println("000");
		// Todos los preagenda del 2019 en estado 5 
		  System.out.println("100");
		List<PreAgenda> colaPreagenda = PreAgenda
				.find.setDistinct(true)
				.select("id")
				.where().eq("estado.id", 5L)
				//.gt("inicio", "2019-01-01")
				.findList();
		System.out.println("200");
		System.out.println("colaPreagenda tam: "+colaPreagenda.size());

		
		
		
		
		
		
		
		///////////////   VERSION COn GROUP_CONCAT
		String sql = "select distinct folio.numfolio, oficio.descripcion as oficioDescripcion"+
				", servicio.descripcion as servicioDescripcion "+
				", CONCAT(personal.nombre,' ', personal.paterno,' ' ,personal.materno )as productor, "+

				//", group_concat(pas.id) as ids "+
				//", group_concat(distinct personalcorreo.email) as correos "+
					"( "+
						"SELECT STRING_AGG(pas.id::text, ',') "+
						"FROM pre_agenda pas "+
						"INNER JOIN folio_productor_asignado fpa ON pas.folioproductorasignado_id = fpa.id "+
						"WHERE fpa.folio_id = folio.id "+
					") as ids, "+
					"	( "+
					"			SELECT STRING_AGG(DISTINCT personalcorreo.email, ',') "+
					"	FROM pre_agenda pas "+
					"	INNER JOIN folio_productor_asignado fpa ON pas.folioproductorasignado_id = fpa.id "+
					"	INNER JOIN personal_correo personalcorreo ON fpa.personal_id = personalcorreo.personal_id "+
					"	WHERE fpa.folio_id = folio.id "+
					") as correos "+
				"from pre_agenda pas "+
				"INNER join folio_productor_asignado fpa on pas.folioproductorasignado_id = fpa.id "+ 
				"INNER join folio folio on fpa.folio_id = folio.id "+ 
				"INNER join oficio oficio on folio.oficio_id = oficio.id "+ 
				"INNER join oficio_servicio_solicitado oficioSol on oficio.id = oficioSol.oficio_id "+
				"inner join servicio servicio on oficioSol.servicio_id = servicio.id "+
				"inner join fase fase on pas.fase_id = fase.id "+
			 	"inner join personal personal on fpa.personal_id = personal.id "+ 
			 	"inner join personal_correo personalcorreo on personal.id = personalcorreo.personal_id "+
				"where    "+
				"pas.estado_id= 5 "+
				"and inicio > '2019-01-01' "+
				//"group by folio.folio, servicio.descripcion, CONCAT(personal.nombre,\" \", personal.paterno,\" \",personal.materno )";
				"group by folio.id, folio.numfolio, oficio.descripcion, servicio.descripcion, CONCAT(personal.nombre,' ', personal.paterno,' ',personal.materno )";


		//AQUI ME QUEDÉ

		System.out.println(sql);
		
		List<SqlRow> sqlRows = 
			    Ebean.createSqlQuery(sql)			        
			        .findList();
		

		System.out.println("sqlRows tam: "+sqlRows.size());
		
		
		
		
		for( SqlRow sqlrow : sqlRows ) {
			String[] correos =  sqlrow.getString("correos").split(",");
			String[] stringIds = sqlrow.getString("ids").split(",");
			List<String> ids = Arrays.asList( stringIds );
			
			// Por cada correo
			for( String correo : correos  ) {
				ColaCorreos cc = new ColaCorreos();
				cc.correo = correo;		
				cc.asunto="Solicitudes del productor "+sqlrow.getString("productor");
				cc.folio = sqlrow.getString("folio");
				cc.oficioDescripcion = sqlrow.getString("oficioDescripcion");
				cc.servicioDescripcion = sqlrow.getString("servicioDescripcion");
				String contenido = "El presente es un reporte automático de los movimientos realizados el <strong>día de hoy, solicitudes y autorizaciones</strong> del Departamento de Televisión Educativa con corte al <strong>"+sdfFecha.format(hoy)+" a las "+sdfHora.format(hoy)+"</strong><br><br> ";
				contenido+="<h3>Folio: "+ cc.folio+"</h3>";
				contenido+="<h3>Descripción: "+cc.oficioDescripcion+"</h3>";
				contenido+="<h3>Servicio solicitado: "+cc.servicioDescripcion+"</h3>";				
				String eventos="<table border='1px' style='width:100%; border-collapse:collapse;  '>"; 
				eventos+="<tbody><tr><th>Fecha</th><th>Horario</th><th>Detalle</th></tr>";
				int i=1;				
				
				List<PreAgenda> detalle = PreAgenda.find
						.fetch("salidas")
						.fetch("accesorios")
						.fetch("accesorios.accesorio")
						.fetch("equipos")
						.fetch("equipos.equipo")
						.fetch("vehiculos")
						.fetch("salas")
						.fetch("salas.sala")
						.fetch("salas.usoscabina")
						.where().idIn(ids).orderBy("inicio").findList();

				StringBuilder eventosBuilder = new StringBuilder(eventos);
				for(PreAgenda pre : detalle ) {
					String colorRenglon="lightgrey";
					if ( (i++%2)==0  )
						colorRenglon="white";
					eventosBuilder.append("<tr bgcolor=\"").append(colorRenglon).append("\">");
					eventosBuilder.append("<td style='padding: 5px; width:90px'>").append(sdfFecha.format(pre.inicio)).append("</td>");
					eventosBuilder.append("<td style='padding: 5px; width:130px'>");
						for (  PreAgendaSalida salida : pre.salidas) {
							eventosBuilder.append("<p><strong>Salida </strong>").append(sdfHora.format(salida.salida)).append("</strong></p>");
						}					
						
						for (  PreAgendaVehiculo v : pre.vehiculos) {
							eventosBuilder.append("<p><strong>Solicitó vehículo</strong></p>");
						}						
						
						eventosBuilder.append("<p><strong>Desde </strong>").append(sdfHora.format(pre.inicio)).append("</p><p><strong>Hasta </strong>").append(sdfHora.format(pre.fin)).append("</p>");
						eventosBuilder.append("</td>");
						eventosBuilder.append("<td style=''>");
						eventosBuilder.append("<p><strong>").append(pre.fase.descripcion);
						if (pre.juntas.size()>0)
							eventosBuilder.append(" - Junta de trabajo");
						if (pre.locaciones.size()>0)
							eventosBuilder.append(" - Scouting");
						eventosBuilder.append("</strong></p>");
						
						if (pre.locaciones.size()>0) {
							for ( PreAgendaLocacion  locacion : pre.locaciones) {
								eventosBuilder.append("<p><strong>Locación </strong>").append(locacion.locacion).append("<br>");
							}
							eventosBuilder.append("</p>");
						}
						
						if (!pre.locutores.isEmpty()) {
							eventosBuilder.append("<p><strong>Locutores</strong><br>");
							for ( PreAgendaLocutor loc : pre.locutores ) {
								eventosBuilder.append(loc.personal.nombreCompleto()).append("<br>");
							}
							eventosBuilder.append("</p>");
						}
						
						if (!pre.equipos.isEmpty()) {
							eventosBuilder.append("<p><strong>Equipo y accesorios</strong><br>");
							eventosBuilder.append("<ul>");
							for (PreAgendaEquipo equipo :  pre.equipos) {
								eventosBuilder.append("<li>").append(equipo.equipo.descripcion).append(" ").append(equipo.equipo.marca).append(" ").append(equipo.equipo.modelo).append(" ").append(equipo.equipo.serie).append(" ").append(equipo.equipo.observacion).append("</li>");
							}
							if (!pre.accesorios.isEmpty() ) {
								for (PreAgendaAccesorio acc :  pre.accesorios) {
									eventosBuilder.append("<li>").append(acc.accesorio.descripcion).append(" ").append(acc.accesorio.modelo).append(" ").append(acc.accesorio.serie).append(" ").append(acc.accesorio.observacion).append("</li>");
								}
							}
							eventosBuilder.append("</ul></p>");
						}

						
						if (!pre.salas.isEmpty()) {
							eventosBuilder.append("<p><strong>");
							for (PreAgendaSala sala :  pre.salas) {
								eventosBuilder.append(sala.sala.descripcion);
								
								for (PreAgendaSalaUsoCabina uso :  sala.usoscabina) {
									String aux="";
									switch (uso.usocabina) {
									case "M":
										aux="Musicalización";
										break;
									case "V":
										aux="Voz en off";
										break;
									case "I":
										aux="Ingesta";
										break;										
									case "C":
										aux="Calificación";
										break;			
									case "R":
										aux="Revisión";
										break;										
									default:
										break;
									}
									eventosBuilder.append(" - ").append(aux);
								}
								eventosBuilder.append("<br>");
								//Operador de sala
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								@SuppressWarnings("unchecked")
								List<Personal> este =  (List<Personal>)(List<?>) UsuarioController.auxOperadorEnHorario(sdf.format(pre.inicio), sdf.format(pre.fin), pre.id, sala.sala.id);
								
								System.out.println("Tam este "+este.size());
								for ( Object p : este ) {
									System.out.println("sala "+sala.sala.descripcion+"   "+ Personal.find.byId( Long.parseLong(p.toString())).nombreCompleto()   );
									
									eventosBuilder.append("Operador: ").append(Personal.find.byId(Long.parseLong(p.toString())).nombreCompleto()).append("<br>");
								}
							}
							eventosBuilder.append("</p></strong>");

						}	
						

						
						
						if (!pre.personal.isEmpty()) {
							eventosBuilder.append("<p><strong>Perfiles solicitados</strong><br>");
							for ( PreAgendaRol p : pre.personal) {
								eventosBuilder.append(p.rol.descripcion).append(":  ").append(p.cantidad).append("<br>");
							}
							eventosBuilder.append("</p>");
						}
						
						if (!pre.formatos.isEmpty()) {
							eventosBuilder.append("<p><strong>Formatos para entrega</strong><br>");
							for (PreAgendaFormatoEntrega fmto: pre.formatos) {
								eventosBuilder.append(fmto.formato.descripcion).append(": ").append(fmto.cantidad).append("<br>");
							}
							eventosBuilder.append("</p>");
						}
						
						eventosBuilder.append("</td>");
					eventosBuilder.append("</tr>");
				}
				eventos = eventosBuilder.toString();
				eventos+="</tbody></table><br><br>"; 
				contenido+=eventos;
				cc.servicioDescripcion = sqlrow.getString("servicioDescripcion");
				cc.oficioDescripcion = sqlrow.getString("oficioDescripcion");
				//cc.folio = cc.folio;
				cc.contenido= contenido;
				cc.estado=1;
				cc.numintentos=0;
				cc.save();				
			}
			
		/*	**********************   CHECAR 	que pasa con 2.SQL en /conf/evolutions
			
			********************  IMPLEMENTAR LA ANOTACIÓN MappedSuperclass
			*/
			
			System.out.println(sqlrow.getString("ids"));
			System.out.println(sqlrow.getLong("folio"));
		}
		
		System.out.println("REgresando...");
		//System.out.println( "{\"agregados\":"+ColaCorreos.find.all().size()+"\"correos:\""+ jsonContext.toJsonString(ColaCorreos.find.all())   +"}"       ); 
		
		JSONArray ja = new JSONArray();

		miCorreo2 mc2 = new miCorreo2();
		

		for ( ColaCorreos laColaCorreos : ColaCorreos.find.all()  ) {
/*			int i=0;

		      new Thread("" + i++){
		          public void run(){*/

					try {
			  			JSONObject jo = new JSONObject();
						jo.put("correo", laColaCorreos.correo);
						jo.put("asunto", laColaCorreos.asunto);						
						jo.put("contenido",  laColaCorreos.contenido);
						ja.put(jo);
						
						mc2.asunto = jo.getString("asunto");
						mc2.para =  Arrays.asList(jo.getString("correo"),"eduardo.puente72@gmail.com"  );
						//mc2.para.add("eduardo.puente72@gmail.com");
						mc2.mensaje = jo.getString("contenido");
						System.out.println("antes del envio del email a:"+mc2.para);
						laColaCorreos.numintentos++;
						//mc2.enviar();
						mc2.run();
						laColaCorreos.estado=2;

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						laColaCorreos.estado=3;
						e.printStackTrace();
					}			
					laColaCorreos.update();
		    //      }
		   //     }.start();

		
			
			
		}
		
		
		//System.out.println( ja.toString() );
		//System.out.println("regresando...");
		
		
		
		// Probando método asíncrono
		

		
		return ok(ja.toString());
	}


	public static Result enviarCorreo() {
		System.out.println("Desde AdministracionController.enviarCorreo....");
		miCorreo2 correo = new miCorreo2();
		correo.para = Arrays.asList("epuente_72@yahoo.com");
		correo.asunto="Prueba";
		correo.mensaje="Esta es una prueba de correo enviada desde una aplicación ";
		
		correo.enviar();
		
		return ok(correo.mensajeoperacion);
	}
	
	
	
	public static Result enviarCorreoG() {
		String from ="eduardo.puente72@gmail.com";
		String password="ctaGmail";
		String to= "epuente_72@yahoo.com";
		String sub="sub";
		String msg="Este es el mensaje";
		
        Properties props = new Properties();    
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                  "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");
        
        //get Session   
        Session session = Session.getDefaultInstance(props,    
         new javax.mail.Authenticator() {    
         protected PasswordAuthentication getPasswordAuthentication() {    
         return new PasswordAuthentication(from,password);  
         }    
        });    
        
        //compose message    
        try {    
         MimeMessage message = new MimeMessage(session);    
         message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
         message.setSubject(sub);    
         message.setText(msg);    
         //send message  
         Transport.send(message);    
         System.out.println("message sent successfully");    
        } catch (MessagingException e) {throw new RuntimeException(e);}    
                   
		return ok("ok?");    		
	}
	
	
	// Se envía un correo de prueba desde la creación / edicion de cuentas de correo de salida
	public static Result envioCorreoPrueba() {
		System.out.println("Desde AdministracionController.envioCorreoPrueba....");
		JsonNode json = request().body().asJson();
		System.out.println(json);
		Properties properties = System.getProperties();    
		properties.setProperty("mail.smtp.host", json.findValue("host").asText()  );		
		properties.setProperty("mail.smtp.port", json.findValue("puerto").asText()); 
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");		
		properties.setProperty("mail.smtp.ssl.protocols","TLSv1.2");
		
		String asunto ="Prueba de correo de salida";
		String mensaje ="PRUEBA DE ENVÍO<br><br>Si esta leyendo este mensaje es un indicador de que la configuración que especificó es correcta para ser usada como cuenta de correo saliente para el sistema de Televisión educativa.<br><br><br>";
		mensaje+= "hostname: "+json.findValue("host").asText()+"<br>";
		mensaje += "puerto: "+json.findValue("puerto").asText()+"<br>";
		mensaje += "cuenta de salida: "+json.findValue("cuenta").asText()+"<br>";
		mensaje += "autorizacion smtp (mail.smtp.auth):true <br>";
		mensaje += "protocolos ssl (mail.smtp.ssl.protocols):TLSv1.2 <br>";
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(json.findValue("cuenta").asText(), json.findValue("password").asText());
			}
		};
		Session session = Session.getInstance(properties, auth);
		MimeMessage message = new MimeMessage(session);
		String mensajeoperacion;
		boolean enviado = false;
		try{
			message.setFrom(new InternetAddress(json.findValue("cuenta").asText()));
			for(String destino :  Arrays.asList(json.findValue("para").asText())){
				System.out.println("-------------------------------"+destino);
				System.out.println("intento   para        "+destino);
				if (destino!= null)
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));	
			}			
			message.setSubject(asunto, "UTF-8");
			
			System.out.println("Envio de correo a las "+new Date());			
            MimeBodyPart textBodyPart = new MimeBodyPart();
            //textBodyPart.setText(this.mensaje);
            textBodyPart.setContent(mensaje,"text/html; charset=utf-8");
            

            // Para el attach
            MimeBodyPart attachPart = new MimeBodyPart();            
        //    String filename ="/home/epuente/Documentos/1010 1015 5577 1743.pdf";
        //    DataSource source = new FileDataSource(filename);
        //    attachPart.setDataHandler(new DataHandler(source));
        //    attachPart.setFileName(filename);
            
            
            
            //El multipart
            Multipart multipart = new MimeMultipart();
         //   multipart.addBodyPart(attachPart);
            multipart.addBodyPart(textBodyPart);
            
            message.setContent(multipart);
            
			Transport.send(message);
			System.out.print("      Se envió correctamente a ");
			
			for (Address a : message.getAllRecipients()) {
				System.out.print(a.toString()+"  ");
			}
			
			System.out.println();
			mensajeoperacion="Se envió correctamente";
			enviado = true;
		} catch(MessagingException e){
			System.out.println("error: "+e.getMessage());	
			mensajeoperacion=e.getLocalizedMessage();
		}
		
		
		
		
		return ok(  Json.parse("{\"mensaje\" : \""+mensajeoperacion+"\", \"enviado\":"+enviado+"}")   );
	}
	
	
}
