package model;

import communication.TCPManager;

import java.util.ArrayList;
import java.util.List;

public class Dealer implements TCPManager.ConnectionEvent {

    TCPManager manager;
    private int participantes;
    private boolean expiro;
    private Baraja baraja;
    private List<String> participantesList;
    private String mensaje;

    public Dealer(){
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
        manager.sendBroadcast("Carta Publica ::" + baraja.toString());
        manager.sendBroadcast("Carta Publica ::" + baraja.toString());
        manager.sendBroadcast("Carta Publica ::" + baraja.toString());




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

    }
}
