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

    public Dealer(){
        participantes = 0;
        participantesList = new ArrayList<>();
        expiro = false;
        manager = TCPManager.getInstance();
        manager.addConnectionEvent(this);
        new Thread(
                () -> manager.waitForConnection(5000)
        ).start();
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

        while (participantes < 3){
            if (participantes == 2 && expiro){
                break;
            }
        }

        baraja = new Baraja();


    }

    @Override
    public void onConnection(String uuid) {
        participantes++;
        participantesList.add(uuid);
    }

    @Override
    public void onMessage(String uuid, String msj) {

    }
}
