package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Credito extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public TipoCredito tipoCredito;

    @Column(length = 500)
    public String personas;

    public static Model.Finder<Long,Credito> find = new Model.Finder<Long,Credito>(Long.class, Credito.class);


}
