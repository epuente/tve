package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Ubicacion extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Ubicacion> find = new Model.Finder<Long,Ubicacion>(Long.class, Ubicacion.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Ubicacion c: Ubicacion.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }    
}
