package server;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import server.database.Database;
import server.eventi.Evento;

public class Server {
    
    /* Strutture dati */
    private ConcurrentHashMap<String, Evento> eventi;
    
    /* Variabili */
    private Database database;
    private ServerGUI serverGUI;
    private AtomicBoolean status;
    private String info;

    /* Costruttore */
    public Server() {
        try {
            database = new Database();
            eventi = database.getEventi();
            status = new AtomicBoolean(true);
            info = "Server avviato con successo!";
        } catch (Exception e) {
            status = new AtomicBoolean(false);
            info = "Errore nell'avvio del Server!";
        }
    }

    /* Il Server vuole aggiugere un nuovo evento */
    public void addEvento(String nomeEvento, int postiLiberi) {
        Evento nuovoEvento = new Evento(nomeEvento, postiLiberi);
        
        synchronized(database){
            if (database.getEventi().containsKey(nomeEvento)) throw new IllegalArgumentException("Si vuole creare un evento con un nome già esistente!");
            
            try {
                database.AggiungiEvento(nuovoEvento);
                eventi.putIfAbsent(nomeEvento, nuovoEvento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Il Server vuole rimuovere un evento */
    public boolean removeEvento(String nomeEvento) {
        synchronized(database){
            if (!database.getEventi().containsKey(nomeEvento)) throw new IllegalArgumentException("Si vuole rimuovere un evento non esistente!");
            
            Evento evento = eventi.get(nomeEvento);

            try {
                database.RimuoviEvento(evento);
                eventi.remove(nomeEvento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /* JTABLE */
    public void updateEventiPanel() throws RemoteException{

        if(SwingUtilities.isEventDispatchThread()){
            serverGUI.setEventiList(eventi);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    serverGUI.setEventiList(eventi);
                }
            });
        }
    }

/*
    public void updateEventiPanel() throws RemoteException{
        StringBuilder sb = new StringBuilder("");

        for(Evento evento : eventi.values()){
            sb.append("Evento: " + evento.getNomeEvento() + " Posti Liberi: " + evento.getPostiLiberi() + "\n");
        }

        if(SwingUtilities.isEventDispatchThread()){
            serverGUI.setEventiList(sb.toString());
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    serverGUI.setEventiList(sb.toString());
                }
            });
        }
    }
*/  
    /* JTEXTPANEL */
/*
    public void updateEventiPanel() throws RemoteException{
        StringBuilder sb = new StringBuilder("<html>I");
        String color = "";

        for(Evento evento : eventi.values()){
            if (evento.getPostiLiberi() == 0) color = "<font color=\"red\">";
            else color = "<font color=\"green\">";
            sb.append("<font color=\""+color+"\">Evento: " + evento.getNomeEvento() + " Posti Liberi: " + evento.getPostiLiberi() + "</font><br>");
        }
        sb.append("</html>");

        if(SwingUtilities.isEventDispatchThread()){
            serverGUI.setEventiList(sb.toString());
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    serverGUI.setEventiList(sb.toString());
                }
            });
        }
    }
*/
    /* Getters and Setters */
    public ServerGUI getFrame() {
        return serverGUI;
    }

    public void setFrame(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    public ConcurrentHashMap<String, Evento> getEventi() {
        return eventi;
    }

    public void setEventi(ConcurrentHashMap<String, Evento> eventi) {
        this.eventi = eventi;
    }

    public AtomicBoolean getStatus() {
        return status;
    }

    public void setStatus(AtomicBoolean status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
