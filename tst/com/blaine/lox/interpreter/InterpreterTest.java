package com.blaine.lox.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blaine.lox.Scanner;
import com.blaine.lox.Token;
import com.blaine.lox.Utils;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.parser.Parser;
import com.blaine.lox.parser.ParserError;
import com.blaine.lox.parser.VarResolver;

// test script with more than one stmt/expr.
public class InterpreterTest {

    private Interpreter interpreter;
    private Environment env;

    @Before
    public void setup() {
        interpreter = new Interpreter();
        env = interpreter.getCurEnv();
      }

    @Test
    public void testVariables() {
        execute(parseStmts("var a = 1; var b = 2; var c = a + b;"));
        assertEquals(3.0, env.getVar("c"));
    } 

    @Test
    public void testVariableScope() {
        {
            execute(parseStmts("var a = 1; {a = 2;}"));
            assertEquals(2.0, env.getVar("a"));
        }

        {
            execute(parseStmts("var b = 1; {var b = 2; b = 3;}"));
            assertEquals(1.0, env.getVar("b"));
        }

        {
            execute(parseStmts("{var c = 1;}"));
            assertEquals(null, env.getVar("c"));
        }
    } 

    @Test
    public void testIfStmt() {
        execute(parseStmts("var a=1; var b=2; if (a<b) {a=a+1;} else {b=b+1;}"));
        assertEquals(2.0, env.getVar("a"));
        assertEquals(2.0, env.getVar("b"));
    }

    @Test
    public void testWhileStmt() {
        execute(parseStmts("var a = 1; while (a < 10) {a = a + 1;}"));
        assertEquals(10.0, env.getVar("a"));
    }

    @Test
    public void testForStmt() {
        execute(parseStmts("var a = 0; for (var b = 0; b < 10; b = b + 1) { a = a + 1;}"));
        assertEquals(10.0, env.getVar("a"));
        // "b" should not be available outside of for.
        assertEquals(null, env.getVar("b"));
    }

    @Test
    public void testFobonacci() throws Exception {
        String script = Utils.readFile(getClass(), "Fibonacci.lox");
        execute(parseStmts(script));
        // 1 2 3 5 8   13 21 34 55 89
        assertEquals(89.0, env.getVar("cur"));
    }

    @Test
    public void testClosure() throws Exception {
        String script = Utils.readFile(getClass(), "ClosureTest.lox");
        execute(parseStmts(script));
        assertEquals(1.0, env.getVar("a"));
        assertEquals(2.0, env.getVar("b"));
    }

    @Test
    public void testBuiltInFunctions() throws Exception {
        execute(parseStmts("var a = clock();"));
        Thread.sleep(1000);
        execute(parseStmts("var b = clock();"));
        double t1 = (double)env.getVar("a");
        double t2 = (double)env.getVar("b");
        assertEquals(1.0, (t2 - t1), 0.1);
    }

    @Test
    public void testVarResolving() throws Exception {
        String script = Utils.readFile(getClass(), "VarResolvingTest.lox");
        execute(parseStmts(script));
        assertEquals(1.0, env.getVar("b"));
        assertEquals(1.0, env.getVar("c"));
    }

    @Test
    public void testClass() {
        execute(parseStmts("class Foo {} var foo = Foo();"));
        assertEquals(LoxClass.class, env.getVar("Foo").getClass());
        assertEquals(LoxInstance.class, env.getVar("foo").getClass());
    }

    @Test
    public void testClassProperties() {
        execute(parseStmts("class Foo {} var foo = Foo();foo.v1 = 123; var a = foo.v1;"));
        assertEquals(123.0, env.getVar("a"));
    }

    @Test
    public void testClassMethods() {
        execute(parseStmts("class Bar { add(a,b) { return a + b;} } var b = 1; var c = 2; var d = Bar(); var e = d.add(b,c);"));
        assertEquals(3.0, env.getVar("e"));

    }

    @Test
    public void testClassThisVariable() {
        execute(parseStmts("class FooFoo { get() {return this.value;} } var f = FooFoo(); var g = f.get();"));
        assertEquals(null, env.getVar("g"));
        execute(parseStmts("f.value = 123; g = f.get();"));
        assertEquals(123.0, env.getVar("g"));
    }

    @Test
    public void testClassInitializer() {
        execute(parseStmts("class Foo { init(a,b) { this.a = a; this.b = b;} } var foo = Foo(1,2); var a = foo.a; var b = foo.b;"));
        assertEquals(1.0, env.getVar("a"));
        assertEquals(2.0, env.getVar("b"));
    }

    @Test
    public void testClassInheritance() {
        execute(parseStmts("class Foo { get() {return 1;} } class Bar < Foo {} var bar = Bar(); var a = bar.get();"));
        assertEquals(1.0, env.getVar("a"));
        execute(parseStmts("class FooFoo { get() {return 1;} } class BarBar < FooFoo { get() {return 2;}} var barbar = BarBar(); var b = barbar.get();"));
        assertEquals(2.0, env.getVar("b"));
    }

    @Test
    public void testClassSuperVariable() {
        execute(parseStmts("class Foo { get() {return 1;} } class Bar < Foo { get() {return super.get() + 1;} } var bar = Bar(); var a = bar.get();"));
        assertEquals(2.0, env.getVar("a"));
    }

    private List<Stmt> parseStmts(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        List<Stmt> stmts = parser.parse();
        if (parser.getErrors().size() > 0) {
            throw parser.getErrors().get(0);
        }
        new VarResolver(interpreter).resolve(stmts);
        assertTrue(parser.isEnd());
        return stmts;
    }

    private void parseExpectError(String script) {
        try {
            parseStmts(script);
        } catch (ParserError e) {
            return;
        }
        fail("runtime error expected");
    }

    private void execute(List<Stmt> stmts) {
        for (Stmt stmt: stmts) {
            stmt.accept(interpreter);
        }
    }

    private void executeExpectError(List<Stmt> stmts) {
        try {
            for (Stmt stmt: stmts) {
                stmt.accept(interpreter);
            }
        } catch (RuntimeError e) {
            return;
        }
        fail("runtime error expected");
    }

}
