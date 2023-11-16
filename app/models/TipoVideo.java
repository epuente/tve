package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class TipoVideo extends models.utils.PlantillaCatalogo {
    public static Model.Finder<Long,TipoVideo> find = new Model.Finder<Long,TipoVideo>(Long.class, TipoVideo.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(TipoVideo c: TipoVideo.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
