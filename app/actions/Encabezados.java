package actions;

import classes.pruebaToken;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;

public class Encabezados  extends Action.Simple{

	@Override
	public F.Promise<Result> call(play.mvc.Http.Context ctx) throws Throwable {

		pruebaToken x = new pruebaToken();
		String y = x.generateSafeToken();
		play.mvc.Controller.session("nonce", y);
		Config conf = ConfigFactory.load();
		String url ="http://148.204.111.41:8080	";
		url = conf.getString("urlProduccion");


		ctx.response().setHeader("Referrer-Policy", "same-origin");
		ctx.response().setHeader("Strict-Transport-Security", "max-age=31536000");
		ctx.response().setHeader("X-Frame-Options", "sameorigin");
		//ctx.response().setHeader("Permissions-Policy", "camera 'none'");




		//ctx.response().setHeader("Content-Security-Policy", "script-src 'nonce-"+y+"'");
		ctx.response().setHeader("Content-Security-Policy", "default-src 'self''nonce-"+y+"'");
		//ctx.response().setHeader("Content-Security-Policy", "csrf-token '"+t+"'");


		ctx.response().setHeader("Content-Security-Policy", "script-src 'nonce-"+y+"' " +
				url+" " +
				url+"/assets/gentelella/vendors/jquery/dist/jquery.min.js " +
				"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js " +
				"https://check.dev.ipn.mx/matomo.js");

		ctx.response().setHeader("Content-Security-Policy", "style-src 'self' 'unsafe-inline'");


		ctx.response().setHeader("X-Content-Type-Options", "nosniff");
	//	ctx.response().setHeader("Permissions-Policy", "geolocation=(self "+url+"\"), microphone=()");
	//	ctx.response().setHeader("Permissions-Policy", "fullscreen=(self "+url+"\"), geolocation=*, camera=()");

		ctx.response().setHeader("Permissions-Policy", "geolocation=(), camera=(), microphone=()");





		//ctx.response().setHeader("Set-Cookie", "PLAY_SESSION=123; path=/; SameSite=Strict");
		//ctx.response().setHeader("Set-Cookie", "PLAY_FLASH=456; path=/; SameSite=Lax");





/*		 
 * 
 *  HEADERS QUE PRODUCE
 *  
 *  
 *  comando wget -q -S -O - localhost:9000 | :
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
