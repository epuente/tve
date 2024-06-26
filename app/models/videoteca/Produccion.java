package models.videoteca;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Produccion extends models.utils.PlantillaCatalogo{

    public static Model.Finder<Long,Produccion> find = new Model.Finder<Long,Produccion>(Long.class, Produccion.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Produccion c: Produccion.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }

}
