### Table of Contents
- [Language Features](#language-features)
  - [Required Features](#required-features)
  - [Additional Features](#additional-features)
- [Implementation](#implementation)
  - [Expression operators' precedence and associates](#expression-operators-precedence-and-associates)
- [Notes](#notes)


## Language Features

### Required Features
Features required by book.
* data types
    * boolean: `true` or `false`
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
* expressions precedence and association.
* statements
    * statement ends with `;`
    * single line comment uses `//`
    * statement block noted by `{` and `}`
    * dynamic type, variables noted by `var`
    * assignment: "var a = 1;"
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
        * access other methods/fields in methods use `this` keyword.  
        * class is first class, assign class to variable: "var v = Foo;"
        * inheritance: use `<` "class Bar < Foo { ... }"
            * init methods are inherited.
            * call super class init methods through `super`. "super.init();"
* standard library
    * clock(): return number of seconds since the program started.
* scanner
    * record line number of each token

### Additional Features
These are not required by the specification (the book).
* syntax 
    * for each loop
    * boolean operators: `and`, `or`, `not`, `&&`, `||`, `!` (weird to mix both styles)
    * `elif`
    * `switch`
    * multile comments use `/*` and `*/`
* scanner
    * record column of each token

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

Same precedence as C.

(refer: [C language expression precedence](https://en.cppreference.com/w/c/language/operator_precedence))

## Notes
* How lox implements array?
* I assume TAB always has size of 4 column, is that always true?