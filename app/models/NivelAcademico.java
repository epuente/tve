package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class NivelAcademico extends models.utils.PlantillaCatalogo{


    public static Model.Finder<Long,NivelAcademico> find = new Model.Finder<Long,NivelAcademico>(Long.class, NivelAcademico.class);


    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(NivelAcademico c: NivelAcademico.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
