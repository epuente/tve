package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class Servicio  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,Servicio> find = new Model.Finder<Long,Servicio>(Long.class, Servicio.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Servicio c: Servicio.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }

        return options;
    }	
	
}
