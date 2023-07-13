package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;
@Entity
public class PreAgendaOperadorSala  extends models.utils.PlantillaModelo{


	
	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	public Personal personal;
	

	

	public static Model.Finder<Long,PreAgendaOperadorSala> find = new Model.Finder<Long,PreAgendaOperadorSala>(Long.class, PreAgendaOperadorSala.class);
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(PreAgendaOperadorSala c: PreAgendaOperadorSala.find.findList()) {
            options.put(c.id.toString(), c.personal.nombreCompleto());
        }
        return options;
    } 	
	

}
