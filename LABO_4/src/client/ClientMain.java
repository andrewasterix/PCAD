package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.SwingUtilities;

import client.logic.Client;
import client.logic.ClientGUI;
import inteface.ServerInterface;

public class ClientMain {

    private static ServerInterface server;
    private static int registryPort = 8000;

    public static void main(String[] args) {
        try {
            startClient();
        } catch (RemoteException | NotBoundException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void startClient() throws RemoteException, NotBoundException, UnknownHostException {
        System.setProperty("java.security.policy", "file:./security.policy");
        System.setProperty("java.rmi.server.codebase", "file:${workspace_loc}/Client/");

        String IP_ADDRESS = (InetAddress.getLocalHost()).toString();
        String info = "<html><div style=\"color:#808080;\">&nbsp;&nbsp;Avvio Client: " + getDate() + "</div>"
            + "&nbsp;&nbsp;Server addr: " + IP_ADDRESS + "<br/>"
            + "<table width=\"200px\" height=\"30px\" align=\"center\">"
            + "<tr><td>Server port:</td><td> " + registryPort + "</td>";

        Registry registry = LocateRegistry.getRegistry("localhost", registryPort);

        try {
            server = (ServerInterface) registry.lookup("Server");
            info += "<tr><td>Registry:</td><td> OK</td>";
        } catch (AccessException e) {
            info += "<tr><td>Registry:</td><td> ERROR</td>";
            System.out.println("Accesso negato al server: " + e.getMessage());
        }

        Client client = new Client(server);

        client.setInfo(info + "</table><table border=\"1\" width=\"100%\">"
                + "<caption>Notifiche</caption>");

        Runnable task = () -> {
            try {
                new ClientGUI(server, client);
            } catch (Exception e) {
                System.out.println("Errore durante l'avvio del client: " + e.getMessage());
            }
        };
        SwingUtilities.invokeLater(task);
    }

    /* Ottiene la data attuale */
    private static String getDate() {
        GregorianCalendar date = new GregorianCalendar();
        return new String(
                date.get(Calendar.DATE) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR) + " - "
                        + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE));
    }
}
