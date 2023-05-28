package com.blaine.lox.parser;

import java.util.function.Supplier;

import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Expr.BinaryExpr;
import com.blaine.lox.generated.Expr.GroupingExpr;
import com.blaine.lox.generated.Expr.LiteralExpr;
import com.blaine.lox.generated.Expr.UnaryExpr;

import static com.blaine.lox.Token.TokenType.*;

/**
 * This is basically translating parsing rules into code.
 */
public class ExprParser {

    private Parser p;

    public ExprParser(Parser parser) {
        this.p = parser;
    }

    // entrance of parsing expression 
    public Expr expression() {
        return orTerm();
    }

    private Expr orTerm() {
        return leftBinaryExprRule(this::andTerm, OR);
    }

    private Expr andTerm() {
        return leftBinaryExprRule(this::equalTerm, AND);
    }

    private Expr equalTerm() {
        return leftBinaryExprRule(this::compareTerm, EQUAL_EQAUL, NOT_EQUAL);
    }

    private Expr compareTerm() {
        return leftBinaryExprRule(this::addTerm, LESSER, GREATER, LESS_EQUAL, GREATER_EQUAL);
    }

    private Expr addTerm() {
        return leftBinaryExprRule(this::mulTerm, PLUS, MINUS);
    }

    private Expr mulTerm() {
        return leftBinaryExprRule(this::unaryexprTerm, STAR, SLASH);
    }

    private Expr unaryexprTerm() {
        if (p.peek(MINUS, EXCLAM)) {
            return new UnaryExpr(p.consume(), unaryexprTerm());
        } else {
            return primary();
        }
    }

    private Expr primary() {
        if (p.peek(IDENTIFIER)) {
            // we are not able to read variable value yet, return identifier name
            // TODO
            return new LiteralExpr(p.consume().literalValue);
        } else if (p.peek(NUMBER, STRING)) {
            return new LiteralExpr(p.consume().literalValue);
        } else if (p.peek(TRUE)) {
            p.consume();
            return new LiteralExpr(true);
        } else if (p.peek(FALSE)) {
            p.consume();
            return new LiteralExpr(false);
        } else if (p.peek(LEFT_PAREN)){
            p.consume();
            Expr expr = expression();
            p.match(RIGHT_PAREN);
            return new GroupingExpr(expr);
        } else if (p.peek(NIL)) {
            p.consume();
            return new LiteralExpr(null);
        } else {
            throw new ParserError();
        }
    }

    /**
     * left associates binaryexpr operator rule
     */
    private Expr leftBinaryExprRule(Supplier<Expr> nextRule, TokenType ... ops) {
        Expr expr = nextRule.get();
        while (p.peek(ops)) {
            Token operator = p.consume();
            expr = new BinaryExpr(expr, operator, nextRule.get());
        }
        return expr;
    }
}
