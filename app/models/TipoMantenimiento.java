package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class TipoMantenimiento  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,TipoMantenimiento> find = new Model.Finder<Long,TipoMantenimiento>(Long.class, TipoMantenimiento.class);
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(TipoMantenimiento m: TipoMantenimiento.find.all()) {
            options.put(m.id.toString(), m.descripcion);
        }
        return options;
    } 
}
