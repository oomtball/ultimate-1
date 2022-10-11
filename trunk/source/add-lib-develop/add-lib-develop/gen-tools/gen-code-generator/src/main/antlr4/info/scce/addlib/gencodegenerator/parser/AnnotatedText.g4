grammar AnnotatedText;

textListRoot:
root=textList EOF;

textList:
(plainText | annotatedText)*;	

plainText:
~(AT | TRIPLE_QUOTE)+;

annotatedText:
AT annotation=ID (paramList=annotatedText_paramList body=annotatedText_body? | body=annotatedText_body);

annotatedText_paramList:
WS? '(' WS? (paramList+=param WS? (',' WS? paramList+=param WS?)*)? ')';

param: 
intValue=INT | doubleValue=DOUBLE | booleanValue=BOOLEAN | idValue=ID | stringValue=stringLiteral;

stringLiteral:
DOUBLE_QUOTE ~(BACKSLASH | DOUBLE_QUOTE | AT)* DOUBLE_QUOTE | SINGLE_QUOTE ~(BACKSLASH | SINGLE_QUOTE | AT)* SINGLE_QUOTE;

annotatedText_body:
WS? TRIPLE_QUOTE NL? body=textList TRIPLE_QUOTE NL?;

BACKSLASH:
'\\';

ESCAPED_BACKSLASH:
'\\\\';

DOUBLE_QUOTE:
'"';

ESCAPED_DOUBLE_QUOTE:
'\\"';

SINGLE_QUOTE:
'\'';

ESCAPED_SINGLE_QUOTE:
'\\\'';

AT: 
'@';

ESCAPED_AT: 
'\\@';

TRIPLE_QUOTE:
'\'\'\'';

NL:
'\r\n' | '\r' | '\n';

ID: 
[a-zA-Z] [a-zA-Z0-9]* ([._\-] [a-zA-Z0-9]+)*;

WS:
[ \t]+;

INT:
('+' | '-')? [0-9]+;

DOUBLE: 
('+' | '-')? [0-9]* '.' [0-9]+ (('e' | 'E') ('+' | '-')? [0-9]+)?;

BOOLEAN:
'true' | 'false';

WILDCARD:
.;
