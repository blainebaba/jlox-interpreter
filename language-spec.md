### Table of Contents
- [Language Features](#language-features)
  - [Required Features](#required-features)
  - [Additional Features](#additional-features)
- [Implementation](#implementation)
  - [Expression operators' precedence and associates](#expression-operators-precedence-and-associates)
  - [Expression Parsing Rules](#expression-parsing-rules)
  - [Statement parsing rules](#statement-parsing-rules)
- [Notes](#notes)
- [Questions](#questions)


## Language Features

### Required Features
Features required by book.
* data types
    * boolean: `true` or `false`
      * implicit conversion: `false` and `nil` are `false`, anything else is `true`
    * number: in format "123" or "123.456"
    * string: enclosed by double quotes, in single line.
    * nil: represented by `nil`
* expressions operators
    * arithmetic: 
        * binary: `+`, `-`, `*`, `/`
        * unary: `-` to negate
    * comparison and equality
        * `==`, `!=`, `>=`, `<=`, `>`, `>`
    * logical
        * `and`, `or`, `!`
    * grouping, use `(` and `)`
    * String:
      * `+` is used to concat two strings.
* expressions precedence and association.
* statements
    * statement ends with `;`
    * single line comment uses `//`
    * statement block noted by `{` and `}`
    * dynamic type, variables noted by `var`
    * variable declaration: "var a = 1;"
    * value assignment: "a = 1 +2;", the assigned variable must be declared.
    * `print` keyword: ' print "hello world"; ', no parentheses.
    * control flow, same format as java
        * `if`, `else`
        * `for`, `while`
    * function
        * define function: use keyword `fun`: "fun foo(a, b) {}"
        * call function: "foo();"
        * return value from function uses `return`, always return `nil` implicitly.
        * function as first class value
        * function closure. https://www.lua.org/pil/6.1.html
    * classes
        * defines class with `class`: "class Foo { ... }"
        * defines methods like defining function but without `fun` keyword:
            "foo() { ... }"
        * constructor is method named "init".
        * instantiate uses class name as function: "var i = Foo();"
        * access instance properties use `.`, like "foo.bar = 1;"
        * access other methods/fields in methods use `this` keyword.  
        * class is first class, assign class to variable: "var v = Foo;"
        * inheritance: use `<` "class Bar < Foo { ... }"
            * init methods are inherited.
            * call super class init methods through `super`. "super.init();"
* standard library
    * clock(): return number of seconds since the program started.
* scanner
    * record line number of each token
* parser
    * recursive descent parsing
    * error recovery: continue parsing when meets error.


### Additional Features
These are not required by the specification (the book).
* syntax 
    * for each loop
    * boolean operators: `and`, `or`, `not`, `&&`, `||`, `!` (weird to mix both styles)
    * `elif`
    * `switch`
    * multile comments use `/*` and `*/`
    * For `+`, implicit convert to string type when one operand is string.
    * `break` and `continue`
* scanner
    * record column of each token
* parser
    * throw parserError if variable is used without being initialized. Different from undefined variable.
* interpreter
    * print stack trace

## Implementation

### Expression operators' precedence and associates

Precedence from low to high.

| Operators         | Description         | Associates    |
| :---------------- | :------------------ | :------------ |
| `or`              | bool or             | left-to-right |
| `and`             | bool and            | left-to-right |
| `==` `!=`         | equality            | left-to-right |
| `<` `>` `<=` `>=` | comparison          | left-to-right |
| `+` `-`           | add and subtract    | left-to-right |
| `*` `/`           | multiply and divide | left-to-right |
| `-` `!`           | negate, bool not    | right-to-left |
| `()`              | function call       | left-to-right |

Same precedence as C.

(refer: [C language expression precedence](https://en.cppreference.com/w/c/language/operator_precedence))

### Expression Parsing Rules
```
EXPR -> ASSIGN_TERM
ASSIGN_TERM(SET_TERM) -> (GET_TERM ".")? IDENTIFIER "=" ASSIGN_TERM | OR_TERM
OR_TERM -> AND_TERM ("or" AND_TERM)*
AND_TERM -> EQUAL_TERM ("and" EQUAL_TERM)*
EQUAL_TERM -> COMP_TERM (("=="|"!=") COMP_TERM)*
COMP_TERM -> ADD_TERM (("<"|">"|"<="|">=") ADD_TERM)*
ADD_TERM -> MUL_TERM (("+"|"-") MUL_TERM)*
MUL_TERM -> UNARY_TERM (("*"|"/") UNARY_TERM)*
UNARY_TERM -> ("!"|"-") UNARY_TERM | CALL_TERM
CALL_TERM -> GET_TERM ( "(" (EXPR (, EXPR)* )? ")")*
GET_TERM -> PRIMARY ("." IDENTIFIER )*
PRIMARY -> STRING|NUMBER|IDENTIFIER|"true"|"false"|nil|"(" EXPR ")"|"this"|"super"
```

Implement parser is basically converting these rules into code.

### Statement parsing rules
PROGRAM -> RELAX_STMT* "EOF"
RELAX_STMT -> DECLARE_VAR_STMT | DECLARE_FUN_STMT | DECLARE_CLASS_STMT | STRICT_STMT
STRICT_STMT -> EXPR_STMT | PRINT_STMT | BLOCK_STMT | IF_STMT | WHILE_STMT | FOR_STMT | RETURN_STMT

DECLARE_VAR_STMT -> "var" IDENTIFIER ("=" EXPR)? ";"
DECLARE_FUN_STMT -> fun FUNCY_STMT
DECLARE_CLASS_STMT -> class IDENTIFIER ( "<" IDENTIFIER )? "{" FUNCY_STMT* "}"
FUNCY_STMT -> IDENTIFIER "(" ( IDENTIFIER (, IDENTIFIER)* )? ")" "{" RELAX_STMT* "}" 
EXPR_STMT -> EXPR ";"
PRINT_STMT -> "print" EXPR ";"
BLOCK_STMT -> "{" RELAX_STMT* "}"
IF_STMT -> "if" "(" EXPR ")" RELAX_STMT ("else" RELAX_STMT)?
WHILE_STMT -> "while" "(" EXPR ")" RELAX_STMT
FOR_STMT -> "for" "(" (DECLARE_VAR_STMT|EXPR_STMT|";") EXPR? ";" EXPR? ";" ")" RELAX_STMT
RETURN_STMT -> "return" (Expr)? ";"

RELAX_STMT is introduced to exclude DECLARE_STMT from some use cases.
FUNCY_STMT is function define statement without "fun" keyword, this is re-used to define methods.

## Notes
* notice type mismatch in expression parsing will not throw parser error. This is because lox is dynamic type language, so type mis-match is a runtime error.

## Questions
* How lox implements array?
* I assume TAB always has size of 4 column, is that always true?
* how is divided by zero handled ?
* why EOF token at the end of program? Without it seems fine.
* with syntax suger, how to correctly locate syntax error to correct place?