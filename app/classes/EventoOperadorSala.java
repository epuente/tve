package classes;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import models.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static play.mvc.Controller.session;

public class EventoOperadorSala {
    static class OperadorSalaTurno{
        Long operadorId;
        Long salaId;
        String turno;
        int diaSemana;
        Date desde;
        Date hasta;
    }


    public List<AuxEventoOperadorSala>  obtener() {

        List<OperadorSala> operadoresSala = OperadorSala.find
                .fetch("personal.horarios")
                .where()
                .eq("personal.id", session("usuario"))
                .findList();
/*
        for (OperadorSala operadorSala : operadoresSala) {
            System.out.println(operadorSala.id + " " + operadorSala.personal.nombreCompleto() + " sala " + operadorSala.sala.descripcion);
        }
*/
        List<OperadorSalaTurno> operadorSalasTurnos = new ArrayList<>();
        for (OperadorSala op : operadoresSala) {
            for (PersonalHorario h : op.personal.horarios) {
                OperadorSalaTurno ost = new OperadorSalaTurno();
                ost.operadorId = op.id;
                ost.salaId = op.sala.id;
                ost.turno = op.turno;
                ost.diaSemana = h.diasemana;
                ost.desde = h.desde;
                ost.hasta = h.hasta;
                operadorSalasTurnos.add(ost);
            }
        }

/*
        operadorSalasTurnos.forEach(x -> {
            System.out.println("operador: " + x.operadorId);
            System.out.println("sala: " + x.salaId);
            System.out.println("turno: " + x.turno);
            System.out.println("dia: " + x.diaSemana);
            System.out.println("desde: " + x.desde);
            System.out.println("hasta: " + x.hasta + "\n\n");
        });
*/
        String auxQueryH;
        StringBuilder auxAgeHorarios = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (OperadorSalaTurno ostx : operadorSalasTurnos) {
            auxAgeHorarios.append("(extract(dow from inicio) = ").append(ostx.diaSemana).append(" and inicio >=  cast( concat(to_char(inicio, 'YYYY-MM-DD'),' ").append(sdf.format(ostx.desde)).append("') as timestamp)").append("	and inicio <  cast( concat(to_char(fin, 'YYYY-MM-DD'),' ").append(sdf.format(ostx.hasta)).append("') as timestamp) 	").append("	and fin >  cast( concat(to_char(fin, 'YYYY-MM-DD'),' ").append(sdf.format(ostx.desde.getTime())).append("') as timestamp)").append("	and fin <=  cast( concat(to_char(fin, 'YYYY-MM-DD'),' ").append(sdf.format(ostx.hasta)).append("') as timestamp)	").append(") or ");
        }
        auxQueryH = auxAgeHorarios.toString();
        System.out.println("auxQueryH: "+auxQueryH);
        // Quitar el ultimo 'or'
        if (!auxQueryH.isEmpty())
            auxQueryH = auxQueryH.substring(0, auxQueryH.length() - 3);
        ExpressionList<FolioProductorAsignado> Exagendas = Ebean.createQuery(FolioProductorAsignado.class)
                .setDistinct(true)
                .fetch("preagendas")
                .fetch("preagendas.operadoresSala")
                .fetch("preagendas.operadoresSala.personal")
                .fetch("preagendas.salas")
                .where()
                .eq("preagendas.operadoresSala.personal.id", session("usuario"))
                .in("preagendas.estado.id", Arrays.asList(4, 5))
                .raw("(" + auxQueryH + ")");

        System.out.println("Exagendas "+Exagendas);

        List<FolioProductorAsignado> fpasss = Exagendas.findList();
        //System.out.println("fpasss : "+fpasss.size());
        //System.out.println("fpasss query:         "+Exagendas.query().getGeneratedSql());

        List<FolioProductorAsignado> agendas = Ebean.createQuery(FolioProductorAsignado.class)
                .setDistinct(true)
                .fetch("agenda")
                .fetch("agenda.operadoresSala")
                .fetch("agenda.operadoresSala.personal")
                .fetch("agenda.salas")
                .where()
                .in("agenda.estado.id", Arrays.asList(7, 8, 11, 12, 13, 14, 15, 99))
                //     .eq("salas.sala.id", laSala)
                .raw("(" + auxQueryH + ")")
                .findList();


        List<classes.AuxEventoOperadorSala> retorno = new ArrayList<>();

        //System.out.println("agendas.size:"+agendas.size());

        for (FolioProductorAsignado fpa : fpasss) {
            for (PreAgenda preA : fpa.preagendas) {
                for ( PreAgendaSala sala:preA.salas ) {
                    classes.AuxEventoOperadorSala aux = new classes.AuxEventoOperadorSala();
                    aux.id = preA.id;
                    aux.tipo = "preagenda";
                    aux.fase = preA.fase;
                    aux.estado = preA.estado;
                    aux.sala = sala.sala;
                    aux.desde = preA.inicio;
                    aux.hasta = preA.fin;
                    retorno.add(aux);
                }
            }
        }
        for (FolioProductorAsignado fpa : agendas) {
            for (  Agenda  age : fpa.agenda) {
                for (AgendaSala sala : age.salas) {
                    classes.AuxEventoOperadorSala aux = new classes.AuxEventoOperadorSala();
                    aux.id = age.id;
                    aux.tipo = "agenda";
                    aux.fase = age.fase;
                    aux.estado = age.estado;
                    aux.sala = sala.sala;
                    aux.desde = age.inicio;
                    aux.hasta = age.fin;
                    retorno.add(aux);
                }
            }
        }
        System.out.println("***************************************************");
        for (AuxEventoOperadorSala a :retorno ){
            System.out.println("        "+a.tipo+"  "+a.id);
        }
        System.out.println("***************************************************");
        return retorno;
    }
}
