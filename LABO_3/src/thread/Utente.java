package thread;

import eventi.Eventi;

public class Utente extends Thread{
    
    private Eventi listaEventi;
    private String nomeEvento;
    private int postiPrenotare;

    /* Costruttore di default */
    public Utente(Eventi listaEventi, String nomeEvento, int postiPrenotare) {
        this.listaEventi = listaEventi;
        this.nomeEvento = nomeEvento;
        this.postiPrenotare = postiPrenotare;
    }

    /* Prenota posti ad un evento */
    @Override
    public void run() {
        try {
            listaEventi.Prenota(nomeEvento, postiPrenotare);
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }   
}
