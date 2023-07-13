package controllers;

import static play.data.Form.form;

import java.util.List;

import models.Rol;
import models.Vehiculo;
import play.data.Form;
import play.mvc.Result;

import views.html.catalogos.Vehiculo.*;

public class AdminVehiculoController extends ControladorSeguroAdministrador{

    public static Result GO_HOME = redirect(
            routes.AdminVehiculoController.list()
        );	
	
	public static Result list(){
		return ok(list.render( Vehiculo.find.all()	)   );
		
	}	
	
    public static Result create(){    	
    	Form<Vehiculo> forma = play.data.Form.form(Vehiculo.class);
    	List<Rol> roles = Rol.find.all();
        return ok(
        		createForm.render(forma, roles)
        );   	
    }	
    
    
    public static Result save(){
        Form<Vehiculo> forma = form(Vehiculo.class).bindFromRequest();
System.out.println(forma);        
        if(forma.hasErrors()) {
            return badRequest(createForm.render(forma, Rol.find.all()));
        }
        forma.get().save();
	
        flash("success", "Se agregó el vehículo "+forma.get().descripcion);
        return list();   	
    }   
    
    
    public static Result edit(Long id) {
    	Vehiculo aux = Vehiculo.find.byId(id);
    	Form<Vehiculo> forma = form(Vehiculo.class).fill( aux  );
	        return ok( editForm.render(id, forma, Rol.find.all(), "edit"  ));

    }    

    public static Result update(Long id) {
        Form<Vehiculo> forma = form(Vehiculo.class).bindFromRequest();
        
System.out.println("desde AdminVehiculoController.update....");
System.out.println(forma);
        if(forma.hasErrors()) {
            return badRequest(editForm.render(id, forma, Rol.find.all() , "edit"));
        }
        forma.get().update(id);
        flash("success", "Se actualizó el vehículo "+ forma.get().descripcion);
        return GO_HOME;
    }    
    
    
    public static Result delete(Long id) {
    	System.out.println("Desde AdminVehiculoController.delete");
        Vehiculo x = Vehiculo.find.ref(id);
        String nombre = x.descripcion;
        x.delete();
        flash("success", "Se eliminó el vehículo "+nombre);
        return list();
    }  	
	
	
	
}
