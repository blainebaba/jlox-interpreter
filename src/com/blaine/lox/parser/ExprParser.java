package com.blaine.lox.parser;

import java.util.function.Supplier;

import com.blaine.lox.Token;
import com.blaine.lox.Token.TokenType;
import com.blaine.lox.generated.Expr;
import com.blaine.lox.generated.Expr.Binary;
import com.blaine.lox.generated.Expr.Grouping;
import com.blaine.lox.generated.Expr.Literal;
import com.blaine.lox.generated.Expr.Unary;

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
        return leftBinaryRule(this::andTerm, OR);
    }

    private Expr andTerm() {
        return leftBinaryRule(this::equalTerm, AND);
    }

    private Expr equalTerm() {
        return leftBinaryRule(this::compareTerm, EQUAL_EQAUL, NOT_EQUAL);
    }

    private Expr compareTerm() {
        return leftBinaryRule(this::addTerm, LESSER, GREATER, LESS_EQUAL, GREATER_EQUAL);
    }

    private Expr addTerm() {
        return leftBinaryRule(this::mulTerm, PLUS, MINUS);
    }

    private Expr mulTerm() {
        return leftBinaryRule(this::unaryTerm, STAR, SLASH);
    }

    private Expr unaryTerm() {
        if (p.peek(MINUS, EXCLAM)) {
            return new Unary(p.consume(), unaryTerm());
        } else {
            return primary();
        }
    }

    private Expr primary() {
        if (p.peek(IDENTIFIER)) {
            // we are not able to read variable value yet, return identifier name
            // TODO
            return new Literal(p.consume().literalValue);
        } else if (p.peek(NUMBER, STRING)) {
            return new Literal(p.consume().literalValue);
        } else if (p.peek(TRUE)) {
            p.consume();
            return new Literal(true);
        } else if (p.peek(FALSE)) {
            p.consume();
            return new Literal(false);
        } else if (p.peek(LEFT_PAREN)){
            p.consume();
            Expr expr = expression();
            p.match(RIGHT_PAREN);
            return new Grouping(expr);
        } else if (p.peek(NIL)) {
            p.consume();
            return new Literal(null);
        } else {
            throw new ParserError();
        }
    }

    /**
     * left associates binary operator rule
     */
    private Expr leftBinaryRule(Supplier<Expr> nextRule, TokenType ... ops) {
        Expr expr = nextRule.get();
        while (p.peek(ops)) {
            Token operator = p.consume();
            expr = new Binary(expr, operator, nextRule.get());
        }
        return expr;
    }
}
