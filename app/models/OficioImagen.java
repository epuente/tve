package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.utils.PlantillaArchivo;
import play.db.ebean.Model;

import java.io.Serializable;

@Entity
public class OficioImagen  extends PlantillaArchivo implements Serializable {
	@ManyToOne
	public Oficio oficio;

	public static Model.Finder<Long,OficioImagen> find = new Model.Finder<Long,OficioImagen>(Long.class, OficioImagen.class);


	public OficioImagen(String nombrearchivo, String contenttype, byte[] contenido) {
		this.nombrearchivo = nombrearchivo;
		this.contenttype = contenttype;
		this.contenido = contenido;
	}

	public OficioImagen() {
	}
}
