//@ ltl invariant positive: [](AP(x == 0) ==> (<>AP(x == 1)));

/* Testcase from Threader's distribution. For details see:
   http://www.model.in.tum.de/~popeea/research/threader
*/

// (!(<>([]AP(fairness == 0) || []AP(fairness == 1))) U AP(fairness == 2)) ==> [](AP(x == 0) ==> (<>AP(x == 1)));
// 35079.58 ms by fixpoint
// 1575.56 ms by double DFS

// [](AP(x == 0) ==> (<>AP(x == 1)));
// 7218.47 ms by fixpoint
// 130.08 ms by double DFS

#include <pthread.h>
typedef unsigned long int pthread_t;

int flag1 = 0, flag2 = 0; // boolean flags
int fairness = 0; //fairness label
int turn; // integer variable to hold the ID of the thread whose turn is it
int x = 0; // boolean variable to test mutual exclusion

void *thr1(void *_) {
    flag1 = 1;
    turn = 1;
    int f21 = flag2;
    int t1 = turn;
    while (f21==1 && t1==1) {
        fairness = 0;
        f21 = flag2;
        t1 = turn;
    };
   
    // begin: critical section
    // x = 0;
	int y1 = 0;
	y1 = x;
	y1++;
    x = y1;
    // end: critical section
    flag1 = 0;
    fairness = 2;
    return 0;
}

void *thr2(void *_) {
    flag2 = 1;
    turn = 0;
    int f12 = flag1;
    int t2 = turn;
    while (f12==1 && t2==0) {
        fairness = 1;
        f12 = flag1;
        t2 = turn;
    };
    // begin: critical section
    // x = 1;
    int y2 = 0;
	y2 = x;
	y2++;
    x = y2;
    // end: critical section
    flag2 = 0;
    fairness = 2;
    return 0;
}
  
int main() {
  pthread_t t1, t2;
  pthread_create(&t1, 0, thr1, 0);
  pthread_create(&t2, 0, thr2, 0);
  pthread_join(t1, 0);
  pthread_join(t2, 0);
  return 0;
}