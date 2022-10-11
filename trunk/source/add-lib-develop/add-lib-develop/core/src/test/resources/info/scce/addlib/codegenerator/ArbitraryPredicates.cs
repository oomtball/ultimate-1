/* This file was generated with the ADD-Lib
 * http://add-lib.scce.info/ */

namespace TestCSharp {

    public class ArbitraryPredicates {

        public string sumOfArbitraryPredicates(Predicates predicates) {
            string result;
            goto eval139663483168288;
            end:
            return result;


            eval139663483168288:
            if (predicates.var0xd8090xdcd5x0x003e200x003f())
                goto eval139663483168256;
            else
                goto eval139663483168224;


            eval139663483168224:
            if (predicates.var0x00e40x003c100x002dv())
                goto eval139663483167520;
            else
                goto eval139663483167552;


            eval139663483168256:
            if (predicates.var0x00e40x003c100x002dv())
                goto eval139663483167840;
            else
                goto eval139663483167520;


            eval139663483167552:
            result = "0.0";
            goto end;


            eval139663483167520:
            result = "1.0";
            goto end;


            eval139663483167840:
            result = "2.0";
            goto end;

        }
    }
}
