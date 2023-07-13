package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import play.data.format.Formats;
import play.db.ebean.Model;
@Entity
public class SalaMantenimiento  extends models.utils.PlantillaModelo{

	
	@ManyToOne
	@NotNull
	public Sala sala;
	
	@NotNull
	@ManyToOne
	public TipoMantenimiento tipo;
	
	@NotNull	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm")
	public Date desde;
	
	public Date getDesde() {
		return desde;
	}


	@NotNull
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm")
	public Date hasta;

	@Column(length=800)
	public String motivo;
	
	@Version
	public java.util.Date version;	
	

	
    public static Model.Finder<Long,SalaMantenimiento> find = new Model.Finder<Long,SalaMantenimiento>(Long.class, SalaMantenimiento.class);		

}
