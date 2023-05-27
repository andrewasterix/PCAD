package server.worker;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import server.Server;
import server.eventi.Evento;

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
        try{
            if(!get()){
                server.getFrame().setInfoText("<font color=\"red\">Evento " +nomeEvento+ " non presente!</font>");
                return;
            }else{
                server.getFrame().setInfoText("<font color=\"green\">Evento " +nomeEvento+ " rimosso con successo!</font>");
                
                try {
                    server.updateEventiPanel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (InterruptedException | ExecutionException e) {
			server.getFrame().setInfoText("<font color=\"red\">Room Not Removed!</font>");
			e.printStackTrace();
        }
    }
}
