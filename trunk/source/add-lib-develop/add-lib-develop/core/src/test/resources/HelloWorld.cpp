/* This file was generated with the ADD-Lib Gen Tools
 * http://add-lib.scce.info/ */

class HelloWorld {
public:

char const* helloWorld(Predicates pPredicates){
    char const* result;
    Predicates predicates = pPredicates;
    goto eval140494760203584;
    end:
    return result;

    
    eval140494760203584:
    if (predicates.sayHello())
        goto eval140494760203456;
    else
        goto eval140494760203552;

    
    eval140494760203552:
    if (predicates.sayWorld())
        goto eval140494760203488;
    else
        goto eval140494760203520;

    
    eval140494760203456:
    if (predicates.sayWorld())
        goto eval140494760203392;
    else
        goto eval140494760203424;

        
    eval140494760203520:
    result = "***** *****!";
    goto end;

    
    eval140494760203488:
    result = "***** World!";
    goto end;

    
    eval140494760203424:
    result = "Hello *****!";
    goto end;

    
    eval140494760203392:
    result = "Hello World!";
    goto end;

    }

};
