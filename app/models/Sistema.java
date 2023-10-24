package models;

import com.avaje.ebean.EbeanServer;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Sistema extends models.utils.PlantillaCatalogo{

    @ManyToOne
    public Personal usuario;

    public static Model.Finder<Long,Sistema> find = new Model.Finder<Long,Sistema>(Long.class, Sistema.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Sistema c: Sistema.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
