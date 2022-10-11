grammar BDDLanguage;

expr
: LPAREN ex=expr RPAREN	#parenExpr
| NOT ex=expr				#notExpr
| left=expr AND right=expr	#andExpr
| left=expr OR right=expr	#orExpr
| var=ID					#varExpr
| ap=atom					#atomExpr
;

atom
:  TRUE #trueExpr
|  FALSE #falseExpr
;

/* Tokens */
LPAREN : '(';
RPAREN : ')';
NOT : '!' | 'not' | 'NOT';
AND : '&' | 'and' | 'AND';
OR  : '|' | 'or' | 'OR';
TRUE : '1' | 'true' | 'TRUE';
FALSE : '0' | 'false' | 'FALSE';
ID : [a-zA-Z][a-zA-Z0-9_]*;

/* skip spaces, tabs, and new lines */
WS : [ \t\r\n]+ -> skip;

