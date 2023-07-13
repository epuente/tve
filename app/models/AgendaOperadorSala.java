package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class AgendaOperadorSala extends models.utils.PlantillaModelo {
	@ManyToOne
	public Agenda agenda;
	
	@ManyToOne
	public Personal personal;

	public static Model.Finder<Long, AgendaOperadorSala> find = new Model.Finder<Long, AgendaOperadorSala>(Long.class, AgendaOperadorSala.class);
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(AgendaOperadorSala c: AgendaOperadorSala.find.findList()) {
            options.put(c.id.toString(), c.personal.nombreCompleto());
        }
        return options;
    } 	
}
