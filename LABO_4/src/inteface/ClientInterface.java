package inteface;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote, Serializable{
    
    public void updateEventiPanel() throws RemoteException;
    
}
