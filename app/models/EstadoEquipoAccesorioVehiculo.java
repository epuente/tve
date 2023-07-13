package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class EstadoEquipoAccesorioVehiculo  extends models.utils.PlantillaModelo {
	@Column(length=50)
	public String descripcion;

	public static Model.Finder<Long,EstadoEquipoAccesorioVehiculo> find = new Model.Finder<Long,EstadoEquipoAccesorioVehiculo>(Long.class, EstadoEquipoAccesorioVehiculo.class);
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(EstadoEquipoAccesorioVehiculo c: EstadoEquipoAccesorioVehiculo.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }
}
