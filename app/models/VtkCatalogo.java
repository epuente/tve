package models;

import com.avaje.ebean.Page;
import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import static play.mvc.Controller.session;

@Entity
public class VtkCatalogo extends models.utils.PlantillaModelo{

    @ManyToOne
    public String folio;

    @ManyToOne
    @NotNull
    public UnidadResponsable unidadresponsable;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    @NotNull
    public List<VtkEvento> eventos;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    @NotNull
    public List<VtkNivelAcademico> nivelesacademicos;

    public boolean esAreaCentral;

    @NotNull
    public String claveclasificatoria;





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

    @ManyToOne (optional = false)
    public Personal catalogador;

    @OneToMany (orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "catalogo")
    public List<VtkTimeLine> timeline;

    @Column(length = 1500)
    public String audio;

    @Column(length = 1500)
    public String video;

    @Column(length = 3000)
    public String observaciones;


    public static Model.Finder<Long,VtkCatalogo> find = new Model.Finder<Long,VtkCatalogo>(Long.class, VtkCatalogo.class);

    public static Page<VtkCatalogo> page(int page, int pageSize, String filtro, String columnaOrden, String tipoOrden) {
        if (columnaOrden.compareTo("estado")==0 || columnaOrden.compareTo("tipo")==0)
            columnaOrden+=".descripcion";
        Page<VtkCatalogo> p = null;

        Logger.debug("rolActual " + session("rolActual"));
        Logger.debug("usuario " + session("usuario"));

        // Es supervisor de catalogadores?
        if (session("rolActual").compareTo("133")==0) {
            Logger.debug("camino 133");
             p = find
                    .where("upper(sinopsis) like :cadena OR upper(titulo) like :cadena OR upper(serie.descripcion) like :cadena")
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
                    .where("(catalogador.id = "+session("usuario")+") AND (upper(sinopsis) like :cadena OR upper(titulo) like :cadena OR upper(serie.descripcion) like :cadena)")
                    .setParameter("cadena", "%" + filtro.toUpperCase() + "%")
                    .orderBy(columnaOrden + " " + tipoOrden)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
        }
        Logger.debug("\n\n\n\nVtlCatalogo Retornonado "+p.getList().size());
        return p;
    }

}
