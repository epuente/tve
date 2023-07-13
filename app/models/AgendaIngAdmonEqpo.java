package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import play.db.ebean.Model;

@Entity
public class AgendaIngAdmonEqpo extends models.utils.PlantillaModelo {
    @ManyToOne
    @NotNull
    public Agenda agenda;

    @ManyToOne
    @NotNull
    public Personal ingeniero;
    public static Model.Finder<Long, AgendaIngAdmonEqpo> find = new Model.Finder<Long, AgendaIngAdmonEqpo>(Long.class, AgendaIngAdmonEqpo.class);
}
