package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Credito extends models.utils.PlantillaCatalogo{
    @ManyToOne
    VtkCatalogo catalogo;
}
