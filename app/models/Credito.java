package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Entity
public class Credito extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public TipoCredito tipoCredito;

    @Column(length = 75)
    public String personas;

    @ManyToOne(optional = false)
    public Personal catalogador;

    public static Model.Finder<Long,Credito> find = new Model.Finder<Long,Credito>(Long.class, Credito.class);



}
