package models;

import javax.persistence.Entity;

import play.db.ebean.Model;
@Entity
public class Formato  extends models.utils.PlantillaCatalogo{
    public static Model.Finder<Long,Formato> find = new Model.Finder<>(Long.class, Formato.class);
}
