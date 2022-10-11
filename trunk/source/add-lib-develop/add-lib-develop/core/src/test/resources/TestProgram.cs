using System;

namespace TestCSharp {

    class MainClass {
        public static void Main(string[] args) {
            Predicates test = new PredicatesABC();
            if (SumOfPredicatesABC.sumOfPredicatesABC(test).Equals("2.0")) {
                System.Environment.Exit(0);
            } else {
                System.Environment.Exit(1);
            }
        }
    }

    class PredicatesABC : Predicates {
      public bool predA() {
        return true;
      }

      public bool predB() {
        return false;
      }

      public bool predC() {
        return true;
      }
    }

}
