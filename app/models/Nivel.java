package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Nivel extends models.utils.PlantillaCatalogo{


    public static Model.Finder<Long,Nivel> find = new Model.Finder<Long,Nivel>(Long.class, Nivel.class);


    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Nivel c: Nivel.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
