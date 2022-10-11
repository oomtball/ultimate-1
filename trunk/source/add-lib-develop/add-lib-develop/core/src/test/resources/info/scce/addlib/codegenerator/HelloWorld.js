/* This file was generated with the ADD-Lib Gen Tools
 * http://add-lib.scce.info/ */

var HelloWorld = {

    helloWorld: function(predicates) {
        return this.eval140421617315936(predicates);
    },

    eval140421617315936: function(predicates) {
        if (predicates.sayHello())
            return this.eval140421617315808(predicates);
        else
            return this.eval140421617315904(predicates);
    },

    eval140421617315904: function(predicates) {
        if (predicates.sayWorld())
            return this.eval140421617315840(predicates);
        else
            return this.eval140421617315872(predicates);
    },

    eval140421617315808: function(predicates) {
        if (predicates.sayWorld())
            return this.eval140421617315744(predicates);
        else
            return this.eval140421617315776(predicates);
    },

    eval140421617315872: function(predicates) {
        return "***** *****!";
    },

    eval140421617315840: function(predicates) {
        return "***** World!";
    },

    eval140421617315776: function(predicates) {
        return "Hello *****!";
    },

    eval140421617315744: function(predicates) {
        return "Hello World!";
    },
};
