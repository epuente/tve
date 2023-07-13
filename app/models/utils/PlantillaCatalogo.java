package models.utils;

import models.Estado;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@MappedSuperclass
public class PlantillaCatalogo extends Model {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    public Date auditInsert =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));
    public Date auditUpdate =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));

    @NotNull
    @Column(length=100)
    public String descripcion;
/*
    @PrePersist
    public void prePersist(){
        Calendar c = Calendar.getInstance();
        c.setTime( new Date()  );
        c.add(Calendar.HOUR,-1);
        this.auditInsert= c.getTime();
        this.auditUpdate= c.getTime();
    }

    @PreUpdate
    public void preUpdate(){
        Calendar c = Calendar.getInstance();
        c.setTime( new Date()  );
        c.add(Calendar.HOUR,-1);
        this.auditUpdate= c.getTime();
    }

 */




}
