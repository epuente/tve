package models;

import play.Logger;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
public class Serie extends Model{
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;

    public Date auditInsert =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));
    public Date auditUpdate =  Date.from( Instant.now().minus(1, ChronoUnit.HOURS));

    @NotNull
    @Column(length=7000)
    public String descripcion;

    @ManyToOne
    public Personal usuario;


    public static Model.Finder<Long,Serie> find = new Model.Finder<Long,Serie>(Long.class, Serie.class);


    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();

        List<Serie> aux = Serie.find.all();

        aux.sort(new Comparator<Serie>(){
            @Override
            public int compare(Serie serie, Serie t1) {
                return  serie.descripcion.compareToIgnoreCase(t1.descripcion)  ;
            }
        });

        for(Serie c: aux) {
            options.put(c.id.toString(), c.descripcion);
        }
        return options;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Serie serie = (Serie) o;
        return id.equals(serie.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


/*
    @PrePersist
    public void prePersist(){
        Logger.debug("Serie.find.nextId(): "+ Serie.find.nextId());
        Long nId = (long) Serie.find.nextId();
        Logger.debug("nId: "+nId);
        this.id = nId ;
    }
*/

}
