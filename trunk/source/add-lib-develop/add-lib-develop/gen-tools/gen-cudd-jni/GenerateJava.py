class GenerateJava:

	def __init__(self, method):
		self.method = method

	def generateJava(self):
		returnType = self.method.returnType
		javaReturnType = self.method.javaReturnType(self.method.returnType, self.method.asteriks,True)
		methodName = self.method.methodName
		strJavaParameter = self.javaParameter()

		javaMethod = 'public static ' + javaReturnType + ' ' + methodName + '('+strJavaParameter+') {\n\t'+self.javaMethodBody() + '\n}'
		nativeMethod = 'private static native '+javaReturnType+' native_'+methodName+'('+strJavaParameter+');'
		generatedJava = javaMethod+'\n\n'+nativeMethod+'\n'
		return generatedJava

	#[(DdManager', '*dd'), ('int', 'index)')] -> 'long dd, int index'
	def javaParameter(self):
		javaParameter = ''
		parameterList = []
		for (par, name) in self.method.parameters:
			#removes asteriks from name
			asterikNameTuple = self.method.splitAsterikAndName(name)
			javaReturnType = self.method.javaReturnType(par,asterikNameTuple[0],True)
			javaParameter = javaReturnType + ' ' + asterikNameTuple[1]
			parameterList.append(javaParameter)
		return ','.join(parameterList)

	def javaMethodBody(self):
		argumentList = []
		javaMethod = ''
		for (par, name) in self.method.parameters:
			asterikNameTuple = self.method.splitAsterikAndName(name)
			argumentList.append(asterikNameTuple[1])
		argumentsCommaSeperated = ','.join(argumentList)
		javaMethodBody = 'ensureSharedLibraries();\n\t'
		if self.method.returnType!='void':
			javaMethodBody += 'return '
		javaMethodBody += 'native_'+self.method.methodName+'('+argumentsCommaSeperated+');'
		return javaMethodBody
