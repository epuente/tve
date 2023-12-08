package classes;

public class Duracion {
    public long horas;
    public long minutos;
    public long segundos;

    public Duracion(){
        super();
    }

    // Constructor que recibe el total de segundos, convierte y asigna las propiedades hora, minuto y segundo como corresponde
    public Duracion(long totalSegundos){
        horas=totalSegundos/3600;
        minutos=(totalSegundos-(3600*horas))/60;
        segundos=totalSegundos-((horas*3600)+(minutos*60));
    }



    // Recibe una cadena tipo "48:15:10" y asigna los valores correspondientes a los atributos horas, minutos y segundos respectivamente
    public void convertir(String cadena) {
        long cantidad = cadena.chars().filter(c -> c == ':').count();
        if (cantidad==2){
            String[] arr = cadena.split(":");
            System.out.println("arr "+arr.length);
            horas = Long.parseLong( arr[0] );
            minutos = Long.parseLong(arr[1]);
            segundos = Long.parseLong(arr[2]);
        }
    }


    // Regresa el total de duracion en segundos (horas+minutos+segundos)
    public Long totalSegundos(){
        Long total = 0L;
        total += this.segundos;
        total += minutos * 60;
        total += horas * 60 * 60;
        return total;
    }


    // Regesa un string con la concatenacion de horas:minutos:segundos
    public String cadena(){
        String retorno="";
        if (horas<10 || horas==0)
            retorno="00";
        retorno+=horas+":";
        if (minutos<10 || minutos==0)
            retorno+="0";
        retorno+=minutos+":";
        if (segundos<10 || segundos==0)
            retorno+="0";
        retorno+=segundos;
        return retorno;
    }


}
