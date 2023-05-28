package com.blaine.lox.parser;

import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;

import static com.blaine.lox.Token.TokenType.*;

import com.blaine.lox.Token;

public class StmtParser {
    private Parser p;

    public StmtParser(Parser parser) {
        this.p = parser;
    }

    // entrance of parsing one statement
    public Stmt statement() {
        return relaxStmt();
    }

    private Stmt relaxStmt() {
        if (p.peek(VAR)) {
            return declareStmt();
        } else {
            return strictStmt();
        }
    }

    private Stmt strictStmt() {
        if (p.peek(PRINT)) {
            return printStmt();
        } else {
            return exprStmt();
        }
    }

    private Stmt declareStmt() {
        p.match(VAR);
        Token id = p.match(IDENTIFIER);
        Expr initializer = null;
        if (p.peek(EQUAL)) {
            p.consume();
            initializer = p.parseExpression();
        }
        p.match(SEMICOLON);
        return new DeclareStmt((String)id.literalValue, initializer);
    }

    private Stmt printStmt() {
        p.match(PRINT);
        Expr expr = p.parseExpression();
        p.match(SEMICOLON);
        return new PrintStmt(expr);
    }

    private Stmt exprStmt() {
        Expr expr = p.parseExpression();
        p.match(SEMICOLON);
        return new ExpressionStmt(expr);
    }
}
