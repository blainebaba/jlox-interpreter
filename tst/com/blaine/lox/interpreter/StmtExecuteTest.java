package com.blaine.lox.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blaine.lox.Scanner;
import com.blaine.lox.Token;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.parser.Parser;
import com.blaine.lox.parser.ParserError;
import com.blaine.lox.parser.VarResolver;

public class StmtExecuteTest {

    // we can check internal states in interpreter to check correctness.
    private Interpreter interpreter;
    private Environment env;
    private LoxCallable dummyFunction;

    @Before
    public void setup() {
        interpreter = new Interpreter();
        env = interpreter.getCurEnv();

        dummyFunction = new LoxCallable() {

            @Override
            public Object call(Interpreter interpreter, List<Object> args) {
                return args.get(0);
            }

            @Override
            public int paramSize() {
                return 1;
            }
        };
    }

    private Stmt parseOneStmt(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        Stmt stmt = parser.parseStatement();
        new VarResolver(interpreter).resolve(Arrays.asList(stmt));
        assertTrue(parser.isEnd());
        return stmt;
    }

    private void parseExpectError(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        try {
            Stmt stmt = parser.parseStatement();
            new VarResolver(interpreter).resolve(Arrays.asList(stmt));
        } catch (ParserError e) {
            return;
        }
        fail("parser error expected");
    }

    private void executeExpectError(Stmt stmt) {
        try {
            stmt.accept(interpreter);
        } catch (RuntimeError e) {
            return;
        }
        fail("runtime error expected");
    }

    @Test
    public void testPrintStmt() {
        {
            Stmt stmt = parseOneStmt("print 1 + 1;");
            assertEquals(PrintStmt.class, stmt.getClass());
            interpreter.execute(stmt);
        }
        {
            env.declareVar("foo", dummyFunction);
            Stmt stmt = parseOneStmt("print foo(1);");
            assertEquals(PrintStmt.class, stmt.getClass());
            interpreter.execute(stmt);
        }
    }

    @Test
    public void testExpressionStmt() {
        Stmt stmt = parseOneStmt("1 + 1;");
        assertEquals(ExpressionStmt.class, stmt.getClass());
        interpreter.execute(stmt);
    }
    
    @Test
    public void testDeclareStmt() {
        // happy case
        {
            Stmt stmt = parseOneStmt("var a = 1;");
            assertEquals(DeclareStmt.class, stmt.getClass());

            interpreter.execute(stmt);
            Object value = interpreter.getCurEnv().getVar("a");
            assertEquals(1.0, value);
        }
        // invalid statements
        {
            parseExpectError("var 123 = 1;");
            parseExpectError("var 123 = print 123;");
            parseExpectError("var a == b;");
        }
    }

    @Test
    public void testDecFunStmt() {
        // happy case
        {
            Stmt stmt = parseOneStmt("fun foo(a) {print a;}");
            stmt.accept(interpreter);

            Object fun = env.getVar("foo");
            assertTrue(fun instanceof LoxFunction);
        }
    }

    @Test
    public void testBlockStmt() {
        // happy case
        {
            Stmt stmt = parseOneStmt("{var a = 1;}");
            stmt.accept(interpreter);
        }
        // invalid
        parseExpectError("{1 + 1}");
        parseExpectError("{var a = 1; var b = 1;");
    }

    @Test
    public void testIfStmt() {
        // happy case
        {
            env.declareVar("a", null);
            Stmt stmt = parseOneStmt("if (1 > 2) {a = 1;} else {a = 2;}");
            stmt.accept(interpreter);
            assertEquals(2.0, env.getVar("a"));
        }  
        // invalid
        parseExpectError("if (1 > 2) var a = 1;");
        parseExpectError("if (var a = 1;) var b = 2;");
        parseExpectError("if");
        parseExpectError("if ()");
        parseExpectError("if (true)");
        parseExpectError("{ if (true) 1 + 1 }");
    }

    @Test
    public void testWhileStmt() {
        // happy case
        {
            Stmt stmt = parseOneStmt("{var a = 1; while (a < 5) a = a + 1;}");
            stmt.accept(interpreter);
        }  
        // invalid
        parseExpectError("while");
        parseExpectError("while ()");
        parseExpectError("while (1 + 1;)");
        parseExpectError("while (true) 1 + 1");
    }

    @Test
    public void testForStmt() {
        // happy case
        {
            Stmt stmt = parseOneStmt("for (var a = 0; a < 5; a = a + 1) {}");
            stmt.accept(interpreter);
        }  
        // invalid
        parseExpectError("for ()");
        parseExpectError("for (;)");
        parseExpectError("for (1 + 1; ;)");
    }

    @Test
    public void testReturnStmt() {
        // parse 
        parseOneStmt("fun foo() {return;}");
        parseOneStmt("fun foo() {return 1;}");
        // invalid
        parseExpectError("return;");
        parseExpectError("fun foo() {return var a;}");
    }

    @Test
    public void testClassDecStmt() {
        // happy case
        {
            parseOneStmt("class Foo {}").accept(interpreter);
            LoxClass klass = (LoxClass) env.getVar("Foo");
            assertEquals("Foo", klass.className);
        }
        // invalid
        parseExpectError("class Foo;");
        parseExpectError("class Foo { 1 + 1;}");
        parseExpectError("fun foo { return this.value;}");
    }

}
