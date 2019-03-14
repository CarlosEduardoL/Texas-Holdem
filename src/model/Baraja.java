package model;

import java.util.ArrayList;
import java.util.List;

public class Baraja {

    private static int CANTIDAD_MAXIMA = 52;
    private List<Carta> baraja;
    private final char[] tipo = {'D','T','C','P'};

    public Baraja(){
        baraja = new ArrayList<>(CANTIDAD_MAXIMA);
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j <= 13; j++) {
                baraja.add(new Carta(false,getChar(j) + tipo[i]));
            }
        }
    }

    private String getChar(int num) {
        if (num <= 10){
            return String.valueOf(num);
        }else if (num == 11){
            return "J";
        }else if(num == 12){
            return  "Q";
        }else{
            return "K";
        }
    }

    public Carta getCartaRandom(){
        int indice = (int)(Math.random() * baraja.size());
        Carta c = baraja.get(indice);
        baraja.remove(indice);
        return c;
    }

}
