package models;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import play.data.format.Formats;
import play.db.ebean.Model;
@Entity
public class Oficio  extends models.utils.PlantillaModelo{

	@Column(length=25, unique=false )
	public String oficio;

	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)
	public List<Folio> folios;
	
	@NotNull
	@ManyToOne
	public UnidadResponsable urremitente;

	@NotNull
	@Column(length=600)
	public String descripcion;
	
	@Column(length=150)
	public String titulo;
		
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date fecharemitente;
	
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date fecharecibidodtve;
	
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date fecharecibidoupev;

	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)
	public List<OficioRespuesta> oficiosrespuestas;

	@OneToMany(mappedBy = "oficio", cascade = CascadeType.ALL)
	public List<OficioMinuta> minutas;

	@OneToMany(mappedBy = "oficio", cascade = CascadeType.ALL)
	public List<OficioGuion> guiones;

	@OneToOne(mappedBy = "oficio", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
	public OficioEntradaSalida entradassalida;

	@OneToMany(mappedBy = "oficio", cascade = CascadeType.ALL)
	public List<OficioBitacora> bitacoras;

	@OneToOne(mappedBy = "oficio", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
	public OficioEvidenciaEntrega evidenciaentrega;

	@OneToOne(mappedBy = "oficio", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
	public OficioEncuesta encuesta;
	
	@Column(length=600)
	public String observacion;


	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)
	public List<OficioFechaGrabacion> fechagrabaciones;	
	
	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)
	public List<OficioServicioSolicitado> servicios;
	
	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)	
	public List<OficioContacto> contactos;
	
	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)
	public List<OficioProductorSolicitado> productoresSolicitados;

	@OneToMany(mappedBy="oficio", cascade=CascadeType.ALL)
	public List<OficioImagen> imagenes;
	
	
	
	
	public static Model.Finder<Long,Oficio> find = new Model.Finder<Long,Oficio>(Long.class, Oficio.class);	
    
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Oficio c: Oficio.find.findList()) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }

	public boolean tieneArchivos() {
		boolean retorno =false;
		if ( !this.imagenes.isEmpty()
				|| !this.oficiosrespuestas.isEmpty()
				|| !this.minutas.isEmpty()
				|| !this.guiones.isEmpty()
				|| this.entradassalida != null
				|| !this.bitacoras.isEmpty()
				|| this.evidenciaentrega!=null
				|| this.encuesta!=null
			)
			retorno = true;

		return retorno;
	}
}
