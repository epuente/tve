package models.videoteca;

import models.Disponibilidad;

import javax.persistence.*;

@Entity
public class VtkDisponibilidad extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public Disponibilidad disponibilidad;
}
