package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class VtkFormato extends models.utils.PlantillaCatalogo {

    @ManyToOne
    public Personal usuario;

    public static Model.Finder<Long,VtkFormato> find = new Model.Finder<Long,VtkFormato>(Long.class, VtkFormato.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(VtkFormato c: VtkFormato.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
