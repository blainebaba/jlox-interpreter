package com.blaine.lox.parser;

import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;

import static com.blaine.lox.Token.TokenType.*;

public class StmtParser {
    private Parser p;

    public StmtParser(Parser parser) {
        this.p = parser;
    }

    // entrance of parsing one statement
    public Stmt statement() {
        if (p.peek(PRINT)) {
            return printStatement();
        } else {
            return expressionStatement();
        }
    }

    private Stmt printStatement() {
        p.match(PRINT);
        Expr expr = p.parseExpression();
        p.match(SEMICOLON);
        return new PrintStmt(expr);
    }

    private Stmt expressionStatement() {
        Expr expr = p.parseExpression();
        p.match(SEMICOLON);
        return new ExpressionStmt(expr);
    }
}
