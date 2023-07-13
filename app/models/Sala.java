package models;

import com.avaje.ebean.Page;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import play.db.ebean.Model;
@Entity
public class Sala extends models.utils.PlantillaCatalogo  {
    @OneToMany(mappedBy="sala", cascade=CascadeType.ALL)
    public List<SalaMantenimiento> mantenimiento;
    
    @OneToMany(mappedBy="sala", cascade=CascadeType.ALL)
    public List<OperadorSala> operadores;
    


    public static Model.Finder<Long,Sala> find = new Model.Finder<Long,Sala>(Long.class, Sala.class);    

    public static Page<Sala> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
    	Page<Sala> p = find
            .where("descripcion like :cadena")
            .setParameter("cadena", "%"+filtro+"%")            
           //     .orderBy( columnaOrden +" "+tipoOrden +", operadores.personal.turno")
                .findPagingList(pageSize)
                .setFetchAhead(true)
                .getPage(page);
    	System.out.println();
    	System.out.println();
    	for (Sala s : p.getList()) {
    		System.out.println("sala: "+s.descripcion);
    		for (OperadorSala op :  s.operadores) {
    			System.out.println(". . "+op.personal.nombreCompleto());
    		}
    	}
    	System.out.println();
    	System.out.println();
        return p;
    }
}
