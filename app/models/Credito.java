package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Credito extends models.utils.PlantillaCatalogo{
    @ManyToOne
    VtkCatalogo catalogo;

    @ManyToOne
    TipoCredito tipoCredito;

    @Column(length = 500)
    String personas;

}
