package models;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Page;

import play.db.ebean.Model;
@Entity
public class UnidadResponsable  extends models.utils.PlantillaModelo{

	

	@Column(length=6)
	public String clave;
	
	@NotNull
	@Column(length=150)
	public String nombreLargo;
	
	@Column(length=50)
	public String nombreCorto;
	

	@Column(length=2)
	public String tipo;
	
	
	@Column(length=1)
	public String nivel;
	
	
	@Column(length=1)
	public String areaConocimiento;

	@ManyToOne
	public Personal catalogador;

	
	
	
	public static Model.Finder<Long,UnidadResponsable> find = new Model.Finder<Long,UnidadResponsable>(Long.class, UnidadResponsable.class);
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(UnidadResponsable c: UnidadResponsable.find.orderBy("nombreLargo").findList()) {
            options.put(c.id.toString(), c.nombreLargo);
        }
        return options;
    } 	

    
    public static Page<UnidadResponsable> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
    	System.out.println(" * * * * * * * * ");    	
    	Page<UnidadResponsable> p = find
            .where("  nombreLargo like :cadena OR nombreCorto like :cadena")
            .setParameter("cadena", "%"+filtro+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);


    	System.out.println(" * * * * * * * * registros:"+p.getTotalRowCount());
        return p;
            
    }     

}
