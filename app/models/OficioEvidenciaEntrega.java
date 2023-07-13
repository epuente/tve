package models;

import models.utils.PlantillaArchivo;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class OficioEvidenciaEntrega extends PlantillaArchivo {
    @OneToOne
    public Oficio oficio;

    public OficioEvidenciaEntrega() {
    }

    public OficioEvidenciaEntrega(String nombrearchivo, String contenttype, byte[] contenido) {
        this.nombrearchivo = nombrearchivo;
        this.contenttype = contenttype;
        this.contenido = contenido;
    }

    public static Finder<Long, OficioEvidenciaEntrega> find = new Finder<Long, OficioEvidenciaEntrega>(Long.class, OficioEvidenciaEntrega.class);
}
