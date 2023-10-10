package classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CapitalizaCadena {

    String cadena="";

    List<String> excepciones = Arrays.asList("en", "la", "las", "de", "del", "y", "a", "o", "e", "u", "al", "el", "lo", "los", "para", "por",
            "su", "sus", "es", "son", "un", "una", "uno",
            "desde", "hasta", "entre"
            );
    // Palabras que deber ir en mayúsculas
    List<String> forzadas = Arrays.asList("i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix",
            "x", "xi", "xii", "xiii", "xiv", "xv", "xvi", "xvii", "xviii", "xix",
            "xx", "xxi", "xxii", "xxiii", "xxiv", "xxv", "xxvi", "xxvii", "xxviii", "xxix",
            "xxx", "xxxi", "xxxii", "xxxiii", "xxxiv", "xxxv", "xxxvi", "xxxvii", "xxxviii", "xxxix",
            "ipn", "unam", "uam", "d.f.", "cdmx", "tv", "t.v.");
    public CapitalizaCadena(){
        super();
    }


    public CapitalizaCadena(String txtInicial) {
        String[] arreglo = txtInicial.split(" ");
        for (int x=0; x< arreglo.length; x++){
            String palabra = arreglo[x];
        //for (String palabra : arreglo) {
            palabra = palabra.toLowerCase();
            String finalPalabra = palabra;
            if (palabra.length() > 0  ) {
                if (forzadas.stream().anyMatch(str -> str.equals(finalPalabra))) {
                    System.out.println("         FORZADO");
                    cadena = cadena + palabra.toUpperCase() + " ";
                } else
                if (excepciones.stream().noneMatch(str -> str.equals(finalPalabra))  || x==0  ) {
                    //System.out.println("    palabra(" + palabra.length() + "):" + palabra+"   "+excepciones.stream().filter(f->f== finalPalabra).count());
                    String letra = palabra.substring(0, 1).toUpperCase();
                    String resto = palabra.substring(1);
                    cadena = cadena + letra + resto + " ";
                } else {
                   // System.out.println("                 segundo else ("+palabra+")");
                        cadena = cadena + palabra + " ";
                    }
            }
        }
        System.out.println("capitalizado:" + cadena);
    }

    public String modificado(){
        return cadena;
    }


}
