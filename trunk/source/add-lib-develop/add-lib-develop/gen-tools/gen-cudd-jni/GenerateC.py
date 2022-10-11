class GenerateC:

	def __init__(self, method):
		self.method = method

	def generateC(self):
		methodName = self.method.methodName
		parameters = self.method.parameters
		packageName = 'info_scce_addlib_cudd_GeneratedJava'

		#Cudd_bddNewVar -> Cudd_1bddNewVar
		headerMethodName = methodName[:5]+'1'+methodName[5:]

		strHeaderGeneratedParameters = self.headerGeneratedParameters()
		strJavaReturnType = self.cReturnType()
		generatedCCode = 'JNIEXPORT '+strJavaReturnType+' JNICALL '+'Java_'+packageName+'_native_1'+headerMethodName+'\n (JNIEnv '
		if(self.method.containsArray()):
			generatedCCode+='*env, jclass, '+strHeaderGeneratedParameters+') {\n\t'+self.generateMethodBodyArray()+'\n}'
		else:
			generatedCCode+='*, jclass, '+strHeaderGeneratedParameters+') {\n\t'+self.generatedCMethodBody()+'\n}'
		return generatedCCode

	#[(DdManager,*dd),(int,*index)] -> jlong dd, jintArray index
	def headerGeneratedParameters(self):
		javaParameter = ''
		parameterList = []
		for (par, name) in self.method.parameters:
			asterikNameTuple = self.method.splitAsterikAndName(name)
			strJavaReturnType = self.method.javaReturnType(par,asterikNameTuple[0], False)
			if len(asterikNameTuple[0])==1 and (par=='int' or par=='long' or par=='double'):
				javaParameter = 'j'+strJavaReturnType + 'Array ' + asterikNameTuple[1]
			else:
				javaParameter = 'j'+strJavaReturnType + ' ' + asterikNameTuple[1]
			parameterList.append(javaParameter)
		return ','.join(parameterList)

	#[(DdManager,*dd),(int,index)] -> (DdManager*)dd,index
	def cMethodArguments(self):
		ret = ''
		argumentList = []
		for (par, name) in self.method.parameters:
			asterikNameTuple = self.method.splitAsterikAndName(name)
			if len(asterikNameTuple[0])==1:
				if par=='int' or par=='long' or par=='double':
					ret = asterikNameTuple[1]+'Array'
				else:
					ret = '('+par+asterikNameTuple[0]+')'+asterikNameTuple[1]
			else:
				ret = asterikNameTuple[1]
			argumentList.append(ret)
		return self.method.methodName+'('+(','.join(argumentList))+');' 


	def generatePrimitiveArray(self, type, name):
		returnType = self.method.returnType
		asteriks = self.method.asteriks
		strJavaReturnType = self.method.javaReturnType(returnType, asteriks,False)
		ret = 'j'+type + ' *'+name+'Array = env->Get'+type.capitalize()+'ArrayElements('+name+',0);'
		return ret
	
	def generateMethodBodyArray(self):
		ret = ''
		releaseArray = ''
		for (par, name) in self.method.parameters:
			asterikNameTuple = self.method.splitAsterikAndName(name)
			if len(asterikNameTuple[0]) == 1 and par == 'int' or par == 'long' or par == 'double':
				nameWithoutAsteriks = asterikNameTuple[1]
				ret += '\n\t'+self.generatePrimitiveArray(par, nameWithoutAsteriks)
				releaseArray+='\n\tenv->Release'+par.capitalize()+'ArrayElements'+'('+nameWithoutAsteriks+','+nameWithoutAsteriks+'Array'+',0);'
		if(self.method.returnType != 'void'):
			ret += '\n\tj'+self.method.javaReturnType(self.method.returnType,self.method.asteriks,False)+' result ='
			ret += '(j'+self.method.javaReturnType(self.method.returnType,self.method.asteriks,False)+')'+self.cMethodArguments()
			ret += releaseArray
			ret += '\n\treturn result;'
		else:
			ret += self.cMethodArguments()
			ret += releaseArray
		
		return ret

	def generatedCMethodBody(self):
		returnType = self.method.returnType
		parameters = self.method.parameters

		if returnType=='void':
			return self.cMethodArguments()
		elif returnType=='long[]':
			#Atm methods with such return types are not being generated
			#TODO: Fix this, should return something properly
			return self.arrayOfPointerReturnTypeMethodBody()
		return 'return (j'+self.method.javaReturnType(returnType,self.method.asteriks,False)+') '+ self.cMethodArguments()


	def arrayOfPointerReturnTypeMethodBody(self):
		return ''

	def cReturnType(self):
		strJavaReturnType = self.method.javaReturnType(self.method.returnType,self.method.asteriks,False)
		if strJavaReturnType=='int' or strJavaReturnType=='long' or strJavaReturnType=='double':
			return 'j'+strJavaReturnType
		if strJavaReturnType=='long[]':
			return 'jlongArray'
		if strJavaReturnType=='void':
			return strJavaReturnType
		return ''
