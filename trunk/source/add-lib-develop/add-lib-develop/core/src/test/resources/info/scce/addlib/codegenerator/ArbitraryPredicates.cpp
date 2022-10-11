/* This file was generated with the ADD-Lib Gen Tools
 * http://add-lib.scce.info/ */

class ArbitraryPredicates {
public:

char const* sumOfArbitraryPredicates(Predicates pPredicates){
    char const* result;
    Predicates predicates = pPredicates;
    goto eval140195455107616;
    end:
    return result;

    
    eval140195455107616:
    if (predicates.var0xd8090xdcd5x0x003e200x003f())
        goto eval140195455107584;
    else
        goto eval140195455107552;

    
    eval140195455107552:
    if (predicates.var0x00e40x003c100x002dv())
        goto eval140195455106848;
    else
        goto eval140195455106880;

    
    eval140195455107584:
    if (predicates.var0x00e40x003c100x002dv())
        goto eval140195455107168;
    else
        goto eval140195455106848;

        
    eval140195455106880:
    result = "0.0";
    goto end;

    
    eval140195455106848:
    result = "1.0";
    goto end;

    
    eval140195455107168:
    result = "2.0";
    goto end;

    }

};
