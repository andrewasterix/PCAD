package data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import eventi.Evento;
import server.database.Database;

public class Data {

    private static List<Evento> eventi = new ArrayList<>(); // Lista degli eventi

    /* Metodo per leggere il file csv */
    public static void readLines(String filename) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] spList = line.split(",");
                eventi.add(new Evento(spList[0], Integer.parseInt(spList[1])));
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws Exception {

        readLines("src\\data\\Data.csv");
        System.out.println("File letto con successo!");

        System.out.println("Creazione del Database...");
        new Database(eventi);
        System.out.println("Database creato con successo!");
    }
}
