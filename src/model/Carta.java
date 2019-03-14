package model;

import com.google.gson.Gson;

public class Carta {

    private boolean oculta;
    private String nombre;

    public Carta(){

    }

    public Carta(boolean oculta, String nombre){
        this.oculta = oculta;
        this.nombre = nombre;
    }

    public boolean isOculta() {
        return oculta;
    }

    public void setOculta(boolean oculta) {
        this.oculta = oculta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
