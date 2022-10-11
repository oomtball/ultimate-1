#include "Predicates.h"
#include "SumOfPredicatesABC.h"
#include <string.h>

int predA() {
  return 1;
}

int predB() {
  return 0;
}

int predC() {
  return 1;
}

int main(){
  Predicates test;
  test.predA = &predA;
  test.predB = &predB;
  test.predC = &predC;
  char const* result = sumOfPredicatesABC(test);
  if(result == "2.0"){
    return 0;
  } else{
    return 1;
  }
}
