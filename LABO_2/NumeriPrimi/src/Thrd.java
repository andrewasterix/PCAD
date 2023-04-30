/* La classe Thrd estende classe Thread da cui eredità metodi ed attributi */
public class Thrd extends Thread{
    /* Varibile per il DEBUG */
    final static boolean DEBUG = false;

    /* Definizione del Numero in analisi, e del numero primo di riferimento */
    private int number = 0;
    private int primeNumber = 0;

    /* Liste Linkate di appoggio */
    LL<Integer> input = new LL<Integer>();
    LL<Integer> output = new LL<Integer>();
    /* Thread successivo, per iniziare l'analisi */
    Thrd nextThread;
    
    /* Inizializzo il thread con la lista in analisi di riferimento */
    public Thrd(LL<Integer> input) {
        super();
        this.input = input;
    }

    /* Oveeride della funzione Thread.run(), affinchè avvenga il calcolo dei numeri primi */
    @Override
    public void run() {
        while(true){
            /* Sincronizzo i Thread */
            synchronized(input){
                /* Controllo degli input */
                if(input.isEmpty()){
                    if(input.isFinished()){
                        
                        /* Il Thread contiene l'ultimo numero primo della lista,
                            quindi non ha la possibilità di creare una nuova lista di output,
                            allora esco */
                        if(output == null){
                            break;
                        }
                        /* Si può creare la nuova lista di output, setto quella vecchia a conclusa */
                        if(output != null)
                            output.setFinished(true);
                        
                        if(DEBUG) System.out.println("Thread " +primeNumber+ " conluso!");
                        break;
                    }else{
                        try{
                            input.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                /* Se non è vuota, prendo il primo elemento */
                try {
                    number = input.take().intValue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /* Se è il numero primo, lo segno come primo */
                if(primeNumber == 0){
                    primeNumber = number;
                    System.out.println("Numero " +primeNumber+ " è primo!");
                }
                /* Se non è divisibile per il primo numero, lo aggiungo alla lista di output */
                if(number % primeNumber != 0){
                    if(DEBUG) System.out.println("Numero " +number+ " non è divisibile per " +primeNumber+ "!");
                    /* Se la lista di output è vuota, la inizializzo */
                    if(output == null) output = new LL<Integer>();
                    /* Se il thread successivo non è inizializzato,
                        lo inizializzo con la lista degli output, 
                        e inizio l'esecuzione */
                    if(nextThread == null){ nextThread = new Thrd(output); nextThread.start();}

                    /* Sincronizzo l'output */
                    synchronized(output){
                        /* Aggiungo il numero alla lista di output */
                        try {
                            output.put(number);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        output.notify();
                    }
                }
            }
        }
    }
}
