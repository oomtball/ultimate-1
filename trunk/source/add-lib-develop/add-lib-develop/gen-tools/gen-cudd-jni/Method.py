class Method:
	
	def __init__(self, returnType, asteriks, methodName, parameters):
		self.returnType = returnType
		self.asteriks = asteriks
		self.methodName = methodName
		self.parameters = self.createParameterTupleList(parameters)

	def createParameterTupleList(self,parameterString):
		#parameters with removed parentheses
		#(DdManager *dd, int index) -> DdManager *dd, int index
		parameters = parameterString[1:-1]

		#DdManager *dd, int index -> DdManager *dd int index
		parameterStringWithoutComma = parameters.replace(',','')
		
		#DdManager *dd int index -> [DdManager,*dd,int,index]
		parameterList = parameterStringWithoutComma.replace(' ',',').split(',')

		#[DdManager,*dd,int,index] -> [(DdManager,*dd), (int,index)]
		it = iter(parameterList)
		parameterTupleList = zip(it,it)
		return parameterTupleList

	#DdManager** or DdNode** => long[]
	#DdManager or DdNode => long
	def javaReturnType(self,returnType, asteriks, forJavaCode):
		if returnType=='DdManager' or returnType=='DdNode':
			return 'long'
		elif returnType=='int' or returnType=='long' or returnType=='double' or returnType=='void':
			if len(asteriks) == 1 and returnType !='void' and forJavaCode:
				return returnType+"[]";
			return returnType
		return ''

	#*dd -> (*,dd)
	def splitAsterikAndName(self,name):
		counter = 0
		while name[counter]=='*':
			counter+=1
		return (name[0:counter],name[counter:])

	def containsArray(self):
		for (par, name) in self.parameters:
			asterikNameTuple = self.splitAsterikAndName(name)
			if len(asterikNameTuple[0])!=0 and (par=='int' or par=='long' or par=='double'):
				return True
		return False

	def returnTypeIsArray(self):
		return (len(self.asteriks) >= 1) and (self.returnType=='int' or self.returnType=='long' or self.returnType=='double')

	def isGeneratable(self):
		#Check return type
		#Can't handle Array as returntype or ** or more asteriks
		if(self.returnTypeIsArray()) or len(self.asteriks) > 1:
			return False
		#can only handle DdManager/DdNode,int,long,double,void
		if(len(self.javaReturnType(self.returnType,self.asteriks,True)) == 0):
			return False

		#check argument types
		for(param, name) in self.parameters:
			splittedAsterikAndName = self.splitAsterikAndName(name)
			numberOfAsteriks = len(splittedAsterikAndName[0])
			#same as for above
			if(len(self.javaReturnType(param, splittedAsterikAndName[0],True)) == 0):
				return False
			#cant handle void as parameter or ** or more asteriks
			if(param == 'void') or numberOfAsteriks > 1:
				return False
		return True