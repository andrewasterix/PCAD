package server.eventi;

import java.util.concurrent.atomic.AtomicInteger;

public class Evento {
    
    private String nomeEvento;
    private AtomicInteger postiLiberi;
    
    /* Costruttore di default */
    public Evento(String nomeEvento, int postiLiberi) {
        this.nomeEvento = nomeEvento;
        this.postiLiberi = new AtomicInteger(postiLiberi);
    }

    /* Stampa il nome dell'evento e il numero di posti liberi */
    public void stampa() {
        System.out.println("Nome evento: " + nomeEvento);
        System.out.println("Posti liberi: " + postiLiberi);
    }

    /* Aggiungi posti liberi per l'evento */
    public void aggiungiPosti(int posti) {
        /* Aggiungi posti liberi */
        postiLiberi.addAndGet(posti);
        System.out.println("Aggiunti " +posti+ " posti a "+ nomeEvento +"!");
    }

    /* Prenota un numero di posti per l'evento */
    public void prenota(int posti) {
        /* Se ci sono posti liberi prenota */
        if (postiLiberi.get() > 0 && postiLiberi.get() >= posti) {
            postiLiberi.addAndGet(-posti);
            System.out.println("Prenotazione " +posti+ " per " +nomeEvento+ " effettuata con successo!");
        } else {
            System.out.println("Prenotazione per " +nomeEvento+ " non effettuata: posti esauriti!");
        }
    }
    
    //#region Getters and Setters
    public String getNomeEvento() {
        return nomeEvento;
    }
    
    public int getPostiLiberi() {
        return postiLiberi.get();
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public void setPostiLiberi(int postiLiberi) {
        this.postiLiberi = new AtomicInteger(postiLiberi);
    }
    //#endregion
}
