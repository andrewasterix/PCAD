package eventi;

import java.util.concurrent.ConcurrentHashMap;

public class Eventi {

    private ConcurrentHashMap<String, Evento> eventi;

    /* Costruttore di default */
    public Eventi() {
        eventi = new ConcurrentHashMap<>();
    }

    /* Crea un evento */
    public void Crea(String nomeEvento, int postiLiberi) {
        Evento nuovoEvento = new Evento(nomeEvento, postiLiberi);
        eventi.putIfAbsent(nomeEvento, nuovoEvento);
        System.out.println("Evento " +nomeEvento+ " aggiunto con successo!");
    }

    /* Aggiunge posti liberi ad un evento */
    public synchronized void Aggiungi(String nomeEvento, int postiLiberi) {
        Evento evento = eventi.get(nomeEvento);
        /* Se l'evento esiste aggiungi posti liberi */
        if (evento != null) {
            evento.aggiungiPosti(postiLiberi);
            notifyAll();
        } else {
            throw new IllegalArgumentException("Evento " +nomeEvento+ " non trovato!");
        }
    }

    /* Prenota posti ad un evento */
    public synchronized void Prenota(String nomeEvento, int posti) {
        
        Evento evento = eventi.get(nomeEvento);      

        if (posti <= 0) throw new IllegalArgumentException("Si vuole prenotare per " +nomeEvento+ " un numero pari a 0 o minore!");

        if (evento != null) {
            /* Se non ci sono posti liberi aspetta */
            while(evento.getPostiLiberi() < posti){
                try {
                    wait(); // Aspetta che ci siano posti liberi
                    System.out.println("In attesa di prenotare per " + nomeEvento);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            evento.prenota(posti);
            notifyAll();
        } else {
            throw new IllegalArgumentException("Evento " +nomeEvento+ " non trovato!");
        }
    }

    /* Lista gli eventi */
    public void ListaEventi() {

        if (eventi.isEmpty()) {
            System.out.println("Non ci sono eventi!");
        } else {
            eventi.forEach((k, v) -> {
                System.out.println("Nome evento: " + k);
                System.out.println("Posti liberi: " + v.getPostiLiberi());
            });
        }
    }

    /* Chiude un evento */
    public synchronized void Chiudi(String nomeEvento) {
        Evento evento = eventi.get(nomeEvento);

        if (evento != null) {
            eventi.remove(nomeEvento);
            notifyAll(); // Sveglia tutti i thread in attesa
            System.out.println("Evento " +nomeEvento+ " chiuso con successo!");
        } else {
            throw new IllegalArgumentException("Evento " +nomeEvento+ " non trovato!");
        }
    }
}
