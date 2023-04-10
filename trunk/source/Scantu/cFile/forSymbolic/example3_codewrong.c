//@ ltl invariant positive: <>AP(x == 10);

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

typedef unsigned long int pthread_t;

int x = 0;
//      ddfs          fixpoint      
// 2    55.99 ms      152.28 ms
// 3    68.29 ms      256.49 ms
// 4    80.87 ms      498.90 ms
// 5    88.75 ms      1280.42 ms
// 6    109.81 ms     5223.06 ms
// 7    113.97 ms     44260.40 ms
// 8    131.04 ms     543766.59 ms
// 10   153.38 ms     

void *thr1(void *_){
    
}

void *thr2(void *_){
    x = x + 1;
}
  
int main() {
    pthread_t t0, t1, t2, t3, t4, t5, t6, t7, t8, t9;
    pthread_create(&t0, 0, thr1, 0);
    pthread_create(&t1, 0, thr2, 0);
    pthread_create(&t2, 0, thr2, 0);
    pthread_create(&t3, 0, thr2, 0);
    pthread_create(&t4, 0, thr2, 0);
    pthread_create(&t5, 0, thr2, 0);
    pthread_create(&t6, 0, thr2, 0);
    pthread_create(&t7, 0, thr2, 0);
    pthread_create(&t8, 0, thr2, 0);
    pthread_create(&t9, 0, thr2, 0);

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