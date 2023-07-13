package controllers;

import static play.data.Form.form;

import java.util.List;

import models.Rol;
import models.Formato;
import play.data.Form;
import play.mvc.Result;

import views.html.catalogos.Formato.*;

public class AdminFormatoController extends ControladorSeguroAdministrador{

    public static Result GO_HOME = redirect(
            routes.AdminFormatoController.list()
        );	
	
	public static Result list(){
		return ok(list.render( Formato.find.all()	)   );
		
	}	
	
    public static Result create(){    
System.out.println("Desde FormatoController.create...");    	
    	Form<Formato> forma = play.data.Form.form(Formato.class);
    	List<Rol> roles = Rol.find.all();
        return ok(
        		createForm.render(forma, roles)
        );   	
    }	
    
    
    public static Result save(){
        Form<Formato> forma = form(Formato.class).bindFromRequest();
System.out.println(forma);        
        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, Rol.find.all()));
        }
        forma.get().save();
	
        flash("success", "Se agregó el formato "+forma.get().descripcion);
        return list();   	
    }   
    
    
    public static Result edit(Long id) {
    	Formato aux = Formato.find.byId(id);
    	Form<Formato> forma = form(Formato.class).fill( aux  );
	        return ok( editForm.render(id, forma, Rol.find.all(), "edit"  ));

    }    

    public static Result update(Long id) {
        Form<Formato> forma = form(Formato.class).bindFromRequest();
        
System.out.println("desde FormatoController.update....");
System.out.println(forma);
        if(forma.hasErrors()) {
            return badRequest(editForm.render(id, forma, Rol.find.all() , "edit"));
        }
        forma.get().update(id);
        flash("success", "Se actualizó el formato "+ forma.get().descripcion);
        return GO_HOME;
    }    
    
    
    public static Result delete(Long id) {
    	System.out.println("Desde FormatoController.delete");
        Formato x = Formato.find.ref(id);
        String nombre = x.descripcion;
        x.delete();
        flash("success", "Se eliminó el formato "+nombre);
        return list();
    }  	
	
	
	
}
