package models;

import javax.persistence.Entity;

import play.db.ebean.Model;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Idioma  extends models.utils.PlantillaCatalogo{
	public static Model.Finder<Long,Idioma> find = new Model.Finder<>(Long.class, Idioma.class);

	public static Map<String,String> options() {
		LinkedHashMap<String,String> options = new LinkedHashMap<>();
		for(Idioma c: Idioma.find.findList()) {
			options.put(c.id.toString(), c.descripcion);
		}
		return options;
	}	
}
