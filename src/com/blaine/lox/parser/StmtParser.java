package com.blaine.lox.parser;

import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Stmt;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Stmt.PrintStmt;
import com.blaine.lox.generated.Stmt.ExpressionStmt;
import com.blaine.lox.generated.Stmt.BlockStmt;
import com.blaine.lox.generated.Stmt.DeclareStmt;
import com.blaine.lox.generated.Stmt.IfStmt;
import com.blaine.lox.generated.Stmt.WhileStmt;

import static com.blaine.lox.Token.TokenType.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        } 
        else if (p.peek(LEFT_BRACE)) {
            return blockStmt();
        }
        else if (p.peek(IF)) {
            return ifStmt();
        }
        else if (p.peek(WHILE)) {
            return whileStmt();
        }
        else if (p.peek(FOR)) {
            return forStmt();
        }
        else {
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

    private Stmt blockStmt() {
        p.match(LEFT_BRACE);
        List<Stmt> stmts = new ArrayList<>();
        while (!p.isEnd() && !p.peek(RIGHT_BRACE)) {
            stmts.add(statement());
        }
        p.match(RIGHT_BRACE);

        return new BlockStmt(stmts);
    }

    private Stmt ifStmt() {
        p.match(IF);
        p.match(LEFT_PAREN);
        Expr cond = p.parseExpression();
        p.match(RIGHT_PAREN);
        Stmt ifClause = p.parseStatement();
        Stmt elseClause = null;
        if (p.peek(ELSE)) {
            p.consume();
            elseClause = p.parseStatement();
        }
        return new IfStmt(cond, ifClause, elseClause);
    }

    private Stmt whileStmt() {
        p.match(WHILE);
        p.match(LEFT_PAREN);
        Expr cond = p.parseExpression();
        p.match(RIGHT_PAREN);
        Stmt stmt = p.parseStatement();
        return new WhileStmt(cond, stmt);
    }

    // syntax suger, converts to while loop
    private Stmt forStmt() {
        p.match(FOR);
        p.match(LEFT_PAREN);
        
        Stmt declare;
        if (p.peek(SEMICOLON)) {
            p.consume();
            declare = null;
        }
        else if (p.peek(VAR)) {
            declare = declareStmt();
        } 
        else {
            declare = exprStmt();
        }

        Expr cond;
        if (p.peek(SEMICOLON)) {
            cond = new LiteralExpr(true, null);
            p.consume();
        }
        else {
            cond = p.parseExpression();
            p.match(SEMICOLON);
        }

        Expr incr;
        if (p.peek(RIGHT_PAREN)) {
            incr = null;
        }
        else {
            incr = p.parseExpression();
        }

        p.match(RIGHT_PAREN);

        Stmt stmt = p.parseStatement();

        // assemble together
        Stmt result = stmt;
        if (incr != null) {
            result = new BlockStmt(Arrays.asList(result, new ExpressionStmt(incr)));
        }
        result = new WhileStmt(cond, result);
        if (declare != null) {
            result = new BlockStmt(Arrays.asList(declare, result));
        }
        return result;
    }
}
