package actions;

import controllers.routes;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Results;

import java.util.Date;

public class ConfirmarSupCatalogador extends Action.Simple {
    public F.Promise<Result> call(play.mvc.Http.Context ctx) throws Throwable {
    	
    	System.out.println(">> ROL ACTUAL : "+play.mvc.Controller.session("rolActual")+" <<");    	
    	if ( 	play.mvc.Controller.session("roles").isEmpty()  ) {
    		System.out.println("No hay nada en sesion roles");
    		Result unauthorized = Results.redirect(  routes.Application.login()     );
            return F.Promise.pure(unauthorized); 
    	}
    	
        if (play.mvc.Controller.session("rolActual").compareTo("133")==0 ) {
        	System.out.println("desde ConfirmarSupCatalogador "+ctx.request().path()+"  "+new Date());
        	return delegate.call(ctx);
        }
        
     //   Result unauthorized = Results.unauthorized("RegistroAcceso no autorizado, requiere iniciar sesión como usuario autorizado.");
        System.out.println("Se intentó ejecutar un método que solo es accesible al rol de Supervisor de catalogadores.");
        Result unauthorized2 = Results.redirect(  routes.Application.operacionNoPermitida()     );
        return F.Promise.pure(unauthorized2);
    }
}



