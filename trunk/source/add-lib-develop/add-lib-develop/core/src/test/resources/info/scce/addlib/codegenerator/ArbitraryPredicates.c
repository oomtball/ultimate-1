/* This file was generated with the ADD-Lib
 * http://add-lib.scce.info/ */


Predicates predicates;
char* returnstr;

char* sumOfArbitraryPredicates(Predicates pPredicates);



char* sumOfArbitraryPredicates(Predicates pPredicates) {
    predicates = pPredicates;
    goto eval139741329377184;
    end:
    return returnstr;


    eval139741329377184:
    
    if (predicates.var0xd8090xdcd5x0x003e200x003f()){
        goto eval139741329377152;
    }else{
        goto eval139741329377120;
    }

    eval139741329377120:
    
    if (predicates.var0x00e40x003c100x002dv()){
        goto eval139741329376416;
    }else{
        goto eval139741329376448;
    }

    eval139741329377152:
    
    if (predicates.var0x00e40x003c100x002dv()){
        goto eval139741329376736;
    }else{
        goto eval139741329376416;
    }

    eval139741329376448:

    returnstr = "0.0";
    goto end;


    eval139741329376416:

    returnstr = "1.0";
    goto end;


    eval139741329376736:

    returnstr = "2.0";
    goto end;

}

