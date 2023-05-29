package server.logic;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import eventi.Evento;
import inteface.ServerInterface;
import server.database.Database;

public class Server implements ServerInterface{
    
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
    @Override
    public void addEvento(String nomeEvento, int postiLiberi) throws RemoteException{
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
    @Override
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

    /* Il Client prenota un evento */
    @Override
    public boolean prenotaEvento(String nomeEvento, int postiRichiesti) {
        synchronized(database){
            if (!database.getEventi().containsKey(nomeEvento)) throw new IllegalArgumentException("Si vuole prenotare un evento non esistente!");
            
            Evento evento = eventi.get(nomeEvento);

            try {
                if(!evento.prenota(postiRichiesti)) return false;
                database.PrenotaPosti(evento, postiRichiesti);
                eventi.replace(nomeEvento, evento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /* Il Server aggiorna la visualizzazione della Tabella degli Eventi */
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
