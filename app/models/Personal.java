package models;

import com.avaje.ebean.Page;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import play.db.ebean.Model;
@Entity
public class Personal  extends models.utils.PlantillaModelo{
	@Column(length=13, unique=true)
	@NotNull
	public String numEmpleado;
	
	@Column(length=25)
	public String paterno;
	
	@Column(length=25)
	public String materno;
	
	@Column(length=25)
	public String nombre;
	/*
	@ManyToOne
	@NotNull
	public TipoPersonal tipo;
	*/
	@ManyToOne	
	public TipoContrato tipocontrato;
	
	
	@Column(length=1)
	public String turno;

	@Column(length=1)
	@NotNull
	public String activo;
	
/*	*/	
	
	
	@OneToMany(mappedBy="personal", cascade=CascadeType.ALL)
	public List<PersonalHorario> horarios;
	
	@OneToMany(mappedBy="personal", cascade=CascadeType.ALL)
	public List<PersonalCambioHorario> cambiosHorarios;	
	
	@OneToMany(mappedBy="personal", cascade=CascadeType.ALL)
	public List<Cuenta> cuentas;
	
	@OneToMany (mappedBy="personal", cascade=CascadeType.ALL)
	public List<PersonalCorreo> correos;
	
	@OneToOne(mappedBy = "personal", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
	public PersonalAvatar avatar;
	
	public static Model.Finder<Long,Personal> find = new Model.Finder<Long,Personal>(Long.class, Personal.class);
    public static Map<String,String> optionsProductores() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        //for(Personal c: Personal.find.where().eq("tipo.id", 2).orderBy("nombre").findList()) {
        for(Personal c: Personal.find.where().eq("cuentas.roles.rol.id", 100).orderBy("nombre").findList()) {
            options.put(c.id.toString(), c.nombreCompleto());
        }
        return options;
    } 	
    
    public static Map<String,String> optionsProductoresActivos() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        //for(Personal c: Personal.find.where().eq("tipo.id", 2).eq("activo", "S").orderBy("nombre").findList()) {
        for(Personal c: Personal.find.where().eq("cuentas.roles.rol.id", 100).eq("activo", "S").orderBy("nombre").findList()) {
            options.put(c.id.toString(), c.nombreCompleto());
        }
        return options;
    }    
    
    public static Map<String,String> optionsProductoresInActivos() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Personal c: Personal.find.where().eq("cuentas.roles.rol.id", 100).eq("activo", "N").orderBy("nombre").findList()) {
            options.put(c.id.toString(), c.nombreCompleto());
        }
        return options;
    }     
    
    public static Map<String,String> optionsOperadoresSala() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        
        List<Personal> auxP = Personal.find.fetch("cuentas").where().eq("cuentas.roles.rol.id", 16).findList();
        
        
        for(Personal c: auxP) {
            options.put(c.id.toString(), c.nombreCompleto());
        }
        return options;
    } 
    
    
    
    
    
    public String nombreCompleto(){
    	return this.nombre+" "+this.paterno+" "+this.materno;
    }


    
    public static Page<Personal> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
    	System.out.println(" * * * * * * * * filtr :"+filtro);    	
    	Page<Personal> p = find
    			.fetch("tipocontrato")
            .where("  numEmpleado like :cadena OR paterno like :cadena  OR  materno like :cadena  OR  nombre like :cadena  OR tipocontrato.descripcion like :cadena OR cuentas.roles.rol.descripcion like :cadena")
           
           
/*            
            .where()
            .or(Expr.ilike("numEmpleado", ":cadena"), Expr.ilike("paterno", "%"+filtro+"%"))
            .or(Expr.ilike("materno", "%"+filtro+"%"), Expr.ilike("nombre", "%"+filtro+"%"))
            .or(Expr.ilike("cuentas.roles.rol.descripcion", "%"+filtro+"%"), Expr.ilike("tipocontrato.descripcion", "%"+filtro+"%"))
            .or(Expr.ilike("turno", "%"+filtro+"%"), Expr.ilike("activo", "%"+filtro+"%"))
            
  */          
            .setParameter("cadena", "%"+filtro+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);


    	System.out.println(" * * * * * * * * registros:"+p.getList().size());
        return p;
            
    }    
    
    // Regresa el horario del personal en JSON
    
    public boolean validarTurnosHorario(Date inicio, Date fin) {
    	boolean r = true;
    	for (PersonalHorario h : this.horarios){
    		if (   ( h.desde.after(inicio) || h.desde.equals(inicio) ) &&  (h.desde.before(fin) || h.desde.equals(fin) ) 
    				&&
    				(h.hasta.after(inicio)  || h.hasta.equals(inicio) ) && (h.hasta.before(fin) ||h.hasta.equals(fin) )
    		   )
    				r =  r && true;
    		else
    				r = r && false;
    		}
		return r;
    }
    
}
