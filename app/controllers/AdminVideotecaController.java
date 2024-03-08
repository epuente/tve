package controllers;

import classes.Duracion;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;
import models.videoteca.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.Form;
import play.db.ebean.Model;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Result;
import views.html.videoteca.admin.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.data.Form.form;

public class AdminVideotecaController extends ControladorSeguroAdministrador {


	private static final String noDefinida = "<small><i>No definida</i></small>";
	private static final String noDefinido = "<small><i>No definido</i></small>";

    public static Result GO_HOME = redirect(
            routes.AdminSalaController.list()
        );

	public static Result list(){
		System.out.println("Desde AdminVideotecaController.list");
		return ok (list.render());
    }

	public static Result ajaxList() throws JSONException {
		System.out.println("Desde AdminVideotecaController.ajaxList");
		String filtro = request().getQueryString("search[value]");
		String colOrden =  request().getQueryString("order[0][column]");
		String tipoOrden = request().getQueryString("order[0][dir]");

		System.out.println( "parametros nombre columna a ordenar:"+ request().getQueryString("columns["+colOrden+"][data]"));
		String nombreColOrden = request().getQueryString("columns["+colOrden+"][data]");
		System.out.println("-10");
		List<Sala> ps = Sala.find.all();
		System.out.println("-20");
		int numPag = 0;
		if (Integer.parseInt(request().getQueryString("start")) != 0)
			numPag = Integer.parseInt(request().getQueryString("start")) /   Integer.parseInt(request().getQueryString("length"));
		System.out.println("-30");
        /*
		Page<Sala> pagp = Sala.page(numPag ,
							Integer.parseInt(request().getQueryString("length"))
							, filtro
							, nombreColOrden
							, tipoOrden
							);
		*/
		com.avaje.ebean.Page<VtkCatalogo> pagp = null;
		System.out.println("-40");
		pagp = VtkCatalogo.page(numPag ,
				Integer.parseInt(request().getQueryString("length"))
				, filtro
				, nombreColOrden
				, tipoOrden
		);
		System.out.println("-50");

		System.out.println("-60 pagp "+pagp);

		pagp.getList().forEach(x->{
			System.out.println("id "+x.id);
			for (VtkAreatematica at : x.areastematicas) {
				System.out.println("    " + at);
			}
		});


		System.out.println("Despues del pagp");
		response().setContentType("application/json");
		JSONObject json2 = new JSONObject();
		try {
			json2.put("draw",  request().getQueryString("draw")+1  );
			json2.put("recordsTotal", ps.size());
			json2.put("recordsFiltered", pagp.getTotalRowCount());

			JSONArray losDatos = new JSONArray();
			for( VtkCatalogo p : pagp.getList()  ){
				JSONObject datoP = new JSONObject();
				datoP.put("id", p.id);
				datoP.put("clave", p.clave);
				datoP.put("serie", p.serie==null?"<i>No definida</i>":p.serie.descripcion);
				datoP.put("titulo", p.titulo);
				datoP.put("obra", p.obra);
				datoP.put("catalogador", p.catalogador.nombreCompleto());
				datoP.put("productor", p.validador!=null?p.validador.nombreCompleto()+"&nbsp;&nbsp;<small><a href=\"javascript:catalogoAsignaProd("+p.id+")\" class=\"pull-right\">Cambiar</a></small>":null);
				JSONArray areastematicas = new JSONArray();
				for (VtkAreatematica at : p.areastematicas) {
					if (at.areatematica!=null) {
						JSONObject area = new JSONObject();
						area.put("descripcion", at.areatematica.descripcion);
						areastematicas.put(area);
						if (at.areatematica.id == 99)
							datoP.put("areaTematicaOtra", p.areaTematicaOtra);
					}
				}
				datoP.put("areastematicas", areastematicas);
                /*
				if (p.mantenimiento != null) {
					System.out.println("... # mantos para la sala:"+p.mantenimiento.size());
					Date masCercano = new Date();
					System.out.println("---");
					System.out.println("masCercano "+masCercano);

					p.mantenimiento.sort(new Comparator<SalaMantenimiento>(){
						@Override
						public int compare(SalaMantenimiento s1, SalaMantenimiento s2) {
							return s1.desde.compareTo(s2.desde);													
						}
					});
					
					for (SalaMantenimiento sm : p.mantenimiento){
						Date hoy = Calendar.getInstance().getTime();
						if (sm != null  &&  p.mantenimiento.get(0).hasta.after(hoy))
							datoP.put("manttoProximo", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(p.mantenimiento.get(0).desde) + " - "+new SimpleDateFormat("HH:mm").format(p.mantenimiento.get(0).hasta));
					}					
				}
				 */
				losDatos.put(datoP);
			}

			System.out.println("-30 "+ losDatos);

			if ( pagp.getTotalRowCount()>0 ){
				json2.put("data", losDatos);
				System.out.println("retornando "+ json2.toString()  );
			} else {
				json2.put("data", new JSONArray() );
				return ok( json2.toString()  );
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(json2.toString());
		return ok( json2.toString() );


	}

	public static Result catalogoInfo() throws JSONException {
		Logger.debug("Desde AdminVideotecaController.catalogoInfo");

		JsonNode json = request().body().asJson();
		Long id = json.findValue("id").asLong();

		VtkCatalogo c = VtkCatalogo.find.byId(id);
		JSONObject jo = new JSONObject();
		JSONArray jaEventos = new JSONArray();
		JSONArray jaNiveles = new JSONArray();
		JSONArray jaPalabrasClave = new JSONArray();
		JSONArray jaAreasTematicas = new JSONArray();
		JSONArray jaCreditos = new JSONArray();
		JSONArray jaTimeLine = new JSONArray();

		if (c!=null){
			jo.put("id", c.id);
			jo.put("clave", c.clave);

			for ( VtkEvento e  : c.eventos){
				JSONObject joEvento = new JSONObject();
				joEvento.put("id", e.servicio.id);
				joEvento.put("descripcion", e.servicio.descripcion);
				jaEventos.put(joEvento);
			}
			jo.put("eventos", jaEventos);

			for ( VtkNivel ne  : c.niveles){
				JSONObject joNivel = new JSONObject();
				joNivel.put("id", ne.nivel.id);
				joNivel.put("descripcion", ne.nivel.descripcion);
				jaNiveles.put(joNivel);
			}
			jo.put("niveles", jaNiveles);
			jo.put("titulo", c.titulo);

			for ( VtkAreatematica at  : c.areastematicas){
				if (at.areatematica!=null) {
					JSONObject joAT = new JSONObject();
					joAT.put("id", at.areatematica.id);
					joAT.put("descripcion", at.areatematica.descripcion);
					if (at.areatematica.id==999)
						jo.put("areaTematicaOtra", c.areaTematicaOtra);
					jaAreasTematicas.put(joAT);
				}
			}
			jo.put("areastematicas", jaAreasTematicas);


			jo.put("folio", c.folio!=null&&c.folio.length()>0?c.folio:noDefinido);
			jo.put("instancia", c.unidadresponsable!=null?c.unidadresponsable.nombreLargo: noDefinida);
			jo.put("serie", c.serie!=null?c.serie.descripcion:noDefinida);
			jo.put("folioDEV", c.folioDEV!=null&&c.folioDEV.length()>0?c.folioDEV:noDefinido);
			/*
			if (c.creditos!=null) {
				for (Credito cr : c.creditos) {
					if (cr != null) {
						JSONObject joCR = new JSONObject();
						joCR.put("tipo", cr.tipoCredito.descripcion);
						joCR.put("persona", cr.personas);
						jaCreditos.put(joCR);
					}
				}
				jo.put("creditos", jaCreditos);
			}
			*/

			if (c.creditos!=null) {
				String sql ="select distinct tipo_credito_id, tc.descripcion  " +
						"from credito c inner join tipo_credito tc on c.tipo_credito_id = tc.id  " +
						"where catalogo_id =" + id +
						"order by c.tipo_credito_id ";
				List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
				for (SqlRow sqlRow : sqlRows) {
					JSONObject joGpo = new JSONObject();
					JSONArray jaGpo = new JSONArray();
					List<Credito> creditos = Credito.find.setDistinct(true).where()
							.and(Expr.eq("catalogo_id", id), Expr.eq("tipoCredito.id", sqlRow.getLong("tipo_credito_id")))
							.findList();
					JSONArray jaPersonas = new JSONArray();
					for ( Credito credito :creditos ) {
						JSONObject jo1 = new JSONObject();
						jo1.put("persona", credito.personas);
						jaPersonas.put(jo1);
					}
					joGpo.put("grupo", sqlRow.getString("descripcion"));
					joGpo.put("personas",jaPersonas);
					jaCreditos.put(joGpo);
				}
				jo.put("creditos", jaCreditos);
			}

			jo.put("numobra", c.obra!=null&&c.obra.length()>0&&c.obra.compareTo("__/__")!=0?c.obra:noDefinida);

			Duracion d = new Duracion();
			if (c.duracion!=0) {
				d = new Duracion(c.duracion);
				jo.put("duracion", d.cadena());
			}
			else
				jo.put("duracion", noDefinida);

			if (c.palabrasClave!=null) {
				for (PalabraClave pc : c.palabrasClave) {
					JSONObject joPC = new JSONObject();
					joPC.put("id", pc.id);
					joPC.put("descripcion", pc.descripcion);
					jaPalabrasClave.put(joPC);
				}
				jo.put("palabrasclave", jaPalabrasClave);
			}

			jo.put("produccion", c.produccion!=null?c.produccion.descripcion:noDefinida);
			jo.put("idioma", c.idioma!=null?c.idioma.descripcion:noDefinido);
			jo.put("accesibilidadAudio", c.accesibilidadAudio);
			jo.put("accesibilidadVideo", c.accesibilidadVideo);
			jo.put("grabacion", c.tipograbacion!=null?c.tipograbacion.descripcion:noDefinida);
			jo.put("fechaproduccion", c.fechaProduccion!=null&&c.fechaProduccion.length()>0?c.fechaProduccion:noDefinida);
			jo.put("fechapublicacion", c.fechaPublicacion!=null&&c.fechaPublicacion.length()>0?c.fechaPublicacion:noDefinida);
			jo.put("disponibilidad", c.disponibilidad!=null?c.disponibilidad.descripcion:noDefinida);
			jo.put("formato", c.formato!=null?c.formato.descripcion:noDefinido);

			if (c.timeline!=null) {
				for (VtkTimeLine tl : c.timeline) {
					JSONObject joTL = new JSONObject();
					Duracion d1 = new Duracion();
					Duracion d2 = new Duracion();
					if (tl.desde!=null){
						d1 = new Duracion(tl.desde);
					}
					if (tl.hasta!=null){
						d2 = new Duracion(tl.hasta);
					}
					joTL.put("desde", d1.cadena());
					joTL.put("hasta", d2.cadena());
					joTL.put("nombre", tl.personaje.nombre);
					joTL.put("grado", tl.gradoacademico);
					joTL.put("cargo", tl.cargo);
					joTL.put("titulo", tl.tema);
					jaTimeLine.put(joTL);
				}
				jo.put("timeline", jaTimeLine);
			}

			jo.put("sinopsis", c.sinopsis!=null&&c.sinopsis.length()>0?c.sinopsis:noDefinido);
			jo.put("audio", c.audio!=null?c.audio.descripcion:noDefinida);
			jo.put("calidadaudio", c.calidadAudio!=null&&c.calidadAudio.length()>0?c.calidadAudio:noDefinida);
			jo.put("video", c.video!=null?c.video.descripcion:noDefinido);
			jo.put("calidadvideo", c.calidadVideo!=null&&c.calidadVideo.length()>0?c.calidadVideo:noDefinida);
			jo.put("observaciones", c.observaciones!=null&&c.observaciones.length()>0?c.observaciones:noDefinida);


			if (c.validador!=null)
				jo.put("validador", c.validador.id);
		}

		return ok ( jo.toString()  );

	}

	public static Result asignarProductor() throws JSONException {
		Logger.debug("Desde AdminVideotecaController.asignarProductor");

		JsonNode json = request().body().asJson();
		String clave = json.findValue("clave").asText();
		Long idProductor = json.findValue("idProductor").asLong();
		JSONObject jo = new JSONObject();
		jo.put("estado", "sin asignar");

		VtkCatalogo vc = VtkCatalogo.find.where().eq("clave", clave).findUnique();
		if (vc != null ){
			vc.validador = Personal.find.byId(idProductor);
			vc.update();
			jo.put("estado", "correcto");
		}


		return ok (jo.toString());

	}
}
