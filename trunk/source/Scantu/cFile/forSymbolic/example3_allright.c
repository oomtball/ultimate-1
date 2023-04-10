//@ ltl invariant positive: <>AP(x == 10);

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

typedef unsigned long int pthread_t;

int x = 0;
//      ddfs          fixpoint      
// 2    90.55 ms      204.77 ms
// 3    137.98 ms     246.69 ms
// 4    254.66 ms     617.52 ms
// 5    485.15 ms     1725.96 ms
// 6    917.51 ms     7757.02 ms
// 7    1210.52 ms    74497.10 ms
// 8    1570.30 ms    835836.08 ms
// 10   3797.24 ms        -

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