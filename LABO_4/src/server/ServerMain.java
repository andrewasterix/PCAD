package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Semaphore;

import javax.swing.SwingUtilities;

import inteface.ServerInterface;
import server.logic.Server;
import server.logic.ServerGUI;

public class ServerMain {
    
    private static String URL = "jdbc:sqlite:src\\server\\database\\database.db";
    private static int registryPort = 8000;

    static Server server;

    public static void main(String[] args) throws Exception {
        try {
            startServer();
        } catch (UnknownHostException | RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Avvio del Server */
    public static void startServer() throws UnknownHostException, RemoteException, InterruptedException{

        /* Propietà della connessione al sistema */
        System.setProperty("java.rmi.server.hostname", "localhost");
        System.setProperty("java.security.policy", "file:./security.policy");
        System.setProperty("java.rmi.server.codebase", "file:${workspace_loc}/server/");
    
        String IP_ADDRESS = (InetAddress.getLocalHost()).toString();
        String info = "<html><div style=\"color:#808080;\">&nbsp;&nbsp;Avvio Server: " +getDate()+ "</div>"
				+ "&nbsp;&nbsp;Server addr: " + IP_ADDRESS + "<br/>"
						+ "<table width=\"200px\" height=\"30px\" align=\"center\">"
						+ "<tr><td>Server port:</td><td> " + registryPort+"</td>";
        
        /* Registry del Sistema */
        Registry registry = null;
        try {
            registry = java.rmi.registry.LocateRegistry.createRegistry(registryPort);
            info += "<tr><td>Registry:</td><td> OK</td>";
        } catch (RemoteException e) {
            info += "<tr><td>Registry:</td><td> ERROR</td>";
            e.printStackTrace();
        }

        /* Creazione del Server */
        server = new Server();

        /* Esporto il registry sul server remoto */
        ServerInterface stub = null;
        try {
            stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
        }catch (RemoteException e) {
            e.printStackTrace();
        }

        registry.rebind("Server", stub);

        /* Inizializzazione del Semaforo */
        Semaphore sem = new Semaphore(1);
        sem.acquire();

        try {
            info += checkDatabase()+"</table><table border=\"1\" width=\"100%\">"
                + "<caption>Notifiche</caption>";
        } catch (SQLException e) {
            e.printStackTrace();
        }
		server.setInfo(info);

        /* Avvio della GUI del Server */
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try {
                    new ServerGUI(server, sem);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        /* Attende la chiusura del Server */
        sem.acquire();
        try{
            registry.unbind("Server");
        }catch(Exception e){
            e.printStackTrace();
        }

        UnicastRemoteObject.unexportObject(registry, true);

        sem.release();
        System.exit(0);
    }

    /* Controlla se il Database è attivo */
    private static String checkDatabase() throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        if (conn != null) {
            conn.close();
            return "<tr><td>Database:</td><td> OK</td>";
        } else {
            return "<tr><td>Database:</td><td> ERROR</td>";
        }
    }

    /* Ottiene la data attuale */
    private static String getDate() {
        GregorianCalendar date = new GregorianCalendar();
		return new String(date.get(Calendar.DATE)+ "/" +date.get(Calendar.MONTH)+ "/" +date.get(Calendar.YEAR)+ " - " 
            +date.get(Calendar.HOUR_OF_DAY)+ ":" +date.get(Calendar.MINUTE));
    }

}
