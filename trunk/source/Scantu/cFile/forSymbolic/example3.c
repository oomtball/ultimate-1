//@ ltl invariant positive: [](AP(x == 0) ==> <>AP(x == 2));

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

typedef unsigned long int pthread_t;

int x = 0;
//      ddfs          fixpoint      reachable     accepting reachable
// 2    112.12 ms     280.51 ms     61            29
// 3    317.69 ms     418.02 ms     253           125
// 4    779.37 ms     1255.42 ms    1021          509
// 5    1848.92 ms    4464.70 ms    4093          2045
// 6    3539.98 ms    25914.37 ms   16381         8189
// 7    7241.55 ms    156888.18 ms  65533         32765

void *thr(void *_){
    x = x + 1;
}
  
int main() {
    pthread_t t0, t1;
    pthread_create(&t0, 0, thr, 0);
    pthread_create(&t1, 0, thr, 0);
    // pthread_create(&t2, 0, thr, 0);
    // pthread_create(&t3, 0, thr, 0);
    // pthread_create(&t4, 0, thr, 0);
    // pthread_create(&t5, 0, thr, 0);
    // pthread_create(&t6, 0, thr, 0);
    // pthread_create(&t7, 0, thr, 0);
    // pthread_create(&t8, 0, thr, 0);
    // pthread_create(&t9, 0, thr, 0);

    pthread_join(t0, 0);
    pthread_join(t1, 0);
    // pthread_join(t2, 0);
    // pthread_join(t3, 0);
    // pthread_join(t4, 0);
    // pthread_join(t5, 0);
    // pthread_join(t6, 0);
    // pthread_join(t7, 0);
    // pthread_join(t8, 0);
    // pthread_join(t9, 0);
	
    return 0;
}