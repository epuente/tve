package actions;

import java.util.Date;

import classes.ColorConsola;
import controllers.routes;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Results;
import classes.ColorConsola.*;

public class ConfirmarAdministradorOperadorSala extends Action.Simple {
    public F.Promise<Result> call(play.mvc.Http.Context ctx) throws Throwable {
    	if ( 	play.mvc.Controller.session("roles").isEmpty()  ) {
    		System.out.println("No hay nada en sesion roles");
    		Result unauthorized = Results.redirect(  routes.Application.login()     );
            return F.Promise.pure(unauthorized); 
    	}
    	
        if (play.mvc.Controller.session("rolActual").compareTo("1")==0 ||  play.mvc.Controller.session("rolActual").compareTo("16")==0) {
        	System.out.println("desde ConfirmarAdministradorOperadorSala "+ctx.request().path()+"  "+new Date());
        	return delegate.call(ctx);
        }
        
     //   Result unauthorized = Results.unauthorized("RegistroAcceso no autorizado, requiere iniciar sesión como usuario autorizado.");
        System.out.println(ColorConsola.ANSI_RED+"Se intentó ingresar a una pag. accesible a otro rol."+ColorConsola.ANSI_RESET);
        Result unauthorized2 = Results.redirect(  routes.Application.operacionNoPermitida()     );
        return F.Promise.pure(unauthorized2);
    }
}



