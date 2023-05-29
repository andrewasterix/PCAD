package client.logic;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import eventi.Evento;
import inteface.ServerInterface;
import inteface.ClientInterface;

public class Client implements ClientInterface{
    
    private ConcurrentHashMap<String, Evento> eventi;

    private ClientGUI clientGUI;
    private ServerInterface server;

    private String info;

    public Client(ServerInterface server) throws RemoteException{
        
        this.server = server;
        eventi = new ConcurrentHashMap<String, Evento>();
        try{
            eventi = server.getEventi();
        }catch(RemoteException e){
            System.out.println("Errore durante la connessione al server: " + e.getMessage());
        }
        info = "Client connesso al server";
        UnicastRemoteObject.exportObject(this, 0);
    }


    public void updateEventiPanel() throws RemoteException{
        eventi = server.getEventi();
        
        if(SwingUtilities.isEventDispatchThread()){
            clientGUI.setEventiList(eventi);
        }else{
            SwingUtilities.invokeLater(() -> {
                clientGUI.setEventiList(eventi);
            });
        }
    }

    /* Gettes and Setters */
    public ClientGUI getFrame() {
        return clientGUI;
    }

    public void setFrame(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public ConcurrentHashMap<String, Evento> getEventi() {
        return eventi;
    }

    public void setEventi(ConcurrentHashMap<String, Evento> eventi) {
        this.eventi = eventi;
    }

    public ServerInterface getServer() {
        return server;
    }

    public void setServer(ServerInterface server) throws RemoteException {
        this.server = server;
        eventi = server.getEventi();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
