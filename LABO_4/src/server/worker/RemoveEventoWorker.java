package server.worker;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eventi.Evento;
import server.logic.Server;

public class RemoveEventoWorker extends SwingWorker<Boolean, Void>{

    private String nomeEvento;
    private Evento evento;
    private Server server;

    public RemoveEventoWorker(String nomeEvento, Server server) {
        this.nomeEvento = nomeEvento;
        this.server = server;
        this.evento = server.getEventi().get(nomeEvento);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        try {
            evento = server.getEventi().get(nomeEvento);
            if(evento == null) return false;
            return server.removeEvento(nomeEvento);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }    
    }
    
    @Override
    protected void done(){
        try {
            if (!get()) {
                server.getFrame().setInfoText("<font color=\"red\">Evento " +nomeEvento+ " non presente!</font>");
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        server.getFrame().setInfoText("<font color=\"green\">Evento " +nomeEvento+ " rimosso con successo!</font>");
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
