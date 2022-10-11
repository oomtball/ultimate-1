/* This file was generated with the ADD-Lib
 * http://add-lib.scce.info/ */

public class HelloWorld {

    public String helloWorld(Predicates predicates) {
        return eval140456328107136(predicates);
    }

    private String eval140456328107136(Predicates predicates) {
        if (predicates.sayHello())
            return eval140456328107008(predicates);
        else
            return eval140456328107104(predicates);
    }

    private String eval140456328107104(Predicates predicates) {
        if (predicates.sayWorld())
            return eval140456328107040(predicates);
        else
            return eval140456328107072(predicates);
    }

    private String eval140456328107008(Predicates predicates) {
        if (predicates.sayWorld())
            return eval140456328106944(predicates);
        else
            return eval140456328106976(predicates);
    }

    private String eval140456328107072(Predicates predicates) {
        return "***** *****!";
    }

    private String eval140456328107040(Predicates predicates) {
        return "***** World!";
    }

    private String eval140456328106976(Predicates predicates) {
        return "Hello *****!";
    }

    private String eval140456328106944(Predicates predicates) {
        return "Hello World!";
    }
}
