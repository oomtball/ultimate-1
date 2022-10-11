class Predicates{

	bool(*A)();
	bool(*B)();
	bool(*C)();

	public:

	Predicates(bool(*a)(), bool(*b)(), bool(*c)()) {
	    A = a;
	    B = b;
	    C = c;
    }

	bool predA() {
	    return A();
    }

	bool predB() {
	    return B();
    }

	bool predC() {
	    return C();
    }
};