#include <pthread.h>
#include <time.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

pthread_barrier_t barrier;

const int M = 17;
const int N = 10;
const int P = 15;

struct matrix {
	int rig;
	int col;
	double *next;
};

struct parameters {
	struct matrix A;
	struct matrix B;
	struct matrix C;
	struct matrix R;			//A*B
	struct matrix result;		//R*C
	int n_thread;	
	int n_block;
};

void init_matrix(struct matrix *m, int rig, int col){
	m->next = (double*)malloc(rig*col*sizeof(double));

	if(m->next == NULL){
		printf("Error: memory not allocated.");
		exit(-1);
	}

	m->rig = rig;
	m->col = col;
}

void random_matrix(struct matrix *m){
	for(int i = 0; i < m->rig; i++){
		for(int j = 0; j < m->col; j++){
			m->next[i*m->col+j] = rand() % 10;
		}
	}
}

void print_matrix(struct matrix *m){
	for(int i = 0; i < m->rig; i++){
		for(int j = 0; j < m->col; j++){
			printf("%f ", m->next[i*m->col+j]);
		}
		printf("\n");
	}
	printf("\n");
}

void* prod_matrix_thread(void *arg){
	struct parameters *p = (struct parameters*)arg;
	double value;
	int i,j,k;

	printf("PIPPO");
	for(i = p->n_thread * p->n_block; i < p->n_thread * p->n_block + p->n_block && p->result.rig; i++){
		for(j = 0; j < p->B.col; j++){
			value = 0;
			for(k = 0; k < p->A.col; k++){
				value += p->A.next[j*p->A.col+k] * p->B.next[k*p->B.col+i];
			}
			p->R.next[j*p->R.col+i] = value;
		}
	}
	
	pthread_barrier_wait(&barrier);

	printf("PIPPO");
	for(i=p->n_thread * p->n_block; i < p->n_thread * p->n_block + p->n_block && p->result.rig; i++){
		for(j = 0; j < p->C.col; j++){
			value = 0;
			for(k = 0; k < p->R.col; k++){
				value += p->R.next[j*p->R.col+k] * p->C.next[k*p->C.col+i];
			}
			p->result.next[j*p->result.col+i] = value;
		}
	}

	pthread_barrier_wait(&barrier);

}

void* prod_matrix_no_thread(void *arg){
	struct parameters *p = (struct parameters*)arg;
	double value;
	int i,j,k;

	for(i = 0; i < p->A.rig; i++){
		for(j = 0; j < p->B.col; j++){
			value = 0;
			for(k = 0; k < p->A.col; k++){
				value += p->A.next[i*p->A.col+k] * p->B.next[k*p->B.col+j];
			}
			p->R.next[i*p->R.col+j] = value;
		}
	}
	
	for(i = 0; i < p->R.rig; i++){
		for(j = 0; j < p->C.col; j++){
			value = 0;
			for(k = 0; k < p->R.col; k++){
				value += p->R.next[i*p->R.col+k] * p->C.next[k*p->C.col+j];
			}
			p->result.next[i*p->result.col+j] = value;
		}
	}
}

int main(){
	printf("PIPPO");
	struct parameters *p = (struct parameters*)malloc(sizeof(struct parameters));
	
	init_matrix(&p->A, M, N);
	init_matrix(&p->B, N, P);
	init_matrix(&p->C, P, M);
	init_matrix(&p->R, M, P);
	init_matrix(&p->result, M, P);
	
	random_matrix(&p->A);
	random_matrix(&p->B);
	random_matrix(&p->C);
	
	p->n_thread = 4;
	p->n_block = 4;
	
	// no thread

	

	printf("PIPPO");
	pthread_barrier_init(&barrier, NULL, p->n_block);
	pthread_t threads[p->n_thread];
	for(int i = 0; i < p->n_thread; i++){
		pthread_create(&threads[i], NULL, &prod_matrix_thread, (void *)p);
	}
	
	printf("PIPPO");
	for(int i = 0; i < p->n_thread; i++){
		pthread_join(threads[i], NULL);
	}
	
	pthread_barrier_destroy(&barrier);
	
	printf("PIPPO");
	print_matrix(&p->A);
	print_matrix(&p->B);
	print_matrix(&p->C);
	print_matrix(&p->R);
	print_matrix(&p->result);
}


/*	for(int i = 0; i < p->A.rig; i++){
		for(int j = 0; j < p->B.col; j++){
			for(int k = 0; k < p->A.col; k++){
				p->R.next[i*p->R.col+j] += p->A.next[i*p->A.col+k] * p->B.next[k*p->B.col+j];
			}
		}
	}
	
	for(int i = 0; i < p->R.rig; i++){
		for(int j = 0; j < p->C.col; j++){
			for(int k = 0; k < p->R.col; k++){
				p->result.next[i*p->result.col+j] += p->R.next[i*p->R.col+k] * p->C.next[k*p->C.col+j];
			}
		}
	}*/
