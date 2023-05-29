package client.worker;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import client.logic.Client;
import inteface.ServerInterface;

public class PrenotaEventoWorker extends SwingWorker<Boolean, Void> {

    private String nomeEvento;
    private int postiRichiesti;
    private ServerInterface server;
    private Client client;

    public PrenotaEventoWorker(String nomeEvento, int postiRichiesti, ServerInterface server, Client client) {
        this.nomeEvento = nomeEvento;
        this.postiRichiesti = postiRichiesti;
        this.server = server;
        this.client = client;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        if (postiRichiesti < 0) throw new IllegalArgumentException("Si vuole creare un evento con un numero di posti liberi negativo!");
        
        if (postiRichiesti == 0) throw new IllegalArgumentException("Si vuole creare un evento con un numero di posti liberi pari a 0!");

        if (nomeEvento == null || nomeEvento.equals("")) throw new IllegalArgumentException("Si vuole creare un evento con un nome nullo o vuoto!");

        return server.prenotaEvento(nomeEvento, postiRichiesti);
    }
    
    @Override
    protected void done() {
        try {
            if (!get()) {
                server.setInfo("<font color=\"red\">Tentativo di prenotazione ad evento " +nomeEvento+ " non presente!</font>");
                //server.getFrame().setInfoText("<font color=\"red\">Tentativo di prenotazione ad evento " +nomeEvento+ " non presente!</font>");
                client.getFrame().setInfoText("<font color=\"red\">Evento " +nomeEvento+ " non presente!</font>");
                return;
            }
        } catch (InterruptedException | ExecutionException | RemoteException e) {
            e.printStackTrace();
        }
        
        try {
            server.setInfo("<font color=\"green\">Evento " +nomeEvento+ " prenotato con successo!</font>");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        client.getFrame().setInfoText("<font color=\"green\">Evento " +nomeEvento+ " prenotato con successo!</font>");
        
        try {
            server.updateEventiPanel();
            client.updateEventiPanel();
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
}
