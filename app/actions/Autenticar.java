package actions;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;

public class Autenticar extends Action.Simple {  
    public F.Promise<Result> call(play.mvc.Http.Context ctx) throws Throwable {
    	String token = play.mvc.Controller.session("usuario");       
        if (token != null ) {
        	play.mvc.Controller.response().setHeader("Cache-Control","no-cache");      	    	
        	return delegate.call(ctx);
        }
      //  Result unauthorized = Results.unauthorized("RegistroAcceso no autorizado, requiere iniciar sesión como usuario autorizado.");
        Result unauthorized = redirect( "/usuarioSinAutorizar"   );
        return F.Promise.pure(unauthorized);
    }
}
