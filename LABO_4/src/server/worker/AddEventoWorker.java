package server.worker;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import server.logic.Server;

public class AddEventoWorker extends SwingWorker<Boolean, Void>{

    private String nomeEvento;
    private int postiLiberi;
    private Server server;

    /* Inizializza il Worker */
    public AddEventoWorker(String nomeEvento, int postiLiberi, Server server) {
        this.nomeEvento = nomeEvento;
        this.postiLiberi = postiLiberi;
        this.server = server;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        
        if (postiLiberi < 0) throw new IllegalArgumentException("Si vuole creare un evento con un numero di posti liberi negativo!");
        
        if (postiLiberi == 0) throw new IllegalArgumentException("Si vuole creare un evento con un numero di posti liberi pari a 0!");

        if (nomeEvento == null || nomeEvento.equals("")) throw new IllegalArgumentException("Si vuole creare un evento con un nome nullo o vuoto!");

        server.addEvento(nomeEvento, postiLiberi);
        return true;
    }

    @Override
    protected void done() {
        try {
            if (!get()) {
                server.getFrame().setInfoText("<font color=\"red\">Evento " +nomeEvento+ " già presente!</font>");
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        server.getFrame().setInfoText("<font color=\"green\">Evento " +nomeEvento+ " aggiunto con successo!</font>");
        try {
            server.doCallbacks();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        try {
            server.updateEventiPanel();
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
    
}
