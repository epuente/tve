package models;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class Servicio  extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Servicio> find = new Model.Finder<>(Long.class, Servicio.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for(Servicio c: Servicio.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }


    public static Map<String,String> optionsVTK() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for(Servicio c: Servicio.find.where().idIn(Arrays.asList(1,2) ).findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
