import re
import Method
import GenerateJava
import GenerateC

generatedJavaPath = '../../java/src/main/java/info/scce/addlib/cudd/GeneratedJava.java'
generatedCPath = '../../native/source/generatedC.cpp'

WS = '(\s|\t)*'
returnTypeREGEX = '?P<returnType>([a-zA-Z])+|unsigned (int|long)|)'
asteriksREGEX = '(?P<asteriks>\**?)'
methodNameREGEX = '(?P<methodName>([a-zA-Z0-9]|_)+)'
parameterRegex = '(?P<parameters>\(.*\))'

lineCounter = 0
matchedCounter = 0
generatedJavaCode = ''
generatedCImpl = '''#include "info_scce_addlib_cudd_GeneratedJava.h"
#include "cudd.h"
#include "cuddInt.h"

'''
generatedJavaCounter = 0
generatedCCounter = 0

def javaTemplate():
	global generatedJavaCode
	generatedJavaCode = 'package info.scce.addlib.cudd;'
	generatedJavaCode += '\npublic class GeneratedJava {'
	generatedJavaCode += '''

	/*
	 * Shared objects will be loaded dynamically when needed. This makes debugging
	 * easier in case they cannot be found.
	 */

	private static boolean cuddAvailable = false;
	private static boolean addLibNativeAvailable = false;

	protected static void ensureSharedLibraries() {
		if (!cuddAvailable) {
			System.loadLibrary("cudd");
			cuddAvailable = true;
		}
		if (!addLibNativeAvailable) {
			System.loadLibrary("add-lib");
			addLibNativeAvailable = true;
		}
	} 

	'''

with open('cuddhtext') as fp:
	javaTemplate();
	for line in fp:
		#Every uneven line contains just a comment
		if lineCounter % 2 == 0:
			pp = re.compile('('+returnTypeREGEX+WS+asteriksREGEX+WS+methodNameREGEX+WS+parameterRegex+'.*')
			m = pp.match(line)
			if m:
				matchedCounter+=1
				returnType = m.group('returnType')
				asteriks =  m.group('asteriks')
				methodName = m.group('methodName')
				parameters = m.group('parameters')
				method = Method.Method(returnType, asteriks, methodName,parameters)
				if(method.isGeneratable()):
					javaCode = GenerateJava.GenerateJava(method)
					cCode = GenerateC.GenerateC(method)

					generatedMethods = javaCode.generateJava()
					generatedCCode = cCode.generateC()

					if len(generatedMethods) >=1:
						generatedJavaCounter+=1
						generatedJavaCode += generatedMethods+'\n'
					if len(generatedCCode) >=1:
						generatedCCounter+=1
						generatedCImpl += generatedCCode+'\n'
		lineCounter+=1
	generatedJavaCode += '\n}'


#delete all previous text
open(generatedJavaPath, 'w').close()
open(generatedCPath, 'w').close()

with open(generatedJavaPath,'a') as file:
	file.write(generatedJavaCode)
with open(generatedCPath,'a') as file:
	file.write(generatedCImpl)

#918 447 317 317
#918 lines read
#447 Methods matched
#317 Java methods generated
#317 C methods generated
print lineCounter
print matchedCounter
print generatedJavaCounter
print generatedCCounter