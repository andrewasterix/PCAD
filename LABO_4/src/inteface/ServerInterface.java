package inteface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import eventi.Evento;
import server.logic.ServerGUI;

import java.io.Serializable;

public interface ServerInterface extends Remote, Serializable {

    public void addEvento(String nomeEvento, int postiLiberi) throws RemoteException;

    public boolean removeEvento(String nomeEvento) throws RemoteException;

    public boolean prenotaEvento(String nome, int postiRichiesti) throws RemoteException;
    
    public void updateEventiPanel() throws RemoteException;

    public void registerCallBack(ClientInterface callbackClient) throws RemoteException;

    public void unregisterCallBack(ClientInterface callbackClient) throws RemoteException;

    public void setPrenotazioneNotify(String text) throws RemoteException;

    /* Getters and Setters */
    public ServerGUI getFrame() throws RemoteException;

    public void setFrame(ServerGUI serverGUI) throws RemoteException;

    public ConcurrentHashMap<String, Evento> getEventi() throws RemoteException;

    public void setEventi(ConcurrentHashMap<String, Evento> eventi) throws RemoteException;

    public AtomicBoolean getStatus() throws RemoteException;

    public void setStatus(AtomicBoolean status) throws RemoteException;

    public String getInfo() throws RemoteException;

    public void setInfo(String info) throws RemoteException;

}
