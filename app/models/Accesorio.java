package models;

import com.avaje.ebean.Page;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;
@Entity
public class Accesorio extends  models.utils.PlantillaModelo {
	@ManyToOne
	public TipoEquipoAccesorio tipo;

	@NotNull
	@Column(length=100)
	public String descripcion;
	
	@ManyToOne
	public EstadoEquipoAccesorioVehiculo estado;
	
	public String serie;

	public String modelo;
	
	@Column(length=600)
	public String observacion;

    public static Model.Finder<Long,Accesorio> find = new Model.Finder<Long,Accesorio>(Long.class, Accesorio.class);
    
    
    public static Page<Accesorio> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
    	if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
    		columnaOrden+=".descripcion";		
    	Page<Accesorio> p = find
    		.fetch("estado")	
            .where("descripcion like :cadena OR serie like :cadena OR tipo.descripcion like :cadena OR modelo like :cadena OR estado.descripcion like :cadena OR observacion like :cadena")
            .setParameter("cadena", "%"+filtro+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)                
                .setFetchAhead(false)
                .getPage(page);
//    	System.out.println(" * * * * * * * * registros:"+p.getTotalRowCount());
        return p;
    }       
    

}
