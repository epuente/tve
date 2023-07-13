package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class AgendaPrevioAutorizaEquipo extends models.utils.PlantillaModelo {
    @ManyToOne
    public Agenda agenda;

    @OneToOne
    public PreAgendaEquipo preagendaequipo;



}
