package models.videoteca;

import com.avaje.ebean.Ebean;
import models.Servicio;
import models.utils.PlantillaModelo;
import models.videoteca.VtkCatalogo;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.Valid;

@Entity
public class VtkEvento extends PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne(optional = false)
    @Column(nullable = false)
    public Servicio servicio;
}
