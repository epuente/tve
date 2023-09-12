package controllers;

import classes.Notificaciones.AuxNotificacion;
import classes.Notificaciones.Notificacion;
import org.json.JSONArray;
import org.json.JSONObject;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.primitives.Bytes.concat;

public class SSEController extends ControladorSeguro {


    public static Result ssePrueba(){
        return ok ( views.html.ssePrueba.render()  );
    }

    public static Result stream() throws UnsupportedEncodingException {
       // System.out.println("Desde SSEController.stream");
        long elUsuario = Long.decode(session("usuario"));
        long elRol      = Long.decode(session("rolActual"));
        //System.out.println("usuario:"+session("usuario"));
        //System.out.println("rolActual "+session("rolActual"));

        response().setContentType("text/event-stream");
        response().setHeader(CACHE_CONTROL, "no-cache");
        response().setHeader(ACCEPT_ENCODING, "gzip");
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader("X-Accel-Buffering", "no");
        Notificacion noti = Notificacion.getInstance();

        try {
            JSONObject jo = new JSONObject();
            JSONArray jaNots = new JSONArray();
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      //      System.out.println("noti.datos.size:" + noti.datos.size());
            List<AuxNotificacion> filtrados = noti.datos.stream().filter(f ->
                    (elRol ==1 && elUsuario==0)
                    ||
                    (f.roles.contains(elRol)
                    &&
                    f.personalId == elUsuario)
            ).collect(Collectors.toList());
            for (AuxNotificacion a : filtrados) {
                JSONObject j = new JSONObject();
                j.put("rolId", elRol);
                j.put("personalId", a.personalId);
                j.put("tipoNotificacion", a.tipoNotificacion);
                j.put("descripcion", "<a href=\"" + a.liga + "\">" + a.descripcion + "</a>");
                j.put("fase", a.fase);
                j.put("liga", a.liga);
                j.put("fecha", a.fecha);
                jaNots.put(j);
            }
            jo.put("datos", jaNots);
            String retry = "retry: 30000\n";
            String event = "event: aviso-"+elRol+"-"+elUsuario+"\n";
            String data = "data:" + jo.toString() + "\n\n";


            byte[] cretry = retry.getBytes("UTF-8");
            byte[] uno = event.getBytes("UTF-8");
            byte[] dos = data.getBytes("UTF-8");
            byte[] tres = concat(uno, cretry, dos);
            //System.out.println("-------------------" + event);
            //System.out.println("-------------------\n" + new String(tres)+"-------------------");
            return ok(tres);
        } catch (Exception e) {
            System.out.println("Error en SSEController");
        }
        return ok();
    }


    public static Result listaNotificaciones(){
        Notificacion noti = Notificacion.getInstance();
        return ok ( views.html.listaNotificaciones.render(noti.datos));
    }

    public static Result recargar(){
        Notificacion noti = Notificacion.getInstance();
        noti.recargar();
        return ok();
    }


}
