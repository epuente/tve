package models;

import models.utils.PlantillaArchivo;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class OficioEncuesta extends PlantillaArchivo {
    @OneToOne
    public Oficio oficio;

    public OficioEncuesta() {
    }

    public OficioEncuesta(String nombrearchivo, String contenttype, byte[] contenido) {
        this.nombrearchivo = nombrearchivo;
        this.contenttype = contenttype;
        this.contenido = contenido;
    }


    public static Finder<Long, OficioEncuesta> find = new Finder<Long, OficioEncuesta>(Long.class, OficioEncuesta.class);
}
