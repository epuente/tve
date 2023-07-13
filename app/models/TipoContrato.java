package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class TipoContrato   extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,TipoContrato> find = new Model.Finder<Long,TipoContrato>(Long.class, TipoContrato.class);
    
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(TipoContrato c: TipoContrato.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    } 	

}
