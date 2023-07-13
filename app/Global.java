import classes.ColorConsola;
import classes.Notificaciones.Notificacion;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import views.html.errorPage;
import views.html.notFoundPage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Configuraciones/acciones globales
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        System.out.println("\n\n\n");
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss:S");
        String modo  =  app.isDev()?"Desarrollo":app.isProd()?"Producción":"Prueba";
        String ruta = Play.application().path().getAbsolutePath();
        Date hoy = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
        c.add(Calendar.HOUR, -1);
        //Logger.info("Application has started");


        //System.out.println(    Play.application().configuration().getString("db.default.url")     );

        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_GREEN+"Iniciando aplicación en modo "+modo+" el "+sdfDia.format(c.getTime())+" a las "+  sdfHora.format(c.getTime())  +ColorConsola.ANSI_RESET);
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_GREEN+"Ejecutable localizado en "+ruta +ColorConsola.ANSI_RESET);
        Notificacion noti = Notificacion.getInstance();
        noti.recargar();
    }


    @Override
    public void onStop  (Application app) {
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss:S");
        String modo  =  app.isDev()?"Desarrollo":app.isProd()?"Producción":"Prueba";
        Date hoy = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
        c.add(Calendar.HOUR, -1);
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_YELLOW+"Deteniendo aplicación en modo "+modo+" el "+sdfDia.format(c.getTime())+" a las "+  sdfHora.format(c.getTime())  +ColorConsola.ANSI_RESET);
    }


    
	@SuppressWarnings("unchecked")
	@Override
    public <T extends EssentialFilter> Class<T>[] filters() {        
        return new Class[]{CSRFFilter.class, GzipFilter.class};
    }
	
    
    public Promise<Result> onError(RequestHeader request, Throwable t) {
        return Promise.<Result>pure(play.mvc.Results.internalServerError(
            errorPage.render(t)
        ));
    }
    
    public Promise<Result> onHandlerNotFound(RequestHeader request) {
        return Promise.<Result>pure(play.mvc.Results.notFound(
            notFoundPage.render(request.uri())
        ));
    }







    
}
