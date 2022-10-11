/* This file was generated with the ADD-Lib
 * http://add-lib.scce.info/ */


Predicates predicates;
char* returnstr;

char* helloWorld(Predicates pPredicates);



char* helloWorld(Predicates pPredicates) {
    predicates = pPredicates;
    goto eval140638574201760;
    end:
    return returnstr;


    eval140638574201760:
    
    if (predicates.sayHello()){
        goto eval140638574201632;
    }else{
        goto eval140638574201728;
    }

    eval140638574201728:
    
    if (predicates.sayWorld()){
        goto eval140638574201664;
    }else{
        goto eval140638574201696;
    }

    eval140638574201632:
    
    if (predicates.sayWorld()){
        goto eval140638574201568;
    }else{
        goto eval140638574201600;
    }

    eval140638574201696:

    returnstr = "***** *****!";
    goto end;


    eval140638574201664:

    returnstr = "***** World!";
    goto end;


    eval140638574201600:

    returnstr = "Hello *****!";
    goto end;


    eval140638574201568:

    returnstr = "Hello World!";
    goto end;

}

