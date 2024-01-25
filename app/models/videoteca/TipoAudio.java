package models.videoteca;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class TipoAudio extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,TipoAudio> find = new Model.Finder<Long,TipoAudio>(Long.class, TipoAudio.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(TipoAudio c: TipoAudio.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }    
}
