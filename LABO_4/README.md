# Laboratorio 4 - PCAD

## Popolo delle Meraviglie : Team 25

- Andrea Franceschetti - 4357070
- William Chen - 4827847
- Marco Chen - 4863267
- Alessio De Vincenzi - 4878315

## Specifiche del Progetto

### SERVER MULTITHREADED SOCKET

Per completare larchitettura del nostro sistema di gestione di prenotazione di eventi ci serve ancora il middleware di comunicazione con potenziali clienti collegati in rete.

Nella versione distribuita i client usano comunicazione TCP per inviare le richieste (prenotazione e lista eventi) al server.

Il server deve avere architettura multithreaded basata su ServerSocket su TCP (vedi lezioni su networking in Java) e deve poter gestire le richieste di prenotazione dei client confinando i socket generati da una richiesta in un task separato.

Per quanto riguarda la struttura dati per memorizzare gli eventi: raffinate la soluzione del foglio 2 usando non più metodi sincronizzati ma invece strutture dati concorrenti cercando di parallelizzare se possibile operazioni su eventi indipendenti tra loro.

Associate al programma client una GUI minimale per visualizzare la lista degli eventi e inviare le richieste di prenotazione come nell’esercitazione su Swing.

Preparate un breve video di presentazione della vostra soluzione (con una parte per ogni membro del gruppo) e inserite il link insieme al codice.

In particolare spiegate in maniera chiara come avete gestito l’accesso concorrente alla struttura dati per la gestione degli eventi.

## Descrizione Applicativa del Progetto

Il Server e il Client per la comunicazione TCP/IP utilizzano il metodo RMI, il quale permette la connessione Multi-Client.

I Dati degli eventi risiedono in una base dati SQLite, leggera e di facile implementazione, soppratutto (al fine di rispettare le specifiche del progetto) supporta il multiaccesso concorrenziale senza perdita di dati.

Il Server estrae i dati da DB e li carica in una ConcurrentHashMap, così da facilitare l'accesso ad essi. L'HashMap inoltre, è stata utilizzata come struttura dati da inviare ai client, affinchè essi abbiano gli stessi dati risiedenti sul server e possano effettutare le prenotazioni, senza accedere direttamente al DB.

L'inserimento di Nuovi Eventi, l'aggiornamento con nuovi posti o con prenotazioni degli Eventi e la rimozione di determinati Eventi viene eseguita prima sul DB e poi anche sulla HashMap di riferimento.

Quando però viene eseguita una delle operazioni sopra citate, tutti i client connessi devono essere aggiornati dei vari cambiamenti. Al fine di permettere ciò è stato impletementato un sistema di CallBacks. I Client all'avvio, dopo aver stabilito una connessione con il Server, si registrano in una lista client sul Server. Il Server quando esegue modifiche avvisa tutti i Client registrati.

Riferimento Bibliografico per le CallBacks: 'http://didawiki.cli.di.unipi.it/lib/exe/fetch.php/lpr-b/lpr-b-09/10-rmi-callback.pdf'


## Running

- Avviare il server da ServerMain.
- Avviare uno o più client da ClientMain.
