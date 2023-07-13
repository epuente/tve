package models;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
public class PreAgendaSalida  extends models.utils.PlantillaModelo{

	@ManyToOne
	public PreAgenda preagenda;
	
	@NotNull
	public Date salida;
}
