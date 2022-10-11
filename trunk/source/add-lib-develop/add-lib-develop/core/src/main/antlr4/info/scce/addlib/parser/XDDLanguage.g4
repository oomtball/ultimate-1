grammar XDDLanguage;

start: ex=expr EOF;

expr
: INVERSE ex=expr					#inverseExpr
| COMPL ex=expr						#complExpr
| NOT ex=expr						#notExpr
| left=expr MEET right=expr			#meetExpr
| left=expr INF right=expr			#infExpr
| left=expr INTERSECT right=expr	#intersectExpr
| left=expr AND right=expr			#andExpr
| left=expr MULT right=expr			#multExpr
| left=expr JOIN right=expr			#joinExpr
| left=expr SUP right=expr			#supExpr
| left=expr UNION right=expr		#unionExpr
| left=expr OR right=expr			#orExpr
| left=expr ADD right=expr			#addExpr
| terminal=STRING_LITERAL			#terminalExpr
;

INVERSE : 'inv';
COMPL : 'compl';
NOT : 'not';
MEET : 'meet';
INF : 'inf';
INTERSECT : 'intersect';
AND : 'and';
MULT : 'mult';
JOIN : 'join';
SUP : 'sup';
UNION: 'union';
OR : 'or';
ADD : 'add';

STRING_LITERAL :
'"' ~["\\]* (('\\"' | '\\\\') ~["\\]*)* '"' 
| '\'' ~[\'\\]* (('\\\'' | '\\\\') ~[\'\\]*)* '\'';

/* skip spaces, tabs, and new lines */
WS : [ \t\r\n]+ -> skip;
