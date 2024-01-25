package models.videoteca;

import models.Personal;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Areatematica extends models.utils.PlantillaCatalogo{

    @ManyToOne
    public Personal catalogador;
    public static Model.Finder<Long,Areatematica> find = new Model.Finder<Long,Areatematica>(Long.class, Areatematica.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Areatematica c: Areatematica.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }    
}
