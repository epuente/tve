package actions;

import classes.nonceToken;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;

public class Encabezados  extends Action.Simple {

	@Override
	public F.Promise<Result> call(play.mvc.Http.Context ctx) throws Throwable {

		nonceToken x = new nonceToken();
		String y = x.generateSafeToken();
		play.mvc.Controller.session("nonce", y);
		Config conf = ConfigFactory.load();
		String url = "http://148.204.111.41:8080	";
		url = conf.getString("urlProduccion");


		//ctx.response().setHeader("Set-Cookie", String.format("%s; %s", "PLAY_SESSION", "SameSite=Strict"));



		ctx.response().setHeader("Referrer-Policy", "same-origin");
		ctx.response().setHeader("Strict-Transport-Security", "max-age=31536000");
		ctx.response().setHeader("X-Frame-Options", "sameorigin");
		//ctx.response().setHeader("Permissions-Policy", "camera 'none'");


		//ctx.response().setHeader("Content-Security-Policy", "script-src 'nonce-"+y+"'");
		ctx.response().setHeader("Content-Security-Policy", "default-src 'self''nonce-" + y + "'");
		//ctx.response().setHeader("Content-Security-Policy", "csrf-token '"+t+"'");


		ctx.response().setHeader("Content-Security-Policy", "script-src 'nonce-" + y + "' " +
				url + " " +
				url + "/assets/gentelella/vendors/jquery/dist/jquery.min.js " +
				"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js " +
				"https://check.dev.ipn.mx/matomo.js");

		ctx.response().setHeader("Content-Security-Policy", "style-src 'self' 'unsafe-inline'");


		ctx.response().setHeader("X-Content-Type-Options", "nosniff");
		//	ctx.response().setHeader("Permissions-Policy", "geolocation=(self "+url+"\"), microphone=()");
		//	ctx.response().setHeader("Permissions-Policy", "fullscreen=(self "+url+"\"), geolocation=*, camera=()");

		ctx.response().setHeader("Permissions-Policy", "geolocation=(), camera=(), microphone=()");

		return delegate.call(ctx);
	}
}


/*		 
 * 
 *  HEADERS QUE PRODUCE
 *  
 *  
 *  comando: wget -q -S -O - localhost:9000 | :
 *
 * regresa:
 * 	HTTP/1.1 200 OK
  	Content-Security-Policy: style-src 'self' 'unsafe-inline'
  	Content-Type: text/html; charset=utf-8
  	Permissions-Policy: geolocation=(), camera=(), microphone=()
  	Referrer-Policy: same-origin
  	Set-Cookie: PLAY_SESSION="3365ab521e94e032f60440cd70fa83f78d939857-nonce=nYgYcYni3lxGkBYFmyJQwARohdE&csrfToken=8e58ffdc5f322d66293be8298d15ab9a94e90e8c-1703003776665-d08d3d161959d097635a45d3"; Path=/; HTTPOnly
  	Strict-Transport-Security: max-age=31536000
  	X-Content-Type-Options: nosniff
  	X-Frame-Options: sameorigin
  	Content-Length: 11282

*/





