/* This file was generated with the ADD-Lib
 * http://add-lib.scce.info/ */

namespace TestCSharp {

    public class HelloWorld {

        public string helloWorld(Predicates predicates) {
            string result;
            goto eval140041708454912;
            end:
            return result;


            eval140041708454912:
            if (predicates.sayHello())
                goto eval140041708454784;
            else
                goto eval140041708454880;


            eval140041708454880:
            if (predicates.sayWorld())
                goto eval140041708454816;
            else
                goto eval140041708454848;


            eval140041708454784:
            if (predicates.sayWorld())
                goto eval140041708454720;
            else
                goto eval140041708454752;


            eval140041708454848:
            result = "***** *****!";
            goto end;


            eval140041708454816:
            result = "***** World!";
            goto end;


            eval140041708454752:
            result = "Hello *****!";
            goto end;


            eval140041708454720:
            result = "Hello World!";
            goto end;

        }
    }
}
