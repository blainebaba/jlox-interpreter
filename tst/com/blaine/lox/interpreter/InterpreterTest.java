package com.blaine.lox.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blaine.lox.Scanner;
import com.blaine.lox.Token;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.parser.Parser;
import com.blaine.lox.parser.ParserError;

// test script with more than one stmt/expr.
public class InterpreterTest {

    private Interpreter interpreter;
    private Environment env;

    @Before
    public void setup() {
        interpreter = new Interpreter();
        env = interpreter.getEnv();
    }

    @Test
    public void testGlobalVariables() {
        execute(parseStmts("var a = 1; var b = 2; var c = a + b;"));
        assertEquals(3.0, env.getGlobalVar("c"));
    } 

    private List<Stmt> parseStmts(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        List<Stmt> stmt = parser.parse();
        assertTrue(parser.isEnd());
        return stmt;
    }

    private void parseExpectError(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        try {
            parser.parseStatement();
        } catch (ParserError e) {
            return;
        }
        fail("parser error expected");
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
