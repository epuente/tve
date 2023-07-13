package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import play.db.ebean.Model;
@Entity
public class Rol  extends models.utils.PlantillaCatalogo{
	@OneToMany
	private List<RolDerecho> derechos; 

	public static Model.Finder<Long,Rol> find = new Model.Finder<Long,Rol>(Long.class, Rol.class);
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Rol c: Rol.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    } 	
	
	
}
