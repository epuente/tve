package models;

import com.avaje.ebean.Page;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Entity
public class VtkCatalogo extends models.utils.PlantillaModelo{
    public String titulo;
    public String sinopsis;

    @ManyToOne
    public Serie serie;

    public String clave;

    public int obraTomo;
    public int obraTotal;

    @ManyToOne
    public VtkFormato formato;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<PalabraClave> palabrasClave;

    @ManyToOne
    public Idioma idioma;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<Credito> creditos;

    @ManyToOne
    public Produccion produccion;

    public Duration duracion;

    public Date anioProduccion;

    @ManyToOne
    public Sistema sistema;

    @ManyToOne
    public Disponibilidad disponibilidad;

    @ManyToOne
    public Ubicacion ubicacion;

    @ManyToOne
    public Areatematica areatematica;

    public String ubicación;



    public String nresguardo;

    public static Model.Finder<Long,VtkCatalogo> find = new Model.Finder<Long,VtkCatalogo>(Long.class, VtkCatalogo.class);

    public static Page<VtkCatalogo> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
        if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
            columnaOrden+=".descripcion";
        Page<VtkCatalogo> p = find
                .where("sinopsis like :cadena OR titulo like :cadena OR serie.descripcion like :cadena")
                .setParameter("cadena", "%"+filtro+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
//    	System.out.println(" * * * * * * * * registros:"+p.getTotalRowCount());
        return p;
    }

}
