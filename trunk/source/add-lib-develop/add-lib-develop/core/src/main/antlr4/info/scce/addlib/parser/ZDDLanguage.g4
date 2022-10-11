grammar ZDDLanguage;

start: ex=expr EOF?;

expr
:  LPAREN ex=expr RPAREN			#parenExpr
|  CHANGE ex=expr v=NUMBER			#changeExpr
|  left=expr DIFF right=expr		#notExpr
|  left=expr INTERSECT right=expr	#andExpr
|  left=expr UNION right=expr		#orExpr
|  var=ID							#varExpr
|  ap=atom							#atomExpr
;

atom
:  TRUE #trueExpr
|  'zTrue' i=NUMBER #zddOneExpr
|  FALSE #falseExpr
;

CHANGE: 'CHANGE';
DIFF : 'DIFF' | '-' | 'not' | 'NOT';
INTERSECT : 'INTSEC' | '&' | 'and' | 'AND';
UNION  : 'UNION' | '|' | 'or'  | 'OR';
LPAREN : '(';
RPAREN : ')';
TRUE : 'true';
FALSE : 'false';

NUMBER: [0-9] | [1-9][0-9]*;
ID : [a-zA-Z][a-zA-Z0-9_]*;

/* skip spaces, tabs, and new lines */
WS : [ \t\r\n]+ -> skip;

