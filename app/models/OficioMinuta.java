package models;

import models.utils.PlantillaArchivo;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OficioMinuta extends PlantillaArchivo {
    @ManyToOne
    public Oficio oficio;

    public OficioMinuta() {
        super();
    }

    public OficioMinuta(String nombrearchivo, String contenttype, byte[] contenido) {
        this.nombrearchivo = nombrearchivo;
        this.contenttype = contenttype;
        this.contenido = contenido;
    }

    public static Model.Finder<Long,OficioMinuta> find = new Model.Finder<Long,OficioMinuta>(Long.class, OficioMinuta.class);
}
