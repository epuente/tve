package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Areatematica extends models.utils.PlantillaCatalogo{

    public String sigla;
    public static Model.Finder<Long,Areatematica> find = new Model.Finder<Long,Areatematica>(Long.class, Areatematica.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Areatematica c: Areatematica.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }    
}
