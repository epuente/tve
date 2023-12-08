package controllers;

import classes.miCorreo2;
import classes.ColorConsola;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.gobEncabezado;
import views.html.login;
import views.html.operacionNoPermitida;
import views.html.usuarioSinAutenticar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static play.data.Form.form;

public class Application extends Controller {
	public static class Login {
		String usuario;
		String password;

		public String validate() {
			if (Cuenta.autenticar(usuario, password) == null) {
				return "Invalid user or password";
			}
			return null;
		}
	}


	public static Result login() {
		return ok(login.render(form(Login.class)));
	}

	// Recibe como parámetros usuario y password
	// Lo compara contra la DB
	// Regresa JSON del objeto Personal
	// Si es válido almacena en sesion: usuario (id de usuario), nombre (nombre real del usuario),  roles (rol o roles qye tenga asignados)
	public static Result autentica() {
		JsonContext jsonContext = Ebean.createJsonContext();
		session().clear();
		System.out.println("...desde Application.autentica.");
		DynamicForm requestData = form().bindFromRequest();
		String usuario = requestData.get("usuario").trim();
		String password = requestData.get("password").trim();
		Personal u = Cuenta.autenticar(usuario, password);
		if (u != null) {
			System.out.println("Ingresando " + u.nombreCompleto());
			session("usuario", u.id.toString());
			session("nombre", u.nombre);
			ArrayList<Long> arrRoles = new ArrayList<>();
			StringBuilder total = new StringBuilder();
			u.cuentas.forEach(x -> x.roles.forEach(y -> {
				arrRoles.add(y.rol.id);
			}));
			for (Cuenta cta : u.cuentas) {
				for (CuentaRol r : cta.roles) {
					total.append("[").append(r.rol.id).append("]");
				}
			}
			session("roles", total.toString());
			if (u.cuentas.get(0).roles.size()==1)
				session("rolActual", u.cuentas.get(0).roles.get(0).rol.id.toString());

			UsuarioController.registraAcceso(request().path());
			/*
			RegistroAcceso ra = new RegistroAcceso();
			ra.usuario = u;
			ra.ruta = request().path();
			ra.ip = request().remoteAddress();
			ra.save();
			 */
		} else {
			System.out.println( ColorConsola.ANSI_RED_BACKGROUND+"    Usuario y/o password no valido    (usuario: "+usuario+")    "+ColorConsola.ANSI_RESET);
//			Notificacion noti = new Notificacion();
			Logger.info(ColorConsola.ANSI_PURPLE+"Error de login !!!!!!!!!!!!!!!!!"+ColorConsola.ANSI_RESET);

		}
		return ok(jsonContext.toJsonString(u));
	}

	public static Result redirecciona() {
		System.out.println("desde redirecciona..................");
		System.out.println("roles->"+session("roles"));
		System.out.println("rolActual->"+session("rolActual"));

		// Productor
		if (session("rolActual").compareTo("100")==0) {
			return redirect(routes.UsuarioController.tablero());
		}
		//Administrador o Jefe depto
		if (session("rolActual").compareTo("1")==0 || session("rolActual").compareTo("127")==0  ) {
			return redirect(routes.UsuarioController.misServiciosAdmin());
		}
		// Rersponsable Digitalización
		if (session("rolActual").compareTo("11")==0) {
			return redirect(routes.UsuarioController.misServiciosRespDigi());
		}
		// Operador de Sala
		if (session("rolActual").compareTo("16")==0) {
			return redirect(routes.UsuarioController.misServiciosOperaSala());
		}
		// Admon. Equipo y Accesorios
		if (session("rolActual").compareTo("10")==0) {
			return redirect(routes.AdminEAController.tableroEA());
		}
		// Ingeniero
		if (session("rolActual").compareTo("18")==0) {
			return redirect(routes.IngenieriaController.tableroIng());
		}
		// Catalogador de Videoteca
		if (session("rolActual").compareTo("132")==0) {
			return redirect(routes.VideotecaController.tablero());
		}

		// Supervisor de Catalogadores de Videoteca
		if (session("rolActual").compareTo("133")==0) {
			return redirect(routes.SupCatalogadorController.lista());
		}

		return null;
	}


	public static Result rolesUsuario() throws JSONException {
		Ebean.createJsonContext();
		Long usuarioId =  Long.decode(session("usuario"));
		List<CuentaRol> roles = CuentaRol.find.where().eq("cuenta.personal.id", usuarioId).findList();
		//new JSONObject();
		JSONArray jaRoles = new JSONArray();
		for(CuentaRol rol:roles) {
			JSONObject jo = new JSONObject();
			jo.put("id",  rol.rol.id.toString());
			jo.put("descripcion",  rol.rol.descripcion);
			jaRoles.put(jo);
		}
		return ok( jaRoles.toString()  );
	}

	public static Result cambiarRol() {
		System.out.println("Desde Application.cambiarRol");
		JsonNode json = request().body().asJson();
		String rolNuevo = json.findValue("rolId").asText();
		Long usuarioId =  Long.decode(session("usuario"));
		String edo;
		List<CuentaRol> roles = CuentaRol.find.where().eq("cuenta.personal.id", usuarioId).eq("rol.id", Long.decode(rolNuevo)).findList();
		if (roles.size()>0) {
			session("rolActual", rolNuevo);
			System.out.println("Se cambio aal rol: "+rolNuevo);
			edo="ok";
			UsuarioController.registraAcceso(request().path());
			/*
			RegistroAcceso ra = new RegistroAcceso();
			ra.usuario =   Personal.find.setId(usuarioId).findUnique();
			ra.rol = Rol.find.setId(  json.findValue("rolId").asLong() ).findUnique();
			ra.ruta = request().path();
			ra.ip = request().remoteAddress();
			ra.save();
			 */
		} else {
			edo="El usuario no tiene ese rol";
		}
		return ok(    "{\"estado\":\""+edo+"\"}"   );
	}


	public static Result notificaciones() {
		List<PreAgenda> notificaPreagenda = null;
		JSONObject jo = new JSONObject();
		JSONArray listaNot = new JSONArray();

		try {
			// Notificaciones del administrador
			if (session("rolActual").compareTo("1")==0) {


				jo.put("rol", "Administrador");

				// ¿Existe cuenta de salida de correo activa?
				if (     Ctacorreo.find.where().eq("activa", true).findList().size()==0) {
					JSONObject joAux= new JSONObject();
					joAux.put("tipo", "existeCorreoSalida");
					joAux.put("titulo", "No hay cuenta de correo de salida");
					joAux.put("descripcion", "En el menú de Sistema/Correo electrónico administre las cuentas de salida y especifique una de ellas como activa.");
					joAux.put("liga", "/correosSalida");
					listaNot.put(joAux);
				}

				// ¿Existe al menos un productor activo?
				if (CuentaRol.find.where().eq("rol.id", 100).eq("cuenta.personal.activo", "S").findRowCount()==0) {
					JSONObject joAux= new JSONObject();
					joAux.put("tipo", "productorActivo");
					joAux.put("titulo", "No hay un productor actualmente activo");
					joAux.put("descripcion", "No se encontró ningún productor activo.");
					joAux.put("liga", "/personal");
					listaNot.put(joAux);
				}
				jo.put("lista", listaNot);


				//	System.out.println("El admin regresa    " + jo.toString());


			}

			// Notificaciones del productor
			if (session("rolActual").compareTo("100")==0) {
				//¿El productor tiene asignaciones sin atender?
				List<FolioProductorAsignado> fpas = FolioProductorAsignado.find
						.fetch("folio.oficio")
						.where()
						.eq("personal.id", session("usuario"))
						.eq("folio.estado.id", 4).findList();

				//System.out.println("\n\n\n\nSIN ATENDER "+fpas.size());


				// ¿Existen folios asignados sin atender?
				if (fpas.size()>0) {
					JSONObject joAux= new JSONObject();
					joAux.put("tipo", "folioSinAtender");
					joAux.put("titulo", "Folios sin atender");
					joAux.put("descripcion", "Hay "+fpas.size()+" folios sin atender o recién asignados");
					joAux.put("liga", "/usuario/misServicios");
					listaNot.put(joAux);
				}

				jo.put("lista", listaNot);
			}
			// Notificaciones del operador de sala
			if (session("rolActual").compareTo("16")==0) {

				List<PreAgenda> preagendaSala = PreAgenda.find
						.where().eq("personal.id", session("usuario")).findList();


				System.out.println("Tam fpas: " + preagendaSala.size());
				// Edo 4 : Productor asignado
				notificaPreagenda = preagendaSala.stream().filter(f -> f.folioproductorasignado.folio.estado.id == 4).collect(Collectors.toList());


			}
			//System.out.println("Tam edo4: " + notifica.size());

			//System.out.println("notificaciones....."+jsonContext.toJsonString(notificaPreagenda));


			//return ok(jsonContext.toJsonString(notificaPreagenda));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(jo.toString());
	}

	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(
				play.Routes.javascriptRouter("jsRoutes", controllers.routes.javascript.UsuarioController.ajaxDatos()));
	}

	public static Result encabezadoGob() {
		return ok(gobEncabezado.render());

	}

	public static Result operacionNoPermitida() {
		return ok (operacionNoPermitida.render());
	}

	public static Result usuarioSinAutenticar() {
		return ok (usuarioSinAutenticar.render());
	}


	public static Result enviarPasswordCorreo() {
		JsonNode json = request().body().asJson();
		String email = json.findPath("email").asText();
		Calendar hoy = Calendar.getInstance();
		String sHoy = hoy.get(Calendar.DAY_OF_MONTH)+"/"+(hoy.get(Calendar.MONTH)+1)+"/"+hoy.get(Calendar.YEAR);
		String sHora = hoy.get(Calendar.HOUR_OF_DAY)+":"+hoy.get(Calendar.MINUTE)+":"+hoy.get(Calendar.SECOND);
		PersonalCorreo pc= PersonalCorreo.find.where().eq("email", email).findUnique();
		Personal p = pc.personal;
		Cuenta c = p.cuentas.get(0);
		miCorreo2 correo = new miCorreo2();
		Config conf = ConfigFactory.load();
		String url = conf.getString("urlProduccion");
		correo.para = Arrays.asList(pc.email);
		correo.asunto="Clave de acceso al sistema de TVEducativa";
		correo.mensaje="El día "+sHoy+" a las "+sHora+" se solicitó la recuperación de su clave de acceso al sistema de TVEducativa.<br><br>Su nombre de usuario es: <b>"+c.username +"</b> y la clave de acceso es: <b>"+c.password+"</b>";
		correo.mensaje+="<br>";
		correo.mensaje+="<br>";
		correo.mensaje+="Ingrese al <a href='"+url+"' target=\"_blank\">sistema con la siguiente dirección:&nbsp;&nbsp;&nbsp;</a>http://" +url;
		//	correo.mensaje+="<button type='button' onclick='alert('Hello world!')'>Click Me!</button>";

		// OJO Aqui tiene que ser un https
		//	correo.mensaje+=" <a href=\""+"https://"+url+"\" target=\"_blank\">";

		//	correo.mensaje+="<button>Ir al sitio</button>";

		correo.mensaje+="</a>";

		correo.enviar();
		return ok( "{\"estado\": \"ok\"}");
	}

	public static Result existenciaCorreo() {
		JsonNode json = request().body().asJson();
		JSONObject retorno = new JSONObject();

		String email = json.findPath("email").asText();
		List<Personal> direcciones = Personal.find.where().eq("correos.email", email).findList();
		try {
			retorno.put("existe", direcciones.size()!=0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /*
		miCorreo2 correo = new miCorreo2();
		correo.para = Arrays.asList("epuente_72@yahoo.com", "eduardo.puente72@gmail.com");
		correo.asunto="Prueba";
		correo.mensaje="Esta es una prueba de correo enviada desde una aplicación ";
		correo.enviar();
		*/
		return ok( retorno.toString());
	}


	public static Result vacio(){
		return ok(views.html.vacio.render());
	}


}
