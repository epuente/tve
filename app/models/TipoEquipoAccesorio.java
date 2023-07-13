package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class TipoEquipoAccesorio  extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,TipoEquipoAccesorio> find = new Model.Finder<Long,TipoEquipoAccesorio>(Long.class, TipoEquipoAccesorio.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(TipoEquipoAccesorio c: TipoEquipoAccesorio.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }     
    
}
