package models.utils;

import play.db.ebean.Model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@MappedSuperclass
public class PlantillaModelo extends Model {
    private static final long serialVersionUID = 1L;
    @Id
    public Long id;
    public Date auditInsert =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));
    public Date auditUpdate =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));

}
