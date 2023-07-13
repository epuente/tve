package models;

import models.utils.PlantillaArchivo;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OficioGuion extends PlantillaArchivo {
    @ManyToOne
    public Oficio oficio;


    public OficioGuion() {
    }

    public OficioGuion(String nombrearchivo, String contenttype, byte[] contenido) {
        this.nombrearchivo = nombrearchivo;
        this.contenttype = contenttype;
        this.contenido = contenido;
    }

    public static Model.Finder<Long,OficioGuion> find = new Model.Finder<Long,OficioGuion>(Long.class, OficioGuion.class);
}
