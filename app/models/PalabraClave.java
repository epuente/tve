package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
public class PalabraClave extends Model {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    public Date auditInsert =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));
    public Date auditUpdate =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));

    @ManyToOne(optional = false)
    public Personal catalogador;

    @NotNull
    @Column(length = 30)
    public String descripcion;
    @ManyToOne
    public VtkCatalogo catalogo;

}
