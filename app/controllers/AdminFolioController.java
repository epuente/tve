package controllers;

import classes.miCorreo2;
import classes.ColorConsola;
import classes.Notificaciones.Notificacion;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.html.catalogos.Folio.createForm;
import views.html.catalogos.Folio.editForm;
import views.html.catalogos.Folio.list;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;

public class AdminFolioController extends ControladorSeguroAdministrador {
	public static Result GO_HOME = redirect(routes.AdminFolioController.list());

	public static Result list() {
		return ok(list.render(Folio.find.all()));

	}

	public static Result create(long idOficio) {
		System.out.println("Desde AdminFolioController.create: ");
		Form<Folio> folioForm = form(Folio.class);

		Folio f = new Folio();
		f.oficio = Oficio.find.byId(idOficio);
		f.estado = Estado.find.byId(2L);
		Ctacorreo activa = Ctacorreo.find.where().eq("activa", true).findUnique();

		boolean ccActiva= activa == null? false: true;

		return ok(createForm.render(folioForm, Oficio.find.byId(idOficio), ccActiva));
	}

	public static Result createSinOficio() {
		System.out.println("Desde AdminFolioController.createSinOficio: ");
		Form<Folio> folioForm = form(Folio.class);
		Folio f = new Folio();
		f.estado = Estado.find.byId(2L);
		Ctacorreo activa = Ctacorreo.find.where().eq("activa", true).findUnique();


		boolean ccActiva= activa == null? false: true;

		return ok(createForm.render(folioForm, null, ccActiva));
	}

	public static Result save() {
		Ebean.beginTransaction();
		try {
			miCorreo2 correo = new miCorreo2();
			System.out.println("Desde AdminFolioController.save()");

			DynamicForm df = DynamicForm.form().bindFromRequest();
			System.out.println(df);


			System.out.println("-------------------------------------------------------");

			Form<Folio> foliorForm = form(Folio.class).bindFromRequest();

			System.out.println(foliorForm);

			Folio obj = foliorForm.get();
			//	Oficio oficio = Oficio.find.ref(obj.oficio.id);

			if (obj.productoresAsignados != null)
				obj.estado = Estado.find.byId(4L);
			else
				obj.estado = Estado.find.byId(3L);


			System.out.println("numfolio "+obj.numfolio);
			System.out.println("oficio.id "+obj.oficio.id);
			System.out.println(obj.estado.id);
			System.out.println(obj.fechaentrega);
			System.out.println(obj.numeroresguardo);


			System.out.println("antes de save");
			Ebean.save(obj);
			Ebean.refresh(obj);
			Notificacion.getInstance().recargar();
			System.out.println("despues de refresh");

			correo.asunto="Asignación de folio";
			correo.mensaje="<strong>ESTA ES UNA PRUEBA DE CORREO (desde AdminFolioController.save)</strong><br><br>"+
					"Se le ha asignado como productor(a) para el folio <strong>"+obj.numfolio+"</strong> "+
					obj.oficio.descripcion+"<br><br>"+
					"Oficio "+obj.oficio.oficio+"  "+obj.oficio.descripcion+"<br><br>"+
					"Servicio(s) solicitado(s): ";
			StringBuilder servs = new StringBuilder();
			for ( OficioServicioSolicitado s : obj. oficio.servicios) {
				servs.append(s.servicio.descripcion).append("<br>");
			}
			correo.mensaje+=servs;
			if (  session("rolActual").compareTo("334")==0 ) {
				System.out.println("\n\n\n\n\nSE LANZA LA EXCEPCION\n\n\n\n\n");
				//	throw new Exception("AGUASSSSSS!!!!!");
				//System.out.println("tam prod: "+obj.productoresAsignados.size());
			}

			for ( FolioProductorAsignado d : obj.productoresAsignados ) {
				Personal p = Personal.find.ref(d.personal.id);
				System.out.println("persona id :"+p.id);
				System.out.println("persona nombre :"+p.nombre);
				System.out.println("tam correos de "+d.personal.nombre+ "  "+d.personal.correos.size());
				System.out.println("tam correos de "+d.personal.nombre+ "  "+d.personal.correos.size());
				for ( PersonalCorreo c :  PersonalCorreo.find.where().eq("personal.id", p.id).findList()) {
					if (correo.para== null)
						correo.para = new ArrayList<>();
					correo.para.add(c.email);
				}
			}


			System.out.print("tam para: "+correo.para.size());


			System.out.println("asunto "+correo.asunto);
			System.out.println("mensaje "+correo.mensaje);
			correo.para.forEach(System.out::println);

			correo.enviar();
			Ebean.commitTransaction();

			flash("success", "Se agregó el folio " + foliorForm.get().numfolio);
		} catch(Exception e) {
			System.out.println("Ocurrió un error al intentar grabar el folio "+e);
		}finally {
			Ebean.endTransaction();
		}

		return GO_HOME;
	}


	private static void correoAsignacion(Folio obj) {
		Ctacorreo cc = new Ctacorreo();
		Ctacorreo activa = Ctacorreo.find.where().eq("activa", true).findUnique();

		if ( activa!=null) {
			miCorreo2 correo = new miCorreo2();
			correo.asunto = "Asignación de folio";
			correo.mensaje = "<strong>ESTA ES UNA PRUEBA DE CORREO (desde AdminFolioController.correoAsignacion)</strong><br><br>"+
					"Se le ha asignado como productor(a) para el folio <strong>" + obj.numfolio + "</strong> " +
					obj.oficio.descripcion + "<br><br>" +
					"Oficio " + obj.oficio.oficio + "  " + obj.oficio.descripcion + "<br><br>" +
					"Servicio(s) solicitado(s): ";
			StringBuilder servs = new StringBuilder();
			for (OficioServicioSolicitado s : obj.oficio.servicios) {
				servs.append(s.servicio.descripcion).append("<br>");
			}
			correo.mensaje += servs;
			if (session("rolActual").compareTo("334") == 0) {
				System.out.println("\n\n\n\n\nSE LANZA LA EXCEPCION\n\n\n\n\n");
				//	throw new Exception("AGUASSSSSS!!!!!");
				//System.out.println("tam prod: "+obj.productoresAsignados.size());
			}

			for (FolioProductorAsignado d : obj.productoresAsignados) {
				Personal p = Personal.find.ref(d.personal.id);
				System.out.println("persona id :" + p.id);
				System.out.println("persona nombre :" + p.nombre);
				System.out.println("tam correos de " + d.personal.nombre + "  " + d.personal.correos.size());
				System.out.println("tam correos de " + d.personal.nombre + "  " + d.personal.correos.size());
				for (PersonalCorreo c : PersonalCorreo.find.where().eq("personal.id", p.id).findList()) {
					if (correo.para == null)
						correo.para = new ArrayList<>();
					correo.para.add(c.email);
				}
			}


			System.out.print("tam para: " + correo.para.size());


			System.out.println("asunto " + correo.asunto);
			System.out.println("mensaje " + correo.mensaje);
			correo.para.forEach(System.out::println);

			correo.enviar();
		}
	}

	public static Result saveSerializada() throws Exception {
		Ebean.beginTransaction();
		JSONObject retorno = new JSONObject();
		System.out.println("Desde AdminFolioController.saveSerializada");
		JsonNode json = request().body().asJson();
		System.out.println("json:"+json);
		Folio nuevo = new Folio();
		nuevo.oficio = Oficio.find.ref(  json.findValue("oficio.id").asLong() );
		nuevo.numfolio = json.findValue("numfolio").asLong();
		if (!json.findValue("observacion").isNull())
			nuevo.observacion = json.findValue("observacion").asText();
		if (json.findValue("fechaentrega").asText() != null) {
			try {
				nuevo.fechaentrega = new SimpleDateFormat("yyyy-MM-dd").parse((json.findValue("fechaentrega").asText()));
			} catch (ParseException e1) {
				System.out.println("Error en la conversion de fecha "+e1);
				e1.printStackTrace();
			}
		}

		System.out.println("numeroresguardo:"+json.findValue("numeroresguardo"));

		if (json.findValue("numeroresguardo")!=null)
			if (json.findValue("numeroresguardo").asText() != null )
				nuevo.numeroresguardo = json.findValue("numeroresguardo").asText();
		nuevo.productoresAsignados = new ArrayList<FolioProductorAsignado>();
		JsonNode json2 = request().body().asJson();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json2.toString());
		for(JsonNode productor : jsonNode.get("productoresAsignados")) {
			FolioProductorAsignado fpa = new FolioProductorAsignado();
			fpa.personal = Personal.find.ref(  productor.get("personal").get("id").asLong()  );
			nuevo.productoresAsignados.add( fpa );
		}
		if (nuevo.productoresAsignados != null) {
			nuevo.estado = Estado.find.byId(4L);
		} else {
			nuevo.estado = Estado.find.byId(3L);
		}
		try {
			retorno.put("estado", 0);
			Ebean.save(nuevo);
			Notificacion.getInstance().recargar();
			Ebean.refresh(nuevo);
			correoAsignacion(nuevo);
			retorno.put("estado", "ok");
			Ebean.commitTransaction();
		} catch (JSONException e) {
			System.out.println(ColorConsola.ANSI_RED_BACKGROUND +ColorConsola.ANSI_WHITE+ "     Error en la conversion a JSON. Se aplica rollback a la transaccion     "+ColorConsola.ANSI_RESET);
			e.printStackTrace();
			Ebean.rollbackTransaction();
		} finally {
			Ebean.endTransaction();
		}
		return ok (retorno.toString());
	}

	public static Result edit(Long id) {
		return ok(editForm.render(form(Folio.class).fill(Folio.find.byId(id))));
	}


	public static Result update(Long id) {
		System.out.println("Desde AdminFolioController.update");
		Form<Folio> folioForm = form(Folio.class).bindFromRequest();
		Folio f = folioForm.get();
		System.out.println("folioForm: " + folioForm);
		System.out.println("objeto folio:" + f);
		if (folioForm.hasErrors()) {
			return badRequest(editForm.render(folioForm));
		}
		System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
		Folio aux = Folio.find.byId(id);
		aux.numfolio = f.numfolio;
		f.update(id);
		Notificacion.getInstance().recargar();
		System.out.println("...............................................................");
		flash("success", "Se actualizó el folio " + aux.numfolio);
		return redirect(routes.AdminFolioController.list());
	}

	public static Result updateAjax() throws JSONException {

		JSONObject retorno = new JSONObject();
		retorno.put("estado", "error");
		System.out.println("Desde AdminFolioController.updateAjax");
		JsonNode json = request().body().asJson();
		System.out.println(json);
		Folio nuevo = Json.fromJson(json, Folio.class);
		System.out.println("objeto: " + nuevo);
		System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
		// Obtener los ids de los productores a asignar en la capa vista
		List<Long> idsProdVista = nuevo.productoresAsignados.stream().map(m->m.personal.id).collect(Collectors.toList());

		Folio aux = Folio.find.byId(nuevo.id);

		// Obtener los ids de los productores asignados en la DB
		List<Long> idsProdDB = aux.productoresAsignados.stream().map(m->m.personal.id).collect(Collectors.toList());
		System.out.println("DB idsProdDB:"+idsProdVista.size());
		System.out.println("vista productores asignados:"+  idsProdDB.size() );

		Ebean.beginTransaction();
		try {
			if (  idsProdVista.equals(idsProdDB) ) {


			} else {
				System.out.println("Difieren los productores asignados");
				retorno.put("tipoError", "Difieren los productores asignados");
				List<Long> diferentes;
				boolean cambio;
				if (idsProdDB.size()!=0) {
					diferentes = new ArrayList<>(idsProdVista);
					cambio = diferentes.removeAll(idsProdDB);
				} else {
					diferentes = idsProdVista;
					cambio = true;
				}
				if (cambio) {
					System.out.println("Hubo cambio, diferentes.size:" + diferentes.size());
					//nuevo.productoresAsignados = new ArrayList<>();
					for (Long u : diferentes) {
						System.out.println("     -->" + u);
						FolioProductorAsignado nfpa = new FolioProductorAsignado();
						nfpa.personal = Personal.find.ref(u);
						nfpa.folio = aux;
						Ebean.save(nfpa);
						Notificacion.getInstance().recargar();
						//nuevo.productoresAsignados.add(nfpa);
					}
				}
			}


			// Checar que si se va a eliminar un productor, este no debe tener preagendas ni agendas
			//https://www.baeldung.com/java-lists-difference


			System.out.println("ANTES nuevo productores asignados:"+  nuevo.productoresAsignados.size() );
			for (FolioProductorAsignado fpa  : nuevo.productoresAsignados ){
				System.out.println("    "+fpa.personal.id+"  "+fpa.personal.nombreCompleto());
			}


			//Ebean.saveAssociation(nuevo, "productoresAsignados");
			Ebean.update(nuevo);
			nuevo.refresh();
			System.out.println("DESPUES nuevo productores asignados:"+  nuevo.productoresAsignados.size() );
			for (FolioProductorAsignado fpa  : nuevo.productoresAsignados ){
				System.out.println("    "+fpa.personal.id+"  "+fpa.personal.nombreCompleto());
			}
			retorno.put("estado", "actualizado");
			System.out.println("AdminFolioController.updateAjax regresando... " + retorno.toString());
			Ebean.commitTransaction();
		} catch (Exception e){
			e.printStackTrace();
			Ebean.rollbackTransaction();
			retorno.put("tipoError", "Ocurrió una excepción");
			Logger.error(ColorConsola.ANSI_RED_BACKGROUND+ColorConsola.ANSI_WHITE+"Ocurrió una excepción en AdminFolioController.updateAjax"+ColorConsola.ANSI_RESET);
		} finally {
			Ebean.endTransaction();
		}
			return ok (retorno.toString());
		}


		public static Result ajaxBuscaFolioProductorAsignado(){
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
			System.out.println("   ...........................   desde AdminFolioController.ajaxBuscaFolioProductorAsignado ");
			JsonNode json = request().body().asJson();
			System.out.println(json);
			Long id = json.findPath("id").asLong();
			boolean borrable=true;
			List<String> mensajes = new ArrayList<>();
			List<FolioProductorAsignado> fpas = FolioProductorAsignado.find
					.where()
					.eq("folio.id", id)
					.findList();

			for (FolioProductorAsignado fpa: fpas){
				mensajes.add("Se asignó al productor "+fpa.personal.nombreCompleto());
				for (PreAgenda preagenda : fpa.preagendas){
					if (preagenda.estado.id > 5) {
						borrable = false;
						break;
					}
				}
				for (Agenda agenda : fpa.agenda){
					String dia = sdfFecha.format(agenda.inicio);
					String desde = sdfHora.format(agenda.inicio);
					String hasta = sdfHora.format(agenda.fin);
					if (agenda.estado.id == 5) {
						mensajes.add("El productor solicitó recursos para el evento del " + dia + " de las "+desde+" a las " + hasta + " fase " + agenda.fase.descripcion);
					}
					if (agenda.estado.id == 7) {
						mensajes.add("Se le han asignado recursos al evento del " + dia + " de las "+desde+" a las " + hasta + " fase " + agenda.fase.descripcion);
					}
					if (agenda.estado.id == 8) {
						mensajes.add("Se le han asignado equipo y accesorios para al evento del " + dia + " de las "+desde+" a las " + hasta + " fase " + agenda.fase.descripcion);
					}
					if (agenda.estado.id > 8 && agenda.estado.id < 99) {
						mensajes.add( agenda.estado.descripcion + " del evento del " + dia + " de las "+desde+" a las " + hasta + " fase " + agenda.fase.descripcion);
					}
					if (agenda.estado.id == 99 || agenda.estado.id ==100) {
						mensajes.add( agenda.estado.descripcion );
					}
					if (agenda.estado.id > 5){
						borrable = false;
					}
				}

			}

			JSONObject jo = new JSONObject();

			try {
				jo.put("borrable", borrable);
				jo.put("mensajes", mensajes);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			return ok(jo.toString());
		}


		public static Result eliminaFolio() throws JSONException {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nDesde AdminFolioController.eliminaFolio");
			System.out.println("   ...........................   desde FolioController.eliminaFolio ");
			JsonNode json = request().body().asJson();
			boolean borrable =true;
			boolean borrado =false;
			String mensaje;
			System.out.println(json);
			Long id = json.findPath("id").asLong();

			List<FolioProductorAsignado> fpas = FolioProductorAsignado.find
					.where()
					.eq("folio.id", id)
					.filterMany("preagendas").gt("estado.id", 5)
					.filterMany("agenda").gt("estado.id", 5)
					.findList();
			for (FolioProductorAsignado fpa : fpas){
				Logger.debug("    pre:"+fpa.preagendas.size()+"    age:"+fpa.agenda.size());
				if (fpa.preagendas.size() > 0 || fpa.agenda.size() > 0) {
					borrable = false;
					borrado = false;
					break;
				}
			}
			Logger.debug("    borrable:"+borrable);
			if (!borrable){
				mensaje = "No es posible eliminar el folio puesto que contiene solicitudes y/o asignaciones de recursos";
			} else {
				List<Long> ids = fpas.stream().map(m -> m.folio.id).collect(Collectors.toList());
				List<Folio> r = Folio.find.where().idIn(ids).findList();
				Ebean.delete(r);
				Notificacion.getInstance().recargar();
				mensaje ="Se eliminó correctamente";
				borrado = true;
			}
			String cadena = "{\"borrado\":"+borrado+", \"borrable\":"+borrable+", \"mensaje\": "+mensaje+"}";
			System.out.println(cadena);
			JSONObject json2 = new JSONObject(cadena);
			System.out.println("FolioController.eliminaFolio regresa:  "+json2);
			return ok( json2.toString()  );
		}


		public static Result cancelaFolio() throws JSONException {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nDesde AdminFolioController.cancelaFolio");
			JsonNode json = request().body().asJson();
			boolean cancelado =false;
			String mensaje;
			System.out.println(json);
			Long id = json.findPath("id").asLong();
			Long motivoId = json.findPath("motivoId").asLong();

			System.out.println(id);
			System.out.println(motivoId);

			Folio f = Folio.find.byId(id);

			System.out.println("f "+f);
			MotivoCancelacion mc = MotivoCancelacion.find.byId(motivoId);
			System.out.println("mc "+mc);

			FolioCancelacion fc = new FolioCancelacion();
			fc.folio = f;
			fc.motivo = mc;
			fc.estadoanterior = f.estado;
			f.foliocancelacion = fc;
			f.estado = Estado.find.byId(100L);
			f.update();
			Notificacion.getInstance().recargar();
			cancelado = true;
			String cadena = "{\"cancelado\":"+cancelado+"}";
			System.out.println(cadena);
			JSONObject json2 = new JSONObject(cadena);
			System.out.println("FolioController.cancelaFolio regresa:  "+json2);
			return ok( json2.toString()  );
		}


}
