from Predicates import Predicates
from SumOfPredicatesABC import SumOfPredicatesABC

test = Predicates(lambda: True, lambda: False, lambda: True)
expected = "2.0"
abc = SumOfPredicatesABC()
actual = abc.sumOfPredicatesABC(test)
if (expected == actual):
	exit(0)
else:
	exit(1)
