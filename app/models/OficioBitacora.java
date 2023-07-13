package models;

import models.utils.PlantillaArchivo;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OficioBitacora extends PlantillaArchivo {
    @ManyToOne
    public Oficio oficio;

    public OficioBitacora() {
    }

    public OficioBitacora(String nombrearchivo, String contenttype, byte[] contenido) {
        this.nombrearchivo = nombrearchivo;
        this.contenttype = contenttype;
        this.contenido = contenido;
    }

    public static Finder<Long, OficioBitacora> find = new Finder<Long, OficioBitacora>(Long.class, OficioBitacora.class);
}
