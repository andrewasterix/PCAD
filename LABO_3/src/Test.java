import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import eventi.Eventi;
import thread.Admin;
import thread.Utente;

public class Test {

    private static Eventi eventi = new Eventi(); // Lista di eventi
    private static List<Utente> utenti = new ArrayList<>(); // Lista di utenti
    private static List<Admin> admin = new ArrayList<>(); // Lista di admin

    private static int size = 0; // Numero di eventi

    /* Metodo per leggere il file csv */
    public static void readLines(String filename) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] spList = line.split(",");
                eventi.Crea(spList[0], Integer.parseInt(spList[1]));
                utenti.add(new Utente(eventi, spList[0], Integer.parseInt(spList[2])));
                admin.add(new Admin(eventi, spList[0], Integer.parseInt(spList[3])));
                size++;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        
        readLines("src\\Test.csv");

        try (/* Creazione dei thread */
        ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 15, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(10))) {
            try {
                for(int i = 0; i < size; i++){
                    /* Esecuzione dei thread */
                    pool.execute(admin.get(i));
                    pool.execute(utenti.get(i));
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } finally {
                pool.shutdown();
            }
            /* Aspetta che tutti i thread terminino */
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
        System.out.println("Tutti i thread sono terminati!");
    }
}
