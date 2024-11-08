package models.videoteca;

import models.Servicio;
import models.utils.PlantillaModelo;

import javax.persistence.*;

@Entity
public class VtkEvento extends PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne(optional = false)
    @Column(nullable = false)
    public Servicio servicio;
}
