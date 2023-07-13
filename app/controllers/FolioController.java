package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class FolioController extends Controller{

    public static Result ajaxBuscarOficioPorId(Long id){
		System.out.println(  "Llegando a FolioController.ajaxBuscarOficioPorId"  );
		System.out.println(  "Buscando "+id);
		JsonContext jsonContext = Ebean.createJsonContext();
		List<Oficio> x = Oficio.find
				.fetch("servicios.servicio")
				.fetch("urremitente")
				.fetch("contactos")
				.fetch("contactos.telefonos")
				.fetch("contactos.correos")
				.fetch("fechagrabaciones")
				.fetch("productoresSolicitados.personal")
				.fetch("folios")
				.fetch("folios.productoresAsignados.personal")
				.where().eq("id", id ).findList();
		return ok( jsonContext.toJsonString(x));
    }

	public static Result ajaxBuscarOficio(String numOficio){
		System.out.println(  "Llegando a FolioController.ajaxBuscarOficio"  );
		System.out.println(  "Buscando "+numOficio  );
		JsonContext jsonContext = Ebean.createJsonContext();
		List<Oficio> x = Oficio.find
				.fetch("servicios.servicio")
				.fetch("urremitente")
				.fetch("productoresSolicitados.personal")
				.fetch("folios")
				.fetch("folios.productoresAsignados.personal")
				.where().eq("oficio", numOficio ).findList();
		return ok( jsonContext.toJsonString(x));
    }
    
    
    public static Result ajaxBuscarFolio(String numFolio){
		System.out.println(  "Llegando a FolioController.ajaxBuscarFolio..."  );
		System.out.println(  "Buscando "+numFolio  );
		JsonContext jsonContext = Ebean.createJsonContext();
		Folio x = Folio.find
				.fetch("oficio")
				.fetch("productoresAsignados")
				.where().eq("numfolio", numFolio ).findUnique();
		return ok( jsonContext.toJsonString(x));
    }    
    
    
    
    //Busca los servicios del folio, por id del folio y por id del estado
	@SuppressWarnings("deprecation")
	public static Result ajaxBuscaServiciosFolio() {
		List<FolioProductorAsignado> retornoFPA = null;
		JsonContext jsonContext = Ebean.createJsonContext();
		Logger.info("\n\n\n...........................   desde FolioController.ajaxBuscaServiciosFolio ");
		JsonNode json = request().body().asJson();
		Long[] roles = {1L, 10L, 127L};
		Logger.debug(String.valueOf(json));

//		System.out.println("session "+session("roles"));

		Long id = json.findPath("folioId").asLong();
		long estadoId = json.findPath("estadoId").asLong();
		json.findPath("estadoId").asText();
		List<FolioProductorAsignado> retorno = new ArrayList<>();
		Logger.debug("id:"+id);
		ExpressionList<FolioProductorAsignado> elFpa = FolioProductorAsignado.find
				.fetch("folio")
				.fetch("folio.oficio")
				.fetch("folio.oficio.contactos")
				.fetch("folio.oficio.fechagrabaciones")
				.fetch("folio.oficio.servicios")
				.fetch("folio.oficio.servicios.servicio")
				.fetch("folio.oficio.productoresSolicitados")
				.fetch("folio.oficio.productoresSolicitados.personal")
				.fetch("folio.productoresAsignados")
				.fetch("folio.productoresAsignados.personal")
				.fetch("folio.oficio.urremitente")
				.fetch("folio.oficio.contactos")
				.fetch("folio.oficio.contactos")
				.fetch("folio.oficio.contactos.telefonos")
				.fetch("folio.oficio.contactos.correos")

				.fetch("personal")
				.fetch("preagendas")
				.fetch("preagendas.fase")
				.fetch("preagendas.salidas")
				.fetch("preagendas.salas")
				.fetch("preagendas.salas.sala")
				.fetch("preagendas.salas.usoscabina")
				.fetch("preagendas.operadoresSala")
				.fetch("preagendas.operadoresSala.personal")
				.fetch("preagendas.estado")
				.fetch("preagendas.locaciones")
				.fetch("preagendas.juntas")
				.fetch("preagendas.vehiculos")
				.fetch("preagendas.personal")
				.fetch("preagendas.personal.rol")
				.fetch("preagendas.locutores")
				.fetch("preagendas.locutores.personal")
				.fetch("preagendas.equipos")
				.fetch("preagendas.equipos.equipo")
				.fetch("preagendas.equipos.equipo.estado")
				.fetch("preagendas.accesorios")
				.fetch("preagendas.accesorios.accesorio")
				.fetch("preagendas.accesorios.accesorio.estado")
				.fetch("preagendas.formatos")
				.fetch("preagendas.formatos.formato")
				.fetch("preagendas.cancelaciones")
				.fetch("preagendas.cancelaciones.motivo")

				.fetch("agenda.ingsAdmonEqpo")

				.fetch("agenda")
				.fetch("agenda.fase")
				.fetch("agenda.salidas")
				.fetch("agenda.estado")
				.fetch("agenda.locaciones")
				.fetch("agenda.juntas")
				.fetch("agenda.salas")
				.fetch("agenda.salas.sala")
				.fetch("agenda.salas.usoscabina")
				.fetch("agenda.vehiculos")
				.fetch("agenda.vehiculos.vehiculo")
				.fetch("agenda.cuentaRol")
				.fetch("agenda.cuentaRol.cuentarol")
				.fetch("agenda.cuentaRol.cuentarol.cuenta")
				.fetch("agenda.cuentaRol.cuentarol.cuenta.personal")
				.fetch("agenda.equipos")
				.fetch("agenda.equipos.equipo")
				.fetch("agenda.equipos.equipo.estado")
				.fetch("agenda.accesorios")
				.fetch("agenda.accesorios.accesorio")
				.fetch("agenda.accesorios.accesorio.estado")
				.fetch("agenda.formatos")
				.fetch("agenda.formatos.formato")
				.fetch("agenda.cancelaciones")
				.fetch("agenda.cancelaciones.motivo")
				.filterMany("preagendas.estado").in("id", Arrays.asList(4, 5))
				.filterMany("agenda.estado").ge("id", Arrays.asList(7, 8, 11, 12, 13, 14, 15, 17))
				.where()
				.eq("folio.id", id);
		//.orderBy("preagendas.inicio asc, agenda.inicio asc");


		String strQuery = "select * " +
				"from folio_productor_asignado fpa " +
				"inner join agenda a on fpa.id = a.folioproductorasignado_id " +
				"inner join folio f on fpa.folio_id = f.id " +
				"inner join oficio o on f.oficio_id = o.id " +
				"inner join personal p on fpa.personal_id = p.id " +
				"inner join unidad_responsable ur on o.urremitente_id = ur.id " +
				"where fpa.folio_id = "+id+" ";




		// Administrador o jefe de depto
		if (Arrays.asList(roles).contains(Long.parseLong(session("rolActual")))) {
		}
		// Es administrador?
		if (session("rolActual").compareTo("1") == 0) {
			Logger.debug("SOY ADMINISTRADOR ");
			retorno = elFpa.findList();
		}


		// Productor
		if (session("rolActual").compareTo("100") == 0) {
			retorno = elFpa.findList();
		}

		// Operador de sala
		if (session("rolActual").compareTo("16") == 0) {
			Logger.debug("Operador de sala");


			Personal p = Personal.find
					.where().eq("id", session("usuario"))
					.findUnique();
			List<Long> salas = OperadorSala.find
					.where().eq("personal.id", p.id)
					.findList().stream().map(f->f.sala.id).collect(Collectors.toList());

			Logger.debug("-----------------------------");

			elFpa.filterMany("preagendas")
					.isNotNull("salas")
					.in("estado.id", Arrays.asList(4, 5))
					.in("salas.sala.id",salas);
			Logger.debug("-----------------------------");
			List<PersonalHorario> hs = p.horarios;
			for (PersonalHorario h : hs) {
				System.out.println(".. "+h.desde.getTime()+" "+h.hasta);
				Calendar c1 = Calendar.getInstance();
				Calendar c2 = Calendar.getInstance();

				c1.setTime(h.desde);
				c1.clear(Calendar.MILLISECOND);
				c2.setTime(h.hasta);
				c2.clear(Calendar.MILLISECOND);
                /*
				elFpa.filterMany("preagendas")
						.isNotNull("salas")
						.in("estado.id", Arrays.asList(4, 5))
						.in("salas.sala.id", salas);



				elFpa.filterMany("agenda")
						.isNotNull("salas").in("estado.id", Arrays.asList(7, 11, 13, 14, 99, 100))
						.in("salas.sala.id", salas);
				 */

			}


			retornoFPA = elFpa.findList();
			Logger.debug("\n\n\nResult set para operador de sala tam    (retornoFPA.size):" + retornoFPA.size());
			List<Long> idsPreagenda = new ArrayList<>();
			List<Long> idsAgenda = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			for (PersonalHorario personaH : hs) {
				Logger.debug("operador "+ personaH.personal.id +" - "+personaH.personal.nombreCompleto() );
				for (FolioProductorAsignado fpa : retornoFPA) {
					Calendar c1 = Calendar.getInstance();
					c1.set(Calendar.HOUR_OF_DAY, personaH.desde.getHours());
					c1.set(Calendar.MINUTE, personaH.desde.getMinutes());
					c1.set(Calendar.SECOND, personaH.desde.getSeconds());
					c1.clear(Calendar.MILLISECOND);
					c1.clear(Calendar.DAY_OF_MONTH);
					c1.clear(Calendar.MONTH);
					c1.clear(Calendar.YEAR);

					Calendar c2 = Calendar.getInstance();
					c2.set(Calendar.HOUR_OF_DAY, personaH.hasta.getHours());
					c2.set(Calendar.MINUTE, personaH.hasta.getMinutes());
					c2.set(Calendar.SECOND, personaH.hasta.getSeconds());
					c2.clear(Calendar.MILLISECOND);
					c2.clear(Calendar.DAY_OF_MONTH);
					c2.clear(Calendar.MONTH);
					c2.clear(Calendar.YEAR);

					int diaSemana = personaH.diasemana;


					System.out.println("    operador Dia "+diaSemana+" desde " +sdf.format(c1.getTime()) + " hasta "+sdf.format(c2.getTime()));


					for (PreAgenda pre : fpa.preagendas) {
						Calendar preInicio = Calendar.getInstance();
						Calendar ageFin = Calendar.getInstance();
						preInicio.setTime(pre.inicio);
						preInicio.clear(Calendar.MILLISECOND);
						ageFin.setTime(pre.fin);
						ageFin.clear(Calendar.MILLISECOND);
						// Se iguala c1 y c2 al mismo día que esta programado el servicio
						c1.set(Calendar.DAY_OF_MONTH, preInicio.get(Calendar.DAY_OF_MONTH));
						c1.set(Calendar.MONTH, preInicio.get(Calendar.MONTH));
						c1.set(Calendar.YEAR, preInicio.get(Calendar.YEAR));
						c2.set(Calendar.DAY_OF_MONTH, ageFin.get(Calendar.DAY_OF_MONTH));
						c2.set(Calendar.MONTH, ageFin.get(Calendar.MONTH));
						c2.set(Calendar.YEAR, ageFin.get(Calendar.YEAR));

						Calendar preFin = Calendar.getInstance();
						preFin.setTime(pre.fin);
						if (preInicio.get(Calendar.DAY_OF_WEEK) != diaSemana) {
							//			System.out.println("No es el día ");
							//			System.out.println("    id preagenda "+pre.id);

						} else {
							//				System.out.println("Comparando "+ formato.format(preInicio.getTime())+" contra "+ formato.format(c1.getTime())+" y "+formato.format(c2.getTime()));
							//				System.out.println((preInicio.compareTo(c1) + "   "+  preInicio.compareTo(c2) ));
							//	System.out.println((preInicio.before(c2) || preInicio.equals(c2)));
							if (((preInicio.after(c1) || preInicio.equals(c1)) && (preInicio.before(c2) || preInicio.equals(c2)))
									&&
									((ageFin.after(c1) || ageFin.equals(c1)) && (ageFin.before(c2) || ageFin.equals(c2)))

							) {
								idsPreagenda.add(pre.id);
								//		System.out.println("cumple");
							}
						}
					}

					for (Agenda age : fpa.agenda) {
						Calendar ageInicio = Calendar.getInstance();
						Calendar ageFin = Calendar.getInstance();

						ageInicio.setTime(age.inicio);
						ageInicio.clear(Calendar.MILLISECOND);
						ageFin.setTime(age.fin);
						ageFin.clear(Calendar.MILLISECOND);
						// Se iguala c1 y c2 al mismo día que esta programado el servicio
						c1.set(Calendar.DAY_OF_MONTH, ageInicio.get(Calendar.DAY_OF_MONTH));
						c1.set(Calendar.MONTH, ageInicio.get(Calendar.MONTH));
						c1.set(Calendar.YEAR, ageInicio.get(Calendar.YEAR));
						c2.set(Calendar.DAY_OF_MONTH, ageFin.get(Calendar.DAY_OF_MONTH));
						c2.set(Calendar.MONTH, ageFin.get(Calendar.MONTH));
						c2.set(Calendar.YEAR, ageFin.get(Calendar.YEAR));

						Calendar preFin = Calendar.getInstance();
						preFin.setTime(age.fin);
						if (ageInicio.get(Calendar.DAY_OF_WEEK) != diaSemana) {
							//						System.out.println("No es el día ");
							//						System.out.println("    id preagenda "+age.id);

						} else {
//							System.out.println("Comparando "+ formato.format(ageInicio.getTime())+" contra "+ formato.format(c1.getTime())+" y "+formato.format(c2.getTime()));
//							System.out.println((ageInicio.compareTo(c1) + "   "+  ageInicio.compareTo(c2) ));

							if (((ageInicio.after(c1) || ageInicio.equals(c1)) && (ageInicio.before(c2) || ageInicio.equals(c2)))
									&&
									((ageFin.after(c1) || ageFin.equals(c1)) && (ageFin.before(c2) || ageFin.equals(c2)))

							) {
								idsAgenda.add(age.id);
								//					System.out.println("cumple");
							}
						}
					}

				}
			}
            /*
			System.out.println("tam retornoFPA "+retornoFPA.size());
			
			System.out.println("ids pre "+idsPreagenda.size());
			System.out.println("ids age "+idsAgenda.size());
			System.out.println("antes 0 pre "+retornoFPA.get(0).preagendas.size());
			System.out.println("antes 0 age "+retornoFPA.get(0).agenda.size());
			*/
			// Se filtra, unicamente quedan los servicios que son del operador, en su turno
			List<PreAgenda> otro = retornoFPA.stream()
					.flatMap(fpa -> fpa.preagendas.stream())
					.filter(ff -> idsPreagenda.contains(ff.id))
					.collect(Collectors.toList());

			List<Agenda> otro2 = retornoFPA.stream()
					.flatMap(fpa -> fpa.agenda.stream())
					.filter(ff -> idsAgenda.contains(ff.id))
					.collect(Collectors.toList());


			retornoFPA.get(0).preagendas = otro;
			retornoFPA.get(0).agenda = otro2;

			elFpa.filterMany("preagendas").eq("id", 81);


			retorno = elFpa.findList();
		}


		//	retornoFPA = elFpa.findList();


		// Ingeniero admon Equipo y accesorios
		if (session("rolActual").compareTo("10") == 0) {
			Logger.debug("SOY INGE ("+session("usuario")+")");

			if (json.findPath("estadoId").asText().compareTo("*")==0) {
				Logger.debug("SOY ESTADO *");
				retorno = elFpa.query()
						.fetch("agenda.fase")
						.where().eq("agenda.fase.id", 2L)
						.filterMany("agenda").eq("fase.id",2L)
						.findList();
				//Logger.debug(elFpa.query().getGeneratedSql());
				Logger.debug("Fin de query de  *");

				List<SqlRow> sqlRows = Ebean.createSqlQuery(strQuery).findList();

			}


			if (estadoId==7){
				Logger.debug("SOY ESTADO 7");
				retorno = elFpa.query()
						.where()
						//.eq("agenda.ingsAdmonEqpo.ingeniero.id",  session("usuario"))
						//.eq("folio.estado.id",7)
						.filterMany("agenda").eq("estado.id", 7)
						.filterMany("agenda").eq("fase.id", 2)
						.filterMany("agenda.equipos").isNull("autorizo.id")
						.filterMany("agenda.accesorios.accesorio").isNull("autorizo")

						.findList();
			}

			if (estadoId==8){
				Logger.debug("SOY ESTADO 8");
				retorno = elFpa.query()
						.where()
						.eq("agenda.ingsAdmonEqpo.ingeniero.id",  session("usuario"))
						.filterMany("agenda").eq("estado.id", 8)
						.filterMany("agenda").eq("fase.id", 2)

						.filterMany("agenda.equipos").isNotNull("autorizo.id")
						.filterMany("agenda.accesorios.accesorio").isNotNull("autorizo")
						.findList();
			}
		}

		//System.out.println("\n... query: "+elFpa.query().getGeneratedSql()+"\n");


		retorno.forEach( f->f.folio.actualizaEstados()  );

		//System.out.println("retornando: "+ retorno.size()+" registros");
		//System.out.println("json : "+ jsonContext.toJsonString(retorno));

		return ok(  jsonContext.toJsonString(retorno) );
	}


	//Busca los servicios del folio, por id (id de preagenda o agenda) y por id del estado
	public static Result ajaxBuscaDetallesServicio(){
		JsonContext jsonContext = Ebean.createJsonContext();
		System.out.println("   ...........................   desde FolioController.ajaxBuscaDetallesServicio ");
		JsonNode json = request().body().asJson();
		System.out.println(json);
		Long id = json.findPath("servicioId").asLong();
		//	Long fpaId = json.findPath("fpaId").asLong();
		String tipo = json.findPath("tipo").toString();

		tipo = tipo.replace("\"", "");
System.out.println("id:"+id);
		System.out.println("tipo:"+tipo);
//System.out.println("fpaId:"+fpaId);


		Query<FolioProductorAsignado> q = FolioProductorAsignado.find
				.fetch("folio")
				.fetch("folio.productoresAsignados")
				.fetch("folio.productoresAsignados.personal")
				.fetch("folio.productoresAsignados.personal.cuentas")
				.fetch("folio.productoresAsignados.personal.cuentas.roles")
				.fetch("folio.productoresAsignados.personal.cuentas.roles.rol")
				.fetch("folio.oficio")
				.fetch("folio.oficio.contactos")
				.fetch("folio.oficio.fechagrabaciones")
				.fetch("folio.oficio.servicios")
				.fetch("folio.oficio.servicios.servicio")
				.fetch("folio.oficio.productoresSolicitados")
				.fetch("folio.oficio.productoresSolicitados.personal")
				.fetch("folio.oficio.urremitente")
				.fetch("folio.oficio.contactos")
				.fetch("folio.oficio.contactos.telefonos")
				.fetch("folio.oficio.contactos.correos");


		if (tipo.compareTo("preagenda")==0) {
			System.out.println("es preagenda");
			//			q.where()
			//			.eq("id", fpaId);
			if (id!=0) {
				System.out.println("id != 0");
				q.fetch("preagendas")
						.fetch("preagendas.fase")
						.fetch("preagendas.salidas")
						.fetch("preagendas.estado")

						.fetch("preagendas.personal")
						.fetch("preagendas.personal.rol")
						.fetch("preagendas.locutores")
						.fetch("preagendas.locutores.personal")
						.fetch("preagendas.accesorios")
						.fetch("preagendas.accesorios.accesorio")
						.fetch("preagendas.equipos")
						.fetch("preagendas.equipos.equipo")
						.fetch("preagendas.estado")
						.fetch("preagendas.locaciones")
						.fetch("preagendas.salas")
						.fetch("preagendas.salas.sala")
						.fetch("preagendas.salas.usoscabina")
						.fetch("preagendas.formatos")
						.fetch("preagendas.formatos.formato")
						.fetch("preagendas.vehiculos")
						.fetch("preagendas.juntas")
						.where()
						//	.eq("id", fpaId)
						.eq("preagendas.id", id)
						.in("preagendas.estado.id", Arrays.asList(4,5))
						.filterMany("preagendas").eq("id", id)
						.filterMany("preagendas.estado").in("id", Arrays.asList(4,5)  )
						.orderBy("preagendas.inicio");
			} else {
				System.out.println("Solo info del oficio y folio");
			}

		}
		if (tipo.compareTo("agenda")==0) {
			q.fetch("agenda")
					.fetch("agenda.fase")
					.fetch("agenda.salidas")
					.fetch("agenda.estado")

					.fetch("agenda.cuentaRol")
					.fetch("agenda.cuentaRol.cuentarol")
					.fetch("agenda.cuentaRol.cuentarol.cuenta")
					.fetch("agenda.cuentaRol.cuentarol.cuenta.personal")

					.fetch("agenda.accesorios")
					.fetch("agenda.accesorios.accesorio")
					.fetch("agenda.equipos")
					.fetch("agenda.equipos.equipo")
					.fetch("agenda.estado")
					.fetch("agenda.locaciones")
					.fetch("agenda.salas")
					.fetch("agenda.salas.sala")
					.fetch("agenda.salas.usoscabina")
					.fetch("agenda.formatos")
					.fetch("agenda.formatos.formato")
					.fetch("agenda.vehiculos")
					.fetch("agenda.vehiculos.vehiculo")
					.fetch("agenda.juntas")
					.where()
					.eq("agenda.id", id)
					.ge("agenda.estado.id", 7)
					.filterMany("agenda").eq("id", id)
					//.filterMany("agenda.estado").ge("id", 7)
					.orderBy("agenda.inicio");
		}


		System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
		//FolioProductorAsignado fpa2 = q.findUnique();
		List<FolioProductorAsignado> fpa2 = q.findList();
		System.out.println("query:" +q.getGeneratedSql());
		System.out.println("registros:"+q.findList().size());
		System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
		String cadena = jsonContext.toJsonString(fpa2);
		System.out.print(cadena);
		return ok (cadena);
	}




}
