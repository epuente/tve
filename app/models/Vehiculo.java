package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import play.db.ebean.Model;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
public class Vehiculo  extends models.utils.PlantillaModelo{

	@SequenceGenerator(name="seqVehiculo",sequenceName="vehiculo_seq")
	@GeneratedValue(strategy=SEQUENCE,generator="seqVehiculo")
	@Id
	public Long id;
	
	@NotNull
	@Column(length=10)
	public String placa;
	
	@Column(length=100)
	public String descripcion;	
	
	@ManyToOne
	public EstadoEquipoAccesorioVehiculo estado;
	
	public String activo;
	
	@Column(length=4)
	public String modelo;	
	
	
	
	
    public static Model.Finder<Long,Vehiculo> find = new Model.Finder<Long,Vehiculo>(Long.class, Vehiculo.class);		

}
