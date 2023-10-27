package models;

import javax.persistence.Entity;

@Entity
public class TipoGrabacion extends models.utils.PlantillaCatalogo{

    public static Finder<Long, TipoGrabacion> find = new Finder<Long, TipoGrabacion>(Long.class, TipoGrabacion.class);

}
