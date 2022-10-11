#include "PredicatesPlusPlus.h"
#include "SumOfPredicatesABC.h"

bool predA() {
	return true;
}

bool predB() {
	return false;
}

bool predC() {
	return true;
}

int main() {
	Predicates test(&predA, &predB, &predC);
	SumOfPredicatesABC abc;
	if (abc.sumOfPredicatesABC(test) == "2.0")
		return 0;
  else
		return 1;
}
