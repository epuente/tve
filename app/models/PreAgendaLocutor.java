package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class PreAgendaLocutor  extends models.utils.PlantillaModelo{


	
	@ManyToOne
	public PreAgenda preagenda;
	
	@ManyToOne
	@NotNull
	public Personal personal;
	

	

	public static Model.Finder<Long,PreAgendaLocutor> find = new Model.Finder<Long,PreAgendaLocutor>(Long.class, PreAgendaLocutor.class);
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(PreAgendaLocutor c: PreAgendaLocutor.find.findList()) {
            options.put(c.id.toString(), c.personal.nombreCompleto());
        }
        return options;
    } 	
	

}
