package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import models.utils.PlantillaArchivo;
import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class OficioRespuesta  extends PlantillaArchivo {
	@ManyToOne
	public Oficio oficio;

	public OficioRespuesta() {
	}

	public OficioRespuesta(String nombrearchivo, String contenttype, byte[] contenido) {
		this.nombrearchivo = nombrearchivo;
		this.contenttype = contenttype;
		this.contenido = contenido;
	}


	public static Model.Finder<Long,OficioRespuesta> find = new Model.Finder<Long,OficioRespuesta>(Long.class, OficioRespuesta.class);

	@Formats.DateTime(pattern="dd-MM-yyyy")
	public Date fecharespuesta;
	
	@Formats.DateTime(pattern="dd-MM-yyyy")
	public Date foliorespuesta;
}
