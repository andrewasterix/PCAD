import data.Data;
import server.ServerMain;
import client.ClientMain;

public class App {
    public static void main(String[] args) throws Exception {
        
        Data.main(args);

        Runnable server = () -> {
            try {
                ServerMain.main(args);
            } catch (Exception e) {
                System.out.println("Errore durante l'avvio del server: " + e.getMessage());
            }
        };

        Runnable client = () -> {
            try {
                ClientMain.main(args);
            } catch (Exception e) {
                System.out.println("Errore durante l'avvio del client: " + e.getMessage());
            }
        };

        server.run();
        client.run();
    }
}
