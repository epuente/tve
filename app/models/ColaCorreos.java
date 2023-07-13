package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.annotation.EnumValue;

import play.db.ebean.Model;

@Entity
public class ColaCorreos  extends models.utils.PlantillaModelo{
	/**
	 * 
	 */
	public ColaCorreos() {
		super();
	}
	
	public ColaCorreos(String correo, String asunto, String contenido) {
		super();
		this.correo = correo;
		this.asunto = asunto;
		this.contenido = contenido;
	}


	@ManyToOne
	@NotNull
	public String correo;
	@NotNull
	// 0 no enviado   1 enviado   2 hubo error  
	public short estado;	
	public short numintentos;
	public String asunto;
	public String folio;
	public String oficioDescripcion;
	public String servicioDescripcion;
	@Column(columnDefinition = "TEXT")
	public String contenido;

	public static Model.Finder<Long,ColaCorreos> find = new Model.Finder<Long,ColaCorreos>(Long.class, ColaCorreos.class);
	
    public enum Status {
        @EnumValue("N")
        NEW,
        
        @EnumValue("A")
        ACTIVE,
        
        @EnumValue("I")
        INACTIVE,
    }	
	
	
}
