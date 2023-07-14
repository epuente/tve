package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
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


		//	aux.id=1931L;
/*
		List<Oficio> tempo = Oficio.find.order("id").findList();
		Long idMax =  (tempo.get(tempo.size()-1).id)+1;
		
		
		System.out.println("nuevo id de Oficio "+idMax);
		
		
		Oficio nuevo = new Oficio();

		
		nuevo.id = idMax;
		nuevo.oficio = aux.oficio;
		nuevo.urremitente = aux.urremitente;
		nuevo.descripcion = aux.descripcion;
		nuevo.titulo = aux.titulo;
		nuevo.fecharecibidoupev = aux.fecharecibidoupev;
		nuevo.fecharemitente = aux.fecharemitente;
		nuevo.observacion = aux.observacion;
		
		
		System.out.println("servicios tam " + aux.servicios.size());
		System.out.println("servicios " + aux.servicios);
		System.out.println("servicios[0] " + aux.servicios.get(0));
		System.out.println("servicios[0].servicio " + aux.servicios.get(0).servicio);
		System.out.println("servicios[0].servicio.id " + aux.servicios.get(0).servicio.id);
		
		
		nuevo.urremitente = UnidadResponsable.find.setId(aux.urremitente.id).findUnique();

		List<OficioServicioSolicitado> tempo2 = OficioServicioSolicitado.find.order("id").findList();
		Long idMax2 =  (tempo2.get(tempo2.size()-1).id)+1;

		
		for (int i = 0; i<aux.servicios.size(); i++) {
			System.out.println("ser id "+i+" ------->"+aux.servicios.get(i).servicio.id);
			OficioServicioSolicitado oss = new OficioServicioSolicitado();
			oss.servicio = Servicio.find.setId(aux.servicios.get(i).servicio.id).findUnique();

			oss.id = idMax2;
			System.out.println("id para OficioServicioSolicitado "+idMax2);
			nuevo.servicios.add(oss);
			idMax2++;
		}
		
		List<OficioProductorSolicitado> maxProdSol = OficioProductorSolicitado.find.order("id").findList();
		Long idMaxProdSol = (maxProdSol.get(maxProdSol.size()-1).id)+1;
		for (int i=0; i<aux.productoresSolicitados.size(); i++) {
			OficioProductorSolicitado ops = new OficioProductorSolicitado();
			ops.id=idMaxProdSol++;
			ops.personal = Personal.find.setId(aux.productoresSolicitados.get(i).personal.id).findUnique();
			nuevo.productoresSolicitados.add(ops);
		}
		
		
		nuevo.fechagrabaciones = aux.fechagrabaciones;
		
		List<OficioContacto> maxOfCont = OficioContacto.find.order("id").findList();
		List<OficioContactoTelefono> maxOfContTel = OficioContactoTelefono.find.order("id").findList();
		List<OficioContactoCorreo> maxOfContCorreo = OficioContactoCorreo.find.order("id").findList();
		Long idMaxOfCont = (maxOfCont.get(maxOfCont.size()-1).id)+1;
		Long idMaxOfContTel = (maxOfContTel.get(maxOfContTel.size()-1).id)+1;
		Long idMaxOfContCorr = (maxOfContCorreo.get(maxOfContCorreo.size()-1).id)+1;
		for ( OficioContacto ofCont  : aux.contactos) {
			System.out.println( ofCont.nombre  );
			ofCont.id=idMaxOfCont++;
			
			for (   OficioContactoTelefono tel  :  ofCont.telefonos ) {
				System.out.println("     "+ tel.telefono );
				tel.id = idMaxOfContTel++;
			}
			for (   OficioContactoCorreo correo  :  ofCont.correos ) {
				System.out.println("     "+ correo.correo );
				correo.id = idMaxOfContCorr++;
			}
		}
		
		
		
		for (  OficioContacto contacto :aux.contactos   ) {
			
		}
		
		
		nuevo.contactos = aux.contactos;
		
		nuevo.observacion = aux.observacion;
		
		

	//	aux.id = idMax;
	//	aux.save();
		
		
	//	nuevo.productoresSolicitados = aux.productoresSolicitados;

		nuevo.save();
		*/
		aux.save();
		aux.refresh();
		retorno.put("estado", "ok");
		retorno.put("id", aux.id);


		//	flash("success", "Oficio " + oficiorForm.get().oficio + " se agregó");
		//return list();
		//	return redirect( routes.AdminOficioController.list());
		return ok(retorno.toString());

	}

	public static Result edit(Long id) {
		return ok(editForm.render(form(Oficio.class).fill(Oficio.find.byId(id))));
	}

	public static Result update(Long id) throws JSONException {
		JSONObject retorno = new JSONObject();
		retorno.put("estado", "error");
		System.out.println("Desde AdminOficioController.update");
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
		Logger.debug("body "+body.getFiles());
		List<FilePart> archs = body.getFiles();
		Logger.debug("body, num file: "+archs.size());

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

			if (tipo==3){
				OficioMinuta a = new OficioMinuta(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.minutas.add(a);
			}

			if (tipo==4){
				OficioGuion a = new OficioGuion(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.guiones.add(a);
			}

			if (tipo==5){
				OficioEntradaSalida a = new OficioEntradaSalida(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.entradassalida = a;
				o.entradassalida.oficio = o;
			}

			if (tipo==6){
				OficioBitacora a = new OficioBitacora(pa.nombrearchivo, pa.contenttype, pa.contenido);
				o.bitacoras.add(a);
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

}
