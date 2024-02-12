package classes;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.validation.constraints.Size;

import controllers.AdminCorreoController;
import models.Ctacorreo;
import play.data.validation.Constraints.MaxLength;

public class miCorreo2 extends Thread{
	public List<String> para;
	public String asunto;
    @Size(max=900)
    @MaxLength(900)
	public String mensaje;
	private String host;
	private String de;
	public Boolean enviado;
	public String mensajeoperacion;

	public Boolean passEncriptado;


	public void enviar()  {
		Ctacorreo cc =Ctacorreo.find.where().eq("activa", true).findUnique();
		if (cc!=null) {
			this.host = cc.hostname;
			//this.host = "smtp.gmail.com";
			this.de = cc.cuenta;
			//	this.de = "eduardo.puente72@gmail.com";
			String user = cc.cuenta;
			String pass =  this.passEncriptado==true? AdminCorreoController.decrypt(cc.contrasenia) : cc.contrasenia;
			String puerto = cc.puerto;
			
			System.out.println("**********************************************************************");
			System.out.println("**  para:"+this.para+"  **");
			System.out.println("**  asunto:"+this.asunto+"  **");
			System.out.println("**  mensaje:"+this.mensaje+"  **");
			System.out.println("**  host:"+this.host+"  **");
			System.out.println("**  de:"+this.de+"  **");
			System.out.println("**  enviado:"+this.enviado+"  **");
			System.out.println("**  mensajeoperacion:"+this.mensajeoperacion+"  **");
			System.out.println("**********************************************************************");

		//	roliwzjcuxsncjsd
			
			
			Properties properties = System.getProperties();    
			properties.setProperty("mail.smtp.host", this.host);		
			properties.setProperty("mail.smtp.port", puerto); 
			properties.setProperty("mail.smtp.auth", "true");
			properties.setProperty("mail.smtp.starttls.enable", "true");		
			properties.setProperty("mail.smtp.ssl.protocols","TLSv1.2");
			
			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			};
			Session session = Session.getInstance(properties, auth);
			MimeMessage message = new MimeMessage(session);
			try{
				message.setFrom(new InternetAddress(this.de));
				for(String destino :para){
					System.out.println("-------------------------------"+destino);
					System.out.println("intento de "+this.de+"  para        "+destino);
					if (destino!= null)
							message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));	
				}			
				message.setSubject(this.asunto, "UTF-8");
				
				System.out.println("Envio de correo a las "+new Date());			
	            MimeBodyPart textBodyPart = new MimeBodyPart();
	            //textBodyPart.setText(this.mensaje);
	            textBodyPart.setContent(this.mensaje,"text/html; charset=utf-8");
	            
	
	            // Para el attach
	            MimeBodyPart attachPart = new MimeBodyPart();            
	        //    String filename ="/home/epuente/Documentos/1010 1015 5577 1743.pdf";
	        //    DataSource source = new FileDataSource(filename);
	        //    attachPart.setDataHandler(new DataHandler(source));
	        //    attachPart.setFileName(filename);
	            
	            
	            
	            //El multipart
	            Multipart multipart = new MimeMultipart();
	         //   multipart.addBodyPart(attachPart);
	            multipart.addBodyPart(textBodyPart);
	            
	            message.setContent(multipart);
	            
				Transport.send(message);
				System.out.print("      Se envió correctamente a ");
				
				for (Address a : message.getAllRecipients()) {
					System.out.print(a.toString()+"  ");
				}
				
				System.out.println();
				this.enviado = true;
				this.mensajeoperacion="Se envió correctamente";
			} catch(MessagingException e){
				System.out.println("error: "+e.getMessage());	
				this.enviado = false;
				this.mensajeoperacion=e.getLocalizedMessage();
			}
			System.out.println("");
		} else {
			System.out.println("NO HAY UNA CUENTA DE CORREO DE SALIDA DEFINIDA COMO ACTIVA");	
			this.enviado = false;
		this.mensajeoperacion="El administrador no ha definido una cuenta de correo de salida activa";			
		}
//System.out.println("mensaje "+this.mensaje); 
	}		
	
	@Override
    public void run() {
        System.out.println("Con Thread ");
        this.enviar();
    }
	
}