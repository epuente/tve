package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class FolioCancelacion extends models.utils.PlantillaModelo{
    @OneToOne(mappedBy = "foliocancelacion")
    public Folio folio;

    @ManyToOne
    public MotivoCancelacion motivo;

    @ManyToOne
    public Estado estadoanterior;

    public static Model.Finder<Long, FolioCancelacion> find = new Model.Finder<Long, FolioCancelacion>(Long.class, FolioCancelacion.class);

}
