package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.avaje.ebean.Expr;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.usuario.*;

public class ProductorServiciosController extends ControladorDefault{
	
	public static Result agenda() throws JSONException{
		System.out.println("desde ProductorServicioController.agenda");
		System.out.println(request());

		DynamicForm df = DynamicForm.form().bindFromRequest();

		System.out.println(df);
		UsuarioController.registraAcceso(request().path());
		String fecha = (df.get("fecha")!=null?df.get("fecha"):null);
		Long eventoId = (df.get("eventoId")!=null)?Long.parseLong(df.get("eventoId")):null;
		String tipo = (df.get("tipo")!=null)?df.get("tipo"):null;
		Long folio= (df.get("folio")!=null)?Long.parseLong(df.get("folio").trim()):null;
		
	
		new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		List<Folio> f = Folio.find.where().ge("estado.id",  4 )
				.where()
				.gt("fechaentrega", new Date())
				.eq("productoresAsignados.personal.id", session("usuario"))
				.findList();
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		Long folioNum=0L;
		
		if (tipo!=null &&tipo.compareTo("preagenda")==0){
			System.out.println("pre");
			folioNum = PreAgenda.find.byId(eventoId).folioproductorasignado.folio.numfolio;
		}
		if (tipo!=null && tipo.compareTo("agenda")==0 && eventoId!=null){
			System.out.println("age");
			folioNum = Agenda.find.byId(eventoId).folioproductorasignado.folio.numfolio;
		}
		if (tipo!=null && tipo.compareTo("agenda")==0 && folio!=null){
			System.out.println("age");
			folioNum = folio;
		}		
		List<OperadorSala> ops = OperadorSala.find.fetch("personal")
				.where().eq("personal.activo", "S")
				.findList();
		
		if (eventoId!=null && session("rolActual").compareTo("100")==0 && eventoId!=0 ){
			session("pinFPAId", eventoId.toString());
		}
		
		System.out.println("  fecha: "+fecha);
		System.out.println("  eventoId: "+eventoId);
		System.out.println("  tipo: "+tipo);
		System.out.println("  folios tam: "+f.size());
		System.out.println("  folioId: "+folioNum);
		ops.forEach(d-> System.out.println(d.id+"  "+d.personal));
		if (folio!=null && fecha==null) {
			System.out.println("retorno 1");
			return ok(agenda.render(f, null, folioNum, null, ops,  "{}"   ));
		}
		if (fecha==null) {
			System.out.println("retorno 2");
			return ok(agenda.render(f, null, null, null, ops,  "{}"   ));
		}
		System.out.println("retorno 3");
		return ok(agenda.render(f, fecha, folioNum, eventoId, ops,  "{}"   ));
	}
	

	public static Result ajaxDatosEventos() throws JSONException, ParseException{
		System.out.println("Desde ProductorServiciosController.ajaxDatosEventos\n");
		System.out.println("request()"+request());
		String inicio =    request().getQueryString("start");
		String fin =    request().getQueryString("end");

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date dtInicio = formatter.parse(inicio);
		Date dtFin = formatter.parse(fin);
		
		System.out.println("desde "+formatter.parse(inicio));
		System.out.println("fin "+formatter.parse(fin));
		
		String cadena;
		List<PreAgenda> ePreagenda = PreAgenda
				.find
				.fetch("estado")
				.fetch("fase")
				.fetch("salas")
				.fetch("folioproductorasignado")
				.fetch("folioproductorasignado.folio")				
				.fetch("folioproductorasignado.folio.oficio")
				.fetch("folioproductorasignado.personal")
				.where()
				.or( Expr.between("inicio", dtInicio,   dtFin  ),  Expr.between("fin", dtInicio,   dtFin  ))
				.findList();

		List<Agenda> eAgenda = Agenda
				.find
				.fetch("estado")
				.fetch("fase")
				.fetch("salas")
				.fetch("folioproductorasignado")
				.fetch("folioproductorasignado.folio")				
				.fetch("folioproductorasignado.folio.oficio")
				.fetch("folioproductorasignado.personal")
				.fetch("operadoresSala")
				.fetch("operadoresSala.personal")
				.where()
				.or( Expr.between("inicio", dtInicio,   dtFin  ),  Expr.between("fin", dtInicio,   dtFin  ))
			.findList();
		

		System.out.println("registros en eAgenda: "+eAgenda.size());
		
		List<SalaMantenimiento> eMantto = SalaMantenimiento
				.find
				.fetch("sala")
				.fetch("tipo")
				.where()
				.or( Expr.between("desde", dtInicio,   dtFin  ),  Expr.between("hasta", dtInicio,   dtFin  ))
			.findList();		
		
		
		
		// Generar JSON con los eventos
		JSONObject jobject = new JSONObject();
		new JSONObject();
		StringBuilder cadenaBuilder = new StringBuilder();
		for(PreAgenda pre : ePreagenda ){
			if( (pre.estado.id == 4 || pre.estado.id == 5)    ) {
					JSONObject jDatos = new JSONObject();
					JSONObject jOficio = new JSONObject();
					JSONObject jFolio = new JSONObject();
					JSONObject jEstado = new JSONObject();
					JSONObject jFase = new JSONObject();
					JSONObject jProductor = new JSONObject();
					JSONObject jFPA = new JSONObject();
					JSONArray  jarrPersonal = new JSONArray();
					new JSONArray();
					JSONArray  jarrLocutores = new JSONArray();
					JSONArray  jarrEquipo = new JSONArray();
					JSONArray  jarrAccesorios = new JSONArray();
					JSONArray  jarrInges = new JSONArray();


				JSONObject jSala = new JSONObject();
					JSONObject jUsoCabina = new JSONObject();
					
					jOficio.put("id", pre.folioproductorasignado.folio.oficio.id);
					jOficio.put("oficio", pre.folioproductorasignado.folio.oficio.oficio);
					jOficio.put("titulo", pre.folioproductorasignado.folio.oficio.titulo);
					jOficio.put("descripcion", pre.folioproductorasignado.folio.oficio.descripcion);
					if (pre.folioproductorasignado.folio.fechaentrega!=null)
						jOficio.put("fechaentrega",  formatter.format(pre.folioproductorasignado.folio.fechaentrega));
					
					if (pre.folioproductorasignado.folio.oficio.observacion!=null)
						jOficio.put("observacion", pre.folioproductorasignado.folio.oficio.observacion);					
					
					jFolio.put("id", pre.folioproductorasignado.folio.id);
					jFolio.put("numfolio", pre.folioproductorasignado.folio.numfolio);					
					jEstado.put("id", pre.folioproductorasignado.folio.estado.id);
					jEstado.put("descripcion", pre.folioproductorasignado.folio.estado.descripcion);
					jFolio.put("estado", jEstado);
					if (pre.folioproductorasignado.folio.observacion!=null)
						jFolio.put("observacion", pre.folioproductorasignado.folio.observacion);
					
					jFase.put("id", pre.fase.id);
					jFase.put("descripcion", pre.fase.descripcion);
					
					jProductor.put("id", pre.folioproductorasignado.personal.id);
					jProductor.put("nombre", pre.folioproductorasignado.personal.nombreCompleto());
					
					jFPA.put("id", pre.folioproductorasignado.id);
					
					if (pre.locaciones.size()>0) {
						JSONObject l = new JSONObject();						
						l.put("locacion", pre.locaciones.get(0).locacion);
						jDatos.put("locacion", l);
					}
					
					if (pre.salidas.size()>0) {
						JSONObject jSal = new JSONObject();
						jSal.put("salida", pre.salidas.get(0).salida);
						jDatos.put("salida", jSal);
					}
					
					if (pre.vehiculos.size()>0) { 
						jDatos.put("vehiculo","solicita");
					}
					
					if (pre.juntas.size()>0) { 
						jDatos.put("junta","si");
					}
					
					
					for ( PreAgendaRol p:pre.personal) {
						JSONObject jPerso = new JSONObject();
						JSONObject jRol = new JSONObject();
						jRol.put("id", p.rol.id);
						jRol.put("descripcion", p.rol.descripcion);
						
						jPerso.put("rol", jRol);
						jPerso.put("cantidad", p.cantidad);
						jarrPersonal.put(jPerso);
					}
					
					if ( pre.locutores.size()>0) {
						for ( PreAgendaLocutor l:pre.locutores) {
							JSONObject jLocutor = new JSONObject();
							jLocutor.put("id", l.personal.id);
							jLocutor.put("nombre", l.personal.nombreCompleto());
							jarrLocutores.put(jLocutor);
						}
						jDatos.put("locutores", jarrLocutores);
					}
					
					if (pre.salas.size()>0) {
						jSala.put("id", pre.salas.get(0).sala.id);
						jSala.put("descripcion", pre.salas.get(0).sala.descripcion);
						if (pre.salas.get(0).usoscabina.size()>0) {
							jUsoCabina.put("id", pre.salas.get(0).usoscabina.get(0).id);
							jUsoCabina.put("descripcion", pre.salas.get(0).usoscabina.get(0).usocabina);	
							jSala.put("usocabina", jUsoCabina);
						}
						jDatos.put("sala", jSala);
					}
					
					if (pre.operadoresSala.size()>0) {
						if (pre.operadoresSala.get(0).personal!=null && pre.operadoresSala.get(0).personal.id!=null) {
							System.out.println("pre.operadoresSala.size() "+pre.operadoresSala.size());
							System.out.println("pre.operadoresSala.get(0) "+pre.operadoresSala.get(0));
							System.out.println("pre.operadoresSala.get(0).personal "+pre.operadoresSala.get(0).personal);
							JSONObject jopSala = new JSONObject();
							jopSala.put("id", pre.operadoresSala.get(0).personal.id);
							jopSala.put("descripcion", pre.operadoresSala.get(0).personal.nombreCompleto());
							jDatos.put("operadorsala", jopSala);
						}
					}
					
					// Equipo	
					if (pre.equipos.size()>0) {
						for( PreAgendaEquipo equipo : pre.equipos) {
							JSONObject jEquipo = new JSONObject();
							jEquipo.put("id", equipo.equipo.id);
							jEquipo.put("descripcion", equipo.equipo.descripcion);
							jarrEquipo.put(jEquipo);
						}
						jDatos.put("equipo", jarrEquipo);
					}
					// Accesorios	
					if (pre.accesorios.size()>0) {
						for( PreAgendaAccesorio acc : pre.accesorios) {
							JSONObject jAcc = new JSONObject();
							jAcc.put("id", acc.accesorio.id);
							jAcc.put("descripcion", acc.accesorio.descripcion);
							jarrAccesorios.put(jAcc);
						}
						jDatos.put("accesorios", jarrAccesorios);
					}

					// Ingenierons admon eqpo y accesorios
					if (pre.ingsAdmonEqpo.size()>0){
						for(  PreAgendaIngAdmonEqpo ing : pre.ingsAdmonEqpo){
							JSONObject joIng = new JSONObject();
							joIng.put("id", ing.ingeniero.id);
							joIng.put("nombre", ing.ingeniero.nombreCompleto());
							jarrInges.put(joIng);
						}
						jDatos.put("ingsAdmonEqpo", jarrInges);
					}
					
					jDatos.put("tipo", "preagenda");
					jDatos.put("oficio", jOficio);
					jDatos.put("folio", jFolio);
					jDatos.put("fase", jFase);
					jDatos.put("productor", jProductor);
					jDatos.put("fpa", jFPA);
					jDatos.put("personalSolicitado", jarrPersonal);
					
					
					
					jobject.put("datos", jDatos);
					
					
				
					jobject.put("id", "pre"+pre.id );
					if(pre.fase.id==1L){
						if(pre.salas.size()==0){
							jobject.put("resourceId","a");
							jobject.put("overlap", true);
							jobject.put("selectOverlap", true);
						} else {
							if (pre.operadoresSala.size()>0)
								jobject.put("resourceId","c"+pre.operadoresSala.get(0).personal.id);

							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
							jobject.put("salaId", pre.salas.get(0).sala.id);
						}
					}
					if(pre.fase.id==2L){
						jobject.put("resourceId", "b");
						jobject.put("overlap", true);
						jobject.put("selectOverLap", true);
					}
					if(pre.fase.id==3L && pre.salas.size()!=0 ){
						if(pre.salas.get(0).sala.id == 3L){
							jobject.put("resourceId", "d"+pre.operadoresSala.get(0).personal.id);
						}
						if(pre.salas.get(0).sala.id == 2L){
							jobject.put("resourceId", "e"+pre.operadoresSala.get(0).personal.id);
						}
						if(pre.salas.get(0).sala.id == 7L){
							jobject.put("resourceId","f"+pre.operadoresSala.get(0).personal.id);
						}
						
						jobject.put("salaId", pre.salas.get(0).sala.id);
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
					}
					if(pre.fase.id==4L && pre.salas.size()!=0){
						if(pre.salas.get(0).sala.id == 3L){jobject.put("resourceId","d"+pre.operadoresSala.get(0).personal.id);}
						if(pre.salas.get(0).sala.id == 2L){jobject.put("resourceId","e"+pre.operadoresSala.get(0).personal.id);}
						if(pre.salas.get(0).sala.id == 7L){jobject.put("resourceId","f"+pre.operadoresSala.get(0).personal.id);}
						if(pre.salas.get(0).sala.id == 4L){jobject.put("resourceId","g"+pre.operadoresSala.get(0).personal.id);}
						if(pre.salas.get(0).sala.id == 1L){
								if (pre.operadoresSala.size()>0)
									jobject.put("resourceId","c"+pre.operadoresSala.get(0).personal.id);
							}
						jobject.put("salaId", pre.salas.get(0).sala.id);
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
					}
					if(pre.fase.id==5L){
						jobject.put("resourceId", "h");
						jobject.put("overlap", true);
						jobject.put("selectOverlap", true);
					}
					if(pre.fase.id==6L){
						jobject.put("resourceId", "i");
						jobject.put("overlap", false);
						jobject.put("selectOverlap", false);
						jobject.put("salaId", pre.salas.get(0).sala.id);
					}
					jobject.put("start",  sdf.format(pre.inicio)+"-06:00");
					jobject.put("end", sdf.format(pre.fin)+"-06:00");
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
					
					
					cadenaBuilder.append(jobject.toString()).append(", ");
					
			}	
			
		}

		StringBuilder cadenaBuilder1 = new StringBuilder(cadenaBuilder.toString());
		for(Agenda age : eAgenda){
			if( (age.estado.id >= 7 || age.estado.id <= 99)  && age.inicio != null  ) {
				JSONObject jDatos = new JSONObject();
				JSONObject jOficio = new JSONObject();
				JSONObject jFolio = new JSONObject();
				JSONObject jEstado = new JSONObject();
				JSONObject jFase = new JSONObject();
				JSONObject jProductor = new JSONObject();
				JSONObject jFPA = new JSONObject();
				JSONArray  jarrPersonalRol = new JSONArray();
				new JSONArray();
				JSONArray  jarrEquipo = new JSONArray();
				JSONArray  jarrAccesorios = new JSONArray();

				JSONObject jSala = new JSONObject();
				JSONObject jUsoCabina = new JSONObject();				
				
	            jOficio.put("id", age.folioproductorasignado.folio.oficio.id);
	            jOficio.put("oficio", age.folioproductorasignado.folio.oficio.oficio);
	            jOficio.put("titulo", age.folioproductorasignado.folio.oficio.titulo);
	            jOficio.put("descripcion", age.folioproductorasignado.folio.oficio.descripcion);
	            if (age.folioproductorasignado.folio.fechaentrega!=null)
	                jOficio.put("fechaentrega",  formatter.format(age.folioproductorasignado.folio.fechaentrega));
	            
	            if (age.folioproductorasignado.folio.oficio.observacion!=null)
	                jOficio.put("observacion", age.folioproductorasignado.folio.oficio.observacion);					
	            
	            jFolio.put("id", age.folioproductorasignado.folio.id);
	            jFolio.put("numfolio", age.folioproductorasignado.folio.numfolio);					
	            jEstado.put("id", age.folioproductorasignado.folio.estado.id);
	            jEstado.put("descripcion", age.folioproductorasignado.folio.estado.descripcion);
	            jFolio.put("estado", jEstado);
	            if (age.folioproductorasignado.folio.observacion!=null)
	                jFolio.put("observacion", age.folioproductorasignado.folio.observacion);
	            
	            jFase.put("id", age.fase.id);
	            jFase.put("descripcion", age.fase.descripcion);
	            
	            jProductor.put("id", age.folioproductorasignado.personal.id);
	            jProductor.put("nombre", age.folioproductorasignado.personal.nombreCompleto());
	            
	            jFPA.put("id", age.folioproductorasignado.id);
	            
	            if (age.locaciones.size()>0) {
	                JSONObject l = new JSONObject();						
	                l.put("locacion", age.locaciones.get(0).locacion);
	                jDatos.put("locacion", l);
	            }
	            
	            if (age.salidas.size()>0) {
	                JSONObject jSal = new JSONObject();
	                jSal.put("salida", age.salidas.get(0).salida);
	                jDatos.put("salida", jSal);
	            }
	            
	            if (age.vehiculos.size()>0) {
	            	JSONObject jV = new JSONObject();
	            	jV.put("id", age.vehiculos.get(0).vehiculo.id);
	            	jV.put("descripcion", age.vehiculos.get(0).vehiculo.descripcion);
	                jDatos.put("vehiculo", jV);
	            }
	            
	            if (age.juntas.size()>0) { 
	                jDatos.put("junta","si");
	            }
	            
	            
	            for ( AgendaCuentaRol p:age.cuentaRol) {
	                JSONObject jPersoRol = new JSONObject();
	                new JSONObject();	                
	                jPersoRol.put("id", p.id);
	                jPersoRol.put("nombre",  p.cuentarol.cuenta.personal.nombreCompleto());
	                jPersoRol.put("rol",  p.cuentarol.rol.id);
	                jPersoRol.put("Descripcion",  p.cuentarol.rol.descripcion);
	                jPersoRol.put("ctaRolId",  p.cuentarol.id);
	                
	                /*
	                jRol.put("id", p.rol.rol.id);
	                jRol.put("descripcion", p.rol.rol.descripcion);
	                
	                jPerso.put("rol", jRol);
	                */
	                jarrPersonalRol.put(jPersoRol);
	                
	            }

	            
	            if (age.salas.size()>0) {
	                jSala.put("id", age.salas.get(0).sala.id);
	                jSala.put("descripcion", age.salas.get(0).sala.descripcion);
	                if (age.salas.get(0).usoscabina.size()>0) {
	                    jUsoCabina.put("id", age.salas.get(0).usoscabina.get(0).id);
	                    jUsoCabina.put("descripcion", age.salas.get(0).usoscabina.get(0).usocabina);	
	                    jSala.put("usocabina", jUsoCabina);
	                }
	                jDatos.put("sala", jSala);
	            }
	            
	            if (age.operadoresSala.size()>0) {
	                if (age.operadoresSala.get(0).personal!=null && age.operadoresSala.get(0).personal.id!=null) {
	                    System.out.println("age.operadoresSala.size() "+age.operadoresSala.size());
	                    System.out.println("age.operadoresSala.get(0) "+age.operadoresSala.get(0));
	                    System.out.println("age.operadoresSala.get(0).personal "+age.operadoresSala.get(0).personal);
	                    JSONObject jopSala = new JSONObject();
	                    jopSala.put("id", age.operadoresSala.get(0).personal.id);
	                    jopSala.put("descripcion", age.operadoresSala.get(0).personal.nombreCompleto());
	                    jDatos.put("operadorsala", jopSala);
	                }
	            }
	            
	            // Equipo	
	            if (age.equipos.size()>0) {
	                for( AgendaEquipo equipo : age.equipos) {
	                    JSONObject jEquipo = new JSONObject();
	                    jEquipo.put("id", equipo.equipo.id);
	                    jEquipo.put("descripcion", equipo.equipo.descripcion);
						jEquipo.put("autorizo", (equipo.autorizo!=null)?equipo.autorizo.id:null);
	                    jarrEquipo.put(jEquipo);
	                }
	                jDatos.put("equipo", jarrEquipo);
	            }
	            // Accesorios	
	            if (age.accesorios.size()>0) {
	                for( AgendaAccesorio acc : age.accesorios) {
	                    JSONObject jAcc = new JSONObject();
	                    jAcc.put("id", acc.accesorio.id);
	                    jAcc.put("descripcion", acc.accesorio.descripcion);
						jAcc.put("autorizo", (acc.autorizo!=null)?acc.autorizo.id:null);
	                    jarrAccesorios.put(jAcc);
	                }
	                jDatos.put("accesorios", jarrAccesorios);
	            }					
	            
	            jDatos.put("tipo", "agenda");
	            jDatos.put("oficio", jOficio);
	            jDatos.put("folio", jFolio);
	            jDatos.put("fase", jFase);
	            jDatos.put("productor", jProductor);
	            jDatos.put("fpa", jFPA);
	            jDatos.put("personalAsignado", jarrPersonalRol);
	            
	            
	            
	            jobject.put("datos", jDatos);
				
				
				
				
				
				
				jobject.put("id", "age"+age.id);
					if(age.fase.id==1L){
						if(age.salas.size()==0){
							jobject.put("resourceId", "a");
							jobject.put("overlap", true);
							jobject.put("selectOverlap", true);
						} else {
							jobject.put("resourceId", "c"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
					}
					if (age.fase.id==2L){jobject.put("resourceId", "b");  jobject.put("overlap", true);}
					if (age.fase.id==3L && age.salas.size()!=0 ){
						if (age.salas.get(0).sala.id == 3L && age.operadoresSala.size()>0){
							jobject.put("resourceId", "d"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if (age.salas.get(0).sala.id == 2L){
							
							jobject.put("resourceId", "e"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if (age.salas.get(0).sala.id == 7L){
							jobject.put("resourceId", "f"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						jobject.put("salaId", age.salas.get(0).sala.id);
					}
					if (age.fase.id==4L && age.salas.size()!=0){
						if(age.salas.get(0).sala.id == 3L){
							jobject.put("resourceId", "d"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if(age.salas.get(0).sala.id == 2L && age.operadoresSala.size()>0){
							jobject.put("resourceId", "e"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if(age.salas.get(0).sala.id == 7L && age.operadoresSala.size()>0){
							jobject.put("resourceId","f"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if (age.salas.get(0).sala.id == 4L && age.operadoresSala.size()>0){
							jobject.put("resourceId", "g"+age.operadoresSala.get(0).personal.id);
							jobject.put("overlap", false);
							jobject.put("selectOverlap", false);
						}
						if(age.salas.get(0).sala.id == 1L && age.operadoresSala.size()>0){
							jobject.put("resourceId", "c"+age.operadoresSala.get(0).personal.id);
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
					jobject.put("start", sdf.format(age.inicio));
					jobject.put("end",   sdf.format(age.fin));
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
					cadenaBuilder1.append(jobject.toString()).append(", ");
			}
			
		}
		cadena = cadenaBuilder1.toString();


		//Si existe mantto para las salas, estas no deben estar disponibles para dar servicio
		//se muestran en la agenda como otro evento, pero no es posible ninguna modificación				
		for(SalaMantenimiento mantto : eMantto){
			String resource="";
			jobject = new JSONObject();
			jobject.put("start",  sdf.format(mantto.desde));
			jobject.put("end",  sdf.format(mantto.hasta));
			switch(mantto.sala.id.toString()) {
				case "1": resource =  "c"; break;  
				case "2": resource =  "e"; break;
				case "3": resource =  "d"; break;
				case "4": resource =  "g"; break;
				case "7": resource =  "f"; break;
			}

			for (  SalaMantenimiento sm : eMantto  ){
				for (  OperadorSala  operador : sm.sala.operadores  ){
					jobject.put("tipo", "mantto");
					jobject.put("resourceId", resource+operador.personal.id);
					jobject.put("title", "MANTENIMIENTO "+mantto.tipo.descripcion.toUpperCase()+"  DE LA SALA");
					jobject.put("overlap", false);
					jobject.put("resourceEditable", false);
					jobject.put("editable", false);
					cadena+=jobject.toString()+", ";
				}
			}


/*
			System.out.println("<<<<<<<   sala "+mantto.sala.id+"     resource "+resource);
			jobject.put("tipo", "mantto");
			jobject.put("resourceId", resource);
			jobject.put("title", "MANTENIMIENTO "+mantto.tipo.descripcion.toUpperCase()+"  DE LA SALA");
			jobject.put("overlap", false);
			jobject.put("resourceEditable", false);
			jobject.put("editable", false);
			cadena+=jobject.toString()+", ";

 */
		}		
		
		
		
		// Marca como no disponible los horarios que no son abarcados por los operadores de sala
		
		//Salas que requieren operador		
		List<Sala> salas = Sala.find.where().in("id", Arrays.asList(1,2,3,4,7)).findList();

		// Se crean variables de tipo Calendar
		Calendar calInicio = Calendar.getInstance();
		calInicio.setTime(dtInicio);
		calInicio.clear(Calendar.MILLISECOND);	
		
		//Horario de la jornada laboral
		Calendar calJornadaInicio = Calendar.getInstance();
		Calendar calJornadaFin = Calendar.getInstance();

		
	//	System.out.println("\n\n\n\n\n\n\ndtInicio "+dtInicio+"\n\n\n\n\n");
		
		
		
		//Horarios de operador por sala y dia
		StringBuilder cadenaBuilder2 = new StringBuilder(cadena);
		for(Sala sala : salas) {
			for ( OperadorSala op : OperadorSala.find
										.where()
										.eq("sala.id", sala.id).findList()){
				for (PersonalHorario horario : op.personal.horarios) {										
					calJornadaInicio.setTime( dtInicio  );
					calJornadaInicio.set(Calendar.HOUR_OF_DAY, 8);
					calJornadaInicio.set(Calendar.MINUTE, 0);
					calJornadaInicio.set(Calendar.SECOND, 0);
					calJornadaInicio.set(Calendar.MILLISECOND, 0);
			//		System.out.println("NUEVO "+ calJornadaInicio.getTime() );
					calJornadaFin.setTime(sdf.parse(inicio+" 21:00:00"));
					calJornadaFin.clear(Calendar.DATE);
					
				
					if ( horario.diasemana == calInicio.get(Calendar.DAY_OF_WEEK)  ) {
						Calendar auxCalInicio = Calendar.getInstance();
						Calendar auxCalFin = Calendar.getInstance();

						auxCalInicio.setTime(horario.desde);
						auxCalInicio.set(Calendar.DAY_OF_MONTH, calJornadaInicio.get(Calendar.DAY_OF_MONTH));
						auxCalInicio.set(Calendar.MONTH, calJornadaInicio.get(Calendar.MONTH));
						auxCalInicio.set(Calendar.YEAR, calJornadaInicio.get(Calendar.YEAR));
						auxCalInicio.set(Calendar.MILLISECOND, 0);
						
						auxCalFin.setTime(horario.hasta);
						auxCalFin.set(Calendar.DAY_OF_MONTH, calJornadaInicio.get(Calendar.DAY_OF_MONTH));
						auxCalFin.set(Calendar.MONTH, calJornadaInicio.get(Calendar.MONTH));
						auxCalFin.set(Calendar.YEAR, calJornadaInicio.get(Calendar.YEAR));
						auxCalFin.set(Calendar.MILLISECOND, 0);							
						
					//	System.out.println("comparando  "+calJornadaInicio.getTime()+"  contra  "+auxCalInicio.getTime());
						// Cuando el horario del operador inicia despues del inicio de la jornada de trabajo 
						if ( calJornadaInicio.before(auxCalInicio)){
							cadenaBuilder2.append(bloqueSinOperadorSala(calJornadaInicio, auxCalInicio, sala.id, op.personal.id));
						}
						// Cuando el horario del operador termina antes del fin de la jornada de trabajo 
						if ( auxCalFin.before(calJornadaFin)){
							cadenaBuilder2.append(bloqueSinOperadorSala(auxCalFin, calJornadaFin, sala.id, op.personal.id));
						}						
					}
				}
			}
		}
		cadena = cadenaBuilder2.toString();

		if (cadena.length()>0){
				System.out.println("\n\n\n\najaxDatosEventos regresa ["+cadena.substring(0, cadena.length()-2)  +"]");
				return ok (  "["+cadena.substring(0, cadena.length()-2)  +"]"   );
		}
		else {
			//System.out.println("\n\n\n\n[]");
			return ok (  "[]"   );
		}
	}
	
	
	
	private static String bloqueSinOperadorSala(Calendar inicio, Calendar fin, Long sala, Long personaId) {
		JSONObject jobject2 = new JSONObject();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


		try {
			jobject2.put("start", sdf.format(inicio.getTime())  );
			jobject2.put("end",  sdf.format(fin.getTime())  );							
			
			String recurso ="";
			//System.out.println("  *** "+op.personal.nombreCompleto()+"  "+op.sala.id+"  dia "+horario.diasemana+"  desde "+horario.desde+" "+horario.hasta);
			switch( sala.toString()) {
				case "1": recurso =  "c"+personaId; break;  
				case "2": recurso =  "e"+personaId; break;
				case "3": recurso =  "d"+personaId; break;
				case "4": recurso =  "g"+personaId; break;
				case "7": recurso =  "f"+personaId; break;
			}
			jobject2.put("resourceId", recurso);
			jobject2.put("rendering", "background");
			jobject2.put("title", "Sin operador");
			jobject2.put("backgroundColor", "#b0b2b4");		
			jobject2.put("overlap", false);		
			jobject2.put("resourceEditable", false);
			jobject2.put("editable", false);			
			return  jobject2.toString()+", ";			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return  jobject2.toString();			

	}
	
	
}



