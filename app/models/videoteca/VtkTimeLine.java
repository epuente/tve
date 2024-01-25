package models.videoteca;

import classes.Duracion;
import models.Personal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class VtkTimeLine extends models.utils.PlantillaModelo{
    @ManyToOne
    public VtkCatalogo catalogo;

    @ManyToOne
    public VideoPersonaje personaje;

    @Column(length = 50)
    public String gradoacademico;

    @Column(length = 50)
    public String cargo;


    public Long desde;
    public Long hasta;

    @Column(length = 200)
    public String tema;

    @ManyToOne (optional = false)
    public Personal catalogador;


    // Convierte  desde (segundos de tipo Long) a cadena con formato hh:mm:ss

    public String convierteACadena(Long segundos){
        Duracion d = new Duracion( segundos);
        return d.cadena();
    }

    public String duracion(){
        Long d = this.hasta - this.desde;
        return convierteACadena(d);
    }

}
