public class TestProgram {

    public static void main(String[] args) {
        Predicates p = new Predicates() {

            @Override
            public boolean predA() {
                return true;
            }

            @Override
            public boolean predB() {
                return false;
            }

            @Override
            public boolean predC() {
                return true;
            }
        };
        String result = SumOfPredicatesABC.sumOfPredicatesABC(p);
        String expectedResult = "2.0";
        if (!result.equals(expectedResult)) {
            System.err.println("test program failed: expected " + expectedResult + " but was " + result);
            int exitCodeFailure = 1;
            System.exit(exitCodeFailure);
        } else {
            int exitCodeSuccess = 0;
            System.exit(exitCodeSuccess);
        }
    }
}

