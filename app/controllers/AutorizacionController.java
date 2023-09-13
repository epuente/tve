package controllers;

import classes.miCorreo2;
import classes.ColorConsola;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.mvc.Result;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import play.db.ebean.Model;
public class AutorizacionController extends ControladorSeguroAdministradorOperadorSala {
    public static Result ajaxAutorizaEvento() {
        System.out.println(ColorConsola.SET_BOLD_TEXT + ColorConsola.ANSI_BLUE_BACKGROUND + ColorConsola.ANSI_BLACK+"\n\nDesde AutorizacionController.ajaxAutorizaEvento\n"+ColorConsola.ANSI_RESET);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Agenda obj = null;
        Agenda age = new Agenda();
        JsonNode json = request().body().asJson();
        System.out.println(json);

        List<AgendaCuentaRol> personal = new ArrayList<AgendaCuentaRol>();
        List<AgendaOperadorSala> operadores = new ArrayList<AgendaOperadorSala>();

        List<JsonNode> arreglo2 = json.findPath("xyz").findValues("operadoresSala");

        System.out.println("arreglo2.size():" + arreglo2.size());
        System.out.println("arreglo2:" + arreglo2);

        for (JsonNode p : arreglo2) {
            Long idPersona = p.findPath("personal").findPath("id").asLong();
            System.out.println("idPersona " + idPersona);
            AgendaOperadorSala operador = new AgendaOperadorSala();
            operador.agenda = obj;

            operador.personal = Personal.find.ref(idPersona);
            operadores.add(operador);
        }

        Ebean.beginTransaction();
        try {
            List<Equipo> lstE = new ArrayList<>();
            List<Accesorio> lstA = new ArrayList<>();
            for ( JsonNode jne : json.findPath("xyz").findValues("equipos")  ) {
                System.out.println("                "+jne);
                       for (JsonNode e : jne) {
                           System.out.println("                         e:"+e);
                           System.out.println("                         e:"+e.findPath("equipo"));
                           System.out.println("                         e:"+e.findPath("equipo").findValue("id").asLong());
                           System.out.println("                         e:"+e.findPath("equipo").findValue("id").asLong());
                           Long auxId = e.findPath("equipo").findValue("id").asLong();
                           lstE.add(Equipo.find.byId(auxId));
                       }
            }
            for ( JsonNode jne : json.findPath("xyz").findValues("accesorios")  ) {
                for (JsonNode e : jne) {
                    Long auxId = e.findPath("accesorio").findValue("id").asLong();
                    lstA.add(Accesorio.find.byId(auxId));
                }
            }





            //Quitar nodo 'locutores', 'personal', 'personalIds'
            for (JsonNode personNode : json) {
                if (personNode instanceof ObjectNode) {
                    ObjectNode object = (ObjectNode) personNode;
                    object.remove("locutores");
                    object.remove("personal");
                    object.remove("personalIds");
                    //Cuando no se trate del rol de  Ingeniero, eliminar los nodos de equipo y accesorios
                    if (session("rolActual").compareTo("10")!=0) {
                        object.remove("equipos");
                        object.remove("accesorios");
                    }

                }
            }

            System.out.println("se eliminó el nodo locutores, personal, accesorios y equipos");

            FolioProductorAsignado fpa = FolioProductorAsignado.find
                    .byId(json.findPath("folioproductorasignado")
                            .findPath("id").asLong());
            System.out.println("000 " + fpa);


            json.findPath("xyz").fieldNames().forEachRemaining(h -> System.out.println("? " + h));
            PreAgenda tempo = PreAgenda.find.byId(json.findPath("id").asLong());

            if (tempo != null) {
                tempo.estado = Estado.find.byId(7L);
                System.out.println("020.20");
                //tempo.update();
                Ebean.update(tempo);
                System.out.println("020.30");
            }


            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(json.get("xyz").traverse(), Agenda.class);

            obj.inicio =  sdf.parse(json.findValue("inicio").asText());
            obj.fin =  sdf.parse(json.findValue("fin").asText());

            System.out.println("inicia propiedades del objeto obj ...");
            Object someObject = obj;
            for (Field field : someObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(someObject);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value != null) {
                    System.out.println("          " + field.getName() + "=" + value);
                }
            }
            System.out.println("... termina propiedades del objeto obj");

            System.out.println("existe personal ?      " + obj.cuentaRol.size());
            System.out.println("existe vehiculos ?     " + obj.vehiculos.size());

            if (obj.salidas.size() != 0) {
                obj.salidas.get(0).agenda = obj;
            }

            if (obj.vehiculos.size() != 0) {
                obj.vehiculos.get(0).agenda = obj;
            }
            if (obj.juntas.size() != 0) {
                obj.juntas.get(0).agenda = obj;
            }
            if (obj.locaciones.size() != 0) {
                obj.locaciones.get(0).agenda = obj;
            }
            if (obj.cuentaRol.size() != 0) {
                obj.cuentaRol.get(0).agenda = obj;
            }


            // Se trata de una sala con operador?
            System.out.println("---- tempo.operadoresSala.size():" + tempo.operadoresSala.size());
            System.out.println("---- obj.operadoresSala.size():" + obj.operadoresSala.size());

            if (obj.operadoresSala.size() > 0)
                obj.operadoresSala.remove(0);
            System.out.println("---- obj.operadoresSala.size():" + obj.operadoresSala.size());

            for (PreAgendaOperadorSala ops : tempo.operadoresSala) {
                AgendaOperadorSala ageops = new AgendaOperadorSala();
                ageops.agenda = obj;
                ageops.personal = ops.personal;
                obj.operadoresSala.add(ageops);
            }

            System.out.println("id: " + obj.id + " edo: " + obj.estado.id);
            System.out.println("junta: " + obj.juntas);
            //System.out.println("junta.as.id: "+obj.juntas.get(0).agenda.id);
            obj.estado = Estado.find.ref(7L);
            System.out.println("inicia propiedades del objeto... obj");
            //someObject = obj;
            for (Field field : someObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(someObject);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value != null) {
                    System.out.println("    " + field.getName() + " = " + value);
                }
            }

            // Equipo y accesorios es un caso especial ya que el evento requiere primero de la autorización del
            // administrador y despues de la autorización del Ingeniero encargado de la Admon. de equipo y accesorios.
            // Para ello se registra en las tablas agenda_previo equipo y agenda_previo_accesorio el id que viene de
            // la preagenda
            // El usuario es Ingeniero Admon. Equipo y Accesorios?

            System.out.println("//////////////");
            System.out.println(" rolActual   "+session("rolActual"));
            // Es administrador?
            if (session("rolActual").compareTo("1")==0   && json.findPath("fase").findValue("id").asInt() == 2) {
                System.out.println("Soy Admin");
                System.out.println("e1:: "+json.findPath("xyz").findValues("equipos"));
                System.out.println("a1:: "+json.findPath("xyz").findValues("accesorios"));

                System.out.println("List lstPAPAE:"+lstE.size());
                System.out.println("List lstPAPAA:"+lstA.size());

                List<AgendaPrevioAutorizaEquipo> agePrevEqs = new ArrayList<>();
                List<AgendaPrevioAutorizaAccesorio> agePrevAccs = new ArrayList<>();
                PreAgenda preAg = PreAgenda.find.byId(  json.findPath("xyz").findValue("id").asLong()  );

                for (PreAgendaEquipo equipo: preAg.equipos){
                    AgendaPrevioAutorizaEquipo nvoPreAgEq = new AgendaPrevioAutorizaEquipo();
                    nvoPreAgEq.preagendaequipo = equipo;
                    agePrevEqs.add(nvoPreAgEq);
                }
                for (PreAgendaAccesorio acc: preAg.accesorios){
                    AgendaPrevioAutorizaAccesorio nvoPreAgAcc= new AgendaPrevioAutorizaAccesorio();
                    nvoPreAgAcc.preagendaaccesorio = acc;
                    agePrevAccs.add(nvoPreAgAcc);
                }

                obj.previoautorizaequipo = agePrevEqs;
                obj.previoautorizaaccesorios = agePrevAccs;
/*
                for (Equipo equipo : lstE) {
                    AgendaPrevioAutorizaEquipo agEquipo = new AgendaPrevioAutorizaEquipo();
                    //agEquipo.equipo = Equipo.find.byId(equipo.id);
                    //agEquipo.equipo = equipo;

                    agEquipo.preagendaequipo.equipo = equipo;
                   // agEquipo.preagendaequipo = obj.folioproductorasignado.preagendas;

                    obj.previoautorizaequipo.add(agEquipo);
                }
                for (Accesorio accesorio : lstA) {
                    AgendaPrevioAutorizaAccesorio agAcc = new AgendaPrevioAutorizaAccesorio();
                    //agAcc.accesorio = accesorio;
                    agAcc.preagendaaccesorio.accesorio = accesorio;
                    obj.previoautorizaaccesorios.add(agAcc);
                }
*/
            }




            System.out.println("... termina propiedades del objeto obj");



           // Ebean.rollbackTransaction();




            age = obj;
            age.id = null;
            age.folioproductorasignado = fpa;
            System.out.println("...  **************************************************");
           // someObject = age;
            for (Field field : someObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
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
            System.out.println("... termina propiedades del objeto age **************************************************");

            age.cuentaRol.addAll(personal);

            Ebean.save(age);
            fpa.agenda.add(age);
            Ebean.update(fpa);

          /*
		  for (AgendaCuentaRol p : personal) {
			  p.agenda = obj;
			  System.out.println("=>> p.agenda:"+p.agenda);
			  System.out.println("=>> p.agenda,id:"+p.agenda.id);
			  System.out.println("");
			  System.out.println("=>> p.cuentarol:"+p.cuentarol);
			  System.out.println("=>> p.cuentarol.id:"+p.cuentarol.id);
			  
			//  Ebean.save(p);  
		  }
		  */
            Ebean.refresh(age);










          /*
		  age.save();
		  fpa.agenda.add(age);
		  fpa.update();
		  age.refresh();
		  */
            System.out.println("   ........................... despues de update (agenda nuevo)");

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ NO es nuevo");

            System.out.println("++++++++++++++++++++++  10");

            System.out.println("++++++++++++++++++++++  20");

            System.out.println("   ........................... despues de update (actualiza agenda existente)");
            Ebean.commitTransaction();

        } catch (Exception e) {
            System.out.println("Error!!!!!!!!!!!!!   " + e);
            e.printStackTrace();
            Ebean.rollbackTransaction();
            System.out.println("se aplicó rollback");
            age.id = null;
        } finally {
            Ebean.endTransaction();
        }
        System.out.println("id generado en agenda despues de autorizar:" + age.id);
        return ok("{\"autorizado\":" + (age.id != null) + ",\"id\":" + age.id + "}");
    }

    public static Result ajaxEliminaEventoAdmin() {
        System.out.println("   ...........................   desde AutorizacionController.ajaxEliminaEventoAdmin ");
        boolean eliminado = false;
        JsonNode json = request().body().asJson();
        System.out.println(json);
        System.out.println("id:" + json.findPath("id").asText());
        System.out.println("tipo:" + json.findPath("tipo").asText());

        try {
            if (json.findPath("tipo").asText().compareTo("preagenda") == 0) {
                System.out.println("preagenda.....");
                PreAgenda o = PreAgenda.find.byId(json.findPath("id").asLong());
                o.delete();
                eliminado = (PreAgenda.find.byId(json.findPath("id").asLong()) == null);
            }
            if (json.findPath("tipo").asText().compareTo("agenda") == 0) {
                System.out.println("agenda.....");
                Agenda.find.byId(json.findPath("id").asLong()).delete();
                eliminado = (Agenda.find.byId(json.findPath("id").asLong()) == null);
            }
        } catch (Exception e) {
            System.out.println("Error!!!!!!!!!!!!!   " + e);
            // e.printStackTrace();
            System.out.println("Error!!!!!!!!!!!!!  regresando " + "{\"eliminado\":" + eliminado + "}");
        } finally {
            System.out.println("Eli9mn9do: " + eliminado);

        }
        return ok("{\"eliminado\":" + eliminado + "}");

    }
    /*
    public static Result ajaxActualizaEventoAdmin() {
        System.out.println("   ...........................   desde AutorizacionController.ajaxActualizaEventoAdmin ");
        Agenda obj = null;
        JsonNode json = request().body().asJson();

        System.out.println(json);

        Agenda o = new Agenda();

        if (json.findPath("tipo").asText().compareTo("agenda") == 0
                || json.findPath("estado").findPath("id").asLong() == 5
                || json.findPath("estado").findPath("id").asLong() == 7) {

            o = Agenda.find.byId(json.findPath("id").asLong());

            // Quitar nodo 'tipo'
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

                if (obj.salidas != null) {
                    o.salidas.get(0).agenda = obj;
                }
                if (obj.vehiculos != null) {
                    o.vehiculos.get(0).agenda = obj;
                }
                if (obj.locaciones.get(0) != null) {
                    o.locaciones.get(0).agenda = obj;
                }
                if (obj.cuentaRol != null) {
                    System.out.println("   evento con personal");
                    for (AgendaCuentaRol tp : obj.cuentaRol) {

                        tp.agenda = obj;

                        o.cuentaRol.add(tp);
                    }


                }

                System.out.println(
                        "   ........................... 00 folioproductorasignado " + o.folioproductorasignado);
                System.out.println("   ........................... 01 fase " + o.fase.id);
                System.out.println("   ........................... 02 inicio " + o.inicio);
                System.out.println("   ........................... 03 inicio " + o.fin);
                System.out.println("   ........................... 04 estado " + o.estado.id);

                System.out.println(
                        "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++antes+++++++++++++++++++++");
                o.update();
                System.out.println(
                        "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++despues+++++++++++++++++++");

            } catch (JsonParseException | JsonMappingException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Error!!!!!!!!!!!!!   " + e.getMessage());
                e.printStackTrace();
            }

        }
        // return ok( "{\"agregado\":"+(obj.id != null)+"}" );
        return ok("{\"actualizado\":" + (o.id != null) + ",\"id\":" + o.id + "}");

    }
    */
    public static Result colaCorreosEnviar() {
        System.out.println("desde AutorizacionController.colaCorreosEnviar");
        ColaCorreos.find.all().forEach(Model::delete);
        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

        Date hoy = new Date();
        System.out.println("000");
        // Todos los preagenda del 2019 en estado 5
        System.out.println("100");
        List<PreAgenda> colaPreagenda = PreAgenda.find.setDistinct(true).select("id").where()
                .eq("estado.id", 5L).gt("inicio", "2019-01-01").findList();
        System.out.println("200");
        System.out.println("colaPreagenda tam: " + colaPreagenda.size());

        /////////////// VERSION COn GROUP_CONCAT
        String sql = "select distinct folio.numfolio, oficio.descripcion as oficioDescripcion"
                + ", servicio.descripcion as servicioDescripcion "
                + ", CONCAT(personal.nombre,\" \", personal.paterno,\" \",personal.materno )as productor "
                + ", group_concat(pas.id) as ids " + ", group_concat(distinct personalcorreo.email) as correos "
                + "from pre_agenda pas "
                + "INNER join folio_productor_asignado fpa on pas.folioproductorasignado_id = fpa.id "
                + "INNER join folio folio on fpa.folio_id = folio.id "
                + "INNER join oficio oficio on folio.oficio_id = oficio.id "
                + "INNER join oficio_servicio_solicitado oficioSol on oficio.id = oficioSol.oficio_id "
                + "inner join servicio servicio on oficioSol.servicio_id = servicio.id "
                + "inner join fase fase on pas.fase_id = fase.id "
                + "inner join personal personal on fpa.personal_id = personal.id "
                + "inner join personal_correo personalcorreo on personal.id = personalcorreo.personal_id " + "where    "
                + "pas.estado_id= 5 " + "and inicio > '2019-01-01' "
                + "group by folio.numfolio, servicio.descripcion, CONCAT(personal.nombre,\" \", personal.paterno,\" \",personal.materno )";

        System.out.println(sql);

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();

        System.out.println("sqlRows tam: " + sqlRows.size());

        for (SqlRow sqlrow : sqlRows) {
            String[] correos = sqlrow.getString("correos").split(",");
            String[] stringIds = sqlrow.getString("ids").split(",");
            List<String> ids = Arrays.asList(stringIds);

            // Por cada correo
            for (String correo : correos) {
                ColaCorreos cc = new ColaCorreos();
                cc.correo = correo;
                cc.asunto = "Solicitudes del productor " + sqlrow.getString("productor");
                cc.folio = sqlrow.getString("folio");
                cc.oficioDescripcion = sqlrow.getString("oficioDescripcion");
                cc.servicioDescripcion = sqlrow.getString("servicioDescripcion");
                String contenido = "";
                contenido = "El presente es un reporte automático de los movimientos realizados el <strong>día de hoy, solicitudes y autorizaciones</strong> del Departamento de Televisión Educativa con corte al <strong>"
                        + sdfFecha.format(hoy) + " a las " + sdfHora.format(hoy) + "</strong><br><br> ";
                contenido += "<h3>Folio: " + cc.folio + "</h3>";
                contenido += "<h3>Descripción: " + cc.oficioDescripcion + "</h3>";
                contenido += "<h3>Servicio solicitado: " + cc.servicioDescripcion + "</h3>";
                String eventos = "<table border='1px' style='width:100%; border-collapse:collapse;  '>";
                eventos += "<tbody><tr><th>Fecha</th><th>Horario</th><th>Detalle</th></tr>";
                int i = 1;

                List<PreAgenda> detalle = PreAgenda.find.fetch("salidas").fetch("accesorios")
                        .fetch("accesorios.accesorio").fetch("equipos").fetch("equipos.equipo").fetch("vehiculos")
                        .fetch("salas").fetch("salas.sala").fetch("salas.usoscabina").where().idIn(ids)
                        .orderBy("inicio").findList();

                StringBuilder eventosBuilder = new StringBuilder(eventos);
                for (PreAgenda pre : detalle) {
                    String colorRenglon = "lightgrey";
                    if ((i++ % 2) == 0)
                        colorRenglon = "white";
                    eventosBuilder.append("<tr bgcolor=\"").append(colorRenglon).append("\">");
                    eventosBuilder.append("<td style='padding: 5px; width:90px'>").append(sdfFecha.format(pre.inicio)).append("</td>");
                    eventosBuilder.append("<td style='padding: 5px; width:130px'>");
                    for (PreAgendaSalida salida : pre.salidas) {
                        eventosBuilder.append("<p><strong>Salida </strong>").append(sdfHora.format(salida.salida)).append("</strong></p>");
                    }

                    for (PreAgendaVehiculo v : pre.vehiculos) {
                        eventosBuilder.append("<p><strong>Solicitó vehículo</strong></p>");
                    }

                    eventosBuilder.append("<p><strong>Desde </strong>").append(sdfHora.format(pre.inicio)).append("</p><p><strong>Hasta </strong>").append(sdfHora.format(pre.fin)).append("</p>");
                    eventosBuilder.append("</td>");
                    eventosBuilder.append("<td style=''>");
                    eventosBuilder.append("<p><strong>").append(pre.fase.descripcion);
                    if (pre.juntas.size() > 0)
                        eventosBuilder.append(" - Junta de trabajo");
                    if (pre.locaciones.size() > 0)
                        eventosBuilder.append(" - Scouting");
                    eventosBuilder.append("</strong></p>");

                    if (pre.locaciones.size() > 0) {
                        for (PreAgendaLocacion locacion : pre.locaciones) {
                            eventosBuilder.append("<p><strong>Locación </strong>").append(locacion.locacion).append("<br>");
                        }
                        eventosBuilder.append("</p>");
                    }

                    if (!pre.locutores.isEmpty()) {
                        eventosBuilder.append("<p><strong>Locutores</strong><br>");
                        for (PreAgendaLocutor loc : pre.locutores) {
                            eventosBuilder.append(loc.personal.nombreCompleto()).append("<br>");
                        }
                        eventosBuilder.append("</p>");
                    }

                    if (!pre.equipos.isEmpty()) {
                        eventosBuilder.append("<p><strong>Equipo y accesorios</strong><br>");
                        eventosBuilder.append("<ul>");
                        for (PreAgendaEquipo equipo : pre.equipos) {
                            eventosBuilder.append("<li>").append(equipo.equipo.descripcion).append(" ").append(equipo.equipo.marca).append(" ").append(equipo.equipo.modelo).append(" ").append(equipo.equipo.serie).append(" ").append(equipo.equipo.observacion).append("</li>");
                        }
                        if (!pre.accesorios.isEmpty()) {
                            for (PreAgendaAccesorio acc : pre.accesorios) {
                                eventosBuilder.append("<li>").append(acc.accesorio.descripcion).append(" ").append(acc.accesorio.modelo).append("  NS:").append(acc.accesorio.serie).append(" ").append(acc.accesorio.observacion).append("</li>");
                            }
                        }
                        eventosBuilder.append("</ul></p>");
                    }

                    if (!pre.salas.isEmpty()) {
                        eventosBuilder.append("<p><strong>");
                        for (PreAgendaSala sala : pre.salas) {
                            eventosBuilder.append(sala.sala.descripcion);

                            for (PreAgendaSalaUsoCabina uso : sala.usoscabina) {
                                String aux = "";
                                switch (uso.usocabina) {
                                    case "M":
                                        aux = "Musicalización";
                                        break;
                                    case "V":
                                        aux = "Voz en off";
                                        break;
                                    case "I":
                                        aux = "Ingesta";
                                        break;
                                    case "C":
                                        aux = "Calificación";
                                        break;
                                    case "R":
                                        aux = "Revisión";
                                        break;
                                    default:
                                        break;
                                }
                                eventosBuilder.append(" - ").append(aux);
                            }
                            eventosBuilder.append("<br>");
                            // Operador de sala
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            @SuppressWarnings("unchecked")
                            List<Personal> este = (List<Personal>) (List<?>) UsuarioController.auxOperadorEnHorario(
                                    sdf.format(pre.inicio), sdf.format(pre.fin), pre.id, sala.sala.id);

                            System.out.println("Tam este " + este.size());
                            for (Object p : este) {
                                System.out.println("sala " + sala.sala.descripcion + "   "
                                        + Personal.find.byId(Long.parseLong(p.toString())).nombreCompleto());

                                eventosBuilder.append("Operador: ").append(Personal.find.byId(Long.parseLong(p.toString())).nombreCompleto()).append("<br>");
                            }
                        }
                        eventosBuilder.append("</p></strong>");

                    }

                    if (!pre.personal.isEmpty()) {
                        eventosBuilder.append("<p><strong>Perfiles solicitados</strong><br>");
                        for (PreAgendaRol p : pre.personal) {
                            eventosBuilder.append(p.rol.descripcion).append(":  ").append(p.cantidad).append("<br>");
                        }
                        eventosBuilder.append("</p>");
                    }

                    if (!pre.formatos.isEmpty()) {
                        eventosBuilder.append("<p><strong>Formatos para entrega</strong><br>");
                        for (PreAgendaFormatoEntrega fmto : pre.formatos) {
                            eventosBuilder.append(fmto.formato.descripcion).append(": ").append(fmto.cantidad).append("<br>");
                        }
                        eventosBuilder.append("</p>");
                    }

                    eventosBuilder.append("</td>");
                    eventosBuilder.append("</tr>");
                }
                eventos = eventosBuilder.toString();
                eventos += "</tbody></table><br><br>";
                contenido += eventos;
                cc.servicioDescripcion = sqlrow.getString("servicioDescripcion");
                cc.oficioDescripcion = sqlrow.getString("oficioDescripcion");
                //cc.folio = cc.folio;
                cc.contenido = contenido;
                cc.estado = 1;
                cc.numintentos = 0;
                cc.save();
            }

            /*
             * ********************** CHECAR que pasa con 2.SQL en /conf/evolutions
             ********************
             * IMPLEMENTAR LA ANOTACIÓN MappedSuperclass
             */

            System.out.println(sqlrow.getString("ids"));
            System.out.println(sqlrow.getLong("folio"));
        }

        System.out.println("REgresando...");
        // System.out.println(
        // "{\"agregados\":"+ColaCorreos.find.all().size()+"\"correos:\""+
        // jsonContext.toJsonString(ColaCorreos.find.all()) +"}" );

        JSONArray ja = new JSONArray();

        miCorreo2 mc2 = new miCorreo2();

        for (ColaCorreos laColaCorreos : ColaCorreos.find.all()) {
            /*
             * int i=0;
             *
             * new Thread("" + i++){ public void run(){
             */

            try {
                JSONObject jo = new JSONObject();
                jo.put("correo", laColaCorreos.correo);
                jo.put("asunto", laColaCorreos.asunto);
                jo.put("contenido", laColaCorreos.contenido);
                ja.put(jo);

                mc2.asunto = jo.getString("asunto");
                mc2.para = Arrays.asList(jo.getString("correo"), "eduardo.puente72@gmail.com");
                // mc2.para.add("eduardo.puente72@gmail.com");
                mc2.mensaje = jo.getString("contenido");
                System.out.println("antes del envio del email a:" + mc2.para);
                laColaCorreos.numintentos++;
                // mc2.enviar();
                mc2.run();
                laColaCorreos.estado = 2;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                laColaCorreos.estado = 3;
                e.printStackTrace();
            }
            laColaCorreos.update();
            // }
            // }.start();

        }

        // System.out.println( ja.toString() );
        // System.out.println("regresando...");

        // Probando método asíncrono

        return ok(ja.toString());
    }


    @Transactional
    public static Result guardaDatosIngesta() {
        System.out.println("   ...........................   desde guardaDatosIngesta ");
        AgendaIngesta obj = null;
        //JsonContext jsonContext = Ebean.createJsonContext();
        JsonNode json = request().body().asJson();
        System.out.println("json... ");
        System.out.println(json);
        System.out.println("...json ");
//FolioProductorAsignado fpa = FolioProductorAsignado.find.byId(  json.findPath("agendaServicioIngesta").findPath("id").asLong()  );
        try {
            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(json.findPath("datosIngesta").traverse(), AgendaIngesta.class);
            System.out.println("de json a objeto oki!!!");

/*@*
				//Quitar nodo 'id'
				for (JsonNode personNode : json) {
				    if (personNode instanceof ObjectNode) {
				        ObjectNode object = (ObjectNode) personNode;
				        object.remove("id");
				    }
				}
System.out.println("se eliminó el nodo id");
System.out.println("obj.estado.id:"+obj.estado.id);
				if (obj.estado.id == 4)
					obj.estado = Estado.find.byId(5L);
				
System.out.println("*****************obj");				
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
System.out.println("*****************obj");				

				if (obj.salidas.size()!=0){
					obj.salidas.get(0).preagenda =  obj;
				}
				if (obj.vehiculos.size()!=0){
					obj.vehiculos.get(0).preagenda =  obj;
				}
				if (obj.juntas.size()!=0){
					obj.juntas.get(0).preagenda =  obj;
				}				
				if (obj.locaciones.size()!=0){
					obj.locaciones.get(0).preagenda =  obj;
				}
				
				for( PreAgendaAccesorio e :  obj.accesorios){
					System.out.println("       id accesorio :"+e.accesorio.id);
					e.preagenda =  obj;	
				}
				
				if (obj.salas.size()!=0){
					obj.salas.get(0).preagenda = obj;
					if (obj.salas.get(0).usoscabina.size()!=0){
						obj.salas.get(0).usoscabina.get(0).preagendasala = obj.salas.get(0); 
					}
				}
				
				
				
				obj.folioproductorasignado.id = fpa.id;
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++antes+++++++++++++++++++++");
				PreAgenda existePAS = PreAgenda.find.byId(obj.id);
System.out.println("PreAgenda.find.byId(obj.id) == null    "+ existePAS);
				if (existePAS != null){
System.out.println("existePAS != null");	
					if (existePAS.salas.size() != 0)
						PreAgendaSala.find.where().eq("preagenda.id", obj.id).findUnique().delete();
					PreAgenda.find.byId(obj.id).delete();
				}
				fpa.preagendas.add(obj);
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++despues+++++++++++++++++++");				
				fpa.update();
				System.out.println("   ........................... despues de update ");	
				*@*/

        } catch (JsonParseException | JsonMappingException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error!!!!!!!!!!!!!   " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("agenda.id " + json.findPath("agendaServicioIngesta").findPath("id").asLong());
        obj.agenda.id = json.findPath("agendaServicioIngesta").findPath("id").asLong();

        obj.save();
        obj.refresh();


        System.out.println("id generado en AgendaIngesta:" + obj.id);
//System.out.println("retorno->"+"{\"agregado\":"+(obj.id != null ||  obj.id != 0 )+",\"id\":"+obj.id+"}");

        //   return ok( "{\"agregado\":"+(obj.id != null)+"}" );
        return ok("{\"guardado\":" + obj.id + "}");


    }


}