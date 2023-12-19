import classes.ColorConsola;
import classes.Notificaciones.Notificacion;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.db.DB;
import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.libs.F.Promise;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import views.html.errorPage;
import views.html.notFoundPage;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Configuraciones/acciones globales
 */
public class Global extends GlobalSettings {



    @Override
    public void beforeStart(Application app) {
        System.out.println("Preaparando inicio...");
    }
    @Override
    public void onStart(Application app) {
        System.out.println("\n\n\n");
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss:S");
        String modo  =  app.isDev()?"Desarrollo":app.isProd()?"Producción":"Prueba";
        try {
            String DBvendor = DB.getConnection().getMetaData().getDatabaseProductName();
            String DBversion = DB.getConnection().getMetaData().getDatabaseProductVersion();
            String DBdriver = DB.getConnection().getMetaData().getDriverName();
            String DBdriverVersion = DB.getConnection().getMetaData().getDriverVersion();
            String DBurl = DB.getConnection().getMetaData().getURL();

            System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_GREEN+"DB: "+DBvendor+" version DB: "+DBversion+"   driver: "+DBdriver+" version del driver: "+DBdriverVersion+"    conectado en: "+DBurl+ ColorConsola.ANSI_RESET);

        } catch (SQLException e) {
            System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_YELLOW+"Ocurrió un error al intentar obtener metadata de la DB"  +ColorConsola.ANSI_RESET);
            throw new RuntimeException(e);
        }
        String ruta = Play.application().path().getAbsolutePath();

        Date hoy = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
       // c.add(Calendar.HOUR, -1);
        //System.out.println(    Play.application().configuration().getString("db.default.url")     );
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_GREEN+"Iniciando aplicación en modo "+modo+" el "+sdfDia.format(c.getTime())+" a las "+  sdfHora.format(c.getTime())  +ColorConsola.ANSI_RESET);
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_GREEN+"Ejecutable localizado en "+ruta +ColorConsola.ANSI_RESET);
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_GREEN+"Aplicación iniciada\n\n"+ColorConsola.ANSI_RESET);
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
        //c.add(Calendar.HOUR, -1);
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_YELLOW+"Aplicación detenida el "+sdfDia.format(c.getTime())+" a las "+  sdfHora.format(c.getTime())  +ColorConsola.ANSI_RESET);
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
/*
    public Action onRequest(Http.Request request, Method actionMethod) {
        System.out.println(ColorConsola.SET_BOLD_TEXT+ColorConsola.ANSI_CYAN+"OnRequest"  +ColorConsola.ANSI_RESET);
        return super.onRequest(request, actionMethod);
    }
 */

}
