/* 
    Nome Gruppo : Popolo delle Meraviglie : Team 25
    - Andrea Franceschetti - 4357070
    - William Chen - 4827847
    - Marco Chen - 4863267
    - Alessio De Vincenzi - 4878315
    
    Laboratorio 1 - Moltiplicazione Matrici *
    • A è una matrice MxN
    • B è una matrice NxP
    • C è una matrice PxM
*/
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/time.h>

/* Definizione delle dimensioni delle matrici */
static int M = 200, N = 600, P = 800, BLOCK = 20;
/* Definizione della barriera di attesa */
pthread_barrier_t barrier;

/* Definizione della Struttura Matrice */
struct Matrix {
    int row; /* Righe */
    int col; /* Colonne */
    float** data; /* Matrice */
};

/* Definizione della Struttura degli argomenti per il Prodotto Matriciale */
struct Arguments {
    struct Matrix *A;
    struct Matrix *B;
    struct Matrix *C;
    struct Matrix *partialRes; /* Risultato Parziale delle Matrici: AxB */
    struct Matrix *Result; /* Risultato Totale: Cx(AxB) */
    int threadNum; /* Numero identificativo del Thread che esegue il conto */
};

/* Prototipi delle funzioni */
struct Matrix* init(struct Matrix *matrix, int row, int col);
struct Arguments* initResult(struct Arguments* result);
void setRandomElement(struct Matrix* matrix);
void multiplyNoThreading(void* arg);
void *multiplyThreading(void* arg);
void printMatrix(struct Matrix* matrix);

/* Inizializzo la struttura matrice a valori nulli */
struct Matrix* init(struct Matrix *matrix, int row, int col){
    
    matrix = malloc(sizeof(struct Matrix));
    matrix->row = row;
    matrix->col = col;

    matrix->data = malloc(row * sizeof(float*));
    for(int i = 0; i < row; i++){
        matrix->data[i] = (float *)calloc(col, sizeof(float));
        for(int j = 0; j < col; j++)
            matrix->data[i][j] = 0.0; /* Inizializzazione a 0 della matrice */
    }
    return matrix;
}

/* Inizializzo la struttura matrice a valori nulli */
struct Arguments* initResult(struct Arguments* result){
    
    result = malloc(sizeof(struct Arguments));
    result->A = init(result->A, M, N); setRandomElement(result->A);
    result->B = init(result->B, N, P); setRandomElement(result->B);
    result->C = init(result->C, P, M); setRandomElement(result->C);
    
    /* Inizializzazione delle Matrici Risultato */
    result->partialRes = init(result->partialRes, M, P);
    result->Result = init(result->Result, P, P);

    result->threadNum = 0;
    return result;
}

/* Inserimento di valori randomici per la Matrice*/
void setRandomElement(struct Matrix* matrix){
    for(int i = 0 ; i < matrix->row ; i++){
        for (int j = 0 ; j < matrix->col ; j++){
            matrix->data[i][j] = ((float)rand() / (RAND_MAX)) * (float)(10.0);
        }
    }
}

/* Moltiplicazione Sequenziale di Matrici */
void multiplyNoThreading(void* arg){

    struct Arguments* result = (struct Arguments*) arg;
    
    /* A x B */
	for (int i = 0; i < result->A->row; i++){		
		for (int j = 0; j < result->B->col; j++){
			for (int k = 0; k < result->A->col; k++)
				result->partialRes->data[i][j] += result->A->data[i][k] * result->B->data[k][j];
		}
	}

    /* C x PartialRes */
    for (int i = 0; i < result->C->row; i++){
        for(int j = 0; j < result->partialRes->col; j++){
            for(int k = 0; k < result->C->col; k++){
                result->Result->data[i][j] += result->C->data[i][k] * result->partialRes->data[k][j];
            }
        }
    }
}

/* Moltiplicazione Concorrenziale di Matrici*/
void *multiplyThreading(void* arg){

    struct Arguments* result = (struct Arguments*) arg;
    int number_thd = result->threadNum;
    /* Calcolo la riga da cui partire per eseguire la moltiplicazione del blocco di A e B*/
    int row_blockA = number_thd * (result->A->row / BLOCK);

    /* A x B */
    /* Divido le righe di A per il numero di Blochhi assegnato */
	for (row_blockA; row_blockA < result->A->row / BLOCK; row_blockA++){		
		for (int j = 0; j < result->B->col; j++){
			for (int k = 0; k < result->A->col; k++)
				result->partialRes->data[row_blockA][j] += result->A->data[row_blockA][k] * result->B->data[k][j];
		}
	}
    
    /* Alzo una barriera di attesa, 
        affinchè, prima che venga eseguita la sedonda moltiplicazione, venga conclusa la prima */
    pthread_barrier_wait(&barrier);

    /* Calcolo la riga da cui partire per eseguire la moltiplicazione del blocco di A e B*/
    int row_blockC = number_thd * (result->C->row / BLOCK);
    
    /* C x PartialRes */
    for (row_blockC; row_blockC < result->C->row; row_blockC++){
        for(int j = 0; j < result->partialRes->col; j++){
            for(int k = 0; k < result->C->col; k++){
                result->Result->data[row_blockC][j] += result->C->data[row_blockC][k] * result->partialRes->data[k][j];
            }
        }
    }
    pthread_barrier_wait(&barrier);
}

/* Stampa Matrice */
void printMatrix(struct Matrix* matrix){
	for (int i = 0; i < matrix->row; i++){
		for (int j = 0; j < matrix->col; j++)
			printf("%f ", matrix->data[i][j]);
        printf("\n");
	}
}

int main(int argc, char const *argv[])
{
    srand(time(NULL));
    struct timeval start, end, delta;

    /* Inizializzazione degli Argomenti del Calc */
    struct Arguments* ResultSeq = initResult(ResultSeq);
    struct Arguments* ResultThd = initResult(ResultThd);

    /* ESECUZIONE: SENZA THREAD */
    #pragma region NO_THREADING
    printf("Running con: M = %d, N = %d, P = %d, NO Threads.\n", M, N, P);
    
    gettimeofday(&start, NULL);
    multiplyNoThreading(ResultSeq); /* Esecuzione sequenziale */
    gettimeofday(&end, NULL);

    timersub(&end, &start, &delta);
    fprintf(stderr, "Tempo di esecuzione NO THREAD: %ld.%06ld sec\n", delta.tv_sec, delta.tv_usec);

    #pragma endregion

    /* ESECUZIONE: CON THREAD */
    #pragma region THREADING
    printf("Runnging con: M = %d, N = %d, P = %d, %d Threads.\n", M, N, P, BLOCK);
    
    gettimeofday(&start, NULL);
    
    /* "Divido" le matrici per il numero di Thread,
        mi salvo il Numero del thread affichè non venga modificato successivamente */
    struct Arguments args[BLOCK];
    for(int i = 0; i < BLOCK; i++){
        args[i] = *ResultThd;
        args[i].threadNum = i;
    }

    /* Inizializzazione della Barriera */
    pthread_barrier_init(&barrier, NULL, BLOCK);
    /* Creazione Array dei Thread */
    pthread_t *threads = (pthread_t*)calloc(BLOCK, sizeof(pthread_t*));
    int count_th = 0;

    /* Avvio dei Thread e connessione alla funzione Multiply */
    for(count_th; count_th < BLOCK; count_th++){
        if(pthread_create(&threads[count_th], NULL, &multiplyThreading, &args[count_th]) != 0){
            fprintf(stderr, "ERROR: main-> Creazione Thread FALLITA\n"); break;
        }
    }
    /* Join dei Thread affinchè si attendano prima di concludere */
    for (int i = 0; i < count_th; i++){
        if(pthread_join(threads[i], NULL) != 0)
            fprintf(stderr, "ERRORE: main-> Join dei Thread fallita\n");
    }
    /* Distruggo la Barriera */
    pthread_barrier_destroy(&barrier);

    gettimeofday(&end, NULL);

    timersub(&end, &start, &delta);
    fprintf(stderr, "Tempo di esecuzione CON THREAD: %ld.%06ld sec\n", delta.tv_sec, delta.tv_usec);
    #pragma endregion
    
    /* Libero la memoria */
    free(ResultSeq);
    free(ResultThd);
    return 0;
}