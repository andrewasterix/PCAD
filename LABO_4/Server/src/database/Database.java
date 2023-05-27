package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import eventi.Evento;

public class Database {
    
    /* Dati Database */
    private final String URL = "jdbc:sqlite:src\\database\\database.db";

    /* Strutture di ritorno dei Dati */
    private ConcurrentHashMap<String, Evento> eventi;

    /* Costruttore se il Database contiene elementi */
    public Database() throws SQLException{
        eventi = initEventi();
    }

    /* Costruttore se il Database è vuoto */
    public Database(List<Evento> eventi) throws SQLException{
        dropTables();
        createDatabase();

        for(Evento evento : eventi){
            AggiungiEvento(evento);
        }
    }

    /* Creazione del Database se non esiste */
    public void createDatabase() throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS eventi(nomeEvento TEXT PRIMARY KEY, postiLiberi INTEGER)");
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    /* Elimino Tabelle in caso esistano */
    private void dropTables() throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("DROP TABLE IF EXISTS eventi");
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    /* Inizializzazione dell'elenco eventi */
    private ConcurrentHashMap<String, Evento> initEventi() throws SQLException{
        
        ConcurrentHashMap<String, Evento> eventi = new ConcurrentHashMap<>();

        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM eventi");
        ResultSet rs = stmt.executeQuery();

        while(rs.next()){
            String nomeEvento = rs.getString("nomeEvento");
            int postiLiberi = rs.getInt("postiLiberi");
            Evento evento = new Evento(nomeEvento, postiLiberi);
            eventi.put(nomeEvento, evento);
        }
        
        rs.close();
        stmt.close();
        conn.close();

        return eventi;
    }

    public void AggiungiEvento(Evento evento) throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO eventi(nomeEvento, postiLiberi) VALUES(?, ?)");
    
        stmt.setString(1, evento.getNomeEvento());
        stmt.setInt(2, evento.getPostiLiberi());
        stmt.executeUpdate();
    
        stmt.close();
        conn.close();
    }

    public void AggiungiPosti(Evento evento, int posti) throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("UPDATE eventi SET postiLiberi = ? WHERE nomeEvento = ?");
    
        stmt.setInt(1, evento.getPostiLiberi() + posti);
        stmt.setString(2, evento.getNomeEvento());
        stmt.executeUpdate();
    
        stmt.close();
        conn.close();
    }

    public void PrenotaPosti(Evento evento, int posti) throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("UPDATE eventi SET postiLiberi = ? WHERE nomeEvento = ?");
    
        stmt.setInt(1, evento.getPostiLiberi() - posti);
        stmt.setString(2, evento.getNomeEvento());
        stmt.executeUpdate();
    
        stmt.close();
        conn.close();
    }

    public void RimuoviEvento(Evento evento) throws SQLException{
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM eventi WHERE nomeEvento = ?");
    
        stmt.setString(1, evento.getNomeEvento());
        stmt.executeUpdate();
    
        stmt.close();
        conn.close();
    }

    public ConcurrentHashMap<String, Evento> getEventi(){
        return eventi;
    }
}
