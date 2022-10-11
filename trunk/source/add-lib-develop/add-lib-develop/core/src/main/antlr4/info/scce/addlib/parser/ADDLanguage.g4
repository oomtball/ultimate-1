grammar ADDLanguage;

expr
: LPAREN ex=expr RPAREN  				#parenExpr
| left=expr op=('*'|'/') right=expr		#mulDivExpr
| left=expr op=('+'|'-') right=expr		#plusMinusExpr
| var=ID								#varExpr
| real=DOUBLE							#realExpr
;

/* Tokens */
LPAREN : '(';
RPAREN : ')';
DOUBLE: ('+' | '-')? [0-9]* '.' [0-9]+ (('e' | 'E') ('+' | '-')? [0-9]+)?;
ID : [a-zA-Z][a-zA-Z0-9-_]*;

/* skip spaces, tabs, and new lines */
WS : [ \t\r\n]+ -> skip;
