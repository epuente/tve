package controllers;

import static play.data.Form.form;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import models.*;
import models.utils.PlantillaArchivo;
import org.json.JSONException;
import org.json.JSONObject;

import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.catalogos.Oficio.createForm;
import views.html.catalogos.Oficio.editForm;
import views.html.catalogos.Oficio.list;
import play.db.ebean.Model;
public class AdminOficioController extends ControladorSeguroAdministrador {
	public static Result create() {
		Form<Oficio> oficioForm = form(Oficio.class);
		return ok(createForm.render(oficioForm));
	}

	public static Result list( String filtro ) {
		return ok(list.render(filtro));
	}

	public static Result save() throws JSONException {
		JSONObject retorno = new JSONObject();
		retorno.put("estado", "error");
		Form<Oficio> oficiorForm = form(Oficio.class).bindFromRequest();
		System.out.println("desde AdminOficioController.save....");
		System.out.println(oficiorForm);
		if (oficiorForm.hasErrors()) {
			return badRequest(createForm.render(oficiorForm));
		}
		Oficio aux = oficiorForm.get();

		MultipartFormData body = request().body().asMultipartFormData();

		System.out.println("\nMultipartFormData body: "+body);
		System.out.println("\nMultipartFormData body.getFiles size: "+body.getFiles().size());
/*
		for ( FilePart f : body.getFiles()){
			System.out.println("    key: " +f.getKey() );
		}

		System.out.println("\nMultipartFormData body: "+body.getFiles());
		FilePart picture = body.getFile("file");
		FilePart picture2 = body.getFile("file2");
		if (picture != null) {
			File file = picture.getFile();
				OficioImagen ima = new OficioImagen();
				Path p = Paths.get(file.getPath());
				byte[] byteFile = null;
				try {
					byteFile = Files.readAllBytes(p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ima.contenido = byteFile;
				ima.nombrearchivo = picture.getFilename();
				ima.contenttype = picture.getContentType();
				aux.imagenes.add(ima);
		}

		if (picture2 != null) {
			File file2 = picture2.getFile();
				OficioRespuesta ima = new OficioRespuesta();
				Path p = Paths.get(file2.getPath());
				byte[] byteFile = null;
				try {
					byteFile = Files.readAllBytes(p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ima.contenido = byteFile;
				ima.nombrearchivo = picture2.getFilename();
				ima.contenttype = picture2.getContentType();
				aux.oficiosrespuestas.add(ima);
		}
*/

		//	aux.id=1931L;
		// ARCHIVOS DEL OFICIO
	//	MultipartFormData body = request().body().asMultipartFormData();
		Logger.debug("body "+body.getFiles());
		List<FilePart> archs = body.getFiles();
		Logger.debug("body, num file: "+archs.size());

		for (FilePart arch : archs) {
			Logger.debug("   "+arch.getKey()+ " -> "+ arch.getFilename());
			String[] aux1 = arch.getKey().split("-");
			int tipo = Integer.parseInt(aux1[1]);
			int sec = Integer.parseInt(aux1[2]);
			PlantillaArchivo pa = new PlantillaArchivo();
			Path p = Paths.get(arch.getFile().getPath());
			byte[] byteFile = null;
			try {
				byteFile = Files.readAllBytes(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pa.nombrearchivo = arch.getFilename();
			pa.contenttype = arch.getContentType();
			pa.contenido = byteFile;
			if (tipo==1){
				OficioImagen ofIma = new OficioImagen(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.imagenes.add(ofIma);
			}
			if (tipo==2){
				OficioRespuesta a = new OficioRespuesta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.oficiosrespuestas.add(a);
			}

			if (tipo==3){
				OficioMinuta a = new OficioMinuta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.minutas.add(a);
			}

			if (tipo==4){
				OficioGuion a = new OficioGuion(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.guiones.add(a);
			}

			if (tipo==5){
				OficioEntradaSalida a = new OficioEntradaSalida(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.entradassalida = a;
				aux.entradassalida.oficio = aux;
			}

			if (tipo==6){
				OficioBitacora a = new OficioBitacora(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.bitacoras.add(a);
			}

			if (tipo==7){
				OficioEvidenciaEntrega a = new OficioEvidenciaEntrega(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.evidenciaentrega = a;
				aux.evidenciaentrega.oficio = aux;
			}

			if (tipo==8){
				OficioEncuesta a = new OficioEncuesta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				aux.encuesta = a;
				aux.encuesta.oficio = aux;
			}
		}


		aux.save();
		aux.refresh();
		retorno.put("estado", "ok");
		retorno.put("id", aux.id);
		return ok(retorno.toString());

	}

	public static Result edit(Long id) {
		return ok(editForm.render(form(Oficio.class).fill(Oficio.find.byId(id))));
	}

	public static Result update(Long id) throws JSONException {
		JSONObject retorno = new JSONObject();
		retorno.put("estado", "error");
		System.out.println("Desde AdminOficioController.update-..........................................");
		Form<Oficio> oficiorForm = form(Oficio.class).bindFromRequest();
		Oficio o = oficiorForm.get();
		System.out.println("oficiorForm: "+oficiorForm);
		System.out.println("objeto Oficio: "+o);
		if (oficiorForm.hasErrors()) {
			return badRequest(editForm.render(oficiorForm));
		}

		// Lo siguiente es solo para mostrar valores en consola
        /*
		for (OficioContacto contacto : o.contactos) {
			for (OficioContactoTelefono telefono : contacto.telefonos) {
				for (Field field : o.contactos.get(0).telefonos.get(0).getClass().getDeclaredFields()) {
					field.setAccessible(true);
					try {
						Object value = field.get(o.contactos.get(0).telefonos.get(0));
						if (value != null) {
							System.out.println(field.getName() + "=" + value);
						}
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		}
		*/

		MultipartFormData body = request().body().asMultipartFormData();
		//Logger.debug("body "+body.getFiles());
		List<FilePart> archs = body.getFiles();
		Logger.debug("body, numero de archivos: "+archs.size());

	    for (FilePart arch : archs) {
			Logger.debug("   "+arch.getKey()+ " -> "+ arch.getFilename());
			String[] aux = arch.getKey().split("-");
			int tipo = Integer.parseInt(aux[1]);
			int sec = Integer.parseInt(aux[2]);
			PlantillaArchivo pa = new PlantillaArchivo();
			Path p = Paths.get(arch.getFile().getPath());
			byte[] byteFile = null;
			try {
				byteFile = Files.readAllBytes(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pa.nombrearchivo = arch.getFilename();
			pa.contenttype = arch.getContentType();
			pa.contenido = byteFile;
			if (tipo==1){
				OficioImagen ofIma = new OficioImagen(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.imagenes.add(ofIma);
			}
			if (tipo==2){
				OficioRespuesta a = new OficioRespuesta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.oficiosrespuestas.add(a);
			}
			// One To Many
			if (tipo==3){
				OficioMinuta a = new OficioMinuta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				// Archivos preexistentes
				List<OficioMinuta> minutas = Oficio.find.byId(o.id).minutas;
				minutas.add(a);
				//o.minutas.add(minutas);
				o.minutas.addAll(minutas);
			}

			// OneToMany
			if (tipo==4){
				OficioGuion a = new OficioGuion(pa.nombrearchivo, pa.contenttype, pa.contenido);
				// Archivos preexistentes
				List<OficioGuion> guiones = Oficio.find.byId(o.id).guiones;
				guiones.add(a);
				o.guiones.addAll(guiones);

			}

			if (tipo==5){
				OficioEntradaSalida a = new OficioEntradaSalida(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.entradassalida = a;
				o.entradassalida.oficio = o;
			}

			// OneToMany
			if (tipo==6){
				OficioBitacora a = new OficioBitacora(pa.nombrearchivo, pa.contenttype, pa.contenido);
				// Archivos preexistentes
				List<OficioBitacora> bitas = Oficio.find.byId(o.id).bitacoras;
				bitas.add(a);
				o.bitacoras.addAll(bitas);
			}

			if (tipo==7){
				OficioEvidenciaEntrega a = new OficioEvidenciaEntrega(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.evidenciaentrega = a;
				o.evidenciaentrega.oficio = o;
			}

			if (tipo==8){
				OficioEncuesta a = new OficioEncuesta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.encuesta = a;
				o.encuesta.oficio = o;
			}
	    }
		o.update(id);
		retorno.put("estado", "ok");
		//  seguir checando esto,   AQUI SI OPERA CORRECTAMENTE EL UPDATE, PERO AL HACER UPDATE DEL FOLIO, SE PIERDEN LAS SOLICITUDES RELACIONADAS

		return ok(retorno.toString());
	}

	@Transactional
	public static Result ajaxEliminaOficio() {
		System.out.println("   ...........................   desde ajaxEliminaOficio ");
		boolean eliminado = false;
		JsonNode json = request().body().asJson();
		System.out.println(json);
		long idOficio = json.findPath("idOficio").asLong();
		System.out.println("idOficio:" + idOficio);
		Oficio o = Oficio.find.byId(idOficio);
		if (o != null) {
			System.out.println("unooooooooooooo");
			o.folios.forEach(f -> {
				System.out.println(" . . . . . folio id:" + f.id);
				List<HisFolio> hf = HisFolio.find.where().eq("folio.id", f.id).findList();
				System.out.println(" . . . . . hf:" + hf.size());
				hf.forEach(hf2 -> {
					System.out.println("ciclo");
					hf2.delete();
				});
			});
		}
		if (o != null) {
			o.delete();
			eliminado = (Oficio.find.byId(idOficio) == null);
		}
		return ok("{\"eliminado\":" + eliminado + "}");
	}

	@Transactional
	public static Result save2() {
		System.out.println("\n\nDesde AdminOficio.save2");
		JsonNode json = request().body().asJson();
		System.out.println(".0...\n"+json);

		ObjectMapper mapper = new ObjectMapper();
		Oficio of;
		//of.urremitente = UnidadResponsable.find.where().idEq(remitente).findUnique();
		//Quitar nodos
		for (JsonNode nodo : json) {
			if (nodo instanceof ObjectNode) {
				ObjectNode object = (ObjectNode) nodo;
				object.remove("csrfToken");
				object.remove("urremitente.id");
				object.remove("auxContactos");
				object.remove("archivoOficio");
			}
		}
		System.out.println(".1... \n"+json);

		try {
			of = mapper.convertValue(json, Oficio.class);
			of.save();
			of.refresh();
			return ok (   "{\"estado\": \"ok\", \"id\":"+of.id+"}"   );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Ocurrió un error ......");
			e.printStackTrace();
			return ok (   "{\"estado\": \"error\"}"   );
		}
	}


	@Transactional
	public static Result update2() {
		System.out.println("\n\nDesde AdminOficio.update2");
		JsonNode json = request().body().asJson();
		System.out.println(".0... "+json);
		ObjectMapper mapper = new ObjectMapper();
		Oficio of;
		//of.urremitente = UnidadResponsable.find.where().idEq(remitente).findUnique();
		//Quitar nodos
		for (JsonNode nodo : json) {
			if (nodo instanceof ObjectNode) {
				ObjectNode object = (ObjectNode) nodo;
				object.remove("csrfToken");
				object.remove("urremitente.id");
				object.remove("auxContactos");
				object.remove("fechagrabaciones.fecha");
			}
		}
		System.out.println(".1... \n"+json);
		try {
			of = mapper.convertValue(json, Oficio.class);

			Oficio auxof = Oficio.find.byId(   json.findValue("id").asLong());

			auxof.productoresSolicitados.forEach(Model::delete);
			auxof.contactos.forEach(Model::delete);
			auxof.servicios.forEach(Model::delete);
			auxof.fechagrabaciones.forEach(Model::delete);


			auxof.contactos = of.contactos;
			auxof.descripcion = of.descripcion;
			auxof.fechagrabaciones = of.fechagrabaciones;
			auxof.fecharecibidodtve = of.fecharecibidodtve;
			auxof.fecharecibidoupev = of.fecharecibidoupev;
			auxof.fecharemitente = of.fecharemitente;
			auxof.folios = of.folios;
			auxof.observacion = of.observacion;
			auxof.oficio = of.oficio;
			auxof.oficiosrespuestas = of.oficiosrespuestas;
			auxof.productoresSolicitados = of.productoresSolicitados;
			auxof.servicios = of.servicios;
			auxof.titulo = of.titulo;
			auxof.urremitente = of.urremitente;


			auxof.update(auxof.id);
			//		of.update(auxof);

			return ok (   "{\"estado\": \"ok\", \"id\":"+auxof.id+"}"   );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Ocurrió un error ......");
			e.printStackTrace();
			return ok (   "{\"estado\": \"error\"}"   );
		}
	}


	// Elimina archivos relacionados al oficio (imagen, respuesta, minuta, etc...)
	public static Result eliminaArchivoOficio() throws JSONException {
		System.out.println("\n\nDesde AdminOficio.eliminaArchivoOficio");
		JsonNode json = request().body().asJson();
		System.out.println(json);
		int tipo = json.findValue("tipo").asInt();
		Long idArchivo = json.findValue("idArchivo").asLong();
		JSONObject retorno = new JSONObject();

		// Usando generics

		try {
			if (tipo == 1)
				OficioImagen.find.byId(idArchivo).delete();
			if (tipo == 2)
				OficioRespuesta.find.byId(idArchivo).delete();
			if (tipo == 3)
				OficioMinuta.find.byId(idArchivo).delete();
			if (tipo == 4)
				OficioGuion.find.byId(idArchivo).delete();
			if (tipo == 5)
				OficioEntradaSalida.find.byId(idArchivo).delete();
			if (tipo == 6)
				OficioBitacora.find.byId(idArchivo).delete();
			if (tipo == 7)
				OficioEvidenciaEntrega.find.byId(idArchivo).delete();
			if (tipo == 8)
				OficioEncuesta.find.byId(idArchivo).delete();
			retorno.put("eliminado", true);
		}catch (Exception e){
			retorno.put("eliminado", false);
		}
		return ok(retorno.toString());
	}

}
