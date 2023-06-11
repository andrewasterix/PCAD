package server.logic;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import eventi.Evento;
import inteface.ClientInterface;
import inteface.ServerInterface;
import server.database.Database;

public class Server implements ServerInterface {

    /* Strutture dati */
    private ConcurrentHashMap<String, Evento> eventi;

    /* Client Connessi */
    private List<ClientInterface> clients;

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
            clients = new ArrayList<>();
        } catch (Exception e) {
            status = new AtomicBoolean(false);
            info = "Errore nell'avvio del Server!";
        }
    }

    /* Il Server vuole aggiugere un nuovo evento */
    @Override
    public void addEvento(String nomeEvento, int postiLiberi) throws RemoteException {
        Evento nuovoEvento = new Evento(nomeEvento, postiLiberi);

        synchronized (database) {
            if (database.getEventi().containsKey(nomeEvento))
                throw new IllegalArgumentException("Si vuole creare un evento con un nome già esistente!");

            try {
                database.AggiungiEvento(nuovoEvento);
                eventi.putIfAbsent(nomeEvento, nuovoEvento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addSeatsEvento(String nomeEvento, int postiLiberi) throws RemoteException {
        synchronized (database) {
            if (!database.getEventi().containsKey(nomeEvento))
                throw new IllegalArgumentException("Si vuole aggiungere posti ad un evento non esistente!");
            
            Evento evento = eventi.get(nomeEvento);

            try {
                database.AggiungiPosti(evento, postiLiberi);
                evento.aggiungiPosti(postiLiberi);
                eventi.replace(nomeEvento, evento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /* Il Server vuole rimuovere un evento */
    @Override
    public boolean removeEvento(String nomeEvento) {
        synchronized (database) {
            if (!database.getEventi().containsKey(nomeEvento))
                throw new IllegalArgumentException("Si vuole rimuovere un evento non esistente!");

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
        synchronized (database) {
            if (!database.getEventi().containsKey(nomeEvento))
                throw new IllegalArgumentException("Si vuole prenotare un evento non esistente!");

            Evento evento = eventi.get(nomeEvento);

            try {
                if (!evento.prenota(postiRichiesti))
                    return false;
                database.PrenotaPosti(evento, postiRichiesti);
                eventi.replace(nomeEvento, evento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /* Il Server aggiorna la visualizzazione della Tabella degli Eventi */
    public void updateEventiPanel() throws RemoteException {

        if (SwingUtilities.isEventDispatchThread()) {
            serverGUI.setEventiList(eventi);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    serverGUI.setEventiList(eventi);
                }
            });
        }
    }

    /* Il server Registra i client per il callBack */
    public synchronized void registerCallBack(ClientInterface callbackClient) throws RemoteException {
        if (!clients.contains(callbackClient)) {
            clients.add(callbackClient);
            System.out.println("Client registrato per avere CallBack!");
        }
    }

    /* Il Server rimuove la Registrazione per i CallBack */
    public synchronized void unregisterCallBack(ClientInterface callbackClient) throws RemoteException {
        if (clients.remove(callbackClient))
            System.out.println("Client rimosso dai CallBack!");
        else
            System.out.println("Errore nella rimozione del Client dalle CallBack!");
    }

    /* Il Server effettua le callBack ai Client in Lista */
    public synchronized void doCallbacks() throws RemoteException {
        System.out.println("Inizio delle Callback..");

        Iterator<ClientInterface> iter = clients.iterator();

        while (iter.hasNext()) {
            ClientInterface client = iter.next();
            client.updateEventiPanel();
        }

        System.out.println("Fine Callback");
    }

    public void setPrenotazioneNotify(String text) throws RemoteException {
        serverGUI.setInfoText(text);
        doCallbacks();
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
