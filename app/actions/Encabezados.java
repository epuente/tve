package actions;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;

public class Encabezados  extends Action.Simple{

	@Override
	 public F.Promise<Result> call(play.mvc.Http.Context ctx) throws Throwable {
		
		
		pruebaToken x = new pruebaToken();
		String y = x.generateSafeToken();
		
		 ctx.response().setHeader("Referrer-Policy", "same-origin");		 
		 ctx.response().setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		 ctx.response().setHeader("X-Frame-Options", "sameorigin");
		 ctx.response().setHeader("Feature-Policy", "camera 'none'");
		 

/*		 
 * 
 *  HEADERS QUE PRODUCE
 *  
 *  
 *  comando wget -q -S -O - localhost:8080 | :
 *  
 *  Content-Security-Policy: script-src localhost:8080 https://evaluardd.upev.ipn.mx:8080 'unsafe-inline'
			  Content-Type: text/html; charset=utf-8
			  Feature-Policy: camera 'none'
			  Referrer-Policy: same-origin
			  Set-Cookie: PLAY_SESSION="b105ea10426fd057c32fb3e2c9430de5528b5771-csrfToken=00e5e292271e71cae270467a70a463f77b7274e4-1574192422974-a07cc8a206cf339e5979e7ce"; Path=/; HTTPOnly
			  Strict-Transport-Security: max-age=31536000; includeSubDomains
			  X-Content-Type-Options: nosniff
			  X-Frame-Options: sameorigin
			  X-Permitted-Cross-Domain-Policies: master-only
			  X-XSS-Protection: 1; mode=block
			  Content-Length: 10994*/

		 
		 
		 
    return delegate.call(ctx);
	}
	
	

	

}
