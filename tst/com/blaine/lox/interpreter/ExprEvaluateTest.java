package com.blaine.lox.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blaine.lox.Scanner;
import com.blaine.lox.Token;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.parser.Parser;
import com.blaine.lox.parser.ParserError;
import com.blaine.lox.parser.VarResolver;

// test expression parse and evaluate
public class ExprEvaluateTest {

    private Interpreter interpreter;
    private Environment env;
    private LoxCallable dummyFunction;
    private LoxClass dummyClass;

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

        dummyClass = new LoxClass("Foo", new ArrayList<>(), env);

    }

    @Test
    public void testEvaluateLiteral() {
        assertEquals(123.0, evaluate("123"));
        assertEquals("abc", evaluate("\"abc\""));
        assertEquals(true, evaluate("true"));
        assertEquals(false, evaluate("false"));
    }

    @Test
    public void testEvaluateUnary() {
        assertEquals(-123.0, evaluate("-123"));
        evaluateExpectError("-\"abc\"");
        evaluateExpectError("-nil");
        assertEquals(false, evaluate("!true"));
    }

    @Test
    public void testEvaluateBinary() {
        assertEquals(3.0, evaluate("1+2"));
        assertEquals(-1.0, evaluate("1-2"));
        assertEquals(4.0, evaluate("2*2"));
        assertEquals(1.5, evaluate("3/2"));
        assertEquals(Double.NaN, evaluate("0 / 0"));
        assertEquals(3.0, evaluate("1 + 1 + 1"));
        assertEquals(3.0, evaluate("1 + 1 * 2"));
        evaluateExpectError("\"abc\"*2");
        evaluateExpectError("nil + 1");

        assertEquals("ab", evaluate("\"a\" + \"b\""));
        assertEquals("abc", evaluate("\"a\" + \"b\" + \"c\""));

        assertEquals(true, evaluate("123 == 123"));
        assertEquals(true, evaluate("123 != 234"));
        assertEquals(false, evaluate("123 == \"123\""));
        assertEquals(false, evaluate("\"abc\" != \"abc\""));
        assertEquals(false, evaluate("\"abc\" == \"efg\""));
        assertEquals(true, evaluate("nil == nil"));
        assertEquals(true, evaluate("nil != 123"));
        assertEquals(true, evaluate("1 == 1 == true"));

        assertEquals(true, evaluate("2 > 1"));
        assertEquals(false, evaluate("2 < 1"));
        assertEquals(false, evaluate("2 >= 3"));
        assertEquals(true, evaluate("10 >= 10"));
        assertEquals(true, evaluate("10 <= 10"));
        assertEquals(false, evaluate("10 <= 8"));
        evaluateExpectError("\"a\" > \"b\"");

        assertEquals(true, evaluate("true and true"));
        assertEquals(false, evaluate("false and true"));
        assertEquals(false, evaluate("true and false"));
        assertEquals(true, evaluate("true or false"));
        assertEquals(false, evaluate("false or false"));
        assertEquals(true, evaluate("true and false or true and true"));
    }

    @Test
    public void testEvaluateGrouping() {
        assertEquals(4.0, evaluate("(1 + 1) * 2"));
        assertEquals(true, evaluate("true and (false or true)"));
    }

    @Test
    public void testBooleanImplicitConversion() {
        assertEquals(false, evaluate("nil or false"));
        assertEquals(true, evaluate("0 or false"));
        assertEquals(true, evaluate("\"\" or false"));
    }

    @Test
    public void testEvaluateAssign() {
        // happy case
        {
            env.declareVar("a", null);
            assertEquals(1.0, evaluate("a = 1"));
            assertEquals(1.0, env.getVar("a"));
        }
        // assign to undefined var
        {
            parseExpectError("b = 2");
        }
        // chain assignment
        {
            interpreter.getCurEnv().declareVar("c", null);
            interpreter.getCurEnv().declareVar("d", null);
            interpreter.getCurEnv().declareVar("e", null);
            assertEquals(1.0, evaluate("c = d = e = 1"));
            assertEquals(1.0, env.getVar("c"));
            assertEquals(1.0, env.getVar("d"));
            assertEquals(1.0, env.getVar("e"));
        }
    }

    @Test
    public void testEvaluateVariable() {
        // exist global var 
        {
            interpreter.getCurEnv().declareVar("a", 1.0);
            assertEquals(1.0, evaluate("a"));
        }
        // non-exist global var 
        {
            parseExpectError("b");
        }
    }

    @Test
    public void testEvaluateFunCall() {
        // happy case
        {
            env.declareVar("foo", dummyFunction);
            assertEquals(1.0, evaluate("foo(1)"));
        }
        // invalid
        evaluateExpectError("foo()()");
    }

    @Test
    public void testClassInstantiate() {
        env.declareVar("Foo", dummyClass);
        LoxInstance instance = (LoxInstance) evaluate("Foo()");
        assertEquals("Foo", instance.klass.className);
    }

    private Expr parse(String expression) {
        List<Token> tokens = new Scanner(expression).scan();
        Expr expr = new Parser(tokens).parseExpression();
        new VarResolver(interpreter).resolveExpression(expr);;
        return expr;
    }

    private void parseExpectError(String script) {
        try {
            List<Token> tokens = new Scanner(script).scan();
            Expr expr = new Parser(tokens).parseExpression();
            new VarResolver(interpreter).resolveExpression(expr);;
        } catch (ParserError e) {
            return;
        }
        fail("ParserError expected.");
    }

    private Object evaluate(Expr expr) {
        return expr.accept(interpreter);
    }

    // parse + evaluate
    private Object evaluate(String expression) {
        Expr expr = parse(expression);
        return evaluate(expr);
    }

    // parse + evaluate
    private void evaluateExpectError(String script) {
        Expr expr = parse(script);
        try {
            evaluate(expr);
        } catch (RuntimeError e) {
            // pass
            return;
        }
        fail("RuntimeError expected.");
    }
}
