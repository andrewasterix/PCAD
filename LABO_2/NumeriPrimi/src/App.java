/* 
    Nome Gruppo : Popolo delle Meraviglie : Team 25
    - Andrea Franceschetti - 4357070
    - William Chen - 4827847
    - Marco Chen - 4863267
    - Alessio De Vincenzi - 4878315
    
    Laboratorio 2 - Crivello di Eratostene
    - L'algoritmo seleziona, scrivendoli in sequenza,i numeri primi compresi tra 1 ed N
    - Si tolgono prima di multipli di 2. 
    - Poi i numeri divisibili per il primo numero che rimane subito dopo il 2.
    - Poi per quello seguente ecc. 
    - Alla fine rimarranno soli i numeri primi.
*/

public class App {
    public static void main(String[] args) throws Exception {
        int maxThread = 100;
        LL<Integer> listLL = new LL<>();
        
        for(int i = 2; i < maxThread; i++)
            listLL.add(i);
        
        Thrd start = new Thrd(listLL);
        start.start();
        listLL.setFinished(true);
    }
}
