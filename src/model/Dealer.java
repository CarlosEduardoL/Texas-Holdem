package model;

import communication.TCPManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Dealer implements TCPManager.ConnectionEvent {

    TCPManager manager;
    private int participantes;
    private boolean expiro;
    private Baraja baraja;
    private List<String> participantesList;
    private String mensaje;
    private int turno;
    private int cartasPublicas;

    public Dealer(){
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        initGame();
    }

    private void initGame() {
        participantes = 0;
        boolean inHilo = false;
        participantesList = new ArrayList<>();
        expiro = false;
        baraja = new Baraja();
        manager = TCPManager.getInstance();
        manager.addConnectionEvent(this);
        new Thread(
                () -> manager.waitForConnection(5000)
        ).start();

        while (participantes < 3){
            if (participantes == 2 && expiro){
                break;
            }else if (participantes == 2 && !inHilo){
                inHilo = true;
                new Thread(
                        () -> {
                            try {
                                Thread.sleep(60000);
                                expiro = true;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                ).start();
            }
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < participantesList.size(); i++) {
            manager.sendBroadcast("Player::"+participantesList.get(i));
        }

        manager.sendBroadcast("Carta Publica::" + baraja.getCartaRandom().toString());
        manager.sendBroadcast("Carta Publica::" + baraja.getCartaRandom().toString());
        manager.sendBroadcast("Carta Publica::" + baraja.getCartaRandom().toString());
        cartasPublicas = 3;

        for (int i = 0; i < participantesList.size(); i++) {
            manager.sendDirectMessage("Server",participantesList.get(i),"Carta Privada::"+baraja.getCartaRandom().toString());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            manager.sendDirectMessage("Server",participantesList.get(i),"Carta Privada::"+baraja.getCartaRandom().toString());
        }

        orden();
    }

    private void orden() {
        if (turno<participantesList.size()){
            manager.sendDirectMessage("Server",participantesList.get(turno),"Permiso::Permiso");
            turno++;
        }else if (cartasPublicas <= 5){
            manager.sendBroadcast("Carta Publica::" + baraja.getCartaRandom().toString());
            turno = 0;
            cartasPublicas++;
            orden();
        }
    }

    @Override
    public void onConnection(String uuid) {
        mensaje = "";
        if (participantes < 3 && !expiro){
            participantes++;
            participantesList.add(uuid);
            mensaje = "Disponible";
        }else{
            mensaje = "Ocupado";
        }
        new Thread(
                () -> {
                    try {
                        Thread.sleep(50);
                        manager.sendDirectMessage("Server", uuid, Dealer.this.mensaje);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        ).start();

    }

    @Override
    public void onMessage(String uuid, String msj) {
        if (msj.equals("Sali")){
            manager.sendBroadcast("Salio::"+uuid);
            int t = turno - 1;
            orden();
            participantesList.remove(t);
        }else if (msj.equals("Sigo")){
            orden();
        }
    }
}
