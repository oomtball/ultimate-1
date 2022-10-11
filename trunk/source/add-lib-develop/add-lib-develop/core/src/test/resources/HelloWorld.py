# This file was generated with the ADD-Lib
# http://add-lib.scce.info/

class HelloWorld:

    def helloWorld(self,predicates):
        return self.__eval140292090775008(predicates)
    


    def __eval140292090775008(self,predicates):
        if (predicates.sayHello()):
            return self.__eval140292090774880(predicates)
        else:
            return self.__eval140292090774976(predicates)


    def __eval140292090774976(self,predicates):
        if (predicates.sayWorld()):
            return self.__eval140292090774912(predicates)
        else:
            return self.__eval140292090774944(predicates)


    def __eval140292090774880(self,predicates):
        if (predicates.sayWorld()):
            return self.__eval140292090774816(predicates)
        else:
            return self.__eval140292090774848(predicates)

    def __eval140292090774944(self,predicates):
        return "***** *****!"    

    def __eval140292090774912(self,predicates):
        return "***** World!"    

    def __eval140292090774848(self,predicates):
        return "Hello *****!"    

    def __eval140292090774816(self,predicates):
        return "Hello World!"    
	
