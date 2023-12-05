package models;

import com.avaje.ebean.Page;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Catalogador extends models.utils.PlantillaModelo{
    @NotNull
    @Column(length=25)
    public String paterno;

    @NotNull
    @Column(length=25)
    public String materno;

    @NotNull
    @Column(length=25)
    public String nombre;

    @NotNull
    @Column(length=25)
    public String password;

    public static  Model.Finder<Long, Catalogador> find = new Model.Finder<Long, Catalogador>(Long.class, Catalogador.class);

    public static Page<Catalogador> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
        if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
            columnaOrden+=".descripcion";
        Page<Catalogador> p = find
                .where("upper(nombre) like :cadena OR upper(paterno) like :cadena OR upper(materno) like :cadena")
                .setParameter("cadena", "%"+filtro.toUpperCase()+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        return p;
    }


    public static Page<Personal> page2(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
        List<String> personalIds = new ArrayList<String>();
        if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
            columnaOrden+=".descripcion";

        // Encontrar quienes tienen el rol de catalogador (rol 132)
        List<Cuenta> cuentas = Cuenta.find.where().eq("roles.id", null).filterMany("roles").eq("id", 132).findList();
        for (Cuenta c : Cuenta.find.all() ){
            for (CuentaRol crol  : c.roles){
                if ( crol.rol.id == 132  )
                    personalIds.add(c.personal.id.toString());
            }

        }

        String cadena2 =    String.join( ",", personalIds);

        System.out.println("\n\n\ncadena "+cadena2+"\n\n");


        Page<Personal> p = Personal.find
                //.where("id = 93 AND  numEmpleado like :cadena OR paterno like :cadena  OR  materno like :cadena  OR  nombre like :cadena  OR tipocontrato.descripcion like :cadena OR cuentas.roles.rol.descripcion like :cadena")
                .where("  (id in ("+cadena2+") ) AND(  numEmpleado like :cadena OR paterno like :cadena  OR  materno like :cadena  OR  nombre like :cadena  OR tipocontrato.descripcion like :cadena OR cuentas.roles.rol.descripcion like :cadena)")
                .setParameter("cadena", "%"+filtro+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);



      //  List<Long> ctasCatalogadores = Cuenta.find.where().ne("peronal.id", null).filterMany("roles").eq("id", 133).findList();
        Logger.debug("Catalogador.page2 regresa:"+p.getList().size()+" registros");
        return p;
    }




    public String nombreCompleto(){
        return nombre+" "+paterno+" "+materno;
    }
    
}
