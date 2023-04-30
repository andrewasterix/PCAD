#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

pthread_mutex_t mutex;

void *foo(void *vargp)
{
    int myid;
    pthread_mutex_lock(&mutex);
    
    myid = *((int *)vargp);
    printf("Thread %d\n", myid);
    
    pthread_mutex_unlock(&mutex);
    return (0);
}

int main()
{
    pthread_t tid[5];
    void *ret;
    int *ptr;

    pthread_mutex_init(&mutex, NULL);

    ptr = malloc(sizeof(int));
    for (int i = 0; i < 5; i++)
    {
        *ptr = i;
        pthread_create(tid + i, 0, foo, ptr);
    }
    for (int i = 0; i < 5; i++)
    {
        pthread_join(tid[i], &ret);
    }
    return 0;
}