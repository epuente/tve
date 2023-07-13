package controllers;

import java.text.SimpleDateFormat;
import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;
import com.avaje.ebean.text.json.JsonContext;
import com.fasterxml.jackson.databind.JsonNode;

import models.Agenda;
import models.Sala;
import org.json.JSONArray;
import play.mvc.Result;
import views.html.reportes.*;
import models.FolioProductorAsignado;


public class ReporteController extends ControladorSeguro {
    public static Result GO_HOME = redirect(
            routes.ReporteController.list()
        );		

	public static Result list(){
		return ok(reporteProductor.render( )   );
		
	}	
	

	public static Result productorAnio() {
		System.out.println("productorAnio");
		JsonNode json = request().body().asJson();
		JsonContext jsonContext = Ebean.createJsonContext();
		System.out.println(json);
		
		
		Calendar primerDiaAnio = Calendar.getInstance();
		Calendar ultimoDiaAnio = Calendar.getInstance();
		Calendar primerDiaSemestre1 = Calendar.getInstance();
		Calendar ultimoDiaSemestre1 = Calendar.getInstance();
		Calendar primerDiaSemestre2 = Calendar.getInstance();
		Calendar ultimoDiaSemestre2 = Calendar.getInstance();
		Calendar primerDiaTrimestre = Calendar.getInstance();
		Calendar ultimoDiaTrimestre = Calendar.getInstance();		
		Calendar mes = Calendar.getInstance();
		
		primerDiaAnio.set(json.findPath("anio").asInt(), 0, 1);
		ultimoDiaAnio.set(json.findPath("anio").asInt(), 11, 31);
		primerDiaSemestre1.set(json.findPath("anio").asInt(), 0, 1);
		ultimoDiaSemestre1.set(json.findPath("anio").asInt(), 5, 30);
		primerDiaSemestre2.set(json.findPath("anio").asInt(), 6, 1);
		ultimoDiaSemestre2.set(json.findPath("anio").asInt(), 11, 31);
		primerDiaTrimestre.set(json.findPath("anio").asInt(), json.findPath("parametro").asInt()*3, 1);
		
		
		
		
		System.out.println("primerDiaAnio:"+primerDiaAnio.getTime()  );
		System.out.println("primerDiaSemestre1:"+primerDiaSemestre1.getTime()  );
		System.out.println("ultimoDiaSemestre1:"+ultimoDiaSemestre1.getTime()  );	
		
		System.out.println("parametro: "+  json.findPath("parametro").asInt()  );
		
		System.out.println("parametro1?: "+  (json.findPath("parametro").asInt()==0)  );
		System.out.println("parametro2?: "+  (json.findPath("parametro").asInt()==1)  );
		
		Query<FolioProductorAsignado> x = FolioProductorAsignado.find
				.select("*, t0.listaEstadosServicios()")
				.fetch("folio")
				.fetch("folio.oficio")
				.fetch("folio.estado")
				.fetch("folio.oficio.urremitente")
				.fetch("personal")
				.fetch("personal.tipo")
				.fetch("personal.tipocontrato")
				.fetch("preagendas")
				.fetch("preagendas.estado")
				.fetch("preagendas.fase")			
				
				.fetch("preagendas.personal")
				.fetch("preagendas.personal.cuentas.roles")
				.fetch("preagendas.locutores")
				.fetch("preagendas.locutores.personal")
				.fetch("preagendas.accesorios")
				.fetch("preagendas.accesorios.accesorio")
				.fetch("preagendas.equipos")
				.fetch("preagendas.equipos.equipo")
				.fetch("preagendas.locaciones")
				.fetch("preagendas.salas")
				.fetch("preagendas.salas.usoscabina")
				.fetch("preagendas.salidas")
				.fetch("preagendas.formatos")
				.fetch("preagendas.vehiculos")
				.fetch("preagendas.juntas")
				.fetch("preagendas.cancelaciones")
				
				.fetch("agenda")
				.fetch("agenda.salidas")
				.fetch("agenda.accesorios")
				.fetch("agenda.accesorios.accesorio")
				.fetch("agenda.equipos")
				.fetch("agenda.equipos.equipo")
				.fetch("agenda.formatos")
				.fetch("agenda.locaciones")
				.fetch("agenda.salas")
				.fetch("agenda.salas.sala")
				.fetch("agenda.salas.usoscabina")
				.fetch("agenda.vehiculos")
				.fetch("agenda.vehiculos.vehiculo")
				.fetch("agenda.juntas")
				.fetch("agenda.personal")
				.fetch("agenda.personal.personal")
				.fetch("agenda.personal.personal.tipo")
				.fetch("agenda.cancelaciones")
				
				.fetch("agenda.estado")
				.fetch("agenda.fase")
				
				.where()
				.eq("personal.id",  json.findPath("personalId").asLong())
				
			//	.between("t0.audit_insert", primerDiaAnio.getTime(), ultimoDiaAnio.getTime())

				.orderBy().asc("t0.audit_insert");
		
				// Alcance: Año
				if (json.findPath("alcanceConsulta").asInt()==0) {
					x.where().between("t0.audit_insert", primerDiaAnio.getTime(), ultimoDiaAnio.getTime());
				}
				// Alcance: Semestre
				if (json.findPath("alcanceConsulta").asInt()==1) {
					System.out.println("Alcance mes");
					if (json.findPath("parametro").asInt()==0) {	
						System.out.println("primer semestre");
						x.where().between("t0.audit_insert", primerDiaSemestre1.getTime(), ultimoDiaSemestre1.getTime());
					}
					if (json.findPath("parametro").asInt()==1) {
						System.out.println("segundo semestre");
						x.where().between("t0.audit_insert", primerDiaSemestre2.getTime(), ultimoDiaSemestre2.getTime());
					}
				}
				// Alcance: Trimestre
				if (json.findPath("alcanceConsulta").asInt()==2) {
					System.out.println("Alcance trimestre");
					
					
					Calendar mesUltimoTrimestre = Calendar.getInstance();
					mesUltimoTrimestre.set(primerDiaTrimestre.get(Calendar.YEAR), primerDiaTrimestre.get(Calendar.MONTH)+2, 1);
					
					ultimoDiaTrimestre.set(mesUltimoTrimestre.get(Calendar.YEAR),  mesUltimoTrimestre.get(Calendar.MONTH),  mesUltimoTrimestre.getActualMaximum(Calendar.DAY_OF_MONTH) );
					System.out.println("inicio trimestre : "+primerDiaTrimestre.getTime()+"  fin de trimestre: "+ultimoDiaTrimestre.getTime());
					
					x.where().between("t0.audit_insert", primerDiaTrimestre.getTime(), ultimoDiaTrimestre.getTime());
					
					
				}
				
				// Alcance: Mes
				if (json.findPath("alcanceConsulta").asInt()==3) {
					System.out.println("Alcance mes");
					mes.set(json.findPath("anio").asInt(), json.findPath("parametro").asInt(), 1);
					Calendar mes2 = Calendar.getInstance();
					mes2.set(mes.get(Calendar.YEAR), mes.get(Calendar.MONTH), mes.getActualMaximum(Calendar.DAY_OF_MONTH));
					
					mes.set(json.findPath("anio").asInt(), json.findPath("parametro").asInt(), 1);
					System.out.println("inicio mes : "+mes.getTime()+"  fin de mes: "+mes2.getTime());

					x.where().between("t0.audit_insert", mes.getTime(), mes2.getTime());
					
				}
				
		List<FolioProductorAsignado> fpas = x.findList();
		System.out.println("query:"+x.getGeneratedSql());		
	//	System.out.println ( jsonContext.toJsonString(fpas)  );
				System.out.println( fpas.size() +" registros"   );
		//		System.out.println( fpas.get(0).listaEstadosServicios()  );
				

				
				
		return ok ( jsonContext.toJsonString(fpas)   );
		
	}


	public static Result sabana(){
		UsuarioController.registraAcceso(request().path());
		return ok (views.html.usuario.sabana.render());
	}


	public static int numSemana(Calendar fecha) {
		/* Lo siguioente es para que funcione correctamente el calculo de la semana del año*/
		Calendar cl = Calendar. getInstance(Locale.GERMAN);
		cl.setTime( fecha.getTime() );
		return cl.get(Calendar.WEEK_OF_YEAR);
	}


	public static Result ajaxSabanaNavega() {
		JsonNode json = request().body().asJson();
		System.out.println("desde UsuarioController.ajaxSabanaNavega");
		System.out.println(json);
		String unidad = json.findPath("unidad").asText();
		int cantidad = json.findPath("cantidad").asInt();
		String f = json.findPath("fecha").asText();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar fecha = Calendar.getInstance(Locale.GERMAN);
		if (  unidad.compareTo("hoy") == 0  ) {
			fecha.setTime(new Date());
		} else {
			String[] arrStringFecha = f.split("/");
			System.out.println("   arrStringFecha: "+arrStringFecha[2]);
			System.out.println("   arrStringFecha: "+arrStringFecha[1]);
			System.out.println("   arrStringFecha: "+arrStringFecha[0]);
			fecha.set(Integer.parseInt(arrStringFecha[2]), Integer.parseInt(arrStringFecha[1]), Integer.parseInt(arrStringFecha[0]));
			System.out.println("   fecha con enteros: "+ sdf.format(fecha.getTime()));
		}
		if (unidad.compareTo("mes")==0) {
			System.out.println("Aumentando el mes "+cantidad);
			fecha.add(Calendar.MONTH, cantidad);
		}
		System.out.println(unidad);
		System.out.println(unidad.length());
		System.out.println(unidad.equals("anio"));
		// HOy
		if (unidad.compareTo("hoy")==0) {
			System.out.println("Fijando a la fecha a la actual");
			fecha.setTime(  new Date()  );
		}
		if (unidad.compareTo("anio")==0) {
			System.out.println("Aumentando el año "+cantidad);
			fecha.add(Calendar.YEAR, cantidad);
		}
		System.out.println("fecha "+sdf.format(fecha.getTime()));

		if (unidad.compareTo("trimestre")==0){
			System.out.println("Aumentando el trimestre "+cantidad);
			fecha.add(Calendar.MONTH, cantidad*3);
		}

		int nSemana = numSemana(fecha);
		Calendar auxSemana = Calendar. getInstance();
		auxSemana.setTime(fecha.getTime());
		auxSemana.set(Calendar.WEEK_OF_YEAR, nSemana);
		auxSemana.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		// Calcular la ultima semana del mes (de fecha)

		Calendar ultimoDiaMes = Calendar.getInstance();
		ultimoDiaMes.set(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("fecha: "+ sdf.format(fecha.getTime()));
		System.out.println("Ultimo dia del mes: " + fecha.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("fecha.getActualMaximum(Calendar.MONTH): "+ sdf.format(  fecha.getActualMaximum(Calendar.MONTH)  ));
		System.out.println("ultimoDiaMes: "+ sdf.format(ultimoDiaMes.getTime()));
		Calendar fechaCiclo = Calendar.getInstance();
		fechaCiclo.setTime(auxSemana.getTime());
		System.out.println("fechaCilo "+sdf.format(fechaCiclo.getTime()));
		String cad;
		StringBuilder sb = new StringBuilder();
		System.out.println("------------------------------------");
		while ( fechaCiclo.before(ultimoDiaMes) ) {
			Calendar auxHasta2 = Calendar.getInstance();
			auxHasta2.setTime(fechaCiclo.getTime());
			auxHasta2.add(Calendar.DATE, 6);
			System.out.println("Semana: "+numSemana(fechaCiclo));

			//cad+="{\"nsemana\":"+numSemana(fechaCiclo)+", \"desde\":\""+sdf.format(fechaCiclo.getTime())+"\", \"hasta\":\""+sdf.format(auxHasta2.getTime())+"\"},";
			sb.append("{\"nsemana\":").append(numSemana(fechaCiclo)).append(", \"desde\":\"").append(sdf.format(fechaCiclo.getTime())).append("\", \"hasta\":\"").append(sdf.format(auxHasta2.getTime())).append("\"},");
			fechaCiclo.add(Calendar.DATE, 6);
			System.out.println("fechaCiclo despues "+sdf.format(fechaCiclo.getTime()));
			fechaCiclo.add(Calendar.DATE, 1);

			System.out.println("numSemana(fechaCiclo) "+numSemana(fechaCiclo));
			System.out.println("\n");
		}
		cad = sb.toString();
		System.out.println("------------------------------------");
		System.out.println(cad);
		if (cad.length()>0)
			cad = cad.substring(0, cad.length()-1);
		System.out.println("Trimestre "+fecha.get(Calendar.MONTH)/3);
		System.out.println(cad);
		System.out.println(  "{\"anio\":"+fecha.get(Calendar.YEAR)+", \"mes\":"+fecha.get(Calendar.MONTH)+", \"numSemana\":"+nSemana+", \"fechas\":["+cad+"]}"       );
		return ok( "{\"anio\":"+fecha.get(Calendar.YEAR)+", \"mes\":"+fecha.get(Calendar.MONTH)+", \"numSemana\":"+nSemana+", \"fechas\":["+cad+"],\"trimestre\":"+fecha.get(Calendar.MONTH)/3+" }"    );
	}


	public static Result ajaxSabana() {
		JsonNode json = request().body().asJson();
		JsonContext jsonContext = Ebean.createJsonContext();
		System.out.println("x");
		System.out.println(json);
		int anio = json.findPath("anio").asInt();
		int nSemana = json.findPath("nSemana").asInt();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		Calendar c1 = Calendar. getInstance(Locale.GERMAN);
		c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		c1.set(Calendar.YEAR, anio );
		c1.set(Calendar.WEEK_OF_YEAR, nSemana );
		Calendar c2 =  (Calendar) c1.clone();

		String[] dias = new String[7];
		for (int i=0;i<6;i++) {
			dias[i] = sdf2.format(c2.getTime());
			c2.add(Calendar.DATE, 1);
		}
		dias[6] = sdf2.format(c2.getTime());
		//Pasar el array de strin a Json array
		JSONArray jsArray = new JSONArray();
		for (String dia : dias) {
			jsArray.put(dia);
		}

		System.out.println("Desde "+sdf.format(c1.getTime())+"  a  "+sdf.format(c2.getTime()));

		List<Sala> salas = Sala.find
				.fetch("operadores")
				.fetch("operadores.personal")
				.where()
				.filterMany("operadores.personal").eq("activo", "S")
				.filterMany("operadores").ne("personal", null)
				.findList();

		System.out.println("salas.size "+salas.size());

		System.out.println("c1 "+sdf.format(c1.getTime()));
		System.out.println("c2 "+sdf.format(c2.getTime()));

		String strc1 = sdf.format(c1.getTime());
		String strc2 = sdf.format(c2.getTime());

		System.out.println("strc1 "+strc1);
		System.out.println("strc2 "+strc2);


		List<Agenda> agenda = Agenda.find
				.fetch("folioproductorasignado.folio")
				.fetch("folioproductorasignado.folio.oficio")
				.fetch("folioproductorasignado.personal")
				.fetch("folioproductorasignado.personal.cuentas.roles")
				.fetch("salidas")
				.fetch("accesorios")
				.fetch("accesorios.accesorio")
				.fetch("equipos")
				.fetch("equipos.equipo")
				.fetch("formatos")
				.fetch("locaciones")
				.fetch("salas")

				.fetch("vehiculos")
				.fetch("vehiculos.vehiculo")
				.fetch("juntas")
				.fetch("cuentaRol")
				.fetch("cuentaRol.cuentarol")
				.fetch("cuentaRol.cuentarol.cuenta")
				.fetch("cuentaRol.cuentarol.cuenta.personal")
				.fetch("cuentaRol.cuentarol.rol")

				.fetch("cancelaciones")
				.where()
				.between("inicio", c1.getTime() , c2.getTime())
				.or(Expr.eq("estado.id", 7), Expr.eq("estado.id", 8))
				//No se incluye la fase 5 (Entrega)
				.in("fase.id", Arrays.asList(1, 2, 3, 4,6))
				//Se excluyen los operadores de salas
				.filterMany("cuentaRol.cuentarol").ne("rol.id", 16)
				.orderBy("inicio")
				.findList();

		System.out.println("agenda.size "+agenda.size());
		System.out.println("agenda json "+  jsonContext.toJsonString(agenda)  );
		return ok(  "{\"salas\": "+ jsonContext.toJsonString(salas)+", \"agenda\":"+jsonContext.toJsonString(agenda)+", \"dias\":"+jsArray+"}");
	}

}
