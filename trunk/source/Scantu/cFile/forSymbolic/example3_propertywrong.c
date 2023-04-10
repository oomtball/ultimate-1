//@ ltl invariant positive: <>AP(x == 11);

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

typedef unsigned long int pthread_t;

int x = 0;
//      ddfs          fixpoint      
// 2    54.56 ms      169.76 ms
// 3    54.72 ms      272.67 ms
// 4    89.88 ms      584.61 ms
// 5    99.45 ms      2011.77 ms
// 6    109.36 ms     8023.92 ms
// 7    119.94 ms     84858.16 ms
// 8    151.74 ms     860803.38 ms
// 10   217.14 ms         -

void *thr(void *_){
    x = x + 1;
}
  
int main() {
    pthread_t t0, t1, t2, t3, t4, t5, t6, t7, t8, t9;
    pthread_create(&t0, 0, thr, 0);
    pthread_create(&t1, 0, thr, 0);
    pthread_create(&t2, 0, thr, 0);
    pthread_create(&t3, 0, thr, 0);
    pthread_create(&t4, 0, thr, 0);
    pthread_create(&t5, 0, thr, 0);
    pthread_create(&t6, 0, thr, 0);
    pthread_create(&t7, 0, thr, 0);
    pthread_create(&t8, 0, thr, 0);
    pthread_create(&t9, 0, thr, 0);

    pthread_join(t0, 0);
    pthread_join(t1, 0);
    pthread_join(t2, 0);
    pthread_join(t3, 0);
    pthread_join(t4, 0);
    pthread_join(t5, 0);
    pthread_join(t6, 0);
    pthread_join(t7, 0);
    pthread_join(t8, 0);
    pthread_join(t9, 0);
	
    return 0;
}