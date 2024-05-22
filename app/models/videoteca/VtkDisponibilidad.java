package models.videoteca;

import models.Disponibilidad;
import models.Servicio;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkDisponibilidad extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public Disponibilidad disponibilidad;
}
