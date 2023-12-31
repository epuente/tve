package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class TipoGrabacion extends models.utils.PlantillaCatalogo{

    public static Finder<Long, TipoGrabacion> find = new Finder<Long, TipoGrabacion>(Long.class, TipoGrabacion.class);


    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(TipoGrabacion c: TipoGrabacion.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
