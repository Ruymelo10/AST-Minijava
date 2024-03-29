grammar MiniJavaGrammar;


goal: mainClass ( classDeclaration )* EOF;

mainClass: 'class' identifier '{' 'public' 'static' 'void' 'main' '(' 'String' '[' ']' identifier ')' '{' statement '}' '}';

classDeclaration: 'class' identifier ('extends' identifier)? '{' (varDeclaration)* (methodDeclaration)* '}';

varDeclaration: type identifier ';';

methodDeclaration: 'public' type identifier '(' ( type identifier ( ',' type identifier )* )? ')' '{' ( varDeclaration )* ( statement )* 'return' expression ';' '}';

type: 'int' '[' ']'
| 'boolean'
| 'int'
| identifier;

statement: '{' ( statement )* '}'
| 'if' '(' expression ')' statement 'else' statement
| 'while' '(' expression ')' statement
| 'System.out.println' '(' expression ')' ';'
| identifier '=' expression ';'
| identifier '[' expression ']' '=' expression ';';

expression: expression ( '&&' | '<' | '+' | '-' | '*' ) expression
| expression '[' expression ']'
| expression '.' 'length'
| expression '.' identifier '(' ( expression ( ',' expression )* )? ')'
| integer_literal
| 'true'
| 'false'
| identifier
| 'this'
| 'new' 'int' '[' expression ']'
| 'new' identifier '(' ')'
| '!' expression
| '(' expression ')';

identifier: IDENTIFIER;
integer_literal: INTEGER_LITERAL;

IDENTIFIER:  [_a-zA-Z][a-zA-Z0-9_]*;
INTEGER_LITERAL: [1-9][0-9]* | '0';
WHITESPACE: [ \t\r\n] -> skip;
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;