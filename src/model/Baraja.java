package model;

import java.util.List;

public class Baraja {

    private static int CANTIDAD_MAXIMA = 52;
    private List<Carta> baraja;

    public Carta getCartaRandom(){
        int indice = (int)(Math.random() * baraja.size());
        Carta c = baraja.get(indice);
        baraja.remove(indice);
        return c;
    }

}
