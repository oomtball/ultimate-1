function assertEquals(a, b) {
	if (a != b) {
		throw "expected equals but " + a + " != " + b;
	}
}

const predicates = {
	predA: function() {
		return true;
	},
	predB: function() {
		return false;
	},
	predC: function() {
		return true
	}
}

imported = require("./SumOfPredicatesABC.js")

const expected = 2;
const actual = imported.SumOfPredicatesABC.sumOfPredicatesABC(predicates);
assertEquals(expected, actual);
