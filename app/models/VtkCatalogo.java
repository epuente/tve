package models;

import com.avaje.ebean.Page;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Entity
public class VtkCatalogo extends models.utils.PlantillaModelo{
    @Column(length = 1000)
    public String titulo;
    @Column(length = 10000)
    public String sinopsis;


    @ManyToOne (optional = false)
    public Serie serie;

    public String clave;

    public String obra;

    @ManyToOne (optional = false)
    public VtkFormato formato;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<PalabraClave> palabrasClave;

    @ManyToOne (optional = false)
    public Idioma idioma;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<Credito> creditos;

    @ManyToOne (optional = false)
    public Produccion produccion;

    public Long duracion;

    public Date anioProduccion;

    @ManyToOne (optional = false)
    public Sistema sistema;

    @ManyToOne
    public Disponibilidad disponibilidad;

    @ManyToOne (optional = true)
    public Ubicacion ubicacion;

    @ManyToOne (optional = false)
    public Areatematica areatematica;


    public String nresguardo;


    public String liga;

    public static Model.Finder<Long,VtkCatalogo> find = new Model.Finder<Long,VtkCatalogo>(Long.class, VtkCatalogo.class);

    public static Page<VtkCatalogo> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
        if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
            columnaOrden+=".descripcion";
        Page<VtkCatalogo> p = find
                .where("upper(sinopsis) like :cadena OR upper(titulo) like :cadena OR upper(serie.descripcion) like :cadena")
                .setParameter("cadena", "%"+filtro.toUpperCase()+"%")
                .orderBy( columnaOrden +" "+tipoOrden )
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
        return p;
    }

}
