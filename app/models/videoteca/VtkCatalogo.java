package models.videoteca;

import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import models.*;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static play.mvc.Controller.session;

@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class VtkCatalogo extends models.utils.PlantillaModelo implements Serializable {

    @ManyToOne
    @Column(length = 30)
    public String folio;

    @ManyToOne
    public UnidadResponsable unidadresponsable;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    @NotNull
    public List<VtkEvento> eventos;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    @NotNull
    public List<VtkNivel> niveles;

    @Column(length = 30)
    public String folioDEV;

    @Column(length = 200)
    @NotNull
    public String titulo;

    @Column(length = 3000)
    @NotNull
    public String sinopsis;

    @ManyToOne
    public Serie serie;

    @NotNull
    @Column(length = 30)
    public String clave;

    @Column(length = 5)
    public String obra;

    @ManyToOne
    @NotNull
    public VtkFormato formato;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    @NotNull
    public List<PalabraClave> palabrasClave;

    @ManyToOne
    public Idioma idioma;



    // Soporte de ayuda para personas con debilidad auditiva (sordos). Incluye lenguaje de señas.

    public boolean accesibilidadAudio;

    // Soporte de ayuda para personas con debilidad visual (ciegos). Incluye subtítulos descriptivos.
    public boolean accesibilidadVideo;
    @ManyToOne
    public TipoGrabacion tipograbacion;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<Credito> creditos;

    @ManyToOne
    public Produccion produccion;

    public Long duracion;

    @Column(length = 10)
    public String fechaProduccion;

    @Column(length = 10)
    public String fechaPublicacion;
    /*
    @ManyToOne
    public Disponibilidad disponibilidad;
     */

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<VtkDisponibilidad> disponibilidades;

    public String disponibilidadOtra;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    @NotNull
    public List<VtkAreatematica> areastematicas;

    public String areaTematicaOtra;

    public String nresguardo;

    public String liga;

    @ManyToOne (optional = false)
    public Personal catalogador;

    @OneToMany (orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<VtkTimeLine> timeline;

    @Column(length = 150)
    @ManyToOne
    @NotNull
    public TipoAudio audio;

    @Column(length = 2)
    @NotNull
    public String calidadAudio;

    @NotNull
    @ManyToOne
    public TipoVideo video;

    @Column(length = 2)
    @NotNull
    public String calidadVideo;

    @Column(length = 3000)
    public String observaciones;

    @Version
    public java.util.Date version;

    @ManyToOne
    public Personal validador;

    public boolean validado=false;

    public static Model.Finder<Long,VtkCatalogo> find = new Model.Finder<Long,VtkCatalogo>(Long.class, VtkCatalogo.class);



    public static Page<VtkCatalogo> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
        if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
            columnaOrden+=".descripcion";
        Page<VtkCatalogo> p = null;

        Logger.debug("rolActual " + session("rolActual"));
        Logger.debug("usuario " + session("usuario"));

        String predicados = "unaccent(upper(clave)) like unaccent(:cadena) OR unaccent(upper(serie.descripcion)) like unaccent(:cadena) OR unaccent(upper(titulo)) like unaccent(:cadena) OR unaccent(upper(obra)) like unaccent(:cadena) ";

        // Es administrador?
        if (session("rolActual").compareTo("1")==0) {
            Logger.debug("camino 1");
            p = find
//                    .where("upper(sinopsis) like :cadena OR upper(titulo) like :cadena OR upper(serie.descripcion) like :cadena")
                    .where(  "unaccent(upper(catalogador.nombre)) like unaccent(:cadena) OR unaccent(upper(catalogador.paterno)) like unaccent(:cadena) OR unaccent(upper(catalogador.materno)) like unaccent(:cadena) OR " +   predicados )
                    .setParameter("cadena", "%" + filtro.toUpperCase() + "%")
                    .orderBy(columnaOrden + " " + tipoOrden)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
        }

        // Es supervisor de catalogadores?
        if (session("rolActual").compareTo("133")==0) {
            Logger.debug("camino 133");
             p = find
//                    .where("upper(sinopsis) like :cadena OR upper(titulo) like :cadena OR upper(serie.descripcion) like :cadena")
                    .where(  "unaccent(upper(catalogador.nombre)) like unaccent(:cadena) OR unaccent(upper(catalogador.paterno)) like unaccent(:cadena) OR unaccent(upper(catalogador.materno)) like unaccent(:cadena) OR " +   predicados )
                    .setParameter("cadena", "%" + filtro.toUpperCase() + "%")
                    .orderBy(columnaOrden + " " + tipoOrden)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
        }
        // Es catalogador?
        if (session("rolActual").compareTo("132")==0) {
            Logger.debug("camino 132");
            p = find
                    .where("(catalogador.id = "+session("usuario")+") AND ("+predicados+")")
                    .setParameter("cadena", "%" + filtro.toUpperCase() + "%")
                    .orderBy(columnaOrden + " " + tipoOrden)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
        }

        // Es productor?
        if (session("rolActual").compareTo("100")==0) {
            Logger.debug("camino 100");
            p = find
                    .where("(validador.id = "+session("usuario")+") AND ("+predicados+")")
                    .setParameter("cadena", "%" + filtro.toUpperCase() + "%")
                    .orderBy(columnaOrden + " " + tipoOrden)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
        }
        if (p!=null)
            Logger.debug("\n\n\n\nVtlCatalogo Retornonado "+p.getList().size());
        return p;
    }



}
