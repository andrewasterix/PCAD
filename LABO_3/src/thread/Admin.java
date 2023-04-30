package thread;

import eventi.Eventi;

public class Admin extends Thread{

    private Eventi listaEventi;
    private String nomeEvento;
    private int postiAggiungere;

    /* Costruttore di default */
    public Admin(Eventi listaEventi, String nomeEvento, int postiAggiungere) {
        this.listaEventi = listaEventi;
        this.nomeEvento = nomeEvento;
        this.postiAggiungere = postiAggiungere;
    }

    /* Aggiunge posti ad un evento */
    @Override
    public void run() {
        try {
            listaEventi.Aggiungi(nomeEvento, postiAggiungere);
            Thread.sleep(1000);
            listaEventi.Chiudi(nomeEvento);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
