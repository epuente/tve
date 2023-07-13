package models;

import models.utils.PlantillaArchivo;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class OficioEntradaSalida extends PlantillaArchivo {
    @OneToOne
    public Oficio oficio;

    public OficioEntradaSalida() {
    }

    public OficioEntradaSalida(String nombrearchivo, String contenttype, byte[] contenido) {
        this.nombrearchivo = nombrearchivo;
        this.contenttype = contenttype;
        this.contenido = contenido;
    }

    public static Finder<Long, OficioEntradaSalida> find = new Finder<Long, OficioEntradaSalida>(Long.class, OficioEntradaSalida.class);
}
