package com.blaine.lox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.blaine.lox.evaluate.Interpreter;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.parser.Parser;

// test statement evaluation and execution
public class StmtExecuteTest {

    // we can check internal states in interpreter to check correctness.
    private Interpreter interpreter;

    @Before
    public void setup() {
        interpreter = new Interpreter();
    }

    private Stmt parseOneStatement(String script) {
        List<Token> tokens = new Scanner(script).scan();
        Parser parser = new Parser(tokens);
        Stmt stmt = parser.parseStatement();
        assertTrue(parser.isEnd());
        return stmt;
    }

    @Test
    public void testPrintStmt() {
        Stmt stmt = parseOneStatement("print 1 + 1;");
        assertEquals(PrintStmt.class, stmt.getClass());
        interpreter.execute(stmt);
    }

    @Test
    public void testExpressionStmt() {
        Stmt stmt = parseOneStatement("1 + 1;");
        assertEquals(ExpressionStmt.class, stmt.getClass());
        interpreter.execute(stmt);
    }
}
