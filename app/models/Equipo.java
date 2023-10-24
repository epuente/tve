package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Page;

import play.db.ebean.Model;
@Entity
public class Equipo  extends models.utils.PlantillaModelo{
	
	/**
	 * 
	 */
	

	
	@NotNull
	@Column(length=100)
	public String descripcion;
	
	@ManyToOne
	public EstadoEquipoAccesorioVehiculo estado;
	
	@Column(length=50)
	public String serie;
	@Column(length=100)
	public String marca;
	@Column(length=100)
	public String modelo;
	
	@Column(length=600)
	public String observacion;

    public static Model.Finder<Long,Equipo> find = new Model.Finder<Long,Equipo>(Long.class, Equipo.class);		

    public static Page<Equipo> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
    	System.out.println(" * * * * * * * * ");    	
    	Page<Equipo> p = find
    		.fetch("estado")	
            .where("  descripcion like :cadena OR estado.descripcion like :cadena  OR serie like :cadena  OR  marca like :cadena  OR  modelo like :cadena  OR  observacion like :cadena")
            .setParameter("cadena", "%"+filtro+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)                
                .setFetchAhead(false)
                .getPage(page);
//    	System.out.println(" * * * * * * * * registros:"+p.getTotalRowCount());
        return p;
    }      
    
    
}
