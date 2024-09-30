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
            .where("  unaccent(lower(descripcion)) like :cadena OR unaccent(lower(estado.descripcion)) like :cadena  OR unaccent(lower(serie)) like :cadena  OR  unaccent(lower(marca)) like :cadena  OR  unaccent(lower(modelo)) like :cadena  OR  unaccent(lower(observacion)) like :cadena")
            .setParameter("cadena", "%"+filtro.toLowerCase()+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)                
                .setFetchAhead(false)
                .getPage(page);
//    	System.out.println(" * * * * * * * * registros:"+p.getTotalRowCount());
        return p;
    }      
    
    
}
