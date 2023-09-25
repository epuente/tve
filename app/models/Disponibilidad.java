package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Disponibilidad extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Disponibilidad> find = new Model.Finder<Long,Disponibilidad>(Long.class, Disponibilidad.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Disponibilidad c: Disponibilidad.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
