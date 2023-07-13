package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class RegistroAcceso extends models.utils.PlantillaModelo{
    @ManyToOne
    public Personal usuario;
    @ManyToOne
    public Rol rol;
    public String ip;
    public String ruta;

}
