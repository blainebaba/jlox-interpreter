## Language features

* data types
    * boolean: `true` or `false`
    * number: in format "123" or "123.456"
    * string: enclosed by double quotes
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
    



## Additional language features

These are not required by the specification (the book).

* for each loop
* boolean operators: `and`, `or`, `not`, `&&`, `||`, `!` (weird to mix both styles)
* `elif`
* 

## Notes
* what about array?