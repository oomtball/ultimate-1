class Predicates:

	def __init__(self, a, b, c):
		self.a = a
		self.b = b
		self.c = c

	def predA(self):
		return self.a()

	def predB(self):
		return self.b()

	def predC(self):
		return self.c()
